package generator.model.diagram.methodes

/**
 * Created by julian on 08.12.15.
 */
trait Methodes {
  val onCreate: Option[OnCreate]
  val onUpdate: Option[OnUpdate]
  val onDelete: Option[OnDelete]
}
