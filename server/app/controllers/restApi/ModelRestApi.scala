package controllers.restApi

import dao.metaModel.MetaModelDaoImpl
import models.model.ModelData
import models.model.instance.ModelReads
import models.oAuth.OAuthDataHandler
import play.api.libs.json.{Json, JsError}
import play.api.mvc.{BodyParsers, Action, Controller, Result}

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRestApi extends Controller with OAuth2Provider {

  def echo = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val metaModelId = "cb1c12d6-cfbd-449f-a8c5-b5843bb6a8a4"
      MetaModelDaoImpl.findById(metaModelId).map {
        case Some(m) => {
          val modelReads = ModelReads.metaModelDefinitionReads(m.definition)
          val in = request.body.validate(modelReads)
          in.fold(
            errors => { BadRequest(JsError.toFlatJson(errors)) },
            model => { Ok(Json.toJson(model) )}
          )

        }
        case None => BadRequest("No fitting metamodel found")
      }
    }
  }

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