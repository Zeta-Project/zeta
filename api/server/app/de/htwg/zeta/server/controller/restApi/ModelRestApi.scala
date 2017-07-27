package de.htwg.zeta.server.controller.restApi

import java.util.UUID

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.controller.restApi.modelUiFormat.EdgeFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelEntityFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.ModelUiFormat
import de.htwg.zeta.server.controller.restApi.modelUiFormat.NodeFormat
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

import play.api.libs.json.JsResult
import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
 * REST-ful API for model definitions
 */
class ModelRestApi() extends Controller with Logging {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id).modelEntity
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read))
    }.map((list: List[ModelEntity]) =>
      Ok(JsArray(list.map(ModelEntityFormat.writes)))
    ).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** inserts whole model structure */
  def insert()(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(Model.playJsonReadsEmpty).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      model => restrictedAccessRepository(request.identity.id).modelEntity.create(
        ModelEntity(
          id = UUID.randomUUID(),
          model = model
        )
      ).map { modelEntity =>
        Ok(ModelEntityFormat.writes(modelEntity))
      }).recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }


  /** updates whole model structure */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id)
    (request.body \ "metaModelId").validate[UUID].fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      metaModelId => repo.metaModelEntity.read(metaModelId).flatMap { metaModelEntity =>
        request.body.validate(Model.playJsonReads(metaModelEntity)).fold(
          faulty => {
            faulty.foreach(error(_))
            Future.successful(BadRequest(JsError.toJson(faulty)))
          },
          model => {
            repo.modelEntity.update(id, _.copy(model = model)).map { updated =>
              Ok(Json.toJson(updated))
            }.recover {
              case e: Exception => Results.BadRequest(e.getMessage)
            }
          }
        )
      }
    )
  }

  /*
 ModelUiFormat.futureReads(request.identity.id, request.body).flatMap(jsRes => {
   jsRes.fold(
     faulty => {
       faulty.foreach(error(_))
       Future.successful(BadRequest(JsError.toJson(faulty)))
     },
     model => {
       restrictedAccessRepository(request.identity.id).modelEntity.update(id, _.copy(model = model)).map {
         updated =>
           Ok(ModelEntityFormat.writes(updated))
       }.recover {
         case e: Exception => Results.BadRequest(e.getMessage)
       }
     }
   )
 }) */

  /** updates model definition only */
  // FIXME duplicate method
  def updateModel(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    update(id)(request)
  }

  /** returns whole model structure incl. HATEOS links */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, modelEntity =>
      Ok(ModelEntityFormat.writes(modelEntity))
    )
  }

  /** returns model definition only */
  def getModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => Ok(ModelFormat.writes(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val nodes = m.model.nodeMap.values
      Ok(JsArray(nodes.map(NodeFormat.writes).toList))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(modelId: UUID, nodeId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: ModelEntity) => {
      m.model.nodeMap.get(nodeId) match {
        case Some(node: Node) => Ok(NodeFormat.writes(node))
        case None => NotFound
      }
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edges = m.model.edgeMap.values
      Ok(JsArray(edges.map(EdgeFormat.writes).toList))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(modelId: UUID, edgeId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: ModelEntity) => {
      m.model.edgeMap.get(edgeId) match {
        case Some(edge) => Ok(EdgeFormat.writes(edge))
        case None => NotFound
      }
    })
  }

  /** deletes a whole model */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.delete(id).map(_ => Ok("")).recover {
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
    protectedReadFuture(id, request, (modelEntity: ModelEntity) => {

      restrictedAccessRepository(request.identity.id).metaModelEntity.read(modelEntity.model.metaModelId).map(metaModelEntity => {
        metaModelEntity.validator match {
          case Some(validatorText) => ValidatorGenerator.create(validatorText) match {
            case Some(validator) =>
              val results: Seq[ModelValidationResult] = validator.validate(modelEntity.model).filterNot(_.valid)
              Ok(JsArray(results.map(ModelValidationResult.modelValidationResultWrites.writes)))
            case None => InternalServerError("Error loading model validator.")
          }
          case None =>
            val url = routes.ScalaRoutes.getMetamodelsValidator(id, Some(true)).absoluteURL()(request)
            Conflict(
              s"""No validator generated yet. Try calling $url first.""")
        }
      })

    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedReadFuture[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: ModelEntity => Future[Result]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).modelEntity.read(id).flatMap(model => {
      trans(model)
    }).recover {
      case e: Exception =>
        info("exception while trying to read from DB", e)
        Results.BadRequest(e.getMessage)
    }
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: ModelEntity => Result): Future[Result] = {
    protectedReadFuture[A](id, request, me => Future(trans(me)))
  }


  def getScalaCodeViewer(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedAccessRepository(request.identity.id)
    for {
      modelEntity <- repo.modelEntity.read(modelId)
      metaModelEntity <- repo.metaModelEntity.read(modelEntity.model.metaModelId)
    } yield {
      val files = experimental.Generator.generate(metaModelEntity.metaModel, modelEntity).toList
      Ok(views.html.codeViewer.ScalaCodeViewer(files))
    }
  }

}
