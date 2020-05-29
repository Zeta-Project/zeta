package de.htwg.zeta.generator

import java.io.FileNotFoundException
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.ClassTag
import scala.reflect.runtime
import scala.tools.reflect.ToolBox

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import de.htwg.zeta.persistence.general.GeneratorRepository
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.ws.ahc.AhcWSClient

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val session: ScallopOption[String] = opt[String]()
  val work: ScallopOption[String] = opt[String](required = true)
  val parent: ScallopOption[String] = opt[String]()
  val options: ScallopOption[String] = opt[String]()
  val key: ScallopOption[String] = opt[String]()
  val filter: ScallopOption[String] = opt[String]()
  val generator: ScallopOption[String] = opt[String]()
  val model: ScallopOption[String] = opt[String]()
  val create: ScallopOption[String] = opt[String]()
  val image: ScallopOption[String] = opt[String]()

  validateOpt(filter, generator, model, create, image, options) {
    case (Some(_), Some(_), Some(_), None, None, None) =>
      Right(Unit)
    case (Some(_), Some(_), None, None, None, None) =>
      Right(Unit)
    case (None, None, None, Some(_), Some(_), None) =>
      Right(Unit)
    case (None, Some(_), None, None, None, Some(_)) =>
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

  private val injector = Guice.createInjector(new PersistenceModule)
  val modelEntityPersistence = injector.getInstance(classOf[GraphicalDslInstanceRepository])
  val filePersistence = injector.getInstance(classOf[FileRepository])
  val generatorPersistence = injector.getInstance(classOf[GeneratorRepository])
  val filterPersistence = injector.getInstance(classOf[FilterRepository])
  val metaModelEntityPersistence = injector.getInstance(classOf[GdslProjectRepository])
  val generatorImagePersistence = injector.getInstance(classOf[GeneratorImageRepository])

  val user: UUID = cmd.session.toOption.fold(UUID.randomUUID)(UUID.fromString)

  if (cmd.options.isSupplied) {
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
  } else if (cmd.model.isSupplied) {
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
  } else if (cmd.filter.isSupplied) {
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
  } else if (cmd.create.isSupplied) {
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
      case _: JsError => Future.failed(new Exception("Option parameter was provided but could not be parsed"))
    }
  }

  private def parseGeneratorCreateOptions(rawOptions: String, imageId: UUID): Future[Result] = {
    Json.parse(rawOptions).validate[S] match {
      case s: JsSuccess[S] => createTransformer(s.get, imageId)
      case e: JsError => Future.failed(new Exception(e.toString))
    }
  }

  private def executeTransformation(transformer: Transformer, entity: GraphicalDslInstance) = {
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
    logger.info("Run the generator")

    val start: Future[Transformer] = generator.prepare(filter.instanceIds.toList)
    val futures = filter.instanceIds.foldLeft(start) {
      case (future, modelId) => future.flatMap { generator =>
        modelEntityPersistence.read(modelId).flatMap { entity =>
          generator.transform(entity)
        }
      }
    }

    futures.onSuccess {
      case generator: Transformer => generator.exit().map { result =>
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
   * @param file The file which to compile
   * @return A future with the compiled class
   */
  protected def compile[E: ClassTag](file: String): Future[E] = {
    val p = Promise[E]
    try {
      logger.info("Create toolbox")
      val toolbox = runtime.currentMirror.mkToolBox()
      logger.info("Parse generator via toolbox")
      val tree = toolbox.parse(file)
      logger.info("Compile generator via toolbox")
      val compiledCode = toolbox.eval(tree)
      logger.info("Cast generator compile to Transformer")
      val fn = compiledCode.asInstanceOf[E]
      p.success(fn)
    } catch {
      case e: Throwable =>
        logger.error("Compile failed", e)
        p.failure(e)
    }
    p.future
  }

  private def checkFilter(filter: Filter): Future[Boolean] = {
    if (filter.instanceIds.nonEmpty) {
      Future.successful(true)
    } else {
      Future.failed(new Exception("No Models are available with the selected filter."))
    }
  }

  /**
   * Run a model transformation for a multiple models
   * @param generatorId Identifier of Generator
   * @param filterId Identifier of Filter
   * @return The result
   */
  private def runGeneratorWithFilter(generatorId: UUID, filterId: UUID): Future[Result] = {
    for {
      generator <- generatorPersistence.read(generatorId)
      filter <- filterPersistence.read(filterId)
      _ <- checkFilter(filter)
      file <- getFile(Settings.generatorFile, generator)
      fn <- getTransformer(file, filter)
      end <- executeTransformation(fn, filter)
    } yield {
      end
    }
  }

  private def getFile(fileName: String, generator: Generator): Future[File] = {
    generator.files.find { case (_, name) => name == fileName } match {
      case Some((id, name)) =>
        logger.info("Request file `{}` {} for generator {}", name, id.toString, generator.id.toString)
        filePersistence.read(id, name)
      case None =>
        logger.error("Could not find '{}' in Generator {}", fileName, generator.id.toString: Any)
        throw new FileNotFoundException(s"Could not find '$fileName' in Generator ${generator.id}")
    }
  }

  /**
   * Run a model transformation for a single model
   *
   * @param generatorId The generator which to use
   * @param modelId     The model for which to run the generator
   * @return The result of the generator
   */
  private def runGeneratorForSingleModel(generatorId: UUID, modelId: UUID): Future[Result] = {
    for {
      generator <- generatorPersistence.read(generatorId)
      model <- modelEntityPersistence.read(modelId)
      file <- getFile(Settings.generatorFile, generator)
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
  protected def runGeneratorWithOptions(options: T): Future[Result]

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param filter    the Filter
   * @return A Generator
   */
  protected def getTransformer(file: File, filter: Filter): Future[Transformer]

  /**
   * Initialize the model transformer
   *
   * @param file      The file which was loaded for the generator
   * @param model     the modelEntity
   * @return A Generator
   */
  protected def getTransformer(file: File, model: GraphicalDslInstance): Future[Transformer]

  /**
   * Create assets for the model transformer
   *
   * @param options   The Options for the creation of the generator
   * @param imageId     The id of the image for the generator
   * @return The result of the generator creation
   */
  protected def createTransformer(options: S, imageId: UUID): Future[Result]
}
