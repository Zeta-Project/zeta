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
  val timeout: Timeout = Duration(3, TimeUnit.MINUTES)
  var createActor: TokenCache = new TokenCacheActor(system,timeout)
  var uid : UUID = UUID.randomUUID()
  var userToken : Option[UUID] = None

  "ActorTokenCache" should "create instances" in {
    createActor.getClass shouldBe classOf[TokenCacheActor]
  }

  it should "create uid" in {
    val future = createActor.create(uid)
    future.onComplete{
        case Success(u) => {
          userToken = Some(u)
          userToken.isEmpty shouldBe false
        }
        case _ => {
          false shouldBe true
        }
    }
    Await.result(future,Duration.Inf)
  }

  it should "read values" in {
    val future = createActor.read(userToken.get)
    future.onComplete{
      case Success(u) => u shouldBe uid
      case _ => ()
    }
    Await.result(future,Duration.Inf)

  }

  it should "delete values" in {
    var deleteResult = false
    val future = createActor.delete(uid)
      .andThen {
        case Success(_) => deleteResult = true
        case _ => ()
      }
    Await.result(future, Duration.Inf)
    deleteResult shouldBe true
    var notFound = false
    val futureNot = createActor.read(uid)
    futureNot.onComplete {
      case Failure(_) => notFound = true
      case _ => notFound = false
    }
    intercept[Exception] {
      Await.result(futureNot, Duration.Inf)
      notFound shouldBe true
    }
  }
}
