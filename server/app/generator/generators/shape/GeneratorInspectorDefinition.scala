package generator.generators.shape

/**
 * Created by julian on 19.01.16.
 * shape inspector generator
 */

import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.HasStyle
import generator.model.diagram.node.Node
import models.modelDefinitions.metaModel.elements.{MAttribute, ScalarType, ScalarValue}

import scala.collection.mutable.HashMap

object GeneratorInspectorDefinition {

  def generate(shape: ShapeContainerElement, packageName: String, lastElement: Boolean, attrs: HashMap[String, HashMap[GeometricModel, String]], node: Option[Node]):String = {
    var boundWidth = 0
    var boundHeight = 0
    var ret = ""

    if (shape.isInstanceOf[Shape]) {
      boundWidth = GeneratorShapeDefinition.calculateWidth(shape.asInstanceOf[Shape])
      boundHeight = GeneratorShapeDefinition.calculateHeight(shape.asInstanceOf[Shape])
    }

    if (attrs.keys.exists(_ == shape.name)) {
      val atts = attrs(shape.name)
      val attsize = atts.size
      var counter = 1
      ret =
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
        s"""
          },${if(node.isDefined) generateMClassAttributeInputs(node.get) else ""}
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
      },
      """
    }
    ret
  }

  def head =
    """
    var InspectorDefs = {
      // FSA
      // ---
      """

  def footer =
    s"""
    $getLinkAttributes
    };
    """

  private def getRightAttributes(shape:GeometricModel, shapeClass:String, last:Boolean, maxWidth:Int, maxHeight:Int)= shape match {
    case e:Ellipse => getAttributes(e, shapeClass, last, maxWidth, maxHeight)
    case r:Rectangle => getAttributes(r, shapeClass, last, maxWidth, maxHeight)
    case t:Text => getAttributes(t, shapeClass, last, maxWidth, maxHeight)
    case l:Line => getAttributes(l, shapeClass, last, maxWidth, maxHeight)
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

  def getAttributes( shape:Text, shapeClass:String, last: Boolean, maxWidth:Int, maxHeight:Int)={
    """
    'text"""+{if(hasStyle(shape)) "."+shapeClass}+ """' : inp({
      text: { group: 'Text', index: 1 },
      x: {group: 'Text Geometry', index: 1, max:""" + (maxWidth - shape.size_width) + """, label: 'x Position Text'},
      y: {group: 'Text Geometry', index: 2, max:""" + (maxHeight - shape.size_height) +""", label: 'y Position Text'}
    }),
    '."""+shapeClass+ """': inp({
      'font-size': { group: 'Text Style', index: 2 },
      'font-family': { group: 'Text Style', index: 3 },
      'font-weight': { group: 'Text Style', index: 4 },
      fill: { group: 'Text Style', index: 6, label:'Text Color' },
    }),
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
      fill: { group: 'Presentation Ellipse', index: 1, label:'Background-Color Ellipse' },
      'fill-opacity': {group: 'Presentation Ellipse', index: 2, label: 'Opacity Ellipse'},
      stroke: {group: 'Presentation Ellipse', index: 3, label: 'Line-Color Ellipse'},
      'stroke-width': { group: 'Presentation Ellipse', index: 4, min: 0, max: 30, defaultValue: 1, label: 'Stroke Width Ellipse' },
      'stroke-dasharray': { group: 'Presentation Ellipse', index: 5, label: 'Stroke Dash Ellipse' }
    }),
    '."""+shapeClass+"""': inp({
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
      fill: { group: 'Presentation Polygon', index: 1, label:'Background-Color Polygon' },
      'fill-opacity': {group: 'Presentation Polygon', index: 2, label: 'Opacity Polygon'},
      stroke: {group: 'Presentation Polygon', index: 3, label: 'Line-Color Polygon'},
      'stroke-width': { group: 'Presentation Polygon', index: 4, min: 0, max: 30, defaultValue: 1,label: 'Stroke Width Polygon' },
      'stroke-dasharray': { group: 'Presentation Polygon', index: 5, label: 'Stroke Dash Polygon' }
    })"""+{if(!last)"," else ""}
  }

  def getAttributes( shape:PolyLine,  shapeClass:String, last: Boolean, maxWidth:Int, maxHeight:Int)= { """
    'polyline"""+{ if(hasStyle(shape))"."+ shapeClass}+
    """' : inp({
      fill: { group: 'Presentation Polyline', index: 1, label:'Background-Color Polyline' },
    })"""+{ if ( !last)"," else ""}
  }

  def getAttributes(shape: RoundedRectangle, shapeClass: String, last: Boolean, maxWidth:Int, maxHeight:Int)={
    """
    'rect"""+{if(hasStyle(shape)) "."+ shapeClass}+ """' : inp({
      fill: { group: 'Presentation R-Rectangle', index: 1 },
      'fill-opacity': {group: 'Presentation R-Rectangle', index: 2, label: 'Opacity Rounded Rectangle'},
      stroke: {group: 'Presentation R-Rectangle', index: 3, label: 'Line-Color Rounded Rectangle'},
      'stroke-width': { group: 'Presentation R-Rectangle', index: 4, min: 0, max: 30, defaultValue: 1, label: 'Stroke Width Rounded Rectangle' },
      'stroke-dasharray': { group: 'Presentation R-Rectangle', index: 5, label: 'Stroke Dash Rounded Rectangle' },
    }),
    '."""+shapeClass+"""': inp({
      rx: { group: 'Geometry R-Rectangle', index: 6, max:"""+(shape.size_width/2)+ """, label: 'Curve X' },
      ry: { group: 'Geometry R-Rectangle', index: 7, max:"""+(shape.size_height/2)+ """, label: 'Curve Y' },
      x: {group: 'Geometry R-Rectangle', index: 1, max:""" + (maxWidth - shape.size_width)+ """, label: 'x Position Rounded Rectangle'},
      y: {group: 'Geometry R-Rectangle', index: 2, max:"""+(maxHeight - shape.size_height)+ """, label: 'y Position Rounded Rectangle'},
      height: {group: 'Geometry R-Rectangle', index: 3, max:"""+maxHeight+ """, label: 'Height Rounded Rectangle'},
      width: {group: 'Geometry R-Rectangle', index: 3, max:"""+maxWidth+""", label: 'Width Rounded Rectangle'}
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
  def generateMClassAttributeInputs(node: Node)= {
    val attributes = node.mcoreElement.attributes
    val ret = for((attr,i) <- attributes.zipWithIndex) yield
      attr.`type` match {
        case ScalarType.String => generateStringAttribute(attr, i)
        case ScalarType.Bool => generateBooleanAttribute(attr, i)
        case ScalarType.Int =>
        case ScalarType.Double =>
        case _ =>
      }

    ret.mkString(",")
  }

  def generateStringAttribute(attr: MAttribute, i: Int) = {
    s"""
        '${attr.name}': {
          type: 'text',
          defaultValue: '${attr.default.asInstanceOf[ScalarValue.MString].value}',
          group: 'data',
          index: $i
        }
     """
  }

  def generateBooleanAttribute(attr: MAttribute, i: Int) = {
    s"""
       '${attr.name}': {
          type: 'toggle',
          defaultValue: ${attr.default.asInstanceOf[ScalarValue.MBool].value},
          group: 'data',
          index: $i
       }
     """
  }

}
