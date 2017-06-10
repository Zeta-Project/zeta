package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import scala.collection.immutable.Seq

import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.model.elements.Attribute
import models.modelDefinitions.model.elements.Node
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesAttributesNamesNotEmptyTest extends FlatSpec with Matchers {

  val rule = new NodesAttributesNamesNotEmpty
  val mClass = MClass("nodeType", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute]())

  "isValid" should "return true on non-empty attribute names" in {
    val attribute = Attribute("attributeName1", Seq())
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))
    rule.isValid(node).get should be(true)
  }

  it should "return false on empty attribute names" in {
    val attribute = Attribute("", Seq())
    val node = Node.apply2("", mClass, Seq(), Seq(), Seq(attribute))
    rule.isValid(node).get should be(false)
  }

}
