import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.ClassTag
import scala.reflect.runtime
import scala.tools.reflect.ToolBox

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Transformer
import org.rogach.scallop.ScallopConf
import org.slf4j.LoggerFactory
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.JsSuccess
import play.api.libs.json.Reads
import play.api.libs.ws.ahc.AhcWSClient

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
  private val logger = LoggerFactory.getLogger(getClass)
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val client = AhcWSClient()


  val repository = Persistence.fullAccessRepository

  val user: UUID = cmd.session.toOption.fold(UUID.randomUUID)(UUID.fromString)

  if (cmd.options.supplied) {
    val raw = cmd.options.getOrElse("")
    parseCallOptions(raw).map { opt =>
      runGeneratorWithOptions(opt).map { result =>
        logger.info(result.message)
        System.exit(result.status)
      }.recover {
        case e: Exception =>
          logger.error(e.getMessage, e)
          System.exit(1)
      }
    }
  } else if (cmd.model.supplied) {
    val modelId = cmd.model.toOption.fold(UUID.randomUUID)(UUID.fromString)
    val generatorId = cmd.generator.toOption.fold(UUID.randomUUID)(UUID.fromString)
    runGeneratorForSingleModel(generatorId, modelId).map { result =>
      logger.info(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  } else if (cmd.filter.supplied) {
    val filterId = cmd.filter.toOption.fold(UUID.randomUUID)(UUID.fromString)
    val generatorId = cmd.generator.toOption.fold(UUID.randomUUID)(UUID.fromString)
    runGeneratorWithFilter(generatorId, filterId).map { result =>
      logger.info(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  } else if (cmd.create.supplied) {
    val options = cmd.create.getOrElse("")
    val imageId = cmd.image.toOption.fold(UUID.randomUUID)(UUID.fromString)
    parseGeneratorCreateOptions(options, imageId).map { result =>
      logger.info(result.message)
      System.exit(result.status)
    }.recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  } else {
    logger.error("No suitable options are provided to run the generator")
    System.exit(1)
  }

  private def parseCallOptions(rawOptions: String): Future[T] = {
    Json.parse(rawOptions).validate[T] match {
      case s: JsSuccess[T] => Future.successful(s.get)
      case e: JsError => Future.failed(new Exception("Option parameter was provided but could not be parsed"))
    }
  }

  private def parseGeneratorCreateOptions(rawOptions: String, imageId: UUID): Future[Result] = {
    Json.parse(rawOptions).validate[S] match {
      case s: JsSuccess[S] => createTransformer(s.get, imageId)
      case e: JsError => Future.failed(new Exception(e.toString))
    }
  }

  private def executeTransformation(transformer: Transformer, entity: ModelEntity) = {
    for {
      prepared <- transformer.prepare(entity.id)
      transformed <- prepared.transform(entity)
      result <- transformed.exit()
    } yield {
      result
    }
  }

  private def executeTransformation(generator: Transformer, filter: Filter): Future[Result] = {
    val p = Promise[Result]
    logger.info("run the generator")

    val start: Future[Transformer] = generator.prepare(filter.instanceIds.toList)
    val futures = filter.instanceIds.foldLeft(start) {
      case (future, modelId) => future.flatMap { generator =>
        repository.modelEntity.read(modelId).flatMap { entity =>
          generator.transform(entity)
        }
      }
    }

    futures.onSuccess {
      case generator: Transformer => generator.exit.map { result =>
        p.success(result)
      }.recover {
        case e: Exception => p.failure(e)
      }
    }
    futures.onFailure {
      case e: Throwable => p.failure(e)
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
    if (filter.instanceIds.nonEmpty) {
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
   * @return
   */
  def runGeneratorWithFilter(generatorId: UUID, filterId: UUID): Future[Result] = {
    for {
      generator <- repository.generator.read(generatorId)
      filter <- repository.filter.read(filterId)
      ok <- checkFilter(filter)
      file <- repository.file.read(generator.id, Settings.generatorFile)
      fn <- getTransformer(file, filter)
      end <- executeTransformation(fn, filter)
    } yield {
      end
    }
  }

  /**
   * Run a model transformation for a single model
   *
   * @param generatorId The generator which to use
   * @param modelId     The model for which to run the generator
   * @return The result of the generator
   */
  def runGeneratorForSingleModel(generatorId: UUID, modelId: UUID): Future[Result] = {
    for {
      generator <- repository.generator.read(generatorId)
      model <- repository.modelEntity.read(modelId)
      file <- repository.file.read(generator.id, Settings.generatorFile)
      fn <- getTransformer(file, model)
      end <- executeTransformation(fn, model)
    } yield {
      end
    }
  }

  /**
   * Call the generator (called by another generator) to run with options
   *
   * @param options   the options for the generator
   * @return The Result of the generator
   */
  def runGeneratorWithOptions(options: T): Future[Result]

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param filter    the Filter
   * @return A Generator
   */
  def getTransformer(file: File, filter: Filter): Future[Transformer]

  /**
   * Initialize the model transformer
   *
   * @param file      The file which was loaded for the generator
   * @param model     the modelEntity
   * @return A Generator
   */
  def getTransformer(file: File, model: ModelEntity): Future[Transformer]

  /**
   * Create assets for the model transformer
   *
   * @param options   The Options for the creation of the generator
   * @param imageId     The id of the image for the generator
   * @return The result of the generator creation
   */
  def createTransformer(options: S, imageId: UUID): Future[Result]

}
