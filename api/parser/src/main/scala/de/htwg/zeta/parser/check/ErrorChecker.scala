package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage

case class ErrorChecker[T](checks: List[ErrorCheck[T]]) {
  def add(check: ErrorCheck[T]): ErrorChecker[T] = copy(checks = checks :+ check)

  def run(): List[T] = checks.flatMap(_.check()).distinct.collect({
    case errors => errors
  })
}

object ErrorChecker {
  def apply(): ErrorChecker[ErrorMessage] = new ErrorChecker[ErrorMessage](List())
}
