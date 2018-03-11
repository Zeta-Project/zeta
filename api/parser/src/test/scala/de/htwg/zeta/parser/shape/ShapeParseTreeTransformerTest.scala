package de.htwg.zeta.parser.shape

import scalaz.Failure
import scalaz.Success

import de.htwg.zeta.common.model.style.Background
import de.htwg.zeta.common.model.style.Color
import de.htwg.zeta.common.model.style.Dashed
import de.htwg.zeta.common.model.style.Font
import de.htwg.zeta.common.model.style.Line
import de.htwg.zeta.common.model.style.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.parser.shape.ShapeParseTreeTransformer.NodesAndEdges
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.AbsoluteAnchor
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.NodeStyle
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMax
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMin
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class ShapeParseTreeTransformerTest extends FreeSpec with Matchers with Inside {

  private object StyleFactory {
    def apply(name: String): Style = Style(
      name,
      "TestDescription",
      Background(Color(0, 0, 0)),
      Font("TestFont", bold = false, Color(0, 0, 0), italic = false, 0),
      Line(Color(0, 0, 0), Dashed(), 1),
      1.0
    )
  }

  private val myConcept = Concept(
    classes = List(
      Helper.createConceptClass(
        "MyConceptClass",
        attributes = List(
          Helper.createConceptAttribute("myAttribute")
        )
      )
    ),
    references = Nil,
    enums = Nil,
    attributes = Nil,
    methods = Nil,
    uiState = ""
  )

  "A ShapeParseTreeTransformer should" - {

    "succeed in transforming" - {

      "an empty list of shape trees" in {
        val shapeParseTrees = Nil
        val styles = List(StyleFactory("myStyle"))
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transform(shapeParseTrees, styles, concept)
        result shouldBe Success(NodesAndEdges(nodes = Nil, edges = Nil))
      }

      "a valid shape definition" in {
        val shapeParseTrees = List(
          NodeParseTree(
            identifier = "node1",
            conceptClass = "MyConceptClass",
            edges = List("edge1"),
            sizeMin = SizeMin(2, 1),
            sizeMax = SizeMax(2, 1),
            style = Some(NodeStyle("myStyle")),
            resizing = Some(NodeAttributes.Resizing(horizontal = true, vertical = false, proportional = true)),
            anchors = List(AbsoluteAnchor(1, 2)),
            geoModels = List()
          ), EdgeParseTree(
            identifier = "edge1",
            conceptConnection = "myAttribute",
            conceptTarget = Target("myAttribute"),
            placings = List()
          ))
        val styles = List(StyleFactory("myStyle"))
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transform(shapeParseTrees, styles, concept)
        result.isSuccess shouldBe true
        val resultNodes = result.getOrElse(NodesAndEdges(Nil, Nil)).nodes
        val resultEdges = result.getOrElse(NodesAndEdges(Nil, Nil)).edges
        resultNodes.size shouldBe 1
        resultEdges.size shouldBe 1
        // TODO: check success tuple
      }

      "nested repeating boxes" in {
        /*
        ShoppingCart(totalPrice) has articles
        Article(nettoPrice, calcBruttoPrice) has producers
        Producer(name)
        *//*
        val shoppingCartNode = Helper.createNode(
          identifier = "ShoppingCartNode",
          conceptClass = "ShoppingCart",
          geoModels = List(
            Helper.createTextfield(identifier = "totalPrice"),
            Helper.createRepeatingBox(
              For(each = Identifier("hasArticles"), as = "article"),
              children = List(
                Helper.createTextfield(identifier = "article.nettoPrice"),
                Helper.createRepeatingBox(
                  For(each = Identifier("article.hasProducers"), as = "producer"),
                  children = List(
                    Helper.createTextfield(identifier = "totalPrice"),
                    Helper.createTextfield(identifier = "article.calcBruttoPrice"),
                    Helper.createTextfield(identifier = "producer.name")
                  )
                )
              )
            )
          )
        )
        val concept = Concept(
          classes = List(
            Helper.createConceptClass(
              "ShoppingCart",
              outputReferences = List("hasArticles"),
              attributes = List(
                Helper.createConceptAttribute("totalPrice")
              )
            ),
            Helper.createConceptClass(
              "Article",
              inputReferences = List("hasArticles"),
              outputReferences = List("hasProducers"),
              attributes = List(
                Helper.createConceptAttribute("nettoPrice")
              ),
              methods = List(
                Helper.createConceptMethod("calcBruttoPrice")
              )
            ),
            Helper.createConceptClass(
              "Producer",
              inputReferences = List("hasProducers"),
              attributes = List(
                Helper.createConceptAttribute("name")
              )
            )
          ),
          references = List(
            MReference.empty("hasArticles", "ShoppingCart", "Article"),
            MReference.empty("hasProducers", "Article", "Producer")
          ),
          enums = Nil,
          attributes = Nil,
          methods = Nil,
          uiState = ""
        )
        val result = ShapeParseTreeTransformer.transformShapes(List(shoppingCartNode), Nil, concept)
        result.isSuccess shouldBe true*/
      }
    }

    "fail" - {

      "when undefined edges are referenced" in {
        val shapeParseTrees = List(
          NodeParseTree(
            identifier = "node1",
            conceptClass = "MyConceptClass",
            edges = List("edge1"),
            sizeMin = SizeMin(2, 1),
            sizeMax = SizeMax(2, 1),
            style = Some(NodeStyle("myStyle")),
            resizing = Some(NodeAttributes.Resizing(horizontal = true, vertical = false, proportional = true)),
            anchors = List(AbsoluteAnchor(1, 2)),
            geoModels = List()
          ))
        val styles = List(StyleFactory("myStyle"))
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transform(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("The following edges are referenced but not defined: edge1")
        )
      }

      "when undefined styles are referenced" in {
        val shapeParseTrees = List(
          NodeParseTree(
            identifier = "node1",
            conceptClass = "MyConceptClass",
            edges = List("edge1"),
            sizeMin = SizeMin(2, 1),
            sizeMax = SizeMax(2, 1),
            style = Some(NodeStyle("myStyle")),
            resizing = Some(NodeAttributes.Resizing(horizontal = true, vertical = false, proportional = true)),
            anchors = List(AbsoluteAnchor(1, 2)),
            geoModels = List()
          ), EdgeParseTree(
            identifier = "edge1",
            conceptConnection = "myAttribute",
            conceptTarget = Target("myAttribute"),
            placings = List()
          ))
        val styles = Nil
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transform(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("The following styles are referenced but not defined: myStyle")
        )
      }

      "when undefined concept classes are referenced" in {
        val shapeParseTrees = List(
          NodeParseTree(
            identifier = "node1",
            conceptClass = "MyConceptClass",
            edges = List("edge1"),
            sizeMin = SizeMin(2, 1),
            sizeMax = SizeMax(2, 1),
            style = Some(NodeStyle("myStyle")),
            resizing = Some(NodeAttributes.Resizing(horizontal = true, vertical = false, proportional = true)),
            anchors = List(AbsoluteAnchor(1, 2)),
            geoModels = List()
          ), EdgeParseTree(
            identifier = "edge1",
            conceptConnection = "myAttribute",
            conceptTarget = Target("myAttribute"),
            placings = List()
          ))
        val styles = List(StyleFactory("myStyle"))
        val concept = Concept.empty
        val result = ShapeParseTreeTransformer.transform(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("Concept class 'MyConceptClass' for node 'node1' not found!")
        )
      }

      "when undefined concept class attribute is referenced" in {
        /*val textfieldWithInvalidIdentifier = myTextfield.copy(identifier = Identifier("noSuchAttribute"))
        val shapeParseTrees = List(myEdge, myNode.copy(geoModels = List(textfieldWithInvalidIdentifier)))
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("Textfield identifier 'noSuchAttribute' not found or it has return type 'Unit'!")
        )*/
      }

    }

  }

}
