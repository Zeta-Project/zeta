import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.Persistence.fullAccessRepository
import de.htwg.zeta.server.generator.Error
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Success
import de.htwg.zeta.server.generator.Transformer
import models.document.Filter
import models.document.Generator
import models.document.ModelEntity
import models.file.File
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.remote.Remote
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MyTransformer() extends Transformer {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def transform(entity: ModelEntity)(implicit remote: Remote): Future[Transformer] = {
    logger.info("Start example")
    val p = Promise[Transformer]

    val filename = "example.txt"
    val content =
      s"""
        |Number of nodes : ${entity.model.nodes.size}
        |Number of edges : ${entity.model.edges.size}
      """.stripMargin

    val f = File(entity.id, filename, content)

    fullAccessRepository.files.createVersion(filename, File(entity.id, filename, content)).map { result =>
      logger.info(s"Successfully saved results to '$filename' for model '${entity.model.name}' (MetaModel '${entity.model.metaModel.name}')")
      p.success(this)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  def exit()(implicit remote: Remote): Future[Result] = {
    val result = Success("The generator finished")
    Future.successful(result)
  }
}

/**
 * Main class of file generator
 */
object Main extends Template[CreateOptions, String] {
  override def createTransformer(options: CreateOptions, imageId: UUID)(implicit remote: Remote): Future[Result] = {
    val repository = Persistence.fullAccessRepository
    for {
      image <- repository.generatorImages.read(imageId)
      generator <- repository.generators.create(Generator(user, options.name, image.id))
      created <- repository.files.createVersion("demoFile", createFileContent())
    } yield {
      Success()
    }
  }

  def createFileContent(): File = {
    File(UUID.randomUUID, Settings.generatorFile, "This is a demo to save the results of a generator. No further configuration is required.")
  }

  def createFile(name: String): File = {
    val content =
      s"""
        |class MyTransformer() extends Transformer {
        | private val logger = LoggerFactory.getLogger(getClass)
        | def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote) : Future[Transformer] = {
        |   logger.info(s"User : $${entity.id}")
        |   entity.model.elements.values.foreach { element => element match {
        |     case node: Node => logger.info(node.`type`.name)
        |     case edge: Edge => logger.info(edge.`type`.name)
        |   }}
        |   Future.successful(this)
        | }
        |
        |def exit()(implicit documents: Documents, files: Files, remote: Remote) : Future[Result] = {
        |   val result = Success("The generator finished")
        |   Future.successful(result)
        | }
        |}
        |
      """.stripMargin
    File(UUID.randomUUID, name, content)
  }

  def compiledGenerator(file: File): Future[Transformer] = {
    val content =
      s"""
        |import scala.concurrent.ExecutionContext.Implicits.global
        |import scala.concurrent.{Future, Promise}
        |import models.modelDefinitions.model.elements.{Node, Edge}
        |import models.document.ModelEntity
        |import models.document.{Repository => Documents}
        |import models.file.{File, Repository => Files}
        |import models.remote.Remote
        |import generator._
        |import org.slf4j.LoggerFactory
        |
        |${file.content}
        |
        |new MyTransformer()
      """
    compile[Transformer](content)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter)(implicit remote: Remote): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity)(implicit remote: Remote): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String)(implicit remote: Remote): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }

}
