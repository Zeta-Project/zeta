package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.AccessAuthorisation
import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsResult
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat
import play.api.libs.json.Reads
import play.api.libs.json.Writes

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
class AccessAuthorisationFormat(
    sId: String = "id",
    sAuthorizedEntityAccess: String = "authorizedEntityAccess",
    sAuthorizedFileAccess: String = "authorizedFileAccess"
) extends OFormat[AccessAuthorisation] {

  override def writes(authorisation: AccessAuthorisation): JsObject = Json.obj(
    sId -> authorisation.id,
    sAuthorizedEntityAccess -> Writes.map(Writes.set[UUID]).writes(authorisation.authorizedEntityAccess),
    sAuthorizedFileAccess -> Writes.map(Writes.set[String]).writes(authorisation.authorizedFileAccess.map(e => (e._1.toString, e._2)))
  )

  override def reads(json: JsValue): JsResult[AccessAuthorisation] = for {
    id <- (json \ sId).validate[UUID]
    authorizedEntityAccess <- (json \ sAuthorizedEntityAccess).validate(Reads.map(Reads.set[UUID]))
    authorizedFileAccess <- (json \ sAuthorizedFileAccess).validate(Reads.map(Reads.set[String]))
  } yield {
    AccessAuthorisation(id, authorizedEntityAccess, authorizedFileAccess.map(e => (UUID.fromString(e._1), e._2)))
  }

}
