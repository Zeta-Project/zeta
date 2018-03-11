package de.htwg.zeta.common.models.modelDefinitions.concept

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept.MetaModelTraverseWrapper
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.MEnum
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.MEnum.EnumMap
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass.ClassMap
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass.MClassTraverseWrapper
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.Method
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.Method.MethodMap
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference.ReferenceMap


/**
 * Immutable container for Concept (formerly named MetaModel) definitions
 *
 * @param classes    the classes of the actual MetaModel data
 * @param references the object graph containing the actual MetaModel data
 * @param enums      the object graph containing the actual MetaModel data
 * @param methods    the object graph containing the actual MetaModel data
 * @param uiState    the ui-state of the browser client. Location is debatable
 */
case class Concept(
    classes: Seq[MClass],
    references: Seq[MReference],
    enums: Seq[MEnum],
    attributes: Seq[MAttribute],
    methods: Seq[Method],
    uiState: String
) extends ClassMap with ReferenceMap with EnumMap with AttributeMap with MethodMap {

  /** A wrapper for bidirectional traversing of the immutable MetaModel. */
  lazy val traverseWrapper = MetaModelTraverseWrapper(this)

}

object Concept {

  val empty: Concept = {
    Concept(
      classes = Seq.empty,
      references = Seq.empty,
      enums = Seq.empty,
      attributes = Seq.empty,
      methods = Seq.empty,
      uiState = ""
    )
  }

  case class MetaModelTraverseWrapper(value: Concept) {

    def classes: Map[String, MClassTraverseWrapper] = {
      value.classMap.map {
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

}
