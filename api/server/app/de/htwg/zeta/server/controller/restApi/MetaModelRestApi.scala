package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.softwaremill.quicklens.ModifyPimp
import controllers.routes
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Diagram
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModelShortInfo
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Shape
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.persistence.accessRestricted.AccessRestrictedMetaModelEntityRepository
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGenerator
import de.htwg.zeta.server.model.modelValidator.generator.ValidatorGeneratorResult
import de.htwg.zeta.server.util.auth.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result

/**
 * REST-ful API for MetaModel definitions
 */
class MetaModelRestApi @Inject()(
    metaModelEntityRepo: AccessRestrictedMetaModelEntityRepository
) extends Controller with Logging {

  /** Lists all MetaModels for the requesting user, provides HATEOAS links.
   *
   * @param request The request
   * @return The result
   */
  def showForUser(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    val repo = metaModelEntityRepo.restrictedTo(request.identity.id)
    repo.readAllIds().flatMap(ids => {
      Future.sequence(ids.map(repo.read)).map(_.map { mm =>
        MetaModelShortInfo(id = mm.id, name = mm.metaModel.name)
      })
    }).map((set: Set[MetaModelShortInfo]) => Ok(JsArray(set.toList.map(MetaModelShortInfo.writes.writes)))).recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** inserts whole MetaModel structure (MetaModel itself, DSLs...)
   *
   * @param request The request
   * @return The result
   */
  def insert(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(MetaModel.playJsonReadsEmpty).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      metaModel => {
        metaModelEntityRepo.restrictedTo(request.identity.id).create(
          MetaModelEntity(
            id = UUID.randomUUID(),
            metaModel = metaModel
          )
        ).map { metaModelEntity =>
          Created(Json.toJson(metaModelEntity))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** Updates whole MetaModel structure (MetaModel itself, DSLs...)
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def update(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate[MetaModel].fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      metaModel => {
        val repo = metaModelEntityRepo.restrictedTo(request.identity.id)
        repo.update(id, _.copy(metaModel = metaModel)).map { _ =>
          Ok(Json.toJson(metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** Deletes whole MetaModel incl. dsl definitions
   *
   * @param id      MetaModel-Id
   * @param request request
   * @return result
   */
  def delete(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).delete(id).map { _ =>
      Ok("")
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** returns whole MetaModels incl. dsl definitions and HATEOAS links */
  def get(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity =>
      Ok(Json.toJson(metaModelEntity))
    )
  }

  /** returns pure MetaModel without dsl definitions */
  def getMetaModelDefinition(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity => {
      Ok(Json.toJson(metaModelEntity.metaModel))
    })
  }

  /** returns all MClasses of a specific MetaModel as Json Array */
  def getMClasses(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity => {
      Ok(Json.toJson(metaModelEntity.metaModel.classMap.values))
    })
  }

  /** returns all MReferences of a specific MetaModel as Json Array */
  def getMReferences(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity => {
      Ok(Json.toJson(metaModelEntity.metaModel.referenceMap.values))
    })
  }

  /** returns specific MClass of a specific MetaModel as Json Object */
  def getMClass(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity => {
      metaModelEntity.metaModel.classMap.get(name).map(clazz =>
        Ok(Json.toJson(clazz))
      ).getOrElse(NotFound)
    })
  }

  /** returns specific MReference of a specific MetaModel as Json Object */
  def getMReference(id: UUID, name: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, metaModelEntity => {
      metaModelEntity.metaModel.referenceMap.get(name).map((reference: MReference) =>
        Ok(Json.toJson(reference))
      ).getOrElse(NotFound)
    })
  }

  /** returns style definition */
  def getStyle(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.style.map(m => Ok(Style.styleFormat.writes(m))).getOrElse(NotFound)
    })
  }

  /** returns shape definition */
  def getShape(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.shape.map(m => Ok(Shape.shapeFormat.writes(m))).getOrElse(NotFound)
    })
  }

  /** returns diagram definition */
  def getDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (m: MetaModelEntity) => {
      m.dsl.diagram.map(m => Ok(Diagram.diagramFormat.writes(m))).getOrElse(NotFound)
    })
  }

  /** A helper method for less verbose reads from the database */
  private def protectedRead[A](id: UUID, request: SecuredRequest[ZetaEnv, A], trans: MetaModelEntity => Result): Future[Result] = {
    metaModelEntityRepo.restrictedTo(request.identity.id).read(id).map { mm =>
      trans(mm)
    }.recover {
      case e: Exception => BadRequest(e.getMessage)
    }
  }

  /** updates shape definition */
  def updateShape(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(Shape.shapeFormat).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      shape => {
        metaModelEntityRepo.restrictedTo(request.identity.id).update(id, _.modify(_.dsl.shape).setTo(Some(shape))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates style definition */
  def updateStyle(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(Style.styleFormat).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      style => {
        metaModelEntityRepo.restrictedTo(request.identity.id).update(id, _.modify(_.dsl.style).setTo(Some(style))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates diagram definition */
  def updateDiagram(id: UUID)(request: SecuredRequest[ZetaEnv, JsValue]): Future[Result] = {
    request.body.validate(Diagram.diagramFormat).fold(
      faulty => {
        faulty.foreach(error(_))
        Future.successful(BadRequest(JsError.toJson(faulty)))
      },
      diagram => {
        metaModelEntityRepo.restrictedTo(request.identity.id).update(id, _.modify(_.dsl.diagram).setTo(Some(diagram))).map { metaModelEntity =>
          Ok(Json.toJson(metaModelEntity.metaModel))
        }.recover {
          case e: Exception => BadRequest(e.getMessage)
        }
      }
    )
  }

  /** updates method code */
  def updateClassMethodCode(metaModelId: UUID, className: String, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    request.body.asText.fold(
      Future.successful(BadRequest("ClassMethodError"))
    ) { code =>
      metaModelEntityRepo.restrictedTo(request.identity.id).update(metaModelId, _.modify(_.metaModel.classes).using { classes =>
        val clazz = classes.find(_.name == className).get
        val method = clazz.methods.find(_.name == methodName).get
        val updatedMethods = method.copy(code = code) +: clazz.methods.filter(_ != method)
        clazz.copy(methods = updatedMethods) +: classes.filter(_ != clazz)
      }).map { metaModelEntity =>
        Ok(Json.toJson(metaModelEntity.metaModel))
      }.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

  /** updates method code */
  def updateReferenceMethodCode(metaModelId: UUID, referenceName: String, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    request.body.asText.fold(
      Future.successful(BadRequest("ReferenceMethodError"))
    ) { code =>
      metaModelEntityRepo.restrictedTo(request.identity.id).update(metaModelId, _.modify(_.metaModel.references).using { references =>
        val reference = references.find(_.name == referenceName).get
        val method = reference.methods.find(_.name == methodName).get
        val updatedMethods = method.copy(code = code) +: reference.methods.filter(_ != method)
        reference.copy(methods = updatedMethods) +: references.filter(_ != reference)
      }).map { metaModelEntity =>
        Ok(Json.toJson(metaModelEntity.metaModel))
      }.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

  /** updates method code */
  def updateCommonMethodCode(metaModelId: UUID, methodName: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    request.body.asText.fold(
      Future.successful(BadRequest("CommonMethodError"))
    ) { code =>
      metaModelEntityRepo.restrictedTo(request.identity.id).update(metaModelId, _.modify(_.metaModel.methods).using { methods =>
        val method = methods.find(_.name == methodName).get
        method.copy(code = code) +: methods.filter(_ != method)
      }).map { metaModelEntity =>
        Ok(Json.toJson(metaModelEntity.metaModel))
      }.recover {
        case e: Exception => BadRequest(e.getMessage)
      }
    }
  }

  /**
   * Loads or generates the validator for a given meta model.
   *
   * The following HTTP status codes can be returned:
   * * 200 OK - The validator was loaded from memory and is contained in the response.
   * * 201 CREATED - The validator has been generated or regenerated and is contained in the response.
   * * 409 CONFLICT - A validator was not yet generated, or could not be generated.
   *
   * @param id          ID of the meta model to load or generate the validator.
   * @param generateOpt Force a (re)generation.
   * @param get         Return a result body.
   * @param request     The HTTP-Request.
   * @return The validator.
   */
  def getValidator(id: UUID, generateOpt: Option[Boolean], get: Boolean)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    protectedRead(id, request, (metaModelEntity: MetaModelEntity) => {

      val generate = generateOpt.getOrElse(false)

      if (generate) {

        new ValidatorGenerator(metaModelEntity).generateValidator() match {
          case ValidatorGeneratorResult(false, msg) => if (get) Conflict(msg) else Conflict
          case ValidatorGeneratorResult(_, validator) =>
            metaModelEntityRepo.restrictedTo(request.identity.id).update(id, _.copy(validator = Some(validator)))
            if (get) Created(validator) else Created
        }

      } else {

        metaModelEntity.validator match {
          case Some(validatorText) => if (get) Ok(validatorText) else Ok
          case None =>
            val url = routes.ScalaRoutes.getMetamodelsValidator(id, Some(true)).absoluteURL()(request)
            if (get) {
              Conflict(
                s"""No validator generated yet. Try calling $url first.""")
            } else {
              Conflict
            }
        }

      }

    })
  }

}
