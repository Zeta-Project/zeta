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
    val loginInfo2Updated = loginInfo2.copy(providerKey = "updatedKey")

    val userId1 = UUID.randomUUID()
    val userId2 = UUID.randomUUID()

    it should "remove all already existing LoginInfo's" in {
      for {
        existingKeys <- persistence.readAllKeys()
        _ <- Future.sequence(existingKeys.map(loginInfo => persistence.delete(loginInfo)))
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set.empty
      }
    }

    it should "create a LoginInfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        userIdResult <- persistence.read(loginInfo1)
        keys <- persistence.readAllKeys()
      } yield {
        userIdResult shouldBe userId1
        keys shouldBe Set(loginInfo1)
      }
    }

    it should "create a second LoginInfo" in {
      for {
        _ <- persistence.create(loginInfo2, userId1)
        userIdResult <- persistence.read(loginInfo2)
        keys <- persistence.readAllKeys()
      } yield {
        userIdResult shouldBe userId1
        keys shouldBe Set(loginInfo1, loginInfo2)
      }
    }

    it should "create a third LoginInfo" in {
      for {
        _ <- persistence.create(loginInfo3, userId2)
        userIdResult <- persistence.read(loginInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        userIdResult shouldBe userId2
        keys shouldBe Set(loginInfo1, loginInfo2, loginInfo3)
      }
    }

    it should "fail the future with any Exception, when creating an already existing file" in {
      recoverToSucceededIf[Exception] {
        persistence.create(loginInfo1, userId1)
      }
    }

    it should "read the first and second LoginInfo" in {
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

    it should "throw any exception, when reading an non existent LoginInfo" in {
      recoverToSucceededIf[Exception] {
        persistence.read(loginInfo2Updated)
      }
    }

    it should "update the second LoginInfo" in {
      for {
        _ <- persistence.update(loginInfo2, loginInfo2Updated)
        i1 <- persistence.read(loginInfo1)
        i2 <- persistence.read(loginInfo2Updated)
        i3 <- persistence.read(loginInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        i1 shouldBe userId1
        i2 shouldBe userId1
        i3 shouldBe userId2
        keys shouldBe Set(loginInfo1, loginInfo2Updated, loginInfo3)
      }
    }

    it should "throw any exception, when updating an non existent LoginInfo" in {
      recoverToSucceededIf[Exception] {
        persistence.update(loginInfo2, loginInfo2.copy(providerKey = "key4"))
      }
    }

    it should "throw any exception, when updating to an already existent LoginInfo" in {
      recoverToSucceededIf[Exception] {
        persistence.update(loginInfo1, loginInfo3)
      }
    }

    it should "remove the first LoginInfo" in {
      for {
        _ <- persistence.delete(loginInfo1)
        i2 <- persistence.read(loginInfo2Updated)
        i3 <- persistence.read(loginInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        i2 shouldBe userId1
        i3 shouldBe userId2
        keys shouldBe Set(loginInfo2Updated, loginInfo3)
      }
    }

    it should "throw any exception, when deleting a non existing LoginInfo" in {
      recoverToSucceededIf[Exception] {
        persistence.delete(loginInfo1)
      }
    }

    it should "remove the second and third LoginInfo" in {
      for {
        _ <- persistence.delete(loginInfo2Updated)
        _ <- persistence.delete(loginInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set.empty
      }
    }

    it should "add all LoginInfo's again" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        _ <- persistence.create(loginInfo2, userId1)
        _ <- persistence.create(loginInfo3, userId2)
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set(loginInfo1, loginInfo2, loginInfo3)
      }
    }

  }

}
