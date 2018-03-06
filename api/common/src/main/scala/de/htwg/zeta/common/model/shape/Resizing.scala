package de.htwg.zeta.common.model.shape

case class Resizing(
    horizontal: Boolean,
    vertical: Boolean,
    proportional: Boolean
)

object Resizing {
  val defaultHorizontal: Boolean = false
  val defaultVertical: Boolean = false
  val defaultProportional: Boolean = false
}
