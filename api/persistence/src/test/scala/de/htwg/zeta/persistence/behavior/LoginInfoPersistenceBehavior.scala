package de.htwg.zeta.persistence.behavior

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait LoginInfoPersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val loginInfo1 = LoginInfo(providerID = "provider1", providerKey = "key1")
  private val userId1 = UUID.randomUUID()

  def loginInfoPersistenceBehavior(persistence: LoginInfoPersistence): Unit = { // scalastyle:ignore

    it should "create a logininfo" in {
      for {
        _ <- persistence.create(loginInfo1, userId1)
        result <- persistence.read(loginInfo1)
      } yield {
        result shouldBe Map(result -> loginInfo1)
      }
    }
  }

}
