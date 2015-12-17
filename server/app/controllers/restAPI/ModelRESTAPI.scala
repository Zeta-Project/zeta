package controllers.restApi

import models.model.ModelData
import models.oAuth.OAuthDataHandler
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRestApi extends Controller with OAuth2Provider {

  def getModel(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(ModelData.findById(modelId))
    }
  }

  def getNodes(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(ModelData.getNodes(modelId))
    }
  }

  def getEdges(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(ModelData.getEdges(modelId))
    }
  }

  def getNode(modelId: String, nodeId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(ModelData.getNode(modelId, nodeId))
    }
  }

  def getEdge(modelId: String, edgeId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(ModelData.getEdge(modelId, edgeId))
    }
  }

  private def serve(model: Future[Option[ModelData]]): Future[Result] = {
    model.map {
      case Some(m) => Ok(m.modelDataJson)
      case _ => NotFound
    }
  }

}
