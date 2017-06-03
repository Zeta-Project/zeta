package de.htwg.zeta.server.controller.restApi

import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.Promise

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.server.util.auth.ZetaEnv
import de.htwg.zeta.server.util.auth.RepositoryFactory
import models.document.AllModels
import models.document.MetaModelEntity
import models.document.ModelEntity
import models.document.Repository
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
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext
import scalaoauth2.provider.OAuth2ProviderActionBuilders.executionContext

/**
 * RESTful API for model definitions
 */
class ModelRestApi @Inject()(repositoryFactory: RepositoryFactory) extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  /** Lists all models for the requesting user, provides HATEOAS links */
  def showForUser()(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val p = Promise[Result]

    repository(request).query[ModelEntity](AllModels())
      .map { info =>
        info.copy(links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request)),
          HLink.get("meta_model", routes.ScalaRoutes.getMetamodels(info.metaModelId).absoluteURL()(request)),
          HLink.delete("remove", routes.ScalaRoutes.getModels(info.id).absoluteURL()(request))
        )))
      }
      .toList.materialize.subscribe(_ match {
      case OnError(err) => p.success(Results.BadRequest(err.getMessage))
      case OnNext(list) => p.success(Results.Ok(Json.toJson(list)))
    })
    p.future
  }

  /** inserts whole model structure */
  def insert()(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {

    val repo =  repository(request)
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
              mm <- repo.get[MetaModelEntity](metaModelId)
              insert <- repo.create[ModelEntity](ModelEntity(request.identity.id, model.copy(metaModel = mm
                .metaModel), mm))
            } yield {
              insert
            }

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
  def update(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[Model]
    in.fold(
      errors => {
        Future.successful(Results.BadRequest(JsError.toJson(errors)))
      },
      model => {
        val op = for {
          saved <- repository(request).get[ModelEntity](id)
          updated <- repository(request).update[ModelEntity](saved.copy(model = model))
        } yield {
          updated
        }

        op.map { value =>
          Results.Ok(Json.toJson(value.model))
        }.recover {
          case e: Exception => Results.BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates model definition only */
  def updateModel(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val p = Promise[Result]
    repository(request).get[ModelEntity](id).map { saved =>
      Model.readAndMergeWithMetaModel(request.body, saved.model.metaModel) match {
        case JsSuccess(model, path) => {
          repository(request).update[ModelEntity](saved.copy(model = model)).map { updated =>
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
  def get(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
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
  def getModelDefinition(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => Results.Ok(Json.toJson(m.model)))
  }

  /** returns all nodes of a model as json array */
  def getNodes(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Node]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific node of a specific model as json object */
  def getNode(id: String, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Node]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** returns all edges of a model as json array */
  def getEdges(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[Edge]))
      Results.Ok(Json.toJson(reduced.elements.values))
    })
  }

  /** returns specific edge of a specific model as json object */
  def getEdge(id: String, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: ModelEntity) => {
      val d = m.model
      val reduced = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[Edge]))
      reduced.elements.values.headOption.map(m => Results.Ok(Json.toJson(m))).getOrElse(Results.NotFound)
    })
  }

  /** deletes a whole model */
  def delete(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).delete(id).map { _ =>
      Results.Ok("")
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: String, request: SecuredRequest[ZetaEnv, A], trans: ModelEntity => Result): Future[Result] = {
    repository(request).get[ModelEntity](id).map { model =>
      trans(model)
    }.recover {
      case e: Exception => Results.BadRequest(e.getMessage)
    }
  }
}
