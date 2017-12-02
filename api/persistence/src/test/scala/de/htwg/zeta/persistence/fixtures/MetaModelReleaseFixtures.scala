package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.GraphicalDslRelease


object MetaModelReleaseFixtures {

  val entity1 = GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease1",
    concept = GraphicalDslFixtures.concept1,
    diagram = "diagram1",
    shape = "shape1",
    style = "style1",
    version = "version1"
  )

  val entity2 = GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease2",
    concept = GraphicalDslFixtures.concept2,
    diagram = "diagram2",
    shape = "shape2",
    style = "style2",
    version = "version2"
  )

  val entity2Updated: GraphicalDslRelease = entity2.copy(version = "version2Updated")

  val entity3 =  GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease3",
    concept = GraphicalDslFixtures.concept1,
    diagram = "diagram3",
    shape = "shape3",
    style = "style3",
    version = "version3"
  )

}
