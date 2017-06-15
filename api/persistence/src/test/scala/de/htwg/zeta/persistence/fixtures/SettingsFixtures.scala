package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.document.DockerSettings
import de.htwg.zeta.common.models.document.JobSettings
import de.htwg.zeta.common.models.entity.Settings


object SettingsFixtures {

  val entity1 = Settings(
    id = UUID.randomUUID,
    owner = UUID.randomUUID,
    jobSettings = JobSettings(
      maxRunning = 1,
      maxPending = 2,
      docker = DockerSettings(
        cpuShares = 1,
        cpuQuota = 2
      )
    )
  )

  val entity2 = Settings(
    id = UUID.randomUUID,
    owner = UUID.randomUUID,
    jobSettings = JobSettings(
      maxRunning = 3,
      maxPending = 0,
      docker = DockerSettings(
        cpuShares = 2,
        cpuQuota = 3
      )
    )
  )

  val entity2Updated: Settings = entity2.copy(owner = UUID.randomUUID)

  val entity3 = Settings(
    id = UUID.randomUUID,
    owner = UUID.randomUUID,
    jobSettings = JobSettings(
      maxRunning = 0,
      maxPending = 2,
      docker = DockerSettings(
        cpuShares = 3,
        cpuQuota = 3
      )
    )
  )

}
