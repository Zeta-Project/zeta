package de.htwg.zeta.codeGenerator


import scala.annotation.tailrec
import scala.collection.immutable.Seq
import scala.collection.mutable.ListBuffer

import de.htwg.zeta.codeGenerator.model.Entity
import de.htwg.zeta.codeGenerator.model.Link
import de.htwg.zeta.codeGenerator.model.MapLink
import de.htwg.zeta.codeGenerator.model.ReferenceLink
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
import play.api.libs.json.JsonValidationError


object GdslInstanceToZetaModel extends Logging {

  private case class GDSLState(
      nodes: Map[String, NodeInstance],
      edges: Map[String, EdgeInstance]
  )

  // scalastyle:off
  def generate(concept: Concept, gdslInstance: GraphicalDslInstance): Seq[File] = {
    val state = GDSLState(
      gdslInstance.nodes.map(n => n.name -> n).toMap,
      gdslInstance.edges.map(e => e.name -> e).toMap
    )

    val anchor = for {
      teamAnch <- gdslInstance.nodes.find("TeamAnchor" == _.className)
      periodAnch <- gdslInstance.nodes.find("PeriodAnchor" == _.className)
      teamOutId <- expectOneElem(teamAnch.outputEdgeNames)
      periodOutId <- expectOneElem(periodAnch.outputEdgeNames)
      teamOut <- state.edges.get(teamOutId)
      periodOut <- state.edges.get(periodOutId)
      teamNode <- state.nodes.get(teamOut.targetNodeName)
      periodNode: NodeInstance <- state.nodes.get(periodOut.targetNodeName)
      teamEntity <- extractEntity(teamNode, state)
      periodEntity <- extractEntity(periodNode, state)
    } yield {
      model.Anchor(teamEntity, periodEntity)
    }

    // todo remove
    val entities = gdslInstance.nodes.filter(_.className == "Entity").map { node =>
      val fixValues = node.attributeValues.get("fix").toList.flatMap(extractValue)
      val inValues = node.attributeValues.get("in").toList.flatMap(extractValue)
      val outValues = node.attributeValues.get("out").toList.flatMap(extractValue)
      val name = getEntityName(node, gdslInstance)
      Entity(name, fixValues, inValues, outValues, Nil, Nil, Nil)
    }

    // todo replace with anchor.
    for {
      entity <- entities.toList
    } yield {
      val fileName = s"${entity.name}.scala"
      val generatedFile = KlimaCodeGenerator.generateSingleEntity(entity)
      val beautifiedFile = ScalaCodeBeautifier.format(fileName, generatedFile)

      File(gdslInstance.id, fileName, beautifiedFile)
    }
  }

  private def expectOneElem[E](s: Seq[E]): Option[E] =
    s.headOption.filter(_ => s.tail.isEmpty) // safe access to

  private def mapAllOrNone[E, R](seq: Seq[E])(map: E => Option[R]): Option[List[R]] = {
    val buff = ListBuffer[R]()
    @tailrec def rek(s: List[E]): Option[List[R]] = s match {
      case Nil => Some(buff.result())
      case head :: tail => map(head) match {
        case None => None
        case Some(r) =>
          buff += r
          rek(tail)
      }
    }
    rek(seq.toList)
  }

  private def extractEntity(node: NodeInstance, state: GDSLState): Option[Entity] = {
    val fixValues = node.attributeValues.get("fix").toList.flatMap(extractValue)
    val inValues = node.attributeValues.get("in").toList.flatMap(extractValue)
    val outValues = node.attributeValues.get("out").toList.flatMap(extractValue)
    mapAllOrNone(node.outputEdgeNames)(en => state.edges.get(en))
    node.outputEdgeNames.map(en => state.edges.get(en))

    for {
      name <- node.attributeValues.get("name").flatMap(expectOneElem).collect { case StringValue(n) => n.trim }
      edges <- mapAllOrNone(node.outputEdgeNames)(en => state.edges.get(en))
      // names are from shape dsl
      linkRef = edges.filter("LinkEdge" == _.referenceName)
      mapRef = edges.filter("MapEdge" == _.referenceName) // this is not yet defined in shape
      refRef = edges.filter("ReferenceEdge" == _.referenceName)
      links <- mapAllOrNone(linkRef)(lr => extractLink(lr, state))
      mapLinks <- mapAllOrNone(mapRef)(lr => extractMapLink(lr, state))
      refLinks <- mapAllOrNone(refRef)(lr => extractReferenceLink(lr, state))
    } yield {
      Entity(name.trim, fixValues, inValues, outValues, links, mapLinks, refLinks)
    }
  }


  private def extractLink(edge: EdgeInstance, state: GDSLState): Option[Link] = {
    for {
      node <- state.nodes.get(edge.targetNodeName)
      entity <- extractEntity(node, state)
    } yield {
      // TODO name
      Link(null, entity)
    }
  }

  private def extractMapLink(edge: EdgeInstance, state: GDSLState): Option[MapLink] = {
    for {
      node <- state.nodes.get(edge.targetNodeName)
      entity <- extractEntity(node, state)
    } yield {
      // TODO
      MapLink(null, null, entity)
    }
  }

  private def extractReferenceLink(edge: EdgeInstance, state: GDSLState): Option[ReferenceLink] = {
    for {
      node <- state.nodes.get(edge.targetNodeName)
    } yield {
      // TODO extract path later.
      ReferenceLink(null, null)
    }
  }


  private def extractValue(list: List[AttributeValue]): List[Value] = {
    list.flatMap(extractSingleValue)
  }

  private def extractSingleValue(at: AttributeValue): Option[Value] = at match {
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

    val entityName = (attr \ "text").validate[List[String]].collect(JsonValidationError("entityName must be one element")) { case name :: Nil => name }.get
    entityName
  }

}
