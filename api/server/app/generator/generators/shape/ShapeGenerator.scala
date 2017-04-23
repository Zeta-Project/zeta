package generator.generators.shape

import java.nio.file._

import generator.model.diagram.node.Node
import generator.parser.Cache

/**
  * The ShapeGenerator Object
  */
object ShapeGenerator {

  val JOINTJS_SHAPE_FILENAME = "shape.js"
  val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
  val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
  val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"

  /** creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js */
  def doGenerate(cache: Cache, location: String, nodes: List[Node]): Unit = {
    val DEFAULT_SHAPE_LOCATION = location
    val shapeGen = doGenerateFile(cache, nodes)

    // Shapes
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_FILENAME), shapeGen.shape.getBytes)

    //ConnectionStyle
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_CONNECTION_FILENAME), shapeGen.connectionStyle.getBytes)

    // Inspector
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_INSPECTOR_FILENAME), shapeGen.inspector.getBytes)

    // ElementAndInlineStyle
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME), shapeGen.elementAndInlineStyle.getBytes)
  }

  def doGenerateFile(cache: Cache, nodes: List[Node]): ShapeGenerator = {
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data)

    // Shapes
    val shape = GeneratorShapeDefinition.generate(shapes, packageName)

    //ConnectionStyle
    val connectionStyle = GeneratorConnectionDefinition.generate(cache.connections.values)

    // Inspector
    val inspector = GeneratorInspectorDefinition.generate(shapes, packageName, attrs, nodes)

    // ElementAndInlineStyle
    val elementAndInlineStyle = GeneratorShapeAndInlineStyle.generate(shapes, packageName, attrs)

    ShapeGenerator(shape, connectionStyle, inspector, elementAndInlineStyle)
  }

}

case class ShapeGenerator(shape: String, connectionStyle: String, inspector: String, elementAndInlineStyle: String)