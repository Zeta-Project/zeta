package de.htwg.zeta.server.generator.generators.shape

import scala.collection.mutable

import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Ellipse
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.GeometricModel
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Line
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.PolyLine
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Polygon
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Rectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.style.HasStyle

object GeneratorInspectorDefinition {
  val attrCounterMap: mutable.HashMap[String, Int] = mutable.HashMap()

  /** generates the inspector definition, with input definitions for the JointJS Shapes
   *  @param attrs additional attributes
   *  @param nodes all nodes
   *  @param packageName mostly zeta
   *  @param shapes all shapes
   *  @return returns generated File as String
   */
  def generate(
    shapes: Iterable[Shape],
    packageName: String,
    attrs: mutable.HashMap[String, mutable.HashMap[GeometricModel, String]],
    nodes: List[Node]): String = {

    s"""
      |var InspectorDefs = {
      |  ${shapes.map(shape => generateDefinition(shape, packageName, attrs, nodes)).mkString}
      |  ${getLinkAttributes()}
      |};
      |""".stripMargin
  }

  private def generateDefinition(
    shape: Shape,
    packageName: String,
    attrs: mutable.HashMap[String, mutable.HashMap[GeometricModel, String]],
    nodes: List[Node]): String = {

    var boundWidth = 0
    var boundHeight = 0
    val node = nodes.find(n => n.shape.get.referencedShape.name == shape.name)

    boundWidth = GeneratorShapeDefinition.calculateWidth(shape)
    boundHeight = GeneratorShapeDefinition.calculateHeight(shape)

    val ret =
      if (attrs.keys.exists(_ == shape.name)) {
        val atts = attrs(shape.name)
        s"""
          |'$packageName.${shape.name}':{
          |  inputs: _.extend({
          |    attrs: {
          |      ${
          atts.keySet.map(k => {
            getRightAttributes(k, atts(k), boundWidth, boundHeight)
          }).mkString(",")
        }
          |    }
          |  }, CommonInspectorInputs),
          |  groups: CommonInspectorGroups
          |},
          |""".stripMargin
      } else {
        ""
      }
    attrCounterMap.clear
    ret
  }

  private def getRightAttributes(shape: GeometricModel, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    if (attrCounterMap.keySet.contains(shape.getClass.getSimpleName)) {
      attrCounterMap(shape.getClass.getSimpleName) = attrCounterMap(shape.getClass.getSimpleName) + 1
    } else {
      attrCounterMap(shape.getClass.getSimpleName) = 1
    }

    shape match {
      case e: Ellipse => getAttributes(e, shapeClass, maxWidth, maxHeight)
      case r: Rectangle => getAttributes(r, shapeClass, maxWidth, maxHeight)
      case t: Text => getAttributes(t, shapeClass, maxWidth, maxHeight)
      case l: Line => getAttributes(l, shapeClass, maxWidth, maxHeight)
      case p: Polygon => getAttributes(p, shapeClass, maxWidth, maxHeight)
      case pl: PolyLine => getAttributes(pl, shapeClass, maxWidth, maxHeight)
      case rr: RoundedRectangle => getAttributes(rr, shapeClass, maxWidth, maxHeight)
    }
  }

  private def getAttributes(shape: Rectangle, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | 'rect${if (hasStyle(shape)) "." + shapeClass}': inp({
      |  ${generateAttributesSpecific(shape)}
      | }),
      | '.$shapeClass': inp({
      |   ${generateAttributesGeneral(shape, maxWidth, maxHeight)}
      | })
    """.stripMargin
  }

  private def generateAttributesSpecific(shape: Rectangle): String = {
    s"""
      | fill: {
      |   group: 'Presentation Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1,
      |   label: 'Background-Color Rectangle'
      | },
      | 'fill-opacity': {
      |   group: 'Presentation Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   label: 'Opacity Rectangle'
      | },
      | stroke: {
      |   group: 'Presentation Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   label: 'Line-Color Rectangle'
      | },
      | 'stroke-width': {
      |   group: 'Presentation Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 4,
      |   min: 0,
      |   max: 30,
      |   defaultValue: 1
      | },
      | 'stroke-dasharray': {
      |   group: 'Presentation Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 5,
      |   label: 'Stroke Dash Rectangle'
      | }
    """.stripMargin
  }

  private def generateAttributesGeneral(shape: Rectangle, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | x: {
      |   group: 'Geometry Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1,
      |   max: ${maxWidth - shape.size_width},
      |   label: 'x Position Rectangle'
      | },
      | y: {
      |   group: 'Geometry Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   max: ${maxHeight - shape.size_height},
      |   label: 'y Position Rectangle'
      | },
      | height: {
      |   group: 'Geometry Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: $maxHeight,
      |   label: 'Height Rectangle'
      | },
      | width: {
      |   group: 'Geometry Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: $maxWidth,
      |   label: 'Width Rectangle'
      | }
      |""".stripMargin
  }

  private def getAttributes(shape: Text, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      |'text${if (hasStyle(shape)) "." + shapeClass}' : inp({
      |  text: {
      |    type: 'list',
      |    item: {
      |      type: 'text'
      |    },
      |    group: 'Text ${shape.id}',
      |    index: 1
      |
      |  },
      |  x: {
      |    group: 'Text Geometry ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 1,
      |    max: ${maxWidth - shape.size_width},
      |    label: 'x Position Text'
      |  },
      |  y: {
      |    group: 'Text Geometry ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 2,
      |    max: ${maxHeight - shape.size_height},
      |    label: 'y Position Text'
      |  }
      |}),
      |'.$shapeClass': inp({
      |  'font-size': {
      |    group: 'Text Style ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 2
      |  },
      |  'font-family': {
      |    group: 'Text Style ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 3
      |  },
      |  'font-weight': {
      |    group: 'Text Style ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 4
      |  },
      |  fill: {
      |    group: 'Text Style ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 6,
      |    label: 'Text Color'
      |  }
      |})
      |""".stripMargin
  }

  private def getAttributes(shape: Line, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      |'line${if (hasStyle(shape)) "." + shapeClass}': inp({
      |  stroke: {
      |    group: 'Presentation Line ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 2,
      |    label: 'Line-Color'
      |  },
      |  'stroke-width': {
      |    group: 'Presentation Line ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 3,
      |    min: 0,
      |    max: 30,
      |    defaultValue: 1,
      |    label: ' Stroke Width Line'
      |  },
      |  'stroke-dasharray': {
      |    group: 'Presentation ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 4,
      |    label: 'Stroke Dash Line'
      |  }
      |})
      |""".stripMargin
  }

  private def getAttributes(shape: Ellipse, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | 'ellipse${if (hasStyle(shape)) "." + shapeClass}': inp({
      |   ${generateAttributesSpecific(shape)}
      | }),
      | '.$shapeClass': inp({
      |   ${generateAttributesGeneral(shape, maxWidth, maxHeight)}
      | })
      |""".stripMargin
  }

  private def generateAttributesSpecific(shape: Ellipse): String = {
    s"""
      | fill: {
      |   group: 'Presentation Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1,
      |   label:'Background-Color Ellipse'
      | },
      |   'fill-opacity': {
      |   group: 'Presentation Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   label: 'Opacity Ellipse'
      | },
      | stroke: {
      |   group: 'Presentation Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   label: 'Line-Color Ellipse'
      | },
      | 'stroke-width': {
      |   group: 'Presentation Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 4,
      |   min: 0,
      |   max: 30,
      |   defaultValue: 1,
      |   label: 'Stroke Width Ellipse'
      | },
      | 'stroke-dasharray': {
      |   group: 'Presentation Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 5,
      |   label: 'Stroke Dash Ellipse'
      | }
      |""".stripMargin
  }

  private def generateAttributesGeneral(shape: Ellipse, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | cx: {
      |   group: 'Geometry Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1,
      |   min: ${shape.size_width / 2},
      |   max: ${maxWidth - shape.size_width / 2}
      | },
      | cy: {
      |   group: 'Geometry Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   min: ${shape.size_height / 2},
      |   max: ${maxHeight - shape.size_height / 2}
      | },
      | rx: {
      |   group: 'Geometry Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: ${maxWidth / 2}
      | },
      | ry: {
      |   group: 'Geometry Ellipse ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: ${maxHeight / 2}
      | }
      |""".stripMargin
  }


  private def getAttributes(shape: Polygon, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      |'polygon${if (hasStyle(shape)) "." + shapeClass}' : inp({
      |  fill: {
      |    group: 'Presentation Polygon ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 1,
      |    label:'Background-Color Polygon'
      |  },
      |  'fill-opacity': {
      |    group: 'Presentation Polygon ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 2,
      |    label: 'Opacity Polygon'
      |  },
      |  stroke: {
      |    group: 'Presentation Polygon ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 3,
      |    label: 'Line-Color Polygon'
      |  },
      |  'stroke-width': {
      |    group: 'Presentation Polygon ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 4,
      |    min: 0,
      |    max: 30,
      |    defaultValue: 1,
      |    label: 'Stroke Width Polygon'
      |  },
      |  'stroke-dasharray': {
      |    group: 'Presentation Polygon ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 5,
      |    label: 'Stroke Dash Polygon'
      |  }
      |})
      |""".stripMargin
  }

  private def getAttributes(shape: PolyLine, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      |'polyline${if (hasStyle(shape)) "." + shapeClass}' : inp({
      |  fill: {
      |    group: 'Presentation Polyline ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 1,
      |    label: 'Background-Color Polyline'
      |  },
      |  stroke: {
      |    group: 'Presentation Polyline ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 2,
      |    label: 'Line-Color'
      |  },
      |  'stroke-width': {
      |    group: 'Presentation Polyline ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 3,
      |    min: 0,
      |    max: 30,
      |    defaultValue: 1,
      |    label: ' Stroke Width Line'
      |  },
      |  'stroke-dasharray': {
      |    group: 'Presentation Polyline ${attrCounterMap(shape.getClass.getSimpleName)}',
      |    index: 4,
      |    label: 'Stroke Dash Line'
      |  }
      |})
      |""".stripMargin
  }

  private def getAttributes(shape: RoundedRectangle, shapeClass: String, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | 'rect${if (hasStyle(shape)) "." + shapeClass}' : inp({
      |   ${generateAttributesSpecific(shape)}
      | }),
      | '.$shapeClass': inp({
      |   ${generateAttributesGeneral(shape, maxWidth, maxHeight)}
      | })
      |""".stripMargin
  }

  private def generateAttributesSpecific(shape: RoundedRectangle): String = {
    s"""
      | fill: {
      |   group: 'Presentation R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1
      | },
      | 'fill-opacity': {
      |   group: 'Presentation R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   label: 'Opacity Rounded Rectangle'
      | },
      | stroke: {
      |   group: 'Presentation R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   label: 'Line-Color Rounded Rectangle'
      | },
      | 'stroke-width': {
      |   group: 'Presentation R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 4,
      |   min: 0,
      |   max: 30,
      |   defaultValue: 1,
      |   label: 'Stroke Width Rounded Rectangle'
      | },
      | 'stroke-dasharray': {
      |   group: 'Presentation R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 5,
      |   label: 'Stroke Dash Rounded Rectangle'
      | },
      |""".stripMargin
  }

  private def generateAttributesGeneral(shape: RoundedRectangle, maxWidth: Int, maxHeight: Int): String = {
    s"""
      | rx: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 6,
      |   max: ${shape.size_width / 2},
      |   label: 'Curve X'
      | },
      | ry: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 7,
      |   max: ${shape.size_height / 2},
      |   label: 'Curve Y'
      | },
      | x: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 1,
      |   max: ${maxWidth - shape.size_width},
      |   label: 'x Position Rounded Rectangle'
      | },
      | y: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 2,
      |   max: ${maxHeight - shape.size_height},
      |   label: 'y Position Rounded Rectangle'
      | },
      | height: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: $maxHeight,
      |   label: 'Height Rounded Rectangle'
      | },
      | width: {
      |   group: 'Geometry R-Rectangle ${attrCounterMap(shape.getClass.getSimpleName)}',
      |   index: 3,
      |   max: $maxWidth,
      |   label: 'Width Rounded Rectangle'
      | }
      |""".stripMargin
  }

  private def getLinkAttributes(): String = {
    s"""
      |'zeta.MLink': {
      |  inputs: {
      |    labels: {
      |      type: 'list',
      |      group: 'labels',
      |      attrs: {
      |        label: {
      |          'data-tooltip': 'Set (possibly multiple) labels for the link'
      |        }
      |      },
      |      item: {
      |        ${generateItem()}
      |      }
      |    }
      |  },
      |  groups: {
      |    labels: {
      |      label: 'Labels',
      |      index: 1
      |    }
      |  }
      |}
      |""".stripMargin
  }

  private def generateItem(): String = {
    s"""
      | type: 'object',
      | properties: {
      |   position: {
      |     type: 'range',
      |     min: 0.1,
      |     max: .9,
      |     step: .1,
      |     defaultValue: .5,
      |     label: 'position',
      |     index: 2,
      |     attrs: {
      |       label: {
      |         'data-tooltip': 'Position the label relative to the source of the link'
      |       }
      |     }
      |   },
      |   attrs: {
      |     text: {
      |       text: {
      |         type: 'text',
      |         label: 'text',
      |         defaultValue: 'label',
      |         index: 1,
      |         attrs: {
      |           label: {
      |             'data-tooltip': 'Set text of the label'
      |           }
      |         }
      |       }
      |     }
      |   }
      | }
      |""".stripMargin
  }

  private def hasStyle(shape: GeometricModel): Boolean = {
    shape match {
      case s: HasStyle if s.style.isDefined => true
      case _ => false
    }
  }
}
