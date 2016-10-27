package generator.generators.vr.shape

/**
  * Created by steffen on 25/10/16.
  */
import generator.model.diagram.node.Node
import generator.parser.Cache

/**
  * The ShapeGenerator Object
  */
object VrShapeGenerator {

  def doGenerate(cache: Cache, location: String, nodes: List[Node]) {
      VrGeneratorConnectionDefinition.generate(cache.connections.values, location)
  }

}
