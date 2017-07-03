package de.htwg.zeta.server.controller.restApi.modelUiFormat


import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.common.models.modelDefinitions.model.Model
import play.api.libs.json.JsError
import play.api.libs.json.JsValue
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsResult


/**
 */
object ModelUiFormat {

  def futureReads(userID: UUID, json: JsValue): Future[JsResult[Model]] = {
    json.validate(ModelFormat(userID)) match {
      case JsSuccess(futureRes, _) => futureRes
      case e: JsError => Future.successful(e)
    }
  }

}
