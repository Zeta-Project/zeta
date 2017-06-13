import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.Persistence
import models.entity.MetaModelRelease
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
      from <- documents.metaModelEntities.read(UUID.fromString(id))
      version <- documents.metaModelReleases.readVersionKeys(from.id).map(_.last.toInt + 1)
      release <- documents.metaModelReleases.createVersion(version.toString,
        MetaModelRelease(
          name = s"${from.metaModel.name} $version",
          metaModel = from.metaModel,
          dsl = from.dsl,
          version = version.toString
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
