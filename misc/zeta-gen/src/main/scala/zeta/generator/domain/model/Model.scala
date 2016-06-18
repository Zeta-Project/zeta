package zeta.generator.domain.model


import play.api.libs.functional.syntax._
import play.api.libs.json._

import play.api.libs.json._
import zeta.generator.domain.metaModel.MetaModel
import zeta.generator.domain.model.elements.{ModelReads, ModelElement}


import scala.collection.immutable._

case class Model(id: String, name: String, metaModel: MetaModel, elements: Map[String, ModelElement])

object Model {

  def reads(implicit meta: MetaModel): Reads[Model] = {
    val mapReads = ModelReads.elementMapReads(meta)
    ((__ \ "id").read[String] and
      (__ \ "model" \ "name").read[String] and
      Reads.pure(meta) and
      (__ \ "model" \ "elements").read[Map[String, ModelElement]](mapReads)
      ) (Model.apply _)
  }
}







