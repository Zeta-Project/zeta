package de.htwg.zeta.server.generator.generators.vr.diagram

import java.util.UUID

import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.edge.Edge
import de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection
import models.file.File
import de.htwg.zeta.server.model.result.Unreliable


object VrDiagramGenerator {
  private val EXTENDED_NEW_BEHAVIOR = "vr-new-extended.html"
  private val EXTENDED_CONNECT_BEHAVIOR = "vr-connect-extended.html"
  private val SCENE = "vr-scene.html"
  private val SAVE_BEHAVIOR = "vr-save.html"

  def doGenerateResult(diagram: Diagram): Unreliable[List[File]] = {
    Unreliable(() => doGenerateGenerators(diagram), "failed trying to create the vr Diagram generators")
  }

  private def doGenerateGenerators(diagram: Diagram): List[File] = {
    val nodes = diagram.nodes
    val connections = diagram.edges.map(getConnection).groupBy(_.name).map(_._2.head)

    List(
      File(UUID.randomUUID, EXTENDED_NEW_BEHAVIOR, VrGeneratorNewBehavior.generate(nodes)),
      File(UUID.randomUUID, EXTENDED_CONNECT_BEHAVIOR, VrGeneratorConnectBehavior.generate(connections, diagram.edges)),
      File(UUID.randomUUID, SCENE, VrGeneratorScene.generate(nodes, connections)),
      File(UUID.randomUUID, SAVE_BEHAVIOR, VrGeneratorSaveBehavior.generate(nodes, connections, diagram))
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


