package de.htwg.zeta.common.models.project.gdsl.shape

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
