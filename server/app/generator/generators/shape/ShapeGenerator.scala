package generator.generators.shape


import java.nio.file._

import generator.model.diagram.node.Node
import generator.parser.Cache

/**
  * The ShapeGenerator Object
  */
object ShapeGenerator {

  val JOINTJS_SHAPE_FILENAME = "shape.js"
  val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
  val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
  val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"

  /** creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js*/
  def doGenerate(cache: Cache, location: String, nodes: List[Node]) {
    val DEFAULT_SHAPE_LOCATION = location
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s=> s.data)

    // Shapes
    val jointJSShapeContent = GeneratorShapeDefinition.generate(shapes, packageName)
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_FILENAME), jointJSShapeContent.getBytes)

    //ConnectionStyle
    val jointJsConnectionContent = GeneratorConnectionDefinition.generate(cache.connections.values)
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_CONNECTION_FILENAME), jointJsConnectionContent.getBytes)

    // Inspector
    val jointJSInspectorContent = GeneratorInspectorDefinition.generate(shapes, packageName, attrs, nodes)

    //  Generate InspectorFile
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_INSPECTOR_FILENAME), jointJSInspectorContent.getBytes)

    // ElementAndInlineStyle
    val jointJSShapeAndInlineStyleContent = GeneratorShapeAndInlineStyle.generate(shapes,packageName, attrs)
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME), jointJSShapeAndInlineStyleContent.getBytes)

  }

}
