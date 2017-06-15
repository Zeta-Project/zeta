package de.htwg.zeta.server.generator.parser

import java.io.Serializable

import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.action.Action
import de.htwg.zeta.server.generator.model.diagram.action.ActionGroup
import de.htwg.zeta.server.generator.model.diagram.action.ActionInclude
import de.htwg.zeta.server.generator.model.diagram.edge.Edge
import de.htwg.zeta.server.generator.model.diagram.methodes.ActionBlock
import de.htwg.zeta.server.generator.model.diagram.methodes.OnCreate
import de.htwg.zeta.server.generator.model.diagram.methodes.OnDelete
import de.htwg.zeta.server.generator.model.diagram.methodes.OnUpdate
import de.htwg.zeta.server.generator.model.diagram.node.DiaShape
import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.model.shapecontainer.ShapeContainerElement
import de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection
import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.DefaultText
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Ellipse
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.GeometricModel
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Line
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Multiline
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.PolyLine
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Polygon
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Rectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.server.generator.parser
import grizzled.slf4j.Logging
import de.htwg.zeta.common.models.entity.MetaModelEntity
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference

/**
 * SprayParser
 */
object SprayParser

/**
 * offers functions like parseRawShape/Style, which parses style or shape strings to instances
 */
class SprayParser(cache: Cache = Cache(), val metaModelE: MetaModelEntity) extends CommonParserMethods with Logging {
  type diaConnection = de.htwg.zeta.server.generator.model.diagram.edge.Connection

  private val metaMapMClass: Map[String, MClass] = metaModelE.metaModel.classMap

  private val metaMapMReference: Map[String, MReference] = metaModelE.metaModel.referenceMap
  require(metaMapMClass.nonEmpty)

  /* Style-specific---------------------------------------------------------------------------- */
  private def styleVariable: Parser[String] = {
    ("""(""" + Style.validStyleAttributes.map(_ + "|").mkString +
      """)""").r ^^ {
      _.toString
    }
  }

  private def styleAttribute: Parser[(String, String)] = styleVariable ~ (rgbArgument | gradientArgument | arguments) ^^ { case v ~ a => (v, a) }

  private def rgbArgument: Parser[String] = {
    "\\s*=\\s*RGB\\s*\\(.+\\)".r ^^ {
      _.toString
    }
  }

  private def gradientArgument: Parser[String] = {
    "(s?)\\s*=\\s*gradient.+\\{[^\\{\\}]*\\}".r ^^ {
      _.toString
    }
  }

  private def style: Parser[Style] = {
    def mapToStyle(f: ~[~[String, Option[List[String]]], List[(String, String)]]): Style = {
      val ((name: String, parents: Option[List[String]]), attributes: List[(String, String)]) = ((f._1._1, f._1._2), f._2)
      Style(name, parents, attributes, cache)
    }

    literal("style").~>(ident)
      .~(literal("extends").~>(rep(ident.<~(regex(Predef.augmentString(",?").r)))).?)
      .~(literal("{").~>(rep(styleAttribute))).<~(literal("}")).^^(mapToStyle)
  }

  private def anonymousStyle: Parser[String] = {
    "style" ~> (("extends" ~> rep(ident <~ ",?".r)) ?) ~ ("[\\{\\(]".r ~> rep(styleAttribute)) <~ "[\\}\\)]".r ^^ {
      case parents ~ attributes => Style("Anonymous_Style" + java.util.UUID.randomUUID(), parents, attributes, cache).name
    }
  }

  private def styles: Parser[List[Style]] = rep(style)

  def parseStyle(input: String): List[Style] = parseAll(styles, trimRight(input)).get

  /* ------------------------------------------------------------------------------------------ */

  /* GeometricModel-specific------------------------------------------------------------------- */
  private def geoVariable: Parser[String] = {
    "(position|size|point|curve|align|id|textBody|compartment)".r ^^ {
      _.toString
    }
  }

  private def geoAttribute: Parser[String] = geoVariable ~ arguments ^^ { case v ~ a => v + a }

  private def geoIdentifier: Parser[String] = {
    "(ellipse|line|polygon|polyline|rectangle|rounded-rectangle|text|wrapped-text)".r ^^ {
      _.toString
    }
  }

  /**
   * parses a geoModel. first ident is the GeometricModels name, second ident is an optional reference to a style
   */
  private def geoModel: Parser[GeoModel] = {
    geoIdentifier ~
      ((("style" ~> ident) ?) <~ "{") ~
      rep(geoAttribute | anonymousStyle) ~
      (rep(geoModel) <~ "}") ^^ {
      case name ~ style ~ attr ~ children =>
        GeoModel(name, style.flatMap(s => IDtoStyle(s)(cache)), attr, children, cache)
    }
  }

  /* ------------------------------------------------------------------------------------------ */

  /* Shape-specific---------------------------------------------------------------------------- */
  private def shapeVariable: Parser[String] = {
    ("""(""" + Shape.validShapeVariables.map(_ + "|").mkString +
      """)""").r ^^ {
      _.toString
    }
  }

  private def shapeAttribute: Parser[(String, String)] = shapeVariable ~ arguments ^^ { case v ~ a => (v, a) }

  private def descriptionAttribute: Parser[(String, String)] = {
    "description" ~> "(style\\s*([a-zA-ZüäöÜÄÖ][-_]?)+)?".r ~ argument_wrapped ^^ { case desStyl ~ args => (desStyl, args) }
  }

  private def anchorAttribute: Parser[String] = {
    "anchor" ~> arguments ^^ {
      _.toString
    }
  }

  private def shapeSketch: Parser[ShapeSketch] = {
    ("shape" ~> ident) ~
      (("extends" ~> rep(("(?!style)".r ~> ident) <~ ",?".r)) ?) ~
      (("style" ~> ident) ?) ~
      ("{" ~> rep(shapeAttribute)) ~
      rep(geoModel) ~
      (descriptionAttribute ?) ~
      (anchorAttribute ?) <~ "}" ^^ {
      case name ~ parent ~ style ~ attrs ~ geos ~ desc ~ anch =>
        ShapeSketch(name, OptionToStyle(style)(cache), parent, attrs, geos, desc, anch, cache)
    }
  }

  private def abstractShape: Parser[Shape] = shapeSketch ^^ { sketch => sketch.toShape(None) }

  private def abstractShapes: Parser[List[ShapeContainerElement]] = rep(abstractShape | abstractConnection)

  private def shapeSketches: Parser[List[Sketch]] = rep(shapeSketch | connectionSketch)

  def parseAbstractShape(input: String): List[ShapeContainerElement] = parseAll(abstractShapes, trimRight(input)).get

  def parseShape(input: String): List[Sketch] = parseAll(shapeSketches, trimRight(input)).get

  /* ------------------------------------------------------------------------------------------ */

  /* Connection-Specific----------------------------------------------------------------------- */
  private def c_type: Parser[String] = {
    "connection-type\\s*=\\s*".r ~> "(freeform|manhatten)".r
  }

  private def c_placing: Parser[PlacingSketch] = {
    ("placing\\s*\\{".r ~> ("position" ~> arguments)) ~ (geoModel <~ "}") ^^ {
      case posi ~ geo => PlacingSketch(posi, geo)
    }
  }

  private def connectionSketch: Parser[ConnectionSketch] = {
    ("connection" ~> ident) ~
      (("style" ~> ident) ?) ~
      ("{" ~> (c_type ?)) ~
      (anonymousStyle ?) ~
      rep(c_placing) <~ "}" ^^ {
      case name ~ style ~ typ ~ anonymousStyle ~ placings =>
        val newConnection = ConnectionSketch(name, OptionToStyle(style)(cache), typ, anonymousStyle, placings)
        cache + newConnection
        newConnection
    }
  }

  private def abstractConnection: Parser[Connection] = connectionSketch ^^ { sketch => sketch.toConnection(None, cache).get }

  private def connectionSketches: Parser[List[ConnectionSketch]] = rep(connectionSketch)

  private def abstractConnections: Parser[List[Connection]] = rep(abstractConnection)

  def parseConnection(input: String): List[ConnectionSketch] = parseAll(connectionSketches, trimRight(input)).get

  def parseAbstractConnection(input: String): List[Connection] = parseAll(abstractConnections, trimRight(input)).get

  /* ------------------------------------------------------------------------------------------ */

  /* Diagram-Specific-------------------------------------------------------------------------- */
  private def possibleActionDefinitionNr1: Parser[Action] = {
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

  private def possibleActionDefinitionNr2: Parser[Action] = {
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

  private def action: Parser[Action] = {
    possibleActionDefinitionNr1 | possibleActionDefinitionNr2
  }

  private def actionInclude: Parser[(String, ActionInclude)] = {
    "include" ~> rep(("," ?) ~> ident) <~ ";" ^^ {
      includes => ("actionInclude", ActionInclude(includes.map(cache.actionGroups(_))))
    }
  }

  private def actions: Parser[(String, (ActionInclude, List[Action]))] = {
    "actions" ~ "{" ~> (actionInclude ?) ~ rep(action) <~ "}" ^^ {
      case includes ~ actions =>
        ("actions", (includes.get._2, actions))
    }
  }

  private def actionGroup: Parser[(String, ActionGroup)] = {
    ("actionGroup" ~> ident) ~ ("{" ~> rep(action) <~ "}") ^^ {
      case name ~ acts =>
        val newActionGroup = ActionGroup(name, acts)
        cache + newActionGroup
        ("actionGroup", newActionGroup)
    }
  }

  private def palette: Parser[(String, String)] = "palette" ~ ":" ~> argument <~ ";" ^^ { arg => ("palette", arg.toString) }

  private def container: Parser[(String, String)] = "container" ~ ":" ~> argument <~ ";" ^^ { arg => ("container", arg.toString) }

  private def actionBlock: Parser[ActionBlock] = {
    rep(("call" ?) ~ "(?!actionGroup)action".r ~> ident) ~ rep(("call" ?) ~ "actionGroup" ~> ident) ^^ {
      case actions ~ actionGroups =>
        val acts = actions.map(i => cache.actions(i))
        val actGrps = actionGroups.map(i => cache.actionGroups(i))
        ActionBlock(acts, actGrps)
    }
  }

  private def askFor: Parser[String] = {
    "askFor" ~ ":" ~> ident ^^ {
      _.toString
    }
  }

  private def onCreate: Parser[(String, (Option[ActionBlock], Option[String]))] = {
    "onCreate" ~ "{" ~> (actionBlock ?) ~ (askFor ?) <~ "}" ^^ {
      case actblock ~ askfor => ("onCreate", (actblock, askfor))
    }
  }

  private def onUpdate: Parser[(String, Option[ActionBlock])] = "onUpdate" ~ "{" ~> (actionBlock ?) <~ "}" ^^ { actBlock => ("onUpdate", actBlock) }

  private def onDelete: Parser[(String, Option[ActionBlock])] = "onDelete" ~ "{" ~> (actionBlock ?) <~ "}" ^^ { actBlock => ("onDelete", actBlock) }

  private def shapeVALPropertie: Parser[(String, (String, String))] = {
    ("val" ~> ident) ~ ("->" ~> ident) ^^ {
      case key ~ value => ("val", key -> value)
    }
  }

  private def shapeVARPropertie: Parser[(String, (String, String))] = {
    ("var" ~ "[" ~> ident <~ "]") ~ ("->" ~> ident) ^^ { case key ~ value => ("var", key -> value) }
  }

  private def shapeTextPropertie: Parser[(String, (String, String))] = shapeVALPropertie | shapeVARPropertie <~ ",?".r

  private def shapeCompartmentPropertie: Parser[(String, (String, String))] = {
    ("nest" ~> ident) ~ ("->" ~> ident) <~ ",?".r ^^ { case key ~ value => ("nest", key -> value) }
  }

  private def diagramShape: Parser[(String, PropsAndComps)] = {
    ("shape" ~ ":" ~> ident) ~ (("(" ~> rep((shapeTextPropertie | shapeCompartmentPropertie) <~ ",?".r) <~ ")") ?) ^^ {
      case shapeReference ~ propertiesAndCompartments =>
        ("shape", PropsAndComps(shapeReference, propertiesAndCompartments))
    }
  }

  private def node: Parser[(String, NodeSketch)] = {
    ("node" ~> ident) ~
      ("for" ~> ident) ~
      (("(" ~ "style" ~ ":" ~> ident <~ ")") ?) ~
      ("{" ~> rep(diagramShape | palette | container | onCreate | onUpdate | onDelete | actions) <~ "}") ^^ {
      case name ~ mcoreElement ~ corporatestyle ~ args =>
        createNodeSketch(name, mcoreElement, corporatestyle, args)
    }
  }

  private def createNodeSketch(name: String, mcoreElement: String, corporatestyle: Option[String], args: List[(String, Serializable)]): (String, NodeSketch) = {
    val corporateStyle: Option[Style] = corporatestyle.flatMap(s => IDtoStyle(s)(cache))
    var shap: Option[PropsAndComps] = None
    var pal: Option[String] = None
    var con: Option[String] = None
    var onCr: Option[(ActionBlock, String)] = None
    var onUp: Option[ActionBlock] = None
    var onDe: Option[ActionBlock] = None
    var actions: List[Action] = List()
    var actionIncludes: Option[ActionInclude] = None
    for {argument <- args} {
      argument._1 match {
        case "shape" => shap = Some(argument._2.asInstanceOf[PropsAndComps])
        case "palette" => pal = Some(argument._2.asInstanceOf[String])
        case "container" => con = Some(argument._2.asInstanceOf[String])
        case "onCreate" =>
          val tmp = argument._2.asInstanceOf[(Option[ActionBlock], Option[String])]
          onCr = Some(tmp._1.get, tmp._2.get)
        case "onUpdate" => onUp = argument._2.asInstanceOf[Option[ActionBlock]]
        case "onDelete" => onDe = argument._2.asInstanceOf[Option[ActionBlock]]
        case "actions" =>
          actions = argument._2.asInstanceOf[(ActionInclude, List[Action])]._2
          actionIncludes = Some(argument._2.asInstanceOf[(ActionInclude, List[Action])]._1)
      }
    }
    ("node", NodeSketch(name, mcoreElement, corporateStyle, shap, pal, con, onCr, onUp, onDe, actions, actionIncludes))
  }

  case class NodeSketch(
      name: String,
      mcoreElement: String,
      style: Option[Style] = None,
      // node-block
      shape: Option[PropsAndComps] = None,
      palette: Option[String] = None,
      container: Option[String] = None,
      onCreate: Option[(ActionBlock, String)] = None,
      onUpdate: Option[ActionBlock] = None,
      onDelete: Option[ActionBlock] = None,
      actions: List[Action] = List(),
      actionIncludes: Option[ActionInclude] = None
  ) {
    def toNode(diagramStyle: Option[Style], cache: Cache): Node = {
      val corporateStyle: Option[Style] = Style.generateChildStyle(cache, diagramStyle, style)
      val mClass = metaMapMClass.get(mcoreElement)
      val diagramShape: Option[DiaShape] =
        shape.map(s => new DiaShape(corporateStyle, s.ref, s.propertiesAndCompartments, cache, mClass.get, metaModelE))

      val onCr = onCreate.flatMap(oc => mClass.get.attributes.find(_.name == oc._2).map(attr => OnCreate(oc._1, attr)))

      val onUp = onUpdate.map(OnUpdate)
      val onDe = onDelete.map(OnDelete)

      val cont = container.flatMap(c => metaMapMReference.get(c))
      Node(name, mClass.get, corporateStyle, diagramShape, palette,
        cont, onCr, onUp, onDe, actions, actionIncludes)
    }
  }

  private def diagramConnection: Parser[PropsAndComps] = {
    type diaConnection = de.htwg.zeta.server.generator.model.diagram.edge.Connection
    ("connection" ~ ":" ~> ident) ~
      (("(" ~> rep(shapeTextPropertie) <~ ")") ?) ^^ {
      case connectionName ~ properties => PropsAndComps(connectionName, properties)
    }
  }

  private def edge: Parser[(String, EdgeSketch)] = {
    type diaConnection = de.htwg.zeta.server.generator.model.diagram.edge.Connection
    ("edge" ~> ident) ~
      ("for" ~> ident) ~
      (("(" ~ "style" ~ ":" ~> ident <~ ")") ?) ~
      ("{" ~> diagramConnection) ~
      ("from" ~ ":" ~> ident) ~
      ("to" ~ ":" ~> ident) ~
      (rep(palette | container | onCreate | onUpdate | onDelete | actions) <~ "}") ^^ {
      case edgeName ~ mcoreElement ~ styleOpt ~ diaCon ~ from ~ to ~ args =>
        createEdgeSketch(styleOpt, edgeName, mcoreElement, diaCon, from, to, args)
    }
  }

  private def createEdgeSketch(
    styleOpt: Option[String],
    name: String,
    mcoreElement: String,
    diaCon: PropsAndComps,
    from: String,
    to: String,
    args: List[(String, Serializable)]): (String, EdgeSketch) = {

    val style: Option[Style] = styleOpt.flatMap(s => parser.IDtoStyle(s)(cache))
    var pal: Option[String] = None
    var con: Option[String] = None
    var onCr: Option[(ActionBlock, String)] = None
    var onUp: Option[ActionBlock] = None
    var onDe: Option[ActionBlock] = None
    var actions: List[Action] = List()
    var actionIncludes: Option[ActionInclude] = None

    for {argument <- args} {
      argument._1 match {
        case "palette" => pal = Some(argument._2.asInstanceOf[String])
        case "container" => con = Some(argument._2.asInstanceOf[String])
        case "onCreate" => onCr = Some(argument._2.asInstanceOf[(ActionBlock, String)])
        case "onUpdate" => onUp = argument._2.asInstanceOf[Option[ActionBlock]]
        case "onDelete" => onDe = argument._2.asInstanceOf[Option[ActionBlock]]
        case "actions" =>
          actions = argument._2.asInstanceOf[(ActionInclude, List[Action])]._2
          actionIncludes = Some(argument._2.asInstanceOf[(ActionInclude, List[Action])]._1)
      }
    }
    ("edge", EdgeSketch(name, mcoreElement, style, diaCon, from, to, pal, con, onCr, onUp, onDe, actions, actionIncludes))
  }

  case class EdgeSketch(
      name: String,
      mReferenceName: String,
      style: Option[Style] = None,
      // edge-Block
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
    def toEdge(diagramStyle: Option[Style], cache: Cache): Edge = {
      val corporateStyle: Option[Style] = Style.generateChildStyle(cache, diagramStyle, style)
      val diagramConnection: diaConnection = new diaConnection(corporateStyle, connection, cache, metaMapMReference(mReferenceName))

      val mReference = metaMapMReference.get(mReferenceName)
      val fromClass = metaMapMClass.get(from)
      val toClass = metaMapMClass.get(to)

      val onCr = onCreate.flatMap(oc => mReference.get.attributes.find(_.name == oc._2).map(attr => OnCreate(oc._1, attr)))

      val onUp = onUpdate.map(OnUpdate)
      val onDe = onDelete.map(OnDelete)

      val cont = container.flatMap(c => metaMapMReference.get(c))
      Edge(name, mReference.get, corporateStyle, diagramConnection, fromClass.get, toClass.get, palette, cont, onCr, onUp, onDe, actions, actionIncludes)
    }
  }

  private def nodeOrEdge: Parser[(String, Product with scala.Serializable)] = node | edge

  private def sprayDiagram: Parser[Option[Diagram]] = {
    ("diagram" ~> ident) ~
      ("for" ~> ident) ~
      ("(" ~ "style" ~ ":" ~> ident <~ ")").? ~
      ("{" ~> rep(actionGroup | nodeOrEdge) <~ "}") ^^ {
      case name ~ metaModelName ~ style ~ arguments =>
        val actionGroups = arguments.filter(i => i._1 == "actionGroup").map(i => i._2.asInstanceOf[ActionGroup].name -> i._2.asInstanceOf[ActionGroup]).toMap

        val nodes: List[Node] = arguments.collect {
          case (typ, node: NodeSketch) if typ == "node" => node.toNode(style.flatMap(s => IDtoStyle(s)(cache)), cache)
        }
        val edges: List[Edge] = arguments.collect {
          case (typ, edge: EdgeSketch) if typ == "edge" => edge.toEdge(style.flatMap(s => IDtoStyle(s)(cache)), cache)
        }

        if (metaModelE.metaModel.name == metaModelName) {
          Some(Diagram(name, actionGroups, nodes, edges, OptionToStyle(style)(cache), metaModelE, cache))
        } else {
          None
        }
      case c: Any =>
        info(c.toString())
        Some(Diagram("test", Map(), List(), List(), None, null, cache))
    }
  }

  private def sprayDiagrams: Parser[List[Option[Diagram]]] = rep(sprayDiagram)

  def parseDiagram(e: String): List[Option[Diagram]] = parseAll(sprayDiagrams, trimRight(e)).get

  /* ------------------------------------------------------------------------------------------ */

  private def trimRight(s: String): String = s.replaceAll("\\/\\/.+", "").split("\n").map(s => s.trim + "\n").mkString
}

trait Sketch {
  val name: String
  val style: Option[Style]
}


/**
 * ShapeSketch is a sketch of a Shape, only used for parsing a Shape String ans temporarily save all
 * the attributes in a struct for later compilation into a shape
 */
case class ShapeSketch(
    override val name: String,
    override val style: Option[Style],
    parents: Option[List[String]],
    attrs: List[(String, String)],
    geos: List[GeoModel],
    descr: Option[(String, String)],
    anch: Option[String],
    cache: Cache
) extends Sketch {
  cache + this

  def toShape(corporateStyle: Option[Style]) = Shape(name, parents, Style.generateChildStyle(cache, corporateStyle, style), attrs, geos, descr, anch, cache)
}

case class PropsAndComps(ref: String, propertiesAndCompartments: Option[List[(String, (String, String))]])

/**
 * GeoModel is a sketch of a GeometricModel, only used for parsing a geometricModel string and temporarily
 * save all the attributes in a struct for later compilation into a GeometricModel
 */
case class GeoModel(typ: String, style: Option[Style], attributes: List[String], children: List[GeoModel], hierarchyCashe: Cache) {

  def parse(parentGeometricModel: Option[GeometricModel], parentStyle: Option[Style]): Option[GeometricModel] = {
    typ match {
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
}

/**
 * PlacingSketch is a sketch of a placing, only used for parsing a Connection
 */
case class PlacingSketch(position: String, shape: GeoModel)

case class ConnectionSketch(
    override val name: String,
    override val style: Option[Style],
    connection_type: Option[String],
    anoStyle: Option[String],
    placing: List[PlacingSketch]
) extends Sketch {
  def toConnection(corporateStyle: Option[Style], cache: Cache): Option[Connection] = {
    Connection(name, Style.generateChildStyle(cache, corporateStyle, style), connection_type, anoStyle, placing, cache)
  }
}
