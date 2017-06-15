package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.util.auth.ZetaEnv
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

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
      Ok(Json.toJson(list))
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
                  id = UUID.randomUUID(),
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
  // FIXME duplicate method
  def updateModel(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    update(id)(request)
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
      val nodes = m.model.nodes.values
      Results.Ok(Json.toJson(nodes))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val node = m.model.nodes.get(name)
      node.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edges = m.model.edges.values
      Results.Ok(Json.toJson(edges))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edge = m.model.edges.get(name)
      edge.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
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

  /**
   * Validates a model against its meta model and returns the validation results.
   *
   * The following HTP status codes can be returned:
   * * 200 OK - The model could be validated and the results are contained in the response.
   * * 400 BAD_REQUEST - The model could not be found or the user does not have the permissions for this model.
   * * 409 CONFLICT - No validator was found for the model.
   * * 500 INTERNAL_SERVER_ERROR - The validator exists but could not be loaded.
   *
   * @param id      ID of the model to validate.
   * @param request The HTTP request.
   * @return Results of the validation.
   */
  def getValidation(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (modelEntity: ModelEntity) => {

      val metaModelEntity = Await.result( // FIXME don't use Await
        restrictedAccessRepository(request.identity.id).metaModelEntity.read(modelEntity.metaModelId),
        Duration(1, TimeUnit.SECONDS)
      )

      metaModelEntity.validator match {
        case Some(validatorText) => ValidatorGenerator.create(validatorText) match {
          case Some(validator) => Ok(Json.toJson(validator.validate(modelEntity.model).filterNot(_.valid)))
          case None => InternalServerError("Error loading model validator.")
        }
        case None =>
          val url = routes.ScalaRoutes.getMetamodelsValidator(id, Some(true), None).absoluteURL()(request)
          Conflict(
            s"""No validator generated yet. Try calling $url first.""")
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
