package de.htwg.zeta.server.generator.generators.shape

import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.parser.Cache
import models.file.File
import de.htwg.zeta.server.model.result.Result

/**
 * The ShapeGenerator Object
 */
object ShapeGenerator {

  private val JOINTJS_SHAPE_FILENAME = "shape.js"
  private val JOINTJS_CONNECTION_FILENAME = "connectionstyle.js"
  private val JOINTJS_INSPECTOR_FILENAME = "inspector.js"
  private val JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME = "elementAndInlineStyle.js"


  /**
   * creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js as Result
   */
  def doGenerateResult(cache: Cache, nodes: List[Node]): Result[List[File]] = {
    Result(() => doGenerateGenerators(cache, nodes), "failed trying to create the Shape generators")
  }

  /**
   * creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js
   */
  private def doGenerateGenerators(cache: Cache, nodes: List[Node]): List[File] = {
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data)

    // Shapes
    val shape = File(JOINTJS_SHAPE_FILENAME, GeneratorShapeDefinition.generate(shapes, packageName))

    // ConnectionStyle
    val connectionStyle = File(JOINTJS_CONNECTION_FILENAME, GeneratorConnectionDefinition.generate(cache.connections.values))

    // Inspector
    val inspector = File(JOINTJS_INSPECTOR_FILENAME, GeneratorInspectorDefinition.generate(shapes, packageName, attrs, nodes))

    // ElementAndInlineStyle
    val elementAndInlineStyle = File(JOINTJS_SHAPE_AND_INLINE_STYLE_FILENAME,
      GeneratorShapeAndInlineStyle.generate(shapes, packageName, attrs))

    List(shape, connectionStyle, inspector, elementAndInlineStyle)
  }

}

