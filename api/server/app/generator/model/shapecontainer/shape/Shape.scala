package generator.model.shapecontainer.shape

import generator.model.ClassHierarchy
import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.shape.anchor.Anchor
import generator.model.shapecontainer.shape.anchor.Anchor.AnchorType
import generator.model.shapecontainer.shape.geometrics.Description
import generator.model.shapecontainer.shape.geometrics.GeometricModel
import generator.model.shapecontainer.shape.geometrics.Rectangle
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.shapecontainer.shape.geometrics.Wrapper
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.CommonParserMethods
import generator.parser.GeoModel


/**
 * Created by julian on 29.09.15.
 * The actual representation of a shape class,
 * will hold all the relevant attributes
 *
 * @param name                 = id
 * @param style                ??
 * @param sizeWidthMin         ??
 * @param sizeHeightMin        ??
 * @param sizeWidthMax         ??
 * @param sizeHeightMax        ??
 * @param stretchingHorizontal ??
 * @param stretchingVertical   ??
 * @param proportional         ??
 * @param parentShapes         are the shapes that this new shape will inherit its attributes of
 * @param parentTextMap        ??
 * @param parentCompartmentMap ??
 * @param geos                 is a list of GeoModels kind of like sketch-GeometricModels, which will be converted into real GeometricModels inside the
 *                             constructor
 * @param description          ??
 * @param anchor               ??
 * @param extendedShape        ??
 * @param shapes               ??
 * @param textMap              ??
 * @param compartmentMap       ??
 */
final class Shape private(
    override val name: String = "no name",
    val style: Option[Style] = None,
    val sizeWidthMin: Option[Int] = None, // from ShapeLayout
    val sizeHeightMin: Option[Int] = None, // from ShapeLayout
    val sizeWidthMax: Option[Int] = None, // from ShapeLayout
    val sizeHeightMax: Option[Int] = None, // from ShapeLayout
    val stretchingHorizontal: Option[Boolean] = None, // from ShapeLayout
    val stretchingVertical: Option[Boolean] = None, // from ShapeLayout
    val proportional: Option[Boolean] = None, // from ShapeLayout

    val parentShapes: Option[List[GeometricModel]] = None,
    val parentTextMap: Option[Map[String, Text]] = None, // necessary addition
    val parentCompartmentMap: Option[Map[String, Compartment]] = None, // necessary addition
    val geos: List[GeoModel] = Nil,

    val description: Option[Description] = None,
    val anchor: Option[AnchorType] = None,
    val extendedShape: List[Shape] = Nil, // FIXME dead param
    val shapes: Option[List[GeometricModel]] = None, // equal to getShapes(parentShapes = None, geos = Nil, style = None)
    val textMap: Option[Map[String, Text]] = None, // equal to getTextMap(shapes = None, parentTextMap = None)
    val compartmentMap: Option[Map[String, Compartment]] = None // equal to getCompartmentMap(shapes = None)

) extends ShapeContainerElement {


  /**
   * useful Methodes
   */
  override def toString: String = {
    List(
      name,
      style,
      sizeWidthMin,
      sizeHeightMin,
      sizeWidthMax,
      sizeHeightMax,
      stretchingHorizontal,
      stretchingVertical,
      proportional,
      shapes,
      textMap,
      compartmentMap,
      description,
      anchor,
      extendedShape
    ).mkString("Shape(", ", ", ")")
  }

}


object Shape extends CommonParserMethods {
  private val sizeMax = "size-max"
  private val sizeMin = "size-min"

  private val stretching = "stretching"
  private val proportional = "proportional"
  private val anchor = "anchor"

  val validShapeVariables = List(
    sizeMin,
    sizeMax,
    stretching,
    proportional,
    anchor,
    // scalastyle:off
    "description(\\s*style\\s*[a-zA-ZüäöÜÄÖ]+([-_][a-zA-ZüäöÜÄÖ])*)?\\s*"
    // scalastyle:on
  )

  /**
   * Constructor  for Shape
   *
   * @param n the name of the shape
   * @return a Shape with name n
   */
  def apply(n: String): Shape = {
    new Shape(name = n)
  }


  /**
   * Constructor  for Shape
   *
   * @param name               of shape
   * @param parentShapesOpt    ??
   * @param styleArgument      ??
   * @param attributes         ??
   * @param geos               ??
   * @param description        ??
   * @param anchor             ??
   * @param hierarchyContainer ??
   * @return a Shape
   */
  def apply(
    name: String,
    parentShapesOpt: Option[List[String]],
    styleArgument: Option[Style],
    attributes: List[(String, String)],
    geos: List[GeoModel],
    description: Option[(String, String)],
    anchor: Option[String],
    hierarchyContainer: Cache): Shape = {
    val extendedShapes: List[Shape] = calcExtendedShapes(parentShapesOpt, hierarchyContainer)

    def mostRelevant[T] = ClassHierarchy.mostRelevant[T, Shape](extendedShapes) _

    def parseTuple[T] = parseAttrTuple[T](attributes, mostRelevant[T]) _

    val style = processStyle(styleArgument, mostRelevant(_.style), hierarchyContainer)
    val parentShapes = mostRelevant(_.shapes)
    val shapes = getShapes(parentShapes = parentShapes, geos = geos, style = style)
    val parentTextMap = mostRelevant(_.textMap)
    val (sizeWidthMin, sizeHeightMin) = parseTuple(sizeMin, widthHeight)(_.sizeWidthMin, _.sizeHeightMin)
    val (sizeWidthMax, sizeHeightMax) = parseTuple(sizeMax, widthHeight)(_.sizeWidthMax, _.sizeHeightMax)
    val (stretchingHorizontal, stretchingVertical) = parseTuple(stretching, parseStretching)(_.stretchingHorizontal, _.stretchingVertical)

    updateShapeHierarchy(extendedShapes, hierarchyContainer)(
      new Shape(
        name = name,
        style = style,
        sizeWidthMin = sizeWidthMin,
        sizeHeightMin = sizeHeightMin,
        sizeWidthMax = sizeWidthMax,
        sizeHeightMax = sizeHeightMax,
        stretchingHorizontal = stretchingHorizontal,
        stretchingVertical = stretchingVertical,
        proportional = parseAttr(attributes)(proportional, parseProportional).orElse(mostRelevant(_.proportional)),
        parentShapes = parentShapes,
        parentTextMap = parentTextMap,
        parentCompartmentMap = mostRelevant(_.compartmentMap),
        geos = geos,
        description = description.flatMap(value => Description.parse(value, style, hierarchyContainer)).orElse(mostRelevant(_.description)),
        anchor = getAnchor(anchor, attributes, extendedShapes),
        extendedShape = Nil,
        shapes = shapes,
        textMap = getTextMap(shapes = shapes, parentTextMap = parentTextMap),
        compartmentMap = getCompartmentMap(shapes = shapes)
      )
    )
  }

  private def getAnchor(anchor: Option[String], attributes: List[(String, String)], extendedShapes: List[Shape]): Option[AnchorType] = {
    anchor.map(value => Anchor.parse(Anchor.anchor, value).get)
      .orElse(attributes.find(_._1 == this.anchor).flatMap(value => Anchor.parse(Anchor.anchor, value._2) match {
        case Anchor.Success(result: Anchor.AnchorType, _) => Some(result)
        case _ => None
      })).orElse(ClassHierarchy.mostRelevant(extendedShapes)(_.anchor))
  }


  /**
   * include new shape instance in shapeHierarchy
   */
  private def updateShapeHierarchy(extendedShapes: scala.List[Shape], cache: Cache)(newShape: Shape): Shape = {
    extendedShapes match {
      case Nil => cache.shapeHierarchy.newBaseClass(newShape)
      case _ => extendedShapes.reverse.foreach(elem => cache.shapeHierarchy(elem.name, newShape))
    }
    newShape
  }

  private def calcExtendedShapes(parentShapesOpt: Option[List[String]], hierarchyContainer: Cache): List[Shape] = {
    parentShapesOpt match {
      case None => Nil
      case Some(list) => list.foldLeft[List[Shape]](Nil)((shapes, s_name) => {
        val trimmed = s_name.trim()
        if (hierarchyContainer.shapeHierarchy.contains(trimmed)) {
          parser.IDtoShape(trimmed)(hierarchyContainer) :: shapes
        } else {
          shapes
        }
      })
    }
  }


  private def parseAttr[T](attributes: List[(String, String)])(attr: String, parser: Parser[Option[T]]): Option[T] = {
    attributes.find(_._1 == attr).flatMap(at => parse(parser, at._2).get)
  }

  private def parseAttrTuple[T](attributes: List[(String, String)], checkRelevant: (Shape => Option[T]) => Option[T])
    (attr: String, parser: Parser[Option[(T, T)]])
    (defaultA: Shape => Option[T], defaultB: Shape => Option[T]): (Option[T], Option[T]) = {
    parseAttr(attributes)(attr, parser) match {
      case Some((a, b)) => (Some(a), Some(b))
      case None => (checkRelevant(defaultA), checkRelevant(defaultB))
    }
  }

  private def processStyle(styleArgument: Option[Style], style: Option[Style], cache: Cache): Option[Style] = {
    styleArgument match {
      case None => style
      case Some(_) => style match {
        case None => styleArgument
        case Some(_) => Style.generateChildStyle(cache, style, styleArgument)
      }
    }
  }

  // parsingRules for special attributes
  private def parseProportional: Parser[Option[Boolean]] = {
    "=?".r ~> argument ^^ {
      case prop: String => Some(matchBoolean(prop))
      case _ => None
    }
  }

  private def parseStretching: Parser[Option[(Boolean, Boolean)]] = {
    "\\(\\s*(horizontal\\s*=\\s*)?".r ~> argument ~ (",\\s*(vertical\\s*=\\s*)?".r ~> argument) <~ ")" ^^ {
      case hor ~ ver => Some(matchBoolean(hor), matchBoolean(ver))
      case _ => None
    }
  }

  private def widthHeight: Parser[Option[(Int, Int)]] = {
    "\\(\\s*(width\\s*=\\s*)?".r ~> argument_int ~ (",\\s*(height\\s*=\\s*)?".r ~> argument_int) <~ ")" ^^ {
      case width ~ height => Some((width.toInt, height.toInt))
      case _ => None
    }
  }

  // TODO check type
  // if parentShape had GeometricModels in 'shapes'-attribute, both the lists (parents and new List of GeometricModels) need to be merged
  private def getShapes(parentShapes: Option[List[GeometricModel]], geos: List[GeoModel], style: Option[Style]): Option[List[GeometricModel]] = {
    (parentShapes, parseGeometricModels(geos, style)) match {
      case (None | Some(Nil), Nil) => None
      case (None | Some(Nil), any) => Some(any)
      case (list, Nil) => list
      case (Some(list), any) => Some(list ::: any)
    }
  }

  /**
   * for generating shape-attribute specific content
   */
  private def parseGeometricModels(geoModels: List[GeoModel], parentStyle: Option[Style]): List[GeometricModel] = {
    geoModels.flatMap(_.parse(None, parentStyle))
  }


  private def getCompartmentMap(shapes: Option[List[GeometricModel]]): Option[Map[String, Compartment]] = {
    // if parentShape had CompartmentInfos and if new CompartmentInfos were parsed, create a new Map[String, CompartmentInfo]
    // first check for new CompartmentInfo
    shapes match {
      case None | Some(Nil) => None
      case Some(comp) => Some(rCompartment(comp).map(i => (i.compartment_id, i)).toMap)
    }
  }

  /**
   * recursively searches for Compartments in the geometricModels
   */
  private def rCompartment(gModels: List[GeometricModel], compartments: List[Compartment] = List[Compartment]()): List[Compartment] = {
    gModels.foldLeft(compartments)((comp, gm) => gm match {
      case r: Rectangle if r.compartment.isDefined =>
        r.compartment.get :: comp
      case w: Wrapper => rCompartment(w.children, comp)
      case _ => comp
    })
  }

  private def getTextMap(shapes: Option[List[GeometricModel]], parentTextMap: Option[Map[String, Text]]): Option[Map[String, Text]] = {
    shapes match {
      case None => parentTextMap
      case Some(geometricModels) =>
        val texts = geometricModels.flatMap {
          case t: Text => List((t.id, t))
          case _ => Nil
        }
        val textsList = parentTextMap match {
          case None => texts
          case Some(parent) => texts ::: parent.toList
        }
        textsList match {
          case Nil => None
          case list: List[(String, Text)] => Some(list.toMap)
        }
    }
  }
}
