package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MLinkDef
import models.modelDefinitions.metaModel.elements.MReference
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.Node
import models.modelDefinitions.model.elements.ToNodes
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class EdgeSourcesLowerBoundTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeSourcesLowerBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or more source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val twoSourceNodes = ToNodes(`type` = sourceType, nodes = Seq(
      Node(
        id = "1",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeTwoSourceNodes = Edge.apply2("", mReference, Seq(twoSourceNodes), Seq(), Seq())

    rule.isValid(edgeTwoSourceNodes).get should be (true)

    val threeSourceNodes = ToNodes(`type` = sourceType, nodes = Seq(
      Node(
        id = "1",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "2",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeThreeSourceNodes = Edge.apply2("", mReference, Seq(threeSourceNodes), Seq(), Seq())

    rule.isValid(edgeThreeSourceNodes).get should be (true)
  }

  it should "return false on edges of type edgeType having less than 2 source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val oneSourceNode = ToNodes(`type` = sourceType, nodes = Seq(
      Node(
        id = "1",
        `type` = sourceType,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edgeOneSourceNode = Edge.apply2("", mReference, Seq(oneSourceNode), Seq(), Seq())

    rule.isValid(edgeOneSourceNode).get should be (false)

    val edgeNoSourceNodes = Edge.apply2("", mReference, Seq(), Seq(), Seq())

    rule.isValid(edgeNoSourceNodes).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentMRef = MReference("invalidReference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentMRef, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Sources ofEdges "edgeType" toNodes "sourceType" haveLowerBound 2""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val class1 = MClass("class", abstractness = false, Seq[MClass](), Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val sourceLinkDef1 = MLinkDef(class1, -1, 5, deleteIfLower = false)
    val reference = MReference("reference", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](sourceLinkDef1), Seq[MLinkDef](), Seq[MAttribute]())
    val metaModel = TestUtil.toMetaModel(Seq(reference))
    val result = EdgeSourcesLowerBound.generateFor(metaModel)

    result.size should be (1)
    result.head match {
      case rule: EdgeSourcesLowerBound =>
        rule.edgeType should be ("reference")
        rule.sourceType should be ("class")
        rule.lowerBound should be (5)
      case _ => fail
    }
  }

}
