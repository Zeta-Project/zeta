package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import scala.collection.mutable.ListBuffer

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumSymbol
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ModelElement
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Node
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.Util.El
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class NodeAttributesGlobalUnique(val nodeTypes: Seq[String], val attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"Every value of attribute $attributeType in nodes of types ${Util.stringSeqToSeqString(nodeTypes)} must be globally unique."
  override val possibleFix: String =
    s"Remove duplicated values of attribute $attributeType in nodes of type ${Util.stringSeqToSeqString(nodeTypes)}."

  def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MString => v }.map(_.value)
  def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MBool => v }.map(_.value.toString)
  def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MInt => v }.map(_.value.toString)
  def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: MDouble => v }.map(_.value.toString)
  def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {

    val nodes = Util.getNodes(elements).filter(node => nodeTypes.contains(node.className))

    val attributes: Seq[(String, Seq[AttributeValue])] = nodes.flatMap(_.attributes).filter(_._1 == attributeType)
    val attributeValues: Seq[AttributeValue] = attributes.flatMap(_._2)

    // convert all attribute values to string for comparison.
    val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
      case None => Seq()
      case Some(_: MString) => handleStrings(attributeValues)
      case Some(_: MBool) => handleBooleans(attributeValues)
      case Some(_: MInt) => handleInts(attributeValues)
      case Some(_: MDouble) => handleDoubles(attributeValues)
      case Some(_: EnumSymbol) => handleEnums(attributeValues)
    }

    // find duplicate values
    val attributesGrouped: Map[String, Seq[String]] = attributeValuesStrings.groupBy(identity)
    val duplicateAttributeValues: Seq[String] = attributesGrouped.filter(_._2.size > 1).keys.toSeq

    def checkNodeDuplicateValues(acc: Seq[ModelValidationResult], currentNode: Node): Seq[ModelValidationResult] = {
      val attributeValues = currentNode.attributes.values.flatten.toSeq

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq()
        case Some(_: MString) => handleStrings(attributeValues)
        case Some(_: MBool) => handleBooleans(attributeValues)
        case Some(_: MInt) => handleInts(attributeValues)
        case Some(_: MDouble) => handleDoubles(attributeValues)
        case Some(_: EnumSymbol) => handleEnums(attributeValues)
      }

      val valid = attributeValuesStrings.foldLeft(true) { (acc, currentString) =>
        if (duplicateAttributeValues.contains(currentString)) false else acc
      }

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(currentNode))
    }

    // check which nodes contains one or more of the duplicated values.
    nodes.foldLeft(Seq[ModelValidationResult]())(checkNodeDuplicateValues)

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inNodes ${Util.stringSeqToSeqString(nodeTypes)} areGlobalUnique ()"""
}

object NodeAttributesGlobalUnique extends GeneratorRule {

  override def generateFor(metaModel: MetaModel): Seq[DslRule] = {

    val graph = Util.inheritAttributes(Util.simplifyMetaModelGraph(metaModel))

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

  def getInheritanceRelationship(el: El, graph: Seq[El]): Seq[El] = {

    def getSuperClasses: Seq[El] = {

      def getSuperClassesRec(current: El, acc: Set[El]): Set[El] = {
        if (current.superTypes.isEmpty) {
          acc
        } else {
          val superTypes = current.superTypes.flatMap(elName => graph.find(_.name == elName))
          superTypes.flatMap(getSuperClassesRec(_, acc ++ superTypes)).toSet
        }
      }

      getSuperClassesRec(el, Set()).toSeq

    }

    def getSubClasses: Seq[El] = {

      def getSubClassesRec(current: El, acc: Set[El]): Set[El] = {
        if (current.subTypes.isEmpty) {
          acc
        } else {
          val subTypes = current.subTypes.flatMap(elName => graph.find(_.name == elName))
          subTypes.flatMap(getSubClassesRec(_, acc ++ subTypes)).toSet
        }
      }

      getSubClassesRec(el, Set()).toSeq
    }

    (getSuperClasses :+ el) ++ getSubClasses

  }

}

