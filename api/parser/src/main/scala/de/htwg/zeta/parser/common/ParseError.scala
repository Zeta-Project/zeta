package de.htwg.zeta.parser.common

case class ParseError(
    message: String,
    offset: Int,
    position: (Int, Int)
)
