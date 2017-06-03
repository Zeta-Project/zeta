package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.softwaremill.quicklens.modify
import controllers.routes
import de.htwg.zeta.persistence.Persistence.restrictedRepository
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.document.MetaModelEntity
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
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


/**
 * RESTful API for metamodel definitions
 */
class MetaModelRestApi @Inject()() extends Controller {

  /** Lists all metamodels for the requesting user, provides HATEOAS links.
   *
   * @param request The request
   * @return The result
   */
  def showForUser(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedRepository(request.identity).metaModelEntity
    repo.readAllIds.flatMap(ids => {
      Future.sequence(ids.map(repo.read)).map(_.map { mm =>
        new MetaModelShortInfo(id = mm.id, name = mm.name, links = Some(Seq(
          HLink.get("self", routes.ScalaRoutes.getMetamodels(mm.id).absoluteURL()(request)),
          HLink.delete("remove", routes.ScalaRoutes.getMetamodels(mm.id).absoluteURL()(request))
        )))
      })
    }).map(list => Ok(Json.toJson(list))).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** inserts whole metamodel structure (metamodel itself, dsls...)
   *
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate[MetaModel].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      entity => {
        val repo = restrictedRepository(request.identity)
        val repo = restrictedRepository(request.identity)
        val metaModelEntity = MetaModelEntity(request.identity.id, entity)
        repo.metaModelEntity.create(MetaModelEntity(request.identity.id, entity)).flatMap { _ =>
          repo.users.update(modify(request.identity)(_.accessAuthorisation.metaModelEntity).using(_ + metaModelEntity.id)).map { _ =>
            Created(Json.toJson(metaModelEntity))
          }
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** Updates whole metamodel structure (metamodel itself, dsls...)
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    val in = request.body.validate[MetaModel]
    in.fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      metaModel => {
        val repo = restrictedRepository(request.identity).metaModelEntity
        repo.read(id).flatMap { saved =>
          repo.update(saved.copy(metaModel = metaModel)).map { _ =>
            Ok(Json.toJson(metaModel))
          }
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** Deletes whole metamodel incl. dsl definitions
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = restrictedRepository(request.identity)
    repo.users.update(modify(request.identity)(_.accessAuthorisation.metaModelEntity).using(_ - id)).flatMap { _ =>
      repo.metaModelEntity.delete(id).map { _ =>
        Ok("")
      }
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** returns whole metamodels incl. dsl definitions and HATEOAS links */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val out = m.copy(links = Some(Seq(
        HLink.put("update", routes.ScalaRoutes.getMetamodels(m.id).absoluteURL()(request)),
        HLink.delete("remove", routes.ScalaRoutes.getMetamodels(m.id).absoluteURL()(request))
      )))
      Ok(Json.toJson(out))
    })
  }

  /** returns pure metamodel without dsl definitions */
  def getMetaModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      Ok(Json.toJson(m.metaModel))
    })
  }

  /** updates pure metamodel without dsl definitions */
  // FIXME Duplicate Function
  def updateMetaModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    update(id)(request)
  }

  /** returns all MClasses of a specific metamodel as Json Array */
  def getMClasses(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classesDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MClass]))
      Ok(Json.toJson(classesDef.elements.values))
    })
  }

  /** returns all MReferences of a specific metamodel as Json Array */
  def getMReferences(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refsDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MReference]))
      Ok(Json.toJson(refsDef.elements.values))
    })
  }

  /** returns specific MClass of a specific metamodel as Json Object */
  def getMClass(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val classDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
      classDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific metamodel as Json Object */
  def getMReference(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val d = m.metaModel
      val refDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
      refDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns style definition */
  def getStyle(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.style.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns shape definition */
  def getShape(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.shape.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** returns diagram definition */
  def getDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.diagram.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: MetaModelEntity => Result): Future[Result] = {
    restrictedRepository(request.identity).metaModelEntity.read(id).map { mm =>
      trans(mm)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** updates shape definition */
  def updateShape(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate[Shape].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      shape => {
        val repo = restrictedRepository(request.identity).metaModelEntity
        repo.read(id).flatMap { saved =>
          val updated = modify(saved)(_.dsl.shape).setTo(Some(shape))
          repo.update(updated).map { _ =>
            Ok(Json.toJson(updated.metaModel))
          }
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates style definition */
  def updateStyle(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate[Style].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      style => {
        val repo = restrictedRepository(request.identity).metaModelEntity
        repo.read(id).flatMap { saved =>
          val updated = modify(saved)(_.dsl.style).setTo(Some(style))
          repo.update(updated).map { _ =>
            Ok(Json.toJson(updated.metaModel))
          }
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates diagram definition */
  def updateDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate[Diagram].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      diagram => {
        val repo = restrictedRepository(request.identity).metaModelEntity
        repo.read(id).flatMap { saved =>
          val updated = modify(saved)(_.dsl.diagram).setTo(Some(diagram))
          repo.update(updated).map { _ =>
            Ok(Json.toJson(updated.metaModel))
          }
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

}
