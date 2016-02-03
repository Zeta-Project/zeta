package models.metaModel.mCore

import scala.collection.immutable._

trait ClassOrRef {
  val name: String
}

case class MetaModelDefinition(name: String, mObjects: Map[String, MObject]) {
  //  lazy val mClasses = mObjects.collect { case c: MClass => c }
  //  lazy val mReferences = mObjects.collect { case r: MReference => r }
  //  lazy val mEnums = mObjects.collect { case e: MEnum => e }
}

trait MObject {
  val name: String
}

trait MBounds {
  val upperBound: Int
  val lowerBound: Int
}

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

  def updateLinks(
    _superTypes: => Seq[MClass] = superTypes,
    _inputs: => Seq[MLinkDef] = inputs,
    _outputs: => Seq[MLinkDef] = outputs) =
    new MClass(name, abstractness, _superTypes, _inputs, _outputs, attributes)

  def updateAttributes(_attributes: Seq[MAttribute]) =
    new MClass(name, abstractness, superTypes, inputs, outputs, _attributes)

  override def toString = {
    val superNames = for (e <- superTypes) yield e.name
    val inputsNames = for (e <- inputs) yield e.mType.name
    val outputsNames = for (e <- outputs) yield e.mType.name
    s"MClass($name, $abstractness, $superNames, $inputsNames, $outputsNames, $attributes)"
  }
}

object MClass {

  def apply(
    name: String,
    abstractness: Boolean,
    superTypes: Seq[MClass],
    inputs: Seq[MLinkDef],
    outputs: Seq[MLinkDef],
    attributes: Seq[MAttribute]
  ) = new MClass(name, abstractness, superTypes, inputs, outputs, attributes)

  def unapply(m: MClass): Option[(String, Boolean, Seq[MClass], Seq[MLinkDef], Seq[MLinkDef], Seq[MAttribute])] =
    Some((m.name, m.abstractness, m.superTypes, m.inputs, m.outputs, m.attributes))
}

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

  def updateLinks(_source: => Seq[MLinkDef] = source, _target: => Seq[MLinkDef] = target) =
    new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, _source, _target, attributes)

  def updateAttributes(_attributes: Seq[MAttribute]) =
    new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, _attributes)

  override def toString = {
    val sourceNames = for (e <- source) yield e.mType.name
    val targetNames = for (e <- target) yield e.mType.name
    s"MReference($name, $sourceDeletionDeletesTarget, $targetDeletionDeletesSource, $sourceNames, $targetNames, $attributes)"
  }
}

object MReference {

  def apply(
    name: String,
    sourceDeletionDeletesTarget: Boolean,
    targetDeletionDeletesSource: Boolean,
    source: Seq[MLinkDef],
    target: Seq[MLinkDef],
    attributes: Seq[MAttribute]
  ) = new MReference(name, sourceDeletionDeletesTarget, targetDeletionDeletesSource, source, target, attributes)

  def unapply(m: MReference): Option[(String, Boolean, Boolean, Seq[MLinkDef], Seq[MLinkDef], Seq[MAttribute])] =
    Some((m.name, m.sourceDeletionDeletesTarget, m.targetDeletionDeletesSource, m.source, m.target, m.attributes))
}

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
  lowerBound: Int
) extends MObject with MBounds

case class MLinkDef(
  mType: ClassOrRef,
  upperBound: Int,
  lowerBound: Int,
  deleteIfLower: Boolean
) extends MBounds

// type system for attribute types

sealed trait AttributeType

sealed trait AttributeValue

object ScalarType {

  case object String extends AttributeType

  case object Bool extends AttributeType

  case object Int extends AttributeType

  case object Double extends AttributeType

}

case class MEnum(
  name: String,
  values: Seq[EnumSymbol]
) extends MObject with AttributeType

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

class EnumSymbol(val name: String, _attributeType: => MEnum) extends AttributeValue {

  lazy val attributeType = _attributeType

  override def equals(other: Any): Boolean = other match {
    case that: EnumSymbol =>
      other.isInstanceOf[EnumSymbol] && name == that.name && attributeType == that.attributeType
    case _ => false
  }

  override def hashCode: Int = 41 * (41 + name.hashCode)// + attributeType.hashCode

  override def toString = s"EnumSymbol($name)"
}

object EnumSymbol {
  def apply(name: String, attributeType: MEnum) = new EnumSymbol(name, attributeType)

  def unapply(e: EnumSymbol): Option[(String, MEnum)] = Some((e.name, e.attributeType))
}