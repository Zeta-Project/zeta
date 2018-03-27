package de.htwg.zeta.parser

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.gdsl.style.Background
import de.htwg.zeta.common.models.project.gdsl.style.Color
import de.htwg.zeta.common.models.project.gdsl.style.Dashed
import de.htwg.zeta.common.models.project.gdsl.style.Dotted
import de.htwg.zeta.common.models.project.gdsl.style.Font
import de.htwg.zeta.common.models.project.gdsl.style.Line
import de.htwg.zeta.common.models.project.gdsl.style.Solid
import de.htwg.zeta.common.models.project.gdsl.style.Style
import de.htwg.zeta.parser.style.LineStyle
import de.htwg.zeta.parser.style.LineWidth
import org.scalatest.FreeSpec
import org.scalatest.Matchers

class IntegrationGraphicalDSLParserTest extends FreeSpec with Matchers {

  private val parser = new GraphicalDSLParser()

  "A Graphical DSL parser should success" - {
    "for an example input" in {

      val style =
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
         }
         
         style aggregation {
           description = "Style for aggregation"
           background-color = white
         }
         
         style component {
           description = "Style for component"
           background-color = black
         }"""

      val diagram =
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

      val shape =
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

      val concept = new Concept(
        classes = List(
          cClass("AbstractKlasse", List(cAttribute("text11"), cAttribute("text21"), cAttribute("text31"))),
          cClass("InterfaceKlasse", List(cAttribute("text113"), cAttribute("text213"), cAttribute("text313"))),
          cClass("Klasse", List(cAttribute("text1"), cAttribute("text2"), cAttribute("text3")))
        ),
        references = List(
          cReference("BaseClassRealization", "Klasse", "InterfaceKlasse"),
          cReference("Realization", "InterfaceKlasse", "AbstractKlasse"),
          cReference("Inheritance", "Klasse", "AbstractKlasse")
        ),
        enums = List(),
        attributes = List(
          cAttribute("text11"),
          cAttribute("text21"),
          cAttribute("text31"),
          cAttribute("text113"),
          cAttribute("text213"),
          cAttribute("text313"),
          cAttribute("text1"),
          cAttribute("text2"),
          cAttribute("text3")
        ),
        methods = List(),
        uiState = ""
      )

      val result = parser.parse(concept, style, shape, diagram)

      result.isSuccess shouldBe true
      val parsed = result.toEither.right.get

      parsed.styles.size shouldBe 6
      parsed.styles should contain(new Style(
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
      ))
      parsed.styles should contain(new Style(
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
      ))
      parsed.styles should contain(new Style(
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
      ))
      parsed.styles should contain(new Style(
        name = "realization",
        description = "Style for realization",
        line = Line.defaultLine,
        background = new Background(Color(255, 255, 255, 1)),
        font = Font.defaultFont,
        transparency = 1
      ))
      parsed.styles should contain(new Style(
        name = "aggregation",
        description = "Style for aggregation",
        line = Line.defaultLine,
        background = new Background(Color(255, 255, 255, 1)),
        font = Font.defaultFont,
        transparency = 1
      ))
      parsed.styles should contain(new Style(
        name = "component",
        description = "Style for component",
        line = Line.defaultLine,
        background = new Background(Color(0, 0, 0, 1)),
        font = Font.defaultFont,
        transparency = 1
      ))
    }
  }

  def cReference(name: String, source: String, target: String): MReference = new MReference(
    name = name,
    description = "",
    sourceDeletionDeletesTarget = true,
    targetDeletionDeletesSource = true,
    sourceClassName = source,
    targetClassName = target,
    attributes = List(),
    methods = List()
  )

  def cClass(name: String, attributes: List[MAttribute]): MClass = new MClass(
    name = name,
    description = "",
    abstractness = true,
    superTypeNames = List(),
    inputReferenceNames = List(),
    outputReferenceNames = List(),
    attributes = attributes,
    methods = List()
  )

  def cAttribute(name: String): MAttribute = new MAttribute(
    name = name,
    globalUnique = true,
    localUnique = true,
    typ = AttributeType.StringType,
    default = AttributeValue.StringValue(""),
    constant = false,
    singleAssignment = false,
    expression = "",
    ordered = true,
    transient = false
  )

}
