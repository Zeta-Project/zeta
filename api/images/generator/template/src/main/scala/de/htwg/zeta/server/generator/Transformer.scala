package de.htwg.zeta.server.generator

import java.util.UUID

import scala.concurrent.Future

import models.document.ModelEntity
import models.remote.Remote

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
  def prepare(modelId: UUID)(implicit remote: Remote): Future[Transformer] = {
    Future.successful(this)
  }

  def prepare(modelIds: List[UUID])(implicit remote: Remote): Future[Transformer] = {
    Future.successful(this)
  }

  def transform(entity: ModelEntity)(implicit remote: Remote): Future[Transformer]

  def exit()(implicit remote: Remote): Future[Result]

}
