package de.htwg.zeta.server.generator.generators.shape

import java.util.UUID

import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.server.model.result.Unreliable

/**
 * The ShapeGenerator Object
 */
object ShapeGenerator {

  private val jointsJsShapeFilename = "shape.js"
  private val jointsJsConnectionFilename = "connectionstyle.js"
  private val jointsJsInspectorFilename = "inspector.js"
  private val jointsJsShapeAndInlineStyleFilename = "elementAndInlineStyle.js"


  /**
   * creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js as Result
   */
  def doGenerateResult(cache: Cache, nodes: List[Node], metaModelId: UUID): Unreliable[List[File]] = {
    Unreliable(() => doGenerateGenerators(cache, nodes, metaModelId), "failed trying to create the Shape generators")
  }

  /**
   * creates the files shape.js, inspector.js, connectionstyle.js and elementAndInlineStyle.js
   */
  private def doGenerateGenerators(cache: Cache, nodes: List[Node], metaModelId: UUID): List[File] = {
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data)

    // Shapes
    val shape = File(metaModelId, jointsJsShapeFilename, GeneratorShapeDefinition.generate(shapes, packageName))

    // ConnectionStyle
    val connectionStyle = File(metaModelId, jointsJsConnectionFilename, GeneratorConnectionDefinition.generate(cache.connections.values))

    // Inspector
    val inspector = File(metaModelId, jointsJsInspectorFilename, GeneratorInspectorDefinition.generate(shapes, packageName, attrs, nodes))

    // ElementAndInlineStyle
    val elementAndInlineStyle = File(metaModelId, jointsJsShapeAndInlineStyleFilename,
      GeneratorShapeAndInlineStyle.generate(shapes, packageName, attrs))

    List(shape, connectionStyle, inspector, elementAndInlineStyle)
  }

}

