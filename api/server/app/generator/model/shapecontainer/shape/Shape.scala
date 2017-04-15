package generator.model.shapecontainer.shape

import generator.model.ClassHierarchy
import generator.model.shapecontainer.ShapeContainerElement
import generator.model.shapecontainer.shape.anchor.Anchor
import generator.model.shapecontainer.shape.anchor.Anchor.AnchorType
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.Style
import generator.parser.Cache
import generator.parser.CommonParserMethods
import generator.parser.GeoModel
import parser._

/**
 * Created by julian on 29.09.15.
 * The actual representation of a shape class,
 * will hold all the relevant attributes
 *
 * @param name = id
 * @param parentShapes are the shapes that this new shape will inherit its attributes of
 * @param geos is a list of GeoModels kind of like sketch-GeometricModels, which will be converted into real GeometricModels inside the constructor
 */
case class Shape private (
    override val name: String = "no name",
    style: Option[Style] = None,
    size_width_min: Option[Int] = None, /*from ShapeLayout*/
    size_height_min: Option[Int] = None, /*from ShapeLayout*/
    size_width_max: Option[Int] = None, /*from ShapeLayout*/
    size_height_max: Option[Int] = None, /*from ShapeLayout*/
    stretching_horizontal: Option[Boolean] = None, /*from ShapeLayout*/
    stretching_vertical: Option[Boolean] = None, /*from ShapeLayout*/
    proportional: Option[Boolean] = None, /*from ShapeLayout*/

    parentShapes: Option[List[GeometricModel]] = None,
    parentTextMap: Option[Map[String, Text]] = None, /*necessary addition*/
    parentCompartmentMap: Option[Map[String, Compartment]] = None, /*necessary addition*/
    geos: List[GeoModel] = List(),

    description: Option[Description] = None,
    anchor: Option[AnchorType] = None,
    extendedShape: List[Shape] = List()
) extends ShapeContainerElement {

  /*if parentShape had GeometricModels in 'shapes'-attribute, both the lists (parents and new List of GeometricModels) need to be merged*/
  val shapes = {
    val inherited_and_new_geometrics = parentShapes.getOrElse(List()) ::: parseGeometricModels(geos, style)
    if (inherited_and_new_geometrics nonEmpty) Some(inherited_and_new_geometrics) else None
  }

  /*if parentShape has TextOutputFields (Text) and if new TextFields(in geos) were parsed, create a new Map[String, Text]*/
  /*first check for new TextOutputs*/
  val textMap = {
    var ret = parentTextMap
    if (shapes isDefined) {
      var texts = shapes.get.filter(i => i.isInstanceOf[Text]).map(i => i.asInstanceOf[Text].id -> i.asInstanceOf[Text]).toMap
      /*now check for old TextOutputs*/
      if (parentTextMap.isDefined)
        parentTextMap.get.foreach(i => texts += i)
      if (texts nonEmpty) ret = Some(texts)
    }
    ret
  }

  val compartmentMap = {
    /*if parentShape had CompartmentInfos and if new CompartmentInfos were parsed, create a new Map[String, CompartmentInfo]*/
    /*first check for new CompartmentInfo*/
    val comparts = if (shapes isDefined) {
      rCompartment(shapes.get)
    } else List[Compartment]()
    if (comparts nonEmpty)
      Some(comparts.map(i => i.compartment_id -> i).toMap)
    else None
  }

  /*useful Methodes */
  override def toString = "Shape(" + name +
    /*"; style: "                  +*/ ", " + style +
    /*"; size_width_min: "         +*/ ", " + size_width_min +
    /*"; size_height_min: "        +*/ ", " + size_height_min +
    /*"; size_width_max: "         +*/ ", " + size_width_max +
    /*"; size_height_max: "        +*/ ", " + size_height_max +
    /*"; stretching_horizontal: "  +*/ ", " + stretching_horizontal +
    /*"; stretching_vertical: "    +*/ ", " + stretching_vertical +
    /*"; proportional: "           +*/ ", " + proportional +
    /*"; shapes: "                 +*/ ", " + shapes +
    /*"; tests: "                  +*/ ", " + textMap +
    /*"; compartments: "           +*/ ", " + compartmentMap +
    /*"; description: "            +*/ ", " + description +
    /*"; anchor: "                 +*/ ", " + anchor +
    /*"; parentShapes: "           +*/ ", " + extendedShape + ")"

  /**for generating shape-attribute specific content*/
  private def parseGeometricModels(geoModels: List[GeoModel], parentStyle: Option[Style]) =
    geoModels.map { _.parse(None, parentStyle) }.
      foldLeft(List[GeometricModel]())((list, c: Option[GeometricModel]) => if (c.isDefined) c.get :: list else list)

  /**recursively searches for Compartments in the geometricModels*/
  private def rCompartment(g: List[GeometricModel], compartments: List[Compartment] = List[Compartment]()): List[Compartment] = {
    var ret: List[Compartment] = compartments
    g foreach {
      case e: Ellipse if e.compartment.isDefined =>
        ret = e.compartment.get :: ret
      case e: Rectangle if e.compartment.isDefined =>
        ret = e.compartment.get :: ret
      case _ =>
    }
    g foreach {
      case e: Wrapper =>
        ret = ret ::: rCompartment(e.children, ret)
      case _ =>
    }
    ret
  }
}

object Shape extends CommonParserMethods {
  val validShapeVariables = List(
    "size-min",
    "size-max",
    "stretching",
    "proportional",
    "anchor",
    "description(\\s*style\\s*[a-zA-ZüäöÜÄÖ]+([-_][a-zA-ZüäöÜÄÖ])*)?\\s*"
  )

  def apply(n: String) = new Shape(name = n)
  def apply(
    name: String,
    parents: Option[List[String]],
    style: Option[Style],
    attributes: List[(String, String)],
    geos: List[GeoModel],
    description: Option[(String, String)],
    anchor: Option[String],
    hierarchyContainer: Cache) = {

    parse(name, parents, style, attributes, geos, description, anchor, hierarchyContainer)
  }

  private def parse(
    name: String,
    parentShapes: Option[List[String]],
    styleArgument: Option[Style],
    attributes: List[(String, String)],
    geos: List[GeoModel],
    desc: Option[(String, String)],
    anch: Option[String],
    hierarchyContainer: Cache
  ): Shape = {
    implicit val cache = hierarchyContainer

    val extendedShapes = parentShapes.getOrElse(List[String]()).foldLeft(List[Shape]())((shapes, s_name) =>
      if (cache.shapeHierarchy.contains(s_name.trim)) s_name.trim :: shapes else shapes)

    /**
     * relevant is a help-methode, which shortens the actual call to mostRelevant of ClassHierarchy by ensuring the collection-parameter
     * relevant speaks for the hierarchical context -> "A extends B, C" -> C is most relevant
     */
    def relevant[T](f: Shape => Option[T]) = ClassHierarchy.mostRelevant(extendedShapes) { f }

    val shape = new Shape(
      name = name,
      style = processStyle(styleArgument, relevant { _.style }, hierarchyContainer),
      size_width_min = relevant { _.size_width_min },
      size_height_min = relevant { _.size_height_min },
      size_width_max = relevant { _.size_width_max },
      size_height_max = relevant { _.size_height_max },
      stretching_horizontal = relevant { _.stretching_horizontal },
      stretching_vertical = relevant { _.stretching_vertical },
      proportional = relevant { _.proportional },
      parentShapes = relevant { _.shapes },
      parentTextMap = relevant { _.textMap },
      parentCompartmentMap = relevant { _.compartmentMap },
      geos = geos,
      description = relevant { _.description },
      anchor = relevant { _.anchor }
    )

    val newShape = processAnch(anch, processDesc(desc, cache, processAttributes(attributes, shape)))

    /*include new shape instance in shapeHierarchie*/
    if (extendedShapes.nonEmpty) {
      extendedShapes.reverse.foreach(elem => cache.shapeHierarchy(elem.name, newShape))
    } else {
      cache.shapeHierarchy.newBaseClass(newShape)
    }
    newShape
  }

  private def processStyle(styleArgument: Option[Style], style: Option[Style], cache: Cache) = {
    styleArgument match {
      case None => style
      case Some(_) => style match {
        case None => styleArgument
        case Some(_) => Style.generateChildStyle(cache, style, styleArgument)
      }
    }
  }

  private def processAttributes(attributes: List[(String, String)], initShape: Shape): Shape = {
    attributes.foldLeft(initShape) { (shape, value) =>
      value._1 match {
        case "size-min" => processSizeMin(value._2, shape)
        case "size-max" => processSizeMax(value._2, shape)
        case "stretching" => processStretching(value._2, shape)
        case "proportional" => shape.copy(proportional = parse(proportional, value._2).get)
        case "anchor" => processAnchor(value._2, shape)
        case _ => shape
      }
    }
  }

  private def processDesc(desc: Option[(String, String)], cache: Cache, shape: Shape) = {
    desc match {
      case None => shape
      case Some(value) => shape.copy(description = Description.parse(value, shape.style, cache))
    }
  }

  private def processAnch(anch: Option[String], shape: Shape) = {
    anch match {
      case None => shape
      case Some(value) => shape.copy(anchor = Some(Anchor.parse(Anchor.anchor, value).get))
    }
  }

  private def processSizeMin(value: String, shape: Shape) = {
    parse(widthHeight, value).get match {
      case None => shape
      case Some(opt) => shape.copy(size_width_min = Some(opt._1), size_height_min = Some(opt._2))
    }
  }

  private def processSizeMax(value: String, shape: Shape) = {
    parse(widthHeight, value).get match {
      case None => shape
      case Some(opt) => shape.copy(size_width_max = Some(opt._1), size_height_max = Some(opt._2))
    }
  }

  private def processStretching(value: String, shape: Shape) = {
    parse(stretching, value).get match {
      case None => shape
      case Some(opt) => shape.copy(stretching_horizontal = Some(opt._1), stretching_vertical = Some(opt._2))
    }
  }

  private def processAnchor(value: String, shape: Shape) = {
    val anchor = Anchor.parse(Anchor.anchor, value)
    if (anchor.isEmpty) {
      shape.copy(anchor = None)
    } else {
      shape.copy(anchor = Some(anchor.get))
    }
  }

  /*parsingRules for special attributes*/
  private def proportional: Parser[Option[Boolean]] = "=?".r ~> argument ^^ {
    case prop: String => Some(matchBoolean(prop))
    case _ => None
  }
  private def stretching: Parser[Option[(Boolean, Boolean)]] = {
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
}
