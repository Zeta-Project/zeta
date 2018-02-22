package de.htwg.zeta.parser.shape

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.{AttributeType, MAttribute, MClass}
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.{LineParseTree, TextfieldParseTree}
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.{NodeStyle, SizeMax, SizeMin}
import de.htwg.zeta.parser.shape.parsetree.{EdgeParseTree, NodeParseTree}
import org.scalatest.{FreeSpec, Inside, Matchers}

import scalaz.{Failure, Success}

//noinspection ScalaStyle
class ShapeParseTreeTransformerTest extends FreeSpec with Matchers with Inside {

  private val textfield = TextfieldParseTree(
    Some(Style("MyStyle")),
    Identifier("myAttribute"),
    Position(0, 0),
    Size(100, 100),
    multiline = None,
    align = None,
    editable = None,
    children = Nil
  )

  private val textfieldWithInvalidIdentifier = textfield.copy(identifier = Identifier("noSuchAttribute"))

  private val myNode = NodeParseTree(
    "MyNode",
    "MyConceptClass",
    edges = List("MyEdge"),
    SizeMin(10, 10),
    SizeMax(100, 100),
    Some(NodeStyle("MyStyle")),
    resizing = None,
    anchors = Nil,
    geoModels = List(textfield)
  )

  private val myEdge = EdgeParseTree(
    "MyEdge",
    conceptConnection = "",
    conceptTarget = Target(""),
    placings = Nil
  )

  private val myConcept = Concept(
    classes = List(
      MClass(
        name = "MyConceptClass",
        description = "",
        abstractness = false,
        superTypeNames = Nil,
        inputReferenceNames = Nil,
        outputReferenceNames = Nil,
        attributes = List(
          MAttribute(
            name = "myAttribute",
            globalUnique = false,
            localUnique = false,
            StringType,
            StringValue(""),
            constant = false,
            singleAssignment = false,
            expression = "?",
            ordered = false,
            transient = true
          )
        ),
        methods = Nil,
      )
    ),
    references = Nil,
    enums = Nil,
    attributes = Nil,
    methods = Nil,
    uiState = ""
  )

  private val myStyle = de.htwg.zeta.server.generator.model.style.Style(
    name = "MyStyle"
  )

  "A ShapeParseTreeTransformer should" - {

    "succeed" - {

      "to transform an empty list of shape trees" in {
        val shapeParseTrees = Nil
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Success((Nil, Nil))
      }

      "to transform a valid shape definition" in {
        val shapeParseTrees = List(myNode, myEdge)
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result.isSuccess shouldBe true
        // TODO: check success tuple
      }
    }

    "fail" - {

      "when undefined edges are referenced" in {
        val shapeParseTrees = List(myNode)
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("The following edges are referenced but not defined: MyEdge")
        )
      }

      "when undefined styles are referenced" in {
        val shapeParseTrees = List(myNode, myEdge)
        val styles = Nil
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("The following styles are referenced but not defined: MyStyle")
        )
      }

      "when undefined concept classes is referenced" in {
        val shapeParseTrees = List(myNode, myEdge)
        val styles = List(myStyle)
        val concept = Concept.empty
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("Node 'MyNode' references undefined concept class 'MyConceptClass'")
        )
      }

      "when undefined concept class attribute is referenced" in {
        val shapeParseTrees = List(myEdge, myNode.copy(geoModels = List(textfieldWithInvalidIdentifier)))
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("The following attributes of class 'MyConceptClass' are referenced but not defined: noSuchAttribute")
        )
      }

    }

  }

}
