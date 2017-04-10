package util

import util.domain._
import play.api.Logger
import play.api.libs.json._

class MetamodelBuilder {

  def fromJson(json: JsObject) = {
    new Metamodel(Map[String, MClass](), Map[String, MReference](), Map[String, MEnum]())
  }
}

object MetamodelBuilder {
  def apply() = new MetamodelBuilder
}
