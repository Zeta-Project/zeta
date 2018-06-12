package de.htwg.zeta.codeGenerator


import de.htwg.zeta.codeGenerator.model.{Entity, Link}
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance


import scala.collection.immutable.Seq
import scala.collection.mutable


object GdslInstanceToZetaModel {

  // scalastyle:off
  def generate(concept: Concept, gdslInstance: GraphicalDslInstance): Seq[File] = {


    val unprocessedNodes = mutable.ListBuffer(gdslInstance.nodes: _*).filter(_.className == "Entity")
    val processedEntities = mutable.ListBuffer.empty[Entity]

    val nonProcessableEdges = mutable.ListBuffer(gdslInstance.edges: _*).filter(_.referenceName == "LinkEdge") // edge pointing to unprocessed edges
    val processableEdges = mutable.ListBuffer.empty[EdgeInstance] // edges pointing to processed edges




    def processRecursive(): Option[Entity] = {
      // A node is processable, when all outgoing dependencies are processed

      val processableNode = unprocessedNodes.find(node =>
        // All output edges needs to be processed
        node.outputEdgeNames.forall(processableEdges.map(_.name).contains)
      )


      processableNode match {
        case Some(node) =>

          unprocessedNodes -= node

          val incomingEdges = nonProcessableEdges.filter(node.inputEdgeNames.contains)
          nonProcessableEdges --= incomingEdges
          processableEdges ++= incomingEdges

          val entity = Entity(
            name = node.name,
            fixValues = List.empty, // TODO
            inValues = List.empty, // TODO
            outValues = List.empty, // TODO
            links = processedEntities.filter(node.outputEdgeNames.contains).map(entity =>
              Link(entity.name, entity)
            ).toList,
            maps = List.empty, // TODO Not possible in current project state
            refs = List.empty // TODO Not possible in current project state
          )

          processedEntities += entity

          if (unprocessedNodes.isEmpty) {
            Some(entity)
          } else {
            processRecursive()
          }

        case None => None
      }


    }


    // TODO check two dropAnchor elements exists and pointing to same entity

    processRecursive() match {
      case Some(result) =>
        List(
          File(gdslInstance.id, "Entity", result.toString)
        )
      case None =>
        List(
          File(gdslInstance.id, "Error", "Converting Zeta-Model to Klima-Model failed: " + processedEntities.size + " - " + processableEdges.size + " - " + nonProcessableEdges.size)
        )
    }


  }

}
