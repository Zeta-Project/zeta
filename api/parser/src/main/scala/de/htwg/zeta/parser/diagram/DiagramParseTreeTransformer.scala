package de.htwg.zeta.parser.diagram

import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.model.diagram.Diagram
import de.htwg.zeta.common.model.diagram.Palette
import de.htwg.zeta.common.model.shape.Node
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.ErrorChecker
import de.htwg.zeta.parser.diagram.check.CheckDuplicateDiagrams
import de.htwg.zeta.parser.diagram.check.CheckDuplicateNodes
import de.htwg.zeta.parser.diagram.check.CheckDuplicatePalettes
import de.htwg.zeta.parser.diagram.check.CheckUndefinedNodes

object DiagramParseTreeTransformer {

  def transform(diagrams: List[DiagramParseTree], nodes: List[Node]): Validation[List[String], List[Diagram]] = {
    val referencedNodes = ReferenceCollector[Node](nodes, _.name)
    checkForErrors(diagrams, referencedNodes) match {
      case Nil => Success(diagrams.map(transform(_, referencedNodes)))
      case errors: List[String] => Failure(errors)
    }
  }

  private def checkForErrors(diagrams: List[DiagramParseTree], nodes: ReferenceCollector[Node]): List[String] =
    ErrorChecker()
      .add(CheckDuplicateDiagrams(diagrams), ids => s"The following diagrams are defined multiple times: $ids")
      .add(CheckDuplicatePalettes(diagrams), ids => s"The following palettes are defined multiple times: $ids")
      .add(CheckDuplicateNodes(diagrams), ids => s"The following nodes are defined multiple times: $ids")
      .add(CheckUndefinedNodes(diagrams, nodes), ids => s"The following nodes are not defined in shape: $ids")
      .run()

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
