package controllers.restApi

import java.time.Instant
import javax.inject.Inject

import dao.{ModelsWriteResult, DbWriteResult}
import dao.metaModel._
import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.elements.MCoreWrites._
import models.modelDefinitions.metaModel._
import MetaModelEntity._
import models.modelDefinitions.metaModel.elements.{MReference, MClass}
import models.oAuth.OAuthDataHandler
import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scalaoauth2.provider.OAuth2Provider

import ModelsWriteResult._

// TODO: Reduce redundancy for some methods

class MetaModelRestApi @Inject()(metaModelDao: ZetaMetaModelDao) extends Controller with OAuth2Provider {

  def showForUser = Action.async { implicit request =>
    oAuth { userId =>
      metaModelDao.findMetaModelsByUser(userId).map { res =>
        val out = res.map{info => info.copy(links = Some(Seq(
          HLink.get("self", routes.MetaModelRestApi.get(info.id).absoluteURL),
          HLink.delete("remove", routes.MetaModelRestApi.get(info.id).absoluteURL)
        )))}
        Ok(Json.toJson(out))
      }
    }
  }

  // inserts whole metamodel structure
  def insert = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[MetaModelEntity](MetaModelEntity.strippedReads)
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        tempEntity => {
          val entity = tempEntity.asNew(userId)
          metaModelDao.insert(entity).map { res =>
            Created(Json.toJson(res))
          }
        }
      )
    }
  }

  def update(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[MetaModelEntity](MetaModelEntity.strippedReads)
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        tempEntity => {
          val entity = tempEntity.asUpdate(id, userId)
          protectedWrite(id, {
            metaModelDao.update(entity)
          })
        }
      )
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedWrite(id, {
        metaModelDao.deleteById(id)
      })
    }
  }

  def get(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        val out = m.copy(links = Some(Seq(
          HLink.put("update", routes.MetaModelRestApi.get(m.id).absoluteURL),
          HLink.delete("remove", routes.MetaModelRestApi.get(m.id).absoluteURL)
        )))
        Ok(Json.toJson(out)(MetaModelEntity.strippedWrites))
      })
    }
  }

  def getMetaModelDefinition(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => Ok(Json.toJson(m.metaModel)))
    }
  }

  def updateMetaModelDefinition(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[MetaModel]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        metaModel => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> Json.obj("metaModel" -> metaModel, "updated" -> Instant.now))
            metaModelDao.update(selector, modifier)
          })
        }
      )
    }
  }

  def getMClasses(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        val d = m.metaModel
        val classesDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MClass]))
        Ok(Json.toJson(classesDef.elements.values))
      })
    }
  }

  def getMReferences(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        val d = m.metaModel
        val refsDef = d.copy(elements = d.elements.filter(t => t._2.isInstanceOf[MReference]))
        Ok(Json.toJson(refsDef.elements.values))
      })
    }
  }

  def getMClass(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        val d = m.metaModel
        val classDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MClass]))
        classDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def getMReference(id: String, name: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        val d = m.metaModel
        val refDef = d.copy(elements = d.elements.filter(p => p._1 == name && p._2.isInstanceOf[MReference]))
        refDef.elements.values.headOption.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def getStyle(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        m.dsl.style.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def getShape(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        m.dsl.shape.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def getDiagram(id: String) = Action.async { implicit request =>
    oAuth { implicit userId =>
      protectedRead(id, (m: MetaModelEntity) => {
        m.dsl.diagram.map(m => Ok(Json.toJson(m))).getOrElse(NotFound)
      })
    }
  }

  def updateShape(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Shape]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        shape => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> Json.obj("dsl.shape" -> shape, "updated" -> Instant.now))
            metaModelDao.update(selector, modifier)
          })
        }
      )
    }
  }

  def updateStyle(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Style]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        style => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> Json.obj("dsl.style" -> style, "updated" -> Instant.now))
            metaModelDao.update(selector, modifier)
          })
        }
      )
    }
  }

  def updateDiagram(id: String) = Action.async(BodyParsers.parse.json) { implicit request =>
    oAuth { implicit userId =>
      val in = request.body.validate[Diagram]
      in.fold(
        errors => {
          Future.successful(BadRequest(JsError.toFlatJson(errors)))
        },
        diagram => {
          protectedWrite(id, {
            val selector = Json.obj("id" -> id)
            val modifier = Json.obj("$set" -> Json.obj("dsl.diagram" -> diagram, "updated" -> Instant.now))
            metaModelDao.update(selector, modifier)
          })
        }
      )
    }
  }

  def oAuth[A](block: String => Future[Result])(implicit request: Request[A]) = {
    authorize(OAuthDataHandler()) { authInfo => block(authInfo.user.uuid.toString) }
  }

  def protectedRead(id: String, trans: MetaModelEntity => Result)(implicit userId: String): Future[Result] = {
    metaModelDao.findById(id).map {
      case Some(meta) => if (userId == meta.userId) {
        trans(meta)
      } else {
        Unauthorized
      }
      case None => NotFound
    }
  }

  private def protectedWrite(id: String, write: => Future[DbWriteResult[String]])(implicit userId: String): Future[Result] = {
    metaModelDao.hasAccess(id, userId).flatMap {
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