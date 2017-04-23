package models.modelDefinitions.model

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.ModelElement
import models.modelDefinitions.model.elements.ModelReads
import models.modelDefinitions.model.elements.ModelWrites
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.Writes

/**
 * Immutable container for model definitions
 *
 * @param name the name of the model
 * @param metaModel the corresponding metamodel instance
 * @param elements the object graph containing the actual model data
 * @param uiState the uistate of the browser client. Location is debatable
 */
case class Model(
    name: String,
    metaModel: MetaModel,
    elements: Map[String, ModelElement],
    uiState: String)

object Model {

  def readAndMergeWithMetaModel(json: JsValue, meta: MetaModel): JsResult[Model] = {
    val mapReads = ModelReads.elementMapReads(meta)
    val name = (json \ "name").as[String]
    val elements = (json \ "elements").as[Map[String, ModelElement]](mapReads)
    val uiState = (json \ "uiState").as[String]
    val model = Model(name, meta, elements, uiState)

    JsSuccess(model)
  }

  implicit val reads = new Reads[Model] {
    def reads(json: JsValue): JsResult[Model] = {
      val name = (json \ "name").as[String]
      val uiState = (json \ "uiState").as[String]
      val model = Model(name, null, Map[String, ModelElement](), uiState)
      JsSuccess(model)
    }
  }

  implicit val writes = new Writes[Model] {
    private implicit val mObjectWrites = ModelWrites.mObjectWrites
    def writes(d: Model): JsValue = Json.obj(
      "name" -> d.name,
      "elements" -> d.elements.values,
      "uiState" -> d.uiState
    )
  }

}
