package de.htwg.zeta.persistence.actorCache

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

import akka.actor.ActorSystem
import akka.util.Timeout
import de.htwg.zeta.common.models.entity.CodeDocument
import de.htwg.zeta.persistence.general.CodeDocumentPersistence

/**
 * Actor Cache Implementation of EntityPersistence.
 */
class ActorCacheCodeDocumentPersistence(
    system: ActorSystem,
    underlying: CodeDocumentPersistence,
    numberActorsPerEntityType: Int,
    cacheDuration: FiniteDuration,
    timeout: Timeout
) extends ActorCacheEntityPersistence[CodeDocument](
  system = system,
  underlying = underlying,
  numberActorsPerEntityType = numberActorsPerEntityType,
  cacheDuration = cacheDuration,
  timeout = timeout
) with CodeDocumentPersistence {

  /** Find a CodeDocument by the id of a MetaModel and the DSL Type.
   *
   * @param metaModelId The id of the MetaModel
   * @param dslType     The type of the DSL
   * @return Future with the CodeDocument
   */
  override def findByMetaModelIdAndDslType(metaModelId: UUID, dslType: String): Future[CodeDocument] = {
    // TODO this is inefficient and should be implemented natively without extending the ActorCacheEntityPersistence
    for {
      allIds <- readAllIds()
      allEntities <- Future.sequence(allIds.map(read))
    } yield {
      allEntities.filter(doc => doc.metaModelId == metaModelId && doc.metaModelId == metaModelId).head
    }
  }

}
