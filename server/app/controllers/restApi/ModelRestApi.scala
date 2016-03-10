package controllers.restApi

import dao.metaModel._
import models.modelDefinitions.model.Model
import models.oAuth.OAuthDataHandler
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider
import scalaoauth2.provider.OAuth2ProviderActionBuilders._



class ModelRestApi extends Controller with OAuth2Provider {

  val mmdao: ZetaMetaModelDao = MetaModelDao

  def echo = Action.async(BodyParsers.parse.json) { implicit request =>
    authorize(OAuthDataHandler()) { authInfo =>
      val metaModelId = "8dd23445-0333-4f12-8dc1-3c5a10ddb11f"
      mmdao.findById(metaModelId).map {
        case Some(m) => {
          val modelReads = Model.reads(m.metaModel)
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

//  def getModel(modelId: String) = Action.async { implicit request =>
//    authorize(OAuthDataHandler()) { authInfo =>
//      serve(MetaModelDaoImpl.findById(modelId))
//    }
//  }

//  def getNodes(modelId: String) = Action.async { implicit request =>
//    authorize(OAuthDataHandler()) { authInfo =>
//      serve(MetaModelDaoImpl.getNodes(modelId))
//    }
//  }
//
//  def getEdges(modelId: String) = Action.async { implicit request =>
//    authorize(OAuthDataHandler()) { authInfo =>
//      serve(ModelData.getEdges(modelId))
//    }
//  }
//
//  def getNode(modelId: String, nodeId: String) = Action.async { implicit request =>
//    authorize(OAuthDataHandler()) { authInfo =>
//      serve(ModelData.getNode(modelId, nodeId))
//    }
//  }
//
//  def getEdge(modelId: String, edgeId: String) = Action.async { implicit request =>
//    authorize(OAuthDataHandler()) { authInfo =>
//      serve(ModelData.getEdge(modelId, edgeId))
//    }
//  }
//
//  private def serve(model: Future[Option[ModelData]]): Future[Result] = {
//    model.map {
//      case Some(m) => Ok(m.modelDataJson)
//      case _ => NotFound
//    }
//  }

}