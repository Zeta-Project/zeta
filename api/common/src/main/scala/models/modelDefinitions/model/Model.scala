package models.modelDefinitions.model

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import play.api.libs.json.Json
import play.api.libs.json.OFormat
/**
 * Immutable container for model definitions
 *
 * @param name the name of the model
 * @param metaModel the corresponding MetaModel instance
 * @param nodes the nodes of the actual model data
 * @param edges the edges of the actual model data
 * @param uiState the ui-state of the browser client. Location is debatable
 */
case class Model(
    name: String,
    metaModel: MetaModel,
    nodes: Map[String, Node],
    edges: Map[String, Edge],
    uiState: String
)

object Model {

  implicit val playJsonModelFormat: OFormat[Model] = Json.format[Model]

}
