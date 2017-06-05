package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import models.document.TimedTask
import play.api.libs.json.Json
import play.api.libs.json.OFormat

/**
 * The user object.
 *
 * @param id                  The unique ID of the user.
 * @param firstName           The first name of the authenticated user.
 * @param lastName            The last name of the authenticated user.
 * @param email               The email of the authenticated provider.
 * @param activated           Indicates that the user has activated its registration.
 * @param accessAuthorisation The document id's the user is authorized to access.
 */
case class User(
    id: UUID,
    firstName: String,
    lastName: String,
    email: String,
    activated: Boolean,
    accessAuthorisation: AccessAuthorisation = AccessAuthorisation()
) extends Identity with Identifiable {

  /** The full name of the user. */
  val fullName = s"$firstName $lastName"

}

object User {

  /**
   * Play-Json conversion format for AccessAuthorisation.
   */
  implicit val accessAuthorisationFormat: OFormat[AccessAuthorisation] = Json.format[AccessAuthorisation]

  /**
   * Play-Json conversion format.
   */
  implicit val jsonFormat: OFormat[User] = Json.format[User]

}

/** Document id's the user is authorized to access.
 *
 * @param eventDrivenTask  id's of the [[models.document.EventDrivenTask]]
 * @param bondTask         id's of the [[models.document.BondedTask]]
 * @param timedTask        id's of the [[models.document.TimedTask]]
 * @param generator        id's of the [[models.document.Generator]]
 * @param filter           id's of the [[models.document.Filter]]
 * @param generatorImage   id's of the [[models.document.GeneratorImage]]
 * @param filterImage      id's of the [[models.document.FilterImage]]
 * @param settings         id's of the [[models.document.Settings]]
 * @param metaModelEntity  id's of the [[models.document.MetaModelEntity]]
 * @param metaModelRelease id's of the [[models.document.MetaModelRelease]]
 * @param modelEntity      id's of the [[models.document.ModelEntity]]
 * @param log              id's of the [[models.document.Log]]
 */
case class AccessAuthorisation(
    eventDrivenTask: Set[UUID] = Set.empty,
    bondTask: Set[UUID] = Set.empty,
    timedTask: Set[UUID] = Set.empty,
    generator: Set[UUID] = Set.empty,
    filter: Set[UUID] = Set.empty,
    generatorImage: Set[UUID] = Set.empty,
    filterImage: Set[UUID] = Set.empty,
    settings: Set[UUID] = Set.empty,
    metaModelEntity: Set[UUID] = Set.empty,
    metaModelRelease: Set[UUID] = Set.empty,
    modelEntity: Set[UUID] = Set.empty,
    log: Set[UUID] = Set.empty
)