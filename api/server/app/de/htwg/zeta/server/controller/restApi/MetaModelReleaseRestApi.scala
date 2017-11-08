package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.common.models.entity.MetaModelRelease
import de.htwg.zeta.persistence.general.EntityRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * REST-ful API for filter definitions
 */
class MetaModelReleaseRestApi @Inject()(
    metaModelReleaseRepo: EntityRepository[MetaModelRelease]
) extends Controller with Logging {

  /** Lists all filter.
   *
   * @param request The request
   * @return The result
   */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelReleaseRepo.readAllIds().flatMap(getIds(metaModelReleaseRepo)).map(getJsonArray).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  private def getIds(repo: EntityRepository[MetaModelRelease])(ids: Set[UUID]) = {
    val list = ids.toList.map(repo.read)
    Future.sequence(list)
  }

  private def getJsonArray(list: List[MetaModelRelease]) = {
    Ok(Json.toJson(list))
  }

}
