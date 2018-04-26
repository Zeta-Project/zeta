package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

class CheckEdgesForUndefinedConceptElementsTest extends FreeSpec with Matchers with Inside {

  private val emptyString = ""

  "the check should" - {

    "succeed" - {

      "for a valid concept class and a valid edge reference" in {
        val myValidClass = "MyClass1"
        val validConnection = "inheritance"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myValidClass.$validConnection", target = myValidClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myValidClass)
          ),
          references = List(
            createConceptReference(name = validConnection, from = myValidClass, to = myValidClass)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe Nil
      }
      "for a valid concept class and a valid edge reference over another class node" in {
        val myClass = "MyClass"
        val hasLink = "hasLink"
        val link = "Link"
        val links = "links"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass.$hasLink.$link.$links", target = myClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass),
            createConceptClass(name = link)
          ),
          references = List(
            createConceptReference(name = hasLink, from = myClass, to = link),
            createConceptReference(name = links, from = link, to = myClass)
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
          "No concept class 'noSuchConceptClass' for edge 'myEdge' exists!"
        )
      }

      "for a non existing concept reference" in {
        val myClass1 = "MyClass1"
        val noSuchConnection = "noSuchConnection"

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

      "for a non existing reference in multi linked classes" in {
        val myClass = "MyClass"
        val hasLink = "hasLink"
        val link = "Link"
        val links = "nonexistingReference"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass.$hasLink.$link.$links", target = myClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass),
            createConceptClass(name = link)
          ),
          references = List(
            createConceptReference(name = hasLink, from = myClass, to = link)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List("Concept connection 'nonexistingReference' (in class 'Link') for edge 'myEdge' does not exist!")
      }

      "for an invalid referenced target in middle of multi linked classes" in {
        val myClass = "MyClass"
        val hasLink = "linkWithWrongTarget"
        val link = "Link"
        val links = "links"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass.$hasLink.$link.$links", target = myClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass),
            createConceptClass(name = link)
          ),
          references = List(
            createConceptReference(name = hasLink, from = myClass, to = myClass),
            createConceptReference(name = links, from = link, to = myClass)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List("Reference 'MyClass.linkWithWrongTarget.Link' in edge 'myEdge' is not defined!")
      }

      "for an invalid edge target in multi linked classes" in {
        val myClass = "MyClass"
        val hasLink = "linkWithWrongTarget"
        val link = "Link"
        val links = "links"

        val shapes: List[ShapeParseTree] = List(
          createEdgeParseTree(connection = s"$myClass.$hasLink.$link.$links", target = myClass)
        )
        val concept = createConcept(
          classes = List(
            createConceptClass(name = myClass),
            createConceptClass(name = link)
          ),
          references = List(
            createConceptReference(name = hasLink, from = myClass, to = myClass),
            createConceptReference(name = links, from = link, to = myClass)
          )
        )

        val check = CheckEdgesForUndefinedConceptElements(shapes, concept)
        val errors = check.check()
        errors shouldBe List("Reference 'MyClass.linkWithWrongTarget.Link' in edge 'myEdge' is not defined!")
      }
    }
  }

  private def createConcept(classes: List[MClass], references: List[MReference]): Concept = Concept(
    classes,
    references,
    enums = Nil,
    attributes = Nil,
    methods = Nil,
    uiState = emptyString
  )

  private def createConceptReference(name: String, from: String, to: String): MReference = MReference(
    name,
    description = emptyString,
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    sourceClassName = from,
    targetClassName = to,
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    attributes = Nil,
    methods = Nil
  )

  private def createConceptClass(name: String, superTypeNames: List[String] = Nil): MClass = MClass(
    name,
    description = emptyString,
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
    style = None,
    placings = Nil
  )

}
