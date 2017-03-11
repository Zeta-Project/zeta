package generator.model.style

/**
 * Created by julian on 29.10.15.
 */
sealed class LineStyle
case object SOLID extends LineStyle { def apply = "solid" }
case object DOT extends LineStyle { def apply = "dot" }
case object DASH extends LineStyle { def apply = "dash" }
case object DASHDOT extends LineStyle { def apply = "dash-dot" }
case object DASHDOTDOT extends LineStyle { def apply = "dash-dot-dot" }

object LineStyle {
  def getIfValid(s: String) = {
    s match {
      case "solid" => Some(SOLID)
      case "dot" => Some(DOT)
      case "dash" => Some(DASH)
      case "dashdot" => Some(DASHDOT)
      case "dashdotdot" => Some(DASHDOTDOT)
      case _ => None
    }
  }
}
