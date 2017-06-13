import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.htwg.zeta.persistence.Persistence
import filter.BaseFilter
import models.entity.File
import models.entity.Filter
import models.entity.ModelEntity
import org.rogach.scallop.ScallopConf
import org.slf4j.LoggerFactory
import play.api.libs.ws.ahc.AhcWSClient

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val filter = opt[String](required = true)
  val session = opt[String](required = true)
  val work = opt[String](required = true)
  verify()
}

/**
 * Main class of filter
 */
object Main extends App {
  private val logger = LoggerFactory.getLogger(Main.getClass)
  logger.info("Execute Filter")
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val client = AhcWSClient()


  private val repository = cmd.session.toOption.fold(
    Persistence.fullAccessRepository
  )(session =>
    Persistence.restrictedAccessRepository(UUID.fromString(session))
  )

  cmd.filter.foreach({ id =>

    val result = for {
      filter <- repository.filters.read(UUID.fromString(id))
      file <- repository.files.read(filter.id, "filter.scala")
      fn <- compileFilter(file)
      instances <- checkInstances(fn)
      saved <- saveResult(filter, instances)
    } yield {
      saved
    }

    result foreach { result =>
      logger.info("Successful executed filter")
      System.exit(0)
    }

    result recover {
      case e: Exception =>
        logger.error(e.getMessage, e)
        System.exit(1)
    }
  })

  def compileFilter(file: File): Future[BaseFilter] = {
    val p = Promise[BaseFilter]
    logger.info("compile filter")

    val content =
      s"""
      import models.entity.{ModelEntity, MetaModelRelease}
      import filter.BaseFilter

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

  def checkInstances(filter: BaseFilter): Future[List[UUID]] = {
    val p = Promise[List[String]]
    logger.info("Check all models")

    val allFilterIds = repository.modelEntities.readAllIds()
    val allFilters = allFilterIds.flatMap(ids => Future.sequence(ids.map(repository.modelEntities.read)) )
    allFilters.flatMap(filters => Future.sequence(filters.map(checkInstance(filter, _)))).map(x => x.map(_._1).toList)

  }

  def checkInstance(fn: BaseFilter, entity: ModelEntity) = {
    val checked = fn.filter(entity)
    if (checked) {
      logger.info(s"${entity.model.metaModel.name} ${entity.id} ✓")
    } else {
      logger.info(s"${entity.model.metaModel.name} ${entity.id}")
    }
    Future.successful(entity.id, checked)
  }

  def saveResult(filter: Filter, instances: List[UUID]): Future[Any] = {
    if (filter.instanceIds.toSet == instances.toSet) {
      logger.info("Filter result is equal to saved result")
      Future.successful(true)
    } else {
      val newFilter = filter.copy(instanceIds = instances)
      logger.info("Filter need to be saved")
      repository.filters.update(filter.id, _ => newFilter)
    }
  }

}
