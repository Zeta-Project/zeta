package de.htwg.zeta.parser.shape

import scala.collection.mutable.ListBuffer
import scalaz.Failure
import scalaz.Success
import scalaz.Validation

import de.htwg.zeta.common.model.shape
import de.htwg.zeta.common.model.shape.Edge
import de.htwg.zeta.common.model.shape.Node
import de.htwg.zeta.common.model.shape.Resizing
import de.htwg.zeta.common.model.shape.Size
import de.htwg.zeta.common.model.shape.geomodel
import de.htwg.zeta.common.model.shape.geomodel.Align
import de.htwg.zeta.common.model.shape.geomodel.Ellipse
import de.htwg.zeta.common.model.shape.geomodel.GeoModel
import de.htwg.zeta.common.model.shape.geomodel.HorizontalLayout
import de.htwg.zeta.common.model.shape.geomodel.Line
import de.htwg.zeta.common.model.shape.geomodel.Point
import de.htwg.zeta.common.model.shape.geomodel.Polygon
import de.htwg.zeta.common.model.shape.geomodel.Polyline
import de.htwg.zeta.common.model.shape.geomodel.Position
import de.htwg.zeta.common.model.shape.geomodel.Rectangle
import de.htwg.zeta.common.model.shape.geomodel.RepeatingBox
import de.htwg.zeta.common.model.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.model.shape.geomodel.StaticText
import de.htwg.zeta.common.model.shape.geomodel.TextField
import de.htwg.zeta.common.model.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.model.style.Style
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.UnitType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MReference
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.Check.Id
import de.htwg.zeta.parser.check.ErrorChecker
import de.htwg.zeta.parser.check.FindDuplicates
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.EllipseParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HasIdentifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HorizontalLayoutParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.LineParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.PolygonParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.PolylineParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RectangleParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RepeatingBoxParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RoundedRectangleParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.StatictextParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TextfieldParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.VerticalLayoutParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree
import de.htwg.zeta.parser.shape.parsetree.ShapeParseTree


object ShapeParseTreeTransformer {

  case class NodesAndEdges(nodes: List[Node], edges: List[Edge])

  def transform(shapeParseTrees: List[ShapeParseTree], styles: List[Style], concept: Concept): Validation[List[String], NodesAndEdges] = {
    val referencedStyles = ReferenceCollector[Style](styles, _.name)
    checkForErrors(shapeParseTrees, referencedStyles, concept) match {
      case Nil =>
        val (nodes, edges) = doTransformShapes(shapeParseTrees, referencedStyles, concept)
        Success(NodesAndEdges(nodes, edges))
      case errors: List[String] =>
        Failure(errors)
    }
  }

  private def checkForErrors(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style], concept: Concept): List[String] = {

    // check if there are any shapes with the same identifier
    def checkForDuplicateShapes(): List[Id] = {
      val findDuplicates = FindDuplicates[ShapeParseTree](_.identifier)
      findDuplicates(shapeParseTrees)
    }

    // check if there are nodes which reference an edge which is not defined
    def checkForUndefinedEdges(): List[Id] = {
      val definedEdges = shapeParseTrees.collect {
        case edge: EdgeParseTree => edge.identifier
      }.toSet
      val referencedEdges = shapeParseTrees.collect {
        case node: NodeParseTree => node.edges
      }.flatten.toSet
      referencedEdges.diff(definedEdges).toList
    }

    // check if there are styles referenced which are not defined
    def checkForUndefinedStyles(): List[Id] = {
      val referencedStyles = shapeParseTrees.collect {
        case node: NodeParseTree =>
          node.allGeoModels.flatMap(_.style).map(_.name) ++ node.style.map(_.name).toList
        case edge: EdgeParseTree =>
          edge.placings.map(_.geoModel).flatMap(_.style).map(_.name)
      }.flatten.toSet
      referencedStyles.diff(styles.identifiers().toSet).toList
    }

    def checkForUndefinedNodeConceptElements(): () => List[Id] = {
      val nodes = shapeParseTrees.collect { case n: NodeParseTree => n }
      () => checkNodesForUndefinedConceptElements(nodes, concept)
    }

    def checkForUndefinedEdgeConceptElements(): () => List[Id] = {
      val edges = shapeParseTrees.collect { case e: EdgeParseTree => e }
      () => checkEdgesForUndefinedConceptElements(edges, concept)
    }

    //noinspection ScalaStyle
    // this thing is a mess!
    def checkNodesForUndefinedConceptElements(nodeParseTrees: List[NodeParseTree], concept: Concept): List[Id] = {
      var errors = new ListBuffer[String]()

      // check if there is an attribute or method with name 'identifier' in class 'context'
      def isValidIdentifier(identifier: String, context: MClass): Boolean = {
        val maybeAttribute = context.attributes.find(_.name == identifier)
        val maybeMethod = context.methods.find(_.name == identifier)
        (maybeAttribute, maybeMethod) match {
          // attribute type / method return type may not be unit!
          case (Some(attr), _) if attr.typ != UnitType => true
          case (_, Some(method)) if method.returnType != UnitType => true
          case (_, _) => false
        }
      }

      // check if a specified identifier is in a valid context
      def checkConceptIdentifier(geoModel: GeoModelParseTree with HasIdentifier, contexts: Map[String, MClass]): Unit = {
        val (prefix, identifier) = geoModel.identifier.split
        contexts.get(prefix) match {
          case None =>
            errors += s"Illegal prefix '$prefix' specified!"
          case Some(context) if !isValidIdentifier(identifier, context) =>
            errors += s"Textfield identifier '$identifier' not found or it has return type 'Unit'!"
          case _ => // identifier is valid in the given context
        }
        geoModel.children.foreach(child => check(child, contexts))
      }

      def getReferenceOrThrow(referenceName: String): MReference = {
        concept.references.find(_.name == referenceName)
          .getOrElse(throw new Exception(s"Concept model is invalid! Reference '$referenceName' does not exist!"))
      }

      def getClassOrThrow(className: String): MClass = {
        concept.classes.find(_.name == className)
          .getOrElse(throw new Exception(s"Concept model is invalid! Class '$className' does not exist!"))
      }

      def checkConceptReference(repeatingBox: RepeatingBoxParseTree, contexts: Map[String, MClass]): Unit = {

        // check if there is already a context with this prefix
        val newPrefix = repeatingBox.foreach.as
        if (contexts.contains(newPrefix)) {
          errors += s"Prefix '$newPrefix' already defined in outer scope!"
          return
        }

        val (prefix, referenceName) = repeatingBox.foreach.each.split
        contexts.get(prefix) match {
          case None =>
            errors += s"RepeatingBox reference name '$referenceName' not found!"
          case Some(context) if !context.outputReferenceNames.contains(referenceName) =>
            errors += s"Concept class '${context.name}' has no reference named '$referenceName'!"
          case Some(_) =>
            val reference = getReferenceOrThrow(referenceName)
            val newContext = getClassOrThrow(reference.targetClassName)
            val updatedContexts = contexts + (newPrefix -> newContext)
            repeatingBox.children.foreach(child => check(child, updatedContexts))
        }
      }

      def check(geoModel: GeoModelParseTree, contexts: Map[String, MClass]): Unit = geoModel match {
        case geoModel: HasIdentifier =>
          checkConceptIdentifier(geoModel, contexts)
        case repeatingBox: RepeatingBoxParseTree =>
          checkConceptReference(repeatingBox, contexts)
        case other: GeoModelParseTree =>
          other.children.foreach(child => check(child, contexts))
      }

      for {node <- nodeParseTrees} {
        val maybeClass = concept.classes.find(_.name == node.conceptClass)
        maybeClass match {
          case None =>
            errors += s"Concept class '${node.conceptClass}' for node '${node.identifier}' not found!"
          case Some(context) =>
            val defaultPrefix = ""
            val contexts = Map[String, MClass](defaultPrefix -> context)
            node.geoModels.foreach(geoModel => check(geoModel, contexts))
        }
      }

      errors.toList
    }

    // check if there are edges which reference undefined concept elements
    def checkEdgesForUndefinedConceptElements(nodeParseTrees: List[EdgeParseTree], concept: Concept): List[String] = {
      Nil // TODO
    }

    ErrorChecker()
      .add(ids => s"The following shapes are defined multiple times: $ids", checkForDuplicateShapes)
      .add(ids => s"The following edges are referenced but not defined: $ids", checkForUndefinedEdges)
      .add(ids => s"The following styles are referenced but not defined: $ids", checkForUndefinedStyles)
      .add(ids => s"Error in concept check TODO: $ids", checkForUndefinedNodeConceptElements()) // TODO
      .add(ids => s"Error in concept check TODO: $ids", checkForUndefinedEdgeConceptElements()) // TODO
      .run()
  }


  private def doTransformShapes(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style], concept: Concept): (List[Node], List[Edge]) = {
    val edges = doTransformEdges(shapeParseTrees.collect { case t: EdgeParseTree => t }, styles, concept)
    val nodes = doTransformNodes(shapeParseTrees.collect { case t: NodeParseTree => t }, edges, styles, concept)
    (nodes, edges)
  }

  private def doTransformEdges(edgeParseTrees: List[EdgeParseTree], styles: ReferenceCollector[Style], concept: Concept): List[Edge] = {
    edgeParseTrees.map(n => {
      Edge(
        n.identifier,
        n.conceptConnection,
        n.conceptTarget.target,
        n.placings.map(doTransformGeoModelPlacing(_, styles))
      )
    })
  }

  private def doTransformNodes(nodeParseTrees: List[NodeParseTree], edges: List[Edge], styles: ReferenceCollector[Style], concept: Concept): List[Node] = {
    nodeParseTrees.map(n => {
      Node(
        n.identifier,
        n.conceptClass,
        List(),
        Size(0, 0, n.sizeMax.width, n.sizeMin.width, n.sizeMax.height, n.sizeMin.height), // TODO
        n.style.fold(Style.defaultStyle)(s => styles.!(s.name)),
        Resizing(
          n.resizing.map(_.horizontal).getOrElse(Resizing.defaultHorizontal),
          n.resizing.map(_.vertical).getOrElse(Resizing.defaultVertical),
          n.resizing.map(_.proportional).getOrElse(Resizing.defaultProportional)
        ),
        n.geoModels.map(doTransformGeoModel(_, styles))
      )
    })
  }

  private def doTransformGeoModel(geoModel: GeoModelParseTree, styles: ReferenceCollector[Style]): GeoModel = {
    geoModel match {
      case m: EllipseParseTree => doTransformGeoModel(m, styles)
      case m: TextfieldParseTree => doTransformGeoModel(m, styles)
      case m: StatictextParseTree => doTransformGeoModel(m, styles)
      case m: RepeatingBoxParseTree => doTransformGeoModel(m, styles)
      case m: LineParseTree => doTransformGeoModel(m, styles)
      case m: PolylineParseTree => doTransformGeoModel(m, styles)
      case m: PolygonParseTree => doTransformGeoModel(m, styles)
      case m: RectangleParseTree => doTransformGeoModel(m, styles)
      case m: RoundedRectangleParseTree => doTransformGeoModel(m, styles)
      case m: HorizontalLayoutParseTree => doTransformGeoModel(m, styles)
      case m: VerticalLayoutParseTree => doTransformGeoModel(m, styles)
    }
  }

  private def doTransformGeoModelSize(size: GeoModelAttributes.Size): geomodel.Size = geomodel.Size(
    width = size.width,
    height = size.height
  )

  private def doTransformGeoModelCurve(curve: GeoModelAttributes.Curve): geomodel.Size = geomodel.Size(
    width = curve.width,
    height = curve.height
  )

  private def doTransformGeoModelPosition(position: GeoModelAttributes.Position) = Position(
    x = position.x,
    y = position.y
  )

  private def doTransformGeoModelPoint(point: GeoModelAttributes.Point) = Point(
    x = point.x,
    y = point.y
  )

  private def doTransformGeoModelPlacing(placing: Placing, styles: ReferenceCollector[Style]): shape.Placing = shape.Placing(
    style = placing.style.fold(Style.defaultStyle)(s => styles.!(s.name)),
    position = shape.Position(
      // TODO thats not correct
      distance = 1,
      offset = 1.0
    ),
    geoModel = doTransformGeoModel(placing.geoModel, styles)
  )

  private def doTransformGeoModelAlign(align: GeoModelAttributes.Align) = Align(
    vertical = align.vertical match {
      case _: GeoModelAttributes.VerticalAlignment.top.type => Align.Vertical.top
      case _: GeoModelAttributes.VerticalAlignment.middle.type => Align.Vertical.middle
      case _: GeoModelAttributes.VerticalAlignment.bottom.type => Align.Vertical.bottom
    },
    horizontal = align.horizontal match {
      case _: GeoModelAttributes.HorizontalAlignment.left.type => Align.Horizontal.left
      case _: GeoModelAttributes.HorizontalAlignment.middle.type => Align.Horizontal.middle
      case _: GeoModelAttributes.HorizontalAlignment.right.type => Align.Horizontal.right
    }
  )

  private def doTransformGeoModel(geoModel: EllipseParseTree, styles: ReferenceCollector[Style]): Ellipse = Ellipse(
    size = doTransformGeoModelSize(geoModel.size),
    position = doTransformGeoModelPosition(geoModel.position),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: TextfieldParseTree, styles: ReferenceCollector[Style]): TextField = TextField(
    identifier = geoModel.identifier.name,
    size = doTransformGeoModelSize(geoModel.size),
    position = doTransformGeoModelPosition(geoModel.position),
    editable = geoModel.editable.fold(TextField.default.editable)(_.editable),
    multiline = geoModel.multiline.fold(TextField.default.multiline)(_.multiline),
    align = geoModel.align.fold(TextField.default.align)(doTransformGeoModelAlign),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: StatictextParseTree, styles: ReferenceCollector[Style]): StaticText = StaticText(
    text = geoModel.text.text,
    size = doTransformGeoModelSize(geoModel.size),
    position = doTransformGeoModelPosition(geoModel.position),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: RepeatingBoxParseTree, styles: ReferenceCollector[Style]): RepeatingBox = RepeatingBox(
    editable = geoModel.editable.editable,
    forEach = geoModel.foreach.each.name,
    forAs = geoModel.foreach.as,
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: LineParseTree, styles: ReferenceCollector[Style]): Line = Line(
    startPoint = doTransformGeoModelPoint(geoModel.startPoint),
    endPoint = doTransformGeoModelPoint(geoModel.endPoint),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: PolylineParseTree, styles: ReferenceCollector[Style]): Polyline = Polyline(
    points = geoModel.points.map(doTransformGeoModelPoint),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: PolygonParseTree, styles: ReferenceCollector[Style]): Polygon = Polygon(
    points = geoModel.points.map(doTransformGeoModelPoint),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: RectangleParseTree, styles: ReferenceCollector[Style]): Rectangle = Rectangle(
    size = doTransformGeoModelSize(geoModel.size),
    position = doTransformGeoModelPosition(geoModel.position),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: RoundedRectangleParseTree, styles: ReferenceCollector[Style]): RoundedRectangle = RoundedRectangle(
    size = doTransformGeoModelSize(geoModel.size),
    curve = doTransformGeoModelCurve(geoModel.curve),
    position = doTransformGeoModelPosition(geoModel.position),
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: HorizontalLayoutParseTree, styles: ReferenceCollector[Style]): HorizontalLayout = HorizontalLayout(
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

  private def doTransformGeoModel(geoModel: VerticalLayoutParseTree, styles: ReferenceCollector[Style]): VerticalLayout = VerticalLayout(
    childGeoModels = geoModel.children.map(doTransformGeoModel(_, styles)),
    style = geoModel.style.fold(Style.defaultStyle)(s => styles.!(s.name))
  )

}
