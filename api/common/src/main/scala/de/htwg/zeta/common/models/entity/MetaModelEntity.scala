package de.htwg.zeta.common.models.entity

import java.util.UUID

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Dsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import play.api.libs.json.Format
import play.api.libs.json.Json


case class MetaModelEntity(
    id: UUID,
    metaModel: MetaModel, // Concept
    dsl: Dsl = Dsl(),
    validator: Option[String] = None
) extends Entity
