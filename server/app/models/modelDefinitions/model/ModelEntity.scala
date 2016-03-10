package models.modelDefinitions.model

import java.time.Instant


case class ModelEntity(
  id: String,
  userId: String,
  metaModelId: String,
  created: Instant,
  updated: Instant,
  model: Model
)
