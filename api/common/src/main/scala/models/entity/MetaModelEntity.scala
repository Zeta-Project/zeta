package models.entity

import java.util.UUID

import models.modelDefinitions.helper.HLink
import models.modelDefinitions.metaModel.Dsl
import models.modelDefinitions.metaModel.MetaModel


case class MetaModelEntity(
    id: UUID = UUID.randomUUID,
    rev: String,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl = Dsl(),
    links: Option[Seq[HLink]] = None
) extends Entity
