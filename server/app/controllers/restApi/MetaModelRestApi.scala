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

  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[MetaModel]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        metaModel => {
          val preparedMeta = metaModel.copy(
            id = Some(java.util.UUID.randomUUID().toString),
            userId = userId
          )
          MetaModelDaoImpl.insert(preparedMeta).map { res =>
            Created(Json.obj("id" -> preparedMeta.id))
          }
        }
      )
    }
  }

  // inserts whole metamodel structure (mcore, dsls..) just by receiving mcore without dsls, not a fan of this..
  def alternativeInsert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Definition]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        definition => {
          val metaModel = createWithDef(userId, definition)
          MetaModelDaoImpl.insert(metaModel).map { res =>
            Created(Json.obj("id" -> metaModel.id))
          }
        }
      )
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[MetaModel]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        metaModel => {
          protectedWrite(id, {
            val preparedMeta = metaModel.copy(id = Some(id), userId = userId)
            MetaModelDaoImpl.update(preparedMeta)
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
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m)))
    }
  }

  def getDefinition(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.definition)))
    }
  }

  def updateDefinition(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Definition]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        definition => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> definition)
            MetaModelDaoImpl.update(selector, modifier)
          })
        }
      )
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.style)))
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.shape)))
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => Ok(Json.toJson(m.diagram)))
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition
        val classesDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classesDef))
      })
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition
        val refsDef = d.copy(mObjects = d.mObjects.filter(t => t._2.isInstanceOf[MReference]))
        Ok(Json.toJson(refsDef))
      })
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition
        val classDef = d.copy(mObjects = d.mObjects.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classDef))
      })
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModel) => {
        val d = m.definition
        val refDef = d.copy(mObjects = d.mObjects.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
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

  private def createWithDef(userId: String, definition: Definition) = MetaModel(
    Some(java.util.UUID.randomUUID().toString),
    userId,
    definition,
    Style(""),
    Shape(""),
    Diagram("")
  )

}