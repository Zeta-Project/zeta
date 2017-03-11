import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.document._
import models.document.http.{ HttpRepository => DocumentRepository }
import models.file.http.{ HttpRepository => FileRepository }
import org.rogach.scallop.ScallopConf
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }

class Commands(arguments: Seq[String]) extends ScallopConf(arguments) {
  val id = opt[String](required = true)
  val session = opt[String](required = true)
  val work = opt[String]()
  verify()
}

object Main extends App {
  val cmd = new Commands(args)

  implicit val actorSystem = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val client = AhcWSClient()

  val documents = DocumentRepository(cmd.session.getOrElse(""))

  cmd.id.foreach({ id =>
    println("Create Model Release for " + id)

    val result = for {
      from <- documents.get[MetaModelEntity](id)
      doc <- getRelease(from)
      release <- documents.create[MetaModelRelease](doc)
    } yield release

    result foreach { result =>
      println("Successful created model release ")
      System.exit(0)
    }

    result recover {
      case e: Exception =>
        println(e)
        System.exit(1)
    }
  })

  def getRelease(current: MetaModelEntity): Future[MetaModelRelease] = {
    val p = Promise[MetaModelRelease]
    var version = 1

    documents.query[MetaModelRelease](AllMetaModelReleases(current))
      .doOnCompleted {
        val release = MetaModelRelease(current, version.toString)
        p.success(release)
      }
      .foreach { release =>
        version += 1
      }

    p.future
  }
}

