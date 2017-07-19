package de.htwg.zeta.generator.template

import java.util.UUID

import scala.concurrent.Future

import de.htwg.zeta.common.models.entity.ModelEntity

/**
 * Result of a generator
 */
sealed trait Result {
  val message: String
  val status: Int
}
case class Success(message: String = "success", status: Int = 0) extends Result
case class Warning(message: String = "warning", status: Int = 2) extends Result
case class Error(message: String = "error", status: Int = 1) extends Result

trait Transformer {
  def prepare(modelId: UUID): Future[Transformer] = {
    Future.successful(this)
  }

  def prepare(modelIds: List[UUID]): Future[Transformer] = {
    Future.successful(this)
  }

  def transform(entity: ModelEntity): Future[Transformer]

  def exit(): Future[Result]

}
