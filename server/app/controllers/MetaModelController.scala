package controllers


import java.util.UUID
import javax.inject.Inject

import dao.DbWriteResult
import dao.metaModel.MetaModelDaoImpl
import models.metaModel._
import models.metaModel.mCore.MObject
import play.api.Play.current
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.WebSocket
import util.definitions.UserEnvironment
import util.graph.MetamodelGraphDiff

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def newMetaModel(name: String) = SecuredAction { implicit request =>
    if (name.isEmpty) {
      BadRequest("Name must not be empty")
    } else {
      val concept = Concept(elements = Map.empty[String, MObject], uiState = "")
      val metaModel = MetaModel(name = name, concept = concept, shape = None, style = None, diagram = None)
      val entity = MetaModelEntity.initialize(request.user.uuid.toString, metaModel)
      val result = Await.result(MetaModelDaoImpl.insert(entity), 30 seconds)
      if (result.ok) {
        Redirect(routes.MetaModelController.metaModelEditor(result.insertId.get))
      } else {
        BadRequest("Could not create new meta model")
      }
    }
  }

  def metaModelEditor(metaModelUuid: String) = SecuredAction { implicit request =>
    val hasAccess = Await.result(MetaModelDaoImpl.hasAccess(metaModelUuid, request.user.uuid.toString), 30 seconds)
    if (hasAccess.isDefined && hasAccess.get) {
      val metaModelEntity = Await.result(MetaModelDaoImpl.findById(metaModelUuid), 30 seconds)
      if (metaModelEntity.isDefined) {
        // TODO: Fix Graph with MetaModelGraphDiff
        Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.user), metaModelUuid, metaModelEntity.get))
      } else {
        BadRequest("Could not find meta model")
      }
    } else {
      Unauthorized("Unauthorized")
    }
  }

  def deleteMetaModel(metaModelUuid: String) = SecuredAction { implicit request =>
    val userUuid = request.user.uuid.toString
    if (Await.result(MetaModelDatabase_2.modelExists(metaModelUuid), 30 seconds)) {
      val metaModel = Await.result(MetaModelDatabase_2.loadModel(metaModelUuid), 30 seconds)
      if (metaModel.isDefined && metaModel.get.userUuid == userUuid) {
        MetaModelDatabase_2.deleteModel(metaModelUuid)

        Redirect(controllers.webpage.routes.WebpageController.diagramsOverview(null))
      } else {
        Redirect(controllers.webpage.routes.WebpageController.index())
      }
    } else {
      Redirect(controllers.webpage.routes.WebpageController.index())
    }
  }

  def metaModelSocket(metaModelUuid: String) = WebSocket.acceptWithActor[JsValue, JsValue] { request => out =>
    MetaModelWsActor.props(out, metaModelUuid)
  }


  // only for development purposes, will be deleted later on
  def dummyInsert = SecuredAction.async { implicit request =>
    val json = "{\"userId\":\"X\",\"definition\":{\"name\":\"xy\",\"mObjects\":[{\"mType\":\"mReference\",\"name\":\"isWife\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":[{\"type\":\"Female\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"target\":[{\"type\":\"Male\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"attributes\":[]},{\"mType\":\"mEnum\",\"name\":\"healthInsurance\",\"symbols\":[\"private\",\"national\",\"none\"]},{\"mType\":\"mClass\",\"name\":\"Person\",\"abstract\":true,\"superTypes\":[],\"inputs\":[{\"type\":\"isFather\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false},{\"type\":\"isMother\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"outputs\":[],\"attributes\":[{\"name\":\"FirstName\",\"globalUnique\":false,\"localUnique\":false,\"type\":\"String\",\"default\":\"Hans\",\"constant\":false,\"singleAssignment\":false,\"expression\":\"\",\"ordered\":false,\"transient\":false,\"upperBound\":1,\"lowerBound\":1},{\"name\":\"Geburtstag\",\"globalUnique\":true,\"localUnique\":true,\"type\":\"String\",\"default\":\"\",\"constant\":false,\"singleAssignment\":false,\"expression\":\"\",\"ordered\":false,\"transient\":false,\"upperBound\":1,\"lowerBound\":1},{\"name\":\"Steuernummer\",\"globalUnique\":true,\"localUnique\":true,\"type\":\"String\",\"default\":\"\",\"constant\":false,\"singleAssignment\":false,\"expression\":\"\",\"ordered\":false,\"transient\":false,\"upperBound\":-1,\"lowerBound\":1},{\"name\":\"Krankenversicherungs\",\"globalUnique\":false,\"localUnique\":false,\"type\":\"healthInsurance\",\"default\":\"none\",\"constant\":false,\"singleAssignment\":false,\"expression\":\"\",\"ordered\":false,\"transient\":false,\"upperBound\":1,\"lowerBound\":1}]},{\"mType\":\"mReference\",\"name\":\"isMother\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":[{\"type\":\"Female\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"target\":[{\"type\":\"Person\",\"upperBound\":-1,\"lowerBound\":0,\"deleteIfLower\":false}],\"attributes\":[]},{\"mType\":\"mClass\",\"name\":\"Female\",\"abstract\":false,\"superTypes\":[\"Person\"],\"inputs\":[{\"type\":\"isHusband\",\"upperBound\":1,\"lowerBound\":0,\"deleteIfLower\":false}],\"outputs\":[{\"type\":\"isWife\",\"upperBound\":1,\"lowerBound\":0,\"deleteIfLower\":false},{\"type\":\"isMother\",\"upperBound\":-1,\"lowerBound\":0,\"deleteIfLower\":false}],\"attributes\":[]},{\"mType\":\"mReference\",\"name\":\"isFather\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":[{\"type\":\"Male\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"target\":[{\"type\":\"Person\",\"upperBound\":-1,\"lowerBound\":0,\"deleteIfLower\":false}],\"attributes\":[]},{\"mType\":\"mReference\",\"name\":\"isHusband\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":[{\"type\":\"Male\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"target\":[{\"type\":\"Female\",\"upperBound\":1,\"lowerBound\":1,\"deleteIfLower\":false}],\"attributes\":[]},{\"mType\":\"mClass\",\"name\":\"Male\",\"abstract\":false,\"superTypes\":[\"Person\"],\"inputs\":[{\"type\":\"isWife\",\"upperBound\":1,\"lowerBound\":0,\"deleteIfLower\":false}],\"outputs\":[{\"type\":\"isHusband\",\"upperBound\":1,\"lowerBound\":0,\"deleteIfLower\":false},{\"type\":\"isFather\",\"upperBound\":-1,\"lowerBound\":0,\"deleteIfLower\":false}],\"attributes\":[]}],\"graph\":\"\"},\"style\":{\"code\":\"\"},\"shape\":{\"code\":\"\"},\"diagram\":{\"code\":\"\"}}"
    val metaModel = Json.parse(json).validate[MetaModelEntity]
    metaModel.fold(
      errors => {
        Future.successful(BadRequest(JsError.toFlatJson(errors)))
      },
      metaModel => {
        val preparedMeta = metaModel.copy(
          id = java.util.UUID.randomUUID().toString,
          userId = request.user.uuid.toString
        )
        MetaModelDaoImpl.insert(preparedMeta).map { res =>
          Created(Json.obj("id" -> preparedMeta.id))
        }
      }
    )
  }


}
