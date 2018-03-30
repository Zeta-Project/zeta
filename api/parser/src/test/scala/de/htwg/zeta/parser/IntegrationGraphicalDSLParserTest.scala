package de.htwg.zeta.parser

import de.htwg.zeta.common.models.project.gdsl
import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.Placing
import de.htwg.zeta.common.models.project.gdsl.shape.Position
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Rectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
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
           rectangle {
             size(width: 200, height: 100)
             position(x: 0, y: 150)
                 textfield   {
                     position(x: 0, y: 0)
                     identifier: text3
                     size(width: 10, height: 40)
                     editable: true
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
             placing {
                 style : X
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
    placings = List(
      Placing(
        style = yStyle,
        position = Position(1, 1.0),
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
    placings = List(
      Placing(
        style = xStyle,
        position = Position(1, 1.0),
        geoModel = Polygon(
          points = List(
            Point(-10, 10),
            Point(0, 0),
            Point(-10, -10)
          ),
          childGeoModels = List(),
          style = Style.defaultStyle
        )
      )
    )
  )

  private val aggregationEdge = Edge(
    name = "aggregation",
    conceptElement = "Klasse.Aggregation",
    target = "Klasse",
    placings = List(
      Placing(
        style = xStyle,
        position = Position(1, 1.0),
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
    placings = List(
      Placing(
        style = xStyle,
        position = Position(1, 1.0),
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
    placings = List(
      Placing(
        style = yStyle,
        position = Position(1, 1.0),
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
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text2",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(0, 150),
        childGeoModels = List(
          new TextField(
            identifier = "text3",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
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
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text21",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 150),
        childGeoModels = List(
          new TextField(
            identifier = "text31",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
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
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 50),
        childGeoModels = List(
          new TextField(
            identifier = "text213",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      ),
      Rectangle(
        size = geomodel.Size(200, 100),
        position = geomodel.Position(10, 150),
        childGeoModels = List(
          new TextField(
            identifier = "text313",
            size = geomodel.Size(10, 40),
            position = geomodel.Position(0, 0),
            editable = true,
            multiline = false,
            align = Align.default,
            childGeoModels = List(),
            style = Style.defaultStyle
          )
        ),
        style = Style.defaultStyle
      )
    )
  )

  "A Graphical DSL parser should success" - {
    "for an example input" in {
      val result = parser.parse(ConceptCreatorHelper.exampleConcept, style, shape, diagram)

      result.isSuccess shouldBe true
      val parsed = result.toEither.right.get

      parsed.styles.size shouldBe 6

      parsed.styles should contain(yStyle)
      parsed.styles should contain(classTextStyle)
      parsed.styles should contain(xStyle)
      parsed.styles should contain(realizationStyle)
      parsed.styles should contain(aggregationStyle)
      parsed.styles should contain(componentStyle)

      parsed.shape.edges should contain(realizationEdge)
      parsed.shape.edges should contain(inheritanceEdge)
      parsed.shape.edges should contain(aggregationEdge)
      parsed.shape.edges should contain(componentEdge)
      parsed.shape.edges should contain(baseClassRealizationEdge)

      parsed.shape.nodes should contain(classNode)
      parsed.shape.nodes should contain(abClassNode)
      parsed.shape.nodes should contain(inClassNode)
    }
  }

}
