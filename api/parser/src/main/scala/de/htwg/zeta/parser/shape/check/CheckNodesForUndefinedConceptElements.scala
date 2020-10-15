package de.htwg.zeta.parser.shape.check

import scala.collection.mutable.ListBuffer

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.UnitType
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HasIdentifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RepeatingBoxParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckNodesForUndefinedConceptElements(shapeParseTrees: List[ShapeParseTree], concept: Concept) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val nodes = shapeParseTrees.collect { case n: NodeParseTree => n }
    checkNodesForUndefinedConceptElements(nodes, concept)
  }

  //noinspection ScalaStyle
  // this thing is a mess!
  def checkNodesForUndefinedConceptElements(nodeParseTrees: List[NodeParseTree], concept: Concept): List[Id] = {
    val errors = new ListBuffer[String]()

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


}
