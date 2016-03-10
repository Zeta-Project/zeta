package models.modelDefinitions.metaModel

import models.modelDefinitions.metaModel.elements._
import models.modelDefinitions.metaModel.elements.MCoreReads._
import models.modelDefinitions.metaModel.elements.MCoreWrites._

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._

import scala.collection.immutable._

case class MetaModel(
  name: String,
  elements: Map[String, MObject],
  uiState: String
) {
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
    (__ \ "name").read[String] and
    (__ \ "elements").read[Map[String, MObject]] and
      (__ \ "uiState").read[String]
    ) (MetaModel.apply _)

  implicit val writes = new Writes[MetaModel] {
    def writes(c: MetaModel): JsValue = Json.obj(
      "name" -> c.name,
      "elements" -> Json.toJson(c.elements.values.toList),
      "uiState" -> c.uiState
    )
  }
}