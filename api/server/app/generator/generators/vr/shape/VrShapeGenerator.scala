package generator.generators.vr.shape


import java.nio.file.Files
import java.nio.file.Paths

import generator.model.diagram.node.Node
import generator.parser.Cache
import models.file.File


/**
 * The ShapeGenerator Object
 */
object VrShapeGenerator {

  def doGenerate(cache: Cache, location: String, nodes: List[Node]): Unit = {
    doGenerateFile(cache, location, nodes).map(f => Files.write(Paths.get(f.name), f.content.getBytes()))
  }

  def doGenerateFile(cache: Cache, location: String, nodes: List[Node]): List[File] = {
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data).toList

    val shapeDefinition = VrGeneratorShapeDefinition.doGenerateFile(shapes, location)
    val connectionDefinition = VrGeneratorConnectionDefinition.doGenerateFile(cache.connections.values, location)

    shapeDefinition ::: connectionDefinition
  }


}
