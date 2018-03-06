package de.htwg.zeta.parser.diagram

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.model.diagram.Diagram
import de.htwg.zeta.common.model.diagram.Palette
import de.htwg.zeta.common.model.shape.Node
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.check.FindInvalidReferences

object DiagramParseTreeTransformer {

  def transform(diagrams: List[DiagramParseTree], nodes: List[Node]): Validation[List[String], List[Diagram]] = {
    val referencedNodes = ReferenceCollector[Node](nodes, _.name)
    checkForErrors(diagrams, referencedNodes) match {
      case Nil => Success(diagrams.map(transform(_, referencedNodes)))
      case errors: List[String] => Failure(errors)
    }
  }

  private def checkForErrors(diagrams: List[DiagramParseTree], nodes: ReferenceCollector[Node]): List[String] = {

    def findDuplicateDiagrams(): List[String] = {
      val findDuplicates = FindDuplicates[DiagramParseTree](_.name)
      findDuplicates(diagrams)
    }

    def findDuplicatePalettes(): List[String] = {
      val findDuplicates = FindDuplicates[PaletteParseTree](_.name)
      diagrams.map(_.palettes).flatMap(findDuplicates(_))
    }

    def findDuplicateNodes(): List[String] = {
      val findDuplicates = FindDuplicates[NodeParseTree](_.name)
      diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findDuplicates(_))
    }

    def findInvalidNodeIds(): List[String] = {
      val findInvalidIds = FindInvalidReferences[NodeParseTree](_.name, nodes.identifiers())
      diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findInvalidIds(_))
    }

    val duplicateDiagrams = findDuplicateDiagrams()
    val duplicatePalettes = findDuplicatePalettes()
    val duplicateNodes = findDuplicateNodes()
    val invalidNodeIds = findInvalidNodeIds()

    val maybeErrors = List(
      createErrorIfNotEmpty("The following diagrams are defined multiple times: ", duplicateDiagrams),
      createErrorIfNotEmpty("The following palettes are defined multiple times: ", duplicatePalettes),
      createErrorIfNotEmpty("The following nodes are defined multiple times: ", duplicateNodes),
      createErrorIfNotEmpty("The following nodes are not defined in shape: ", invalidNodeIds)
    )
    maybeErrors.collect {
      case Some(error) => error
    }
  }

  private def createErrorIfNotEmpty(errorMessage: String, errorIds: List[Id]): Option[String] = errorIds match {
    case Nil => None
    case _ => Some(errorMessage + errorIds.mkString(","))
  }

  private def transform(diagramTree: DiagramParseTree, nodes: ReferenceCollector[Node]): Diagram = {
    Diagram(
      name = diagramTree.name,
      palettes = diagramTree.palettes.map(p => Palette(
        name = p.name,
        nodes = p.nodes.map(t => nodes.!(t.name))
      ))
    )
  }
}
