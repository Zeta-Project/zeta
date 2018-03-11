package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.mutable.ListBuffer

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.Concept
import de.htwg.zeta.common.models.project.instance.elements.NodeInstance
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.NodesRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesGlobalUnique(val nodeTypes: Seq[String], val attributeType: String) extends NodesRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Every value of attribute $attributeType in nodes of types ${Util.stringSeqToSeqString(nodeTypes)} must be globally unique."
  override val possibleFix: String =
    s"Remove duplicated values of attribute $attributeType in nodes of type ${Util.stringSeqToSeqString(nodeTypes)}."

  def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: StringValue => v }.map(_.value)
  def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: BoolValue => v }.map(_.value.toString)
  def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: IntValue => v }.map(_.value.toString)
  def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: DoubleValue => v }.map(_.value.toString)
  def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumValue => v }.map(_.toString)

  override def check(elements: Seq[NodeInstance]): Seq[ModelValidationResult] = {

    val nodes = elements.filter(node => nodeTypes.contains(node.className))

    val attributes: Seq[(String, AttributeValue)] = nodes.flatMap(_.attributeValues).filter(_._1 == attributeType)
    val attributeValues: Seq[AttributeValue] = attributes.map(_._2)

    // convert all attribute values to string for comparison.
    val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
      case None => Seq()
      case Some(_: StringValue) => handleStrings(attributeValues)
      case Some(_: BoolValue) => handleBooleans(attributeValues)
      case Some(_: IntValue) => handleInts(attributeValues)
      case Some(_: DoubleValue) => handleDoubles(attributeValues)
      case Some(_: EnumValue) => handleEnums(attributeValues)
    }

    // find duplicate values
    val attributesGrouped: Map[String, Seq[String]] = attributeValuesStrings.groupBy(identity)
    val duplicateAttributeValues: Seq[String] = attributesGrouped.filter(_._2.size > 1).keys.toSeq

    def checkNodeDuplicateValues(acc: Seq[ModelValidationResult], currentNode: NodeInstance): Seq[ModelValidationResult] = {
      val attributeValues = currentNode.attributeValues.values.toSeq

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq()
        case Some(_: StringValue) => handleStrings(attributeValues)
        case Some(_: BoolValue) => handleBooleans(attributeValues)
        case Some(_: IntValue) => handleInts(attributeValues)
        case Some(_: DoubleValue) => handleDoubles(attributeValues)
        case Some(_: EnumValue) => handleEnums(attributeValues)
      }

      val valid = attributeValuesStrings.foldLeft(true) { (acc, currentString) =>
        if (duplicateAttributeValues.contains(currentString)) false else acc
      }

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(Left(currentNode)))
    }

    // check which nodes contains one or more of the duplicated values.
    nodes.foldLeft(Seq[ModelValidationResult]())(checkNodeDuplicateValues)

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes ${Util.stringSeqToSeqString(nodeTypes)} areGlobalUnique ()"""
}

object NodeAttributesGlobalUnique extends GeneratorRule {

  override def generateFor(metaModel: Concept): Seq[DslRule] = {

    val graph = metaModel.classes

    val inheritanceRelationships = graph.map(el => (el, getInheritanceRelationship(el, graph)))

    val inheritanceWithAttribute = for {
      inheritanceRelationship <- inheritanceRelationships
      attribute <- inheritanceRelationship._1.attributes if attribute.globalUnique
    } yield (inheritanceRelationship._1, inheritanceRelationship._2, attribute)

    val globalUniqueAttributes = inheritanceWithAttribute.groupBy(rec => (rec._2, rec._3)).keySet

    val ruleData = ListBuffer[(Seq[String], String)]()

    for {globalUniqueAttribute <- globalUniqueAttributes} {
      val elSet = globalUniqueAttribute._1.filter { el =>
        el.attributes.exists(att => att.name == globalUniqueAttribute._2.name && att.globalUnique)
      }
      ruleData += ((elSet.map(_.name), globalUniqueAttribute._2.name))
    }

    ruleData.distinct.map(rule => new NodeAttributesGlobalUnique(rule._1, rule._2))

  }

  def getInheritanceRelationship(el: MClass, graph: Seq[MClass]): Seq[MClass] = {

    def getSuperClasses: Seq[MClass] = {

      def getSuperClassesRec(current: MClass, acc: Set[MClass]): Set[MClass] = {
        if (current.superTypeNames.isEmpty) {
          acc
        } else {
          val superTypes = current.superTypeNames.flatMap(elName => graph.find(_.name == elName))
          superTypes.flatMap(getSuperClassesRec(_, acc ++ superTypes)).toSet
        }
      }

      getSuperClassesRec(el, Set()).toSeq

    }

    def getSubClasses: Seq[MClass] = {

      def getSubClassesRec(current: MClass, acc: Set[MClass]): Set[MClass] = {
        val sub = graph.filter(_.superTypeNames.contains(current.name))
        if (sub.isEmpty) {
          acc
        } else {
          val subTypes = sub.flatMap(elName => graph.find(_.name == elName))
          subTypes.flatMap(getSubClassesRec(_, acc ++ subTypes)).toSet
        }
      }

      getSubClassesRec(el, Set()).toSeq
    }

    (getSuperClasses :+ el) ++ getSubClasses

  }

}

