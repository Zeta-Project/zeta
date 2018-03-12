package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.common.format.model.EdgeFormat
import de.htwg.zeta.common.format.model.GraphicalDslInstanceFormat
import de.htwg.zeta.common.format.model.NodeFormat
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslInstanceRepository
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedGraphicalDslRepository
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Writes
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results

/**
 * REST-ful API for model definitions
 */
class ModelRestApi @Inject()(
    modelEntityRepo: AccessRestrictedGraphicalDslInstanceRepository,
    metaModelEntityRepo: AccessRestrictedGraphicalDslRepository,
    graphicalDslInstanceFormat: GraphicalDslInstanceFormat,
    nodeFormat: NodeFormat,
    edgeFormat: EdgeFormat
) extends Controller with Logging {

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = modelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap { ids =>
      Future.sequence(ids.toList.map(repo.read))
    }.map(list =>
      Ok(Writes.list(graphicalDslInstanceFormat).writes(list))
    ).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** inserts whole model structure */
  def insert()(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(graphicalDslInstanceFormat.empty).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      model => modelEntityRepo.restrictedTo(request.identity.id).create(model).map { graphicalDslInstance =>
        Ok(graphicalDslInstanceFormat.writes(graphicalDslInstance))
      }).recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }


  /** updates whole model structure */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    info("updating GraphicalDslInstance: " + request.body.toString)
    (request.body \ "graphicalDslId").validate[UUID].fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      metaModelId => metaModelEntityRepo.restrictedTo(request.identity.id).read(metaModelId).flatMap { _ =>
        request.body.validate(graphicalDslInstanceFormat.withId(id)).fold(
          faulty => {
            faulty.foreach(error(_))
            Future.successful(BadRequest(JsError.toJson(faulty)))
          },
          graphicalDslInstance => {
            modelEntityRepo.restrictedTo(request.identity.id).update(id, _ => graphicalDslInstance).map { updated =>
              Ok(graphicalDslInstanceFormat.writes(updated))
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

  /** returns whole model structure */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, modelEntity =>
      Ok(graphicalDslInstanceFormat.writes(modelEntity))
    )
  }

  // FIXME duplicate method (same as get)
  /** returns model definition only */
  def getModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: GraphicalDslInstance) => Ok(graphicalDslInstanceFormat.writes(m)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: GraphicalDslInstance) => {
      Ok(Writes.seq(nodeFormat).writes(m.nodes))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(modelId: UUID, nodeName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: GraphicalDslInstance) => {
      m.nodeMap.get(nodeName) match {
        case Some(node: NodeInstance) => Ok(nodeFormat.writes(node))
        case None => NotFound
      }
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: GraphicalDslInstance) => {
      Ok(Writes.seq(edgeFormat).writes(m.edges))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(modelId: UUID, edgeName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(modelId, request, (m: GraphicalDslInstance) => {
      m.edgeMap.get(edgeName) match {
        case Some(edge) => Ok(edgeFormat.writes(edge))
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
    protectedReadFuture(id, request, (modelEntity: GraphicalDslInstance) => {

      metaModelEntityRepo.restrictedTo(request.identity.id).read(modelEntity.graphicalDslId).map(metaModelEntity => {
        metaModelEntity.validator match {
          case Some(validatorText) => ValidatorGenerator.create(validatorText) match {
            case Some(validator) =>
              val results: Seq[ModelValidationResult] = validator.validate(modelEntity).filterNot(_.valid)
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
  private def protectedReadFuture[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: GraphicalDslInstance => Future[Result]): Future[Result] = {
    modelEntityRepo.restrictedTo(request.identity.id).read(id).flatMap(model => {
      trans(model)
    }).recover {
      case e: Exception =>
        info("exception while trying to read from DB", e)
        Results.BadRequest(e.getMessage)
    }
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: GraphicalDslInstance => Result): Future[Result] = {
    protectedReadFuture[A](id, request, me => Future(trans(me)))
  }

  def getScalaCodeViewer(modelId: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    for {
      graphicalDslInstance <- modelEntityRepo.restrictedTo(request.identity.id).read(modelId)
      metaModelEntity <- metaModelEntityRepo.restrictedTo(request.identity.id).read(graphicalDslInstance.graphicalDslId)
    } yield {
      val files = experimental.ScalaCodeGenerator.generate(metaModelEntity.concept, graphicalDslInstance).toList
      Ok(views.html.codeViewer.ScalaCodeViewer(files))
    }
  }

}
