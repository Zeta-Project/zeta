package modigen.util.domain

sealed abstract class MAttribute(override val name:String, override val upperBound: Int, override val lowerBound: Int) extends MObj with MBounds{
  val default:Any
  var uniqueLocal = false
  var singleAssignment = false
  var uniqueGlobal = false
  var ordered = false
  var transient = false
  var constant = false
  val _type:String
}

case class MAttributeNumber(override val name:String, override val upperBound: Int, override val lowerBound: Int, override val default:Double = 0) extends MAttribute(name, upperBound, lowerBound){
  val _type = "Number"
}

case class MAttributeString(override val name:String, override val upperBound: Int, override val lowerBound: Int, override val default:String = "") extends MAttribute(name, upperBound, lowerBound){
  val _type = "String"
}

case class MAttributeBoolean(override val name:String, override val upperBound: Int, override val lowerBound: Int, override val default:Boolean = false) extends MAttribute(name, upperBound, lowerBound){
  val _type = "Boolean"
}

abstract sealed class MAttributeMEnum(val enum:MEnum, override val name:String, override val upperBound: Int, override val lowerBound: Int) extends MAttribute(name, upperBound, lowerBound){
  val _type = enum.name
}

case class MAttributeMEnumNumber(override val enum:MEnumNumber, override val name:String, override val upperBound: Int, override val lowerBound: Int, override val default:Double = 0) extends MAttributeMEnum(enum, name, upperBound, lowerBound){
  if (!enum.values.contains(default)) throw new IllegalArgumentException("Default value is not part of specified M_Enum")
}

case class MAttributeMEnumString(override val enum:MEnumString, override val name:String, override val upperBound: Int, override val lowerBound: Int, override val default:String = "") extends MAttributeMEnum(enum, name, upperBound, lowerBound){
  if (!enum.values.contains(default)) throw new IllegalArgumentException("Default value is not part of specified M_Enum")
}