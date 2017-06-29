package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributeSetTest extends FlatSpec with Matchers {
  val rule = new NodesAttributeSet
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())


  "isValid" should "return true on valid nodes attribute sets" in {
    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "attributeName1" -> Seq(),
      "attributeName2" -> Seq()
    )
    val node = Node("", mClass, Seq(), Seq(), attributes)
    rule.isValid(node).get should be(true)
  }


  it should "return false on invalid edges attribute sets" in {
    val attributes: Map[String, Seq[AttributeValue]] = Map(
      "duplicateAttributeName" -> Seq(),
      "duplicateAttributeName" -> Seq()
    )
    val node = Node("", mClass, Seq(), Seq(), attributes)
    rule.isValid(node).get should be(false)
  }
}
