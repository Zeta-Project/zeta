package de.htwg.zeta.server.actor

import java.util.concurrent.TimeUnit
import java.util.UUID

import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import akka.actor.ActorSystem
import akka.util.Timeout
import de.htwg.zeta.server.model.TokenCache
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class TransientTokenCacheActorSpec extends AnyFlatSpec with Matchers {

  val system: ActorSystem = ActorSystem()
  val timeout: Timeout = Duration(1, TimeUnit.MINUTES)
  var createActor: TokenCache = new TokenCacheActor(system,timeout)
  var uid : UUID = UUID.randomUUID()
  var userToken : UUID = _

  "ActorTokenCache" should "create instances" in {
    createActor.getClass shouldBe classOf[TokenCacheActor]
  }

  it should "create uid" in {
    val future = createActor.create(uid)
    future.onComplete{
        case Success(u) => userToken = u
        case _ => ()
    }
    Await.result(future,Duration.Inf)
    userToken != null shouldBe true
  }
  it should "read values" in {
    val future = createActor.read(userToken)
    future.onComplete{
      case Success(u) => u shouldBe uid
      case _ => ()
    }
    Await.result(future,Duration.Inf)

  }
  it should "delete values" in {
    var deleteResult = false
    val future = createActor.delete(uid)
      .map { _: Unit => true }
      .andThen {
        case Success(_) => deleteResult = true
        case _ => ()
      }
    Await.result(future, Duration.Inf)
    deleteResult shouldBe true
    createActor.read(uid).andThen {
        case Failure(_) => deleteResult = true
        case _ => ()
      }
  }
}
