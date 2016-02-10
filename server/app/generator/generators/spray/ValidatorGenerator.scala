package generator.generators.spray

import generator.generators.MclassMock
import generator.model.diagram.Diagram
import generator.model.diagram.edge.Edge
import generator.model.diagram.node.Node
import scala.collection.mutable.HashMap

/**
 * Created by julian on 07.02.16.
 */
object ValidatorGenerator {
  def generate( diagram:Diagram)= {
    s"""
    $generateHead
    var validator = {
      ${generateTargetMatrix(diagram)}

      ${generateSourceMatrix(diagram)}

      ${generateEdgeData(diagram)}

      ${generateCompartmentMatrix(diagram)}

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

  def generateTargetMatrix( diagram:Diagram) = {
    val targetMatrix = getTargetMatrix(diagram)
    val clazzes = targetMatrix
    s"""
    targetMatrix: {
      ${for(((key, value), i) <- clazzes.zipWithIndex) yield
        s"""${key.name}: {
      ${generateEdgeMap(value)}
    }${if(i != clazzes.size)","}"""}
    },
    """
  }

  def generateEdgeMap(edgeMap:HashMap[String, Boolean]) = {
    s"""
    ${
      val edges = edgeMap
      for (((key, value), i) <- edges.zipWithIndex) yield
        s"""${key}: ${if (value) "true" else "false"}${if (i != edges.size) ","}"""
    }
    """
  }

  def generateSourceMatrix(diagram:Diagram) = {
    s"""
    sourceMatrix: {
      ${
      val sourceMatrix = getSourceMatrix(diagram)
      val clazzes = sourceMatrix
      for(((key, value), i) <- clazzes.zipWithIndex) yield
        s"""${key.name}: {
        ${generateEdgeMap(value)}
        }${if(i != clazzes.size)","}"""
      }
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

  def generateEdgeData( diagram:Diagram) = {
    s"""
    edgeData: {
      ${for(edge <- diagram.edges) yield
      s"""${edge.name}: {
      type: "${edge.mcoreElement.name}",
      from: "${edge.from.name}",
      to: "${edge.to.name}",
      style: "${getStyleForEdge(edge/*, diagram.style.dslStyle TODO getStyleForEdge doesnt use style and returns a connection not a style?!!?!?*/)}"
    }${if(edge != diagram.edges.last)"," else ""}"""
      }
    },
    """
  }

  def generateCompartmentMatrix( diagram:Diagram) = {
    s"""
    compartmentMatrix: {
      ${val compartmentMatrix = getCompartmentMatrix(diagram.nodes)
      for(((key, value), i) <- compartmentMatrix.zipWithIndex) yield
      s"""${key.name}: {
      ${generateCompartmentMap(value)}
    }${if(i != compartmentMatrix.size)","}"""
      }
    },
    """
  }

  def generateCompartmentMap(compartmentMap:HashMap[String, List[String]])={
    s"""
      ${
      val compartmentData = compartmentMap
      for (((key, value), i) <- compartmentData.zipWithIndex) yield
      s"""$key: [${for (compartment <- value) yield s""""$compartment"${if (compartment != value.last) ", "}"""}]${if (i != compartmentData.size) ", "}"""
    }"""
  }

  /*TODO ask markus what methode/field type does*/
  private def getTargetMatrix( diagram:Diagram) = {
    val targetMatrix = new HashMap[MclassMock/*TODO MClass*/, HashMap[String, Boolean]]
    for(node <- diagram.nodes){
      val edgeTargetMap = getEdgeTargetMap(node, diagram.edges)
      targetMatrix.put(node.mcoreElement, edgeTargetMap)
    }
   targetMatrix
  }

  private def getSourceMatrix(diagram:Diagram) ={
    val sourceMatrix = new HashMap[MclassMock, HashMap[String, Boolean]]
    for(node <- diagram.nodes){
      val edgeSourceMap = getEdgeSourceMap(node, diagram.edges)
      sourceMatrix.put(node.mcoreElement, edgeSourceMap)
    }
    sourceMatrix
  }

  private def getCompartmentMatrix(nodes:List[Node])={
    val compartmentMatrix = new HashMap[Node, HashMap[String, List[String]]]
    for(node <- nodes){
      val compartmentMap = getCompartmentMap(node, nodes)
      compartmentMatrix.put(node, compartmentMap)
    }
    compartmentMatrix
  }

  private def getEdgeTargetMap(node:Node, edges:List[Edge])={
    val edgeTargetMap = HashMap[String, Boolean]()
    val nodeClass = node.mcoreElement
    for(edge <- edges){
      val targetClass = edge.to.EReferenceType
      edgeTargetMap.put(edge.name, targetClass.isSuperTypeOf(nodeClass))
    }
    edgeTargetMap
  }

  private def getEdgeSourceMap( node:Node, edges:List[Edge])={
    val edgeSourceMap = new HashMap[String, Boolean]
    val nodeClass = node.mcoreElement
    for(edge <- edges){
      val sourceClass = edge.from.EReferenceType
      edgeSourceMap.put(edge.name, sourceClass.isSuperTypeOf(nodeClass))
    }
    edgeSourceMap
  }

  private def getCompartmentMap(node:Node, nodeList:List[Node])={
    val compartmentMap = new HashMap[String, List[String]]
    val nodeClass = node.mcoreElement
    /*TODO why list with parentnodes? compartmentmap is inherited now. what is meant wirh compartmentMap? the "nests" in DiaShape or the compartmentmap of Shape*/
    for(parent <- nodeList){
      var validCompartments = List[String]()
      if(parent.shape isDefined){
        for((name, compartment ) <- parent.shape.get.nests /*compartments*/){
          //TODO what is meant by nestedShape? According to Shape.xtext compartment doesnt hold a Shape
          if(compartment.nestedShape.EReferenceType.isSuperTypeOf(nodeClass)){
            validCompartments =  compartment.nestedShape.name :: validCompartments
          }
        }
      }
      compartmentMap.put(parent.name, validCompartments)
    }
    compartmentMap
  }

  private def getStyleForEdge(edge:Edge)={
    val connection = getConnection(edge)
    connection.name
  }

  private def getConnection(edge:Edge)={
    val connectionReference = edge.connection.referencedConnection
    if(connectionReference isDefined){
      connectionReference.get
    }else{
      throw new NoSuchElementException("No connection defined for edge "+edge.name)
    }
  }
}
