import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.persistence.Persistence
import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient


/**
 *
 * @param arguments the commands
 */
class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val id: ScallopOption[String] = opt[String](required = true)
  val session: ScallopOption[String] = opt[String](required = true)
  val work: ScallopOption[String] = opt[String]()
  verify()
}

/**
 * Main class of metaModelRelease
 */
object Main extends App {
  private val logger = LoggerFactory.getLogger(getClass)
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val client = AhcWSClient()

  val documents = Persistence.fullAccessRepository

  cmd.id.foreach({ id =>
    logger.info("Create Model Release for " + id)

    val result = for {
      from <- documents.metaModelEntity.read(UUID.fromString(id))
      release <- documents.metaModelRelease.createOrUpdate(
        MetaModelRelease(
          id = UUID.randomUUID(),
          name = s"${from.metaModel.name}",
          metaModel = from.metaModel,
          dsl = from.dsl,
          version = "1"
        )
      )
    } yield {
      release
    }

    result foreach { result =>
      logger.info("Successful created model release ")
      System.exit(0)
    }

    result recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  })

}
