package de.htwg.zeta.server.util.domain

sealed abstract class MAttribute(
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MObj
  with MBounds {

  val default: Option[Any]
  val _type: String
}

case class MAttributeNumber(
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    override val default: Option[Double] = None,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttribute(name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  val _type = "Number"
}

case class MAttributeString(
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    override val default: Option[String] = None,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttribute(name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  val _type = "String"
}

case class MAttributeBoolean(
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    override val default: Option[Boolean] = None,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttribute(name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  val _type = "Boolean"
}

abstract sealed class MAttributeMEnum(
    val enum: MEnum,
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttribute(name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  val _type = enum.name
}

case class MAttributeMEnumNumber(
    override val enum: MEnumNumber,
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    override val default: Option[Double] = None,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttributeMEnum(enum, name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  default match {
    case Some(d) => if (!enum.values.contains(d)) throw new IllegalArgumentException("Default value is not part of specified M_Enum")
    case _ =>
  }
}

case class MAttributeMEnumString(
    override val enum: MEnumString,
    override val name: String,
    override val upperBound: Int,
    override val lowerBound: Int,
    override val default: Option[String] = None,
    uniqueLocal: Boolean = false,
    uniqueGlobal: Boolean = false,
    singleAssignment: Boolean = false,
    ordered: Boolean = false,
    transient: Boolean = false,
    constant: Boolean = false)
  extends MAttributeMEnum(enum, name, upperBound, lowerBound, uniqueLocal, uniqueGlobal, singleAssignment, ordered, transient, constant) {

  default match {
    case Some(d) => if (!enum.values.contains(d)) throw new IllegalArgumentException("Default value is not part of specified M_Enum")
    case _ =>
  }
}
