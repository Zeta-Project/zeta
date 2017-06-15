package de.htwg.zeta.server.generator.generators.vr.shape


import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.parser.Cache
import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.server.model.result.Unreliable


/**
 * The ShapeGenerator Object
 */
object VrShapeGenerator {
  def doGenerateResult(cache: Cache, nodes: List[Node]): Unreliable[List[File]] = {
    Unreliable(() => cache.shapeHierarchy.nodeView.values.map(s => s.data).toList, "failed trying to create the vr generators")
      .flatMap(VrGeneratorShapeDefinition.doGenerateResult).flatMap(shapeDefinition => {
      VrGeneratorConnectionDefinition.doGenerateResult(cache.connections.values)
        .map(connectionDefinition => shapeDefinition ::: connectionDefinition)
    })
  }
}
