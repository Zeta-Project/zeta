package de.htwg.zeta.common.models.project

case class TaskResult(
    errorDsl: String,
    messages: List[String],
    success: Boolean
)
object TaskResult {
  def error(errorDsl: String, messages: List[String]) = TaskResult(errorDsl, messages, success = false)

  def success() = TaskResult(errorDsl = "", List(), success = true)
}
