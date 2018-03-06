package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
 * Check for elements which are referenced but undefined.
 *
 * @param toId                    Function which returns the id for a given element.
 * @param getReferencedElementIds Function which returns a list of referenced element ids for a given element.
 * @tparam T Type of the elements which will be checked.
 */
class FindUndefinedElements[T](toId: T => Id, getReferencedElementIds: T => List[Id]) extends Check[T] {

  /**
   * Checks each element if it references an undefined element.
   *
   * @param elements List of elements which will be checked for references which are undefined.
   * @return A list of referenced element ids which are referenced but never undefined.
   */
  override def apply(elements: List[T]): List[Id] = {
    val definedElementIds = elements.map(toId).toSet
    elements.flatMap { element =>
      val referencedIds = getReferencedElementIds(element).toSet
      val undefinedElementIds = referencedIds.diff(definedElementIds)
      undefinedElementIds
    }.distinct
  }

}

object FindUndefinedElements {
  def apply[T](toId: T => Id, getReferencedElementIds: T => List[Id]): FindUndefinedElements[T] = new FindUndefinedElements[T](toId, getReferencedElementIds)
}