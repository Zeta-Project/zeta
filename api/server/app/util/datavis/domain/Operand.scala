package util.datavis.domain

sealed abstract class Operand
sealed abstract class Identifier extends Operand

case class MIdentifier(identifier: String) extends Identifier {
  override def toString = identifier
}

case class StyleIdentifier(selector: String, private val ident: String) extends Identifier {
  val identifier = ident.substring(1).replace(" ", "")
}

sealed abstract class Literal extends Operand

case class NumericLiteral(double: Double) extends Literal {
  override def toString = double.toString
}

case class StringLiteral(string: String) extends Literal {
  override def toString = string
}

case class BooleanLiteral(boolean: Boolean) extends Literal {
  override def toString = boolean.toString
}
