package generator.model.shapecontainer.shape.geometrics

/**
 * Created by julian on 19.10.15.
 */
object Alignment {
  abstract class HAlign
  case object LEFT extends HAlign
  case object CENTER extends HAlign
  case object RIGHT extends HAlign

  abstract class VAlign
  case object TOP extends VAlign
  case object MIDDLE extends VAlign
  case object BOTTOM extends VAlign

  def parseVAlign(line: String): Option[VAlign] = line match {
    case "top" => Some(TOP)
    case "middle" => Some(MIDDLE)
    case "bottom" => Some(BOTTOM)
    case _ => None
  }
  def parseHAlign(line: String): Option[HAlign] = line match {
    case "center" => Some(CENTER)
    case "right" => Some(RIGHT)
    case "left" => Some(LEFT)
    case _ => None
  }
}
