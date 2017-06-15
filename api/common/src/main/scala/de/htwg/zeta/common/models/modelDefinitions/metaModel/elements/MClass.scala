package de.htwg.zeta.common.models.modelDefinitions.metaModel.elements

import scala.annotation.tailrec

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel.MetaModelTraverseWrapper
import play.api.libs.json.Json
import play.api.libs.json.OFormat


/** The MClass implementation
 *
 * @param name           the name of the MClass instance
 * @param abstractness   defines if the MClass is abstract
 * @param superTypeNames the names of the supertypes of the MClass
 * @param inputs         the incoming MReferences
 * @param outputs        the outgoing MReferences
 * @param attributes     the attributes of the MClass
 */
case class MClass(
    name: String,
    abstractness: Boolean,
    superTypeNames: Set[String],
    inputs: Set[MReferenceLinkDef],
    outputs: Set[MReferenceLinkDef],
    attributes: Set[MAttribute]
) extends MObject

object MClass {


  case class MClassTraverseWrapper(value: MClass, metaModel: MetaModelTraverseWrapper) {
    def superTypes: Set[MClassTraverseWrapper] = {
      value.superTypeNames.map(name =>
        MClassTraverseWrapper(metaModel.classes(name).value, metaModel)
      )
    }

    /**
     * represents the supertype hierarchy of this particular MClass
     */
    lazy val typeHierarchy: Set[MClassTraverseWrapper] = getSuperHierarchy(Set(this), superTypes)

    /**
     * Determines the supertype hierarchy of this particular MClass
     *
     * @param acc     accumulated value of recursion
     * @param inspect the next MClass to check
     * @return MClasses that take part in the supertype hierarchy
     */
    private def getSuperHierarchy(acc: Set[MClassTraverseWrapper], inspect: Set[MClassTraverseWrapper]): Set[MClassTraverseWrapper] = {
      inspect.foldLeft(acc) { (a, m) =>
        if (a.exists(_.value.name == m.value.name)) {
          a
        } else {
          getSuperHierarchy(acc + m, m.superTypes)
        }
      }
    }

    /**
     * Checks if certain input relationship is allowed, also based on supertypes
     *
     * @param inputName the name of the incoming relationship
     * @return true if the relationship is defined within the type hierarchy
     */
    def typeHasInput(inputName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.inputs.exists(link => link.referenceName == inputName)
      )
    }

    /**
     * Checks if certain output relationship is allowed, also based on supertypes
     *
     * @param outputName the name of the outgoing relationship
     * @return true if the relationship is defined within the type hierarchy
     */
    def typeHasOutput(outputName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.outputs.exists(link => link.referenceName == outputName)
      )
    }

    /**
     * Checks if MClass has a certain supertype
     *
     * @param superName the name of the supertype in question
     * @return true if the given name belongs to a supertype
     */
    def typeHasSuperType(superName: String): Boolean = {
      typeHierarchy.exists(
        cls => cls.value.name == superName
      )
    }

    /**
     * Returns all effective (inherited) MAttributes of this MClass
     *
     * @return the MAttributes
     */
    def getTypeMAttributes: Set[MAttribute] = {
      typeHierarchy.flatMap(_.value.attributes)
    }

    /**
     * Finds an MAttribute within supertypes
     *
     * @param attributeName the name of the attribute to find
     * @return the MAttribute, if present
     */
    def findMAttribute(attributeName: String): Option[MAttribute] = {
      @tailrec
      def find(remaining: Set[MClass]): Option[MAttribute] = {
        if (remaining.isEmpty) {
          None
        } else {
          val head = remaining.head
          val attribute = head.attributes.find(_.name == attributeName)
          if (attribute.isDefined) attribute else find(remaining - head)
        }
      }
      find(typeHierarchy.map(_.value))
    }

  }


  implicit val playJsonFormat: OFormat[MClass] = Json.format[MClass]

}
