package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.Promise
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.entity.ModelEntity
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.Model
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results

/**
 * RESTful API for model definitions
 */
class ModelRestApi @Inject()() extends Controller {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntity
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.map(repo.read)).map(_.map(info =>
        info.copy(links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request)),
          HLink.get("meta_model", routes.ScalaRoutes.getMetamodels(info.metaModelId).absoluteURL()(request)),
          HLink.delete("remove", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request))
        )))
      ))
    }.map(list =>
      Ok // TODO Ok(Json.toJson(list))
    ).recover {
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
            repo.metaModelEntity.read(metaModelId).flatMap(metaModel => {
              repo.modelEntity.create(
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
    request.body.validate[Model].fold(
      errors => Future.successful(Results.BadRequest(JsError.toJson(errors))),
      model => {
        restrictedAccessRepository(request.identity.id).modelEntity.update(id, _.copy(model = model)).map { updated =>
          Results.Ok(Json.toJson(updated))
        }.recover {
          case e: Exception => Results.BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates model definition only */
  def updateModel(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntity

    val p = Promise[Result]
    repo.read(id).map { saved =>
      // TODO readAndMergeWithMetaModel is deleted due to refactoring
      /* Model.readAndMergeWithMetaModel(request.body, saved.model.metaModel) match {
        case JsSuccess(model, path) =>
          repo.update(id, _.copy(model = model)).map { updated =>
            p.success(Results.Ok(Json.toJson(updated)))
          }.recover {
            case e: Exception => p.success(Results.BadRequest(e.getMessage))
          }
        case JsError(_) => p.success(Results.BadRequest(s"Failed parsing of MetaModel in Model on GET $id"))
      } */
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
      null // TODO Results.Ok(Json.toJson(out))
    })
  }

  /** returns model definition only */
  def getModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    null // TODO protectedRead(id, request, (m: ModelEntity) => Results.Ok(Json.toJson(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val nodes = m.model.nodes.values
      null // TODO Results.Ok(Json.toJson(nodes))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val node = m.model.nodes.get(name) // TODO ese fold
      null // TODO node.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edges = m.model.edges.values
      null // TODO Results.Ok(Json.toJson(edges))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edge = m.model.edges.get(name) // TODO use fold
      null // TODO edge.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** deletes a whole model */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.delete(id).map { _ =>
      Ok("")
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  def getValidation(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (modelEntity: ModelEntity) => {

      val metaModelId = modelEntity.metaModelId

      if (ValidatorGenerator.validatorExists(metaModelId)) {
        ValidatorGenerator.load(metaModelId) match {
          case Some(modelValidator) => Ok(modelValidator.validate(modelEntity.model).map(_.rule.description).mkString("\n"))
          case None => InternalServerError("Error loading model validator")
        }
      } else {
        Conflict(s"There is no validator for this meta model. Try calling GET /metamodels/$metaModelId/validator?noContent=true first.")
      }

    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: ModelEntity => Result): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.read(id).map { model =>
      trans(model)
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }
}
