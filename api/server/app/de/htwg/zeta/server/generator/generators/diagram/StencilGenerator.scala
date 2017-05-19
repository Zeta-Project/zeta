package de.htwg.zeta.server.generator.generators.diagram

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.node.Node

/**
 * The StencilGenerator object, responsible for the generation of the String for stencil.js
 */
object StencilGenerator {

  // FIXME variable in object
  var packageName = ""

  def generate(diagram: Diagram): String = {
    s"""
      |$generateHeader
      |${generateStencilGroups(diagram)}
      |${generateShapes(diagram)}
      |${generateGroupsToStencilMapping(getNodeToPaletteMapping(diagram))}
      |${generateDocumentReadyFunction(diagram)}
      |""".stripMargin
  }

  def generateHeader: String = {
    s"""
      |/*
      | * This is a generated stencil file for JointJS
      | */
      |""".stripMargin
  }

  def generateStencilGroups(diagram: Diagram): String = {
    var i = 1
    val groupSet = getNodeToPaletteMapping(diagram).keySet
    var groups = List[String]()
    for {groupName <- groupSet} {
      groups ::= getVarName(groupName) +
        s""": {index: $i, label: '$groupName' }"""
      i += 1
    }
    "Stencil.groups = {" + groups.mkString(",") + "};"
  }

  def generateShapes(diagram: Diagram): String = {

    def mCoreAttr(node: Node): String = {
      node.onCreate match {
        case None => ""
        case Some(oc) =>
          s"""mcoreAttributes: [
            |  {
            |    mcore: '${oc.askFor.name}',
            |    cellPath: ['attrs', '.label', 'text']
            |  }
            |],""".stripMargin
      }
    }

    {
      for {node: Node <- diagram.nodes} yield {
        s"""
          |var ${getVarName(node.name)} = new joint.shapes.$packageName.${getClassName(getShapeName(node))}({
          |  ${mCoreAttr(node)}
          |  nodeName: '${node.name}',
          |  mClass: '${node.mcoreElement.name}',
          |  mClassAttributeInfo: [${
          node.mcoreElement.attributes.map(
            attr =>
              s"""{ name: '${attr.name}', type: '${attr.`type`}'}"""
          ).mkString(",")
        }]
          |});
          |""".stripMargin
      }
    }.mkString
  }

  def generateGroupsToStencilMapping(mapping: mutable.HashMap[String, ListBuffer[Node]]): String = {
    s"""
      |Stencil.shapes = {
      ${
      {
        for {((key, value), i) <- mapping.zipWithIndex} yield {
          s"${generateShapesToGroupMapping(key, value, i == mapping.size)}"
        }
      }.mkString(",")
    }
      |};
      |""".stripMargin
  }

  def generateShapesToGroupMapping(group: String, nodes: ListBuffer[Node], isLast: Boolean): String = {
    s"""
      |${getVarName(group)}: [
      |  ${
      {
        for {node <- nodes} yield {
          s"""${
            getVarName(node.name) + {
              if (node != nodes.last) "," else ""
            }
          }
            """
        }
      }.mkString
    }
      |]
      |""".stripMargin
  }

  def generateDocumentReadyFunction(diagram: Diagram): String = {
    s"""
      |$$(document).ready(function() {
      |${
      {
        for {node <- diagram.nodes} yield {
          s"""
            |${getVarName(node.name)}.attr(getShapeStyle("${getClassName(getShapeName(node))}"));
            |${
            {
              for ((key, value) <- node.shape.get.vals) yield {
                s"""${getVarName(node.name)}.attr({'.${value.id}':{text: '${key}'}});"""
              }
            }.mkString
          }
            |""".stripMargin
        }
      }.mkString
    }
      |${
      diagram.style match {
        case None => ""
        case Some(style) =>
          s"""
            |var style = document.createElement('style');
            |style.id = 'highlighting-style';
            |style.type = 'text/css';
            |style.innerHTML = getDiagramHighlighting("${style.name}");
            |document.getElementsByTagName('head')[0].appendChild(style);
            |""".stripMargin
      }
    }
      |});""".stripMargin
  }

  def setPackageName(name: String): Unit = {
    packageName = name
  }

  private def getNodeToPaletteMapping(diagram: Diagram): mutable.HashMap[String, ListBuffer[Node]] = {
    var mapping = new mutable.HashMap[String, ListBuffer[Node]]
    for {node <- diagram.nodes} {
      val paletteName = node.palette.getOrElse("")
      if (mapping.contains(paletteName)) {
        mapping(paletteName) += node
      } else {
        mapping += (paletteName -> (ListBuffer[Node]() += node))
      }
    }
    mapping
  }

  private def getVarName(name: String): String = {
    val ret = name.replaceAll("\\W", "")
    ret.substring(0, 1).toLowerCase + ret.substring(1)
  }

  private def getClassName(name: String): String = {
    name.replaceAll("\\W", "")
  }

  private def getShapeName(node: Node): String = {
    val diaShape = node.shape
    if (diaShape.isDefined) {
      diaShape.get.referencedShape.name
    } else {
      throw new NoSuchElementException("No Shape defined for node " + node.name)
    }
  }
}

