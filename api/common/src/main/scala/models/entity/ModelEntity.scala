package models.entity

import java.util.UUID

import models.modelDefinitions.helper.HLink
import models.modelDefinitions.model.Model


case class ModelEntity(
    id: UUID = UUID.randomUUID,
    model: Model,
    metaModelId: UUID,
    links: Option[Seq[HLink]] = None
) extends Entity
