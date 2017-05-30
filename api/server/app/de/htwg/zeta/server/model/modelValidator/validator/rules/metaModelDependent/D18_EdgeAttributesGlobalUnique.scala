package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidationResult
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, ElementsRule, GeneratorRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.{AttributeValue, EnumSymbol}
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.ModelElement

class D18_EdgeAttributesGlobalUnique(edgeType: String, attributeType: String) extends ElementsRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Every value of attribute $attributeType in edges of type $edgeType must be globally unique."
  override val possibleFix: String = s"Remove duplicated values of attribute $attributeType in edges of type $edgeType."

  override def check(elements: Seq[ModelElement]): Seq[ModelValidationResult] = {

    val edges = Util.getEdges(elements).filter(_.`type`.name == edgeType)
    val attributeValues: Seq[AttributeValue] = edges.flatMap(_.attributes).filter(_.name == attributeType).flatMap(_.value)

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

    // check which edges contains one or more of the duplicated values.
    edges.foldLeft(Seq[ModelValidationResult]()) { (acc, currentEdge) =>

      val attributeValues = currentEdge.attributes.flatMap(_.value)

      val attributeValuesStrings: Seq[String] = attributeValues.headOption match {
        case None => Seq[String]()
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

      acc :+ ModelValidationResult(rule = this, valid = valid, modelElement = Some(currentEdge))
    }

  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areGlobalUnique ()"""
}

object D18_EdgeAttributesGlobalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.globalUnique).map(attr => new D18_EdgeAttributesGlobalUnique(currentReference.name, attr.name))
    }
}
