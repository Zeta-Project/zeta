package de.htwg.zeta.parser.diagram

import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.server.generator.model.diagram.Diagram
import scalaz.Failure
import scalaz.Success
import scalaz.Validation

object DiagramParseTreeTransformer {

  def transform(diagrams: Seq[DiagramParseTree]): Validation[Seq[String], Seq[Diagram]] = {
    checkForErrors(diagrams) match {
      case Seq() => Success(diagrams.map(transform))
      case errors: Seq[String] => Failure(errors)
    }
  }

  private def checkForErrors(diagrams: Seq[DiagramParseTree]): Seq[String] = {

    def findDuplicateDiagrams(): Seq[String] = {
      val findDuplicates = new FindDuplicates[DiagramParseTree](_.name)
      findDuplicates(diagrams)
    }

    def findDuplicatePalettes(): Seq[String] = {
      val findDuplicates = new FindDuplicates[PaletteParseTree](_.name)
      diagrams.map(_.palettes).flatMap(findDuplicates(_))
    }

    def findDuplicateNodes(): Seq[String] = {
      val findDuplicates = new FindDuplicates[NodeParseTree](_.name)
      diagrams.flatMap(_.palettes).map(_.nodes).flatMap(findDuplicates(_))
    }

    val duplicateDiagrams = findDuplicateDiagrams()
    val duplicatePalettes = findDuplicatePalettes()
    val duplicateNodes = findDuplicateNodes()

    val maybeErrors = List(
      createErrorIfNotEmpty("The following diagrams are defined multiple times: ", duplicateDiagrams),
      createErrorIfNotEmpty("The following palettes are defined multiple times: ", duplicatePalettes),
      createErrorIfNotEmpty("The following nodes are defined multiple times: ", duplicateNodes)
    )
    maybeErrors.collect {
      case Some(error) => error
    }
  }

  private def createErrorIfNotEmpty(errorMessage: String, errorIds: Seq[Id]): Option[String] = errorIds match {
    case Seq() => None
    case _ => Some(errorMessage + errorIds.mkString(","))
  }

  private def transform(diagramTree: DiagramParseTree): Diagram = {
    //noinspection ScalaStyle
    Diagram(diagramTree.name,
      Map(),
      List(),
      List(),
      None,
      null,
      null
    )
  }
}
