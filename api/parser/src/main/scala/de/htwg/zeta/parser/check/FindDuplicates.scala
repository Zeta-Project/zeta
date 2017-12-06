package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
  * Check a sequence for duplicates.
  *
  * @param toId Function which returns the id of a given element.
  * @tparam T Type of the elements which will be checked.
  */
class FindDuplicates[T](toId: T => Id) extends Check[T] {

  /**
    * Checks each element if it is defined multiple times.
    *
    * @param elements Sequence of elements which will be checked for duplicates.
    * @return A sequence of element ids which are defined more than once.
    */
  override def apply(elements: Seq[T]): Seq[Id] = {
    val ids = elements.map(toId)
    val duplicates = ids.diff(ids.distinct)
    duplicates.distinct
  }

}