package generator.generators.style

/**
 * Created by julian on 07.10.15.
 * the generator object for style.js
 */

import generator.model.style._
import generator.model.style.color.Transparent
import generator.model.style.gradient.{Gradient, HORIZONTAL}
import java.nio.file.{Paths, Files}


object StyleGenerator {


  def filename = "style.js"

  def doGenerate(styles: List[Style], outputLocation: String) = {
    var output = head
    for (style <- styles) output += compile(style)
    output += footer + headDia
    for (style <- styles) output += compileDia(style)
    output += footerDia
    Files.write(Paths.get(outputLocation + filename), output.getBytes)
  }

  def compile(s: Style) = body(s)

  def headDia =
    raw"""function getDiagramHighlighting(stylename) {

                      var highlighting;

                      switch(stylename) {

    """

  def compileDia(s: Style) = {
    val (selected, multiselected, allowed, unallowed) =
      (s.selected_highlighting, s.multiselected_highlighting, s.allowed_highlighting, s.unallowed_highlighting)
    val name = s.name
    val highlighting = ""+selected.getOrElse("")+multiselected.getOrElse("")+allowed.getOrElse("")+unallowed.getOrElse("")
    if (!highlighting.isEmpty)
      raw"""case "$name":

              var highlighting = '$highlighting';

            break;
      """
    else ""
  }

  def selected(s: Style) =
    if (s.selected_highlighting isDefined) {
      raw""".free-transform { border: 2px dashed  ${s.selected_highlighting.get.getRGBValue}; }"""
    } else ""

  def multiselected(s: Style) =
    if (s.multiselected_highlighting isDefined) {
      raw""".selection-box { border: 2px solid ${s.multiselected_highlighting.get.getRGBValue}; }"""
    } else ""

  def allowed(s: Style):String =
    if (s.allowed_highlighting isDefined) {
      raw""".linking-allowed { outline: 2px solid ${s.allowed_highlighting.get.getRGBValue}; }"""
    } else ""

  def unallowed(s: Style):String =
    if (s.unallowed_highlighting isDefined) {
      raw""".linking-unallowed { solid : 2px solid ${s.unallowed_highlighting.get.getRGBValue}; }"""
    } else ""

  def footerDia: String =
    raw"""
                default:
                  highlighting = '';
                break;
         }
         return highlighting;
       }
       """

  def head =
    """
       function getStyle(stylename) {

        var style;

        switch(stylename) {
    """

  def body(s: Style) =
    s"""
    /*
    This is a generated JavaScript file for the spray JointJS online editor.
    The ${s.name} function will be called when the shapes are created, setting style attributes.
    ${s.description.getOrElse("")}
    */
    case '${s.name}':
      style = {
      '.': { filter: Stencil.filter },
      ${createFontAttributes(s)}
      ${createRectangleAttributes(s)}
      ${createRoundedRectangleAttributes(s)}
      ${createCircleAttributes(s)}
      ${createEllipseAttributes(s)}
      ${createLineAttributes(s)}
      ${createConnectionAttributes(s)}
      ${createPolygonAttributes(s)}
      ${createPolylineAttributes(s)}
      $createBoundingBoxStyle
    };
    break;"""

  def footer = """				default:
                 						style = {};
                 					break;

                 				}
                 				return style;
                 			}
               """

  def createFontAttributes(s: Style) =
    s"""
          /*
          Generated Text style attributes
          */
            text: {
              ${fontAttributes(s)}
            },
    """

  def fontAttributes(s: Style) = {
    raw"""
       'dominant-baseline': "text-before-edge",
       'font-family': '${s.font_name.getOrElse("sans-serif")}',
       'font-size': '${s.font_size.getOrElse("11")}',
       'fill': '${ val c = s.font_color; if (c.isDefined) c.get.getRGBValue else "#000000" } ',
       'font-weight': ' ${ if (s.font_bold.getOrElse(false)) "700" else "400" }'
       ${if (s.font_italic.getOrElse(false)) raw""",'font-style': 'italic' """ else ""}
       """
  }


  def createRectangleAttributes(s: Style) =
    raw"""
          /*
          Generated rectangle style attributes.
          */
          rect: {
            ${commonAttributes(s)}
          },
      """

  def createRoundedRectangleAttributes(s: Style) =
    raw"""
          /*
          Generated rounded rectangle style attributes.
          */
          rect: {
            ${commonAttributes(s)}
          },
      """

  def createCircleAttributes(s: Style) =
    raw"""
          /*
          Generated circle style attributes.
          */
          circle: {
            ${commonAttributes(s)}
          },
      """

  def createPolygonAttributes(s: Style) =
    raw"""
          /*
          Generated polyline style attributes.
          */
          polygon: {
            ${commonAttributes(s)}
          },
      """


  def createPolylineAttributes(s: Style) =
    raw"""
          /*
          Generated polyline style attributes.
          */
          polyline: {
            ${commonAttributes(s)}
          },
      """


  def createEllipseAttributes(s: Style) =
    raw"""
          /*
          Generated ellipse style attributes.
          */
          ellipse: {
            ${commonAttributes(s)}
          },
      """


  def createLineAttributes(s: Style):String =
    raw"""
          /*
          Generated Line style attributes.
          */
          line: {
            ${commonAttributes(s)}
          },
      """


  def createConnectionAttributes(s: Style) =
    raw"""
          /*
          Generated connection style attributes.
          */
          '.connection': {
            ${commonAttributes(s)}
          },
      """

  def createBoundingBoxStyle =
    raw"""
          /*
          Generated bounding box styles.
          */
          '.bounding-box':{
            'stroke-width': 0,
            fill: 'transparent'
          }
      """

  def commonAttributes(s: Style):String =
    raw"""
    ${if (checkBackgroundGradientNecessary(s))
      createGradientAttributes(s.background_color.get.asInstanceOf[Gradient],
        s.gradient_orientation.get match { case HORIZONTAL => true
        case _ => false})
    else
        createBackgroundAttributes(s)}
        ${createLineAttributesFromLayout(s)}
        """


  def checkBackgroundGradientNecessary(s: Style) = if(s.background_color.isDefined && s.background_color.get.isInstanceOf[Gradient]) true else false

  def createGradientAttributes(gr: Gradient, horizontal: Boolean) = {
    val areas = for (area <- gr.area)yield{s"offset: '${(area.offset * 100).toInt}%', color: '${area.color.getRGBValue}'"}
    var ret = """
      fill: {
        type: 'linearGradient',
        stops: [
              """

    ret += areas.mkString("{", "\n}, {", "}")

    if(horizontal){
      ret += "]"
    }else{
      ret +=
        raw"""],
           attrs: {
                    x1: '0%',
                    y1: '0%',
                    x2: '0%',
                    y2: '100%'
                  }
        """
    }
    ret += "},"
    ret
  }


  def createBackgroundAttributes(s: Style):String = {
    if (s.background_color.isDefined) {
      val bg_color = s.background_color.get
      raw"""
          fill: '${bg_color.getRGBValue}',
          'fill.opacity':${bg_color.createOpacityValue},
        """
    }
    else ""
  }

  def createLineAttributesFromLayout(s:Style) = {
    var ret = """"""
    if (s.line_color.isEmpty)
      ret += """
      				stroke: '#000000',
      				'stroke-width': 0,
      				'stroke-dasharray': "0",
             """
    else
      s.line_color.get match {
        case Transparent=> ret +=
          """
                                        'stroke-opacity': 0,
          """
        case _ => ret +=
          """
      				stroke: '"""+s.line_color.get.getRGBValue+"""'"""
          if(s.line_width.isDefined)
            ret += """,'stroke-width':"""+s.line_width.get
          if(s.line_style isDefined)
            s.line_style.get match {
              case DASH => ret +=
                """
                                  ,'stroke-dasharray': "10,10"
                """
              case DOT => ret +=
                """
                                  ,'stroke-dasharray': "5,5
                """
              case DASHDOT => ret +=
                """
                                  ,'stroke-dasharray': "10,5,5,5"
                """
              case DASHDOTDOT => ret +=
                """
                                  ,'stroke-dasharray': "10,5,5,5,5,5"
                """
              case _ => ret +=
                """
                                  ,'stroke-dasharray': "0"
                """
            }
      }
    ret
  }


  /*The rest of the methodes (refering to styleGEnerator.xtext from MoDiGenV2) is defined in the Color classes directly*/
}
