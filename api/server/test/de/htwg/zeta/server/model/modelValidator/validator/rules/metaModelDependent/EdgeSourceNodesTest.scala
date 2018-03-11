package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourceNodesTest extends FlatSpec with Matchers {

  val mReference = MReference(
    "edgeType",
    description = "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    Seq[MAttribute](),
    Seq.empty
  )

  val emptyEdge: EdgeInstance = EdgeInstance.empty("", mReference.name, "", "")

  val rule = new EdgeSourceNodes("edgeType", Seq("source1", "source2"))

  "isValid" should "return true on edges of type edgeType with valid source nodes" in {

    val source1 = MClass(
      name = "source1",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge1 = emptyEdge.copy(targetNodeName = source1.name)

    rule.isValid(edge1).get should be(true)

    val source2 = MClass(
      name = "source2",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge2 = emptyEdge.copy(sourceNodeName = source1.name)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid source nodes" in {
    val invalidSource = MClass(
      name = "invalidSource",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge1 = emptyEdge.copy(sourceNodeName = invalidSource.name)

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference(
      "differentEdgeType",
      description = "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      "",
      "",
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentMReference.name)

    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Sources ofEdges "edgeType" areOfTypes Seq("source1", "source2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class1", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val class2 = MClass("class2", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val reference = MReference("reference", "", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, class1.name, class2.name,
      Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(references = Seq(reference))
    val result = EdgeSourceNodes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeSourceNodes =>
        rule.edgeType should be("reference")
        rule.sourceTypes should be(Seq("class1", "class2"))
      case _ => fail
    }
  }

}
