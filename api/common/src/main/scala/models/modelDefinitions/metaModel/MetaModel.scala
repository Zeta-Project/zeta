package models.modelDefinitions.metaModel

import models.modelDefinitions.metaModel.elements._
import models.modelDefinitions.metaModel.elements.MCoreReads._
import models.modelDefinitions.metaModel.elements.MCoreWrites._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.json._

import scala.collection.immutable._

/**
 * Immutable container for metamodel definitions
 * @param name the name of the metamodel
 * @param elements the object graph containing the actual metamodel data
 * @param uiState the uistate of the browser client. Location is debatable
 */
case class MetaModel(
    name: String,
    elements: Map[String, MObject],
    uiState: String
) {
  /**
   * Looks for a specific MCLass
   * @param name the name of the MClass
   * @return the MClass, if present
   */
  def getMClass(name: String): Option[MClass] = elements.get(name) match {
    case Some(c: MClass) => Some(c)
    case _ => None
  }

  /**
   * Looks for a specific MReference
   * @param name the name of the MReference
   * @return the MReference, if present
   */
  def getMReference(name: String): Option[MReference] = elements.get(name) match {
    case Some(r: MReference) => Some(r)
    case _ => None
  }

  /**
   * Looks for a specific MEnum
   * @param name the name of the MEnum
   * @return the MEnum, if present
   */
  def getMEnum(name: String): Option[MEnum] = elements.get(name) match {
    case Some(e: MEnum) => Some(e)
    case _ => None
  }

  /**
   * Some convenience methods that check the presence of certain elements...
   */
  def containsMClass(name: String) = getMClass(name).isDefined
  def containsMReference(name: String) = getMReference(name).isDefined
  def containsMEnum(name: String) = getMEnum(name).isDefined

  /**
   * Checks if MClass is subtype of another MClass
   * @param className the name of the MClass
   * @param superClassName the name of the super class in question
   * @return true if there is a inheritance relationship
   */
  def mClassInstanceOf(className: String, superClassName: String): Boolean = {
    (getMClass(className), getMClass(superClassName)) match {
      case (Some(c1: MClass), Some(c2: MClass)) => {
        if (c1.name == c2.name) true else searchSuperType(c1.superTypes.toList, c2)
      }
      case _ => false
    }
  }

  /**
   * Checks if supertype hierarchy contains MClass
   * @param superTypes list of super types
   * @param toFind the MClass to find
   * @return true if MClass in question is part of the hierarchy
   */
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
  )(MetaModel.apply _)

  implicit val writes = new Writes[MetaModel] {
    def writes(c: MetaModel): JsValue = Json.obj(
      "name" -> c.name,
      "elements" -> Json.toJson(c.elements.values.toList),
      "uiState" -> c.uiState
    )
  }
}
