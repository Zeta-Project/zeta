package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
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
) extends Identity with Entity {

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
 * @param eventDrivenTasks  id's of the [[models.document.EventDrivenTask]]
 * @param bondTasks         id's of the [[models.document.BondedTask]]
 * @param timedTasks        id's of the [[models.document.TimedTask]]
 * @param generators        id's of the [[models.document.Generator]]
 * @param filters           id's of the [[models.document.Filter]]
 * @param generatorImages   id's of the [[models.document.GeneratorImage]]
 * @param filterImages      id's of the [[models.document.FilterImage]]
 * @param settings          id's of the [[models.document.Settings]]
 * @param metaModelEntities id's of the [[models.document.MetaModelEntity]]
 * @param metaModelReleases id's of the [[models.document.MetaModelRelease]]
 * @param modelEntities     id's of the [[models.document.ModelEntity]]
 * @param logs              id's of the [[models.document.Log]]
 * @param fileIndices       id's of the fileIndices
 */
case class AccessAuthorisation(
    eventDrivenTasks: Set[UUID] = Set.empty,
    bondTasks: Set[UUID] = Set.empty,
    timedTasks: Set[UUID] = Set.empty,
    generators: Set[UUID] = Set.empty,
    filters: Set[UUID] = Set.empty,
    generatorImages: Set[UUID] = Set.empty,
    filterImages: Set[UUID] = Set.empty,
    settings: Set[UUID] = Set.empty,
    metaModelEntities: Set[UUID] = Set.empty,
    metaModelReleases: Set[UUID] = Set.empty,
    modelEntities: Set[UUID] = Set.empty,
    logs: Set[UUID] = Set.empty,
    fileIndices: Set[UUID] = Set.empty
)