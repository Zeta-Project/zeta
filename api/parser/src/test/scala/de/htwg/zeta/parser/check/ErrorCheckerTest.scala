package de.htwg.zeta.parser.check

import de.htwg.zeta.parser.check.Check.Id
import org.scalatest.FreeSpec
import org.scalatest.Inside
import org.scalatest.Matchers

class ErrorCheckerTest extends FreeSpec with Matchers with Inside {

  "An error checker" - {
    "should run a single configured checker class" - {
      val errors = ErrorChecker()
        .add(Checker(error => s"Printed Error: $error", () => List("1", "2")))
        .run()

      errors.size shouldBe 1
      errors should contain("Printed Error: 1,2")
    }

    "should run multiple configured checker classes in right order" - {
      val errors = ErrorChecker()
        .add(Checker(error => s"Printed Error1: $error", () => List("1", "2")))
        .add(Checker(error => s"Printed Error2: $error", () => List("3")))
        .run()

      errors.size shouldBe 2
      errors should contain("Printed Error1: 1,2")
      errors should contain("Printed Error2: 3")
    }

    "should return an empty list if no errors occurred" - {
      val errors = ErrorChecker()
        .add(Checker(error => s"Printed Error1: $error", () => List()))
        .add(Checker(error => s"Printed Error2: $error", () => List()))
        .run()

      errors.size shouldBe 0
    }

    "should return an empty list if no errors occurred and Nil is returned" - {
      val errors = ErrorChecker()
        .add(Checker(error => s"Printed Error1: $error", () => Nil))
        .add(Checker(error => s"Printed Error2: $error", () => Nil))
        .run()

      errors.size shouldBe 0
    }

    "should run multiple configured checker with shortcut method in right order" - {
      val errors = ErrorChecker()
        .add(error => s"Printed Error1: $error", () => List("1", "2"))
        .add(error => s"Printed Error2: $error", () => List("3"))
        .run()

      errors.size shouldBe 2
      errors should contain("Printed Error1: 1,2")
      errors should contain("Printed Error2: 3")
    }

    "should handle a configured ErrorCheck correctly" - {
      case class ErrorCheckImpl() extends ErrorCheck {
        override def check(): List[Id] = List("Working!")
      }
      val errors = ErrorChecker()
        .add(ErrorCheckImpl(), s => s)
        .run()

      errors.size shouldBe 1
      errors should contain("Working!")
    }
  }

}
