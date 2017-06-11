package models.entity

import java.util.UUID

import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.MetaModel


case class MetaModelRelease(
    id: UUID = UUID.randomUUID,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl,
    version: String
) extends Entity
