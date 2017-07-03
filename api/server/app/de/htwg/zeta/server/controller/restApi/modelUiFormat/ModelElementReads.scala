package de.htwg.zeta.server.controller.restApi.modelUiFormat

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import play.api.libs.json.JsResult
import play.api.libs.json.JsError
import play.api.libs.json.Reads
import play.api.libs.json.JsValue


class ModelElementReads private(metaModel: MetaModel) extends Reads[Either[Node, Edge]] {
  val nodeReads = new NodeFormat(metaModel)
  val edgeReads = new EdgeFormat(metaModel)

  override def reads(json: JsValue): JsResult[Either[Node, Edge]] = {
    def hasKey(name: String) = (json \ name).toOption.isDefined

    if (hasKey("mClass")) {
      json.validate(nodeReads).map(Left(_))
    } else if (hasKey("mReference")) {
      json.validate(edgeReads).map(Right(_))
    } else {
      JsError(s"Unknown type. json: $json is neither a Node nor an Edge")
    }
  }
}

object ModelElementReads{
  def apply(metaModel: MetaModel): ModelElementReads = new ModelElementReads(metaModel)

}