package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.immutable.Seq

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.common.models.project.concept.Concept
import org.scalatest.FlatSpec
import org.scalatest.Matchers

class NodesTest extends FlatSpec with Matchers {

  val rule = new Nodes(Seq("nodeType1", "nodeType2"))
  val mClass1 = MClass("nodeType1", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val mClass2 = MClass("nodeType2", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
  val mClass3 = MClass("nodeType3", "", abstractness = false, Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)

  "isValid" should "return true on valid edges" in {

    val node1 = Node.empty("", mClass1.name, Seq(), Seq())
    rule.isValid(node1).get should be(true)

    val node2 = Node.empty("", mClass2.name, Seq(), Seq())
    rule.isValid(node2).get should be(true)
  }

  it should "return false on invalid edges" in {
    val node3 = Node.empty("", mClass3.name, Seq(), Seq())
    rule.isValid(node3).get should be(false)
  }

  "dslStatement" should "return the correct string" in {
    rule.dslStatement should be(
      """Nodes areOfTypes Seq("nodeType1", "nodeType2")""")
  }

  "generateFor" should "generate this rule from the meta model" in {
    val mClass1 = MClass("class1", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val mClass2 = MClass("class2", "", abstractness = false, superTypeNames = Seq.empty, Seq.empty, Seq.empty, Seq[MAttribute](), Seq.empty)
    val metaModel = Concept.empty.copy(classes = Seq(mClass1, mClass2))
    val result = Nodes.generateFor(metaModel)

    result.size should be(1)
    result.head match {
      case rule: Nodes =>
        rule.nodeTypes should be(Seq("class1", "class2"))
      case _ => fail
    }

  }

}
