package modigen.util.datavis.domain

sealed abstract class Operand
sealed abstract class Identifier extends Operand

case class MIdentifier(val identifier:String) extends Identifier{
  override def toString = identifier
}

case class StyleIdentifier(val selector:String, private val ident:String) extends Identifier{
  val identifier = ident.substring(1).replace(" ", "")
}

sealed abstract class Literal extends Operand

case class NumericLiteral(val double: Double) extends Literal{
  override def toString = double.toString
}

case class StringLiteral(val string: String) extends Literal{
  override def toString = string
}

case class BooleanLiteral(val boolean: Boolean) extends Literal{
  override def toString = boolean.toString
}
