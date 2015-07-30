package modigen.util.domain

class Metamodel(val classes:Map[String, MClass], val references:Map[String, MReference]){
  def getObjectByName(name:String) = {
    classes.get(name) match {
      case Some(x) => Some(x)
      case None => references.get(name)
    }
  }
}
