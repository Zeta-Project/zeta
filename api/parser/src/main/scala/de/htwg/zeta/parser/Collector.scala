package de.htwg.zeta.parser

import scala.reflect.ClassTag

object Collector {
  def apply(list: List[Any]): Collector = new Collector(list)
}

class Collector(private val list: List[Any]) {

  //noinspection ScalaStyle
  // get all values by type => use for ParserConf like
  // - min(1)
  // - max(x), x > 1
  // - range(_, y), y > 1
  // - exact(x), x > 1
  def *[T](implicit classTag: ClassTag[T]): List[T] = list.collect { case x: T => x }

  //noinspection ScalaStyle
  // get single value by type (optional) => use for ParserConf like
  // - optional()
  def ?[T](implicit classTag: ClassTag[T]): Option[T] = list.collectFirst { case x: T => x }

  //noinspection ScalaStyle
  // get single value by type (throws) => use for ParserConf like
  // - once()
  def ![T](implicit classTag: ClassTag[T]): T = ?(classTag).get
}
