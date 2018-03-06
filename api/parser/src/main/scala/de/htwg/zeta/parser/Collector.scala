package de.htwg.zeta.parser

import scala.reflect.ClassTag

object Collector {
  def apply(list: List[Any]): Collector = new Collector(list)
}

class Collector(private val list: List[Any]) {

  /**
   * Get all values by type => use for ParserConf like
   * - min(1)
   * - max(x), x > 1
   * - range(_, y), y > 1
   * - exact(x), x > 1
   *
   * @param classTag Erased class of given type T.
   * @tparam T Type to be found.
   * @return List of all values for type T.
   */
  //noinspection ScalaStyle
  def *[T](implicit classTag: ClassTag[T]): List[T] = list.collect { case x: T => x }

  /**
   * Get single value by type (optional) => use for ParserConf like
   * - optional()
   *
   * @param classTag Erased class of given type T.
   * @tparam T Type to be found.
   * @return Optional value for type T.
   */
  //noinspection ScalaStyle
  def ?[T](implicit classTag: ClassTag[T]): Option[T] = list.collectFirst { case x: T => x }

  /**
   * Get single value by type (throws) => use for ParserConf like
   * - once()
   *
   * @param classTag Erased class of given type T.
   * @tparam T Type to be found.
   * @return One single value for type T.
   */
  //noinspection ScalaStyle
  def ![T](implicit classTag: ClassTag[T]): T = ?(classTag).get
}
