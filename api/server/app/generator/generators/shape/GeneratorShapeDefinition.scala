package generator.generators.shape

import java.util.UUID

import scala.collection.mutable
import scala.collection.AbstractSeq
import scala.collection.mutable.ListBuffer

import generator.model.shapecontainer.shape.Compartment
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.Ellipse
import generator.model.shapecontainer.shape.geometrics.Line
import generator.model.shapecontainer.shape.geometrics.Point
import generator.model.shapecontainer.shape.geometrics.PolyLine
import generator.model.shapecontainer.shape.geometrics.Polygon
import generator.model.shapecontainer.shape.geometrics.Rectangle
import generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.shapecontainer.shape.geometrics.GeometricModel
import java.io

/**
 * Generates the output String for shape.js
 */
object GeneratorShapeDefinition {
  val attrs: mutable.HashMap[String, mutable.MutableList[String]] = mutable.HashMap()
  val attrsInspector: mutable.HashMap[String, mutable.HashMap[GeometricModel, String]] = mutable.HashMap()
  private val compartmentMap: mutable.HashMap[String, ListBuffer[(String, Compartment)]] = mutable.HashMap()
  private val stencilSize: Int = 80

  /**
   * generates all JointJS shapes
   */
  def generate(shapes: Iterable[Shape], packageName: String): String = {
    s"""
      |${head(packageName)}
      |${shapes.map(shape => generateShape(shape, packageName)).mkString("")}
      |""".stripMargin
  }

  def head(packageName: String): String = {
    raw"""
      |/*
      | * This is a generated ShapeFile for JointJS
      | */
      |
      |if (typeof exports === 'object') {
      |
      |  var joint = {
      |    util: require('../src/core').util,
      |    shapes: {
      |      basic: require('./joint.shapes.basic')
      |    },
      |    dia: {
      |      ElementView: require('../src/joint.dia.element').ElementView,
      |      Link: require('../src/joint.dia.link').Link
      |    }
      |  };
      |}
      |
      |joint.shapes.$packageName = {};
      |
      |""".stripMargin
  }

  /**
   * generates a JointJS shape from Shape
   */
  def generateShape(shape: Shape, packageName: String): String = {
    s"""
      |joint.shapes.$packageName.${shape.name} = joint.shapes.basic.Generic.extend({
      |  markup: ${generateSvgMarkup(shape)},
      |    defaults: joint.util.deepSupplement({
      |    type: '$packageName.${shape.name}',
      |    'init-size': {
      |      width: ${calculateWidth(shape)},
      |      height: ${calculateHeight(shape)}},
      |    size: {${getStencilSize(shape)}},
      |    ${generateSizeProperties(shape)}
      |    resize:{${getResizingPolicies(shape)}},
      |    attrs: {
      |      'rect.bounding-box':{
      |        height: ${calculateHeight(shape)},
      |        width: ${calculateWidth(shape)}
      |      },
      |    ${generateAttrs(shape.name)}
      |    },
      |    compartments: [${generateCompartmentProperties(shape.name).mkString(",")}]
      |  }, joint.dia.Element.prototype.defaults)
      |});
      |""".stripMargin
  }

  /**
   * calculates the size of the Shape in the stencil, while keeping proportions
   */
  protected def getStencilSize(shape: Shape): String = {
    val height = calculateHeight(shape).toDouble
    val width = calculateWidth(shape).toDouble
    var newHeight = 0.0
    var newWidth = 0.0
    if (height <= stencilSize && width <= stencilSize) {
      newHeight = height
      newWidth = width
    } else if (height > width) {
      newHeight = stencilSize
      newWidth = width / (height / stencilSize)
    } else {
      newWidth = stencilSize
      newHeight = height / (width / stencilSize)
    }
    s"""
      |width: ${newWidth.toInt}, height: ${newHeight.toInt}
      |""".stripMargin
  }

  protected def getResizingPolicies(shape: Shape): String = {
    s"""
      |horizontal: ${shape.stretchingHorizontal.getOrElse("true")},
      |vertical: ${shape.stretchingVertical.getOrElse("true")},
      |proportional: ${shape.proportional.getOrElse("true")}
      |""".stripMargin
  }

  def generateSizeProperties(shape: Shape): String = {
    s"""
      |${
      if (shape.sizeHeightMax.isDefined && shape.sizeWidthMax.isDefined) {
        s"""'size-max': {height: ${shape.sizeHeightMax.get}, width: ${shape.sizeWidthMax.get}},"""
      } else {
        ""
      }
    }
      |${
      if (shape.sizeHeightMin.isDefined && shape.sizeWidthMin.isDefined) {
        s"""'size-min': {height: ${shape.sizeHeightMin.get}, width: ${shape.sizeWidthMin.get}},"""
      } else {
        ""
      }
    }
      |""".stripMargin
  }

  /** generates the markup of the JointJS shape */
  protected def generateSvgMarkup(shape: Shape): String = {
    """ '<g class="rotatable"><g class="scalable"><rect class="bounding-box" />""" + {
      for {s <- shape.shapes.getOrElse(List())} yield {
        generateSvgShape(s, shape.name, "scalable")
      }
    }.mkString("") + "</g></g>'"
  }

  private def generateSvgShape(g: GeometricModel, shapeName: String, parentClass: String): String = {
    g match {
      case l: Line => generateSvgShape(l, shapeName, parentClass)
      case e: Ellipse => generateSvgShape(e, shapeName, parentClass)
      case r: Rectangle => generateSvgShape(r, shapeName, parentClass)
      case p: Polygon => generateSvgShape(p, shapeName, parentClass)
      case p: PolyLine => generateSvgShape(p, shapeName, parentClass)
      case r: RoundedRectangle => generateSvgShape(r, shapeName, parentClass)
      case t: Text => generateSvgShape(t, shapeName, parentClass)
    }
  }

  protected def generateSvgShape(shape: Line, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<line class="$className" />"""
  }

  protected def generateSvgShape(shape: Rectangle, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<rect class="$className" />""" + shape.children.map { subShape => generateSvgShape(subShape, rootShapeName, className.toString) }.mkString
  }

  protected def generateSvgShape(shape: Ellipse, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<ellipse class="$className" />""" + shape.children.map { subShape => generateSvgShape(subShape, rootShapeName, className.toString) }.mkString
  }

  protected def generateSvgShape(shape: Polygon, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<polygon class="$className" />""" + shape.children.map { subShape => generateSvgShape(subShape, rootShapeName, className.toString) }.mkString
  }

  protected def generateSvgShape(shape: PolyLine, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<polyline class="$className" />"""
  }

  protected def generateSvgShape(shape: RoundedRectangle, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<rect class="$className" />""" + shape.children.map { subShape => generateSvgShape(subShape, rootShapeName, className.toString) }.mkString
  }

  protected def generateSvgShape(shape: Text, rootShapeName: String, parentClass: String): String = {
    val className = UUID.randomUUID
    buildAttrs(shape, rootShapeName, className.toString, parentClass)
    s"""<text class="$className ${shape.id}" > </text>"""
  }

  protected def generateAttrs(shapeName: String): String = {
    val classes = attrs.get(shapeName)
    if (classes.isEmpty) {
      ""
    } else {
      val text = classes.get.map { c =>
        c + {
          if (c != classes.get.last) {
            ","
          } else {
            ""
          }
        }
      }.mkString
      attrs.clear()
      text
    }
  }

  protected def buildAttrs(shape: GeometricModel, shapeName: String, className: String, parentClass: String): Option[io.Serializable] = {
    buildCompartmentClassMap(shapeName, shape, className)
    val attributes = {
      val text = shape match {
        case _: Text => "text"
        case _ => ""
      }

      s"""
        |'${text}.${className}':{
        |${getAttributes(shape, parentClass)}
        |}
        |""".stripMargin
    }

    if (attrs.contains(shapeName)) {
      attrs(shapeName) += attributes
      attrsInspector(shapeName).put(shape, className)
    } else {
      val list = mutable.MutableList[String]()
      list += attributes

      val att = new mutable.HashMap[GeometricModel, String]
      att.put(shape, className)
      attrs.put(shapeName, list)
      attrsInspector.put(shapeName, att)
    }

  }

  protected def buildCompartmentClassMap(shapeName: String, shape: GeometricModel, className: String): Any = {
    shape match {
      case e: Ellipse => if (e.compartment.isDefined) addToCompartmentMap(shapeName, (className, e.compartment.get))
      case r: Rectangle => if (r.compartment.isDefined) addToCompartmentMap(shapeName, (className, r.compartment.get))
      case _ =>
    }
  }

  protected def addToCompartmentMap(shapeName: String, tuple: (String, Compartment)): Any = {
    if (compartmentMap.keySet.contains(shapeName)) {
      compartmentMap(shapeName) += tuple
    } else {
      compartmentMap(shapeName) = mutable.ListBuffer[(String, Compartment)](tuple)
    }
  }

  protected def generateCompartmentProperties(shapeName: String): AbstractSeq[String] with io.Serializable = {
    if (compartmentMap.keySet.contains(shapeName)) {
      compartmentMap(shapeName) map {
        case (className, comp) =>
          s"""
            |{
            |  className: "$className",
            |  id: "${comp.compartment_id}"
            |}
            |""".stripMargin
        case _ => ""
      }
    } else {
      Nil
    }
  }

  private def getAttributes(shape: GeometricModel, parentClass: String): String = {
    shape match {
      case l: Line => getAttributes(l, parentClass)
      case e: Ellipse => getAttributes(e, parentClass)
      case r: Rectangle => getAttributes(r, parentClass)
      case r: RoundedRectangle => getAttributes(r, parentClass)
      case p: Polygon => getAttributes(p, parentClass)
      case p: PolyLine => getAttributes(p, parentClass)
      case t: Text => getAttributes(t, parentClass)
    }
  }

  protected def getAttributes(shape: Line, parentClass: String): String = {
    s"""
      |x1: ${shape.x1},
      |y1: ${shape.y1},
      |x2: ${shape.x2},
      |y2: ${shape.y2}
      |""".stripMargin
  }

  protected def getAttributes(shape: Rectangle, parentClass: String): String = {
    s"""
      |${generatePosition(shape)}
      |width: ${shape.size_width},
      |height: ${shape.size_height}
      |""".stripMargin
  }

  protected def getAttributes(shape: Ellipse, parentClass: String): String = {
    s"""
      |${generatePosition(shape)}
      |rx: ${shape.size_width / 2},
      |ry: ${shape.size_height / 2}
      |""".stripMargin
  }

  protected def getAttributes(shape: Polygon, parentClass: String): String = {
    shape.points.map { p => getX(p, shape) + "," + getY(p, shape) + " " }.mkString("\npoints: \"", "", "\"")
  }

  protected def getAttributes(shape: PolyLine, parentClass: String): String = {
    shape.points.map { p => getX(p, shape) + "," + getY(p, shape) + " " }.mkString("\npoints: \"", "", "\"")
  }

  protected def getAttributes(shape: RoundedRectangle, parentClass: String): String = {
    s"""
      |${generatePosition(shape)}
      |'width': ${shape.size_width},
      |'height': ${shape.size_height},
      |rx: ${shape.curve_width},
      |ry: ${shape.curve_height}
      |""".stripMargin
  }

  protected def getAttributes(shape: Text, parentClass: String): String = {
    s"""
      |'width': ${shape.size_width},
      |'height': ${shape.size_height},
      |text: ${shape.textBody} // Is overwritten in stencil, but needed here for scaling
      |""".stripMargin
  }

  protected def generatePosition(shape: Rectangle): String = {
    s"""
      |x: ${getX(shape)},
      |y: ${getY(shape)},
      |""".stripMargin
  }

  protected def generatePosition(shape: Ellipse): String = {
    s"""
      |cx: ${getCx(shape)},
      |cy: ${getCy(shape)},
      |""".stripMargin
  }

  protected def generatePosition(shape: RoundedRectangle): String = {
    s"""
      |x: ${getX(shape)},
      |y: ${getY(shape)},
      |""".stripMargin
  }

  protected def generatePosition(shape: Text): String = {
    s"""
      |x: ${getX(shape)},
      |y: ${getY(shape)},
      |""".stripMargin
  }

  def calculateWidth(shapeDef: Shape): Int = {
    var width = 0
    shapeDef.shapes.getOrElse(List()).foreach { s =>
      val geoWidth = s match {
        case l: Ellipse => getWidth(l)
        case l: Rectangle => getWidth(l)
        case l: Line => getWidth(l)
        case l: RoundedRectangle => getWidth(l)
        case l: Polygon => getWidth(l)
        case l: PolyLine => getWidth(l)
        case l: Text => getWidth(l)
      }
      width = Math.max(geoWidth, width)
    }
    width
  }

  protected def getWidth(shape: Line): Int = {
    maxX(List(shape.points._1, shape.points._2))
  }

  protected def getWidth(shape: Rectangle): Int = {
    shape.x + shape.size_width
  }

  // Ellipse is automatically positioned with xcor + radius. Thus, we need xcor + radius + radius = xcor + diameter here.
  protected def getWidth(shape: Ellipse): Int = {
    shape.x + shape.size_width
  }

  protected def getWidth(shape: Polygon): Int = {
    maxX(shape.points)
  }

  protected def getWidth(shape: PolyLine): Int = {
    maxX(shape.points)
  }

  protected def getWidth(shape: RoundedRectangle): Int = {
    shape.x + shape.size_width
  }

  protected def getWidth(shape: Text): Int = {
    shape.x + shape.size_width
  }

  protected def maxX(points: List[Point]): Int = {
    var max = points.head.x
    for {p <- points} {
      if (p.x > max) {
        max = p.x
      }
    }
    max
  }

  protected def minX(points: List[Point]): Int = {
    var min = points.head.x
    for {p <- points} {
      if (p.x < min) {
        min = p.x
      }
    }
    min
  }

  def calculateHeight(shapeDef: Shape): Int = {
    var height = 0
    shapeDef.shapes.getOrElse(List()).foreach { s =>
      val geoWidth = s match {
        case l: Ellipse => getHeight(l)
        case l: Rectangle => getHeight(l)
        case l: Line => getHeight(l)
        case l: RoundedRectangle => getHeight(l)
        case l: Polygon => getHeight(l)
        case l: PolyLine => getHeight(l)
        case l: Text => getHeight(l)
      }
      height = Math.max(geoWidth, height)
    }
    height
  }

  protected def getHeight(shape: Line): Int = maxY(List(shape.points._1, shape.points._2))

  protected def getHeight(shape: Rectangle): Int = shape.y + shape.size_height

  // Ellipse is automatically positioned with ycor + radius. Thus we need ycor + radius + radius = ycor + diameter here
  protected def getHeight(shape: Ellipse): Int = {
    shape.y + shape.size_height
  }

  protected def getHeight(shape: Polygon): Int = maxY(shape.points)

  protected def getHeight(shape: PolyLine): Int = maxY(shape.points)

  protected def getHeight(shape: RoundedRectangle): Int = shape.y + shape.size_height

  protected def getHeight(shape: Text): Int = shape.y + shape.size_height

  protected def maxY(points: List[Point]): Int = {
    var max = points.head.y
    for {p <- points} {
      if (p.y > max) {
        max = p.y
      }
    }
    max
  }

  protected def minY(points: List[Point]): Int = {
    var min = points.head.y
    for {p <- points} {
      if (p.y < min) {
        min = p.y
      }
    }
    min
  }

  protected def getX(point: Point, shape: GeometricModel): Int = {
    if (shape.parent.isEmpty) {
      point.x
    } else {
      point.x + callRightReferenceX(shape.parent.get) // last because multiple inheritance is now possible and latest bound principle
    }
  }

  protected def getY(point: Point, shape: GeometricModel): Int = {
    if (shape.parent.isEmpty) {
      point.y
    } else {
      point.y + callRightReferenceY(shape.parent.get)
    }
  }

  protected def getX1(shape: Line): Int = {
    if (shape.parent.isEmpty) {
      shape.points._1.x
    } else {
      shape.points._1.x + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getX2(shape: Line): Int = {
    if (shape.parent.isEmpty) {
      shape.points._2.x
    } else {
      shape.points._2.x + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getY1(shape: Line): Int = {
    if (shape.parent.isEmpty) {
      shape.points._1.y
    } else {
      shape.points._2.y + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getY2(shape: Line): Int = {
    if (shape.parent.isEmpty) {
      shape.points._1.y
    } else {
      shape.points._2.y + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getX(shape: Rectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getY(shape: Rectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._2
    } else {
      shape.position.getOrElse((0, 0))._2 + callRightReferenceY(shape.parent.get)
    }
  }

  protected def getX(shape: RoundedRectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getY(shape: RoundedRectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._2
    } else {
      shape.position.getOrElse((0, 0))._2 + callRightReferenceY(shape.parent.get)
    }
  }

  protected def getX(shape: Text): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getY(shape: Text): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._2
    } else {
      shape.position.getOrElse((0, 0))._2 + callRightReferenceY(shape.parent.get)
    }
  }

  protected def getCx(shape: Ellipse): Int = {
    if (shape.parent.isEmpty) {
      (shape.size_width / 2) + shape.x
    } else {
      (shape.size_width / 2) + shape.x + callRightReferenceX(shape.parent.get)
    }
  }

  protected def getCy(shape: Ellipse): Int = {
    if (shape.parent.isEmpty) {
      (shape.size_height / 2) + shape.y
    } else {
      (shape.size_height / 2) + shape.y + callRightReferenceY(shape.parent.get)
    }
  }

  private def callRightReferenceX(g: GeometricModel): Int = {
    g match {
      case e: Ellipse => referenceX(e)
      case r: Rectangle => referenceX(r)
      case p: Polygon => referenceX(p)
      case r: RoundedRectangle => referenceX(r)
      case _ => 0
    }
  }

  private def callRightReferenceY(g: GeometricModel): Int = {
    g match {
      case e: Ellipse => referenceY(e)
      case r: Rectangle => referenceY(r)
      case p: Polygon => referenceY(p)
      case r: RoundedRectangle => referenceY(r)
      case _ => 0
    }
  }

  protected def referenceX(shape: Rectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def referenceX(shape: Ellipse): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def referenceX(shape: Polygon): Int = {
    if (shape.parent.isEmpty) {
      minX(shape.points)
    } else {
      minX(shape.points) + callRightReferenceX(shape.parent.get)
    }
  }

  protected def referenceX(shape: RoundedRectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._1
    } else {
      shape.position.getOrElse((0, 0))._1 + callRightReferenceX(shape.parent.get)
    }
  }

  protected def referenceY(shape: Rectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._2
    } else {
      shape.position.getOrElse((0, 0))._2 + callRightReferenceY(shape.parent.get)
    }
  }

  protected def referenceY(shape: Ellipse): Int = {
    if (shape.parent.isEmpty) {
      shape.position.getOrElse((0, 0))._2
    } else {
      shape.position.getOrElse((0, 0))._2 + callRightReferenceY(shape.parent.get)
    }
  }

  protected def referenceY(shape: Polygon): Int = {
    if (shape.parent.isEmpty) {
      minY(shape.points)
    } else {
      minY(shape.points) + callRightReferenceY(shape.parent.get)
    }
  }

  protected def referenceY(shape: RoundedRectangle): Int = {
    if (shape.parent.isEmpty) {
      shape.y
    } else {
      shape.y + callRightReferenceY(shape.parent.get)
    }
  }

  protected def getParent(shape: Shape): Shape = {
    if (shape.extendedShape.isEmpty) {
      null
    } else {
      shape.extendedShape.last
    }
  }
}
