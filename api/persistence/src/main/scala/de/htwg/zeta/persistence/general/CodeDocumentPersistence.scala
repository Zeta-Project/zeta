package de.htwg.zeta.persistence.general

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.CodeDocument

/**
 * CodeDocumentPersistence.
 */
trait CodeDocumentPersistence extends EntityPersistence[CodeDocument] {

  /** Find a CodeDocument by the id of a MetaModel and the DSL Type.
   *
   * @param metaModelId The id of the MetaModel
   * @param dslType The type of the DSL
   * @return Future with the CodeDocument
   */
  def findByMetaModelIdAndDslType(metaModelId: UUID, dslType: String): Future[CodeDocument]

}
