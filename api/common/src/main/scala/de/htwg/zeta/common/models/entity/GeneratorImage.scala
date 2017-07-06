package de.htwg.zeta.common.models.entity

import java.util.UUID

case class GeneratorImage(
    id: UUID,
    name: String,
    description: String,
    dockerImage: String,
    options: GeneratorImageOptions
) extends Entity

case class GeneratorImageOptions(
    schema: String,
    title: String,
    optionType: String,
    properties: GeneratorOptionProperties
)

case class GeneratorOptionProperties(
    name: Option[GeneratorNameProperty] = None,
    metaModelRelease: Option[GeneratorMetaModelReleaseProperty] = None
)

case class GeneratorNameProperty(
    title: String,
    propertyType: String,
    required: Boolean
)

case class GeneratorMetaModelReleaseProperty(
    ref: String
)
