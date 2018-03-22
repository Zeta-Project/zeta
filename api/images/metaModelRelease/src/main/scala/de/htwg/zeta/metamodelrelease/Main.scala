package de.htwg.zeta.metamodelrelease

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.general.GdslProjectRepository
import de.htwg.zeta.persistence.general.GraphicalDslReleaseRepository
import de.htwg.zeta.persistence.general.GraphicalDslRepository
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

  private val injector = Guice.createInjector(new PersistenceModule)
  private val metaModelEntityPersistence = injector.getInstance(classOf[GdslProjectRepository])
  private val metaModelReleasePersistence = injector.getInstance(classOf[GraphicalDslReleaseRepository])

  cmd.id.foreach({ id =>
    logger.info("Create Model Release for " + id)

    val result = for {
      from <- metaModelEntityPersistence.read(UUID.fromString(id))
      release <- metaModelReleasePersistence.createOrUpdate(
        GraphicalDslRelease(
          id = UUID.randomUUID(),
          name = s"${from.name}",
          concept = from.concept,
          diagram = from.diagram,
          shape = from.shape,
          style = from.style,
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
