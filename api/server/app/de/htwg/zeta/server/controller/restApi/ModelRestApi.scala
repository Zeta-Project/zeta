package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.Promise

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.document.Document.modelFormat
import models.document.ModelEntity
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.Model
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ModelWrites.mObjectWrites
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import play.api.mvc.AnyContent
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for model definitions
 */
class ModelRestApi @Inject()() extends Controller {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntities
    repo.readAllIds.flatMap { ids =>
      Future.sequence(ids.map(repo.read)).map(_.map(info =>
        info.copy(links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request)),
          HLink.get("meta_model", routes.ScalaRoutes.getMetamodels(info.metaModelId).absoluteURL()(request)),
          HLink.delete("remove", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request))
        )))
      ))
    }.map(list => Ok(Json.toJson(list))).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** inserts whole model structure */
  def insert()(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    (request.body \ "metaModelId").validate[UUID].fold(
      error => Future.successful(Results.BadRequest(JsError.toJson(error))),
      metaModelId => {
        (request.body \ "model").validate[Model].fold(
          errors => Future.successful(Results.BadRequest(JsError.toJson(errors))),
          model => {
            val repo = restrictedAccessRepository(request.identity.id)
            repo.metaModelEntities.read(metaModelId).flatMap(metaModel => {
              repo.modelEntities.create(
                ModelEntity(
                  model = model.copy(metaModel = metaModel.metaModel),
                  metaModelId = metaModel.id
                )
              ).map { modelEntity =>
                Results.Ok(Json.toJson(modelEntity))
              }
            }).recover {
              case e: Exception => Results.BadRequest(e.getMessage)
            }
          }
        )
      }
    )
  }

  /** updates whole model structure */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[Model]
    in.fold(
      errors => Future.successful(Results.BadRequest(JsError.toJson(errors))),
      model => {
        restrictedAccessRepository(request.identity.id).modelEntities.update(id, _.copy(model = model)).map { updated =>
          Results.Ok(Json.toJson(updated))
        }.recover {
          case e: Exception => Results.BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates model definition only */
  def updateModel(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntities

    val p = Promise[Result]
    repo.read(id).map { saved =>
      Model.readAndMergeWithMetaModel(request.body, saved.model.metaModel) match {
        case JsSuccess(model, path) => {
          repo.update(id, _.copy(model = model)).map { updated =>
            p.success(Results.Ok(Json.toJson(updated)))
          }.recover {
            case e: Exception => p.success(Results.BadRequest(e.getMessage))
          }
        }
        case JsError(_) => p.success(Results.BadRequest(s"Failed parsing of MetaModel in Model on GET ${id}"))
      }
    }.recover {
      case e: Exception => p.success(Results.BadRequest(e.getMessage))
    }
    p.future
  }

  /** returns whole model structure incl. HATEOS links */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val out = m.copy(links = Some(Seq(
        HLink.put("update", routes.ScalaRoutes.getModels(m.id).absoluteURL()(request)),
        HLink.get("meta_model", routes.ScalaRoutes.getMetamodels(m.metaModelId).absoluteURL()(request)),
        HLink.delete("remove", routes.ScalaRoutes.getModels(m.id).absoluteURL()(request))
      )))
      Results.Ok(Json.toJson(out))
    })
  }

  /** returns model definition only */
  def getModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => Results.Ok(Json.toJson(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Node]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Node]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Edge]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Edge]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** deletes a whole model */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntities.delete(id).map { _ =>
      Ok("")
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: ModelEntity => Result): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntities.read(id).map { model =>
      trans(model)
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }
}
