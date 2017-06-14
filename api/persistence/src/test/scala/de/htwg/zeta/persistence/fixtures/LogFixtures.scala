package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import models.entity.Log


object LogFixtures {

  val entity1 = Log(
    id = UUID.randomUUID,
    task = "task1",
    log = "log1",
    status = 1,
    date = "date1"
  )

  val entity2 = Log(
    id = UUID.randomUUID,
    task = "task2",
    log = "log2",
    status = 1,
    date = "date2"
  )

  val entity2Updated: Log = entity2.copy(status = 2)

  val entity3 = Log(
    id = UUID.randomUUID,
    task = "task3",
    log = "log3",
    status = 3,
    date = "date3"
  )

}
