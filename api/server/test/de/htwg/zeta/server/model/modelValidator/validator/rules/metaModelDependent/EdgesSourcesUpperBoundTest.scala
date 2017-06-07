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

class EdgesSourcesUpperBoundTest extends FlatSpec with Matchers {

  val mReference = MReference("edgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
  val rule = new EdgeSourcesUpperBound("edgeType", "sourceType", 2)

  "isValid" should "return true on edges of type edgeType having 2 or less source nodes of type sourceType" in {
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

    rule.isValid(edgeOneSourceNode).get should be (true)


    val edgeNoSourceNodes = Edge.apply2("", mReference, Seq(), Seq(), Seq())

    rule.isValid(edgeNoSourceNodes).get should be (true)
  }

  it should "return false on edges of type edgeType having more than 2 source nodes of type sourceType" in {
    val sourceType = MClass(
      name = "sourceType",
      abstractness = false,
      superTypes = Seq(),
      inputs = Seq(),
      outputs = Seq(),
      attributes = Seq()
    )

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

    rule.isValid(edgeThreeSourceNodes).get should be (false)
  }

  it should "return None on non-matching edges" in {
    val differentReference = MReference("differentEdgeType", sourceDeletionDeletesTarget = false, targetDeletionDeletesSource = false, Seq[MLinkDef](), Seq[MLinkDef](), Seq[MAttribute]())
    val edge = Edge.apply2("", differentReference, Seq(), Seq(), Seq())
    rule.isValid(edge) should be (None)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be ("""Sources ofEdges "edgeType" toNodes "sourceType" haveUpperBound 2""")
  }

}
