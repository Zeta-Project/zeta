package de.htwg.zeta.server.controller.restApi

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.routes
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGeneratorResult
import de.htwg.zeta.server.util.auth.RepositoryFactory
import de.htwg.zeta.server.util.auth.ZetaEnv
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
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MCoreWrites.mObjectWrites
import models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result
import rx.lang.scala.Notification.OnError
import rx.lang.scala.Notification.OnNext

/**
 * RESTful API for metamodel definitions
 */
class MetaModelRestApi @Inject()(repositoryFactory: RepositoryFactory) extends Controller {

  private def repository[A](request: SecuredRequest[ZetaEnv, A]): Repository =
    repositoryFactory.fromSession(request)

  /** Lists all metamodels for the requesting user, provides HATEOAS links */
  def showForUser(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val p = Promise[Result]

    repository(request).query[MetaModelEntity](AllMetaModels())
      .map { mm =>
        new MetaModelShortInfo(id = mm.id(), name = mm.name, links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.getMetamodels(mm.id()).absoluteURL()(request)),
          HLink.delete("remove", routes.ScalaRoutes.getMetamodels(mm.id()).absoluteURL()(request))
        )))
      }
      .toList.materialize.subscribe(n => n match {
      case OnError(err) => p.success(BadRequest(err.getMessage))
      case OnNext(list) => p.success(Ok(Json.toJson(list)))
    })

    p.future
  }

  /** inserts whole metamodel structure (metamodel itself, dsls...) */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {

    val in = request.body.validate[MetaModel]

    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      entity => {
        repository(request).create[MetaModelEntity](MetaModelEntity(User.getUserId(request.identity.loginInfo), entity)).map { value =>
          Created(Json.toJson(value))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates whole metamodel structure (metamodel itself, dsls...) */
  def update(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[MetaModel]
    in.fold(errors => {
      Future.successful(BadRequest(JsError.toJson(errors)))
    },
      metaModel => {
        val op = for {
          saved <- repository(request).get[MetaModelEntity](id)
          updated <- repository(request).update[MetaModelEntity](saved.copy(metaModel = metaModel))
        } yield {
          updated
        }

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** deletes whole metamodel incl. dsl definitions */
  def delete(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    repository(request).delete(id).map { _ =>
      Ok("")
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** returns whole metamodels incl. dsl definitions and HATEOAS links */
  def get(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val out = m.copy(links = Some(Seq(
        HLink.put("update", routes.ScalaRoutes.getMetamodels(m.id()).absoluteURL()(request)),
        HLink.delete("remove", routes.ScalaRoutes.getMetamodels(m.id()).absoluteURL()(request))
      )))
      Ok(Json.toJson(out))
    })
  }

  /** returns pure metamodel without dsl definitions */
  def getMetaModelDefinition(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      Ok(Json.toJson(m.metaModel))
    })
  }

  /** updates pure metamodel without dsl definitions */
  def updateMetaModelDefinition(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[MetaModel]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      metaModel => {
        val op = for {
          saved <- repository(request).get[MetaModelEntity](id)
          update <- repository(request).update[MetaModelEntity](saved.copy(metaModel = metaModel))
        } yield {
          update
        }

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** returns all MClasses of a specific metamodel as Json Array */
  def getMClasses(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classesDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MClass]))
      Ok(Json.toJson(classesDef.elements.values))
    })
  }

  /** returns all MReferences of a specific metamodel as Json Array */
  def getMReferences(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refsDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MReference]))
      Ok(Json.toJson(refsDef.elements.values))
    })
  }

  /** returns specific MClass of a specific metamodel as Json Object */
  def getMClass(id: String, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
      classDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific metamodel as Json Object */
  def getMReference(id: String, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
      refDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns style definition */
  def getStyle(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.style.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns shape definition */
  def getShape(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.shape.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns diagram definition */
  def getDiagram(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.diagram.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: String, request: SecuredRequest[ZetaEnv, A], trans: MetaModelEntity => Result): Future[Result] = {
    repository(request).get[MetaModelEntity](id).map { mm =>
      trans(mm)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** updates shape definition */
  def updateShape(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[Shape]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      shape => {
        val op = for {
          saved <- repository(request).get[MetaModelEntity](id)
          update <- repository(request).update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(shape = Some(shape))))
        } yield {
          update
        }

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates style definition */
  def updateStyle(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[Style]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      style => {
        val op = for {
          saved <- repository(request).get[MetaModelEntity](id)
          update <- repository(request).update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(style = Some(style))))
        } yield {
          update
        }

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates diagram definition */
  def updateDiagram(id: String)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[Diagram]
    in.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      diagram => {
        val op = for {
          saved <- repository(request).get[MetaModelEntity](id)
          update <- repository(request).update[MetaModelEntity](saved.copy(dsl = saved.dsl.copy(diagram = Some(diagram))))
        } yield {
          update
        }

        op.map { value =>
          Ok(Json.toJson(value.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  def getValidator(id: String, regenerateOpt: Option[Boolean], noContentOpt: Option[Boolean])(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (metaModelEntity: MetaModelEntity) => {
      val validatorGenerator = new ValidatorGenerator(metaModelEntity)
      val regenerate = regenerateOpt.getOrElse(false)
      val noContent = noContentOpt.getOrElse(false)

      validatorGenerator.getGenerator(regenerate) match {
        case ValidatorGeneratorResult(false, msg, _) => BadRequest(msg)
        case ValidatorGeneratorResult(_, validator, true) => if (noContent) NoContent else Created(validator)
        case ValidatorGeneratorResult(_, validator, false) => if (noContent) NoContent else Ok(validator)
      }

    })
  }

  def deleteValidator(id: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    Future.successful {
      if (ValidatorGenerator.deleteValidator(id)) NoContent else NotFound
    }
  }

}
