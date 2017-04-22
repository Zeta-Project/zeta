package models.frontend

import julienrf.json.derived
import play.api.libs.json.__
import play.api.libs.json.OFormat

/**
 * Representation of a change of the model.
 * E.g. Model Saved, Entity created etc..
 */
sealed trait ModelChanged extends UserRequest

sealed trait UserRequest extends Request {
  /**
   * The model from which the request was send
   */
  val model: String
}
case class ExecuteBondedTask(model: String, task: String) extends UserRequest
case class SavedModel(model: String) extends UserRequest with ModelChanged

object UserRequest {
  implicit val format: OFormat[UserRequest] = derived.flat.oformat((__ \ "action").format[String])
}

sealed trait UserResponse extends Response
case class BondedTaskNotExecutable(task: String, reason: String) extends UserResponse
case class Entry(task: String, menu: String, item: String)
case class BondedTaskList(tasks: List[Entry]) extends UserResponse
case class BondedTaskCompleted(task: String, status: Int) extends UserResponse
case class BondedTaskStarted(task: String) extends UserResponse

object UserResponse {
  implicit val formatEntry: OFormat[Entry] = derived.oformat
  implicit val format: OFormat[UserResponse] = derived.flat.oformat((__ \ "type").format[String])
}
