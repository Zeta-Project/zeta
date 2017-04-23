package generator.generators.style

import generator.model.style.DASH
import generator.model.style.DASHDOT
import generator.model.style.DASHDOTDOT
import generator.model.style.DOT
import generator.model.style.LineStyle
import generator.model.style.Style
import generator.model.style.color.Transparent
import generator.model.style.gradient.Gradient
import generator.model.style.gradient.HORIZONTAL

import java.nio.file.Paths
import java.nio.file.Files

/**
 * The StyleGenerator object, responsible for generation of style.js
 */
object StyleGenerator {

  def filename = "style.js"

  /**
   * Generates the Output String and writes the String
   * to the file style.js in the outputLocation
   */
  def doGenerate(styles: List[Style], outputLocation: String): Unit = {
    val output = doGenerateFile(styles)

    Files.write(Paths.get(outputLocation + filename), output.getBytes)
  }

  /**
    *
    * @param styles the Styles
    * @return Generator as String
    */
  def doGenerateFile(styles: List[Style]): String = {
    s"""
        ${generateGetStyle(styles)}
        ${generateGetDiagramHighlighting(styles)}
      """
  }

  /** Generates the getStyle function */
  private def generateGetStyle(styles: List[Style]): String = {
    s"""
      function getStyle(stylename) {
        var style;
        switch(stylename) {
          ${styles.map(style => generateStyleCase(style)).mkString("")}
          default:
            style = {};
            break;
        }
        return style;
      }
    """
  }

  /** Generates a case of the switch-case in the getStyle function */
  private def generateStyleCase(s: Style) =
    s"""
      case '${s.name}':
        style = {
        ${createFontAttributes(s)}
        ${commonAttributes(s)}
      };
      break;
    """

  /**
   * generates getDiagramHighlighting function with the highlighting styles
   */
  private def generateGetDiagramHighlighting(styles: List[Style]): String = {
    s"""
      function getDiagramHighlighting(stylename) {
        var highlighting;
        switch(stylename) {
          ${styles.map(style => generateDiagramHighlightingCases(style)).mkString("")}
          default:
            highlighting = '';
          break;
        }
        return highlighting;
      }
    """
  }

  /**
   * generates a case for the switch case of the getDiagramHilighting
   */
  private def generateDiagramHighlightingCases(s: Style) = {
    val highlighting = s"""${getSelected(s)}${getMultiselected(s)}${getAllowed(s)}${getUnallowed(s)}"""
    if (!highlighting.isEmpty) {
      raw"""
      case "${s.name}":
        var highlighting = '$highlighting';
        break;
      """
    } else {
      ""
    }
  }

  private def getSelected(s: Style) = {
    s.selected_highlighting match {
      case None => ""
      case Some(value) =>
        s""".paper-container .free-transform { border: 1px dashed  ${value.getRGBValue}; }"""
    }
  }

  private def getMultiselected(s: Style) = {
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
  private def createFontAttributes(s: Style) =
    s"""
    text: {
      ${fontAttributes(s)}
    },
    """

  /** generates all text style attributes */
  def fontAttributes(s: Style) = {
    raw"""
      'dominant-baseline': "text-before-edge",
      'font-family': '${s.font_name.getOrElse("sans-serif")}',
      'font-size': '${s.font_size.getOrElse("11")}',
      'fill': '${val c = s.font_color; if (c.isDefined) c.get.getRGBValue else "#000000"}',
      'font-weight': ' ${if (s.font_bold.isDefined && s.font_bold.get) "bold" else "normal"}'
      ${if (s.font_italic.getOrElse(false)) raw""",'font-style': 'italic' """ else ""}
    """
  }

  /** creates all common attributes, which are not associated with text */
  def commonAttributes(s: Style): String = {
    raw"""
      ${
      if (checkBackgroundGradientNecessary(s)) {
        createGradientAttributes(
          s.background_color.get.asInstanceOf[Gradient],
          s.gradient_orientation.get match {
            case HORIZONTAL => true
            case _ => false
          }
        )
      } else {
        createBackgroundAttributes(s)
      }
    }
      'fill-opacity':${s.transparency.getOrElse("1.0")},
      ${createLineAttributesFromLayout(s)}
    """
  }

  private def checkBackgroundGradientNecessary(s: Style) = {
    s.background_color match {
      case None => false
      case Some(value) => value match {
        case t: Gradient => true
        case _ => false
      }
    }
  }

  /** generates gradient background */
  private def createGradientAttributes(gr: Gradient, horizontal: Boolean) = {
    s"""
      fill: {
        type: 'linearGradient',
        stops: [
          ${
            gr.area.map(
              area => s"offset: '${(area.offset * 100).toInt}%', color: '${area.color.getRGBValue}'"
            ).mkString("{", "\n}, {", "}")
          }
        ]
        ${
          if (horizontal) {
            ""
          } else {
            """,attrs: { x1: '0%', y1: '0%', x2: '0%', y2: '100%'}"""
          }
        }
      },
    """
  }

  /** generates simple one colored background */
  private def createBackgroundAttributes(s: Style): String = {
    s.background_color match {
      case None => ""
      case Some(value) =>
        raw"""
          fill: '${value.getRGBValue}',
        """
    }
  }

  /** generates the stroke attributes */
  private def createLineAttributesFromLayout(s: Style) = {
    s.line_color match {
      case None =>
        """
          stroke: '#000000',
          'stroke-width': 0,
          'stroke-dasharray': "0"
        """
      case Some(value) => value match {
        case Transparent =>
          """
            'stroke-opacity': 0,
          """
        case _ =>
          """
            stroke: '""" + value.getRGBValue +
          """'""" + processLineWidth(s.line_width) + processLineStyle(s.line_style)
      }
    }
  }

  private def processLineWidth(lineWidth: Option[Int]) = {
    lineWidth match {
      case None => ""
      case Some(value) => """,'stroke-width':""" + value
    }
  }

  private def processLineStyle(lineStyle: Option[LineStyle]) = {
    lineStyle match {
      case None => ""
      case Some(value) => value match {
        case DASH =>
          """
            ,'stroke-dasharray': "10,10"
          """
        case DOT =>
          """
            ,'stroke-dasharray': "5,5
          """
        case DASHDOT =>
          """
            ,'stroke-dasharray': "10,5,5,5"
          """
        case DASHDOTDOT =>
          """
            ,'stroke-dasharray': "10,5,5,5,5,5"
          """
        case _ =>
          """
            ,'stroke-dasharray': "0"
          """
      }
    }
  }
}
