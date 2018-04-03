package de.htwg.zeta.parser

import org.scalatest.FreeSpec
import org.scalatest.Matchers

//noinspection ScalaStyle
class EnumParserTest extends FreeSpec with Matchers {

  object MyEnum extends Enumeration {
    type MyEnum = Value
    val val1, val2, val3, val30 = Value
  }

  "an enumparser should" - {

    "succeed in parsing" - {

      "a valid enum value" in {
        val validEnumValue = "val1"
        val result = EnumParser.parse(EnumParser.parseEnum(MyEnum), validEnumValue)
        result.successful shouldBe true
        result.get shouldBe MyEnum.val1
      }

      "the longest enum value" in {
        val longestEnumValue = "val30"
        val result = EnumParser.parse(EnumParser.parseEnum(MyEnum), longestEnumValue)
        result.successful shouldBe true
        result.get shouldBe MyEnum.val30
      }

      "while ignoring uppercase / lowercase" in {
        val validEnumValue = "VAL2"
        val result = EnumParser.parse(EnumParser.parseEnum(MyEnum), validEnumValue)
        result.successful shouldBe true
        result.get shouldBe MyEnum.val2
      }
    }

    "fail in parsing" - {

      "an empty string" in {
        val emptyString = ""
        val result = EnumParser.parse(EnumParser.parseEnum(MyEnum), emptyString)
        result.successful shouldBe false
      }

      "an invalid enum value" in {
        val invalidEnumValue = "val666"
        val result = EnumParser.parse(EnumParser.parseEnum(MyEnum), invalidEnumValue)
        result.successful shouldBe false
      }

      "an enum without values" in {
        object EnumWithoutValues extends Enumeration
        val caught = intercept[IllegalArgumentException] {
          EnumParser.parse(EnumParser.parseEnum(EnumWithoutValues), "")
        }
        caught.getMessage shouldBe "Enum must contain at least one value!"
      }

    }
  }
}
