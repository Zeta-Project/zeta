package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgesNoSourcesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: EdgeInstance = EdgeInstance.empty("", mReference.name, "", "")
  val rule = new EdgesNoSources("edgeType")

  "isValid" should "return true on edges of type edgeType with no sources" in {
    val edge = emptyEdge
    rule.isValid(edge).get should be(true)
  }

  it should "return false on edges of type edgeType with sources" in {
    val source = MClass(
      name = "",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )
    val edge = emptyEdge.copy(sourceNodeName = source.name)

    rule.isValid(edge).get should be(false)
  }

  it should "return true on edges of type edgeType with empty source list" in {
    val source = MClass(
      name = "",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge = emptyEdge.copy(sourceNodeName = source.name)

    rule.isValid(edge).get should be(true)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference(
      "differenteEdgeType",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      "",
      "",
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentReference.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Edges ofType "edgeType" haveNoSources ()""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, "", "",
      Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(references = Seq(reference))
    val result = EdgesNoSources.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgesNoSources =>
        rule.edgeType should be("reference")
      case _ => fail
    }
  }

}
