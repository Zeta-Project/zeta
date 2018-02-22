package de.htwg.zeta.parser.shape

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.model.elements.{Edge, Node}
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.{GeoModelParseTree, RepeatingBoxParseTree, TextfieldParseTree}
import de.htwg.zeta.parser.shape.parsetree.{EdgeParseTree, NodeParseTree, ShapeParseTree}
import de.htwg.zeta.server.generator.model.style.Style

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
  private def checkForUndefinedConceptElements(shapeParseTrees: List[ShapeParseTree],
                                               concept: Concept): Option[String] = {

    val nodes = shapeParseTrees.collect { case n: NodeParseTree => n }
    val nodeErrors = checkNodesForUndefinedConceptElements(nodes, concept)

    val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }
    val edgeErrors = checkEdgesForUndefinedConceptElements(edges, concept)

    nodeErrors ++ edgeErrors match {
      case Nil => None
      case errors: List[String] => Some(errors.mkString(",\n"))
    }
  }

  // check if there are nodes which reference undefined concept elements
  private def checkNodesForUndefinedConceptElements(nodeParseTrees: List[NodeParseTree],
                                                    concept: Concept): List[String] = {

    // collect all textfields which are not within a repeating box
    def collectTextfields(geoModel: GeoModelParseTree): List[TextfieldParseTree] = {
      geoModel match {
        case tf: TextfieldParseTree => tf.children.flatMap(collectTextfields) :+ tf
        case _: RepeatingBoxParseTree => Nil
        case other: GeoModelParseTree => other.children.flatMap(collectTextfields)
      }
    }

    val errors = nodeParseTrees.flatMap { node =>
      val correspondingConceptClass = concept.classes.find(_.name == node.conceptClass)
      correspondingConceptClass match {
        case None =>
          Some(s"Node '${node.identifier}' references undefined concept class '${node.conceptClass}'")
        case Some(conceptClass) =>
          val textfields = node.geoModels.flatMap(collectTextfields)
          val referencedAttributes = textfields.map(_.identifier.name).toSet
          val definedAttributes = conceptClass.attributes.map(_.name).toSet
          val undefinedAttributes = referencedAttributes.diff(definedAttributes).toList

          undefinedAttributes match {
            case Nil => None
            case _ => Some(s"The following attributes of class '${node.conceptClass}' are referenced but not defined: ${undefinedAttributes.mkString(",")}")
          }
      }
    }
    errors
  }

  // check if there are edges which reference undefined concept elements
  private def checkEdgesForUndefinedConceptElements(nodeParseTrees: List[EdgeParseTree],
                                                    concept: Concept): List[String] = {
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
