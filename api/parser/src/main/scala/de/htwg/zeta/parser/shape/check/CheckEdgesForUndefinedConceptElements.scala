package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckEdgesForUndefinedConceptElements(shapeParseTrees: List[ShapeParseTree], concept: Concept) extends ErrorCheck {

  override def check(): List[Id] = {
    val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }
    checkEdgesForUndefinedConceptElements(edges, concept)
  }


  // check if there are edges which reference undefined concept elements
  def checkEdgesForUndefinedConceptElements(nodeParseTrees: List[EdgeParseTree], concept: Concept): List[String] = {
    Nil // TODO
  }

}
