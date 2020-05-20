package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.MReference
import de.htwg.zeta.common.models.project.instance.elements.EdgeInstance
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EdgeTargetNodesTest extends AnyFlatSpec with Matchers {
  val mReference = MReference(
    "edgeType",
    "",
    sourceDeletionDeletesTarget = false,
    targetDeletionDeletesSource = false,
    "",
    "",
    sourceLowerBounds = 0,
    sourceUpperBounds = 0,
    targetLowerBounds = 0,
    targetUpperBounds = 0,
    Seq[MAttribute](),
    Seq.empty
  )
  val emptyEdge: EdgeInstance = EdgeInstance.empty("", mReference.name, "", "")
  val rule = new EdgeTargetNodes("edgeType", Seq("target1", "target2"))

  "isValid" should "return true on edges of type edgeType with valid target nodes" in {

    val target1 = MClass(
      name = "target1",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge1 = emptyEdge.copy(targetNodeName = target1.name)

    rule.isValid(edge1).get should be(true)

    val target2 = MClass(
      name = "target2",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge2 = emptyEdge.copy(targetNodeName = target1.name)

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid target nodes" in {
    val invalidTarget = MClass(
      name = "invalidTarget",
      description = "",
      abstractness = false,
      superTypeNames = Seq(),
      inputReferenceNames = Seq(),
      outputReferenceNames = Seq(),
      attributes = Seq(),
      methods = Seq.empty
    )

    val edge1 = emptyEdge.copy(targetNodeName = invalidTarget.name)

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
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val edge = emptyEdge.copy(referenceName = differentMReference.name)
    rule.isValid(edge) should be(None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Targets ofEdges "edgeType" areOfTypes Seq("target1", "target2")""")
  }

  "generateFor" should "generate this rule from the meta model" ignore {
    val class1Name = "class1"
    val class1 = MClass(class1Name, "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val class2Name = "class2"
    val class2 = MClass(class2Name, "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val reference = MReference(
      "reference",
      "",
      sourceDeletionDeletesTarget = false,
      targetDeletionDeletesSource = false,
      class1.name,
      class2.name,
      sourceLowerBounds = 0,
      sourceUpperBounds = 0,
      targetLowerBounds = 0,
      targetUpperBounds = 0,
      Seq[MAttribute](),
      Seq.empty
    )
    val metaModel = Concept.empty.copy(references = Seq(reference))
    val result = EdgeTargetNodes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: EdgeTargetNodes =>
        rule.edgeType should be("reference")
        rule.targetTypes should be(Seq(class1Name, class2Name))
      case _ => fail
    }
  }
}
