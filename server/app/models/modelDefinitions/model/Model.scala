package models.modelDefinitions.model


import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements._
import models.modelDefinitions.model.elements.ModelWrites._
import models.modelDefinitions.model.elements.ModelReads._

import play.api.libs.functional.syntax._
import play.api.libs.json._

import play.api.libs.json._

import scala.collection.immutable._

case class Model(name: String, elements: Map[String, ModelElement], uiState: String)

object Model {

  def reads(implicit meta: MetaModel): Reads[Model] = {
    val mapReads = ModelReads.elementMapReads(meta)
    ((__ \ "name").read[String] and
      (__ \ "elements").read[Map[String, ModelElement]](mapReads) and
      (__ \ "uiState").read[String]
      ) (Model.apply _)
  }

  implicit val writes = new Writes[Model] {
    def writes(d: Model): JsValue = Json.obj(
      "name" -> d.name,
      "elements" -> d.elements.values,
      "uiState" -> d.uiState
    )
  }

}







