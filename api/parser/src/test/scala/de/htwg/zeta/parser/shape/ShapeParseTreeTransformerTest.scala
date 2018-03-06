package de.htwg.zeta.parser.shape

import scalaz.Failure
import scalaz.Success

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.parser.shape.ShapeParseTreeTransformer.NodesAndEdges
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Target
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes._
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.NodeStyle
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class ShapeParseTreeTransformerTest extends FreeSpec with Matchers with Inside {

  private val myStyle = de.htwg.zeta.server.generator.model.style.Style(
    name = "MyStyle"
  )

  private val myTextfield = Helper.createTextfield(
    Some(Style(myStyle.name)),
    identifier = "myAttribute"
  )

  private val myNode = Helper.createNode(
    "MyNode",
    "MyConceptClass",
    edges = List("MyEdge"),
    Some(NodeStyle(myStyle.name)),
    geoModels = List(myTextfield)
  )

  private val myEdge = EdgeParseTree(
    "MyEdge",
    conceptConnection = "",
    conceptTarget = Target(""),
    placings = Nil
  )

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
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Success(NodesAndEdges(nodes = Nil, edges = Nil))
      }

      "a valid shape definition" in {
        val shapeParseTrees = List(myNode, myEdge)
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
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
        */
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
        result.isSuccess shouldBe true
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

      "when undefined concept classes are referenced" in {
        val shapeParseTrees = List(myNode, myEdge)
        val styles = List(myStyle)
        val concept = Concept.empty
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("Concept class 'MyConceptClass' for node 'MyNode' not found!")
        )
      }

      "when undefined concept class attribute is referenced" in {
        val textfieldWithInvalidIdentifier = myTextfield.copy(identifier = Identifier("noSuchAttribute"))
        val shapeParseTrees = List(myEdge, myNode.copy(geoModels = List(textfieldWithInvalidIdentifier)))
        val styles = List(myStyle)
        val concept = myConcept
        val result = ShapeParseTreeTransformer.transformShapes(shapeParseTrees, styles, concept)
        result shouldBe Failure(
          List("Textfield identifier 'noSuchAttribute' not found or it has return type 'Unit'!")
        )
      }

    }

  }

}
