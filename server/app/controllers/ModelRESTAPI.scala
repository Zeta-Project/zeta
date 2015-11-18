package controllers

import models.oauth.OAuthDataHandler
import play.api.libs.json.Json
import play.api.mvc.Controller

import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRESTAPI extends Controller with OAuth2Provider {

  def getModel(modelId: String) = AuthorizedAction(OAuthDataHandler()) { request =>
    BadRequest("currently not implemented")
  }

  def getNodes(modelId: String) = AuthorizedAction(OAuthDataHandler()) { request =>
    BadRequest("currently not implemented")
  }

  def getEdges(modelId: String) = AuthorizedAction(OAuthDataHandler()) { request =>
    BadRequest("currently not implemented")
  }

  def getEdge(edgeId: String) = AuthorizedAction(OAuthDataHandler()) { request =>
    BadRequest("currently not implemented")
  }

  def getNode(nodeId: String) = AuthorizedAction(OAuthDataHandler()) { request =>
    BadRequest("currently not implemented")
  }

}
