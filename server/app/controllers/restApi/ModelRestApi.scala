package controllers.restApi

import dao.metaModel.MetaModelDaoImpl
import models.model.ModelData
import models.model.instance.ModelReads
import models.oAuth.OAuthDataHandler
import play.api.libs.json.JsError
import play.api.mvc.{BodyParsers, Action, Controller, Result}

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._


class ModelRestApi extends Controller with OAuth2Provider {

  def echo = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val metaModelId = "30345d76-6b13-4efb-bb4a-2658a62a9f76"
      MetaModelDaoImpl.findById(metaModelId).map {
        case Some(m) => {
          val modelReads = ModelReads.metaModelDefinitionReads(m.definition)
          val in = request.body.validate(modelReads)
          in.fold(
            errors => { BadRequest(JsError.toFlatJson(errors)) },
            model => { Ok(model.toString)}
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