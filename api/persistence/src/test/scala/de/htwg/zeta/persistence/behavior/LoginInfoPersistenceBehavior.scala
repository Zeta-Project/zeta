package de.htwg.zeta.persistence.behavior

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers

import scala.concurrent.Future


/** PersistenceBehavior. */
trait LoginInfoPersistenceBehavior extends AsyncFlatSpec with Matchers {

  def loginInfoPersistenceBehavior(persistence: LoginInfoPersistence): Unit = { // scalastyle:ignore

    val loginInfo1 = LoginInfo(providerID = "provider1", providerKey = "key1")
    val loginInfo2 = LoginInfo(providerID = "provider2", providerKey = "key2")
    val loginInfo3 = LoginInfo(providerID = "provider3", providerKey = "key3")

    val userId1 = UUID.randomUUID()
    val userId2 = UUID.randomUUID()

    // persistence.delete(loginInfo1)
    // persistence.delete(loginInfo2)
    // persistence.delete(loginInfo3)

    it should "remove all already existing LoginInfo's" in {
      for {
        existingLoginInfos <- persistence.readAllLoginInfos()
        _ <- Future.sequence(existingLoginInfos.map(loginInfo => persistence.delete(loginInfo)))
        ids <- persistence.readAllLoginInfos()
      } yield {
        ids shouldBe Set.empty
      }
    }

    it should "create a LoginInfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        userIdResult <- persistence.read(loginInfo1)
      } yield {
        userIdResult shouldBe userId1
      }
    }

    it should "create a secound logininfo" in {
      for {
        _ <- persistence.create(loginInfo2, userId1)
        userIdResult <- persistence.read(loginInfo2)
      } yield {
        userIdResult shouldBe userId1
      }
    }

    it should "create a third logininfo" in {
      for {
        _ <- persistence.create(loginInfo3, userId2)
        userIdResult <- persistence.read(loginInfo3)
      } yield {
        userIdResult shouldBe userId2
      }
    }

    it should "fail the future with any Exception, when creating an already existing file" in {
      recoverToSucceededIf[Exception] {
        persistence.create(loginInfo1, userId1)
      }
    }

    it should "read the first and second logininfo" in {
      for {
        i1 <- persistence.read(loginInfo1)
        i2 <- persistence.read(loginInfo2)
        i3 <- persistence.read(loginInfo3)
      } yield {
        i1 shouldBe userId1
        i2 shouldBe userId1
        i3 shouldBe userId2
      }
    }
  }

}
