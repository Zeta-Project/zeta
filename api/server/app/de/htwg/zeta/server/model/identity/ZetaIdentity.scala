package de.htwg.zeta.server.model.identity

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import de.htwg.zeta.common.models.entity.User

case class ZetaIdentity(user: User) extends Identity {
  val id: UUID = user.id
  val firstName: String = user.firstName
  val lastName: String = user.lastName
  val email: String = user.email
  val activated: Boolean = user.activated
  val fullName: String = user.fullName
}