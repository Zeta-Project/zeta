package generator.model.diagram.methodes

/**
 * ???
 */
trait Methodes {
  val onCreate: Option[OnCreate]
  val onUpdate: Option[OnUpdate]
  val onDelete: Option[OnDelete]
}
