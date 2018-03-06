package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
 * Check a list of elements for invalid ids / foreign keys / references.
 *
 * @param toId      Mapping function (eventually used to map the given element to its identifier).
 * @param available List of all available identifiers (valid identifiers).
 * @tparam T Type of elements which will be checked.
 */
class FindInvalidReferences[T](toId: T => Id, available: List[Id]) extends Check[T] {

  /**
   * Checks each element (id / foreign key / references) whether it is an invalid key.
   *
   * @param references List of all references to be checked.
   * @return A list of element ids for those elements on which the check was true.
   */
  override def apply(references: List[T]): List[Id] = {
    references.map(toId).filter(id => available.count(i => i == id) != 1)
  }

}