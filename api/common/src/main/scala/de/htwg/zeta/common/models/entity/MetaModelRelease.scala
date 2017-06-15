package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel


case class MetaModelRelease(
    id: UUID,
    name: String,
    metaModel: MetaModel,
    dsl: Dsl,
    version: String
) extends Entity
