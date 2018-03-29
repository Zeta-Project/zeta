package de.htwg.zeta.parser

import de.htwg.zeta.parser.ReferenceCollector.Id

/**
 * A helper object, which can be used to find elements in a list of elements
 * by a specific id. There must be a function defined, which maps the element
 * list to their identifiers.
 */
object ReferenceCollector {
  type Id = String

  def apply[T](list: List[T], toId: T => Id): ReferenceCollector[T] = new ReferenceCollector[T](list, toId)
}

class ReferenceCollector[T](private val list: List[T], private val toId: T => Id) {

  /**
   * Find all values with the given id (could be empty).
   *
   * @param id Identifier to find the referenced elements.
   * @return List of all found elements with that id.
   */
  def *(id: Id): List[T] = list.collect { case m if id == toId(m) => m }

  /**
   * Find exactly one element (optional) in a configured list by the given id.
   *
   * @param id Identifier to find the referenced element.
   * @return Optional found element.
   */
  def ?(id: Id): Option[T] = list.collectFirst { case m if id == toId(m) => m }

  /**
   * Find exactly one element (throws) in a configured list by the given id.
   *
   * @param id Identifier to find the referenced element.
   * @return One single found element.
   */
  //noinspection ScalaStyle
  def !(id: Id): T = ?(id).get

  /**
   * Get a list of all identifiers of these referenced elements. Contains duplicate identifiers too.
   *
   * @return List of identifiers (could be empty).
   */
  def identifiers(): List[Id] = list.map(toId)

}
