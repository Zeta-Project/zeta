package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.ElementsRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.ScalarBoolValue
import models.modelDefinitions.metaModel.elements.ScalarDoubleValue
import models.modelDefinitions.metaModel.elements.ScalarIntValue
import models.modelDefinitions.metaModel.elements.ScalarStringValue
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ModelElement

class EdgeAttributesGlobalUnique(edgeType: String, attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Every value of attribute $attributeType in edges of type $edgeType must be globally unique."
  override val possibleFix: String = s"Remove duplicated values of attribute $attributeType in edges of type $edgeType."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {

    def handleStrings(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarStringValue => v }.map(_.value)
    def handleBooleans(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarBoolValue => v }.map(_.value.toString)
    def handleInts(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarIntValue => v }.map(_.value.toString)
    def handleDoubles(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: ScalarDoubleValue => v }.map(_.value.toString)
    def handleEnums(values: Seq[AttributeValue]): Seq[String] = values.collect { case v: EnumSymbol => v }.map(_.toString)


    val edges = Util.getEdges(elements).filter(_.reference.name == edgeType)
    val attributeValues: Seq[AttributeValue] = edges.flatMap(_.attributes).filter(_._1 == attributeType).flatMap(_._2)

    val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
      case None => Seq()
      case Some(_: ScalarStringValue) => handleStrings(attributeValues)
      case Some(_: ScalarBoolValue) => handleBooleans(attributeValues)
      case Some(_: ScalarIntValue) => handleInts(attributeValues)
      case Some(_: ScalarDoubleValue) => handleDoubles(attributeValues)
      case Some(_: EnumSymbol) => handleEnums(attributeValues)
    }

    // find duplicate values
    val attributesGrouped: Map[String, Seq[String]] = attributeValuesStrings.groupBy(identity)
    val duplicateAttributeValues: Seq[String] = attributesGrouped.filter(_._2.size > 1).keys.toSeq

    def checkEdgeDuplicateValues(acc: Seq[ModelValidationResult], currentEdge: Edge): Seq[ModelValidationResult] = {
      val attributeValues = currentEdge.attributes.flatMap(_._2).toSeq

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq()
        case Some(_: ScalarStringValue) => handleStrings(attributeValues)
        case Some(_: ScalarBoolValue) => handleBooleans(attributeValues)
        case Some(_: ScalarIntValue) => handleInts(attributeValues)
        case Some(_: ScalarDoubleValue) => handleDoubles(attributeValues)
        case Some(_: EnumSymbol) => handleEnums(attributeValues)
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
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.globalUnique).map(attr => new EdgeAttributesGlobalUnique(currentReference.name, attr.name))
    }
}
