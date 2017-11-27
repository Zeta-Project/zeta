package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.persistence.general.MetaModelReleaseRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

import de.htwg.zeta.common.format.metaModel.MetaModelReleaseFormat
import play.api.libs.json.Writes

/**
 * REST-ful API for filter definitions
 */
class MetaModelReleaseRestApi @Inject()(
    metaModelReleaseRepo: MetaModelReleaseRepository
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

  private def getJsonArray(list: List[MetaModelRelease]) = {
    Ok(Writes.list(MetaModelReleaseFormat).writes(list))
  }

}
