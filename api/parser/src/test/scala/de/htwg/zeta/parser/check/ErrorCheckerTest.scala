package de.htwg.zeta.parser.check

import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

//noinspection ScalaStyle
class ErrorCheckerTest extends FreeSpec with Matchers with Inside {

  case class ErrorCheckImpl(definedErrors: List[String]) extends ErrorCheck[String] {
    override def check(): List[String] = definedErrors
  }

  "An error checker" - {
    "should run a single configured checker class" in {
      val errors = ErrorChecker()
        .add(ErrorCheckImpl(List("Printed Error: 1", "Printed Error: 2")))
        .run()

      errors.size shouldBe 2
      errors should contain("Printed Error: 1")
      errors should contain("Printed Error: 2")
    }

    "should run multiple configured checker classes in right order" in {
      val errors = ErrorChecker()
        .add(ErrorCheckImpl(List("Printed Error: 1", "Printed Error: 2")))
        .add(ErrorCheckImpl(List("Printed Error: 3")))
        .run()

      errors.size shouldBe 3
      errors should contain("Printed Error: 1")
      errors should contain("Printed Error: 2")
      errors should contain("Printed Error: 3")
    }

    "should return an empty list if no errors occurred" in {
      val errors = ErrorChecker()
        .add(ErrorCheckImpl(List()))
        .add(ErrorCheckImpl(List()))
        .run()

      errors.size shouldBe 0
    }

    "should return an empty list if no errors occurred and Nil is returned" in {
      val errors = ErrorChecker()
        .add(ErrorCheckImpl(Nil))
        .add(ErrorCheckImpl(Nil))
        .run()

      errors.size shouldBe 0
    }
  }

}
