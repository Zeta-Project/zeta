package generator.generators.vr.shape

import java.nio.file.Files
import java.nio.file.Paths

import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.connection.Placing
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.DASH

/**
 * Created by max on 26.10.16.
 */
object VrGeneratorConnectionDefinition {
  def generate(connections: Iterable[Connection], location: String) {
    for {conn <- connections} { generateFile(conn, location) }
  }

  def generateFile(conn: Connection, DEFAULT_SHAPE_LOCATION: String) = {
    val FILENAME = "vr-connection-" + conn.name + ".html"

    val polymerElement = generatePolymerElement(conn)

    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), polymerElement.getBytes())
  }

  def generatePolymerElement(conn: Connection) = {
    s"""
    <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-connection.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-delete.html">
    <link rel="import" href="/assets/prototyp/elements/vr-polyline.html">
    <link rel="import" href="/assets/prototyp/elements/vr-placing.html">
    <link rel="import" href="/assets/prototyp/elements/vr-point.html">
    ${importPlacing(conn.placing)}

    <dom-module id="vr-connection-${conn.name}">
      <template>
        <!-- Polyline is always needed -->
        <vr-polyline id="line" ${if (conn.style.get.line_style.get == DASH) { "dashed" } else { "" }}></vr-polyline>
        ${conn.placing.map(generatePlacing(_)).mkString}
      </template>
    </dom-module>

    <script>
      window.VrElement = window.VrElement || {};
      VrElement.Connection${conn.name.capitalize} = Polymer({
        is: "vr-connection-${conn.name}",
        behaviors: [
          VrBehavior.Connection,
          VrBehavior.Delete
        ]
      });
    </script>
    """
  }

  def importPlacing(placings: List[Placing]) = {
    val placingTypes = placings.map(placing => getElement(placing.shapeCon))
    val imports = placingTypes.distinct
    imports.map(imp => s"""<link rel='import' href='/assets/prototyp/elements/vr-${imp}.html'>""").mkString
  }

  def generatePlacing(placing: Placing) = {
    val radius = placing.position_distance.getOrElse(0)
    val angle = 0
    val element = getElement(placing.shapeCon)
    s"""
    <vr-placing offset="${placing.position_offset}" angle="${angle}" radius="${radius}">
      ${generateElement(placing.shapeCon)}
    </vr-placing>
    """
  }

  def generateElement(geometric: GeometricModel) = {
    geometric match {
      case g: Ellipse => s"""<vr-ellipse x-pos="${g.x}" y-pos="${g.y}" width="${g.size_width}" height="${g.size_height}"></vr-ellipse>"""
      case g: Rectangle => s"""<vr-box x-pos="${g.x}" y-pos="${g.y}" width="${g.size_width}" height="${g.size_height}"></vr-box>"""
      case g: Polygon => s"""<vr-polygon>${generatePoints(g.points)}</vr-polygon>"""
      case g: PolyLine => s"""<vr-polyline>${generatePoints(g.points)}</vr-polyline>"""
      case g: Line => s"""<vr-polyline>${generatePoints(List(g.points._1, g.points._1))}</vr-polyline>"""
      case g: Text => s"""<vr-text x-pos="${g.x}" y-pos="${g.y}" text="${g.textBody}"></vr-text>"""
      case _ => "//There are no rules to handle that element"
    }
  }

  def generatePoints(points: List[Point]) = {
    points.map(point => generateVrPoint(point.x, point.y)).mkString
  }

  def generateVrPoint(xy: (Int, Int)) = {
    val (x, y) = xy
    s"""<vr-point x="${y}" y="${x}"></vr-point>
    """
  }

  def getElement(geometric: GeometricModel) = {
    geometric match {
      case g: Line => "Line"
      case g: Ellipse => "ellipse"
      case g: Rectangle => "box"
      case g: Polygon => "polygon"
      case g: PolyLine => "polyline"
      case g: RoundedRectangle => "roundedrectangle"
      case g: Text => "text"
      case _ => geometric.toString()
    }
  }

}
