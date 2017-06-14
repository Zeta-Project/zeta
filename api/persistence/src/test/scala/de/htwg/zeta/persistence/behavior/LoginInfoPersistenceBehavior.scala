package de.htwg.zeta.persistence.behavior

import com.mohiva.play.silhouette.api.LoginInfo
import de.htwg.zeta.persistence.general.LoginInfoPersistence
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait LoginInfoPersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val loginInfo1 = LoginInfo(providerID = "provider1", providerKey = "key1")
  // TODO create more TestFixtures here

  def loginInfoPersistenceBehavior(persistence: LoginInfoPersistence): Unit = { // scalastyle:ignore

    // TODO create test here

  }

}
