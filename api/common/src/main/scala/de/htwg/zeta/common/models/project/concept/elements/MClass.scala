package de.htwg.zeta.common.models.project.concept.elements

import scala.annotation.tailrec
import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept.MetaModelTraverseWrapper
import de.htwg.zeta.common.models.project.concept.elements.MAttribute.AttributeMap
import de.htwg.zeta.common.models.project.concept.elements.Method.MethodMap


/** The MClass implementation
 *
 * @param name                 the name of the MClass instance
 * @param abstractness         defines if the MClass is abstract
 * @param superTypeNames       the names of the supertypes of the MClass
 * @param inputReferenceNames  the names of the incoming MReferences
 * @param outputReferenceNames the names of the outgoing MReferences
 * @param attributes           the attributes of the MClass
 */
case class MClass(
    name: String,
    description: String,
    abstractness: Boolean,
    superTypeNames: Seq[String],
    inputReferenceNames: Seq[String],
    outputReferenceNames: Seq[String],
    attributes: Seq[MAttribute],
    methods: Seq[Method]
) extends AttributeMap with MethodMap

object MClass {

  def empty(name: String): MClass = MClass(
    name = name,
    description = "",
    abstractness = false,
    superTypeNames = Seq.empty,
    inputReferenceNames = Seq.empty,
    outputReferenceNames = Seq.empty,
    attributes = Seq.empty,
    methods = Seq.empty
  )

  trait ClassMap {

    val classes: Seq[MClass]

    /** Classes mapped to their own names. */
    final val classMap: Map[String, MClass] = Option(classes).fold(
      Map.empty[String, MClass]
    ) { classes =>
      classes.filter(Option(_).isDefined).map(clazz => (clazz.name, clazz)).toMap
    }

  }

  case class MClassTraverseWrapper(value: MClass, metaModel: MetaModelTraverseWrapper) {
    def superTypes: Seq[MClassTraverseWrapper] = {
      value.superTypeNames.map(name =>
        MClassTraverseWrapper(metaModel.classes(name).value, metaModel)
      )
    }

    /**
     * represents the supertype hierarchy of this particular MClass
     */
    lazy val typeHierarchy: Seq[MClassTraverseWrapper] = getSuperHierarchy(Seq(this), superTypes)

    /**
     * Determines the supertype hierarchy of this particular MClass
     *
     * @param acc     accumulated value of recursion
     * @param inspect the next MClass to check
     * @return MClasses that take part in the supertype hierarchy
     */
    private def getSuperHierarchy(acc: Seq[MClassTraverseWrapper], inspect: Seq[MClassTraverseWrapper]): Seq[MClassTraverseWrapper] = {
      inspect.foldLeft(acc) { (a, m) =>
        if (a.exists(_.value.name == m.value.name)) {
          a
        } else {
          getSuperHierarchy(acc :+ m, m.superTypes)
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
        cls => cls.value.inputReferenceNames.contains(inputName)
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
        cls => cls.value.outputReferenceNames.contains(outputName)
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
    def getTypeMAttributes: Seq[MAttribute] = {
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
      def find(remaining: Seq[MClass]): Option[MAttribute] = {
        if (remaining.isEmpty) {
          None
        } else {
          val head = remaining.head
          val attribute = head.attributes.find(_.name == attributeName)
          if (attribute.isDefined) attribute else find(remaining.filter(_ != head))
        }
      }

      find(typeHierarchy.map(_.value))
    }

  }

}
