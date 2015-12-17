package util.domain

abstract sealed class MEnum(override val name:String) extends MObj{
  val values:List[Any]
}

case class MEnumNumber(override val values:List[Double], override val name:String) extends MEnum(name)
case class MEnumString(override val values:List[String], override val name:String) extends MEnum(name)