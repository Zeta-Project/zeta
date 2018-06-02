package de.htwg.zeta.parser

import de.htwg.zeta.common.models.project.gdsl
import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.HorizontalLayout
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Rectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RepeatingBox
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.StaticText
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Solid
import de.htwg.zeta.common.models.project.gdsl.style.Style
import org.scalatest.FreeSpec
import org.scalatest.Matchers

//noinspection ScalaStyle
class IntegrationGraphicalDSLParserTest extends FreeSpec with Matchers {

  private val parser = new GraphicalDSLParser()

  private val style =
    """style Y {
           description = "Style for a connection between an interface and its implementing class"
           line-color = black
           line-style = dotted
           line-width = 1
           background-color = white
           font-size = 20
           font-name = Arial
           font-bold = true
           font-color = black
           font-italic = true
           transparency = 0.9
         }

         style ClassText {
           description = "Style for text in a class"
           line-color = black
           line-style = dash
           line-width = 1
           background-color = white
           font-size = 10
         }

         style X {
           description = "The default style"
           line-color = black
           line-style = solid
           line-width = 1
           background-color = white
           font-size = 20
         }

         style realization {
           description = "Style for realization"
           background-color = white
           line-style = wrongLineStyle
         }

         style aggregation {
           description = "Style for aggregation"
           background-color = white
         }

         style component {
           description = "Style for component"
           background-color = black
         }"""

  private val diagram =
    """diagram klassendiagramm1 {
           palette Class {
             classNode
           }
           palette AbstractClass {
             abClassNode
           }
           palette Interface {
             inClassNode
           }
         }"""

  private val shape =
    """node classNode for Klasse {
           edges {
               inheritance
               BaseClassRealization
               component
               aggregation
           }
           style: X
           sizeMin(width: 200, height: 400)
           sizeMax(width: 200, height: 400)
           resizing(horizontal: false, vertical: false, proportional: true)
           rectangle {
             size(width: 200, height: 50)
             position(x: 0, y: 50)
               textfield {
               position(x: 0, y: 0)
                 identifier: text1
                 size(width: 10, height: 40)
                 editable: true
               }
           }
           rectangle {
             size(width: 200, height: 100)
             position(x: 0, y: 50)
             textfield   {
                 position(x: 0, y: 0)
                 identifier: text2
                 size(width: 10, height: 40)
                 editable: true
             }
           }
           roundedRectangle {
             size(width: 200, height: 100)
             curve(width: 10, height: 10)
             position(x: 0, y: 150)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text3
                     size(width: 10, height: 40)
                     editable: true
                 }
           }
           ellipse {
             size(width: 200, height: 100)
             position(x: 0, y: 150)
           }
           statictext {
             size(width: 200, height: 100)
             position(x: 0, y: 150)
             text: "test"
           }
           repeatingBox {
             editable: false
             for(each: Inheritance, as: i)
             verticalLayout {
               line {
                 point(x: 1, y: 51)
                 point(x: 2, y: 52)
               }
             }
           }
           horizontalLayout {
             polyline {
               point(x: 1, y: 51)
               point(x: 2, y: 52)
             }
           }
         }
         node abClassNode for AbstractKlasse {
           edges {
               realization
           }
           style: X
           resizing(horizontal: false, vertical: false, proportional: true)
           sizeMax(width: 200, height: 400)
           sizeMin(width: 200, height: 400)
           rectangle {
             size(width: 200, height: 50)
             position(x: 10, y: 0)
               textfield {
                 position(x: 0, y: 0)
                 identifier: text11
                 size(width: 10, height: 40)
                 editable: true
               }
           }
           rectangle {
             size(width: 200, height: 100)
             position(x: 10, y: 50)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text21
                     size(width: 10, height: 40)
                     editable: true
                 }
           }
           rectangle {
             size(width: 200, height: 100)
             position(x: 10, y: 150)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text31
                     size(width: 10, height: 40)
                     editable: true
                 }
           }
         }
         node inClassNode for InterfaceKlasse {
           edges {}
           style: X
           resizing(horizontal: false, vertical: false, proportional: true)
           sizeMin(width: 200, height: 400)
           sizeMax(width: 200, height: 400)
           rectangle {
             size(width: 200, height: 50)
             position(x: 10, y: 0)
               textfield {
                 position(x: 0, y: 0)
                 identifier: text113
                 size(width: 10, height: 40)
                 editable: true
                 align(horizontal: left, vertical: top)
               }
           }
           rectangle {
             size(width: 200, height: 100)
             position(x: 10, y: 50)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text213
                     size(width: 10, height: 40)
                     editable: true
                     align(horizontal: middle, vertical: middle)
                 }
           }
           rectangle {
             size(width: 200, height: 100)
             position(x: 10, y: 150)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text313
                     size(width: 10, height: 40)
                     editable: true
                     align(horizontal: right, vertical: bottom)
                 }
           }
         }
         edge inheritance for Klasse.Inheritance {
             target: AbstractKlasse
             placing {
                 style : X
                 offset: 1.0
                 polygon {
                     point(x: -10, y: 10)
                     point(x: 0, y: 0)
                     point(x: -10, y: -10)
                 }
             }
         }
         edge realization for Klasse.Realization {
             target: InterfaceKlasse
             style: Y
             placing {
                 style : Y
                 offset: 1.0
                 polygon {
                     style : realization
                     point(x: -10, y: 10)
                     point(x: 0, y: 0)
                     point(x: -10, y: -10)
                 }
             }
         }
         edge BaseClassRealization for Klasse.BaseClassRealization {
             target: InterfaceKlasse
             style: X
             placing {
                 style : Y
                 offset: 1.0
                 polygon {
                     style : realization
                     point(x: -10, y: 10)
                     point(x: 0, y: 0)
                     point(x: -10, y: -10)
                 }
             }
         }
         edge component for Klasse.Component {
             target: Klasse
             style: X
             placing {
                 offset: 1.0
                 polygon {
                     style : component
                     point(x: 0, y: 0)
                     point(x: 20, y: 0)
                     point(x: 40, y: 0)
                     point(x: 20, y: -10)
                 }
             }
         }
         edge aggregation for Klasse.Aggregation {
             target: Klasse
             style: X
             placing {
                 style : X
                 offset: 1.0
                 polygon {
                     style : aggregation
                     point(x: 0, y: 0)
                     point(x: -20, y: 10)
                     point(x: -40, y: 0)
                     point(x: -20, y: -10)
                 }
             }
         }"""

  private val yStyle = new Style(
    name = "Y",
    description = "Style for a connection between an interface and its implementing class",
    line = new Line(
      color = Color(0, 0, 0, 1),
      style = Dotted(),
      width = 1
    ),
    background = new Background(Color(255, 255, 255, 1)),
    font = new Font("Arial", bold = true, Color(0, 0, 0, 1), italic = true, 20),
    transparency = 0.9
  )

  private val classTextStyle = new Style(
    name = "ClassText",
    description = "Style for text in a class",
    line = new Line(
      color = Color(0, 0, 0, 1),
      style = Dashed(),
      width = 1
    ),
    background = new Background(Color(255, 255, 255, 1)),
    font = new Font("Arial", bold = false, Color(0, 0, 0, 1), italic = false, 10),
    transparency = 1
  )

  private val xStyle = new Style(
    name = "X",
    description = "The default style",
    line = new Line(
      color = Color(0, 0, 0, 1),
      style = Solid(),
      width = 1
    ),
    background = new Background(Color(255, 255, 255, 1)),
    font = new Font("Arial", bold = false, Color(0, 0, 0, 1), italic = false, 20),
    transparency = 1
  )

  private val realizationStyle = new Style(
    name = "realization",
    description = "Style for realization",
    line = Line.defaultLine,
    background = new Background(Color(255, 255, 255, 1)),
    font = Font.defaultFont,
    transparency = 1
  )

  private val componentStyle = new Style(
    name = "component",
    description = "Style for component",
    line = Line.defaultLine,
    background = new Background(Color(0, 0, 0, 1)),
    font = Font.defaultFont,
    transparency = 1
  )

  private val aggregationStyle = new Style(
    name = "aggregation",
    description = "Style for aggregation",
    line = Line.defaultLine,
    background = new Background(Color(255, 255, 255, 1)),
    font = Font.defaultFont,
    transparency = 1
  )

  private val realizationEdge = Edge(
    name = "realization",
    conceptElement = "Klasse.Realization",
    target = "InterfaceKlasse",
    style = yStyle,
    placings = List(
      Placing(
        style = yStyle,
        position = PlacingPosition(1.0),
        geoModel = Polygon(
          points = List(
            Point(-10, 10),
            Point(0, 0),
            Point(-10, -10)
          ),
          childGeoModels = List(),
          style = realizationStyle
        )
      )
    )
  )

  private val inheritanceEdge = Edge(
    name = "inheritance",
    conceptElement = "Klasse.Inheritance",
    target = "AbstractKlasse",
    style = Style.defaultStyle,
    placings = List(
      Placing(
        style = xStyle,
        position = PlacingPosition(1.0),
        geoModel = Polygon(
          points = List(
            Point(-10, 10),
            Point(0, 0),
            Point(-10, -10)
          ),
          childGeoModels = List(),
          style = xStyle
        )
      )
    )
  )

  private val aggregationEdge = Edge(
    name = "aggregation",
    conceptElement = "Klasse.Aggregation",
    target = "Klasse",
    style = xStyle,
    placings = List(
      Placing(
        style = xStyle,
        position = PlacingPosition(1.0),
        geoModel = Polygon(
          points = List(
            Point(0, 0),
            Point(-20, 10),
            Point(-40, 0),
            Point(-20, -10)
          ),
          childGeoModels = List(),
          style = aggregationStyle
        )
      )
    )
  )

  private val componentEdge = Edge(
    name = "component",
    conceptElement = "Klasse.Component",
    target = "Klasse",
    style = xStyle,
    placings = List(
      Placing(
        style = xStyle,
        position = PlacingPosition(1.0),
        geoModel = Polygon(
          points = List(
            Point(0, 0),
            Point(20, 0),
            Point(40, 0),
            Point(20, -10)
          ),
          childGeoModels = List(),
          style = componentStyle
        )
      )
    )
  )

  private val baseClassRealizationEdge = Edge(
    name = "BaseClassRealization",
    conceptElement = "Klasse.BaseClassRealization",
    target = "InterfaceKlasse",
    style = xStyle,
    placings = List(
      Placing(
        style = yStyle,
        position = PlacingPosition(1.0),
        geoModel = Polygon(
          points = List(
            Point(-10, 10),
            Point(0, 0),
            Point(-10, -10)
          ),
          childGeoModels = List(),
          style = realizationStyle
        )
      )
    )
  )

  private val classNode = Node(
    name = "classNode",
    conceptElement = "Klasse",
    edges = List(
      inheritanceEdge,
      baseClassRealizationEdge,
      componentEdge,
      aggregationEdge
    ),
    size = Size(0, 0, 200, 200, 400, 400),
    style = xStyle,
    resizing = gdsl.shape.Resizing(horizontal = false, vertical = false, proportional = true),
    geoModels = List(
      Rectangle(
        size = geomodel.Size(200, 50),
        position = geomodel.Position(0, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text1",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text2",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      RoundedRectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 150),
        curve = geomodel.Size(10, 10),
        childGeoModels = List(
          new TextField(
            identifier = "text3",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Ellipse(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 150),
        childGeoModels = List(),
        style = xStyle
      ),
      StaticText(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 150),
        childGeoModels = List(),
        style =xStyle,
        text = "test"
      ),
      RepeatingBox(
        forEach = "Inheritance",
        forAs = "i",
        editable = false,
        style = xStyle,
        childGeoModels = List(
          VerticalLayout(
            style = xStyle,
            childGeoModels = List(geomodel.Line(
              startPoint = geomodel.Point(1, 51),
              endPoint = geomodel.Point(2, 52),
              childGeoModels = List(),
              style = xStyle
            ))
          )
        )
      ),
      HorizontalLayout(
        style = xStyle,
        childGeoModels = List(Polyline(
          points = List(geomodel.Point(1, 51), geomodel.Point(2, 52)),
          childGeoModels = List(),
          style = xStyle
        ))
      )
    )
  )

  private val abClassNode = Node(
    name = "abClassNode",
    conceptElement = "AbstractKlasse",
    edges = List(realizationEdge),
    size = Size(0, 0, 200, 200, 400, 400),
    style = xStyle,
    resizing = gdsl.shape.Resizing(horizontal = false, vertical = false, proportional = true),
    geoModels = List(
      Rectangle(
        size = geomodel.Size(200, 50),
        position = geomodel.Position(10, 0),
        childGeoModels = List(
          new TextField(
            identifier = "text11",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text21",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 150),
        childGeoModels = List(
          new TextField(
            identifier = "text31",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      )
    )
  )

  private val inClassNode = Node(
    name = "inClassNode",
    conceptElement = "InterfaceKlasse",
    edges = List(),
    size = Size(0, 0, 200, 200, 400, 400),
    style = xStyle,
    resizing = gdsl.shape.Resizing(horizontal = false, vertical = false, proportional = true),
    geoModels = List(
      Rectangle(
        size = geomodel.Size(200, 50),
        position = geomodel.Position(10, 0),
        childGeoModels = List(
          new TextField(
            identifier = "text113",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align(
              vertical = Align.Vertical.top,
              horizontal = Align.Horizontal.left
            ),
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text213",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align(
              vertical = Align.Vertical.middle,
              horizontal = Align.Horizontal.middle
            ),
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 150),
        childGeoModels = List(
          new TextField(
            identifier = "text313",
            textBody = "",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align(
              vertical = Align.Vertical.bottom,
              horizontal = Align.Horizontal.right
            ),
            childGeoModels = List(),
            style = xStyle
          )
        ),
        style = xStyle
      )
    )
  )

  "A Graphical DSL parser should success" - {
    "for an example input" in {
      val result = parser.parse(ConceptCreatorHelper.exampleConcept, style, shape, diagram)

      result.isSuccess shouldBe true
      val parsed = result.toEither.right.get

      parsed.styles.size shouldBe 7

      // NOTE: we do not test with "should contain()" here, but use direct checks
      // for every list index cause of better, faster and easier comparison
      // in case of a failure
      parsed.styles.head shouldBe Style.defaultStyle
      parsed.styles(1) shouldBe yStyle
      parsed.styles(2) shouldBe classTextStyle
      parsed.styles(3) shouldBe xStyle
      parsed.styles(4) shouldBe realizationStyle
      parsed.styles(5) shouldBe aggregationStyle
      parsed.styles(6) shouldBe componentStyle

      parsed.shape.edges.head shouldBe inheritanceEdge
      parsed.shape.edges(1) shouldBe realizationEdge
      parsed.shape.edges(2) shouldBe baseClassRealizationEdge
      parsed.shape.edges(3) shouldBe componentEdge
      parsed.shape.edges(4) shouldBe aggregationEdge

      parsed.shape.nodes.head shouldBe classNode
      parsed.shape.nodes(1) shouldBe abClassNode
      parsed.shape.nodes(2) shouldBe inClassNode
    }
  }

  "A Graphical DSL parser should fail" - {
    "for an example with missing styles" in {
      val result = parser.parse(ConceptCreatorHelper.exampleConcept, "", shape, diagram)

      result.isSuccess shouldBe false
      val parsed = result.toEither.left.get

      parsed.errorDsl shouldBe "shape"
    }
    "for an example with missing shapes" in {
      val result = parser.parse(ConceptCreatorHelper.exampleConcept, style, "", diagram)

      result.isSuccess shouldBe false
      val parsed = result.toEither.left.get

      parsed.errorDsl shouldBe "diagram"
    }
  }

}
