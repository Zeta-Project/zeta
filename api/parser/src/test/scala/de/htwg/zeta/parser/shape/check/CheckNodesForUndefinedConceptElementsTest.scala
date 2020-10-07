package de.htwg.zeta.parser.shape.check

import de.htwg.zeta.parser.ConceptCreatorHelper
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Editable
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.For
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RepeatingBoxParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TextfieldParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMax
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMin
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree
import org.scalatest.Inside
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class CheckNodesForUndefinedConceptElementsTest extends AnyFreeSpec with Matchers with Inside {

  "the check should" - {
    "succeed" - {
      "for a valid concept class and valid attribute" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTree("node0", "AbstractKlasse", "text11")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 0
      }
      "for a valid concept class and method with return type string" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTree("node0", "Klasse", "methodString")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 0
      }
      "for a repeating textfield with valid reference identifier in repeating" ignore {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTreeRepeating("node0", "Klasse", "BaseClassRealization", "test1")
        )
        // TODO this test fails with "Textfield identifier 'test113' not found or it has return type 'Unit'!"
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 0
      }
    }
    "fail" - {
      "for an invalid concept class" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTree("node0", "InvalidConceptClass", "test")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 1
        checkResult should contain("Concept class 'InvalidConceptClass' for node 'node0' not found!")
      }
      "for an invalid attribute identifier in geo model" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTree("node0", "AbstractKlasse", "test")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 1
        checkResult should contain("Textfield identifier 'test' not found or it has return type 'Unit'!")
      }
      "for a method identifier in geo model with return type unit" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTree("node0", "Klasse", "methodUnit")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 1
        checkResult should contain("Textfield identifier 'methodUnit' not found or it has return type 'Unit'!")
      }
      "for a repeating textfield with invalid reference identifier in repeating" in {
        val nodeParseTrees: List[ShapeParseTree] = List(
          cNodeParseTreeRepeating("node0", "Klasse", "invalid", "test1")
        )
        val checkResult = CheckNodesForUndefinedConceptElements(nodeParseTrees, ConceptCreatorHelper.exampleConcept).check()
        checkResult.size shouldBe 1
        checkResult should contain("Concept class 'Klasse' has no reference named 'invalid'!")
      }
    }
  }

  private def cNodeParseTree(
      identifier: String,
      conceptClass: String,
      textfieldIdentifier: String
  ): NodeParseTree = NodeParseTree(
    identifier = identifier,
    conceptClass = conceptClass,
    edges = List(),
    sizeMin = SizeMin(0, 0),
    sizeMax = SizeMax(0, 0),
    style = None,
    resizing = None,
    anchors = List(),
    geoModels = List(
      TextfieldParseTree(
        style = None,
        identifier = Identifier(textfieldIdentifier),
        textBody = None,
        position = Position(0,0),
        size = Size(0,0),
        multiline = None,
        align = None,
        editable = None,
        children = List()
      )
    )
  )

  private def cNodeParseTreeRepeating(
      identifier: String,
      conceptClass: String,
      forIdentifier: String,
      textfieldIdentifier: String
  ): NodeParseTree = NodeParseTree(
    identifier = identifier,
    conceptClass = conceptClass,
    edges = List(),
    sizeMin = SizeMin(0, 0),
    sizeMax = SizeMax(0, 0),
    style = None,
    resizing = None,
    anchors = List(),
    geoModels = List(
      RepeatingBoxParseTree(
        editable = Editable(true),
        foreach = For(each = Identifier(forIdentifier), as = "a"),
        children = List(
          TextfieldParseTree(
            style = None,
            identifier = Identifier(textfieldIdentifier),
            textBody = None,
            position = Position(0,0),
            size = Size(0,0),
            multiline = None,
            align = None,
            editable = None,
            children = List()
          )
        )
      )
    )
  )

}
