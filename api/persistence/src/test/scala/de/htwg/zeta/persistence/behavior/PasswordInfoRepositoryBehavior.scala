package de.htwg.zeta.persistence.behavior

import scala.concurrent.Future

import de.htwg.zeta.persistence.authInfo.ZetaLoginInfo
import de.htwg.zeta.persistence.authInfo.ZetaPasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoRepository
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers


/** PasswordInfoPersistenceBehavior. */
trait PasswordInfoRepositoryBehavior extends AsyncFlatSpec with Matchers {

  def passwordInfoPersistenceBehavior(persistence: PasswordInfoRepository): Unit = { // scalastyle:ignore

    val loginInfo1 = ZetaLoginInfo(providerID = "providerId1", providerKey = "providerKey1")
    val loginInfo2 = ZetaLoginInfo(providerID = "providerId2", providerKey = "providerKey2")
    val loginInfo3 = ZetaLoginInfo(providerID = "providerId3", providerKey = "providerKey3")

    val passwordInfo1 = ZetaPasswordInfo(hasher = "hasher1", password = "password1", salt = None)
    val passwordInfo2 = ZetaPasswordInfo(hasher = "hasher2", password = "password2", salt = Some("salt2"))
    val passwordInfo3 = ZetaPasswordInfo(hasher = "hasher3", password = "password3", salt = Some("salt3"))

    it should "remove all already existing PasswordInfo's" in {
      for {
        existingKeys <- persistence.readAllKeys()
        _ <- Future.sequence(existingKeys.map(persistence.remove))
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set.empty
      }
    }

    it should "add a PasswordInfo" in {
      for {
        info1 <- persistence.add(loginInfo1, passwordInfo1)
        keys <- persistence.readAllKeys()
      } yield {
        info1 shouldBe passwordInfo1
        keys shouldBe Set(loginInfo1)
      }
    }

    it should "add a second PasswordInfo" in {
      for {
        info2 <- persistence.add(loginInfo2, passwordInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        info2 shouldBe passwordInfo3
        keys shouldBe Set(loginInfo1, loginInfo2)
      }
    }

    it should "throw any exception, when adding an already existing" in {
      recoverToSucceededIf[Exception] {
        persistence.add(loginInfo2, passwordInfo3)
      }
    }

    it should "update the second PasswordInfo" in {
      for {
        info2 <- persistence.update(loginInfo2, passwordInfo2)
        keys <- persistence.readAllKeys()
      } yield {
        info2 shouldBe passwordInfo2
        keys shouldBe Set(loginInfo1, loginInfo2)
      }
    }

    it should "throw any exception, when updating an non-existing PasswordInfo" in {
      recoverToSucceededIf[Exception] {
        persistence.update(loginInfo3, passwordInfo3)
      }
    }

    it should "save a third PasswordInfo" in {
      for {
        info3 <- persistence.add(loginInfo3, passwordInfo1)
        keys <- persistence.readAllKeys()
      } yield {
        info3 shouldBe passwordInfo1
        keys shouldBe Set(loginInfo1, loginInfo2, loginInfo3)
      }
    }

    it should "update the third entity, when saving it again" in {
      for {
        info3 <- persistence.save(loginInfo3, passwordInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        info3 shouldBe passwordInfo3
        keys shouldBe Set(loginInfo1, loginInfo2, loginInfo3)
      }
    }

    it should "find all PasswordInfo's" in {
      for {
        info1 <- persistence.find(loginInfo1)
        info2 <- persistence.find(loginInfo2)
        info3 <- persistence.find(loginInfo3)
      } yield {
        info1 shouldBe Some(passwordInfo1)
        info2 shouldBe Some(passwordInfo2)
        info3 shouldBe Some(passwordInfo3)
      }
    }

    it should "remove the first PasswordInfo" in {
      for {
        _ <- persistence.remove(loginInfo1)
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set(loginInfo2, loginInfo3)
      }
    }

    it should "return None, when finding a non-existent PasswordInfo" in {
      for {
        info1 <- persistence.find(loginInfo1)
      } yield {
        info1 shouldBe None
      }
    }

    it should "remove the second and third PasswordInfo" in {
      for {
        _ <- persistence.remove(loginInfo2)
        _ <- persistence.remove(loginInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set.empty
      }
    }

    it should "add all PasswordInfo*s again" in {
      for {
        _ <- persistence.add(loginInfo1, passwordInfo1)
        _ <- persistence.add(loginInfo2, passwordInfo2)
        _ <- persistence.add(loginInfo3, passwordInfo3)
        keys <- persistence.readAllKeys()
      } yield {
        keys shouldBe Set(loginInfo1, loginInfo2, loginInfo3)
      }
    }

  }

}
