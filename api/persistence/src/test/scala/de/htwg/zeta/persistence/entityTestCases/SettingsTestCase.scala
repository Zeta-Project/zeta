package de.htwg.zeta.persistence.entityTestCases

import java.util.UUID

import models.document.DockerSettings
import models.document.JobSettings
import models.entity.Settings


object SettingsTestCase extends EntityTestCase[Settings] {

  override val entity1 = Settings(
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

  override val entity2 = Settings(
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

  override val entity2Updated: Settings = entity2.copy(owner = UUID.randomUUID)

  override val entity3 = Settings(
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
