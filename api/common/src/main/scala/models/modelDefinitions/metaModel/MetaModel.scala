package models.modelDefinitions.metaModel

import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MClassTraverseWrapper
// import models.modelDefinitions.metaModel.elements.MCoreReads.mObjectMapReads
// import models.modelDefinitions.metaModel.elements.MCoreWrites.mObjectWrites
import models.modelDefinitions.metaModel.elements.MEnum
import models.modelDefinitions.metaModel.elements.MReference
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Reads.functorReads
import play.api.libs.json.Writes
import play.api.libs.json.__

/**
 * Immutable container for MetaModel definitions
 *
 * @param name       the name of the MetaModel
 * @param classes    the classes of the actual MetaModel data
 * @param references the object graph containing the actual MetaModel data
 * @param enums      the object graph containing the actual MetaModel data
 * @param uiState    the ui-state of the browser client. Location is debatable
 */
case class MetaModel(
    name: String,
    classes: Map[String, MClass],
    references: Map[String, MReference],
    enums: Map[String, MEnum],
    uiState: String
) {

  /** A wrapper for bidirectional traversing of the immutable MetaModel. */
  lazy val traverseWrapper = MetaModelTraverseWrapper(this)

}

/*
object MetaModel {

  implicit val reads: Reads[MetaModel] = null

   (
     (__ \ "name").read[String] and
       (__ \ "elements").read[Map[String, MObject]] and
       (__ \ "uiState").read[String]
     ) (MetaModel.apply _)

  implicit val writes = null

  new Writes[MetaModel] {
     def writes(c: MetaModel): JsValue = {
       Json.obj(
         "name" -> c.name,
         "elements" -> Json.toJson(c.elements.values.toList),
         "uiState" -> c.uiState
       )
     }
   }

} */

case class MetaModelTraverseWrapper(value: MetaModel) {

  def classes: Map[String, MClassTraverseWrapper] = {
    value.classes.map {
      case (name: String, clazz: MClass) => (name, MClassTraverseWrapper(clazz, this))
    }
  }

  /**
   * Checks if MClass is subtype of another MClass
   *
   * @param className      the name of the MClass
   * @param superClassName the name of the super class in question
   * @return true if there is a inheritance relationship
   */
  def mClassInstanceOf(className: String, superClassName: String): Boolean = {
    (classes.get(className), classes.get(superClassName)) match {
      case (Some(c1: MClassTraverseWrapper), Some(c2: MClassTraverseWrapper)) =>
        if (c1.value.name == c2.value.name) true else searchSuperType(c1.superTypes.toList, c2)
      case _ => false
    }
  }

  /**
   * Checks if supertype hierarchy contains MClass
   *
   * @param superTypes list of super types
   * @param toFind     the MClass to find
   * @return true if MClass in question is part of the hierarchy
   */
  private def searchSuperType(superTypes: List[MClassTraverseWrapper], toFind: MClassTraverseWrapper): Boolean = {
    superTypes match {
      case Nil =>
        val levelUp = superTypes.filter(_.superTypes.nonEmpty)
        if (levelUp.isEmpty) {
          false
        } else {
          (for {s <- levelUp} yield {
            searchSuperType(s.superTypes.toList, toFind)
          }).exists(b => b)
        }
      case head :: tail => if (head.value.name == toFind.value.name) true else searchSuperType(tail, toFind)
    }
  }

}
