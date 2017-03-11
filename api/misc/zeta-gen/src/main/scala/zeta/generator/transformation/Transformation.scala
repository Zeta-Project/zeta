package zeta.generator.transformation

import com.typesafe.config.ConfigFactory
import zeta.generator.domain.model.Model
import collection.JavaConversions._
import scala.collection.immutable._

trait Transformation {
  def transform(input: Model): TransformationResult
  final val name = s"${getClass.getSimpleName} (${getClass.getName})"
}

object Transformation {
  lazy val transformations = getTransformationClasses(getTransformationNames)

  private def getTransformationNames: Seq[String] = {
    val conf = ConfigFactory.load()
    conf.getStringList("transformations").toList
  }

  private def getTransformationClasses(names: Seq[String]): Seq[Class[Transformation]] = {
    names.map {
      className => Class.forName(className)
    }.collect {
      case c => c.asInstanceOf[Class[Transformation]]
    }
  }
}

sealed trait TransformationResult
case object TransformationSuccess extends TransformationResult
case class TransformationError(errors: Seq[String]) extends TransformationResult
