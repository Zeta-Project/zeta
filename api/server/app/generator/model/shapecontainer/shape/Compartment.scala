package generator.model.shapecontainer.shape

import generator.parser.CommonParserMethods

/**
 * Created by julian on 19.10.15.
 * representation of CompartmentInfo
 */
case class Compartment private (
    compartment_id: String,
    compartment_layout: Option[CompartmentLayout] = None,
    compartment_stretching_horizontal: Option[Boolean] = None,
    compartment_stretching_vertical: Option[Boolean] = None,
    compartment_spacing: Option[Int] = None,
    compartment_margin: Option[Int] = None,
    compartment_invisible: Option[Boolean] = None)

sealed abstract class CompartmentLayout
case object FIXED extends CompartmentLayout
case object VERTICAL extends CompartmentLayout
case object HORIZONTAL extends CompartmentLayout
case object FIT extends CompartmentLayout

/**
 * Compartment
 */
object Compartment extends CommonParserMethods {

  def apply(attributes: List[String]): Option[Compartment] = parse(attributes)
  private def parse(attributes: List[String]): Option[Compartment] = {

    var layout: Option[CompartmentLayout] = None
    var margin: Option[Int] = None
    var spacing: Option[Int] = None
    var stretching_vertical: Option[Boolean] = None
    var stretching_horizontal: Option[Boolean] = None
    var id: Option[String] = None
    var invisible: Option[Boolean] = None

    var compartment_attributes = List[String]()

    attributes.foreach { x =>
      if (x.startsWith("compartment")) {
        compartment_attributes = parse(split_compartment, x).get
      }
    }

    for {attribute <- compartment_attributes} {
      attribute.toSeq match {
        case x@Seq('l', 'a', 'y', 'o', 'u', 't', _*) => layout = Some(parse(parseLayout, x.toString()).get)
        case x@Seq('m', 'a', 'r', 'g', 'i', 'n', _*) => margin = Some(parse(parseMargin, x.toString()).get)
        case x@Seq('s', 'p', 'a', 'c', 'i', 'n', 'g', _*) => spacing = Some(parse(parseSpacing, x.toString()).get)
        case x@Seq('s', 't', 'r', 'e', 't', 'c', 'h', 'i', 'n', 'g', _*) =>
          val tup = parse(parseStretching, x.toString()).get
          stretching_horizontal = Some(tup._1)
          stretching_vertical = Some(tup._2)
        case x@Seq('i', 'n', 'v', 'i', 's', 'i', 'b', 'l', 'e', _*) => invisible = Some(true)
        case x@Seq('i', 'd',_*) => id = Some(parse(parseId, x.toString()).get)
      }
    }

    create(id, layout, stretching_horizontal, stretching_vertical, spacing, margin, invisible)
  }

  private def create(
    id: Option[String],
    layout: Option[CompartmentLayout],
    stretching_horizontal: Option[Boolean],
    stretching_vertical: Option[Boolean],
    spacing: Option[Int],
    margin: Option[Int],
    invisible: Option[Boolean]) = {

    if (id.isDefined && layout.isDefined) {
      Some(new Compartment(id.get, layout, stretching_horizontal, stretching_vertical, spacing, margin, invisible))
    } else {
      None
    }
  }

  private def parseLayout = "layout\\s*=\\s*".r ~> "(fixed|vertical|horizontal|fit)".r ^^ {
    case "fixed" => FIXED
    case "horizontal" => HORIZONTAL
    case "vertical" => VERTICAL
    case "fit" => FIT
  }

  private def parseMargin = "margin\\s*=\\s*".r ~> "\\d+".r ^^ { mar => mar.toInt }
  private def parseSpacing = "spacing\\s*=\\s*".r ~> "\\d+".r ^^ { spac: String => spac.toInt }

  private def parseStretching = {
    ("stretching\\s*\\(\\s*horizontal\\s*=".r ~> "(yes|y|true|no|n|false)".r) ~ (",\\s*vertical\\s*=".r ~> ("(yes|y|true|no|n|false)".r <~ ")")) ^^ {
      case hor ~ ver => (matchBoolean(hor), matchBoolean(ver))
    }
  }

  private def parseId = "id\\s*=".r ~> ident ^^ { _.toString }
}

