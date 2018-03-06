package de.htwg.zeta.parser

import de.htwg.zeta.parser.check.Check.Id

case class Checker(message: (String) => String, check: () => List[Id])

case class ErrorChecker(checks: List[Checker]) {
  def add(check: Checker): ErrorChecker = copy(checks = checks :+ check)

  def run(): List[String] = checks.map(check => check.check() match {
    case Nil => None
    case errorIds => Some(check.message(errorIds.mkString(",")))
  }).collect({
    case Some(error) => error
  })
}

object ErrorChecker {
  def apply(): ErrorChecker = new ErrorChecker(List())
}
