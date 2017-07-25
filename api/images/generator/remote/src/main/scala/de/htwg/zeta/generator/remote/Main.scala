package de.htwg.zeta.generator.remote

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.entity.Generator
import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.remote.Remote
import de.htwg.zeta.common.models.remote.RemoteGenerator
import de.htwg.zeta.generator.template.Result
import de.htwg.zeta.generator.template.Settings
import de.htwg.zeta.generator.template.Success
import de.htwg.zeta.generator.template.Template
import de.htwg.zeta.generator.template.Transformer
import org.slf4j.LoggerFactory
import rx.lang.scala.Notification.OnCompleted
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext

/**
 * Main class of remote generator
 */
object Main extends Template[CreateOptions, RemoteOptions] {

  private val logger = LoggerFactory.getLogger(getClass)
  // TODO Remote /socket/generator is failing on authentication
  private val remote: Remote = RemoteGenerator(cmd.session.getOrElse(""), cmd.work.getOrElse(""), cmd.parent.toOption, cmd.key.toOption)


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
    val content = "This is a demo of the remote capabilities which doesn't require a template to configure."
    val entity = File(UUID.randomUUID, Settings.generatorFile, content)
    repository.file.create(entity)
  }

  private def compiledGenerator(file: File) = {
    Future.successful(MyGenerator(cmd.generator.toOption.fold(UUID.randomUUID)(UUID.fromString)))
  }

  /**
   * Initialize the generator
   *
   * @param file The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, filter: Filter): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Initialize the generator
   *
   * @param file The file which was loaded for the generator
   * @return A Generator
   */
  override def getTransformer(file: File, model: ModelEntity): Future[Transformer] = {
    compiledGenerator(file)
  }

  /**
   * Call the generator (called by another generator) to run with options
   *
   * @return The Result of the generator
   */
  override def runGeneratorWithOptions(options: RemoteOptions): Future[Result] = {
    logger.info(s"called with options $options")
    remote.emit[File](File(UUID.randomUUID, options.nodeType, "Started and sleep now"))
    Thread.sleep(10000)
    val p = Promise[Result]

    repository.modelEntity.read(options.modelId).map { entity =>
      entity.model.nodeMap.values.foreach { node: Node =>
        if (node.className == options.nodeType) {
          remote.emit[File](File(UUID.randomUUID, options.nodeType, node.className))
        }
      }
      p.success(Success())
    }
    p.future
  }

  private case class MyGenerator(generatorId: UUID = UUID.randomUUID) extends Transformer {

    private def transformBasicActorNode(node: Node) = {
      val actorName = "AttributeValue"
      val filename = s"${actorName}.scala"
      val content =
        s"""
          |class ${actorName}() extends Actor {
          |   def receive = {
          |
          |   }
          | }
        """.stripMargin

      File(UUID.randomUUID, filename, content)
    }

    private def transformPersistentActorNode(node: Node) = "transformPersistentActorNode"

    private def transformNode(node: Node) = {
      node.className match {
        case "BasicActor" => transformBasicActorNode(node)
        case "PersistentActor" => transformPersistentActorNode(node)
      }
    }

    def transform(entity: ModelEntity): Future[Transformer] = {
      val p = Promise[Transformer]

      val r1 = remote.call[RemoteOptions, File](generatorId, RemoteOptions("BasicActor", entity.id))
      val r2 = remote.call[RemoteOptions, File](generatorId, RemoteOptions("PersistentActor", entity.id))

      val merged = r1.merge(r2)

      merged.materialize.subscribe(n => n match {
        case OnNext(file) => logger.info(file.toString)
        case OnCompleted => p.success(this)
        case OnError(err) => p.failure(err)
      })

      p.future
    }

    def exit(): Future[Result] = {
      val result = Success("The generator finished")
      Future.successful(result)
    }
  }
}
