package de.htwg.zeta.server.start

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
import de.htwg.zeta.common.models.entity.GeneratorMetaModelReleaseProperty
import de.htwg.zeta.common.models.entity.GeneratorNameProperty
import de.htwg.zeta.common.models.entity.GeneratorOptionProperties
import de.htwg.zeta.persistence.general.GeneratorImageRepository
import grizzled.slf4j.Logging


/**
 * Setup GeneratorImage on database, when no entries exists
 */
class GeneratorImageSetup(generatorImageRepo: GeneratorImageRepository) extends Logging {

  generatorImageRepo.readAllIds().onComplete {
    case Success(value) =>
      if (value.isEmpty) {
        info("Database has no entries for GeneratorImages - adding entries")
        addEntries(generatorImageRepo)
      }
  }

  private def addEntries(persistence: GeneratorImageRepository) = {
    persistence.create(createBasicImage())
  }

  private def createBasicImage(): GeneratorImage = {
    GeneratorImage(
      id = UUID.randomUUID,
      name = "Basic Generator",
      description = "Basic scala generator.",
      dockerImage = "modigen/generator/basic:0.1",
      options = createOptions(createNameProperty())
    )
  }

  private def createOptions(name: Option[GeneratorNameProperty], metaModelRelease: Option[GeneratorMetaModelReleaseProperty] = None): GeneratorImageOptions = {
    GeneratorImageOptions(
      schema = "http://json-schema.org/schema#",
      title = "Options",
      optionType = "object",
      properties = GeneratorOptionProperties(name, metaModelRelease)
    )
  }

  private def createNameProperty(): Option[GeneratorNameProperty] = {
    Some(GeneratorNameProperty(
      title = "Name",
      propertyType = "string",
      required = true
    ))
  }
}

object GeneratorImageSetup {
  def apply(generatorImageRepo: GeneratorImageRepository): GeneratorImageSetup = new GeneratorImageSetup(generatorImageRepo)
}
