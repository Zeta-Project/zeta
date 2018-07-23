package de.htwg.zeta.codeGenerator

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.common.models.project.concept
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.common.models.project.instance.elements
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import org.scalatest.FreeSpec
import org.scalatest.Matchers

class GdslInstanceToZetaModelTest extends FreeSpec with Matchers {

  val instance: GraphicalDslInstance = GraphicalDslInstance(
    UUID.fromString("c07e0c4d-4172-49a2-bab1-09f4cd2a59c6"),
    "beispiel",
    UUID.fromString("59fccf04-10de-41f0-885f-1e726d60d14a"),
    List(
      NodeInstance(
        "51631c94-d0c6-4c06-99cd-11c01bf4de2b"
        , "TeamAnchor",
        List(
          "5d3e5349-b69c-43ea-ab0b-efff44ccbf25"
        ), List(), List(), Map(), List()),
      NodeInstance(
        "467945d7-c3be-494d-82bd-20dd6a0c3b9b",
        "PeriodAnchor",
        List(
          "07764f4e-e213-4168-9ccb-6c657e95cf29"
        ), List(), List(), Map(), List()),
      NodeInstance(
        "c8bab695-a451-4bb3-96b8-9b4868790b39",
        "Entity",
        List(), List(
          "07764f4e-e213-4168-9ccb-6c657e95cf29",
          "4c918253-22e7-4362-afbd-b123711e8844"
        ), List(
          concept.elements.MAttribute("name", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("fix", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("in", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("out", false, false, StringType, StringValue(""), false, false, "", false, false)),
        Map("name" -> List(StringValue("EntityZwei")), "fix" -> List(StringValue("...")), "in" -> List(StringValue("...")), "out" -> List(StringValue("..."))),
        List()),
      NodeInstance(
        "3f687d80-b190-450a-8ed9-2fe2b3fc1e0e", "Entity",
        List("2d4f4b29-bac8-441a-800f-14f60294a08c"),
        List("5d3e5349-b69c-43ea-ab0b-efff44ccbf25"),
        List(
          MAttribute("name", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("fix", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("in", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("out", false, false, StringType, StringValue(""), false, false, "", false, false)),
        Map(
          "name" -> List(StringValue("EntityEins")),
          "fix" -> List(StringValue("...")),
          "in" -> List(StringValue("...")),
          "out" -> List(StringValue("..."))),
        List()),
      NodeInstance(
        "16cd7304-5ec1-44b5-966f-b8048a627012",
        "Entity",
        List("4c918253-22e7-4362-afbd-b123711e8844"),
        List("2d4f4b29-bac8-441a-800f-14f60294a08c"),
        List(
          MAttribute("name", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("fix", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("in", false, false, StringType, StringValue(""), false, false, "", false, false),
          MAttribute("out", false, false, StringType, StringValue(""), false, false, "", false, false)),
        Map("name" -> List(StringValue("EntityDrei")), "fix" -> List(StringValue("...")), "in" -> List(StringValue("...")), "out" -> List(StringValue("..."))),
        List())),
    List(
      EdgeInstance(
        "5d3e5349-b69c-43ea-ab0b-efff44ccbf25",
        "drop",
        "51631c94-d0c6-4c06-99cd-11c01bf4de2b",
        "3f687d80-b190-450a-8ed9-2fe2b3fc1e0e",
        List(), Map(), List()),
      EdgeInstance("07764f4e-e213-4168-9ccb-6c657e95cf29",
        "drop",
        "467945d7-c3be-494d-82bd-20dd6a0c3b9b",
        "c8bab695-a451-4bb3-96b8-9b4868790b39", List(), Map(), List()),
      EdgeInstance("2d4f4b29-bac8-441a-800f-14f60294a08c",
        "Link",
        "3f687d80-b190-450a-8ed9-2fe2b3fc1e0e",
        "16cd7304-5ec1-44b5-966f-b8048a627012",
        List(MAttribute("name", false, false, StringType, StringValue(""), false, false, "", false, false)),
        Map("name" -> List(StringValue("hatDrei"))), List()),
      elements.EdgeInstance(
        "4c918253-22e7-4362-afbd-b123711e8844",
        "Reference",
        "16cd7304-5ec1-44b5-966f-b8048a627012",
        "c8bab695-a451-4bb3-96b8-9b4868790b39",
        List(MAttribute("name", false, false, StringType, StringValue(""), false, false, "", false, false)),
        Map("name" -> List(StringValue("refZwei"))), List())),
    List(), Map(), List(), "")

  "Klima generator can" - {
    "generate example" in {
      // null is ok here as the element is currently unused
      // scalastyle:off
      val concept: Concept = null
      // scalastyle:on

      val a: Either[String, List[File]] = GdslInstanceToZetaModel.generate(concept, instance)

      a match {
        case Left(msg) => println(msg)
        case Right(_) =>
      }
      a.isRight shouldBe  true
    }

  }

}
