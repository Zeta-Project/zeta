package de.htwg.zeta.server.util

import play.api.libs.json.JsObject

import de.htwg.zeta.server.util.domain.MClass
import de.htwg.zeta.server.util.domain.MEnum
import de.htwg.zeta.server.util.domain.MReference
import de.htwg.zeta.server.util.domain.Metamodel

class MetamodelBuilder {

  def fromJson(json: JsObject) = {
    new Metamodel(Map[String, MClass](), Map[String, MReference](), Map[String, MEnum]())
  }
}

object MetamodelBuilder {
  def apply() = new MetamodelBuilder
}
