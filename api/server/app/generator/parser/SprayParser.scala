package generator.parser

import generator.model.diagram.action.{ Action, ActionGroup, ActionInclude }
import generator.model.diagram.edge.Edge
import generator.model.diagram.methodes.{ ActionBlock, OnCreate, OnDelete, OnUpdate }
import generator.model.diagram.node.{ DiaShape, Node }
import generator.model.diagram.Diagram
import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics._
import generator.model.style.Style
import models.document.MetaModelEntity
import models.modelDefinitions.metaModel.elements.{ MClass, MReference }
import parser._

/**
 * Created by julian on 23.10.15.
 * offers functions like parseRawShape/Style, which parses style or shape strings to instances
 */
class SprayParser(c: Cache = Cache(), val metaModelE: MetaModelEntity) extends CommonParserMethods {
  implicit val cache = c
  type diaConnection = generator.model.diagram.edge.Connection

  private val metaMapMClass = metaModelE.metaModel.elements.collect {
    case (name, x) if x.isInstanceOf[MClass] => (name, x.asInstanceOf[MClass])
  }
  private val metaMapMReference = metaModelE.metaModel.elements.collect {
    case (name, x) if x.isInstanceOf[MReference] => (name, x.asInstanceOf[MReference])
  }
  require(metaMapMClass nonEmpty)

  /*Style-specific----------------------------------------------------------------------------*/
  private def styleVariable = ("""(""" + Style.validStyleAttributes.map(_ + "|").mkString + """)""").r ^^ { _.toString }
  private def styleAttribute = styleVariable ~ (rgbArgument | gradientArgument | arguments) ^^ { case v ~ a => (v, a) }
  private def rgbArgument = "\\s*=\\s*RGB\\s*\\(.+\\)".r ^^ { _.toString }
  private def gradientArgument = "(s?)\\s*=\\s*gradient.+\\{[^\\{\\}]*\\}".r ^^ { _.toString }
  private def style: Parser[Style] =
    ("style" ~> ident) ~ (("extends" ~> rep(ident <~ ",?".r))?) ~ ("{" ~> rep(styleAttribute)) <~ "}" ^^ {
      case name ~ parents ~ attributes => Style(name, parents, attributes, cache)
    }
  private def anonymousStyle =
    "style" ~> (("extends" ~> rep(ident <~ ",?".r))?) ~ ("[\\{\\(]".r ~> rep(styleAttribute)) <~ "[\\}\\)]".r ^^ {
      case parents ~ attributes => Style("Anonymous_Style" + java.util.UUID.randomUUID(), parents, attributes, cache).name
    }
  private def styles = rep(style)
  def parseStyle(input: String) = parseAll(styles, trimRight(input)).get
  /*------------------------------------------------------------------------------------------*/

  /*GeometricModel-specific-------------------------------------------------------------------*/
  private def geoVariable: Parser[String] = "(position|size|point|curve|align|id|textBody|compartment)".r ^^ { _.toString }
  private def geoAttribute = geoVariable ~ arguments ^^ { case v ~ a => v + a }
  private def geoIdentifier: Parser[String] = "(ellipse|line|polygon|polyline|rectangle|rounded-rectangle|text|wrapped-text)".r ^^ { _.toString }

  /**parses a geoModel. first ident is the GeometricModels name, second ident is an optional reference to a style*/
  private def geoModel: Parser[GeoModel] =
    geoIdentifier ~
      ((("style" ~> ident)?) <~ "{") ~
      rep(geoAttribute | anonymousStyle) ~
      (rep(geoModel) <~ "}") ^^ {
        case name ~ style ~ attr ~ children =>
          GeoModel(name, { if (style.isDefined) Some(style.get) else None }, attr, children, cache)
      }
  /*------------------------------------------------------------------------------------------*/

  /*Shape-specific----------------------------------------------------------------------------*/
  private def shapeVariable = ("""(""" + Shape.validShapeVariables.map(_ + "|").mkString + """)""").r ^^ { _.toString }
  private def shapeAttribute = shapeVariable ~ arguments ^^ { case v ~ a => (v, a) }
  private def descriptionAttribute: Parser[(String, String)] =
    "description" ~> "(style\\s*([a-zA-ZüäöÜÄÖ][-_]?)+)?".r ~ argument_wrapped ^^ { case desStyl ~ args => (desStyl, args) }
  private def anchorAttribute = "anchor" ~> arguments ^^ { _.toString }

  private def shapeSketch: Parser[ShapeSketch] =
    ("shape" ~> ident) ~
      (("extends" ~> rep(("(?!style)".r ~> ident) <~ ",?".r))?) ~
      (("style" ~> ident)?) ~
      ("{" ~> rep(shapeAttribute)) ~
      rep(geoModel) ~
      (descriptionAttribute?) ~
      (anchorAttribute?) <~ "}" ^^
      {
        case name ~ parent ~ style ~ attrs ~ geos ~ desc ~ anch =>
          ShapeSketch(name, parent, style, attrs, geos, desc, anch, cache)
      }
  private def abstractShape: Parser[Shape] = shapeSketch ^^ { case sketch => sketch.toShape(None) }

  private def abstractShapes = rep(abstractShape | abstractConnection)
  private def shapeSketches = rep(shapeSketch | connectionSketch)

  def parseAbstractShape(input: String): List[AnyRef] = parseAll(abstractShapes, trimRight(input)).get
  def parseShape(input: String): List[AnyRef] = parseAll(shapeSketches, trimRight(input)).get
  /*------------------------------------------------------------------------------------------*/

  /*Connection-Specific-----------------------------------------------------------------------*/
  private def c_type = "connection-type\\s*=\\s*".r ~> "(freeform|manhatten)".r
  private def c_placing = ("placing\\s*\\{".r ~> ("position" ~> arguments)) ~ (geoModel <~ "}") ^^ {
    case posi ~ geo => PlacingSketch(posi, geo)
  }

  private def connectionSketch: Parser[ConnectionSketch] =
    ("connection" ~> ident) ~
      (("style" ~> ident)?) ~
      ("{" ~> (c_type?)) ~
      (anonymousStyle?) ~
      rep(c_placing) <~ "}" ^^ {
        case name ~ style ~ typ ~ anonymousStyle ~ placings =>
          val newConnection = ConnectionSketch(name, style, typ, anonymousStyle, placings)
          cache + newConnection
          newConnection
      }
  private def abstractConnection: Parser[Connection] = connectionSketch ^^ { case sketch => sketch.toConnection(None, cache).get }

  private def connectionSketches = rep(connectionSketch)
  private def abstractConnections = rep(abstractConnection)

  def parseConnection(input: String) = parseAll(connectionSketches, trimRight(input)).get
  def parseAbstractConnection(input: String) = parseAll(abstractConnections, trimRight(input)).get
  /*------------------------------------------------------------------------------------------*/

  /*Diagram-Specific--------------------------------------------------------------------------*/
  private def possibleActionDefinitionNr1 = {
    ("action" ~> ident) ~
      (("(" ~ "label" ~ ":") ~> argument <~ ",") ~
      ("class" ~ ":" ~> argument <~ ",") ~
      ("method" ~ ":" ~> ident <~ ")") ^^ {
        case name ~ label ~ className ~ methode =>
          val newAction = Action(name, label, className, methode)
          cache + newAction
          newAction
      }
  }
  private def possibleActionDefinitionNr2 = {
    ("action" ~> ident) ~
      (("(" ~ "label" ~ ":") ~> argument <~ ",") ~
      ("method" ~ ":" ~> ident <~ ",") ~
      ("class" ~ ":" ~> argument <~ ")") ^^ {
        case name ~ label ~ methode ~ className =>
          val newAction = Action(name, label, className, methode)
          cache + newAction
          newAction
      }
  }

  private def action = {
    possibleActionDefinitionNr1 | possibleActionDefinitionNr2
  }
  private def actionInclude = "include" ~> rep((","?) ~> ident) <~ ";" ^^ {
    case includes =>
      ("actionInclude", ActionInclude(includes.map(cache.actionGroups(_))))
  }

  private def actions = "actions" ~ "{" ~> (actionInclude?) ~ rep(action) <~ "}" ^^ {
    case includes ~ actions =>
      ("actions", (includes.get._2, actions))
  }
  private def actionGroup = ("actionGroup" ~> ident) ~ ("{" ~> rep(action) <~ "}") ^^ {
    case name ~ acts =>
      val newActionGroup = ActionGroup(name, acts)
      cache + newActionGroup
      ("actionGroup", newActionGroup)
  }

  private def palette = "palette" ~ ":" ~> argument <~ ";" ^^ {
    case arg => ("palette", arg.toString)
  }
  private def container = "container" ~ ":" ~> argument <~ ";" ^^ {
    case arg => ("container", arg.toString)
  }
  private def actionBlock = rep(("call"?) ~ "(?!actionGroup)action".r ~> ident) ~ rep(("call"?) ~ "actionGroup" ~> ident) ^^ {
    case actions ~ actionGroups =>
      val acts = actions.map(i => cache.actions(i))
      val actGrps = actionGroups.map(i => cache.actionGroups(i))
      ActionBlock(acts, actGrps)
  }
  private def askFor = "askFor" ~ ":" ~> ident ^^ { _.toString }

  private def onCreate = "onCreate" ~ "{" ~> (actionBlock?) ~ (askFor?) <~ "}" ^^ {
    case actblock ~ askfor => ("onCreate", (actblock, askfor))
  }
  private def onUpdate = "onUpdate" ~ "{" ~> (actionBlock?) <~ "}" ^^ {
    case actBlock => ("onUpdate", actBlock)
  }
  private def onDelete = "onDelete" ~ "{" ~> (actionBlock?) <~ "}" ^^ {
    case actBlock => ("onDelete", actBlock)
  }

  private def shapeVALPropertie = ("val" ~> ident) ~ ("->" ~> ident) ^^ {
    case key ~ value => ("val", key -> value)
  }
  private def shapeVARPropertie = ("var" ~ "[" ~> ident <~ "]") ~ ("->" ~> ident) ^^ { case key ~ value => ("var", key -> value) }
  private def shapeTextPropertie = shapeVALPropertie | shapeVARPropertie <~ ",?".r
  private def shapeCompartmentPropertie = ("nest" ~> ident) ~ ("->" ~> ident) <~ ",?".r ^^ { case key ~ value => ("nest", key -> value) }
  private def diagramShape: Parser[(String, PropsAndComps)] = {
    ("shape" ~ ":" ~> ident) ~ (("(" ~> rep((shapeTextPropertie | shapeCompartmentPropertie) <~ ",?".r) <~ ")")?) ^^ {
      case shapeReference ~ propertiesAndCompartments =>
        ("shape", PropsAndComps(shapeReference, propertiesAndCompartments))
    }
  }

  private def node: Parser[(String, NodeSketch)] = {
    ("node" ~> ident) ~
      ("for" ~> ident) ~
      (("(" ~ "style" ~ ":" ~> ident <~ ")")?) ~
      ("{" ~> rep(diagramShape | palette | container | onCreate | onUpdate | onDelete | actions) <~ "}") ^^ {
        case name ~ mcoreElement ~ corporatestyle ~ args =>
          val corporateStyle: Option[Style] = if (corporatestyle.isDefined) corporatestyle.get else None
          var shap: Option[PropsAndComps] = None
          var pal: Option[String] = None
          var con: Option[String] = None
          var onCr: Option[(ActionBlock, String)] = None
          var onUp: Option[ActionBlock] = None
          var onDe: Option[ActionBlock] = None
          var actions: List[Action] = List()
          var actionIncludes: Option[ActionInclude] = None
          args.foreach {
            case i if i._1 == "shape" => shap = Some(i._2.asInstanceOf[PropsAndComps])
            case i if i._1 == "palette" => pal = Some(i._2.asInstanceOf[String])
            case i if i._1 == "container" => con = Some(i._2.asInstanceOf[String])
            case i if i._1 == "onCreate" =>
              val tmp = i._2.asInstanceOf[(Option[ActionBlock], Option[String])]
              onCr = Some(tmp._1.get, tmp._2.get)
            case i if i._1 == "onUpdate" => onUp = i._2.asInstanceOf[Option[ActionBlock]]
            case i if i._1 == "onDelete" => onDe = i._2.asInstanceOf[Option[ActionBlock]]
            case i if i._1 == "actions" =>
              actions = i._2.asInstanceOf[(ActionInclude, List[Action])]._2
              actionIncludes = Some(i._2.asInstanceOf[(ActionInclude, List[Action])]._1)
          }
          ("node", NodeSketch(name, mcoreElement, corporateStyle, shap, pal, con, onCr, onUp, onDe, actions, actionIncludes))
      }
  }
  case class NodeSketch(
      name: String,
      mcoreElement: String,
      style: Option[Style] = None,
      /*node-block*/
      shape: Option[PropsAndComps] = None,
      palette: Option[String] = None,
      container: Option[String] = None,
      onCreate: Option[(ActionBlock, String)] = None,
      onUpdate: Option[ActionBlock] = None,
      onDelete: Option[ActionBlock] = None,
      actions: List[Action] = List(),
      actionIncludes: Option[ActionInclude] = None
  ) {
    def toNode(diagramStyle: Option[Style], cache: Cache) = {
      val corporateStyle: Option[Style] = Style.generateChildStyle(cache, diagramStyle, style)
      val mClass = metaMapMClass.get(mcoreElement)
      val diagramShape: Option[DiaShape] =
        if (shape isDefined) Some(new DiaShape(corporateStyle, shape.get.ref, shape.get.propertiesAndCompartments, cache, mClass.get, metaModelE))
        else None
      val onCr = if (onCreate isDefined) {
        Some(OnCreate(Some(onCreate.get._1), mClass.get.attributes.find(_.name == onCreate.get._2)))
      } else None
      val onUp = if (onUpdate isDefined) Some(OnUpdate(onUpdate)) else None
      val onDe = if (onDelete isDefined) Some(OnDelete(onDelete)) else None

      val cont = if (container isDefined) metaMapMReference.get(container.get) else None
      Some(Node(name, mClass.get, corporateStyle, diagramShape, palette,
        cont, onCr, onUp, onDe, actions, actionIncludes))
    }
  }

  private def diagramConnection = {
    type diaConnection = generator.model.diagram.edge.Connection
    ("connection" ~ ":" ~> ident) ~
      (("(" ~> rep(shapeTextPropertie) <~ ")") ?) ^^ {
        case connectionName ~ properties => PropsAndComps(connectionName, properties)
      }
  }
  private def edge: Parser[(String, EdgeSketch)] = {
    type diaConnection = generator.model.diagram.edge.Connection
    ("edge" ~> ident) ~
      ("for" ~> ident) ~
      (("(" ~ "style" ~ ":" ~> ident <~ ")") ?) ~
      ("{" ~> diagramConnection) ~
      ("from" ~ ":" ~> ident) ~
      ("to" ~ ":" ~> ident) ~
      (rep(palette | container | onCreate | onUpdate | onDelete | actions) <~ "}") ^^ {
        case edgeName ~ mcoreElement ~ styleOpt ~ diaCon ~ from ~ to ~ args =>
          val style: Option[Style] = if (styleOpt isDefined) styleOpt.get else None
          var pal: Option[String] = None
          var con: Option[String] = None
          var onCr: Option[(ActionBlock, String)] = None
          var onUp: Option[ActionBlock] = None
          var onDe: Option[ActionBlock] = None
          var actions: List[Action] = List()
          var actionIncludes: Option[ActionInclude] = None
          args.foreach {
            case i if i._1 == "palette" => pal = Some(i._2.asInstanceOf[String])
            case i if i._1 == "container" => con = Some(i._2.asInstanceOf[String])
            case i if i._1 == "onCreate" => onCr = Some(i._2.asInstanceOf[(ActionBlock, String)])
            case i if i._1 == "onUpdate" => onUp = i._2.asInstanceOf[Option[ActionBlock]]
            case i if i._1 == "onDelete" => onDe = i._2.asInstanceOf[Option[ActionBlock]]
            case i if i._1 == "actions" =>
              actions = i._2.asInstanceOf[(ActionInclude, List[Action])]._2
              actionIncludes = Some(i._2.asInstanceOf[(ActionInclude, List[Action])]._1)
            case _ =>
          }
          ("edge", EdgeSketch(edgeName, mcoreElement, style, diaCon, from, to, pal, con, onCr, onUp, onDe, actions, actionIncludes))
      }
  }
  case class EdgeSketch(
      name: String,
      mReferenceName: String,
      style: Option[Style] = None,
      /*edge-Block*/
      connection: PropsAndComps,
      from: String,
      to: String,
      palette: Option[String] = None,
      container: Option[String] = None,
      onCreate: Option[(ActionBlock, String)] = None,
      onUpdate: Option[ActionBlock] = None,
      onDelete: Option[ActionBlock] = None,
      actions: List[Action] = List(),
      actionIncludes: Option[ActionInclude] = None
  ) {
    def toEdge(diagramStyle: Option[Style], cache: Cache) = {
      val corporateStyle: Option[Style] = Style.generateChildStyle(cache, diagramStyle, style)
      val diagramConnection: diaConnection = new diaConnection(corporateStyle, connection, cache, metaMapMReference(mReferenceName))

      val mReference = metaMapMReference.get(mReferenceName)
      val fromClass = metaMapMClass.get(from)
      val toClass = metaMapMClass.get(to)

      val onCr = if (onCreate isDefined) {
        Some(OnCreate(Some(onCreate.get._1), mReference.get.attributes.find(_.name == onCreate.get._2)))
      } else None
      val onUp = if (onUpdate isDefined) Some(OnUpdate(onUpdate)) else None
      val onDe = if (onDelete isDefined) Some(OnDelete(onDelete)) else None

      val cont = if (container isDefined) metaMapMReference.get(container.get) else None
      Some(Edge(name, mReference.get, corporateStyle, diagramConnection, fromClass.get, toClass.get, palette, cont, onCr, onUp, onDe, actions, actionIncludes))
    }
  }

  private def nodeOrEdge = node | edge

  private def sprayDiagram: Parser[Option[Diagram]] = {
    ("diagram" ~> ident) ~
      ("for" ~> ident) ~
      (("(" ~ "style" ~ ":" ~> ident <~ ")")?) ~
      ("{" ~> rep(actionGroup | nodeOrEdge) <~ "}") ^^ {
        case name ~ metaModelName ~ style ~ arguments =>
          val actionGroups = arguments.filter(i => i._1 == "actionGroup").map(i => i._2.asInstanceOf[ActionGroup].name -> i._2.asInstanceOf[ActionGroup]).toMap
          val nodes = arguments.filter(i => i._1 == "node").map(i =>
            i._2.asInstanceOf[NodeSketch].toNode(style, cache)).foldLeft(List[Node]()) { (l, n) => if (n isDefined) n.get +: l else l }
          val edges = arguments.filter(i => i._1 == "edge").map(i =>
            i._2.asInstanceOf[EdgeSketch].toEdge(style, cache)).foldLeft(List[Edge]()) { (l, e) => if (e isDefined) e.get +: l else l }
          if (metaModelE.metaModel.name == metaModelName) {
            Some(Diagram(name, actionGroups, nodes, edges, style, metaModelE, cache))
          } else {
            None
          }
        case c => println(c.toString()); Some(Diagram("test", Map(), List(), List(), None, null, cache))
      }
  }

  private def sprayDiagrams = rep(sprayDiagram)
  def parseDiagram(e: String) = parseAll(sprayDiagrams, trimRight(e)).get
  /*------------------------------------------------------------------------------------------*/

  private def trimRight(s: String) = s.replaceAll("\\/\\/.+", "").split("\n").map(s => s.trim + "\n").mkString
}

/**
 * ShapeSketch is a sketch of a Shape, only used for parsing a Shape String ans temporarily save all
 * the attributes in a struct for later compilation into a shape
 */
case class ShapeSketch(
    name: String,
    parents: Option[List[String]],
    style: Option[Style],
    attrs: List[(String, String)],
    geos: List[GeoModel],
    descr: Option[(String, String)],
    anch: Option[String],
    cache: Cache
) {
  cache + this
  def toShape(corporateStyle: Option[Style]) = Shape(name, parents, Style.generateChildStyle(cache, corporateStyle, style), attrs, geos, descr, anch, cache)
}

case class PropsAndComps(ref: String, propertiesAndCompartments: Option[List[(String, (String, String))]])

/**
 * GeoModel is a sketch of a GeometricModel, only used for parsing a geometricModel string and temporarily
 * save all the attributes in a struct for later compilation into a GeometricModel
 */
case class GeoModel(typ: String, style: Option[Style], attributes: List[String], children: List[GeoModel], hierarchyCashe: Cache) {

  def parse(parentGeometricModel: Option[GeometricModel], parentStyle: Option[Style]): Option[GeometricModel] = typ match {
    case "ellipse" => Ellipse(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "line" => Line(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "polygon" => Polygon(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "polyline" => PolyLine(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "rectangle" => Rectangle(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "rounded-rectangle" => RoundedRectangle(this, parentGeometricModel, parentStyle, hierarchyCashe)
    case "text" => Text(this, parentGeometricModel, DefaultText, parentStyle, hierarchyCashe)
    case "text-wrapped" => Text(this, parentGeometricModel, Multiline, parentStyle, hierarchyCashe)
    case _ => None
  }
}

/**
 * PlacingSketch is a sketch of a placing, only used for parsing a Connection
 */
case class PlacingSketch(position: String, shape: GeoModel)

case class ConnectionSketch(
    name: String,
    style: Option[Style],
    connection_type: Option[String],
    anoStyle: Option[String],
    placing: List[PlacingSketch]
) {
  def toConnection(corporateStyle: Option[Style], cache: Cache) =
    Connection(name, Style.generateChildStyle(cache, corporateStyle, style), connection_type, anoStyle, placing, cache)
}
