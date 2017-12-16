package de.htwg.zeta.parser.shape

import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeOf
import scala.util.parsing.combinator.JavaTokenParsers

trait ParserUtils extends JavaTokenParsers {

  private def checkN[U](check: U => Boolean): PartialFunction[U, U] = {
    object Check {
      def unapply(arg: U): Option[U] = Some(arg).filter(check)
    }
    val func: PartialFunction[U, U] = {
      case Check(ret) => ret
    }
    func
  }

  case class ParseConfiguration[A: TypeTag](t: Type, parser: Parser[A], count: Int)
  object ParseConfiguration {
    def apply[A: TypeTag, B >: A](parser: Parser[A], count: Int): ParseConfiguration[UnorderedParseResult[B]] =
      ParseConfiguration(typeOf[UnorderedParseResult[A]], parser.^^({
        f => UnorderedParseResult(f)
      }), count).asInstanceOf[ParseConfiguration[UnorderedParseResult[B]]]
  }

  case class UnorderedParseResult[A: TypeTag](elem: A, t: Type)
  object UnorderedParseResult {
    def apply[A: TypeTag](elem: A): UnorderedParseResult[A] = new UnorderedParseResult(elem, typeOf[UnorderedParseResult[A]])
  }

  def unordered[B: TypeTag](parserConfs: List[ParseConfiguration[UnorderedParseResult[B]]]): Parser[List[B]] = {
    def parser: Parser[UnorderedParseResult[B]] = parserConfs.map(p => p.parser).reduceLeft(_ | _)

    def errorMessage: String = {
      parserConfs.map(value => value.t.toString + " must appear " + value.count.toString + " times").mkString(" and ")
    }

    val value: Parser[List[UnorderedParseResult[B]]] = rep(parser) ^? (checkN {
      resultList =>
        resultList.lengthCompare(parserConfs.map(i => i.count).sum) == 0 && parserConfs.map {
          conf => resultList.count(result => conf.t <:< result.t) == conf.count
        }.reduceLeft(_ && _)
    }, _ => errorMessage)
    value.^^(list => list.map(l => l.elem))
  }

}