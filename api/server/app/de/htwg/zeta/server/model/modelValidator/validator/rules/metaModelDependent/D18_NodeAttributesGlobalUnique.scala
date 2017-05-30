package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, ElementsRule, GeneratorRule}
import de.htwg.zeta.server.model.modelValidator.Util.El
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.{AttributeValue, EnumSymbol}
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.{Attribute, ModelElement}

import scala.collection.mutable.ListBuffer

class D18_NodeAttributesGlobalUnique(nodeTypes: Seq[String], attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Every value of attribute $attributeType in nodes of types ${Util.stringSeqToSeqString(nodeTypes)} must be globally unique."
  override val possibleFix: String = s"Remove duplicated values of attribute $attributeType in nodes of type ${Util.stringSeqToSeqString(nodeTypes)}."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {
    val nodes = Util.getNodes(elements).filter(node => nodeTypes.contains(node.`type`.name))

    val attributes: Seq[Attribute] = nodes.flatMap(_.attributes).filter(_.name == attributeType)
    val attributeValues: Seq[AttributeValue] = attributes.flatMap(_.value)

    // convert all attribute values to string for comparison.
    val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
      case None => Seq()
      case Some(_: MString) => attributeValues.collect { case v: MString => v }.map(_.value)
      case Some(_: MBool) => attributeValues.collect { case v: MBool => v }.map(_.value.toString)
      case Some(_: MInt) => attributeValues.collect { case v: MInt => v }.map(_.value.toString)
      case Some(_: MDouble) => attributeValues.collect { case v: MDouble => v }.map(_.value.toString)
      case Some(_: EnumSymbol) => attributeValues.collect { case v: EnumSymbol => v }.map(_.toString)
    }

    // find duplicate values
    val attributesGrouped: Map[String, Seq[String]] = attributeValuesStrings.groupBy(identity)
    val duplicateAttributeValues: Seq[String] = attributesGrouped.filter(_._2.size > 1).keys.toSeq

    // check which nodes contains one or more of the duplicated values.
    nodes.foldLeft(Seq[ModelValidationResult]()) { (acc, currentNode) =>

      val attributeValues = currentNode.attributes.flatMap(_.value)

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq()
        case Some(_: MString) => attributeValues.collect { case v: MString => v }.map(_.value)
        case Some(_: MBool) => attributeValues.collect { case v: MBool => v }.map(_.value.toString)
        case Some(_: MInt) => attributeValues.collect { case v: MInt => v }.map(_.value.toString)
        case Some(_: MDouble) => attributeValues.collect { case v: MDouble => v }.map(_.value.toString)
        case Some(_: EnumSymbol) => attributeValues.collect { case v: EnumSymbol => v }.map(_.toString)
      }

      val valid = attributeValuesStrings.foldLeft(true) { (acc, currentString) =>
        if (duplicateAttributeValues.contains(currentString)) false
        else acc
      }

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(currentNode))
    }

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes ${Util.stringSeqToSeqString(nodeTypes)} areGlobalUnique ()"""
}

object D18_NodeAttributesGlobalUnique extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = {

    val graph = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))

    def getInheritanceRelationship(el: El): Seq[El] = {

      def getSuperClasses: Seq[El] = {
        val result = ListBuffer[El]()
        var superClasses = el.superTypes.flatMap(elName => graph.find(_.name == elName))
        while (superClasses.nonEmpty) {
          result ++= superClasses
          superClasses = superClasses.flatMap(superClass => superClass.superTypes).flatMap(elName => graph.find(_.name == elName))
        }
        result
      }

      def getSubClasses: Seq[El] = {
        val result = ListBuffer[El]()
        var subClasses = el.subTypes.flatMap(elName => graph.find(_.name == elName))
        while (subClasses.nonEmpty) {
          result ++= subClasses
          subClasses = subClasses.flatMap(subClass => subClass.subTypes).flatMap(elName => graph.find(_.name == elName))
        }
        result
      }

      (getSuperClasses :+ el) ++ getSubClasses

    }

    val inheritanceRelationships = graph.map(el => (el, getInheritanceRelationship(el)))

    val inheritanceWithAttribute = for (
      inheritanceRelationship <- inheritanceRelationships;
      attribute <- inheritanceRelationship._1.attributes if attribute.globalUnique
    ) yield (inheritanceRelationship._1, inheritanceRelationship._2, attribute)

    val globalUniqueAttributes = inheritanceWithAttribute.groupBy(rec => (rec._2, rec._3)).keySet

    val ruleData = ListBuffer[(Seq[String], String)]()

    for (globalUniqueAttribute <- globalUniqueAttributes) {
      val elSet = globalUniqueAttribute._1.filter { el =>
        el.attributes.exists(att => att.name == globalUniqueAttribute._2.name && att.globalUnique)
      }
      ruleData += ((elSet.map(_.name), globalUniqueAttribute._2.name))
    }

    ruleData.distinct.map(rule => new D18_NodeAttributesGlobalUnique(rule._1, rule._2))

  }

}

