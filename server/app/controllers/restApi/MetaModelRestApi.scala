package controllers.restApi

import dao.{DbWriteResult}
import dao.metaModel.MetaModelDaoImpl
import models.metaModel._
import models.metaModel.MetaModel._
import models.metaModel.mCore.{MReference, MClass}
import models.oAuth.OAuthDataHandler
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scalaoauth2.provider.OAuth2Provider


class MetaModelRestApi extends Controller with OAuth2Provider {

  def showForUser = Action.async { implicit request =>
    oAuth { userId =>
      MetaModelDaoImpl.findIdsByUser(userId).map { res =>
        Ok(Json.toJson(res))
      }
    }
  }

  // inserts whole metamodel structure just by receiving definition
  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Definition]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        definition => {
          val metaModel = initMetaModel(userId, definition)
          MetaModelDaoImpl.insert(metaModel).map { res =>
            Created(Json.toJson(res))
          }
        }
      )
    }
  }

  private def initMetaModel(userId: String, definition: Definition) = MetaModel(
    java.util.UUID.randomUUID().toString,
    userId,
    definition
  )

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Definition]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        definition => {
          protectedWrite(id, {
            MetaModelDaoImpl.updateDefinition(id, definition)
          })
        }
      )
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedWrite(id, {
        MetaModelDaoImpl.deleteById(id)
      })
    }
  }

  def get(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition)))
    }
  }

  def getConcept(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition.concept)))
    }
  }

  def updateConcept(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Concept]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        concept => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> Json.obj("definition.concept" -> concept))
            MetaModelDaoImpl.update(selector, modifier)
          })
        }
      )
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition.style)))
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition.shape)))
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition.diagram)))
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition.concept
        val classesDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classesDef))
      })
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition.concept
        val refsDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MReference]))
        Ok(Json.toJson(refsDef))
      })
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition.concept
        val classDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classDef))
      })
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition.concept
        val refDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
        Ok(Json.toJson(refDef))
      })
    }
  }

  def oAuth[A](block: String => Future[Result])(implicit request: Request[A]) = {
    authorize(OAuthDataHandler()) { authInfo => block(authInfo.user.uuid.toString) }
  }

  def protectedRead(id: String, trans: MetaModel => Result)(implicit userId: String): Future[Result] = {
    MetaModelDaoImpl.findById(id).map {
      case Some(meta) => if (userId == meta.userId) {
        trans(meta)
      } else {
        Unauthorized
      }
      case None => NotFound
    }
  }

  private def protectedWrite[A](id: String, write: => Future[DbWriteResult])(implicit userId: String): Future[Result] = {
    MetaModelDaoImpl.hasAccess(id, userId).flatMap {
      case Some(b) => {
        if (b) {
          write.map { res => Ok(Json.toJson(res)) }
        } else {
          Future.successful(Unauthorized)
        }
      }
      case None => Future.successful(NotFound)
    }
  }



}