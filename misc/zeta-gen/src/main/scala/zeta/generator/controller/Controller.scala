package zeta.generator.controller

import zeta.generator.domain.model.{Model, ModelShortInfo}
import zeta.generator.network.OAuthClient
import zeta.generator.transformation.{TransformationError, TransformationSuccess, Transformation}

import scala.swing.Publisher
import scala.swing.event.Event

import scala.concurrent.ExecutionContext.Implicits.global

trait Controller extends Publisher {
  def generate(transformation: String, model: String): Unit
  def getTransformations: Seq[String]
  def getModels: Unit
}

object Controller {
  def apply() = new GeneratorController
}

class GeneratorController extends Controller {

  val client = new OAuthClient

  override def generate(transformation: String, model: String) {
    val t = Transformation.transformations.find(_.getCanonicalName == transformation)
    val m = client.getModel(model)
    m.map { optModel =>
      (optModel, t) match {
        case (Some(model), Some(transformation)) => run(transformation, model)
        case _ => publish(GeneratorLoadFailure)
      }
    }
  }

  private def run(transClass: Class[Transformation], model: Model) = {
    val transformation = transClass.newInstance()
    transformation.transform(model) match {
      case TransformationSuccess => publish(GeneratorSuccess)
      case TransformationError(e) => publish(GeneratorError(e.mkString(", ")))
    }
  }

  override def getTransformations = Transformation.transformations.map(t => t.getCanonicalName)

  override def getModels {
    client.getModelOverview.map { info =>
      publish(ShortInfoAvailable(info))
    }
  }

}

case class ShortInfoAvailable(info: Seq[ModelShortInfo]) extends Event
case object GeneratorLoadFailure extends Event
case object GeneratorSuccess extends Event
case class GeneratorError(error: String) extends Event
