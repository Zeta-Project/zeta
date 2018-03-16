package de.htwg.zeta.generator.basic

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.remote.Remote
import de.htwg.zeta.common.models.remote.RemoteGenerator
import org.slf4j.LoggerFactory

/**
 * Main class of basic generator
 */
object Main extends Template[CreateOptions, RemoteOptions] {

  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * Create assets for the model transformer
   *
   * @param options   The Options for the creation of the generator
   * @param imageId     The id of the image for the generator
   * @return The result of the generator creation
   */
  override def createTransformer(options: CreateOptions, imageId: UUID): Future[Result] = {
    for {
      image <- generatorImagePersistence.read(imageId)
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
    generatorPersistence.create(entity)
  }

  private def createFile(): Future[File] = {
    val content =
      s"""
        |class MyTransformer() extends Transformer {
        |  private val logger = LoggerFactory.getLogger(getClass)
        |
        |  def transform(entity: GraphicalDslInstance) : Future[Transformer] = {
        |    logger.info(s"Model : $${entity.id}")
        |    entity.nodes.foreach { node =>
        |      logger.info(node.name)
        |    }
        |    entity.edges.foreach { edge =>
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
    filePersistence.create(entity)
  }

  private def compiledGenerator(file: File): Future[Transformer] = {
    val content =
      s"""
        |import scala.concurrent.Future
        |import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Node, Edge}
        |import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
        |import de.htwg.zeta.generator.basic.Error
        |import de.htwg.zeta.generator.basic.Result
        |import de.htwg.zeta.generator.basic.Success
        |import de.htwg.zeta.generator.basic.Transformer
        |import de.htwg.zeta.generator.basic.Warning
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
   * @param model     the GraphicalDslInstance
   * @return A Generator
   */
  override def getTransformer(file: File, model: GraphicalDslInstance): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Call the generator (called by another generator) to run with options
   *
   * @param options   the options for the generator
   * @return A Generator
   */
  override def runGeneratorWithOptions(options: RemoteOptions): Future[Result] = {
    logger.info(s"called with options $options")
    val remote: Remote = RemoteGenerator(cmd.session.getOrElse(""), cmd.work.getOrElse(""), cmd.parent.toOption, cmd.key.toOption)
    remote.emit[File](File(UUID.randomUUID, options.nodeType, "Started and sleep now"))
    Thread.sleep(10000)
    val p = Promise[Result]

    modelEntityPersistence.read(options.modelId).map { entity =>
      entity.nodeMap.values.foreach { node: Node =>
        if (node.className == options.nodeType) {
          remote.emit[File](File(UUID.randomUUID, options.nodeType, node.className))
        }
      }
      p.success(Success())
    }
    p.future
  }
}
