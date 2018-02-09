package de.htwg.zeta.parser

import scala.util.parsing.combinator.JavaTokenParsers

trait UnorderedParser extends JavaTokenParsers {

  sealed abstract class ParseConf[+A](min: Int, max: Int) {
    if (min < 0 || max < 0 || min > max) {
      throw new IllegalArgumentException(s"Illegal min/max-values: min=$min, max=$max")
    }

    val parser: Parser[A]

    private def toErrorString(mod: String, times: Int) = s"failed parsing $parser. Element occurred $mod than $times times"

    def checkSize(size: Int): Option[String] = {
      if (size < min) {
        Some(toErrorString("less", min))
      } else if (size > max) {
        Some(toErrorString("more", max))
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


  private def checkParsersUnique(parsers: List[Parser[_]]): Unit = {
    parsers.diff(parsers.distinct) match {
      case Nil =>
      case nonEmpty: List[Parser[_]] =>
        throw new IllegalArgumentException(
          "Each parser can only be configured once in an unordered context. duplicate parsers: " +
            nonEmpty.distinct.mkString(", ")
        )
    }
  }


  def unordered[A](configs: ParseConf[A]*): Parser[List[A]] = {
    checkParsersUnique(configs.map(_.parser).toList)

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