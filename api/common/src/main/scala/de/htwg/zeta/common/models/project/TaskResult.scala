package de.htwg.zeta.common.models.project

case class TaskResult(
    errorDsl: String,
    messages: List[String],
    position: Option[(Int, Int)],
    success: Boolean
)
object TaskResult {
  def error(errorDsl: String, messages: List[String], position: Option[(Int, Int)]): TaskResult = TaskResult(errorDsl, messages, position, success = false)

  def success(): TaskResult = TaskResult(errorDsl = "", List(), None, success = true)
}
