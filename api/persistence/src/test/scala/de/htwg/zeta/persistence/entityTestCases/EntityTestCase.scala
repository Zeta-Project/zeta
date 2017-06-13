package de.htwg.zeta.persistence.entityTestCases

import models.entity.Entity

trait EntityTestCase[E <: Entity] {
  val entity1: E
  val entity2: E
  val entity2Updated: E
  val entity3: E
}
