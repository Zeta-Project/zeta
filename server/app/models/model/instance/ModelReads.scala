package models.model.instance

import models.metaModel.mCore.{MObject, MClass}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.collection.immutable._

object ModelReads {

//  private trait InvalidLink { val message: String }
//  private def InvalidNodeLink

//  def metaModelDefinitionReads(meta: MetaModelDefinition): Reads[ModelData] = (
//    (__ \ "name").read[String] and
//      (__ \ "data").read[Map[String, ModelElement]]
//    ) (ModelData.apply _)
//
//
//  implicit val modelElementReads = new Reads[ModelElement] {
//
//    def hasKey(name: String, json: JsValue) = (json \ name).toOption != None
//
//    val meta = new MetaModelDefinition("", Map[String,MObject]())
//
//    override def reads(json: JsValue): JsResult[ModelElement] = {
//      if(hasKey("mClass", json)) {
//        json.validate(nodeReads.reads(json).map(_.asInstanceOf[ModelElement]))
//      } else if(hasKey("mReference", json)) {
//
//      } else {
//        JsError("")
//      }
//    }
//  }
//
//  val unknownMClassError = ValidationError("Unknown mClass")
//
//  def metaHasType[T <: MObject](meta: MetaModelDefinition, name: String) = {
//    meta.mObjects.get(name).exists(mObj => mObj.isInstanceOf[T])
//  }
//
//  def nodeReads(implicit meta: MetaModelDefinition): Reads[Node] = (
//    (__ \ "id").read("1") and
//    (__ \ "mClass").read[String].filter(unknownMClassError) {
//      s => metaHasType[MClass](meta, s)
//    }.map {
//      s => meta.mObjects.get(s).get.asInstanceOf[MClass]
//    } and
//    (__ \ "outputs").read(Seq[Edge]()) and
//      (__ \ "inputs").read(Seq[Edge]()) and
//      (__ \ "attributes").read(Seq[Attribute]())
//    )(Node.apply2 _)



}
