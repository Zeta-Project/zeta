package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import de.htwg.zeta.persistence.transientCache.TransientCachePersistence
import models.document.EventDrivenTask
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
   * Play-Json conversion format.
   */
  implicit val jsonFormat: OFormat[User] = Json.format[User]

}

/** Document id's the user is authorized to access.
 *
 * @param eventDrivenTask  id's of the [[models.document.EventDrivenTask]]
 * @param bondTask         id's of the [[models.document.BondedTask]]
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
    eventDrivenTask: Seq[UUID] = Seq.empty,
    bondTask: Seq[UUID] = Seq.empty,
    generator: Seq[UUID] = Seq.empty,
    filter: Seq[UUID] = Seq.empty,
    generatorImage: Seq[UUID] = Seq.empty,
    filterImage: Seq[UUID] = Seq.empty,
    settings: Seq[UUID] = Seq.empty,
    metaModelEntity: Seq[UUID] = Seq.empty,
    metaModelRelease: Seq[UUID] = Seq.empty,
    modelEntity: Seq[UUID] = Seq.empty,
    log: Seq[UUID] = Seq.empty
)