package controllers

import models.SecureSocialUser
import securesocial.core.RuntimeEnvironment


class ModelRESTAPI(override implicit val env: RuntimeEnvironment[SecureSocialUser])
  extends securesocial.core.SecureSocial[SecureSocialUser]{

  def getModel(modelId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getNodes(modelId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getEdges(modelId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getEdge(edgeId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

  def getNode(nodeId: String) = SecuredAction { implicit request =>
    BadRequest("currently not implemented")
  }

}
