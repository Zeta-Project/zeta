package generator.generators.style

import generator.model.style._

/**
 * Created by julian on 07.10.15.
 * the generator object for style.js
 */

object StyleGenerator {

  def filepath = "style.js"

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
    val highlighting = ""+selected.getOrElse(multiselected.getOrElse(allowed.getOrElse(unallowed.getOrElse(""))))
    if (!highlighting.isEmpty)
      raw"""case "$name":

              var highlighting = '$highlighting';

            break;
      """
    else ""
  }

  def selected(s: Style) =
    if (s.selected_highlighting isDefined) {//TODO check if getRGBValue and getColorValue do the same things
      raw""".free-transform { border: 2px dashed """ + s.selected_highlighting.get.getRGBValue + """; }"""
    } else ""

  def multiselected(s: Style) =
    if (s.multiselected_highlighting isDefined) {
      raw""".selection-box { border: 2px solid """ + s.multiselected_highlighting.get.getRGBValue + """; }"""
    } else ""

  def allowed(s: Style) =
    if (s.allowed_highlighting isDefined) {
      raw""".linking-allowed { outline: 2px solid """ + s.allowed_highlighting.get.getRGBValue + """; }"""
    } else ""

  def unallowed(s: Style) =
    if (s.unallowed_highlighting isDefined) {
      raw""".linking-unallowed { solid : 2px solid """ + s.unallowed_highlighting.get.getRGBValue + """; }"""
    } else ""

  def footerDia: String = {
    raw"""
                default:
                  highlighting = '';
                break;
         }
         return highlighting;
       }
       """
  }

  def head(s: Style) =
    """
       function getStyle(stylename) {

        var style;

        switch(stylename) {
    """

  def body(s: Style) {
    raw"""
    /*
    This is a generated JavaScript file for the spray JointJS online editor.
    The """ + s.name + raw""" function will be called when the shapes are created, setting style attributes.
    """ + s.description.getOrElse("") + raw"""
    */
    case """ + s.name + raw""":
      style = {
      '.': { filter: Stencil.filter },

      """ + createFontAttributes(s) + raw"""

      """ + createRectangleAttributes(s) + raw"""

      """ + createRoundedRectangleAttributes(s) + raw"""

      """ + createCircleAttributes(s) + raw"""

      """ + createEllipseAttributes(s) + raw"""

      """ + createLineAttributes(s) + raw"""

      """ + createConnectionAttributes(s) + raw"""

      """ + createPolygonAttributes(s) + raw"""

      """ + createPolylineAttributes(s) + raw"""

      """ + createBoundingBoxStyle + raw"""
    };
    break;"""
  }

  def footer = """				default:
                 						style = {};
                 					break;

                 				}
                 				return style;
                 			}
               """

  def createFontAttributes(s: Style) = {
    //TODO s.name can never be null??
    raw"""
          /*
          Generated Text style attributes
          */
            text: {
              """ + fontAttributes(s) + raw"""
            },
    """
  }

  def fontAttributes(s: Style) = {
    raw"""
       'font-family': '""" + s.font_name.getOrElse("sans-serif") + """',
       'font-size': '""" + s.font_size.getOrElse("11px") + """',
       fill': '""" +
      {
        val c = s.font_color
        if (c.isDefined) c.get.getRGBValue else "#000000"
      } + """',
       'font-weight': '""" +
      {
        if (s.font_bold.getOrElse(false)) "700" else "400"
      } + """',
        """ + {
      if (s.font_italic.getOrElse(false))
        raw"""'font-style': 'italic',
            """
      else ""
    }
  }


  def createRectangleAttributes(s: Style) = {
    raw"""
          /*
          Generated rectangle style attributes.
          */
          rect: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }

  def createRoundedRectangleAttributes(s: Style) = {
    raw"""
          /*
          Generated rounded rectangle style attributes.
          */
          rect: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }


  def createCircleAttributes(s: Style) = {
    raw"""
          /*
          Generated circle style attributes.
          */
          circle: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }

  def createPolygonAttributes(s: Style) = {
    raw"""
          /*
          Generated polyline style attributes.
          */
          polygon: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }


  def createPolylineAttributes(s: Style) = {
    raw"""
          /*
          Generated polyline style attributes.
          */
          polyline: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }


  def createEllipseAttributes(s: Style) = {
    raw"""
          /*
          Generated ellipse style attributes.
          */
          ellipse: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }


  def createLineAttributes(s: Style):String = {
    raw"""
          /*
          Generated Line style attributes.
          */
          line: {
            """ + commonAttributes(s) + raw"""
          },
      """
  }


  def createConnectionAttributes(s: Style) = {
    raw"""
          /*
          Generated connection style attributes.
          */
          '.connection': {
            """ + commonAttributes(s) + raw"""
          },
      """
  }

  def createBoundingBoxStyle = raw"""/*
                                  Generated bounding box styles.
                                  */
                                  '.bounding-box':{
                                    'stroke-width': 0,
                                    fill: 'transparent'
                                  }"""

  def commonAttributes(s: Style):String = {
    var ret = raw""""""
    if (checkBackgroundGradientNecessary(s))
      ret += createGradientAttributes(s.background_color.get.asInstanceOf[GradientRef],
                                      s.gradient_orientation.get match { case HORIZONTAL => true
                                                                         case _ => false}
      ) + """

          """
    else
        ret+=
          createBackgroundAttributes(s)+ """

          """

   ret+=
     raw"""
       """+
     createLineAttributes(s)+
        raw"""

           """
    ret
  }


  def checkBackgroundGradientNecessary(s: Style) = if(!s.background_color.get.isInstanceOf[GradientRef]) false else true

  def createGradientAttributes(gr: GradientRef, horizontal: Boolean) = {
    val areas = for (area <- gr.area)yield{"{ offset: '"+area.offset+"', color: '"+area.color.getRGBValue+"' },"}
    var ret = """
      fill: {
        type: 'lineGradient',
        stops: [
    """
    for(area <- areas){ret += area}

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


  def createBackgroundAttributes(s: Style) = {
    if (s.background_color.isDefined) {
      val bg_color = s.background_color.get
      raw"""
          fill: '""" + bg_color.getRGBValue +raw"""',
          'fill.opacity': """ + bg_color.createOpacityValue+raw""",
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
      				stroke: '"""+s.line_color.get.getRGBValue+"""',"""
          if(s.line_width.get > 0)
            ret += """'stroke-width':"""+s.line_width.get+""","""
          if(s.line_style isDefined)
            s.line_style.get match {
              case DASH => ret +=
                           """
                                  'stroke-dasharray': "10,10",
                           """
              case DOT => ret +=
                """
                                  'stroke-dasharray': "5,5",
                """
              case DASHDOT => ret +=
                """
                                  'stroke-dasharray': "10,5,5,5",
                """
              case DASHDOTDOT => ret +=
                """
                                  'stroke-dasharray': "10,5,5,5,5,5",
                """
              case _ => ret +=
                """
                                  'stroke-dasharray': "0",
                """
            }
      }
    ret
  }

  def gradientOrientation(s:Style)=
    if(s.gradient_orientation isDefined)s.gradient_orientation.get match {
    case HORIZONTAL => true
    case _ => false
  }
  else false

  /*The rest of the methodes (refering to styleGEnerator.xtext from MoDiGenV2) is defined in the Color classes directly*/
}
