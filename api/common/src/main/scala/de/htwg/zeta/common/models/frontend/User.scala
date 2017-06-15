package de.htwg.zeta.common.models.frontend

import java.util.UUID

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
  val modelId: UUID
}
case class ExecuteBondedTask(modelId: UUID, taskId: UUID) extends UserRequest
case class SavedModel(modelId: UUID) extends UserRequest with ModelChanged

object UserRequest {
  implicit val format: OFormat[UserRequest] = derived.flat.oformat((__ \ "action").format[String])
}

sealed trait UserResponse extends Response
case class BondedTaskNotExecutable(taskId: UUID, reason: String) extends UserResponse
case class Entry(taskId: UUID, menu: String, item: String)
case class BondedTaskList(tasks: List[Entry]) extends UserResponse
case class BondedTaskCompleted(taskId: UUID, status: Int) extends UserResponse
case class BondedTaskStarted(taskId: UUID) extends UserResponse

object UserResponse {
  implicit val formatEntry: OFormat[Entry] = derived.oformat
  implicit val format: OFormat[UserResponse] = derived.flat.oformat((__ \ "type").format[String])
}
