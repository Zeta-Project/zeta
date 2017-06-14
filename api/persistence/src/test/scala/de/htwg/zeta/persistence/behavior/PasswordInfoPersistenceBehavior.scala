package de.htwg.zeta.persistence.behavior

import com.mohiva.play.silhouette.api.util.PasswordInfo
import de.htwg.zeta.persistence.general.PasswordInfoPersistence
import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers


/** PersistenceBehavior. */
trait PasswordInfoPersistenceBehavior extends AsyncFlatSpec with Matchers {

  private val passwordInfo1 = PasswordInfo(hasher = "hasher1", password = "password1", salt = None)
  private val passwordInfo2 = PasswordInfo(hasher = "hasher2", password = "password2", salt = Some("salt2"))
  // TODO create more TestFixtures here

  def passwordInfoPersistenceBehavior(persistence: PasswordInfoPersistence): Unit = { // scalastyle:ignore

    // TODO create test here

  }

}
