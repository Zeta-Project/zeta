package de.htwg.zeta.server.generator.generators.vr.shape

import java.util.UUID

import de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection
import de.htwg.zeta.server.generator.model.shapecontainer.connection.Placing
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Ellipse
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.GeometricModel
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Line
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Point
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.PolyLine
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Polygon
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Rectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.style.DASH
import de.htwg.zeta.server.model.result.Unreliable
import models.entity.File


object VrGeneratorConnectionDefinition {


  def doGenerateResult(connections: Iterable[Connection]): Unreliable[List[File]] = {
    Unreliable(() => doGenerateGenerators(connections), "failed trying to create the vr Connector generators")
  }

  private def doGenerateGenerators(connections: Iterable[Connection]): List[File] = {
    connections.map(generateSingleFile).toList
  }


  private def generateSingleFile(conn: Connection): File = {
    val filename = "vr-connection-" + conn.name + ".html"
    val polymerElement = generatePolymerElement(conn)

    File(UUID.randomUUID, filename, polymerElement)
  }


  def generatePolymerElement(conn: Connection): String = {
    s"""
      |<link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
      |<link rel="import" href="/assets/prototyp/behaviors/vr-connection.html">
      |<link rel="import" href="/assets/prototyp/behaviors/vr-delete.html">
      |<link rel="import" href="/assets/prototyp/elements/vr-polyline.html">
      |<link rel="import" href="/assets/prototyp/elements/vr-placing.html">
      |<link rel="import" href="/assets/prototyp/elements/vr-point.html">
      |${importPlacing(conn.placing)}
      |
      |<dom-module id="vr-connection-${conn.name}">
      |  <template>
      |    <!-- Polyline is always needed -->
      |    <vr-polyline id="line" ${
      if (conn.style.get.line_style.get == DASH) {
        "dashed"
      } else {
        ""
      }
    }></vr-polyline>
      |    ${conn.placing.map(generatePlacing).mkString}
      |  </template>
      |</dom-module>
      |
      |<script>
      |  window.VrElement = window.VrElement || {};
      |  VrElement.Connection${conn.name.capitalize} = Polymer({
      |    is: "vr-connection-${conn.name}",
      |    behaviors: [
      |      VrBehavior.Connection,
      |      VrBehavior.Delete
      |    ]
      |  });
      |</script>
    """.stripMargin
  }

  def importPlacing(placings: List[Placing]): String = {
    val placingTypes = placings.map(placing => getElement(placing.shapeCon))
    val imports = placingTypes.distinct
    imports.map(imp => s"""<link rel='import' href='/assets/prototyp/elements/vr-${imp}.html'>""").mkString
  }

  def generatePlacing(placing: Placing): String = {
    val radius = placing.position_distance.getOrElse(0)
    val angle = 0
    val element = getElement(placing.shapeCon)
    s"""
    <vr-placing offset="${placing.position_offset}" angle="${angle}" radius="${radius}">
      ${generateElement(placing.shapeCon)}
    </vr-placing>
    """
  }

  def generateElement(geometric: GeometricModel): String = {
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

  def generatePoints(points: List[Point]): String = {
    points.map(point => generateVrPoint(point.x, point.y)).mkString
  }

  def generateVrPoint(xy: (Int, Int)): String = {
    val (x, y) = xy
    s"""<vr-point x="${y}" y="${x}"></vr-point>
    """
  }

  def getElement(geometric: GeometricModel): String = {
    geometric match {
      case g: Line => "Line"
      case g: Ellipse => "ellipse"
      case g: Rectangle => "box"
      case g: Polygon => "polygon"
      case g: PolyLine => "polyline"
      case g: RoundedRectangle => "roundedrectangle"
      case g: Text => "text"
      case _ => geometric.toString
    }
  }

}
