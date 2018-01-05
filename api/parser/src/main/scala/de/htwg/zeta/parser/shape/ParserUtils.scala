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

  def unorderedOld[B: TypeTag](parserConfs: List[ParseConfiguration[UnorderedParseResult[B]]]): Parser[List[B]] = {
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

  sealed abstract class ParseConf[+A](min: Int, max: Int) {
    val parser: Parser[A]

    private def toErrorString(mod: String) = s"failed parsing $parser. Element occured $mod than $max times"

    def checkSize(size: Int): Option[String] = {
      if (size < min) {
        Some(toErrorString("less"))
      } else if (size > max) {
        Some(toErrorString("more"))
      } else {
        None
      }
    }
  }
  case class MinParseConf[A](min: Int, parser: Parser[A]) extends ParseConf[A](min, Int.MaxValue)
  case class MaxParseConf[A](max: Int, parser: Parser[A]) extends ParseConf[A](0, max)
  case class ExactParseConf[A](exact: Int, parser: Parser[A]) extends ParseConf[A](exact, exact)
  case class RangeParseConf[A](min: Int, max: Int, parser: Parser[A]) extends ParseConf[A](min, max)

  def min[A](min: Int, parser: Parser[A]): ParseConf[A] = MinParseConf(min, parser)

  def max[A](max: Int, parser: Parser[A]): ParseConf[A] = MaxParseConf(max, parser)

  def exact[A](exact: Int, parser: Parser[A]): ParseConf[A] = ExactParseConf(exact, parser)

  def range[A](min: Int, max: Int, parser: Parser[A]): ParseConf[A] = RangeParseConf(min, max, parser)


  private def checkParsersUnique(parsers: Seq[Parser[_]]): Unit = {
    parsers.diff(parsers.distinct) match {
      case Seq() =>
      case nonEmpty: Parser[_] =>
        throw new IllegalArgumentException(
          "Each parser can only be configured once in an unordered context. duplicate parsers: " +
            nonEmpty.distinct.mkString(", ")
        )
    }
  }


  def unordered[A](configs: ParseConf[A]*): Parser[List[A]] = {
    checkParsersUnique(configs.map(_.parser))

    def parser: Parser[(ParseConf[A], A)] = configs.map(p => p.parser.map(a => (p, a))).reduceLeft(_ | _)

    def checkSize(resultList: List[(ParseConf[A], A)]): PartialFunction[ParseConf[A], String] = {
      object Check {
        def unapply(conf: ParseConf[A]): Option[String] = {
          val res = resultList.collect { case (`conf`, a) => a }
          conf.checkSize(res.size)
        }
      }
      val func: PartialFunction[ParseConf[A], String] = {
        case Check(ret) => ret
      }
      func
    }

    rep(parser).flatMap { resultList =>
      val pf = checkSize(resultList)
      configs.collectFirst(pf) match {
        case Some(f) => failure(f)
        case None => success(resultList.map(_._2))
      }
    }
  }
}