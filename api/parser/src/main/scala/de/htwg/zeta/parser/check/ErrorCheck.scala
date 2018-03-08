package de.htwg.zeta.parser.check

trait ErrorCheck[T] {
  def check(): List[T]
}

object ErrorCheck {
  type ErrorMessage = String
}
