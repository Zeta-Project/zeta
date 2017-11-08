package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.entity.ModelEntity
import de.htwg.zeta.common.models.modelDefinitions.model.Model
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedEntityPersistence
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * REST-ful API for model definitions
 */
class ModelRestApi @Inject()(
    modelEntityRepo: AccessRestrictedEntityPersistence[ModelEntity],
    metaModelEntityRepo: AccessRestrictedEntityPersistence[MetaModelEntity]
) extends Controller with Logging {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = modelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read))
    }.map(list =>
      Ok(Json.toJson(list))
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
      model => modelEntityRepo.restrictedTo(request.identity.id).create(
        ModelEntity(
          id = UUID.randomUUID(),
          model = model
        )
      ).map { modelEntity =>
        Ok(Json.toJson(modelEntity))
      }).recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }


  /** updates whole model structure */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    (request.body \ "metaModelId").validate[UUID].fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      metaModelId => metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).flatMap { metaModelEntity =>
        request.body.validate(Model.playJsonReads(metaModelEntity)).fold(
          faulty => {
            faulty.foreach(error(_))
            Future.successful(BadRequest(JsError.toJson(faulty)))
          },
          model => {
            modelEntityRepo.restrictedTo(request.identity.id).update(id, _.copy(model = model)).map { updated =>
              Ok(Json.toJson(updated))
            }.recover {
              case e: Exception => Results.BadRequest(e.getMessage)
            }
          }
        )
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
    protectedRead(id, request, modelEntity =>
      Ok(Json.toJson(modelEntity))
    )
  }

  /** returns model definition only */
  def getModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => Ok(Json.toJson(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val nodes = m.model.nodeMap.values
      Ok(Json.toJson(nodes))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(modelId: UUID, nodeName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: ModelEntity) => {
      m.model.nodeMap.get(nodeName) match {
        case Some(node: Node) => Ok(Json.toJson(node))
        case None => NotFound
      }
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val edges = m.model.edgeMap.values
      Ok(Json.toJson(edges))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(modelId: UUID, edgeName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: ModelEntity) => {
      m.model.edgeMap.get(edgeName) match {
        case Some(edge) => Ok(Json.toJson(edge))
        case None => NotFound
      }
    })
  }

  /** deletes a whole model */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    modelEntityRepo.restrictedTo(request.identity.id).delete(id).map(_ => Ok("")).recover {
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

      metaModelEntityRepo.restrictedTo(request.identity.id).read(modelEntity.model.metaModelId).map(metaModelEntity => {
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
    modelEntityRepo.restrictedTo(request.identity.id).read(id).flatMap(model => {
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
    for {
      modelEntity <- modelEntityRepo.restrictedTo(request.identity.id).read(modelId)
      metaModelEntity <- metaModelEntityRepo.restrictedTo(request.identity.id).read(modelEntity.model.metaModelId)
    } yield {
      val files = experimental.ScalaCodeGenerator.generate(metaModelEntity.metaModel, modelEntity).toList
      Ok(views.html.codeViewer.ScalaCodeViewer(files))
    }
  }

}
