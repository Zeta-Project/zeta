package generator

import generator.util.{SprayParser, Cache}

/**
 * Created by julian on 9/3/15.
 * Diverse Tests für die Klassen parser ClassHierarchy Diagram Style
 */
object ParserApp extends App {

  val hierarchyContainer = Cache()

  val classUno = """style BpmnDefaultStyle {
                  description = "The default style of the petrinet hierarchyContainer type."
                  transparency = 0.95
                  background-color = green
                  line-color = black
                  line-style = solid
                  line-width = 1
                  font-color = white
                  font-name = "Tahoma"
                  font-size = 6
                  font-bold = yes
                  font-italic = yes
                  gradient-orientation = horizontal
                 }"""

  val classDuo = """style BpmnExtending extends BpmnDefaultStyle{
                  line-color = green
                }"""

  val parser = new SprayParser(hierarchyContainer)

  val newStyle = parser.parseRawStyle(classUno).head
  val anotherStyle = parser.parseRawStyle(classDuo).head

  hierarchyContainer.styleHierarchy.root.rPrint()
  println(hierarchyContainer.styleHierarchy(newStyle).data.line_color)
  println(hierarchyContainer.styleHierarchy(anotherStyle).data.line_color)

  val classTres =
    """style yetAnotherStyle extends BpmnExtending{
      font-size = 10
      }"""
  val yetAnotherStyle = parser.parseRawStyle(classTres).head
  println(hierarchyContainer.styleHierarchy(newStyle).data.       font_size)
  println(hierarchyContainer.styleHierarchy(anotherStyle).data.   font_size)
  println(hierarchyContainer.styleHierarchy(yetAnotherStyle).data.font_size)

  hierarchyContainer.styleHierarchy.root.rPrint()


  val styleA =
    """style A{
      font-size = 20
      highlighting (allowed = blue)
      }""".stripMargin

  val styleB =
  """style B extends A{
    line-color = blue
    }"""

  val styleC =
    """style C extends B{
      description = "The default style of the petrinet hierarchyContainer type."
      transparency = 0.95
      background-color = green
      line-style = solid
      line-color = white
      line-width = 1
      font-color = white
      font-name = "Tahoma"
      font-bold = yes
      font-italic = yes
      gradient-orientation = horizontal
      }"""
  val A = parser.parseRawStyle(styleA).head
  val B = parser.parseRawStyle(styleB).head
  val C = parser.parseRawStyle(styleC).head

  println(hierarchyContainer.styleHierarchy(A).data.line_color)
  println(hierarchyContainer.styleHierarchy(B).data.line_color)
  println(hierarchyContainer.styleHierarchy(C).data.line_color)

  println(hierarchyContainer.styleHierarchy(A).data.font_size)
  println(hierarchyContainer.styleHierarchy(B).data.font_size)
  println(hierarchyContainer.styleHierarchy(C).data.font_size)

  println(StyleGenerator.compileDia(A))
}

