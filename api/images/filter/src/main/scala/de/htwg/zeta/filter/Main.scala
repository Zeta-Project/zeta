package de.htwg.zeta.filter

import java.io.FileNotFoundException
import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

import akka.actor.ActorSystem
import com.google.inject.Guice
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.entity.Filter
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.persistence.PersistenceModule
import de.htwg.zeta.persistence.general.FileRepository
import de.htwg.zeta.persistence.general.FilterRepository
import de.htwg.zeta.persistence.general.GraphicalDslInstanceRepository
import org.rogach.scallop.ScallopConf
import org.rogach.scallop.ScallopOption
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val filter: ScallopOption[String] = opt[String](required = true)
  val session: ScallopOption[String] = opt[String](required = true)
  val work: ScallopOption[String] = opt[String](required = true)
  verify()
}

/**
 * Main class of filter
 */
object Main extends App {
  private val logger = LoggerFactory.getLogger(Main.getClass)
  logger.info("Execute Filter")
  val cmd = new Commands(args)

  implicit val actorSystem: ActorSystem = ActorSystem()
  //implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val client: AhcWSClient = AhcWSClient()

  private val injector = Guice.createInjector(new PersistenceModule)
  private val modelEntityPersistence = injector.getInstance(classOf[GraphicalDslInstanceRepository])
  private val filePersistence = injector.getInstance(classOf[FileRepository])
  private val filterPersistence = injector.getInstance(classOf[FilterRepository])

  cmd.filter.foreach({ id =>
    logger.info("Run filter: " + id)
    val result = for {
      filter <- filterPersistence.read(UUID.fromString(id))
      file <- getFile("filter.scala", filter)
      fn <- compileFilter(file)
      instances <- checkInstances(fn)
      saved <- saveResult(filter, instances)
    } yield {
      saved
    }

    result foreach { _ =>
      logger.info("Successful executed filter")
      System.exit(0)
    }

    result recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  })

  private def getFile(fileName: String, filter: Filter): Future[File] = {
    filter.files.find { case (_, name) => name == fileName } match {
      case Some((id, name)) => filePersistence.read(id, name)
      case None =>
        logger.error("Could not find '{}' in Generator {}", fileName, filter.id.toString: Any)
        throw new FileNotFoundException(s"Could not find '$fileName' in Generator ${filter.id}")
    }
  }

  private def compileFilter(file: File): Future[BaseFilter] = {
    val p = Promise[BaseFilter]
    logger.info("compile filter")

    val content = s"""
      import de.htwg.zeta.common.models.modelDefinitions.model.GraphicalDslInstance
      import de.htwg.zeta.filter.BaseFilter

      ${file.content}

      new Filter()
      """
    try {
      val toolbox = currentMirror.mkToolBox()
      val tree = toolbox.parse(content)
      val compiledCode = toolbox.eval(tree)
      val fn = compiledCode.asInstanceOf[BaseFilter]
      p.success(fn)
    } catch {
      case e: Throwable => p.failure(new Exception(e))
    }

    p.future
  }

  private def checkInstances(filter: BaseFilter): Future[List[UUID]] = {
    logger.info("Check all models")
    val allFilterIds = modelEntityPersistence.readAllIds()
    val allFilters = allFilterIds.flatMap(ids => Future.sequence(ids.map(modelEntityPersistence.read)) )
    allFilters.flatMap(filters => Future.sequence(filters.map(checkInstance(filter, _)))).map(x => x.map(_._1).toList)

  }

  private def checkInstance(fn: BaseFilter, entity: GraphicalDslInstance): Future[(UUID,Boolean)] = {
    val checked = fn.filter(entity)
    if (checked) {
      logger.info(s"Check successful: metaModel ${entity.graphicalDslId}, model ${entity.id}")
    } else {
      logger.info(s"Check failed: metaModel ${entity.graphicalDslId}, model ${entity.id}")
    }
    Future.successful(Tuple2(entity.id, checked))
  }

  def saveResult(filter: Filter, instances: List[UUID]): Future[Any] = {
    if (filter.instanceIds.toSet == instances.toSet) {
      logger.info("Filter result is equal to saved result")
      Future.successful(true)
    } else {
      val newFilter = filter.copy(instanceIds = instances)
      logger.info("Filter need to be saved")
      filterPersistence.update(filter.id, _ => newFilter)
    }
  }

}
