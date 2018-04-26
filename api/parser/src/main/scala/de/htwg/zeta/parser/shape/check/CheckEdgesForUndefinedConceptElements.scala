package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

case class CheckEdgesForUndefinedConceptElements(shapeParseTrees: List[ShapeParseTree], concept: Concept) extends ErrorCheck[ErrorMessage] {

  override def check(): List[ErrorMessage] = {
    val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }
    checkEdgesForUndefinedConceptElements(edges, concept)
  }

  // check if there are edges which reference undefined concept elements
  private def checkEdgesForUndefinedConceptElements(edges: List[EdgeParseTree], concept: Concept): List[ErrorMessage] = {
    edges.flatMap(edge => checkEdgeForUndefinedConceptElements(edge, concept))
  }

  private def checkEdgeForUndefinedConceptElements(edge: EdgeParseTree, concept: Concept): List[ErrorMessage] = {
    val conn = edge.conceptConnection
    val splitConn = conn.split("\\.").toList

    def checkConnReferenceParts(splitConnSeq: Seq[String]) = splitConnSeq.nonEmpty && splitConnSeq.length % 2 == 0

    if(!checkConnReferenceParts(splitConn)) {
      List(s"Edge concept reference '$conn' is not a valid identifier <class>.<connection> or <class>.<connection>.<class>.<connection>!")
    } else {
      val List(conceptClass, conceptConnection) = splitConn

      lazy val maybeConceptClassDoesNotExist: Option[ErrorMessage] = concept.classes.find(_.name == conceptClass) match {
        case Some(_) => None // the concept class referenced by the edge exists
        case None => Some(s"Concept class '$conceptClass' for edge '${edge.identifier}' does not exist!")
      }

      lazy val maybeConceptConnectionDoesNotExist: Option[ErrorMessage] = concept.references.find(_.name == conceptConnection) match {
        case Some(_) => None // the concept connection referenced by the edge exists
        case None => Some(s"Concept connection '$conceptConnection' (in class '$conceptClass') for edge '${edge.identifier}' does not exist!")
      }

      val maybeTargetClassDoesNotExist: Option[ErrorMessage] = concept.classes.find(_.name == edge.conceptTarget.target) match {
        case Some(_) => None // the concept target referenced by the edge exists
        case None => Some(s"Target '${edge.conceptTarget.target}' for edge '${edge.identifier}' is not a concept class!")
      }

      (maybeTargetClassDoesNotExist ++: List(maybeConceptClassDoesNotExist, maybeConceptConnectionDoesNotExist).collectFirst {
        case Some(error) => error
      }).toList
    }
  }

}
