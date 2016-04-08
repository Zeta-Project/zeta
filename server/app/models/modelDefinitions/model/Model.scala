package models.modelDefinitions.model


import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements._
import models.modelDefinitions.model.elements.ModelWrites._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.collection.immutable._

/**
  * Immutable container for model definitions
  * @param name the name of the model
  * @param metaModel the corresponding metamodel instance
  * @param elements the object graph containing the actual model data
  * @param uiState the uistate of the browser client. Location is debatable
  */
case class Model(
  name: String,
  metaModel: MetaModel,
  elements: Map[String, ModelElement],
  uiState: String
)

object Model {

  def reads(implicit meta: MetaModel): Reads[Model] = {
    val mapReads = ModelReads.elementMapReads(meta)
    ((__ \ "name").read[String] and
      Reads.pure(meta) and
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







