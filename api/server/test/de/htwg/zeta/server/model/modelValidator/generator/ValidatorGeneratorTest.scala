package de.htwg.zeta.server.model.modelValidator.generator

import java.util.UUID

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.entity.GraphicalDsl
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidator
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class ValidatorGeneratorTest extends FlatSpec with Matchers {

  val metaModel = Concept(
    name = "metaModel",
    classes = Seq(),
    references = Seq(),
    enums = Seq(),
    methods = Seq.empty,
    attributes = Seq.empty,
    uiState = ""
  )

  val metaModelEntity = MetaModelEntity(
    id = UUID.randomUUID(),
    metaModel = metaModel
  )

  val generator = new ValidatorGenerator(metaModelEntity)
  val generationResult: ValidatorGeneratorResult = generator.generateValidator()

  "generateValidator" should "generate a validator from the meta model" in {
    generationResult.success should be (true)
    generationResult.result.contains("""Edges areOfTypes Seq("")""") should be (true)
    generationResult.result.contains("""Nodes areOfTypes Seq("")""") should be (true)
  }

  "create" should "create a valid ModelValidator instance from the generated String" in {
    ValidatorGenerator.create(generationResult.result).get shouldBe a[ModelValidator]
  }

}
