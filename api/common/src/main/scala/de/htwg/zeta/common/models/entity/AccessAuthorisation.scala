package de.htwg.zeta.common.models.entity

import java.util.UUID


/** All entity id's a user is authorized to access.
 *
 * @param id                     entity-id, same id as [[de.htwg.zeta.common.models.entity.User]]
 * @param authorizedEntityAccess authorized id's for entities
 * @param authorizedFileAccess   authorized id's for files, with a counter on the number of files per id
 */
case class AccessAuthorisation(
    id: UUID,
    authorizedEntityAccess: Map[String, Set[UUID]],
    authorizedFileAccess: Map[UUID, Int]
) extends Entity {

  /** Add a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the added id
   */
  def grantEntityAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    copy(authorizedEntityAccess = authorizedEntityAccess.updated(
      entityType, authorizedEntityAccess.getOrElse(entityType, Set.empty) + entityId)
    )
  }

  /** Remove a id to the accessible id's.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return copy of this with the removed id
   */
  def revokeEntityAccess(entityType: String, entityId: UUID): AccessAuthorisation = {
    val authorizedIds = authorizedEntityAccess(entityType) - entityId
    if (authorizedIds.isEmpty) {
      copy(authorizedEntityAccess = authorizedEntityAccess - entityType)
    } else {
      copy(authorizedEntityAccess = authorizedEntityAccess.updated(entityType, authorizedIds))
    }
  }

  /** List all accessible id's.
   *
   * @param entityType the name of the entity type
   * @return List with the accessible id's
   */
  def listEntityAccess(entityType: String): Set[UUID] = {
    authorizedEntityAccess.getOrElse(entityType, Set.empty)
  }

  /** Check if a id is authorised to access.
   *
   * @param entityType the name of the entity type
   * @param entityId   the entity-id
   * @return List with the accessible id's
   */
  def checkEntityAccess(entityType: String, entityId: UUID): Boolean = {
    listEntityAccess(entityType).contains(entityId)
  }

  /** Add a id to the accessible file id's.
   *
   * @param fileId the file-id
   * @param name   the name of the file
   * @return copy of this with the added id
   */
  def grantFileAccess(fileId: UUID, name: String): AccessAuthorisation = {
    copy(authorizedFileAccess = authorizedFileAccess.updated(
      fileId, authorizedFileAccess.getOrElse(fileId, 0) + 1)
    )
  }

  /** Remove a id to the accessible id's.
   *
   * @param fileId the file-id
   * @param name   the name of the file
   * @return copy of this with the removed id
   */
  def revokeFileAccess(fileId: UUID, name: String): AccessAuthorisation = {
    val remaining = authorizedFileAccess(fileId) - 1
    if (remaining == 0) {
      copy(authorizedFileAccess = authorizedFileAccess - fileId)
    } else {
      copy(authorizedFileAccess = authorizedFileAccess.updated(fileId, remaining))
    }
  }

  /** List all accessible id's.
   *
   * @return List with the accessible id's
   */
  def listFileAccess(): Set[UUID] = {
    authorizedFileAccess.keySet
  }

  /** Check if a id is authorised to access.
   *
   * @param fileId the file-id
   * @return List with the accessible id's
   */
  def checkFileAccess(fileId: UUID): Boolean = {
    authorizedFileAccess.contains(fileId)
  }

}
