package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id

import scala.annotation.tailrec

/**
  * Check for cyclic dependency graphs.
  *
  * @param toId         Function which returns the id for a given element.
  * @param toElement    Function which returns the element for a given id.
  * @param getParentIds Function which returns a list of parent ids for a given element.
  * @tparam T Type of the elements which will be checked.
  */
class FindGraphCycles[T](toId: T => Id, toElement: Id => Option[T], getParentIds: T => List[Id]) extends Check[T] {

  /**
    * Checks each element if it is part of a cyclic dependency graph.
    *
    * @param elements List of elements which will be checked for cyclic dependency graphs.
    * @return A list of those element which are part of a cyclic dependency graph.
    */
  override def apply(elements: List[T]): List[Id] = {
    elements.filter(isPartOfGraphCycle).map(toId).distinct
  }

  private def isPartOfGraphCycle(element: T): Boolean = {
    val visited = collection.mutable.Set[Id]()

    @tailrec
    def check(parentIds: List[Id]): Boolean = parentIds match {
      case Nil => false
      case _ if parentIds.contains(toId(element)) => true
      case _ =>
        val unvisited: List[Id] = parentIds.filterNot(visited.contains)
        visited ++= unvisited
        val parentStyles = unvisited.flatMap(toElement(_))
        val grandparents = parentStyles.flatMap(getParentIds)
        check(grandparents)
    }

    check(getParentIds(element))
  }
}
