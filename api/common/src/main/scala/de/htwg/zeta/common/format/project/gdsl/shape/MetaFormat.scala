package de.htwg.zeta.common.format.project.gdsl.shape

import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import play.api.libs.json.JsNull
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

class MetaFormat extends Writes[Edge] {

  override def writes(edge: Edge): JsValue = {
    edge.conceptElement.split('.') match {
      case Array(sourceMClass, sourceMRef, forMClass, targetMRef) =>
        val targetMClass = edge.target
        JsObject(Seq(
          "source" -> JsObject(Seq(
            "mclass" -> JsString(sourceMClass),
            "mref" -> JsString(sourceMRef)
          )),
          "target" -> JsObject(Seq(
            "mclass" -> JsString(targetMClass),
            "mref" -> JsString(targetMRef)
          )),
          "forMClass" -> JsString(forMClass)
        ))
      case _ => JsNull
    }
  }
  
}

object MetaFormat {
  def apply(): MetaFormat = new MetaFormat()
}
