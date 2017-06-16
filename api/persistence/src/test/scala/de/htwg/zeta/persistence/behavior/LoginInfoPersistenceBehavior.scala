package de.htwg.zeta.persistence.behavior

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait LoginInfoPersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val loginInfo1 = LoginInfo(providerID = "provider1", providerKey = "key1")
  private val loginInfo2 = LoginInfo(providerID = "provider2", providerKey = "key2")
  //private val loginInfo3 = LoginInfo(providerID = "provider3", providerKey = "key3")


  def loginInfoPersistenceBehavior(persistence: LoginInfoPersistence): Unit = { // scalastyle:ignore
    val userId1 = UUID.fromString("9ccb9969-aa3a-40b8-8d1a-c19f878b4cb7")
    // val userId2 = UUID.randomUUID()

    it should "create a logininfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        userIdResult <- persistence.read(loginInfo1)
      } yield {
        userIdResult shouldBe userId1
      }
    }

    it should "create a secound logininfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        userIdResult <- persistence.read(loginInfo1)
      } yield {
        userIdResult shouldBe userId1
      }
    }

    it should "create a third logininfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        userIdResult <- persistence.read(loginInfo1)
      } yield {
        userIdResult shouldBe userId1
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
        i2 <- persistence.read(loginInfo1)
        i3 <- persistence.read(loginInfo1)
      } yield {
        i1 shouldBe userId1
        i2 shouldBe userId1
        i3 shouldBe userId1
      }
    }
  }

}
