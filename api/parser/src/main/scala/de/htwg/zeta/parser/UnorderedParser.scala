package de.htwg.zeta.parser

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.util.parsing.combinator.Parsers

trait UnorderedParser extends Parsers {

  final case class ParseConf[+A] private(min: Int, max: Int, parser: Parser[A]) {
    // noinspection ScalaStyle
    require(min >= 0, s"min must be greater than or equal to zero! Current: min=$min")
    require(max >= 1, s"max must be greater than zero! Current: max=$max")
    require(max >= min, s"max must be greater than or equal to min! Current: min=$min, max=$max")

    private[UnorderedParser] def getMutator[B >: A](): MutableConf[B] = {
      new MutableConf[B](this)
    }
  }


  private class MutableConf[A](val conf: ParseConf[A]) extends Parser[Unit] {

    private def toErrorString(mod: String, times: Int) = s"failed parsing ${conf.parser}. Element occurred $mod than $times times"

    private val buff = new ListBuffer[A]

    // noinspection ScalaStyle
    // scalastyle:ignore var.field
    private var lastFail: Option[NoSuccess] = None

    def apply(in: Input): ParseResult[Unit] = {
      conf.parser(in) match {
        case ns: NoSuccess =>
          lastFail = Some(ns)
          ns

        case Success(elem, next) =>
          buff += elem
          lastFail = None
          if (buff.length > conf.max) {
            Error(toErrorString("more", conf.max), in)
          } else {
            Success((), next)
          }
      }
    }

    def checkLTMin(): Boolean = {
      buff.length < conf.min
    }

    def getLastFail: List[NoSuccess] = {
      lastFail.toList
    }

    def failWithMin(in: Input): Failure = {
      Failure(toErrorString("less", conf.min), in)
    }

    def result(): List[A] = {
      buff.toList
    }

  }


  def min[A](min: Int, parser: Parser[A]): ParseConf[A] = ParseConf(min, Int.MaxValue, parser)

  def max[A](max: Int, parser: Parser[A]): ParseConf[A] = ParseConf(0, max, parser)

  def exact[A](exact: Int, parser: Parser[A]): ParseConf[A] = ParseConf(exact, exact, parser)

  def range[A](min: Int, max: Int, parser: Parser[A]): ParseConf[A] = ParseConf(min, max, parser)

  def optional[A](parser: Parser[A]): ParseConf[A] = range(0, 1, parser)

  def once[A](parser: Parser[A]): ParseConf[A] = exact(1, parser)

  def arbitrary[A](parser: Parser[A]): ParseConf[A] = min(0, parser)


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

  // noinspection ScalaStyle
  private def listOrParser[A](mutList: List[MutableConf[A]]): Parser[Unit] = {
    def parse(in: Input) = {
      val results: List[ParseResult[Unit]] = mutList.map(mut => mut(in)).sortWith { (a, b) => a.next.offset >= b.next.offset }

      results.collectFirst { case e @ Error(_, _) => e } match {
        case Some(err) => err
        case None =>
          results.collectFirst { case s @ Success(_, _) => s } match {
            case Some(success) => success
            case None =>
              results.headOption match {
                case Some(fail) => fail
                case None => Success((), in)
              }
          }
      }
    }

    Parser { in => parse(in) }
  }

  // this is in an extracted method because mutable data must be generated every time the parser runs.
  private def parseUnordered[A](in: Input, configs: List[ParseConf[A]]): ParseResult[List[A]] = {
    val mutList: List[MutableConf[A]] = configs.map(_.getMutator())
    val mutParser: Parser[Unit] = listOrParser(mutList)

    @tailrec
    def rec(in: Input): ParseResult[List[A]] = {
      mutParser(in) match {
        case e @ Error(_, _) => e
        case Success(_, next) => rec(next)

        case Failure(_, next) =>
          mutList.filter(_.checkLTMin()) match {
            case Nil => Success(mutList.flatMap(_.result()), next)
            case list @ head :: _ => list.flatMap(_.getLastFail).headOption match {
              case Some(lastFail) => lastFail
              case None => head.failWithMin(in)
            }
          }
      }
    }

    rec(in)
  }

  def unordered[A](configs: ParseConf[A]*): Parser[List[A]] = {
    checkParsersUnique(configs.map(_.parser).toList)
    Parser { in => parseUnordered(in, configs.toList) }
  }
}
