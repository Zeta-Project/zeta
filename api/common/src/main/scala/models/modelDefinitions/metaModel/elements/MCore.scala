package models.modelDefinitions.metaModel.elements

import scala.annotation.tailrec
import scala.collection.immutable.Seq

/**
 * Immutable domain model for the MCore (meta)metamodel
 */

/** a helper type to combine classes and refs */
trait ClassOrRef {
  val name: String
}

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
 * @param _superTypes the supertypes of the MClass (by-Name)
 * @param _inputs the incoming MReferences (by-Name)
 * @param _outputs the outgoing MReferences (by-Name)
 * @param attributes the attributes of the MClass
 */
class MClass(
    val name: String,
    val abstractness: Boolean,
    _superTypes: => Seq[MClass],
    _inputs: => Seq[MLinkDef],
    _outputs: => Seq[MLinkDef],
    val attributes: Seq[MAttribute]
) extends MObject with ClassOrRef {
  lazy val superTypes = _superTypes
  lazy val inputs = _inputs
  lazy val outputs = _outputs

  /**
   * convenience method for updating relationships
   * @param _superTypes possible update of supertypes
   * @param _inputs possible update of inputs
   * @param _outputs possible update of outputs
   * @return the new MClass
   */
  def updateLinks(
    _superTypes: => Seq[MClass] = superTypes,
    _inputs: => Seq[MLinkDef] = inputs,
    _outputs: => Seq[MLinkDef] = outputs
  ): MClass = new MClass(name, abstractness, _superTypes, _inputs, _outputs, attributes)

  /**
   * convenience method for updating attributes
   * @param _attributes the updated attributes
   * @return the new MClass
   */
  def updateAttributes(_attributes: Seq[MAttribute]): MClass = new MClass(name, abstractness, superTypes, inputs, outputs, _attributes)

  /**
   * @return String
   */
  override def toString: String = {
    val superNames = for {e <- superTypes} yield e.name
    val inputsNames = for {e <- inputs} yield e.mType.name
    val outputsNames = for {e <- outputs} yield e.mType.name
    s"MClass($name, $abstractness, $superNames, $inputsNames, $outputsNames, $attributes)"
  }

  /**
   * represents the supertype hierarchy of this particular MClass
   */
  lazy val typeHierarchy: Seq[MClass] = getSuperHierarchy(Seq(this), this.superTypes)

  /**
   * Determines the supertype hierarchy of this particular MClass
   * @param acc accumulated value of recursion
   * @param inspect the next MClass to check
   * @return MClasses that take part in the supertype hierarchy
   */
  private def getSuperHierarchy(acc: Seq[MClass], inspect: Seq[MClass]): Seq[MClass] = {
    inspect.foldLeft(acc) { (a, m) =>
      if (a.exists(_.name == m.name)) {
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
    cls => cls.inputs.exists(link => link.mType.name == inputName)
  )

  /**
   * Checks if certain output relationship is allowed, also based on supertypes
   * @param outputName the name of the outgoing relationship
   * @return true if the relationship is defined within the type hierarchy
   */
  def typeHasOutput(outputName: String): Boolean = typeHierarchy.exists(
    cls => cls.outputs.exists(link => link.mType.name == outputName)
  )

  /**
   * Checks if MClass has a certain supertype
   * @param superName the name of the supertype in question
   * @return true if the given name belongs to a supertype
   */
  def typeHasSuperType(superName: String): Boolean = typeHierarchy.exists(
    cls => cls.name == superName
  )

  /**
   * Returns all effective (inherited) MAttributes of this MClass
   * @return the MAttributes
   */
  def getTypeMAttributes: Seq[MAttribute] = typeHierarchy.flatMap(_.attributes)

  /**
   * Finds an MAttribute within supertypes
   * @param attributeName the name of the attribute to find
   * @return the MAttribute, if present
   */
  def findMAttribute(attributeName: String): Option[MAttribute] = {
    @tailrec
    def find(remaining: List[MClass]): Option[MAttribute] = remaining match {
      case Nil => None
      case head :: tail => {
        val attribute = head.attributes.find(_.name == attributeName)
        if (attribute.isDefined) attribute else find(tail)
      }
    }
    find(typeHierarchy.toList)
  }

}

/** Companion / Extractor */
object MClass {

  def apply(
    name: String,
    abstractness: Boolean,
    superTypes: Seq[MClass],
    inputs: Seq[MLinkDef],
    outputs: Seq[MLinkDef],
    attributes: Seq[MAttribute]
  ): MClass = new MClass(name, abstractness, superTypes, inputs, outputs, attributes)

  def unapply(m: MClass): Option[(String, Boolean, Seq[MClass], Seq[MLinkDef], Seq[MLinkDef], Seq[MAttribute])] =
    Some((m.name, m.abstractness, m.superTypes, m.inputs, m.outputs, m.attributes))
}

/**
 * The MReference implementation
 * @param name the name of the MReference instance
 * @param sourceDeletionDeletesTarget whether source deletion leads to removal of target
 * @param targetDeletionDeletesSource whether target deletion leads to removal of source
 * @param _source the incoming MClass relationships
 * @param _target the outgoing MClass relationships
 * @param attributes the attributes of the MReference
 */
class MReference(
    val name: String,
    val sourceDeletionDeletesTarget: Boolean,
    val targetDeletionDeletesSource: Boolean,
    _source: => Seq[MLinkDef],
    _target: => Seq[MLinkDef],
    val attributes: Seq[MAttribute]
) extends MObject with ClassOrRef {
  lazy val source = _source
  lazy val target = _target

  /**
   * convenience method for updating relationships
   * @param _source possible update for source
   * @param _target possible update for target
   * @return the new MReference
   */
  def updateLinks(_source: => Seq[MLinkDef] = source, _target: => Seq[MLinkDef] = target): MReference =
    new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, _source, _target, attributes)

  /** convenience method for updating attributes */
  def updateAttributes(_attributes: Seq[MAttribute]): MReference =
    new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, _attributes)

  /**
   * @return String
   */
  override def toString: String = {
    val sourceNames = for {e <- source} yield e.mType.name
    val targetNames = for {e <- target} yield e.mType.name
    s"MReference($name, $sourceDeletionDeletesTarget, $targetDeletionDeletesSource, $sourceNames, $targetNames, $attributes)"
  }
}

/** Companion / Extractor */
object MReference {

  def apply(
    name: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MLinkDef],
    target: Seq[MLinkDef],
    attributes: Seq[MAttribute]
  ): MReference = new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, attributes)

  def unapply(m: MReference): Option[(String, Boolean, Boolean, Seq[MLinkDef], Seq[MLinkDef], Seq[MAttribute])] =
    Some((m.name, m.sourceDeletionDeletesTarget, m.targetDeletionDeletesSource, m.source, m.target, m.attributes))
}

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
case class MLinkDef(
    mType: ClassOrRef,
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

/**
 * An Enum Symbol
 * @param name name of the symbol
 * @param _attributeType backreference to the MEnum instance
 */
class EnumSymbol(val name: String, _attributeType: => MEnum) extends AttributeValue {

  lazy val attributeType = _attributeType

  override def equals(other: Any): Boolean = other match {
    case that: EnumSymbol =>
      other.isInstanceOf[EnumSymbol] && name == that.name && attributeType == that.attributeType
    case _ => false
  }

  override def hashCode: Int = 41 * (41 + name.hashCode) // + attributeType.hashCode

  override def toString: String = s"EnumSymbol($name)"
}

object EnumSymbol {
  def apply(name: String, attributeType: MEnum): EnumSymbol = new EnumSymbol(name, attributeType)

  def unapply(e: EnumSymbol): Option[(String, MEnum)] = Some((e.name, e.attributeType))
}

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
