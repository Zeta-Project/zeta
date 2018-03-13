package de.htwg.zeta.server.generator.generators.diagram

import scala.collection.immutable.Seq
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.edge.Edge
import de.htwg.zeta.server.generator.model.diagram.node.Node

/**
 * ValidatorGenerator is responsible for the creation of the String for validator.js
 */
object ValidatorGenerator {

  /**
   * generates matrix for validation of link types and checking upper and lower bounds
   */
  def generate(diagram: Diagram) = {
    // TODO: fix ${generateCompartmentMatrix(diagram)}
    s"""
    $generateHead
    var validator = {

      ${generateInOutMatrix(diagram)}

      ${generateTargetMatrix(diagram)}

      ${generateSourceMatrix(diagram)}

      ${generateEdgeData(diagram)}

      $generateAccessMethods
    };
    """
  }

  def generateHead =
    """
    /*
     * This is a generated validator file for JointJS
     */
    """

  def generateInOutMatrix(diagram: Diagram) = {
    var inputMatrix = new ListBuffer[String]
    var outputMatrix = new ListBuffer[String]
    diagram.metamodel.concept.classMap.foreach { e =>
      e._2 match {
        case mc: MClass =>
          if (mc.inputReferenceNames.nonEmpty) {
            inputMatrix += generateInOutMatrixForMClass(mc.inputReferenceNames, mc.name)
          }
          if (mc.outputReferenceNames.nonEmpty) {
            outputMatrix += generateInOutMatrixForMClass(mc.outputReferenceNames, mc.name)
          }
        case _ =>
      }
    }

    s"""
    inputMatrix: {
      ${inputMatrix.mkString(",")}
    },
    outputMatrix: {
      ${outputMatrix.mkString(",")}
    },
    """
  }

  def generateInOutMatrixForMClass(inputs: Seq[String], mcName: String): String = {
    s"""
    $mcName: {
      ${
        (for {input <- inputs} yield s"""$input: {
          upperBound: ${1},
          lowerBound: ${1}}"""
        ).mkString(",")
      }
    }
    """
  }

  def generateTargetMatrix(diagram: Diagram) = {
    val targetMatrix = getTargetMatrix(diagram)
    val clazzes = targetMatrix
    s"""
    targetMatrix: {
      ${
      {
        for {((key, value), i) <- clazzes.zipWithIndex} yield s"""$key: {
      ${generateEdgeMap(value)}
    }"""
      }.mkString(",")
    }},
    """
  }

  def generateEdgeMap(edgeMap: HashMap[String, Boolean]) = {
    s"""
    ${
      (for {((key, value), i) <- edgeMap.zipWithIndex} yield s"""$key: ${if (value) "true" else "false"}""").mkString(",")
    }
    """
  }

  def generateSourceMatrix(diagram: Diagram) = {
    val sourceMatrix = getSourceMatrix(diagram)
    val s = for {((key, value), i) <- sourceMatrix.zipWithIndex} yield s"""$key: {
      ${generateEdgeMap(value)}
    }"""

    s"""
    sourceMatrix: {
      ${s.mkString(",")}
    },
    """
  }

  def generateAccessMethods = {
    """
    isValidTarget: function(nodeName, edgeName){
      return this.targetMatrix[nodeName][edgeName];
    },

    isValidSource: function(nodeName, edgeName){
      return this.sourceMatrix[nodeName][edgeName];
    },

    getEdgeData: function(edgeName){
      return this.edgeData[edgeName];
    },

    getValidEdges: function(sourceName, targetName){
      var validEdges = [];
      var candidateEdges = Object.keys(this.sourceMatrix[sourceName]);
      for(var i=0; i < candidateEdges.length; i++){
        if(this.isValidSource(sourceName, candidateEdges[i]) && this.isValidTarget(targetName, candidateEdges[i])){
          validEdges.push(candidateEdges[i]);
        }
      }

      return validEdges;
    },

    getValidCompartments: function(childName, parentName){
      return this.compartmentMatrix[childName][parentName];
    },

    isValidChild: function(childName, parentName){
      return this.getValidCompartments(childName, parentName).length > 0;
    }
    """
  }

  def generateEdgeData(diagram: Diagram) = {
    s"""
    edgeData: {
      ${
      {
        for {edge <- diagram.edges} yield s"""${edge.name}: {
      type: "${edge.mcoreElement.name}",
      from: "${edge.from.name}",
      to: "${edge.to.name}",
      style: "${getStyleForEdge(edge)}"
    }${if (edge != diagram.edges.last) "," else ""}"""
      }.mkString
    }
    },
    """
  }

  def generateCompartmentMatrix(diagram: Diagram) = {
    s"""
    compartmentMatrix: {
      ${
      val compartmentMatrix = getCompartmentMatrix(diagram.nodes)
      for {((key, value), i) <- compartmentMatrix.zipWithIndex} yield s"""${key.name}: {
      ${generateCompartmentMap(value)}
    }${if (i != compartmentMatrix.size) ","}"""
    }
    },
    """
  }

  def generateCompartmentMap(compartmentMap: HashMap[String, List[String]]) = {
    s"""
    ${
      val compartmentData = compartmentMap
      for {((key, value), i) <- compartmentData.zipWithIndex}
        yield s"""$key: [${
          for (compartment <- value)
            yield s""""$compartment"${if (compartment != value.last) ", "}"""
        }]${if (i != compartmentData.size) ", "}"""
    }
    """
  }

  private def getTargetMatrix(diagram: Diagram) = {
    val targetMatrix = new HashMap[String, HashMap[String, Boolean]]
    for {node <- diagram.nodes} {
      val edgeTargetMap = getEdgeTargetMap(node, diagram.edges)
      targetMatrix.put(node.name, edgeTargetMap)
    }
    targetMatrix
  }

  private def getSourceMatrix(diagram: Diagram) = {
    val sourceMatrix = new HashMap[String, HashMap[String, Boolean]]
    for {node <- diagram.nodes} {
      val edgeSourceMap = getEdgeSourceMap(node, diagram.edges)
      sourceMatrix.put(node.name, edgeSourceMap)
    }
    sourceMatrix
  }

  private def getCompartmentMatrix(nodes: List[Node]) = {
    val compartmentMatrix = new HashMap[Node, HashMap[String, List[String]]]
    for {node <- nodes} {
      val compartmentMap = getCompartmentMap(node, nodes)
      compartmentMatrix.put(node, compartmentMap)
    }
    compartmentMatrix
  }

  private def getEdgeTargetMap(node: Node, edges: List[Edge]) = {
    val edgeTargetMap = HashMap[String, Boolean]()
    val nodeClass = node.mcoreElement
    for {edge <- edges} {
      val targetClass = edge.to
      val superTypeIsValidTarget = nodeClass.superTypeNames.contains(targetClass.name)
      edgeTargetMap.put(edge.name, nodeClass.name == targetClass.name || superTypeIsValidTarget)
    }
    edgeTargetMap
  }

  private def getEdgeSourceMap(node: Node, edges: List[Edge]) = {
    val edgeSourceMap = new HashMap[String, Boolean]
    val nodeClass = node.mcoreElement
    for {edge <- edges} {
      val sourceClass = edge.from
      val superTypeIsValidSource = nodeClass.superTypeNames.contains(sourceClass.name)
      edgeSourceMap.put(edge.name, nodeClass.name == sourceClass.name || superTypeIsValidSource)
    }
    edgeSourceMap
  }

  /**
   * Since compartment definitions in shape.xtext are declared as depcrecated this method is not usable anymore
   */
  @deprecated
  private def getCompartmentMap(node: Node, nodeList: List[Node]) = {
    val compartmentMap = new HashMap[String, List[String]]
    val nodeClass = node.mcoreElement
    for {parent <- nodeList} {
      var validCompartments = List[String]()
      if (parent.shape.isDefined) {
        for {(name, compartment) <- parent.shape.get.nests} {
          /* compartments */
          // TODO cant be resolved since compartments have no nestedShape  - Spray.xtext says compartments are (Ereference -> Shape) mapping.... ?!?!?
          // if(compartment.nestedShape.EReferenceType.isSuperTypeOf(nodeClass)){
          //   validCompartments =  compartment.nestedShape.name :: validCompartments
          // }
        }
      }
      compartmentMap.put(parent.name, validCompartments)
    }
    compartmentMap
  }

  private def getStyleForEdge(edge: Edge) = {
    val connection = getConnection(edge)
    connection.name
  }

  private def getConnection(edge: Edge) = {
    val connectionReference = edge.connection.referencedConnection
    if (connectionReference isDefined) {
      connectionReference.get
    } else {
      throw new NoSuchElementException("No connection defined for edge " + edge.name)
    }
  }
}
