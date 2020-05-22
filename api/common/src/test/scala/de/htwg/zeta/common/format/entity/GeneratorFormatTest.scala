package de.htwg.zeta.common.format.entity

import java.util.UUID

import de.htwg.zeta.common.models.entity.Generator
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class GeneratorFormatTest extends AnyFreeSpec with Matchers {

  "A GeneratorFormat can format from Generator to Json and back to Generator" in {
    val target = new GeneratorFormat();
    val generator = new Generator(
      UUID.randomUUID,
      "example",
      UUID.randomUUID,
      Map(UUID.randomUUID -> "generator.scala"),
      false
    );
    val json = target.writes(generator);
    val result = target.reads(json).asOpt match {
      case Some(entity) => entity
      case None => fail("GeneratorFormat failed")
    }

    result shouldEqual generator
  }
}
