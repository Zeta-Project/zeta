import java.util.UUID

import de.htwg.zeta.persistence.Persistence
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.document.AllMetaModelReleases
import models.document.MetaModelEntity
import models.document.MetaModelRelease
import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise




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
      doc <- getRelease(from)
      release <- documents.metaModelRelease.create(doc)
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

  def getRelease(current: MetaModelEntity): Future[MetaModelRelease] = {
    val p = Promise[MetaModelRelease]
    var version = 1

    documents.query[MetaModelRelease](AllMetaModelReleases(current))
      .doOnCompleted {
        val release = MetaModelRelease(
          name = s"${current.metaModel.name} $version",
          metaModel = current.metaModel,
          dsl = current.dsl,
          version = version.toString
        )
        p.success(release)
      }
      .foreach { release =>
        version += 1
      }

    p.future
  }
}
