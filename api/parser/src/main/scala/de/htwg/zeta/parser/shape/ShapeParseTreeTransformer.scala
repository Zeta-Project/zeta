package de.htwg.zeta.parser.shape

import scala.collection.mutable.ListBuffer
import scala.collection.immutable
import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HasIdentifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RepeatingBoxParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import de.htwg.zeta.server.generator.model.style.Style


object ShapeParseTreeTransformer {

  case class NodesAndEdges(nodes: List[Node], edges: List[Edge])

  /**
   * Transform a list of shape parse trees into model instances
   *
   * @param shapeParseTrees List of shape parse trees to transform
   * @param styles          List of styles
   * @param concept         Concept container
   * @return     Validation, i.e.
   *             - either Failure (with a list of error messages)
   *             - or     Success (with a tuple, list of nodes and list of edges)
   */
  def transformShapes(
      shapeParseTrees: List[ShapeParseTree],
      styles: List[Style],
      concept: Concept): Validation[List[String], NodesAndEdges] = {
    checkForErrors(shapeParseTrees, styles, concept) match {
      case Nil =>
        val (nodes, edges) = doTransformShapes(shapeParseTrees, styles, concept)
        val nodesAndEdges = NodesAndEdges(nodes, edges)
        Success(nodesAndEdges)
      case errors: List[String] =>
        Failure(errors)
    }
  }

  // apply a number of checks and return error messages
  private def checkForErrors(shapeParseTrees: List[ShapeParseTree], styles: List[Style], concept: Concept): List[String] = {
    val maybeErrors = List(
      checkForDuplicateShapes(shapeParseTrees),
      checkForUndefinedEdges(shapeParseTrees),
      checkForUndefinedStyles(shapeParseTrees, styles),
      checkForUndefinedConceptElements(shapeParseTrees, concept)
    )
    maybeErrors.collect {
      case Some(error) => error
    }
  }

  // check if there are any shapes with the same identifier
  private def checkForDuplicateShapes(shapeParseTrees: List[ShapeParseTree]): Option[String] = {
    val findDuplicates = new FindDuplicates[ShapeParseTree](_.identifier)
    findDuplicates(shapeParseTrees) match {
      case Nil =>
        None
      case duplicates: List[Id] =>
        Some(s"The following shapes are defined multiple times: ${duplicates.mkString(",")}")
    }
  }

  // check if there are nodes which reference an edge which is not defined
  private def checkForUndefinedEdges(shapeParseTrees: List[ShapeParseTree]): Option[String] = {
    val definedEdges = shapeParseTrees.collect {
      case edge: EdgeParseTree => edge.identifier
    }.toSet

    val referencedEdges = shapeParseTrees.collect {
      case node: NodeParseTree => node.edges
    }.flatten.toSet

    val undefinedEdges = referencedEdges.diff(definedEdges).toList

    undefinedEdges match {
      case Nil =>
        None
      case _ =>
        Some(s"The following edges are referenced but not defined: ${undefinedEdges.mkString(",")}")
    }
  }


  // check if there are styles referenced which are not defined
  private def checkForUndefinedStyles(shapeParseTrees: List[ShapeParseTree], styles: List[Style]): Option[String] = {
    val definedStyles = styles.map(_.name).toSet

    val referencedStyles = shapeParseTrees.collect {
      case node: NodeParseTree =>
        node.allGeoModels.flatMap(_.style).map(_.name) ++ node.style.map(_.name).toList
      case edge: EdgeParseTree =>
        edge.placings.map(_.geoModel).flatMap(_.style).map(_.name)
    }.flatten.toSet

    val undefinedStyles = referencedStyles.diff(definedStyles).toList

    undefinedStyles match {
      case Nil =>
        None
      case _ =>
        Some(s"The following styles are referenced but not defined: ${undefinedStyles.mkString(",")}")
    }
  }

  // check if there are concept elements referenced which are not defined
  private def checkForUndefinedConceptElements(shapeParseTrees: List[ShapeParseTree], concept: Concept): Option[String] = {
    val nodes = shapeParseTrees.collect { case n: NodeParseTree => n }
    val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }

    val nodeErrors = checkNodesForUndefinedConceptElements(nodes, concept)
    val edgeErrors = checkEdgesForUndefinedConceptElements(edges, concept)

    nodeErrors ++ edgeErrors match {
      case Nil => None
      case errors: List[String] => Some(errors.mkString(",\n"))
    }
  }

  //noinspection ScalaStyle
  // this thing is a mess!
  private def checkNodesForUndefinedConceptElements(nodeParseTrees: List[NodeParseTree], concept: Concept): List[String] = {
    var errors = new ListBuffer[String]()

    // check if there is an attribute or method with name 'identifier' in class 'context'
    def isValidIdentifier(identifier: String, context: MClass): Boolean = {
      val maybeAttribute = context.attributes.find(_.name == identifier)
      val maybeMethod = context.methods.find(_.name == identifier)
      (maybeAttribute, maybeMethod) match {
        // attribute type / method return type may not be unit!
        case (Some(attr), _) if attr.typ != UnitType => true
        case (_, Some(method)) if method.returnType != UnitType => true
        case (_, _) => false
      }
    }

    // check if a specified identifier is in a valid context
    def checkConceptIdentifier(geoModel: GeoModelParseTree with HasIdentifier, contexts: Map[String, MClass]): Unit = {
      val (prefix, identifier) = geoModel.identifier.split
      contexts.get(prefix) match {
        case None =>
          errors += s"Illegal prefix '$prefix' specified!"
        case Some(context) if !isValidIdentifier(identifier, context) =>
          errors += s"Textfield identifier '$identifier' not found or it has return type 'Unit'!"
        case _ => // identifier is valid in the given context
      }
      geoModel.children.foreach(child => check(child, contexts))
    }

    def getReferenceOrThrow(referenceName: String): MReference = {
      concept.references.find(_.name == referenceName)
        .getOrElse(throw new Exception(s"Concept model is invalid! Reference '$referenceName' does not exist!"))
    }

    def getClassOrThrow(className: String): MClass = {
      concept.classes.find(_.name == className)
        .getOrElse(throw new Exception(s"Concept model is invalid! Class '$className' does not exist!"))
    }

    def checkConceptReference(repeatingBox: RepeatingBoxParseTree, contexts: Map[String, MClass]): Unit = {

      // check if there is already a context with this prefix
      val newPrefix = repeatingBox.foreach.as
      if (contexts.contains(newPrefix)) {
        errors += s"Prefix '$newPrefix' already defined in outer scope!"
        return
      }

      val (prefix, referenceName) = repeatingBox.foreach.each.split
      contexts.get(prefix) match {
        case None =>
          errors += s"RepeatingBox reference name '$referenceName' not found!"
        case Some(context) if !context.outputReferenceNames.contains(referenceName) =>
          errors += s"Concept class '${context.name}' has no reference named '$referenceName'!"
        case Some(_) =>
          val reference = getReferenceOrThrow(referenceName)
          val newContext = getClassOrThrow(reference.targetClassName)
          val updatedContexts = contexts + (newPrefix -> newContext)
          repeatingBox.children.foreach(child => check(child, updatedContexts))
      }
    }

    def check(geoModel: GeoModelParseTree, contexts: Map[String, MClass]): Unit = geoModel match {
      case geoModel: HasIdentifier =>
        checkConceptIdentifier(geoModel, contexts)
      case repeatingBox: RepeatingBoxParseTree =>
        checkConceptReference(repeatingBox, contexts)
      case other: GeoModelParseTree =>
        other.children.foreach(child => check(child, contexts))
    }

    for {node <- nodeParseTrees} {
      val maybeClass = concept.classes.find(_.name == node.conceptClass)
      maybeClass match {
        case None =>
          errors += s"Concept class '${node.conceptClass}' for node '${node.identifier}' not found!"
        case Some(context) =>
          val defaultPrefix = ""
          val contexts = Map[String, MClass](defaultPrefix -> context)
          node.geoModels.foreach(geoModel => check(geoModel, contexts))
      }
    }

    errors.toList
  }

  // check if there are edges which reference undefined concept elements
  private def checkEdgesForUndefinedConceptElements(nodeParseTrees: List[EdgeParseTree], concept: Concept): List[String] = {
    Nil
  }

  def doTransformShapes(shapeParseTrees: List[ShapeParseTree], styles: List[Style], concept: Concept): (List[Node], List[Edge]) = {
    val edges = doTransformEdges(shapeParseTrees, styles, concept)
    val nodes = doTransformNodes(shapeParseTrees, edges, styles, concept)
    (nodes, edges)
  }

  def doTransformEdges(shapeParseTrees: List[ShapeParseTree], styles: List[Style], concept: Concept): List[Edge] = {
    shapeParseTrees.collect{case t: EdgeParseTree => t}.map(n => {
      Edge(n.identifier,
        "",
        "",
        "",
        immutable.Seq(),
        immutable.Map(),
        immutable.Seq())
    })
  }

  def doTransformNodes(shapeParseTrees: List[ShapeParseTree], edges: List[Edge], styles: List[Style], concept: Concept): List[Node] = {
    shapeParseTrees.collect{case t: NodeParseTree => t}.map(n => {
      Node(n.identifier,
        n.conceptClass,
        immutable.Seq(),
        immutable.Seq(),
        immutable.Seq(),
        immutable.Map(),
        immutable.Seq())
    })
  }

}
