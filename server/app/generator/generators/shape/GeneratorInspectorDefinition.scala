package generator.generators.shape

/**
 * Created by julian on 19.01.16.
 * shape inspector generator
 */

import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.HasStyle

import scala.collection.mutable
object GeneratorInspectorDefinition {

  def generate(shape: ShapeContainerElement, packageName: String, lastElement: Boolean, attrs: mutable.HashMap[String, mutable.HashMap[GeometricModel, String]]) = {
    val atts = attrs(shape.name)
    var boundWidth = 0
    var boundHeight = 0

    if (shape.isInstanceOf[Shape]) {
      //TODOboundWidth = ShapeGenerator.calculateWidth(shape)
      //TODOboundHeight = ShapeGenerator.calculateHeight(shape)
    }

    if (atts != null) {
      val attsize = atts.size
      var counter = 1
      """
      '""" + packageName + "." + shape.name + """':{
        inputs: _.extend({
          attrs: {
            """+atts.keySet.map { k => {if (attsize != counter){
        getRightAttributes(k, atts(k), last = false, boundWidth, boundHeight)
            }else{
              getRightAttributes(k, atts(k), last = true, boundWidth, boundHeight)
            }} + """
                """+{
        counter= counter+1;""
      }
          }.
        mkString +
        """
          }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
      },
      """
    }
  }

  def head()={

    """
    var InspectorDefs = {
      // FSA
      // ---
      """
    }

    def
    footer()={
      """
      «linkAttributes»
    };
    """
  }

  private def getRightAttributes(shape:GeometricModel, shapeClass:String, last:Boolean, maxWidth:Int, maxHeight:Int)= shape match {
    case r:Rectangle => getAttributes(r, shapeClass, last, maxWidth, maxHeight)
    case t:Text => getAttributes(t, shapeClass, last, maxWidth, maxHeight)
    case t:Text => getAttributes(t, shapeClass, last, maxWidth, maxHeight)
    case l:Line => getAttributes(l, shapeClass, last, maxWidth, maxHeight)
    case e:Ellipse => getAttributes(e, shapeClass, last, maxWidth, maxHeight)
    case p:Polygon => getAttributes(p, shapeClass, last, maxWidth, maxHeight)
    case pl:PolyLine => getAttributes(pl, shapeClass, last, maxWidth, maxHeight)
    case rr:RoundedRectangle => getAttributes(rr, shapeClass, last, maxWidth, maxHeight)
  }

  def getAttributes(shape: Rectangle ,shapeClass:String, last: Boolean, maxWidth: Int, maxHeight:Int)={ """
    'rect"""+{if(hasStyle(shape)) "."+ shapeClass}+ """': inp({
      fill: { group: 'Presentation Rectangle', index: 1, label: 'Background-Color Rectangle' },
      'fill-opacity': {group: 'Presentation Rectangle', index: 2, label: 'Opacity Rectangle'},
      stroke: {group: 'Presentation Rectangle', index: 3, label: 'Line-Color Rectangle'},
      'stroke-width': { group: 'Presentation Rectangle', index: 4, min: 0, max: 30, defaultValue: 1 },
      'stroke-dasharray': { group: 'Presentation Rectangle', index: 5, label: 'Stroke Dash Rectangle' }
    }),
    '."""+ shapeClass+ """': inp({
      x: {group: 'Geometry Rectangle', index: 1, max:"""+(maxWidth - shape.size_width)+ """, label: 'x Position Rectangle'},
      y: {group: 'Geometry Rectangle', index: 2, max:"""+(maxHeight - shape.size_height)+ """, label: 'y Position Rectangle'},
      height: {group: 'Geometry Rectangle', index: 3, max:"""+ maxHeight+ """, label: 'Height Rectangle'},
      width: {group: 'Geometry Rectangle', index: 3, max:"""+maxWidth + """, label: 'Width Rectangle'}
    })"""+{if(!last) "," else ""}

  }

  def
  getAttributes( shape:Text, shapeClass:String, last: Boolean, maxWidth:Int, maxHeight:Int)={
    """
    '.""" + shape.textBody+ """': inp({text: { group: 'text', index: 1 }}),
    'text"""+{if(hasStyle(shape)) "."+shapeClass}+ """' : inp({
      'font-size': { group: 'text', index: 2 },
      'font-family': { group: 'text', index: 3 },
      'font-weight': { group: 'text', index: 4 },
      fill: { group: 'text', index: 6, label:'Text Color' },
    }),
    '."""+shapeClass+ """': inp({
      x: {group: 'Text Geometry', index: 1, max:"""+(maxWidth - shape.size_width) + """, label: 'x Position Text'},
      y: {group: 'Text Geometry', index: 2, max:"""+(maxHeight - shape.size_height)+ """, label: 'y Position Text'}
    })«IF !last»,«ENDIF»
    """
  }

  def getAttributes( shape:Line,
                     shapeClass:String, last:Boolean, maxWidth:Int, maxHeight:Int)= { """
    'line""" + {
    if (hasStyle(shape)) "." + shapeClass + """' : inp({
      stroke: { group: 'Presentation', index: 2, label: 'Line-Color' },
      'stroke-width': { group: 'Presentation', index: 3, min: 0, max: 30, defaultValue: 1, label: ' Stroke Width Line' },
      'stroke-dasharray': { group: 'Presentation', index: 4, label: 'Stroke Dash Line' }
    })""" + { if (!last) "," else "" }
    }
  }

  def getAttributes( shape:Ellipse, shapeClass: String, last: Boolean , maxWidth:Int, maxHeight:Int)={
    """
    'ellipse"""+ {if(hasStyle(shape))"."+ shapeClass} +raw"""' : inp({
      fill: { group: 'Presentation', index: 1, label:'Background-Color Ellipse' },
      'fill-opacity': {group: 'Presentation', index: 2, label: 'Opacity Ellipse'},
      stroke: {group: 'Presentation', index: 3, label: 'Line-Color Rectangle'},
      'stroke-width': { group: 'Presentation', index: 4, min: 0, max: 30, defaultValue: 1, label: 'Stroke Width Ellipse' },
      'stroke-dasharray': { group: 'Presentation', index: 5, label: 'Stroke Dash Ellipse' }
    }),
    '."""+shapeClass+""": inp({
      cx: {group: 'Geometry Ellipse', index: 1, min: """+(shape.size_width/ 2)+ """, max:"""+(maxWidth - shape.size_width/2)+ """},
      cy: {group: 'Geometry Ellipse', index: 2, min:"""+shape.size_height/2+""", max:"""+(maxHeight - shape.size_height/2)+ """},
      rx: {group: 'Geometry Ellipse', index: 3, max:"""+ (maxWidth/2)+ """},
      ry: {group: 'Geometry Ellipse', index: 3, max:"""+(maxHeight/2)+"""}
    })"""+{if(!last) "," else ""}
  }

  def getAttributes(shape:Polygon, shapeClass: String, last: Boolean, maxWidth: Int, maxHeight: Int)
  ={
    """
    'polygon"""+{if ( hasStyle(shape))"."+shapeClass}+ """' : inp({
      fill: { group: 'Presentation', index: 1, label:'Background-Color Polygon' },
      'fill-opacity': {group: 'Presentation', index: 2, label: 'Opacity Polygon'},
      stroke: {group: 'Presentation', index: 3, label: 'Line-Color Rectangle'},
      'stroke-width': { group: 'Presentation', index: 4, min: 0, max: 30, defaultValue: 1,label: 'Stroke Width Polygon' },
      'stroke-dasharray': { group: 'Presentation', index: 5, label: 'Stroke Dash Polygon' }
    })"""+{if(!last)"," else ""}
  }

  def getAttributes( shape:PolyLine,  shapeClass:String, last: Boolean, maxWidth:Int, maxHeight:Int)= { """
    'polyline"""+{ if(hasStyle(shape))"."+ shapeClass}+
    """' : inp({
      fill: { group: 'Presentation', index: 1, label:'Background-Color Polyline' },
    })"""+{ if ( !last)"," else ""}
  }

  def getAttributes(shape: RoundedRectangle, shapeClass: String, last: Boolean, maxWidth:Int, maxHeight:Int)={
    """
    'rect"""+{if(hasStyle(shape)) "."+ shapeClass}+ """' : inp({
      fill: { group: 'Presentation Rounded Rectangle', index: 1 },
      'fill-opacity': {group: 'Presentation Rounded Rectangle', index: 2, label: 'Opacity Rounded Rectangle'},
      stroke: {group: 'Presentation Rounded Rectangle', index: 3, label: 'Line-Color Rectangle'},
      'stroke-width': { group: 'Presentation Rounded Rectangle', index: 4, min: 0, max: 30, defaultValue: 1, label: 'Stroke Width Rounded Rectangle' },
      'stroke-dasharray': { group: 'Presentation Rounded Rectangle', index: 5, label: 'Stroke Dash Rounded Rectangle' },
    }),
    '."""+shapeClass+""": inp({
      rx: { group: 'Geometry Rounded Rectangle', index: 6, max:"""+(shape.size_width/2)+ """, label: 'Curve X' },
      ry: { group: 'Geometry Rounded Rectangle', index: 7, max:"""+(shape.size_height/2)+ """, label: 'Curve Y' },
      x: {group: 'Geometry Rounded Rectangle', index: 1, max:""" + (maxWidth - shape.size_width)+ """, label: 'x Position Rounded Rectangle'},
      y: {group: 'Geometry Rounded Rectangle', index: 2, max:"""+(maxHeight - shape.size_height)+ """, label: 'y Position Rounded Rectangle'},
      height: {group: 'Geometry Rounded Rectangle', index: 3, max:"""+maxHeight+ """, label: 'Height Rounded Rectangle'},
      width: {group: 'Geometry Rounded Rectangle', index: 3, max:"""+maxWidth+""", label: 'Width Rounded Rectangle'}
    })"""+{if(!last)"," else ""}
  }

  def getLinkAttributes =
    """
    'modigen.MLink':{
      inputs:{
        labels:{
        type: 'list',
        group: 'labels',
        attrs:{
        label: {'data-tooltip': 'Set (possibly multiple) labels for the link'}
      },
        item:{
        type: 'object',
        properties: {
        position: { type: 'range', min: 0.1, max: .9, step: .1, defaultValue: .5, label: 'position', index: 2, attrs: { label: { 'data-tooltip': 'Position the label relative to the source of the link' } } },
        attrs: {
        text: {
        text: { type: 'text', label: 'text', defaultValue: 'label', index: 1, attrs: { label: { 'data-tooltip': 'Set text of the label' } } }
      }
      }
      }
      }
      }
      },
      groups:{
        labels: {label: 'Labels', index: 1}
      }
    }
    """

  def hasStyle(shape:GeometricModel) = shape match {
    case s:HasStyle if s.style.isDefined => true
    case _ => false
  }
    /* FOLLOWING METHODS ARE NOT NEEDED ANYMORE - INLINESTYLE AND NORMAL STYLE ARE MERGED AUTOMATICALLY AT PARSING
  protected def hasShapeOrInlineStyle(shape:Shape)={
    shape.style != null && shape.style.dslStyle != null || shape.hasInlineStyle
  }

  protected def dispatch hasInlineStyle(Line shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(Polyline shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(Polygon shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(Rectangle shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(RoundedRectangle shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(Ellipse shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }

  protected def dispatch hasInlineStyle(Text shape){
    return shape.layout !=null && shape.layout.layout != null && shape.layout.layout.layout != null
  }
  */
}
