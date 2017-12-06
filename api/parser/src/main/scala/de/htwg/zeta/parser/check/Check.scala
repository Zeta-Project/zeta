package de.htwg.zeta.parser.check


/**
  * The Check trait serves as a template for checks on a sequence of elements of an arbitrary type.
  *
  * @tparam T Type of elements which will be checked.
  */
trait Check[T] {

  /**
    * Check a sequence of elements.
    *
    * @param elements Sequence of elements which will be checked by a specific algorithm.
    * @return A sequence of element ids for those elements on which the check was true.
    */
  def apply(elements: Seq[T]): Seq[Check.Id]
}


object Check {
  // Each element must be uniquely identifiable
  type Id = String
}
