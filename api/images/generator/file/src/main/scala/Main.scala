import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.remote.Remote
import de.htwg.zeta.persistence.Persistence
import de.htwg.zeta.persistence.Persistence.fullAccessRepository
import de.htwg.zeta.server.generator.Error
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Success
import de.htwg.zeta.server.generator.Transformer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MyTransformer() extends Transformer {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def transform(entity: ModelEntity): Future[Transformer] = {
    logger.info("Start example")
    val p = Promise[Transformer]

    val filename = "example.txt"
    val content =
      s"""
        |Number of nodes : ${entity.model.nodeMap.size}
        |Number of edges : ${entity.model.edgeMap.size}
      """.stripMargin

    fullAccessRepository.file.create(File(entity.id, filename, content)).map { _ =>
      logger.info(s"Successfully saved results to '$filename' for model '${entity.model.name}' (MetaModel '${entity.model.metaModelId}')")
      p.success(this)
    }.recover {
      case e: Exception => p.failure(e)
    }

    p.future
  }

  def exit(): Future[Result] = {
    val result = Success("The generator finished")
    Future.successful(result)
  }
}

/**
 * Main class of file generator
 */
object Main extends Template[CreateOptions, String] {
  override def createTransformer(options: CreateOptions, imageId: UUID): Future[Result] = {
    val repository = Persistence.fullAccessRepository
    for {
      image <- repository.generatorImage.read(imageId)
      generator <- repository.generator.create(Generator(UUID.randomUUID(), options.name, image.id))
      created <- repository.file.create(createFileContent())
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
        |import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Node, Edge}
        |import de.htwg.zeta.common.models.document.ModelEntity
        |import de.htwg.zeta.common.models.document.{Repository => Documents}
        |import de.htwg.zeta.common.models.file.{File, Repository => Files}
        |import de.htwg.zeta.common.models.remote.Remote
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
  override def getTransformer(file: File, filter: Filter): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity): Future[Transformer] =
    Future.successful(new MyTransformer())

  /**
   * Initialize the generator
   *
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }

}
