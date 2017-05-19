package de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics

/**
 * Created by julian on 19.10.15.
 * represents the collection a GeometricModel can have, including n other Geometric Models which should lay inside the parent-Model
 */
trait Wrapper {
  val children: List[GeometricModel]
}
