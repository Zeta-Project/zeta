import generator.Error
import generator.Result
import generator.Success
import generator.Transformer
import models.document.Filter
import models.document.Generator
import models.document.GeneratorImage
import models.document.ModelEntity
import models.document.{Repository => Documents}
import models.file.File
import models.file.{Repository => Files}
import models.remote.Remote

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
   * @param image     The id of the image for the generator
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @param remote    Access to docker container
   * @return The result of the generator creation
   */
  override def createTransformer(options: CreateOptions, image: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    for {
      image <- documents.get[GeneratorImage](image)
      generator <- documents.create[Generator](Generator(user, options.name, image))
      created <- files.create(generator, createFile(Settings.generatorFile))
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
        |  def exit()(implicit documents: Documents, files: Files, remote: Remote) : Future[Result] = {
        |    val result = Success("The generator finished")
        |    Future.successful(result)
        |  }
        |}
        |
      """.stripMargin
    File(name, content)
  }

  private def compiledGenerator(file: File): Future[Transformer] = {
    val content =
      s"""
        |import scala.concurrent.Future
        |import models.modelDefinitions.model.elements.{Node, Edge}
        |import models.document.ModelEntity
        |import models.document.{Repository => Documents}
        |import models.file.{Repository => Files}
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

  /** Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param filter    not used.
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @param remote    unused.
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file      The file which was loaded for the generator
   * @param model     the modelEntity
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @param remote    Access to docker container
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param options   the options for the generator
   * @param documents Access to the Documents repository
   * @param files     Access to the Files repository
   * @param remote    Access to docker container
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Result] = {
    Future.successful(Error(s"Call a generator from a generator is not supported in this example"))
  }
}
