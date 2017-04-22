package util

import play.api.libs.json.JsObject

import util.domain.MClass
import util.domain.MEnum
import util.domain.MReference
import util.domain.Metamodel

class MetamodelBuilder {

  def fromJson(json: JsObject) = {
    new Metamodel(Map[String, MClass](), Map[String, MReference](), Map[String, MEnum]())
  }
}

object MetamodelBuilder {
  def apply() = new MetamodelBuilder
}
