package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

trait ErrorCheck {
  def check(): List[Id]
}
