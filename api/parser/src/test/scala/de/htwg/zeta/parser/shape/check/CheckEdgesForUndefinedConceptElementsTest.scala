package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import org.scalatest.Inside
import org.scalatest.Matchers
import org.scalatest.FreeSpec

class CheckEdgesForUndefinedConceptElementsTest extends FreeSpec with Matchers with Inside {

  "the check should" - {

    "succeed" - {

      "for a valid concept class and a valid edge reference" in {
        val myClass1 = "MyClass1"
        val connection = "inheritance"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass1.$connection", target = myClass1)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass1)
          ),
          references = List(
            createConceptReference(name = connection, from = myClass1, to = myClass1)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe Nil
      }
    }

    "fail" - {

      "for a non existing target class" in {
        val myClass1 = "MyClass1"
        val noSuchConceptClass = "noSuchConceptClass"
        val connection = "inheritance"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass1.$connection", target = noSuchConceptClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass1)
          ),
          references = List(
            createConceptReference(name = connection, from = myClass1, to = myClass1)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List(
          "Target 'noSuchConceptClass' for edge 'myEdge' is not a concept class!"
        )
      }

      "for a non existing class reference" in {
        val myClass1 = "MyClass1"
        val noSuchConceptClass = "noSuchConceptClass"
        val connection = "inheritance"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$noSuchConceptClass.$connection", target = myClass1)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass1)
          ),
          references = List(
            createConceptReference(name = connection, from = myClass1, to = myClass1)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List(
          "Concept class 'noSuchConceptClass' for edge 'myEdge' does not exist!"
        )
      }

      "for a non existing concept reference" in {
        val myClass1 = "MyClass1"
        val noSuchConnection = "noSuchConnection"
        val connection = "inheritance"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass1.$noSuchConnection", target = myClass1)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass1)
          ),
          references = Nil
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List(
          "Concept connection 'noSuchConnection' (in class 'MyClass1') for edge 'myEdge' does not exist!"
        )
      }

    }
  }

  private def createConcept(classes: List[MClass], references: List[MReference]): Concept = Concept(
    classes,
    references,
    enums = Nil,
    attributes = Nil,
    methods = Nil,
    uiState = ""
  )

  private def createConceptReference(name: String, from: String, to: String): MReference = MReference(
    name,
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    sourceClassName = from,
    targetClassName = to,
    attributes = Nil,
    methods = Nil
  )

  private def createConceptClass(name: String, superTypeNames: List[String] = Nil): MClass = MClass(
    name,
    description = "",
    abstractness = false,
    superTypeNames,
    inputReferenceNames = Nil,
    outputReferenceNames = Nil,
    attributes = Nil,
    methods = Nil
  )

  private def createEdgeParseTree(connection: String, target: String): EdgeParseTree = EdgeParseTree(
    identifier = "myEdge",
    conceptConnection = connection,
    conceptTarget = Target(target),
    placings = Nil
  )

}
