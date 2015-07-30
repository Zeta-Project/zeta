package modigen.util.domain

import play.api.libs.json.{JsValue, JsObject}

class Metamodel(val classes:Map[String, MClass], val references:Map[String, MReference], val enums:Map[String, MEnum]){
  def getObjectByName(name:String) = {
    classes.get(name) match {
      case Some(x) => Some(x)
      case None => references.get(name)
    }
  }
}