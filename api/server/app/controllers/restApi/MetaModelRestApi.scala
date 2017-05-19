package controllers.restApi

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import models.User
import models.document.AllMetaModels
import models.document.MetaModelEntity
import models.document.Repository
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Diagram
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.MetaModelShortInfo
import models.modelDefinitions.metaModel.Shape
import models.modelDefinitions.metaModel.Style
import models.modelDefinitions.metaModel.elements.MCoreWrites.mObjectWrites
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.JsError
import play.api.libs.json.Json
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.api.mvc.Result
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext
import de.htwg.zeta.server.utils.auth.ZetaEnv
import de.htwg.zeta.server.utils.auth.RepositoryFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import controllers.routes

/**
 * RESTful API for metamodel definitions
 */
class MetaModelRestApi @Inject() (implicit repositoryFactory: RepositoryFactory, silhouette: Silhouette[ZetaEnv]) extends Controller {

  def repository[A]()(implicit request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  /** Lists all metamodels for the requesting user, provides HATEOAS links */
  def showForUser = silhouette.SecuredAction.async { implicit request =>
    val p = Promise[Result]

    repository.query[MetaModelEntity](AllMetaModels())
      .map { mm =>
        new MetaModelShortInfo(id = mm.id, name = mm.name, links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.MMRAget(mm.id).absoluteURL),
          HLink.delete("remove", routes.ScalaRoutes.MMRAget(mm.id).absoluteURL)
        )))
      }
      .toList.materialize.subscribe(n => n match {
        case OnError(err) => p.success(BadRequest(err.getMessage))
        case OnNext(list) => p.success(Ok(Json.toJson(list)))
      })

    p.future
  }

  /** inserts whole metamodel structure (metamodel itself, dsls...) */
  def insert = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[MetaModel]

    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      entity => {
        repository.create[MetaModelEntity](MetaModelEntity(User.getUserId(request.identity.loginInfo), entity)).map { value =>
          Created(Json.toJson(value))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates whole metamodel structure (metamodel itself, dsls...) */
  def update(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[MetaModel]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      metaModel => {
        val op = for {
          saved <- repository.get[MetaModelEntity](id)
          updated <- repository.update[MetaModelEntity](saved.copy(metaModel = metaModel))
        } yield updated

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** deletes whole metamodel incl. dsl definitions */
  def delete(id: String) = silhouette.SecuredAction.async { implicit request =>
    repository.delete(id).map { value =>
      Ok("")
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** returns whole metamodels incl. dsl definitions and HATEOAS links */
  def get(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      val out = m.copy(links = Some(Seq(
        HLink.put("update", routes.ScalaRoutes.MMRAget(m.id).absoluteURL),
        HLink.delete("remove", routes.ScalaRoutes.MMRAget(m.id).absoluteURL)
      )))
      Ok(Json.toJson(out))
    })
  }

  /** returns pure metamodel without dsl definitions */
  def getMetaModelDefinition(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      Ok(Json.toJson(m.metaModel))
    })
  }

  /** updates pure metamodel without dsl definitions */
  def updateMetaModelDefinition(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[MetaModel]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      metaModel => {
        val op = for {
          saved <- repository.get[MetaModelEntity](id)
          update <- repository.update[MetaModelEntity](saved.copy(metaModel = metaModel))
        } yield update

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** returns all MClasses of a specific metamodel as Json Array */
  def getMClasses(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classesDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MClass]))
      Ok(Json.toJson(classesDef.elements.values))
    })
  }

  /** returns all MReferences of a specific metamodel as Json Array */
  def getMReferences(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refsDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MReference]))
      Ok(Json.toJson(refsDef.elements.values))
    })
  }

  /** returns specific MClass of a specific metamodel as Json Object */
  def getMClass(id: String, name: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
      classDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific metamodel as Json Object */
  def getMReference(id: String, name: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
      refDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns style definition */
  def getStyle(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      m.dsl.style.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns shape definition */
  def getShape(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      m.dsl.shape.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns diagram definition */
  def getDiagram(id: String) = silhouette.SecuredAction.async { implicit request =>
    protectedRead(id, (m: MetaModelEntity) => {
      m.dsl.diagram.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** A helper method for less verbose reads from the database */
  def protectedRead[A](id: String, trans: MetaModelEntity => Result)(implicit request: SecuredRequest[ZetaEnv, A]): Future[Result] = {
    repository.get[MetaModelEntity](id).map { mm =>
      trans(mm)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** updates shape definition */
  def updateShape(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[Shape]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      shape => {
        val op = for {
          saved <- repository.get[MetaModelEntity](id)
          update <- repository.update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(shape = Some(shape))))
        } yield update

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates style definition */
  def updateStyle(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[Style]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      style => {
        val op = for {
          saved <- repository.get[MetaModelEntity](id)
          update <- repository.update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(style = Some(style))))
        } yield update

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates diagram definition */
  def updateDiagram(id: String) = silhouette.SecuredAction.async(BodyParsers.parse.json) { implicit request =>
    val in = request.body.validate[Diagram]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      diagram => {
        val op = for {
          saved <- repository.get[MetaModelEntity](id)
          update <- repository.update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(diagram = Some(diagram))))
        } yield update

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }
}
