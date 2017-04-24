package generator.generators.shape

import java.nio.file.Files
import java.nio.file.Paths

import generator.model.diagram.node.Node
import generator.parser.Cache
import models.file.File

/**
 * The ShapeGenerator Object
 */
object ShapeGenerator {

  val JOINTJS_SHAPE_FILENAME = "shape.js"
  val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
  val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
  val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"

  /**
   * creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js
   */
  def doGenerate(cache: Cache, location: String, nodes: List[Node]): Unit = {
    doGenerateFile(cache, location, nodes)
      .foreach(f => Files.write(Paths.get(f.name), f.content.getBytes))
  }

  def doGenerateFile(cache: Cache, location: String, nodes: List[Node]): List[File] = {
    val DEFAULT_SHAPE_LOCATION = location
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data)

    // Shapes
    val shape = File(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_FILENAME, GeneratorShapeDefinition.generate(shapes, packageName))

    // ConnectionStyle
    val connectionStyle = File(DEFAULT_SHAPE_LOCATION + JOINTJS_CONNECTION_FILENAME, GeneratorConnectionDefinition.generate(cache.connections.values))

    // Inspector
    val inspector = File(DEFAULT_SHAPE_LOCATION + JOINTJS_INSPECTOR_FILENAME, GeneratorInspectorDefinition.generate(shapes, packageName, attrs, nodes))

    // ElementAndInlineStyle
    val elementAndInlineStyle = File(DEFAULT_SHAPE_LOCATION + JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME, GeneratorShapeAndInlineStyle.generate(shapes, packageName, attrs))

    List(shape, connectionStyle, inspector, elementAndInlineStyle)
  }

}

