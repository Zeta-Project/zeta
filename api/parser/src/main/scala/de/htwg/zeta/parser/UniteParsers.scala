package de.htwg.zeta.parser

import scala.util.parsing.combinator.Parsers

trait UniteParsers extends Parsers {

  override type Elem = Char

  def include[T, UP <: UniteParsers](other: UP#Parser[T]) = new UniteParser[T, UP](other)

  protected class UniteParser[T, UP <: UniteParsers] private[UniteParsers](other: UP#Parser[T]) extends Parser[T] {
    override def apply(in: Input): ParseResult[T] = {
      val result: UP#ParseResult[T] = other(in)
      result match {
        case e: UP#Error => Error(e.msg, e.next)
        case f: UP#Failure => Failure(f.msg, f.next)
        case s: UP#Success[T] => Success(s.get, s.next)
      }
    }
  }
}
