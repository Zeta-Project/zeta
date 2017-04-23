package util.datavis.domain

sealed abstract class Comparator

case class Equal() extends Comparator {
  override def toString = "=="
}
case class NotEqual() extends Comparator {
  override def toString = "!="
}

case class Less() extends Comparator {
  override def toString = "<"
}

case class Greater() extends Comparator {
  override def toString = ">"
}

case class LessOrEqual() extends Comparator {
  override def toString = "<="
}

case class GreaterOrEqual() extends Comparator {
  override def toString = ">="
}
