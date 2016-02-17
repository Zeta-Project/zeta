package controllers


import java.util.UUID
import javax.inject.Inject


import dao.metaModel.MetaModelDaoImpl
import models.metaModel._
import play.api.Play.current
import play.api.libs.json.{JsError, Json, JsValue}
import play.api.mvc.{Action, WebSocket}
import util.definitions.UserEnvironment
import util.graph.MetamodelGraphDiff

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by mgt on 17.10.15.
  */


class MetaModelController @Inject()(override implicit val env: UserEnvironment) extends securesocial.core.SecureSocial {

  def newMetaModel() = SecuredAction { implicit request =>
    Redirect(routes.MetaModelController.metaModelEditor(UUID.randomUUID.toString))
  }

  def metaModelEditor(metaModelUuid: String) = SecuredAction { implicit request =>
    var metaModel: Option[MetaModel_2] = None
    if (Await.result(MetaModelDatabase_2.modelExists(metaModelUuid), 30 seconds)) {
      val tmpMetaModel = Await.result(MetaModelDatabase_2.loadModel(metaModelUuid), 30 seconds)
      if (tmpMetaModel.get.userUuid == request.user.uuid.toString) {
        metaModel = Some(tmpMetaModel.get.copy(metaModel = MetamodelGraphDiff.fixMetaModel(tmpMetaModel.get.metaModel)))
      }
    }
    Ok(views.html.metamodel.MetaModelGraphicalEditor(Some(request.user), metaModelUuid, metaModel))
  }

  def saveMetaModel() = SecuredAction { implicit request =>
    println(request.body.asJson)
    request.body.asJson match {
      case Some(json) =>
        Try(UUID.fromString((json \ "uuid").as[String])).toOption match {
          case Some(uuid) =>
            val uuidStr = uuid.toString

            val metaModelData = new MetaModelData_2(
              name = (json \ "name").as[String],
              data = (json \ "data").as[JsValue].toString(),
              graph = (json \ "graph").as[JsValue].toString()
            )

            if (Await.result(MetaModelDatabase_2.modelExists(uuidStr), 30 seconds)) {
              // Change the existing Meta Model
              MetaModelDatabase_2.updateMetaModelData(uuidStr, metaModelData)
            } else {
              // Create a new Meta Model
              MetaModelDatabase_2.saveModel(new MetaModel_2(
                uuid = uuidStr,
                userUuid = request.user.uuid.toString,
                metaModel = metaModelData,
                style = new MetaModelStyle_2,
                shape = new MetaModelShape_2,
                diagram = new MetaModelDiagram_2
              ))

            }

            Ok("Saved")
          case None => BadRequest("Invalid UUID")
        }
      case None => BadRequest("Invalid JSON")
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
    val metaModel = Json.parse(json).validate[MetaModel]
    metaModel.fold(
      errors => {
        Future.successful(BadRequest(JsError.toFlatJson(errors)))
      },
      metaModel => {
        val preparedMeta = metaModel.copy(
          id = Some(java.util.UUID.randomUUID().toString),
          userId = request.user.uuid.toString
        )
        MetaModelDaoImpl.insert(preparedMeta).map { res =>
          Created(Json.obj("id" -> preparedMeta.id))
        }
      }
    )
  }



}
