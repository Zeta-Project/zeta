package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
 * Check a list of elements for invalid foreign keys / pointers.
 *
 * @param toId      Mapping function (eventually used to map the given element to its identifier).
 * @param available List of all available identifiers (valid identifiers).
 * @tparam T Type of elements which will be checked.
 */
class FindInvalidPointers[T](toId: T => Id, available: List[Id]) extends Check[T] {

  /**
   * Checks each element (foreign key / pointer) whether it is an invalid key.
   *
   * @param pointers List of all pointers to be checked.
   * @return A list of element ids for those elements on which the check was true.
   */
  override def apply(pointers: List[T]): List[Id] = {
    pointers.map(toId).filter(id => available.count(i => i == id) != 1)
  }

}