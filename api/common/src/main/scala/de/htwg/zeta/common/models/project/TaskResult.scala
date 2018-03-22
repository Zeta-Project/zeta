package de.htwg.zeta.common.models.project

case class TaskResult(
    messages: List[String],
    success: Boolean
)
object TaskResult {
  def error(messages: List[String]) = TaskResult(messages, success = false)

  def success() = TaskResult(List(), success = true)
}
