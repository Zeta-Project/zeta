package de.htwg.zeta.parser.shape

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Edge, Node}
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.{GeoModelParseTree, HasIdentifier, RepeatingBoxParseTree, TextfieldParseTree}
import de.htwg.zeta.parser.shape.parsetree.{EdgeParseTree, NodeParseTree, ShapeParseTree}
import de.htwg.zeta.server.generator.model.style.Style

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scalaz.{Failure, Success, Validation}

object ShapeParseTreeTransformer {

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
  def transformShapes(shapeParseTrees: List[ShapeParseTree],
                      styles: List[Style],
                      concept: Concept): Validation[List[String], (List[Node], List[Edge])] = {
    checkForErrors(shapeParseTrees, styles, concept) match {
      case Nil =>
        val (nodes, edges) = doTransformShapes(shapeParseTrees, styles, concept)
        Success((nodes, edges))
      case errors: List[String] =>
        Failure(errors)
    }
  }

  // apply a number of checks and return error messages
  private def checkForErrors(shapeParseTrees: List[ShapeParseTree],
                             styles: List[Style],
                             concept: Concept): List[String] = {
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
  private def checkForUndefinedStyles(shapeParseTrees: List[ShapeParseTree],
                                      styles: List[Style]): Option[String] = {
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
    val nodeErrors = checkNodesForUndefinedConceptElements(nodes, concept)

    val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }
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

    def checkConceptIdentifier(geoModel: GeoModelParseTree with HasIdentifier, contexts: Map[String, MClass]): Unit = {
      val prefixes = contexts.keys.toList
      val (prefix, identifier) = geoModel.identifier.split
      if (!prefixes.contains(prefix)) {
        errors += s"Illegal prefix '$prefix' specified!"
      } else {
        val context = contexts(prefix)
        val attributeExists = context.attributes.map(_.name).contains(identifier)
        if (!attributeExists) {
          errors += s"Textfield identifier '$identifier' not found!"
        }
      }
      geoModel.children.foreach(child => check(child, contexts))
    }

    def checkConceptReference(repeatingBox: RepeatingBoxParseTree, contexts: Map[String, MClass]): Unit = {
      val prefixes = contexts.keys.toList
      val (prefix, referenceName) = repeatingBox.foreach.each.split
      if (!prefixes.contains(prefix)) {
        errors += s"RepeatingBox reference name '$referenceName' not found!"
      } else {
        val context = contexts(prefix)
        val referenceExists = context.outputReferenceNames.contains(referenceName)
        if (!referenceExists) {
          errors += s"Concept class '${context.name}' has no reference named '$referenceName'!"
        } else {
          val reference = concept.references.find(_.name == referenceName)
            .getOrElse(throw new Exception("Concept model is invalid!"))
          val newContext = concept.classes.find(_.name == reference.targetClassName)
            .getOrElse(throw new Exception("Concept model is invalid!"))
          val newPrefix = repeatingBox.foreach.as + "."
          if (prefixes.contains(newPrefix)) {
            errors += s"Prefix '$newPrefix' already defined in outer scope!"
          } else {
            val updatedContexts = contexts + (newPrefix -> newContext)
            repeatingBox.children.foreach(child => check(child, updatedContexts))
          }
        }
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
          val contexts = Map[String, MClass](/*prefix=*/ "" -> context)
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
    Nil
  }

  def doTransformNodes(shapeParseTrees: List[ShapeParseTree], edges: List[Edge], styles: List[Style], concept: Concept): List[Node] = {
    Nil
  }

}
