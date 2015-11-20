package controllers

import models.Model
import models.oauth.OAuthDataHandler
import play.api.mvc.{Result, Action, Controller}

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRESTAPI extends Controller with OAuth2Provider {

  def getModel(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(Model.findById(modelId))
    }
  }

  def getNodes(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(Model.getNodes(modelId))
    }
  }

  def getEdges(modelId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(Model.getEdges(modelId))
    }
  }

  def getNode(nodeId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(Model.getNode(nodeId))
    }
  }

  def getEdge(edgeId: String) = Action.async { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      serve(Model.getEdge(edgeId))
    }
  }

  private def serve(model: Future[Option[Model]]): Future[Result] = {
    model.map { om => om match {
      case Some(m) => Ok(m.modelDataJson)
      case _ => NotFound
      }
    }
  }

}
