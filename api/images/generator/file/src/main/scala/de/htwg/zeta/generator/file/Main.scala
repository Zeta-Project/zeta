package de.htwg.zeta.generator.file

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import com.google.inject.Guice
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.generator.template.Error
import de.htwg.zeta.generator.template.Result
import de.htwg.zeta.generator.template.Settings
import de.htwg.zeta.generator.template.Success
import de.htwg.zeta.generator.template.Template
import de.htwg.zeta.generator.template.Transformer
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.general.FileRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MyTransformer() extends Transformer {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val injector = Guice.createInjector(new PersistenceModule)
  private val filePersistence = injector.getInstance(classOf[FileRepository])

  def transform(entity: GraphicalDslInstance): Future[Transformer] = {
    logger.info("Start example")
    val p = Promise[Transformer]

    val filename = "example.txt"
    val content =
      s"""
        |Number of nodes : ${entity.nodeMap.size}
        |Number of edges : ${entity.edgeMap.size}
      """.stripMargin

   filePersistence.create(File(entity.id, filename, content)).map { _ =>
      logger.info(s"Successfully saved results to '$filename' for model '${entity.name}' (MetaModel '${entity.graphicalDslId}')")
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
    for {
      image <- generatorImagePersistence.read(imageId)
      file <- createFileContent()
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

  private def createFileContent(): Future[File] = {
    val content = "This is a demo to save the results of a generator. No further configuration is required."
    val entity = File(UUID.randomUUID, Settings.generatorFile, content)
    filePersistence.create(entity)
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
  override def getTransformer(file: File, model: GraphicalDslInstance): Future[Transformer] =
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
