package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.MetaModelRelease


object MetaModelReleaseFixtures {

  val entity1 = MetaModelRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease1",
    metaModel = MetaModelEntityFixtures.metaModel1,
    dsl = MetaModelEntityFixtures.dsl1,
    version = "version1"
  )

  val entity2 = MetaModelRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease2",
    metaModel = MetaModelEntityFixtures.metaModel2,
    dsl = MetaModelEntityFixtures.dsl2,
    version = "version2"
  )

  val entity2Updated: MetaModelRelease = entity2.copy(version = "version2Updated")

  val entity3 =  MetaModelRelease(
    id = UUID.randomUUID,
    name = "metaModelRelease3",
    metaModel = MetaModelEntityFixtures.metaModel1,
    dsl = MetaModelEntityFixtures.dsl2,
    version = "version3"
  )

}
