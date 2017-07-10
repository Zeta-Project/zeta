import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.server.generator.Error
import de.htwg.zeta.server.generator.Result
import de.htwg.zeta.server.generator.Success
import de.htwg.zeta.server.generator.Transformer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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
      _ <- repository.generator.create(Generator(user, options.name, image.id))
      _ <- repository.file.create(createFile(Settings.generatorFile))
    } yield {
      Success()
    }
  }

  private def createFile(name: String): File = {
    val content =
      s"""
        |class MyTransformer() extends Transformer {
        |  private val logger = LoggerFactory.getLogger(getClass)
        |
        |  def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote) : Future[Transformer] = {
        |    logger.info(s"User : $${entity.id}")
        |    entity.model.elements.values.foreach { element => element match {
        |      case node: Node => logger.info(node.`type`.name)
        |      case edge: Edge => logger.info(edge.`type`.name)
        |    }}
        |    Future.successful(this)
        |  }
        |
        |  def exit()(implicit documents    : Documents, files: Files, remote: Remote) : Future[Result] = {
        |    val result = Success("The generator finished")
        |    Future.successful(result)
        |  }
        |}
        |
      """.stripMargin
    File(UUID.randomUUID, name, content)
  }

  private def compiledGenerator(file: File): Future[Transformer] = {
    val content =
      s"""
        |import scala.concurrent.Future
        |import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Node, Edge}
        |import de.htwg.zeta.common.models.document.ModelEntity
        |import de.htwg.zeta.common.models.document.{Repository => Documents}
        |import de.htwg.zeta.common.models.file.{Repository => Files}
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
