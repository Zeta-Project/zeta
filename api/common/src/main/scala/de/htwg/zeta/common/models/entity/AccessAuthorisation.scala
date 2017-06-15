package de.htwg.zeta.common.models.entity

import java.util.UUID


/** All entity id's a user is authorized to access.
 *
 * @param id               entity-id, same id as [[de.htwg.zeta.common.models.entity.User]]
 * @param authorizedAccess all authorized id's
 */
case class AccessAuthorisation(
    id: UUID,
    authorizedAccess: Map[String, Set[UUID]]
) extends Entity {

  /** Add a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the added id
   */
  def grantAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    copy(authorizedAccess = authorizedAccess.updated(
      entityType, authorizedAccess.getOrElse(entityType, Set.empty) + entityId)
    )
  }

  /** Remove a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the removed id
   */
  def revokeAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    val authorizedIds = authorizedAccess.getOrElse(entityType, Set.empty) - entityId
    if (authorizedIds.isEmpty) {
      copy(authorizedAccess = authorizedAccess - entityType)
    } else {
      copy(authorizedAccess = authorizedAccess.updated(entityType, authorizedIds))
    }
  }

  /** List all accessible id's.
   *
   * @param entityType the name of the entity type
   * @return List with the accessible id's
   */
  def listAccess(entityType: String): Set[UUID] = {
    authorizedAccess.getOrElse(entityType, Set.empty)
  }

  /** Check if a id is authorised to access.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return List with the accessible id's
   */
  def checkAccess(entityType: String, entityId: UUID): Boolean = {
    listAccess(entityType).contains(entityId)
  }

}
