package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

/**
  * Check for elements which are referenced but undefined.
  *
  * @param toId                    Function which returns the id for a given element.
  * @param getReferencedElementIds Function which returns a list of referenced element ids for a given element.
  */
class FindUndefinedElements(toId: PartialFunction[Any, Id], getReferencedElementIds: PartialFunction[Any, List[Id]]) extends Check[Any] {

  /**
    * Checks each element if it references an undefined element.
    *
    * @param elements List of elements which will be checked for references which are undefined.
    * @return A list of referenced element ids which are referenced but never undefined.
    */
  override def apply(elements: List[Any]): List[Id] = {

    val knownIds = elements.collect {
      case elem: Any if toId.isDefinedAt(elem) => toId(elem)
    }.toSet

    val elementsToCheck = elements.collect {
      case elem: Any if getReferencedElementIds.isDefinedAt(elem) => elem
    }

    elementsToCheck.flatMap { elem =>
      val referencedIds = getReferencedElementIds(elem).toSet
      val diff = referencedIds.diff(knownIds)
      diff
    }.distinct
  }

}