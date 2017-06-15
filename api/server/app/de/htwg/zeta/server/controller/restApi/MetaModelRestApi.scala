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
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.helper.HLink
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModelShortInfo
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
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
    val repo = restrictedAccessRepository(request.identity.id).metaModelEntity
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
    request.body.validate[MetaModel].fold(
      faulty => Future.successful(BadRequest(JsError.toJson(faulty))),
      entity => {
        restrictedAccessRepository(request.identity.id).metaModelEntity.create(
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
        val repo = restrictedAccessRepository(request.identity.id).metaModelEntity
        repo.update(id, _.copy(metaModel = metaModel)).map { _ =>
          Ok(Json.toJson(metaModel))
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
    restrictedAccessRepository(request.identity.id).metaModelEntity.delete(id).map { _ =>
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
      val classes = m.metaModel.classes.values
      Ok(Json.toJson(classes))
    })
  }

  /** returns all MReferences of a specific metamodel as Json Array */
  def getMReferences(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      val references = m.metaModel.references.values
      Ok(Json.toJson(references))
    })
  }

  /** returns specific MClass of a specific metamodel as Json Object */
  def getMClass(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.metaModel.classes.get(name).map(clazz =>
        Ok(Json.toJson(clazz))
      ).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific metamodel as Json Object */
  def getMReference(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.metaModel.references.get(name).map(reference =>
        Ok(Json.toJson(reference))
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
    restrictedAccessRepository(request.identity.id).metaModelEntity.read(id).map { mm =>
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
        restrictedAccessRepository(request.identity.id).metaModelEntity.update(id, _.modify(_.dsl.shape).setTo(Some(shape))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
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
        restrictedAccessRepository(request.identity.id).metaModelEntity.update(id, _.modify(_.dsl.style).setTo(Some(style))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
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
        restrictedAccessRepository(request.identity.id).metaModelEntity.update(id, _.modify(_.dsl.diagram).setTo(Some(diagram))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /**
   * Loads or generates the validator for a given meta model.
   *
   * The following HTTP status codes can be returned:
   * * 200 OK - The validator was loaded from memory and is contained in the response.
   * * 201 CREATED - The validator has been generated or regenerated and is contained in the response.
   * * 204 NO_CONTENT - The validator has been successfully loaded or generated and is NOT contained in the response.
   * * 400 BAD_REQUEST - There was an error loading or generating the validator OR you have no access to the meta model.
   * * 409 CONFLICT - A validator was not yet generated.
   *
   * @param id           ID of the meta model to load or generate the validator.
   * @param generateOpt  Force a (re)generation.
   * @param noContentOpt Expect a NoContent result on success. Helpful, when you just want to generate the validator.
   * @param request      The HTTP-Request.
   * @return The validator.
   */
  def getValidator(id: UUID, generateOpt: Option[Boolean], noContentOpt: Option[Boolean])(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (metaModelEntity: MetaModelEntity) => {

      val generate = generateOpt.getOrElse(false)
      val noContent = noContentOpt.getOrElse(false)

      if (generate) {

        new ValidatorGenerator(metaModelEntity).generateValidator() match {
          case ValidatorGeneratorResult(false, msg) => BadRequest(msg)
          case ValidatorGeneratorResult(_, validator) =>
            restrictedAccessRepository(request.identity.id).metaModelEntity.update(id, _.copy(validator = Some(validator)))
            if (noContent) NoContent else Created(validator)
        }

      } else {

        metaModelEntity.validator match {
          case Some(validatorText) => Ok(validatorText)
          case None =>
            val url = routes.ScalaRoutes.getMetamodelsValidator(id, Some(true), None).absoluteURL()(request)
            Conflict(
              s"""No validator generated yet. Try calling $url first.""")
        }

      }

    })
  }

}
