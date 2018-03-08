package de.htwg.zeta.parser.shape

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
import de.htwg.zeta.parser.ReferenceCollector
import de.htwg.zeta.parser.check.ErrorChecker
import de.htwg.zeta.parser.shape.check.CheckDuplicateShapes
import de.htwg.zeta.parser.shape.check.CheckEdgesForUndefinedConceptElements
import de.htwg.zeta.parser.shape.check.CheckNodesForUndefinedConceptElements
import de.htwg.zeta.parser.shape.check.CheckUndefinedEdges
import de.htwg.zeta.parser.shape.check.CheckUndefinedStyles
import de.htwg.zeta.parser.shape.parsetree.EdgeAttributes.Placing
import de.htwg.zeta.parser.shape.parsetree.EdgeParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.EllipseParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
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

  private def checkForErrors(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style], concept: Concept): List[String] =
    ErrorChecker()
      .add(CheckDuplicateShapes(shapeParseTrees), ids => s"The following shapes are defined multiple times: $ids")
      .add(CheckUndefinedEdges(shapeParseTrees), ids => s"The following edges are referenced but not defined: $ids")
      .add(CheckUndefinedStyles(shapeParseTrees, styles), ids => s"The following styles are referenced but not defined: $ids")
      .add(CheckNodesForUndefinedConceptElements(shapeParseTrees, concept), errors => s"$errors")
      .add(CheckEdgesForUndefinedConceptElements(shapeParseTrees, concept), errors => s"$errors")
      .run()


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
