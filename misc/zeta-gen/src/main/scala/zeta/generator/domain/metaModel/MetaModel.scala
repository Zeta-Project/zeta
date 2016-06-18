package zeta.generator.domain.metaModel

import play.api.libs.json._
import play.api.libs.functional.syntax._
import zeta.generator.domain.metaModel.elements.{MEnum, MReference, MClass, MObject}
import zeta.generator.domain.metaModel.elements.MCoreReads._
import scala.collection.immutable._

case class MetaModel(id: String, name: String, elements: Map[String, MObject]) {

  def getMClass(name: String): Option[MClass] = elements.get(name) match {
    case Some(c: MClass) => Some(c)
    case _ => None
  }

  def getMReference(name: String): Option[MReference] = elements.get(name) match {
    case Some(r: MReference) => Some(r)
    case _ => None
  }

  def getMEnum(name: String): Option[MEnum] = elements.get(name) match {
    case Some(e: MEnum) => Some(e)
    case _ => None
  }

  def containsMClass(name: String) = getMClass(name).isDefined

  def containsMReference(name: String) = getMReference(name).isDefined

  def containsMEnum(name: String) = getMEnum(name).isDefined

  def mClassInstanceOf(className: String, superClassName: String): Boolean = {
    (getMClass(className), getMClass(superClassName)) match {
      case (Some(c1: MClass), Some(c2: MClass)) => {
        if (c1.name == c2.name) true else searchSuperType(c1.superTypes.toList, c2)
      }
      case _ => false
    }
  }

  private def searchSuperType(superTypes: List[MClass], toFind: MClass): Boolean = {
    superTypes match {
      case Nil => {
        val levelUp = superTypes.filter(_.superTypes.size > 0)
        if (levelUp.isEmpty) false
        else (for (s <- levelUp) yield {
          searchSuperType(s.superTypes.toList, toFind)
        }).exists(b => b)
      }
      case head :: tail => if (head.name == toFind.name) true else searchSuperType(tail, toFind)
    }
  }

}

object MetaModel {
  implicit val reads: Reads[MetaModel] = (
    (__ \ "id").read[String] and
      (__ \ "metaModel" \ "name").read[String] and
      (__ \ "metaModel" \ "elements").read[Map[String, MObject]]
    ) (MetaModel.apply _)
}
