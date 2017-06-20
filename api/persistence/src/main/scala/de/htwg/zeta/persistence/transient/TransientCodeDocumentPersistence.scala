package de.htwg.zeta.persistence.transient

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.CodeDocument
import de.htwg.zeta.persistence.general.CodeDocumentPersistence

/**
 * Transient implementation of CodeDocumentPersistence.
 */
class TransientCodeDocumentPersistence extends TransientEntityPersistence[CodeDocument] with CodeDocumentPersistence {

  /** Find a CodeDocument by the id of a MetaModel and the DSL Type.
   *
   * @param metaModelId The id of the MetaModel
   * @param dslType     The type of the DSL
   * @return Future with the CodeDocument
   */
  override def findByMetaModelIdAndDslType(metaModelId: UUID, dslType: String): Future[CodeDocument] = {
    for {
      allIds <- readAllIds()
      allEntities <- Future.sequence(allIds.map(read))
    } yield {
      allEntities.filter(doc => doc.metaModelId == metaModelId && doc.metaModelId == metaModelId).head
    }
  }

}
