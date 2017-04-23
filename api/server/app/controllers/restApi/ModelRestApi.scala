package controllers.restApi

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest

import models.User
import models.document.AllModels
import models.document.MetaModelEntity
import models.document.ModelEntity
import models.document.Repository
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ModelWrites.mObjectWrites
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.Model

import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.api.mvc.Result
import play.api.mvc.Results

import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext
import utils.auth.DefaultEnv
import utils.auth.RepositoryFactory

import scala.concurrent.Future
import scala.concurrent.Promise

import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for model definitions
 */
class ModelRestApi @Inject() (implicit repositoryFactory: RepositoryFactory, silhouette: Silhouette[DefaultEnv]) extends Controller {

  def repository[A]()(implicit request: SecuredRequest[DefaultEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser = silhouette.SecuredAction.async { implicit request =>
    val p = Promise[Result]

    repository.query[ModelEntity](AllModels())
      .map { info =>
        info.copy(links = Some(Seq(
          HLink.get("self", routes.ModelRestApi.get(info.id).absoluteURL),
          HLink.get("meta_model", routes.MetaModelRestApi.get(info.metaModelId).absoluteURL),
          HLink.delete("remove", routes.ModelRestApi.get(info.id).absoluteURL)
        )))
      }
      .toList.materialize.subscribe(n => n match {
        case OnError(err) => p.success(Results.BadRequest(err.getMessage))
        case OnNext(list) => p.success(Results.Ok(Json.toJson(list)))
      })
    p.future
  }

  /** inserts whole model structure */
  def insert = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    (request.body \ "metaModelId").validate[String].fold(
      error => Future.successful(Results.BadRequest(JsError.toJson(error))),
      metaModelId => {
        val in = (request.body \ "model").validate[Model]
        in.fold(
          errors => {
            Future.successful(Results.BadRequest(JsError.toJson(errors)))
          },
          model => {
            val op = for {
              mm <- repository.get[MetaModelEntity](metaModelId)
              insert <- repository.create[ModelEntity](ModelEntity(User.getUserId(request.identity.loginInfo), model.copy(metaModel = mm.metaModel), mm))
            } yield insert

            op.map { value =>
              Results.Ok(Json.toJson(value))
            }.recover {
              case e: Exception => Results.BadRequest(e.getMessage)
            }
          }
        )
      }
    )
  }

  /** updates whole model structure */
  def update(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[Model]
    in.fold(
      errors => {
        Future.successful(Results.BadRequest(JsError.toJson(errors)))
      },
      model => {
        val op = for {
          saved <- repository.get[ModelEntity](id)
          updated <- repository.update[ModelEntity](saved.copy(model = model))
        } yield updated

        op.map { value =>
          Results.Ok(Json.toJson(value.model))
        }.recover {
          case e: Exception => Results.BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates model definition only */
  def updateModel(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val p = Promise[Result]
    repository.get[ModelEntity](id).map { saved =>
      Model.readAndMergeWithMetaModel(request.body, saved.model.metaModel) match {
        case JsSuccess(model, path) => {
          repository.update[ModelEntity](saved.copy(model = model)).map { updated =>
            p.success(Results.Ok(Json.toJson(updated)))
          }.recover {
            case e: Exception => p.success(Results.BadRequest(e.getMessage))
          }
        }
        case JsError(errors) => p.success(Results.BadRequest(s"Failed parsing of MetaModel in Model on GET ${id}"))
      }
    }.recover {
      case e: Exception => p.success(Results.BadRequest(e.getMessage))
    }
    p.future
  }

  /** returns whole model structure incl. HATEOS links */
  def get(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => {
      val out = m.copy(links = Some(Seq(
        HLink.put("update", routes.ModelRestApi.get(m.id).absoluteURL),
        HLink.get("meta_model", routes.MetaModelRestApi.get(m.metaModelId).absoluteURL),
        HLink.delete("remove", routes.ModelRestApi.get(m.id).absoluteURL)
      )))
      Results.Ok(Json.toJson(out))
    })
  }

  /** returns model definition only */
  def getModelDefinition(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => Results.Ok(Json.toJson(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Node]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: String, name: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Node]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Edge]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: String, name: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Edge]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** deletes a whole model */
  def delete(id: String) = silhouette.SecuredAction.async { implicit request =>
    repository.delete(id).map { value =>
      Results.Ok("")
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }

  /** A helper method for less verbose reads from the database */
  def protectedRead[A](id: String, trans: ModelEntity => Result)(implicit request: SecuredRequest[DefaultEnv, A]): Future[Result] = {
    repository.get[ModelEntity](id).map { model =>
      trans(model)
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }
}
