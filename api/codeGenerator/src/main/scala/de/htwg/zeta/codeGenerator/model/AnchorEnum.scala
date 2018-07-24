package de.htwg.zeta.codeGenerator.model


sealed trait AnchorEnum {
  val name: String
  def lower: String = name.toLowerCase

  override def toString: String = name
}

object AnchorEnum {
  case object Period extends AnchorEnum {
    val name = "Period"
  }

  case object Team extends AnchorEnum {
    val name = "Team"
  }
}