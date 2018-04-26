package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.parser.check.ErrorCheck
import de.htwg.zeta.parser.check.ErrorCheck.ErrorMessage
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree

//noinspection ScalaStyle
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
    def checkConnReferenceParts(splitConnSeq: Seq[String]) = splitConnSeq.nonEmpty && splitConnSeq.length % 2 == 0

    def checkConceptReference(referenceChain: List[String]): List[ErrorMessage] = {
      if (referenceChain.size < 3) return Nil
      val conceptClass = referenceChain.head
      val conceptConnection = referenceChain(1)
      val conceptTarget = referenceChain(2)

      lazy val maybeConceptClassDoesNotExist =
        concept.classes.find(_.name == conceptClass) match {
          case Some(_) => None
          case None => Some(s"Concept class '$conceptClass' for edge '${edge.identifier}' does not exist!")
        }

      lazy val maybeConceptConnectionDoesNotExist =
        concept.references.find(_.name == conceptConnection) match {
          case Some(_) => None
          case None => Some(s"Concept connection '$conceptConnection' (in class '$conceptClass') for edge '${edge.identifier}' does not exist!")
        }

      lazy val maybeTargetClassDoesNotExist =
        concept.classes.find(_.name == edge.conceptTarget.target) match {
          case Some(_) => None
          case None => Some(s"Target '${edge.conceptTarget.target}' for edge '${edge.identifier}' is not a concept class!")
        }

      lazy val maybeReferenceIsNotDefined = concept.references
        .find(e => e.sourceClassName == conceptClass && e.name == conceptConnection && e.targetClassName == conceptTarget) match {
        case Some(_) => None
        case None => Some(s"Reference '$conceptClass.$conceptConnection.$conceptTarget' in edge '${edge.identifier}' is not defined!")
      }

      val _ :: _ :: followingChain = referenceChain
      List(
        maybeConceptClassDoesNotExist,
        maybeConceptConnectionDoesNotExist,
        maybeTargetClassDoesNotExist,
        maybeReferenceIsNotDefined
      ).collectFirst({
        case Some(error) => error
      }).toList ::: checkConceptReference(followingChain)
    }

    val conn = edge.conceptConnection
    val splitReferenceChain = conn.split("\\.").toList

    if (!checkConnReferenceParts(splitReferenceChain)) {
      List(s"Edge concept reference '$conn' is not a valid identifier <class>.<connection> or <class>.<connection>.<class>.<connection>!")
    } else {
      // append target as last element of reference chain
      checkConceptReference(splitReferenceChain ::: List(edge.conceptTarget.target))
    }
  }

}
