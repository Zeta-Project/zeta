package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.GraphicalDslRelease


object MetaModelReleaseFixtures {

  val entity1 = GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease1",
    metaModel = MetaModelEntityFixtures.metaModel1,
    dsl = MetaModelEntityFixtures.dsl1,
    version = "version1"
  )

  val entity2 = GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease2",
    metaModel = MetaModelEntityFixtures.metaModel2,
    dsl = MetaModelEntityFixtures.dsl2,
    version = "version2"
  )

  val entity2Updated: GraphicalDslRelease = entity2.copy(version = "version2Updated")

  val entity3 =  GraphicalDslRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease3",
    metaModel = MetaModelEntityFixtures.metaModel1,
    dsl = MetaModelEntityFixtures.dsl2,
    version = "version3"
  )

}
