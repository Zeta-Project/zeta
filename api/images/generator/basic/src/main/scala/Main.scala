import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.server.generator.Error
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Success
import de.htwg.zeta.server.generator.Transformer

/**
 * Main class of basic generator
 */
object Main extends Template[CreateOptions, String] {

  /**
   * Create assets for the model transformer
   *
   * @param options   The Options for the creation of the generator
   * @param imageId     The id of the image for the generator
   * @return The result of the generator creation
   */
  override def createTransformer(options: CreateOptions, imageId: UUID): Future[Result] = {
    for {
      image <- repository.generatorImage.read(imageId)
      file <- createFile()
      _ <- createGenerator(options, image, file)
    } yield {
      Success()
    }
  }

  private def createGenerator(options: CreateOptions, image: GeneratorImage, file: File): Future[Generator] = {
    val entity = Generator(
      id = UUID.randomUUID(),
      name = options.name,
      imageId = image.id,
      files = Map(file.id -> file.name)
    )
    repository.generator.create(entity)
  }

  private def createFile(): Future[File] = {
    val content =
      s"""
        |class MyTransformer() extends Transformer {
        |  private val logger = LoggerFactory.getLogger(getClass)
        |
        |  def transform(entity: ModelEntity) : Future[Transformer] = {
        |    logger.info(s"Model : $${entity.id}")
        |    entity.model.nodes.foreach { node =>
        |      logger.info(node.name)
        |    }
        |    entity.model.edges.foreach { edge =>
        |      logger.info(edge.name)
        |    }
        |    Future.successful(this)
        |  }
        |
        |  def exit() : Future[Result] = {
        |    val result = Success("The generator finished")
        |    Future.successful(result)
        |  }
        |}
        |
      """.stripMargin
    val entity = File(UUID.randomUUID, Settings.generatorFile, content)
    repository.file.create(entity)
  }

  private def compiledGenerator(file: File): Future[Transformer] = {
    val content =
      s"""
        |import scala.concurrent.Future
        |import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Node, Edge}
        |import de.htwg.zeta.common.models.entity.ModelEntity
        |import de.htwg.zeta.server.generator.Error
        |import de.htwg.zeta.server.generator.Result
        |import de.htwg.zeta.server.generator.Success
        |import de.htwg.zeta.server.generator.Transformer
        |import de.htwg.zeta.server.generator.Warning
        |import org.slf4j.LoggerFactory
        |
        |${file.content}
        |
        |new MyTransformer()
      """.stripMargin
    compile[Transformer](content)
  }

  /** Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param filter    not used.
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param model     the modelEntity
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param options   the options for the generator
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}
