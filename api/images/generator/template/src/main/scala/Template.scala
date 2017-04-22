import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import generator.Result
import generator.Transformer
import models.document.Filter
import models.document.Generator
import models.document.ModelEntity
import models.document.{Repository => Documents}
import models.document.http.{HttpRepository => DocumentRepository}
import models.file.File
import models.file.{Repository => Files}
import models.file.http.{HttpRepository => FileRepository}
import models.remote.Remote
import models.remote.RemoteGenerator
import models.session.SyncGatewaySession
import org.rogach.scallop.ScallopConf
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.ClassTag
import scala.reflect.runtime
import scala.tools.reflect.ToolBox

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val session = opt[String]()
  val work = opt[String](required = true)
  val parent = opt[String]()
  val options = opt[String]()
  val key = opt[String]()
  val filter = opt[String]()
  val generator = opt[String]()
  val model = opt[String]()
  val create = opt[String]()
  val image = opt[String]()

  validateOpt(filter, generator, model, create, image, options) {
    case (Some(filter), Some(generator), Some(model), None, None, None) =>
      Right(Unit)
    case (Some(filter), Some(generator), None, None, None, None) =>
      Right(Unit)
    case (None, None, None, Some(create), Some(image), None) =>
      Right(Unit)
    case (None, Some(generator), None, None, None, Some(options)) =>
      Right(Unit)
    case _ =>
      Left(
        "Invalid arguments. It's only valid to call a generator with a filter, " +
        "a generator with a model or to generate a generator with options and the image id."
      )
  }

  verify()
}

abstract class Template[S, T]()(implicit createOptions: Reads[S], callOptions: Reads[T]) extends App {
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()

  implicit val remote: Remote = RemoteGenerator(cmd.session.getOrElse(""), cmd.work.getOrElse(""), cmd.parent.toOption, cmd.key.toOption)
  implicit val documents: Documents = DocumentRepository(cmd.session.getOrElse(""))
  implicit val files = FileRepository(cmd.session.getOrElse(""))
  implicit val session = SyncGatewaySession()

  val user = Await.result(session.getUser(cmd.session.getOrElse("")), Duration(10, TimeUnit.SECONDS))

  if (cmd.options.supplied) {
    val raw = cmd.options.getOrElse("")
    parseCallOptions(raw).map { opt =>
      runGeneratorWithOptions(opt).map { result =>
        println(result.message)
        System.exit(result.status)
      }.recover {
        case e: Exception =>
          System.err.println(e)
          System.exit(1)
      }
    }
  } else if (cmd.model.supplied) {
    val model = cmd.model.getOrElse("")
    val generator = cmd.generator.getOrElse("")
    runGeneratorForSingleModel(generator, model).map { result =>
      println(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        System.err.println(e)
        System.exit(1)
    }
  } else if (cmd.filter.supplied) {
    val filter = cmd.filter.getOrElse("")
    val generator = cmd.generator.getOrElse("")
    runGeneratorWithFilter(generator, filter).map { result =>
      println(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        System.err.println(e)
        System.exit(1)
    }
  } else if (cmd.create.supplied) {
    val options = cmd.create.getOrElse("")
    val image = cmd.image.getOrElse("")
    parseGeneratorCreateOptions(options, image).map { result =>
      println(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        System.err.println(e)
        System.exit(1)
    }
  } else {
    System.err.println("No suitable options are provided to run the generator")
    System.exit(1)
  }

  private def parseCallOptions(rawOptions: String): Future[T] = {
    Json.parse(rawOptions).validate[T] match {
      case s: JsSuccess[T] => Future.successful(s.get)
      case e: JsError => Future.failed(new Exception("Option parameter was provided but could not be parsed"))
    }
  }

  private def parseGeneratorCreateOptions(rawOptions: String, image: String): Future[Result] = {
    Json.parse(rawOptions).validate[S] match {
      case s: JsSuccess[S] => createTransformer(s.get, image)
      case e: JsError => Future.failed(new Exception(e.toString))
    }
  }

  private def executeTransformation(transformer: Transformer, entity: ModelEntity) = {
    for {
      prepared <- transformer.prepare(entity.id)
      transformed <- prepared.transform(entity)
      result <- transformed.exit()
    } yield result
  }

  private def executeTransformation(generator: Transformer, filter: Filter): Future[Result] = {
    val p = Promise[Result]
    println("run the generator")

    val start: Future[Transformer] = generator.prepare(filter.instances)
    val futures = filter.instances.foldLeft(start) {
      case (future, model) => future.flatMap { generator =>
        documents.get[ModelEntity](model).flatMap { entity =>
          generator.transform(entity)
        }
      }
    }

    futures.onSuccess {
      case generator => generator.exit.map { result =>
        p.success(result)
      }.recover {
        case e: Exception => p.failure(e)
      }
    }
    futures.onFailure {
      case e => p.failure(e)
    }

    p.future
  }

  /**
   * Compile a file to the specified class
   *
   * @param file The file which to compile
   * @return A future with the compiled class
   */
  def compile[T: ClassTag](file: String): Future[T] = {
    val p = Promise[T]

    try {
      val toolbox = runtime.currentMirror.mkToolBox()
      val tree = toolbox.parse(file)
      val compiledCode = toolbox.eval(tree)
      val fn = compiledCode.asInstanceOf[T]
      p.success(fn)
    } catch {
      case e: Throwable => p.failure(new Exception(e))
    }
    p.future
  }

  def checkFilter(filter: Filter): Future[Boolean] = {
    if (filter.instances.size > 0) {
      Future.successful(true)
    } else {
      Future.failed(new Exception("No Models are available with the selected filter."))
    }
  }

  /**
   * Run a model transformation for a multiple models
   *
   * @param generatorId
   * @param filterId
   * @param documents
   * @param files
   * @return
   */
  def runGeneratorWithFilter(generatorId: String, filterId: String)(implicit documents: Documents, files: Files): Future[Result] = {
    for {
      generator <- documents.get[Generator](generatorId)
      filter <- documents.get[Filter](filterId)
      ok <- checkFilter(filter)
      file <- files.get(generator, Settings.generatorFile)
      fn <- getTransformer(file, filter)
      end <- executeTransformation(fn, filter)
    } yield end
  }

  /**
   * Run a model transformation for a single model
   *
   * @param generatorId The generator which to use
   * @param modelId The model for which to run the generator
   * @param documents Access to the Documents repository
   * @param files Access to the Files repository
   * @return The result of the generator
   */
  def runGeneratorForSingleModel(generatorId: String, modelId: String)(implicit documents: Documents, files: Files): Future[Result] = {
    for {
      generator <- documents.get[Generator](generatorId)
      model <- documents.get[ModelEntity](modelId)
      file <- files.get(generator, Settings.generatorFile)
      fn <- getTransformer(file, model)
      end <- executeTransformation(fn, model)
    } yield end
  }

  /**
   * Call the generator (called by another generator) to run with options
   *
   * @return The Result of the generator
   */
  def runGeneratorWithOptions(options: T)(implicit documents: Documents, files: Files, remote: Remote): Future[Result]

  /**
   * Initialize the generator
   *
   * @param file The file which was loaded for the generator
   * @param documents Access to the Documents repository
   * @param files Access to the Files repository
   * @return A Generator
   */
  def getTransformer(file: File, filter: Filter)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer]

  /**
   * Initialize the model transformer
   *
   * @param file The file which was loaded for the generator
   * @param documents Access to the Documents repository
   * @param files Access to the Files repository
   * @return A Generator
   */
  def getTransformer(file: File, model: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer]

  /**
   * Create assets for the model transformer
   *
   * @param options The Options for the creation of the generator
   * @param image The id of the image for the generator
   * @param documents Access to the Documents repository
   * @param files Access to the Files repository
   * @return The result of the generator creation
   */
  def createTransformer(options: S, image: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result]
}
