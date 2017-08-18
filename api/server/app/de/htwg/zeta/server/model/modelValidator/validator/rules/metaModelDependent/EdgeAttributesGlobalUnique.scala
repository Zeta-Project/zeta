package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.EnumValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.BoolValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.DoubleValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.IntValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge
import de.htwg.zeta.common.models.modelDefinitions.model.elements.ModelElement
import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributesGlobalUnique(val edgeType: String, val attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Every value of attribute $attributeType in edges of type $edgeType must be globally unique."
  override val possibleFix: String = s"Remove duplicated values of attribute $attributeType in edges of type $edgeType."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {

    def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: StringValue => v }.map(_.value)
    def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: BoolValue => v }.map(_.value.toString)
    def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: IntValue => v }.map(_.value.toString)
    def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: DoubleValue => v }.map(_.value.toString)
    def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumValue => v }.map(_.toString)


    val edges = Util.getEdges(elements).filter(_.referenceName == edgeType)
    val attributeValues: Seq[AttributeValue] = edges.flatMap(_.attributeValues).filter(_._1 == attributeType).flatMap(_._2)

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

    def checkEdgeDuplicateValues(acc: Seq[ModelValidationResult], currentEdge: Edge): Seq[ModelValidationResult] = {
      val attributeValues = currentEdge.attributeValues.flatMap(_._2).toSeq

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

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(currentEdge))
    }

    // check which edges contains one or more of the duplicated values.
    edges.foldLeft(Seq[ModelValidationResult]())(checkEdgeDuplicateValues)

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areGlobalUnique ()"""
}

object EdgeAttributesGlobalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.referenceMap.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.globalUnique).map(attr => new EdgeAttributesGlobalUnique(currentReference.name, attr.name))
    }
}
