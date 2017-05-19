package de.htwg.zeta.server.generator

import models.document.ModelEntity
import models.document.{ Repository => Documents }
import models.file.{ Repository => Files }
import models.remote.Remote

import scala.concurrent.Future

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
  def prepare(model: String)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    Future.successful(this)
  }

  def prepare(models: List[String])(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer] = {
    Future.successful(this)
  }

  def transform(entity: ModelEntity)(implicit documents: Documents, files: Files, remote: Remote): Future[Transformer]

  def exit()(implicit documents: Documents, files: Files, remote: Remote): Future[Result]
}
