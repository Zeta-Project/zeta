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

class EdgeSourceNodesTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeSourceNodes("edgeType", Seq("source1", "source2"))

  "isValid" should "return true on edges of type edgeType with valid source nodes" in {

    val source1 = MClass(
      name = "source1",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val toNodes1 = ToNodes(`type` = source1, nodes = Seq(
      Node(
        id = "",
        `type` = source1,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edge1 = Edge.apply2("", mReference, Seq(toNodes1), Seq(), Seq())

    rule.isValid(edge1).get should be(true)

    val source2 = MClass(
      name = "source2",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val toNodes2 = ToNodes(`type` = source1, nodes = Seq(
      Node(
        id = "",
        `type` = source1,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      ),
      Node(
        id = "",
        `type` = source2,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edge2 = Edge.apply2("", mReference, Seq(toNodes2), Seq(), Seq())

    rule.isValid(edge2).get should be(true)

  }

  it should "return false on edges of type edgeType with invalid source nodes" in {
    val invalidSource = MClass(
      name = "invalidSource",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

    val invalidToNodes = ToNodes(`type` = invalidSource, nodes = Seq(
      Node(
        id = "",
        `type` = invalidSource,
        _outputs = Seq(),
        _inputs = Seq(),
        attributes = Seq()
      )
    ))

    val edge1 = Edge.apply2("", mReference, Seq(invalidToNodes), Seq(), Seq())

    rule.isValid(edge1).get should be(false)
  }

  it should "return None on non-matching edges" in {
    val differentMReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentMReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Sources ofEdges "edgeType" areOfTypes Seq("source1", "source2")""")
  }

}
