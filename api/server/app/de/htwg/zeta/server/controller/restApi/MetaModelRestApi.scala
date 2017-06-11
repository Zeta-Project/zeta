package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.softwaremill.quicklens.ModifyPimp
import controllers.routes
import de.htwg.zeta.persistence.Persistence.restrictedAccessRepository
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGeneratorResult
import de.htwg.zeta.server.util.auth.ZetaEnv
import models.entity.MetaModelEntity
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Diagram
import models.modelDefinitions.metaModel.MetaModelShortInfo
import models.modelDefinitions.metaModel.Shape
import models.modelDefinitions.metaModel.Style
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Json
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
    val repo = restrictedAccessRepository(request.identity.id).metaModelEntities
    repo.readAllIds().flatMap(ids => {
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
    null /* TODO
    request.body.validate[MetaModel].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      entity => {
        restrictedAccessRepository(request.identity.id).metaModelEntities.create(
          MetaModelEntity(
            name = entity.name,
            rev = "1",
            metaModel = entity
          )
        ).map { metaModelEntity =>
          Created(Json.toJson(metaModelEntity))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    ) */
  }

  /** Updates whole metamodel structure (metamodel itself, dsls...)
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    null /* TODO
    val in = request.body.validate[MetaModel]
    in.fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      metaModel => {
        val repo = restrictedAccessRepository(request.identity.id).metaModelEntities
        repo.update(id, _.copy(metaModel = metaModel)).map { _ =>
          Ok(Json.toJson(metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    ) */
  }

  /** Deletes whole metamodel incl. dsl definitions
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    restrictedAccessRepository(request.identity.id).metaModelEntities.delete(id).map { _ =>
      Ok("")
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
      Ok // TODO Ok(Json.toJson(out))
    })
  }

  /** returns pure metamodel without dsl definitions */
  def getMetaModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      Ok // TODO Ok(Json.toJson(m.metaModel))
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
      val classes = m.metaModel.classes.values
      Ok // TODO Ok(Json.toJson(classes))
    })
  }

  /** returns all MReferences of a specific metamodel as Json Array */
  def getMReferences(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val references = m.metaModel.references.values
      Ok // TODO Ok(Json.toJson(references))
    })
  }

  /** returns specific MClass of a specific metamodel as Json Object */
  def getMClass(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val clazz = m.metaModel.classes.get(name)
      clazz.map(m =>  // TODO replace with fold
        Ok // TODO Ok(Json.toJson(m))
      ).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific metamodel as Json Object */
  def getMReference(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val reference = m.metaModel.references.get(name)
      reference.map(m => // TODO replace with fold
        Ok // TODO Ok(Json.toJson(m))
      ).getOrElse(NotFound)
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
    restrictedAccessRepository(request.identity.id).metaModelEntities.read(id).map { mm =>
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
        restrictedAccessRepository(request.identity.id).metaModelEntities.update(id, _.modify(_.dsl.shape).setTo(Some(shape))).map { metaModelEntity =>
          Ok // TODO Ok(Json.toJson(metaModelEntity.metaModel))
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
        restrictedAccessRepository(request.identity.id).metaModelEntities.update(id, _.modify(_.dsl.style).setTo(Some(style))).map { metaModelEntity =>
          Ok // TODO Ok(Json.toJson(metaModelEntity.metaModel))
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
        restrictedAccessRepository(request.identity.id).metaModelEntities.update(id, _.modify(_.dsl.diagram).setTo(Some(diagram))).map { metaModelEntity =>
          Ok // TODO Ok(Json.toJson(metaModelEntity.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  def getValidator(id: UUID, regenerateOpt: Option[Boolean], noContentOpt: Option[Boolean])(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
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

  def deleteValidator(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    Future.successful {
      if (ValidatorGenerator.deleteValidator(id)) NoContent else NotFound
    }
  }

}
