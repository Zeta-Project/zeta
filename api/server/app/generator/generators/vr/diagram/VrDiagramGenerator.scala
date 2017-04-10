package generator.generators.vr.diagram

import java.nio.file.Files
import java.nio.file.Paths

import generator.model.diagram.Diagram
import generator.model.diagram.edge.Edge

/**
 * Created by max on 12.11.16.
 */
object VrDiagramGenerator {
  private val EXTENDED_NEW_BEHAVIOR = "vr-new-extended.html"
  private val EXTENDED_CONNECT_BEHAVIOR = "vr-connect-extended.html"
  private val SCENE = "vr-scene.html"
  private val SAVE_BEHAVIOR = "vr-save.html"

  def doGenerate(diagram: Diagram, location: String) {
    val DEFAULT_DIAGRAM_LOCATION = location

    val nodes = diagram.nodes
    val connections = diagram.edges.map(getConnection(_)).groupBy(_.name).map(_._2.head)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + EXTENDED_NEW_BEHAVIOR), VrGeneratorNewBehavior.generate(nodes).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + EXTENDED_CONNECT_BEHAVIOR), VrGeneratorConnectBehavior.generate(connections, diagram.edges).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + SCENE), VrGeneratorScene.generate(nodes, connections).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + SAVE_BEHAVIOR), VrGeneratorSaveBehavior.generate(nodes, connections, diagram).getBytes)
  }

  // copied from ValidatorGenerator
  def getConnection(edge: Edge) = {
    val connectionReference = edge.connection.referencedConnection
    if (connectionReference isDefined) {
      connectionReference.get
    } else {
      throw new NoSuchElementException("No connection defined for edge " + edge.name)
    }
  }
}
