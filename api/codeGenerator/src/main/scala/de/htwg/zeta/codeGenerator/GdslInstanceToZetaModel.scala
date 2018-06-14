package de.htwg.zeta.codeGenerator


import scala.collection.immutable.Seq
import scala.collection.mutable

import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.Link
import de.htwg.zeta.codeGenerator.model.Value
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import grizzled.slf4j.Logging
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json


object GdslInstanceToZetaModel extends Logging {

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


    val entities = gdslInstance.nodes.filter(_.className == "Entity").map { node =>
      val fixValues = node.attributeValues.get("fix").flatMap(extractValue).toList
      val inValues = node.attributeValues.get("in").flatMap(extractValue).toList
      val outValues = node.attributeValues.get("out").flatMap(extractValue).toList
      val name = getEntityName(node, gdslInstance)
      Entity(name, fixValues, inValues, outValues, Nil, Nil, Nil)
    }

    for {
      entity <- entities.toList
    } yield {
      val generatedFile = KlimaCodeGenerator.generateSingleEntity(entity)
      val beautifiedFile = ScalaCodeBeautifier.format(generatedFile, entity.name)

      File(gdslInstance.id, entity.name + ".scala", beautifiedFile)
    }
  }

  private def extractValue(at: AttributeValue): Option[Value] = at match {
    case StringValue(combinedString) =>
      combinedString.split(":", 2).toList match {
        case name :: tpe :: Nil => Some(Value(name, tpe))
        case _ => None
      }

    case _ => None
  }

  private def getEntityName(node: NodeInstance, gdslInstance: GraphicalDslInstance) = {

    val idForEntityName = "name"

    val id = node.name
    val uiState = Json.parse(gdslInstance.uiState)
    val cells = (uiState \ "cells").as[List[JsValue]]

    val cell: JsValue = cells.find { cell =>
      val cellId = (cell \ "id").as[String]
      cellId == id
    }.get

    val attributeInfos: List[JsObject] = (cell \ "mClassAttributeInfo").as[List[JsObject]]

    val o: JsObject = attributeInfos.find { attributeInfo: JsValue =>
      val name = (attributeInfo \ "name").as[String]
      name == idForEntityName
    }.get

    val generatedId = (o \ "id").as[String] // something like 0000-0000000000-0000-dead

    val attrs = (cell \ "attrs").as[JsObject]
    val attr = attrs.value.find { case (name, value) =>
      name == s"text.$generatedId"
    }.get._2

    val entityName = (attr \ "text").as[String]
    entityName
  }

}
