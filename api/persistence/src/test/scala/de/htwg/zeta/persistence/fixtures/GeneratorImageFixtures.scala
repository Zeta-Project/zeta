package de.htwg.zeta.persistence.fixtures

import java.util.UUID

import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
import de.htwg.zeta.common.models.entity.GeneratorOptionProperties
import de.htwg.zeta.common.models.entity.GeneratorNameProperty


object GeneratorImageFixtures {

  val entity1 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage1",
    description = "desc",
    dockerImage = "dockerImage1",
    options = GeneratorImageOptions(
      schema = "http://json-schema.org/schema#",
      title = "option title",
      optionType = "object",
      properties = GeneratorOptionProperties()
    )
  )

  val entity2 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage2",
    description = "desc",
    dockerImage = "dockerImage2",
      options = GeneratorImageOptions(
      schema = "http://json-schema.org/schema#",
      title = "option title",
      optionType = "object",
      properties = GeneratorOptionProperties()
    )
  )

  val entity2Updated: GeneratorImage = entity2.copy(name = "filterImage2Updated")

  val entity3 = GeneratorImage(
    id = UUID.randomUUID,
    name = "filterImage3",
    description = "desc",
    dockerImage = "dockerImage3",
      options = GeneratorImageOptions(
      schema = "http://json-schema.org/schema#",
      title = "option title",
      optionType = "object",
      properties = GeneratorOptionProperties(
        name = Some(GeneratorNameProperty(
          title = "Name",
          propertyType = "String",
          required = true
        ))
      )
    )
  )

}
