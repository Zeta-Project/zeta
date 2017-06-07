package de.htwg.zeta.server.generator.generators.style

import de.htwg.zeta.server.generator.model.style.DASH
import de.htwg.zeta.server.generator.model.style.DASHDOT
import de.htwg.zeta.server.generator.model.style.DASHDOTDOT
import de.htwg.zeta.server.generator.model.style.DOT
import de.htwg.zeta.server.generator.model.style.LineStyle
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.model.style.color.Transparent
import de.htwg.zeta.server.generator.model.style.gradient.Gradient
import de.htwg.zeta.server.generator.model.style.gradient.HORIZONTAL
import models.file.File
import de.htwg.zeta.server.model.result.Unreliable

/**
 * The StyleGenerator object, responsible for generation of style.js
 */
object StyleGenerator {

  private val Filename = "style.js"


  /**
   * @param styles the Styles
   * @return Generator as File
   */
  def doGenerateResult(styles: List[Style]): Unreliable[File] = {
    Unreliable(() => File(Filename, doGenerateContent(styles)), "failed trying to create the Style generators")
  }

  /**
   * @param styles the Styles
   * @return Generator as String
   */
  private def doGenerateContent(styles: List[Style]): String = {
    s"""
      |${generateGetStyle(styles)}
      |${generateGetDiagramHighlighting(styles)}
      """.stripMargin
  }


  /** Generates the getStyle function */
  private def generateGetStyle(styles: List[Style]): String = {
    s"""
      |function getStyle(stylename) {
      |  var style;
      |  switch(stylename) {
      |    ${styles.map(style => generateStyleCase(style)).mkString("")}
      |    default:
      |      style = {};
      |      break;
      |  }
      |  return style;
      |}
      |""".stripMargin
  }

  /** Generates a case of the switch-case in the getStyle function */
  private def generateStyleCase(s: Style) = {
    s"""
      |case '${s.name}':
      |  style = {
      |  ${createFontAttributes(s)}
      |  ${commonAttributes(s)}
      |};
      |break;
      |""".stripMargin
  }

  /**
   * generates getDiagramHighlighting function with the highlighting styles
   */
  private def generateGetDiagramHighlighting(styles: List[Style]): String = {
    s"""
      |function getDiagramHighlighting(stylename) {
      |  var highlighting;
      |  switch(stylename) {
      |    ${styles.map(style => generateDiagramHighlightingCases(style)).mkString("")}
      |    default:
      |      highlighting = '';
      |    break;
      |  }
      |  return highlighting;
      |}
      |""".stripMargin
  }

  /**
   * generates a case for the switch case of the getDiagramHilighting
   */
  private def generateDiagramHighlightingCases(s: Style): String = {
    val highlighting =
      s"""${getSelected(s)}${getMultiselected(s)}${getAllowed(s)}${getUnallowed(s)}"""
    if (!highlighting.isEmpty) {
      s"""
        |case "${s.name}":
        |  var highlighting = '$highlighting';
        |  break;
        |""".stripMargin
    } else {
      ""
    }
  }

  private def getSelected(s: Style): String = {
    s.selected_highlighting match {
      case None => ""
      case Some(value) =>
        s""".paper-container .free-transform { border: 1px dashed  ${value.getRGBValue}; }"""
    }
  }

  private def getMultiselected(s: Style): String = {
    s.multiselected_highlighting match {
      case None => ""
      case Some(value) =>
        s""".paper-container .selection-box { border: 1px solid ${value.getRGBValue}; }"""
    }
  }

  private def getAllowed(s: Style): String = {
    s.allowed_highlighting match {
      case None => ""
      case Some(value) =>
        s""".paper-container .linking-allowed { outline: 2px solid ${value.getRGBValue}; }"""
    }
  }

  private def getUnallowed(s: Style): String = {
    s.unallowed_highlighting match {
      case None => ""
      case Some(value) =>
        s""".paper-container .linking-unallowed { outline: 2px solid ${value.getRGBValue}; }"""
    }
  }

  /** generates all text style attributes for the style */
  private def createFontAttributes(s: Style) = {
    s"""
text: {
  ${fontAttributes(s)}
},
""".stripMargin
  }

  /** generates all text style attributes */
  def fontAttributes(s: Style): String = {
    s"""
      |'dominant-baseline': "text-before-edge",
      |'font-family': '${s.font_name.getOrElse("sans-serif")}',
      |'font-size': '${s.font_size.getOrElse("11")}',
      |'fill': '${val c = s.font_color; if (c.isDefined) c.get.getRGBValue else "#000000"}',
      |'font-weight': ' ${if (s.font_bold.isDefined && s.font_bold.get) "bold" else "normal"}'
      |${
      if (s.font_italic.getOrElse(false)) {
        """,'font-style': 'italic' """
      } else {
        ""
      }
    }
      |""".stripMargin
  }

  /** creates all common attributes, which are not associated with text */
  def commonAttributes(s: Style): String = {
    s"""
      |${createBackgroundAttributes(s)}
      |'fill-opacity':${s.transparency.getOrElse("1.0")},
      |${createLineAttributesFromLayout(s)}
      |""".stripMargin
  }

  private def createBackgroundAttributes(s: Style): String = {
    s.background_color match {
      case Some(gradient: Gradient) =>
        s.gradient_orientation match {
          case Some(HORIZONTAL) => createGradientAttributes(gradient, horizontal = true)
          case _ => createGradientAttributes(gradient, horizontal = false)
        }
      case _ =>
        createNonGradientBackgroundAttributes(s)
    }
  }


  /** generates gradient background */
  private def createGradientAttributes(gr: Gradient, horizontal: Boolean): String = {
    s"""
      |  fill: {
      |    type: 'linearGradient',
      |    stops: [
      |    ${
      gr.area.map(
        area => s"offset: '${(area.offset * 100).toInt}%', color: '${area.color.getRGBValue}'"
      ).mkString("{", "\n}, {", "}")
    }
      |    ]
        ${
      if (horizontal) {
        ""
      } else {
        """,attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%'}"""
      }
    }
      |  },
      |""".stripMargin
  }

  /** generates simple one colored background */
  private def createNonGradientBackgroundAttributes(s: Style): String = {
    s.background_color match {
      case None => ""
      case Some(value) =>
        s"""
          |fill: '${value.getRGBValue}',
          |""".stripMargin
    }
  }

  /** generates the stroke attributes */
  private def createLineAttributesFromLayout(s: Style): String = {
    s.line_color match {
      case None =>
        s"""
          |stroke: '#000000',
          |'stroke-width': 0,
          |'stroke-dasharray': "0"
          |""".stripMargin
      case Some(value) => value match {
        case Transparent =>
          s"""
            |'stroke-opacity': 0,
            |""".stripMargin
        case _ =>
          s"""
            |stroke: '${value.getRGBValue}',
            |${processLineWidth(s.line_width)},
            |${processLineStyle(s.line_style)}
          """.stripMargin
      }
    }
  }

  private def processLineWidth(lineWidth: Option[Int]): String = {
    lineWidth match {
      case None => ""
      case Some(value) => "'stroke-width':" + value
    }
  }

  private def processLineStyle(lineStyle: Option[LineStyle]): String = {
    lineStyle match {
      case None => ""
      case Some(value) => value match {
        case DASH =>
          "'stroke-dasharray': \"10,10\""
        case DOT =>
          "'stroke-dasharray': \"5,5\""
        case DASHDOT =>
          "'stroke-dasharray': \"10,5,5,5\""
        case DASHDOTDOT =>
          "'stroke-dasharray': \"10,5,5,5,5,5\""
        case _ =>
          "'stroke-dasharray': \"0\""
      }
    }
  }
}
