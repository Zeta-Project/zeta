package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.format.metaModel.GraphicalDslReleaseFormat
import de.htwg.zeta.common.models.entity.GraphicalDslRelease
import de.htwg.zeta.persistence.general.GraphicalDslReleaseRepository
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


/**
 * REST-ful API for filter definitions
 */
class MetaModelReleaseRestApi @Inject()(
    metaModelReleaseRepo: GraphicalDslReleaseRepository,
    metaModelReleaseFormat: GraphicalDslReleaseFormat
) extends Controller with Logging {

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelReleaseRepo.readAllIds().flatMap(getIds).map(getJsonArray).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  private def getIds(ids: Set[UUID]) = {
    val list = ids.toList.map(metaModelReleaseRepo.read)
    Future.sequence(list)
  }

  private def getJsonArray(list: List[GraphicalDslRelease]) = {
    Ok(Writes.list(metaModelReleaseFormat).writes(list))
  }

}
