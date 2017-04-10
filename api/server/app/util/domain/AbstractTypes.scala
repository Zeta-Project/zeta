package util.domain

abstract class MObj {
  val name: String
}

trait MBounds {
  def upperBound: Int
  def lowerBound: Int
}

//This is not part of the official MoDiGen metamodel, but a utility trait for easier access to attributes
trait ObjectWithAttributes {
  def attribute(name: String): Option[MAttribute]
  val name: String
}
