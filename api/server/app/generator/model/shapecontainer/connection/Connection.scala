package generator.model.shapecontainer.connection

import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.connection
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.PlacingSketch
import generator.parser.CommonParserMethods
import generator.parser.IDtoStyle

/**
 * Created by julian on 20.10.15.
 * representation of a Connection
 *
 * @param connection_type -> inner Connection.scala ConnectionStyle, can either be an object {FreeForm, Manhatten}
 * @param style           is a model.style.Style instance
 * @param placing         outstanding
 *                        TODO
 */
final class Connection private(
    override val name: String,
    val connection_type: Option[ConnectionStyle] = None,
    val style: Option[Style] = None,
    val placing: List[Placing] = List[Placing]()
) extends ShapeContainerElement {
  val textMap: Map[String, Text] = {
    val textMaps = for {p <- placing} yield {
      p.shape.textMap
    }
    textMaps.filter(_.isDefined).map(_.get).foldLeft(Map[String, Text]()) { (ret, m) => ret ++ m }
  }

}

object Connection extends CommonParserMethods {
  val validConnectionAttributes = List("connection-type", "layout", "placing")

  /**
   * parse method
   */
  def apply(
    name: String,
    styleRef: Option[Style],
    typ: Option[String],
    anonymousStyle: Option[String],
    placings: List[PlacingSketch],
    cache: Cache
  ): Option[Connection] = {
    // mapping
    val connection_type: Option[ConnectionStyle] = typ.map(t => parse(connectionType, t).get)
    val style: Option[Style] =
      anonymousStyle match {
        case Some(as) => Style.generateChildStyle(cache, styleRef, IDtoStyle(as)(cache))
        case None => styleRef
      }

    val placingList = placings.map {
      Placing(_, style, cache.shapeHierarchy.root.data)
    }

    placingList match {
      case Nil => None
      case _ =>
        val newConnection = new Connection(name, connection_type, style, placingList)
        cache + newConnection
        Some(newConnection)
    }

  }

  private def connectionType: connection.Connection.Parser[ConnectionStyle] = {
    "connection-type\\s*=".r ~> "(freeform|manhatten)".r ^^ {
      case "freeform" => FreeForm
      case "manhatten" => Manhatten
    }
  }

}

abstract sealed class ConnectionStyle

case object FreeForm extends ConnectionStyle

case object Manhatten extends ConnectionStyle
