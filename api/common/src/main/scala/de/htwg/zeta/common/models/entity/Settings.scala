package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.document.JobSettings


case class Settings(
    id: UUID,
    owner: UUID,
    jobSettings: JobSettings
) extends Entity
