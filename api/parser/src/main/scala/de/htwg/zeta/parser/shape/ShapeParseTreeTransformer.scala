package de.htwg.zeta.parser.shape

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.gdsl.shape
import de.htwg.zeta.common.models.project.gdsl.shape.Edge
import de.htwg.zeta.common.models.project.gdsl.shape.Node
import de.htwg.zeta.common.models.project.gdsl.shape.PlacingPosition
import de.htwg.zeta.common.models.project.gdsl.shape.Resizing
import de.htwg.zeta.common.models.project.gdsl.shape.Shape
import de.htwg.zeta.common.models.project.gdsl.shape.Size
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Align
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Ellipse
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Triangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Hexagon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Octagon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Diamond
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Star8
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Star6
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Star5
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Trapez
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.ShearedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.GeoModel
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.HorizontalLayout
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Line
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Point
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polygon
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Polyline
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Position
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.Rectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RepeatingBox
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.RoundedRectangle
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.StaticText
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.TextField
import de.htwg.zeta.common.models.project.gdsl.shape.geomodel.VerticalLayout
import de.htwg.zeta.common.models.project.gdsl.style.Style
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
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TriangleParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.HexagonParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.OctagonParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.DiamondParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.Star8ParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.Star5ParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.Star6ParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TrapezParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.ShearedRectangleParseTree
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
import scalaz.Failure
import scalaz.Success
import scalaz.Validation


object ShapeParseTreeTransformer {

  def transform(shapeParseTrees: List[ShapeParseTree], styles: List[Style], concept: Concept): Validation[List[String], Shape] = {
    val referencedStyles = ReferenceCollector[Style](styles, _.name)
    checkForErrors(shapeParseTrees, referencedStyles, concept) match {
      case Nil =>
        val (nodes, edges) = transformShapes(shapeParseTrees, referencedStyles, concept)
        Success(Shape(nodes, edges))
      case errors: List[String] =>
        Failure(errors)
    }
  }

  private def checkForErrors(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style], concept: Concept): List[String] =
    ErrorChecker()
      .add(CheckDuplicateShapes(shapeParseTrees))
      .add(CheckUndefinedEdges(shapeParseTrees))
      .add(CheckUndefinedStyles(shapeParseTrees, styles))
      .add(CheckNodesForUndefinedConceptElements(shapeParseTrees, concept))
      .add(CheckEdgesForUndefinedConceptElements(shapeParseTrees, concept))
      .run()


  private def transformShapes(shapeParseTrees: List[ShapeParseTree], styles: ReferenceCollector[Style], concept: Concept): (List[Node], List[Edge]) = {
    val edges = transformEdges(shapeParseTrees.collect { case t: EdgeParseTree => t }, styles, concept)
    val nodes = transformNodes(shapeParseTrees.collect { case t: NodeParseTree => t }, edges, styles, concept)
    (nodes, edges)
  }

  private def transformEdges(edgeParseTrees: List[EdgeParseTree], styles: ReferenceCollector[Style], concept: Concept): List[Edge] = {
    edgeParseTrees.map(n => {
      val style = n.style.fold(Style.defaultStyle)(s => styles.!(s.name))
      Edge(
        n.identifier,
        n.conceptConnection,
        n.conceptTarget.target,
        style,
        n.placings.map(transformGeoModelPlacing(_, style, styles))
      )
    })
  }

  private def transformNodes(nodeParseTrees: List[NodeParseTree], edges: List[Edge], styles: ReferenceCollector[Style], concept: Concept): List[Node] = {
    nodeParseTrees.map(n => {
      val style = n.style.fold(Style.defaultStyle)(s => styles.!(s.name))
      Node(
        n.identifier,
        n.conceptClass,
        edges.filter(e => n.edges.contains(e.name)),
        Size(0, 0, n.sizeMax.width, n.sizeMin.width, n.sizeMax.height, n.sizeMin.height), // TODO
        style,
        Resizing(
          n.resizing.map(_.horizontal).getOrElse(Resizing.defaultHorizontal),
          n.resizing.map(_.vertical).getOrElse(Resizing.defaultVertical),
          n.resizing.map(_.proportional).getOrElse(Resizing.defaultProportional)
        ),
        n.geoModels.map(transformGeoModel(_, style, styles))
      )
    })
  }

  private def transformGeoModel(geoModel: GeoModelParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): GeoModel = {
    geoModel match {
      case m: EllipseParseTree => transformGeoModel(m, parentStyle, styles)
      case m: TriangleParseTree => transformGeoModel(m, parentStyle, styles)
      case m: HexagonParseTree => transformGeoModel(m, parentStyle, styles)
      case m: OctagonParseTree => transformGeoModel(m, parentStyle, styles)
      case m: DiamondParseTree => transformGeoModel(m, parentStyle, styles)
      case m: Star8ParseTree => transformGeoModel(m, parentStyle, styles)
      case m: Star5ParseTree => transformGeoModel(m, parentStyle, styles)
      case m: Star6ParseTree => transformGeoModel(m, parentStyle, styles)
      case m: TrapezParseTree => transformGeoModel(m, parentStyle, styles)
      case m: ShearedRectangleParseTree => transformGeoModel(m, parentStyle, styles)
      case m: TextfieldParseTree => transformGeoModel(m, parentStyle, styles)
      case m: StatictextParseTree => transformGeoModel(m, parentStyle, styles)
      case m: RepeatingBoxParseTree => transformGeoModel(m, parentStyle, styles)
      case m: LineParseTree => transformGeoModel(m, parentStyle, styles)
      case m: PolylineParseTree => transformGeoModel(m, parentStyle, styles)
      case m: PolygonParseTree => transformGeoModel(m, parentStyle, styles)
      case m: RectangleParseTree => transformGeoModel(m, parentStyle, styles)
      case m: RoundedRectangleParseTree => transformGeoModel(m, parentStyle, styles)
      case m: HorizontalLayoutParseTree => transformGeoModel(m, parentStyle, styles)
      case m: VerticalLayoutParseTree => transformGeoModel(m, parentStyle, styles)
    }
  }

  private def transformGeoModelSize(size: GeoModelAttributes.Size): geomodel.Size = geomodel.Size(
    width = size.width,
    height = size.height
  )

  private def transformGeoModelCurve(curve: GeoModelAttributes.Curve): geomodel.Size = geomodel.Size(
    width = curve.width,
    height = curve.height
  )

  private def transformGeoModelPosition(position: GeoModelAttributes.Position) = Position(
    x = position.x,
    y = position.y
  )

  private def transformGeoModelPoint(point: GeoModelAttributes.Point) = Point(
    x = point.x,
    y = point.y
  )

  private def transformGeoModelPlacing(placing: Placing, parentStyle: Style, styles: ReferenceCollector[Style]): shape.Placing = {
    val style = placing.style.fold(parentStyle)(s => styles.!(s.name))
    shape.Placing(
      style = style,
      position = PlacingPosition(
        offset = placing.offset.offset
      ),
      geoModel = transformGeoModel(placing.geoModel, style, styles)
    )
  }

  private def transformGeoModelAlign(align: GeoModelAttributes.Align) = Align(
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

  private def transformGeoModel(geoModel: EllipseParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Ellipse = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Ellipse(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: TriangleParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Triangle = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Triangle(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: HexagonParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Hexagon = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Hexagon(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: OctagonParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Octagon = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Octagon(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: DiamondParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Diamond = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Diamond(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: Star5ParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Star5 = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Star5(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: Star6ParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Star6 = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Star6(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: TrapezParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Trapez = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Trapez(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: ShearedRectangleParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): ShearedRectangle= {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    ShearedRectangle(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: Star8ParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Star8 = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Star8(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: TextfieldParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): TextField = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    TextField(
      identifier = geoModel.identifier.name,
      textBody = geoModel.textBody.fold(TextField.default.textBody)(_.text),
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      editable = geoModel.editable.fold(TextField.default.editable)(_.editable),
      multiline = geoModel.multiline.fold(TextField.default.multiline)(_.multiline),
      align = geoModel.align.fold(TextField.default.align)(transformGeoModelAlign),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: StatictextParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): StaticText = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    StaticText(
      text = geoModel.text.text,
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: RepeatingBoxParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): RepeatingBox = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    RepeatingBox(
      editable = geoModel.editable.editable,
      forEach = geoModel.foreach.each.name,
      forAs = geoModel.foreach.as,
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: LineParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Line = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Line(
      startPoint = transformGeoModelPoint(geoModel.startPoint),
      endPoint = transformGeoModelPoint(geoModel.endPoint),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: PolylineParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Polyline = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Polyline(
      points = geoModel.points.map(transformGeoModelPoint),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: PolygonParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Polygon = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Polygon(
      points = geoModel.points.map(transformGeoModelPoint),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: RectangleParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): Rectangle = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    Rectangle(
      size = transformGeoModelSize(geoModel.size),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: RoundedRectangleParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): RoundedRectangle = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    RoundedRectangle(
      size = transformGeoModelSize(geoModel.size),
      curve = transformGeoModelCurve(geoModel.curve),
      position = transformGeoModelPosition(geoModel.position),
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: HorizontalLayoutParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): HorizontalLayout = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    HorizontalLayout(
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

  private def transformGeoModel(geoModel: VerticalLayoutParseTree, parentStyle: Style, styles: ReferenceCollector[Style]): VerticalLayout = {
    val style = geoModel.style.fold(parentStyle)(s => styles.!(s.name))
    VerticalLayout(
      childGeoModels = geoModel.children.map(transformGeoModel(_, style, styles)),
      style = style
    )
  }

}
