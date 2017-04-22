import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import filter.BaseFilter

import models.document.AllModels
import models.document.Filter
import models.document.ModelEntity
import models.document.http.{ HttpRepository => DocumentRepository }
import models.file.File
import models.file.http.{ HttpRepository => FileRepository }

import org.rogach.scallop.ScallopConf

import play.api.libs.ws.ahc.AhcWSClient

import rx.lang.scala.Observable

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val filter = opt[String](required = true)
  val session = opt[String](required = true)
  val work = opt[String](required = true)
  verify()
}

object Main extends App {
  println("Execute Filter")
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val client = AhcWSClient()

  val documents = DocumentRepository(cmd.session.getOrElse(""))
  val files = FileRepository(cmd.session.getOrElse(""))

  cmd.filter.foreach({ id =>

    val result = for {
      filter <- documents.get[Filter](id)
      file <- files.get(filter, "filter.scala")
      fn <- compileFilter(file)
      instances <- checkInstances(fn)
      saved <- saveResult(filter, instances)
    } yield saved

    result foreach { result =>
      println("Successful executed filter")
      System.exit(0)
    }

    result recover {
      case e: Exception =>
        println(e)
        System.exit(1)
    }
  })

  def compileFilter(file: File): Future[BaseFilter] = {
    val p = Promise[BaseFilter]
    println("compile filter")

    val content = s"""
      import models.document.{ModelEntity, MetaModelRelease}
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

  def checkInstances(filter: BaseFilter) = {
    val p = Promise[List[String]]
    println("Check all models")

    documents.query[ModelEntity](AllModels())
      .doOnError(p.failure(_))
      .flatMap(instance => Observable.from(checkInstance(filter, instance)))
      .doOnError(p.failure(_))
      .filter { case (instance, checked) => if (checked) true else false }
      .map(_._1)
      .toList
      .foreach(p.success(_))
    p.future
  }

  def checkInstance(fn: BaseFilter, entity: ModelEntity) = {
    val checked = fn.filter(entity)
    if (checked) {
      println(s"${entity.model.metaModel.name} ${entity.id} ✓")
    } else {
      println(s"${entity.model.metaModel.name} ${entity.id}")
    }
    Future.successful(entity.id, checked)
  }

  def saveResult(filter: Filter, instances: List[String]): Future[Any] = {
    if (filter.instances.toSet == instances.toSet) {
      println("Filter result is equal to saved result")
      Future.successful(true)
    } else {
      val newFilter = filter.copy(instances = instances)
      println("Filter need to be saved")
      documents.update[Filter](newFilter)
    }
  }
}
