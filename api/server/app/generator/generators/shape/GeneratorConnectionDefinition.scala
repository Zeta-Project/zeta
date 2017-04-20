package generator.generators.shape

import generator.generators.style.StyleGenerator
import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.connection.Placing
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.HasStyle

import scala.collection.mutable

/**
 * Created by julian on 19.01.16.
 * the connection generator
 */
object GeneratorConnectionDefinition {
  val placingsCache = mutable.HashMap[String, mutable.MutableList[Placing]]()
  val labelCache = mutable.HashMap[String, mutable.MutableList[Placing]]()

  def generate(connections: Iterable[Connection]) = {
    s"""
      ${head}
      function getConnectionStyle(stylename){
        var style;
        switch(stylename){
          ${generateConnectionsCases(connections)}
          default:
            style = {};
            break;
        }

        return style;
      }

      function getPlacings(stylename){
        var placings;
        switch(stylename){
          $generateCachedPlacings
          default:
            placings = [];
            break;
        }

        return placings;
      }

      function getLabels(stylename){
        var labels;
        switch(stylename){
          $generateCachedLabels
          default:
            labels = [];
          break;
        }

        return labels;
      }
    """
  }

  private def generateConnectionsCases(connections: Iterable[Connection]) = {
    connections.map(c => s"""
      case '${c.name}':
        ${
          if (c.style.isDefined) {
            "style = getStyle('" + c.style.get.name + "');\n"
          } else {
            "style = {'.connection':{stroke: 'black'}};\n"
          }
        }
        ${generateInlineStyle(c)}
        ${handlePlacings(c)}
        break;
    """).mkString
  }

  private def head = {
    raw"""
    /*
     * This is a generated ShapeFile for JointJS
     */
    """
  }

  private def generateInlineStyle(connection: Connection) = {
    if (connection.style isDefined) {
      s"""
        //Get inline style
        var inline = {
          '.connection, .marker-target, .marker-source':{
            ${StyleGenerator.commonAttributes(connection.style.get)},
            ${StyleGenerator.fontAttributes(connection.style.get)}
          }
        };

        //Merge with default style
        jQuery.extend(style, inline);
      """
    } else {
      ""
    }
  }

  private def handlePlacings(connection: Connection) = {
    val placings = connection.placing
    var isTargetMarkerSet = false; //Check, whether a target marker is set, because JointJS will show an arrow if none is set
    var ret = ""
    for {p <- placings} {

      p.position_offset match {
        case 0.0 => ret +=
          raw"""
            style['.marker-source'] = {
            ${generateStyle(p.shapeCon)}
            $generateMarkerSourceCorrection
            ${generateMarker(p)}};
          """
        case 1.0 =>
          if (!p.shapeCon.isInstanceOf[Text]) {
            ret += raw"""style['.marker-target'] = { ${generateMirroredMarker(p)}, ${generateStyle(p.shapeCon)} };"""
          } else {
            cachePlacing(connection.name, p)
          }
          isTargetMarkerSet = true

        case _ => cachePlacing(connection.name, p)
      }
      if (!isTargetMarkerSet) {
        ret +=
          """
            style['.marker-target'] = {
              d: 'M 0 0' //override JointJS default arrow
            };
          """
      }
    }
    ret
  }

  private def generateStyle(geometricModel: GeometricModel): String = {
    geometricModel match {
      case hs: HasStyle =>
        if (hs.style.isDefined) {
          s"""
            ${StyleGenerator.commonAttributes(hs.style.get)},
            text: {
              ${StyleGenerator.fontAttributes(hs.style.get)}
            },
          """
        } else {
          ""
        }

    }
  }

  private def generateCachedPlacings = {
    var placings = ""
    if (placingsCache.nonEmpty) {
      placings = placingsCache.map {
        case (k, v) =>
          s"""
          case "$k":
            placings = [
              ${v.map(p => generatePlacing(p)).mkString(",")}
            ];
            break;
          """
      }.mkString
      placingsCache.clear()
    }
    placings
  }

  private def generateCachedLabels = {
    val labels = labelCache.map {
      case (k, v) =>
        s"""
        case "$k":
          labels = [ ${v.map(generateLabel).mkString}];
          break;
        """
    }.mkString
    labelCache.clear()
    labels
  }

  private def generateLabel(placing: Placing) = {
    raw"""
    {
      position: ${placing.position_offset},
      attrs: {
        rect: {fill: 'transparent'},
        text: {
          y: ${placing.position_distance.getOrElse(0)},
          text: ${placing.shapeCon.asInstanceOf[Text].textBody}
        }
      },
      id: '${placing.shapeCon.asInstanceOf[Text].id}'
    }
    """
  }

  private def generatePlacing(placing: Placing) = {
    s"""
    {
      position: ${placing.position_offset},
      ${generateRightPlacingShape(placing.shapeCon, placing.position_distance.getOrElse(1))}
    }
    """
  }

  private def generateRightPlacingShape(g: GeometricModel, distance: Int): String = g match {
    case l: Line => generatePlacingShape(l, distance)
    case e: Ellipse => generatePlacingShape(e, distance)
    case p: Polygon => generatePlacingShape(p, distance)
    case pl: PolyLine => generatePlacingShape(pl, distance)
    case r: Rectangle => generatePlacingShape(r, distance)
    case rr: RoundedRectangle => generatePlacingShape(rr, distance)
    case t: Text => generatePlacingShape(t, distance)
  }

  private def generatePlacingShape(shape: Line, distance: Int) = {
    s"""
    markup: '<line />',
    attrs:{
      x1: ${shape.points._1.x},
      y1: ${shape.points._1.y},
      x2: ${shape.points._2.x},
      y2: ${shape.points._2.y},
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
    }
    """
  }

  private def generatePlacingShape(shape: PolyLine, distance: Int) = {
    """
    markup: '<polyline />',
    attrs:{
      points: """" + shape.points.map(point => point.x + ", " + point.y + { if (point != shape.points.last) ", " else "\"" }).mkString("") + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""},
      $generateStyleCorrections
    }
    """
  }

  private def generatePlacingShape(shape: Rectangle, distance: Int) = {
    s"""
    markup: '<rect />',
    attrs:{
      height: ${shape.size_height},
      width: ${shape.size_width},
      y: ${distance - shape.size_height / 2},
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
    }
    """
  }

  private def generatePlacingShape(shape: RoundedRectangle, distance: Int) = {
    s"""
    markup: '<rect />',
    attrs:{
      height: ${shape.size_height},
      width: ${shape.size_width},
      rx: ${shape.curve_width},
      ry: ${shape.curve_height},
      y: ${distance - shape.size_height / 2},
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
    }
    """
  }

  private def generatePlacingShape(shape: Polygon, distance: Int) = {
    """
    markup: '<polygon />',
    attrs:{
      points: """" + shape.points.map(point => point.x + "," + point.y + { if (point != shape.points.last) " " else "\"" }).mkString + raw""",
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""}
    }
    """
  }

  private def generatePlacingShape(shape: Ellipse, distance: Int) = {
    s"""
    markup: '<ellipse />',
    attrs:{
      rx: ${shape.size_width / 2},
      ry: ${shape.size_height / 2},
      cy: $distance,
      ${if (shape.style.isDefined) StyleGenerator.commonAttributes(shape.style.get) else ""},
      }
    """
  }

  private def generatePlacingShape(shape: Text, distance: Int) = {
    s"""
    markup: '<text>${shape.textBody}</text>',
    attrs:{
      y: ${shape.size_height / 2}
    }
    """
  }

  private def generateMarker(placing: Placing) = {
    """
    d: '""" + generateRightSvgPathData(placing.shapeCon) + """'
    """
  }

  private def generateMirroredMarker(placing: Placing) = {
    /*
     * PolyLine and Polygon need to be mirrored against the x and y-axis because target
     * marker gets rotated by 180 degree
     */
    val svgPathData = placing.shapeCon match {
      case p: Polygon => generateMirroredPolygon(placing.shapeCon.asInstanceOf[Polygon])
      case pl: PolyLine => generaMirroredPolyLine(placing.shapeCon.asInstanceOf[PolyLine])
      case _ => generateRightSvgPathData(placing.shapeCon)
    }

    s"""
    d: '$svgPathData'
    """
  }

  private def generaMirroredPolyLine(shape: PolyLine) = {
    val mirroredPoints = shape.points.map(p => new Point((p.x * -1), p.y))
    val head = mirroredPoints.head
    val tail = mirroredPoints.tail
    """M """ + head.x + " " + head.y + " " + tail.map(point => "L " + point.x + " " + point.y).mkString
  }

  private def generateMirroredPolygon(shape: Polygon) = {
    val mirroredPoints = shape.points.map(p => new Point((p.x * -1), (p.y * -1)))
    val head = mirroredPoints.head
    val tail = mirroredPoints.tail
    "M " + head.x + " " + head.y + " " + tail.map(p => "L " + p.x + " " + p.y).mkString + "z"
  }

  private def generateRightSvgPathData(g: GeometricModel): String = {
    g match {
      case l: Line => generateSvgPathData(l)
      case p: Polygon => generateSvgPathData(p)
      case pl: PolyLine => generateSvgPathData(pl)
      case e: Ellipse => generateSvgPathData(e)
      case r: Rectangle => generateSvgPathData(r)
      case rr: RoundedRectangle => generateSvgPathData(rr)
    }
  }

  private def generateSvgPathData(shape: Line) = {
    val points = shape.points
    """M """ + points._1.x + " " + points._1.y + " L " + points._2.x + " " + points._2.y
  }

  private def generateSvgPathData(shape: PolyLine) = {
    val head = shape.points.head
    val tail = shape.points.tail
    """M """ + head.x + " " + head.y + " " + tail.map(point => "L " + point.x + " " + point.y).mkString
  }

  private def generateSvgPathData(shape: Rectangle) = {
    """M """ + shape.x + " " + shape.y + "l " + shape.size_width + " 0 l 0 " + shape.size_height + " l -" + shape.size_width + " 0 z"
  }

  private def generateSvgPathData(shape: RoundedRectangle) = {
    "M " + shape.x + " " + shape.curve_width + " " + shape.y + " " + shape.curve_height + " l " + (shape.size_width - 2 * shape.curve_width) +
      "l 0 a " + shape.curve_width + " " + shape.curve_height + " 0 0 1 " + shape.curve_width + " " + shape.curve_height + "l 0 " +
      (shape.size_height - 2 * shape.curve_height) + " a " + shape.curve_width + " " + shape.curve_height + " 0 0 1 -" + shape.curve_width +
      " " + shape.curve_height + " l -" + (shape.size_width - 2 * shape.curve_width) + " 0 a " + shape.curve_width + " " + shape.curve_height +
      " 0 0 1 -" + shape.curve_width + " -" + shape.curve_height + " l 0 -" + (shape.size_height - 2 * shape.curve_height) +
      " a " + shape.curve_width + " " + shape.curve_height + " 0 0 1 " + shape.curve_width + " -" + shape.curve_height
  }

  private def generateSvgPathData(shape: Polygon) = {
    val head = shape.points.head
    val tail = shape.points.tail
    "M " + head.x + " " + head.y + " " + tail.map(p => "L " + p.x + " " + p.y).mkString + "z"
  }

  private def generateSvgPathData(shape: Ellipse) = {
    val rx = shape.size_width / 2
    val ry = shape.size_height / 2
    "M " + shape.x + " " + shape.y + " a  " + rx + " " + ry + " 0 0 1 " + rx + " -" + ry + " a  " + rx + " " + ry + " 0 0 1 " + rx + " " +
      ry + " a  " + rx + " " + ry + " 0 0 1 -" + rx + " " + ry + " a  " + rx + " " + ry + " 0 0 1 -" + rx + " -" + ry
  }

  private def generateStyleCorrections = {
    """
    fill: 'transparent' //JointJS uses fill attribute to fill in all markers
    """
  }

  private def generateMarkerSourceCorrection = {
    """transform: 'scale(1,1)',"""
  }

  private def cachePlacing(connection: String, placing: Placing) {
    if (placing.shapeCon.isInstanceOf[Text]) {
      writeToCache(connection, placing, labelCache)
    } else {
      writeToCache(connection, placing, placingsCache)
    }

  }

  private def writeToCache(connection: String, placing: Placing, cache: mutable.HashMap[String, mutable.MutableList[Placing]]) {
    if (cache.contains(connection)) {
      cache(connection) += placing
    } else {
      val list = mutable.MutableList[Placing]()
      list += placing
      cache.put(connection, list)
    }
  }

}
