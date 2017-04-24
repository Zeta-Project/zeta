package generator.generators.vr.diagram

import java.nio.file.Files
import java.nio.file.Paths

import generator.model.diagram.Diagram
import generator.model.diagram.edge.Edge
import generator.model.shapecontainer.connection.Connection
import models.file.File


object VrDiagramGenerator {
  val EXTENDED_NEW_BEHAVIOR = "vr-new-extended.html"
  val EXTENDED_CONNECT_BEHAVIOR = "vr-connect-extended.html"
  val SCENE = "vr-scene.html"
  val SAVE_BEHAVIOR = "vr-save.html"

  def doGenerate(diagram: Diagram, location: String): Unit = {
    doGenerateFiles(diagram, location).map(f => Files.write(Paths.get(f.name), f.content.getBytes()))
  }

  def doGenerateFiles(diagram: Diagram, location: String): List[File] = {
    val DEFAULT_DIAGRAM_LOCATION = location
    val nodes = diagram.nodes
    val connections = diagram.edges.map(getConnection).groupBy(_.name).map(_._2.head)

    List(
      File(DEFAULT_DIAGRAM_LOCATION + EXTENDED_NEW_BEHAVIOR, VrGeneratorNewBehavior.generate(nodes)),
      File(DEFAULT_DIAGRAM_LOCATION + EXTENDED_CONNECT_BEHAVIOR, VrGeneratorConnectBehavior.generate(connections, diagram.edges)),
      File(DEFAULT_DIAGRAM_LOCATION + SCENE, VrGeneratorScene.generate(nodes, connections)),
      File(DEFAULT_DIAGRAM_LOCATION + SAVE_BEHAVIOR, VrGeneratorSaveBehavior.generate(nodes, connections, diagram))
    )
  }

  // copied from ValidatorGenerator
  def getConnection(edge: Edge): Connection = {
    val connectionReference = edge.connection.referencedConnection
    if (connectionReference.isDefined) {
      connectionReference.get
    } else {
      throw new NoSuchElementException("No connection defined for edge " + edge.name)
    }
  }
}

case class VrDiagramGenerator(vrNew: String, vrConnect: String, vrScene: String, vrSave: String)


