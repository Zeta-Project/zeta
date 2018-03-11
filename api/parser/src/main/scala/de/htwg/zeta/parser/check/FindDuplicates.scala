package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
 * Check a list for duplicates.
 *
 * @param toId Function which returns the id of a given element.
 * @tparam T Type of the elements which will be checked.
 */
class FindDuplicates[T](toId: T => Id) extends Check[T] {

  /**
   * Checks each element if it is defined multiple times.
   *
   * @param elements List of elements which will be checked for duplicates.
   * @return A list of element ids which are defined more than once.
   */
  override def apply(elements: List[T]): List[Id] = {
    val ids = elements.map(toId)
    val duplicates = ids.diff(ids.distinct)
    duplicates.distinct
  }

}

object FindDuplicates {
  def apply[T](toId: T => Id): FindDuplicates[T] = new FindDuplicates[T](toId)
}