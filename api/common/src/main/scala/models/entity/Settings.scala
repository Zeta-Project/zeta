package models.entity

import java.util.UUID

import models.document.JobSettings


case class Settings(
    id: UUID = UUID.randomUUID,
    owner: UUID,
    jobSettings: JobSettings
) extends Entity
