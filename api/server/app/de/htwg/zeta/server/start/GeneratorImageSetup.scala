package de.htwg.zeta.server.start

import java.util.UUID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import scala.util.Success

import de.htwg.zeta.common.models.entity.GeneratorImage
import de.htwg.zeta.common.models.entity.GeneratorImageOptions
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
        generatorImageRepo.create(createImage())
      }
    case Failure(e) => warn(e)
  }

  private def createImage(): GeneratorImage = {
    GeneratorImage(
      id = UUID.randomUUID,
      name = "Generator",
      description = "Scala generator.",
      dockerImage = "modigen/generator:0.1",
      options = createOptions(createNameProperty())
    )
  }

  private def createOptions(name: Option[GeneratorNameProperty]): GeneratorImageOptions = {
    GeneratorImageOptions(
      schema = "http://json-schema.org/schema#",
      title = "Options",
      optionType = "object",
      properties = GeneratorOptionProperties(name, None)
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
