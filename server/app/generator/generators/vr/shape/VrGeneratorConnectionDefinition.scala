package generator.generators.vr.shape

import java.nio.file.{Files, Paths}

import generator.model.shapecontainer.connection.{Connection, Placing}


/**
  * Created by max on 26.10.16.
  */
object VrGeneratorConnectionDefinition {
  def generate(connections: Iterable[Connection], location: String) {
    for(conn <- connections) {generateFile(conn, location)}
  }

  def generateFile(conn: Connection, DEFAULT_SHAPE_LOCATION: String) = {
    val FILENAME = "vr-connection-" + conn.name + ".html"

    val polymerElement = generatePolymerElement(conn)

    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), polymerElement.getBytes())
  }

  def generatePolymerElement(conn: Connection) = {
    s"""
    <link rel="import" href="../bower_components/polymer/polymer.html">
    <link rel="import" href="../behaviors/vr-three.html">
    <link rel="import" href="../behaviors/vr-connection.html">
    <link rel="import" href="./vr-placing.html">
    <link rel="import" href="./vr-point.html">
    ${importPlacing(conn.placing)}

    <dom-module id="vr-connection-${conn.name}">
        <template>
            <!-- Polyline is always needed -->
            <vr-polyline id="line"></vr-polyline>
            ${conn.placing.map(generatePlacing(_)).mkString}
        </template>
    </dom-module>

    <script>
        Polymer({
            is: "vr-connection-${conn.name}",
            behaviors: [
                VrBehaviors.ThreeBehavior,
                VrBehaviors.ConnectionBehavior,
                VrBehaviors.DeleteBehavior
            ]
        });
    </script>
    """
  }

  def importPlacing(placings: List[Placing]) = {
    val placingTypes = placings.map(_.attributes.typ)
    val imports = placingTypes.distinct
    imports.map(imp => s"""<link rel='import' href='./vr-${imp}.html'>""").mkString
  }

  def generatePlacing(placing: Placing) = {
    val radius = placing.position_distance.getOrElse(0)
    val angle = 0
    s"""
    <vr-placing offset="${placing.position_offset}" angle="${angle}" radius="${radius}">
      <vr-${placing.attributes.typ}>
        ${placing.attributes.points.map(generateVrPoint(_)).mkString}
      </vr-${placing.attributes.typ}>
    </vr-placing>
     """
  }

  def generateVrPoint(xy: (Int, Int)) = {
    val (x,y) = xy
    s"""<vr-point x='${x}' y='${y}'></vr-point>
    """
  }

}
