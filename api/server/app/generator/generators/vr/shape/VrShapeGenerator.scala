package generator.generators.vr.shape


import generator.model.diagram.node.Node
import generator.parser.Cache
import models.file.File
import models.result.Result


/**
 * The ShapeGenerator Object
 */
object VrShapeGenerator {
  def doGenerateResult(cache: Cache, nodes: List[Node]): Result[List[File]] = {
    Result(() => cache.shapeHierarchy.nodeView.values.map(s => s.data).toList, "failed trying to create the vr generators")
      .flatMap(VrGeneratorShapeDefinition.doGenerateResult).flatMap(shapeDefinition => {
      VrGeneratorConnectionDefinition.doGenerateResult(cache.connections.values)
        .map(connectionDefinition => shapeDefinition ::: connectionDefinition)
    })
  }
}
