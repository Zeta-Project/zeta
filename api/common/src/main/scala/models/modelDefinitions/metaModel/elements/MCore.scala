package models.modelDefinitions.metaModel.elements

import scala.annotation.tailrec
import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.MetaModelTraverseWrapper

/**
 * Immutable domain model for the MCore (meta)metamodel
 */


/**
 * the MObject trait
 */
sealed trait MObject {
  val name: String
}

/**
 * the MBounds trait
 */
trait MBounds {
  val upperBound: Int
  val lowerBound: Int
}

/**
 * The MClass implementation
 * @param name the name of the MClass instance
 * @param abstractness defines if the MClass is abstract
 * @param superTypeNames the names of the supertypes of the MClass
 * @param inputs the incoming MReferences
 * @param outputs the outgoing MReferences
 * @param attributes the attributes of the MClass
 */
case class MClass(
    name: String,
    abstractness: Boolean,
    superTypeNames: Seq[String],
    inputs: Seq[MReferenceLinkDef],
    outputs: Seq[MReferenceLinkDef],
    attributes: Seq[MAttribute]
) extends MObject

case class MClassTraverseWrapper(value: MClass, metaModel: MetaModelTraverseWrapper) {

  def superTypes: Seq[MClassTraverseWrapper] = value.superTypeNames.map(name =>
    MClassTraverseWrapper(metaModel.classes(name).value, metaModel)
  )

  /**
   * represents the supertype hierarchy of this particular MClass
   */
  lazy val typeHierarchy: Seq[MClassTraverseWrapper] = getSuperHierarchy(Seq(this), superTypes)

  /**
   * Determines the supertype hierarchy of this particular MClass
   * @param acc accumulated value of recursion
   * @param inspect the next MClass to check
   * @return MClasses that take part in the supertype hierarchy
   */
  private def getSuperHierarchy(acc: Seq[MClassTraverseWrapper], inspect: Seq[MClassTraverseWrapper]): Seq[MClassTraverseWrapper] = {
    inspect.foldLeft(acc) { (a, m) =>
      if (a.exists(_.value.name == m.value.name)) {
        a
      } else {
        getSuperHierarchy(m +: acc, m.superTypes)
      }
    }
  }.reverse

  /**
   * Checks if certain input relationship is allowed, also based on supertypes
   * @param inputName the name of the incoming relationship
   * @return true if the relationship is defined within the type hierarchy
   */
  def typeHasInput(inputName: String): Boolean = typeHierarchy.exists(
    cls => cls.value.inputs.exists(link => link.referenceName == inputName)
  )

  /**
   * Checks if certain output relationship is allowed, also based on supertypes
   * @param outputName the name of the outgoing relationship
   * @return true if the relationship is defined within the type hierarchy
   */
  def typeHasOutput(outputName: String): Boolean = typeHierarchy.exists(
    cls => cls.value.outputs.exists(link => link.referenceName == outputName)
  )

  /**
   * Checks if MClass has a certain supertype
   * @param superName the name of the supertype in question
   * @return true if the given name belongs to a supertype
   */
  def typeHasSuperType(superName: String): Boolean = typeHierarchy.exists(
    cls => cls.value.name == superName
  )

  /**
   * Returns all effective (inherited) MAttributes of this MClass
   * @return the MAttributes
   */
  def getTypeMAttributes: Seq[MAttribute] = typeHierarchy.flatMap(_.value.attributes)

  /**
   * Finds an MAttribute within supertypes
   * @param attributeName the name of the attribute to find
   * @return the MAttribute, if present
   */
  def findMAttribute(attributeName: String): Option[MAttribute] = {
    @tailrec
    def find(remaining: Seq[MClass]): Option[MAttribute] = remaining match {
      case Nil => None
      case head :: tail =>
        val attribute = head.attributes.find(_.name == attributeName)
        if (attribute.isDefined) attribute else find(tail)
    }
    find(typeHierarchy.map(_.value))
  }


}

/**
 * The MReference implementation
 * @param name the name of the MReference instance
 * @param sourceDeletionDeletesTarget whether source deletion leads to removal of target
 * @param targetDeletionDeletesSource whether target deletion leads to removal of source
 * @param source the incoming MClass relationships
 * @param target the outgoing MClass relationships
 * @param attributes the attributes of the MReference
 */
case class MReference(
    name: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MClassLinkDef],
    target: Seq[MClassLinkDef],
    attributes: Seq[MAttribute]
) extends MObject


/**
 * The MAttribute implementation
 * @param name the name of the MAttribute instance
 * @param globalUnique globalUnique flag
 * @param localUnique localUnique flag
 * @param `type` the attribute type
 * @param default the attribute's default value
 * @param constant constant flag
 * @param singleAssignment single assignment flag
 * @param expression a composed expression
 * @param ordered ordered flag
 * @param transient transient flag
 * @param upperBound the upper bound
 * @param lowerBound the lower bound
 */
case class MAttribute(
    name: String,
    globalUnique: Boolean,
    localUnique: Boolean,
    `type`: AttributeType,
    default: AttributeValue,
    constant: Boolean,
    singleAssignment: Boolean,
    expression: String,
    ordered: Boolean,
    transient: Boolean,
    upperBound: Int,
    lowerBound: Int)
  extends MObject with MBounds

/** MLinkDef implementation */
case class MClassLinkDef(
    className: String,
    upperBound: Int,
    lowerBound: Int,
    deleteIfLower: Boolean)
  extends MBounds

/** MLinkDef implementation */
case class MReferenceLinkDef(
    referenceName: String,
    upperBound: Int,
    lowerBound: Int,
    deleteIfLower: Boolean)
  extends MBounds



/**
 * The MEnum implementation
 * @param name the name of the MENum instance
 * @param values the symbols
 */
case class MEnum(
    name: String,
    values: Seq[EnumSymbol])
  extends MObject with AttributeType

object MEnum {

  def buildFrom(name: String, values: Seq[String]): MEnum = {
    MEnum(name, values.map(value => EnumSymbol(value, name)))
  }

}



/**
 * An Enum Symbol
 * @param name name of the symbol
 * @param enumName name of the the belonging MEnum
 */
case class EnumSymbol(name: String, enumName: String) extends AttributeValue



sealed trait AttributeType

sealed trait AttributeValue

/** Marker objects */
object ScalarType {
  case object String extends AttributeType
  case object Bool extends AttributeType
  case object Int extends AttributeType
  case object Double extends AttributeType
}

/** available types */
object ScalarValue {

  case class MString(value: String) extends AttributeValue {
    val attributeType = ScalarType.String
  }

  case class MBool(value: Boolean) extends AttributeValue {
    val attributeType = ScalarType.Bool
  }

  case class MInt(value: Int) extends AttributeValue {
    val attributeType = ScalarType.Int
  }

  case class MDouble(value: Double) extends AttributeValue {
    val attributeType = ScalarType.Double
  }

}
