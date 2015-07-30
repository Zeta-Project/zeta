package shared

sealed trait DiagramWSMessage
object DiagramWSMessage{
  case class DataVisCodeMessage(context: String, code: String, classname: String) extends DiagramWSMessage
  case class DataVisScopeQuery(classname: String) extends DiagramWSMessage
}

sealed trait DiagramWSOutMessage
object DiagramWSOutMessage{
  case class NewScriptFile(context: String, path: String) extends DiagramWSOutMessage
  case class DataVisScope(scope: List[String], classname: String) extends DiagramWSOutMessage
  case class DataVisError(msg: List[String], context: String) extends DiagramWSOutMessage
}
