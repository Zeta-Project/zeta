package de.htwg.zeta.parser.shape

import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

//noinspection ScalaStyle
class IdentifierTest extends AnyFreeSpec with Matchers {

  "An identifier should split the identifier" - {

    "without a prefix" in {
      val identifierWithoutPrefix = Identifier("myIdentifier")
      identifierWithoutPrefix.split shouldBe ("", "myIdentifier")
    }

    "with a prefix" in {
      val identifierWithIdentifier = Identifier("prefix.ident")
      identifierWithIdentifier.split shouldBe ("prefix", "ident")
    }

  }
}
