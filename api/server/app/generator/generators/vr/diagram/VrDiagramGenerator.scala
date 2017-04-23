package generator.generators.vr.diagram

import java.nio.file.Files
import java.nio.file.Paths

import generator.model.diagram.Diagram
import generator.model.diagram.edge.Edge
import generator.model.shapecontainer.connection.Connection


object VrDiagramGenerator {
  val EXTENDED_NEW_BEHAVIOR = "vr-new-extended.html"
  val EXTENDED_CONNECT_BEHAVIOR = "vr-connect-extended.html"
  val SCENE = "vr-scene.html"
  val SAVE_BEHAVIOR = "vr-save.html"

  def doGenerate(diagram: Diagram, location: String): Unit = {
    val DEFAULT_DIAGRAM_LOCATION = location
    val vrDiaGen = doGenerateFiles(diagram)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + EXTENDED_NEW_BEHAVIOR), vrDiaGen.vrNew.getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + EXTENDED_CONNECT_BEHAVIOR), vrDiaGen.vrConnect.getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + SCENE), vrDiaGen.vrScene.getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + SAVE_BEHAVIOR), vrDiaGen.vrSave.getBytes)
  }


  def doGenerateFiles(diagram: Diagram): VrDiagramGenerator = {
    val nodes = diagram.nodes
    val connections = diagram.edges.map(getConnection).groupBy(_.name).map(_._2.head)

    VrDiagramGenerator(
      vrNew = VrGeneratorNewBehavior.generate(nodes),
      vrConnect = VrGeneratorConnectBehavior.generate(connections, diagram.edges),
      vrScene = VrGeneratorScene.generate(nodes, connections),
      vrSave = VrGeneratorSaveBehavior.generate(nodes, connections, diagram)
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


