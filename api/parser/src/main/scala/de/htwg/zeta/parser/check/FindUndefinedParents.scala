package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
  * Check for parents which are undefined.
  *
  * @param toId         Function which returns the id for a given element.
  * @param getParentIds Function which returns a sequence of parent ids for a given element.
  * @tparam T Type of the elements which will be checked.
  */
class FindUndefinedParents[T](toId: T => Id, getParentIds: T => Seq[Id]) extends Check[T] {

  /**
    * Checks each element if it is defined multiple times.
    *
    * @param elements Sequence of elements which will be checked for undefined parents.
    * @return A sequence of parent element ids which are undefined.
    */
  override def apply(elements: Seq[T]): Seq[Id] = {
    val definedStyles = elements.map(toId)
    elements.flatMap(element => {
      val parentIds = getParentIds(element)
      val undefinedParents = parentIds.filterNot(definedStyles.contains)
      undefinedParents
    }).distinct
  }

}