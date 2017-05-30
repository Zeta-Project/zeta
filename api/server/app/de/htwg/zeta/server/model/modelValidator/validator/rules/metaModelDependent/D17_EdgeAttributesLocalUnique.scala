package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.EnumSymbol
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.Edge

class D17_EdgeAttributesLocalUnique(val edgeType: String, val attributeType: String) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attribute values of attribute type $attributeType in Edge of type $edgeType must be unique locally."
  override val possibleFix: String = s"Remove all but one of the duplicated attribute values of type $attributeType in Edge of type $edgeType."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = {

    edge.attributes.find(_.name == attributeType) match {
      case None => true
      case Some(attribute) =>
        val attributeValues: Seq[String] = attribute.value.headOption match {
          case None => Seq()
          case Some(_: MString) => attribute.value.collect { case v: MString => v }.map(_.value)
          case Some(_: MBool) => attribute.value.collect { case v: MBool => v }.map(_.value.toString)
          case Some(_: MInt) => attribute.value.collect { case v: MInt => v }.map(_.value.toString)
          case Some(_: MDouble) => attribute.value.collect { case v: MDouble => v }.map(_.value.toString)
          case Some(_: EnumSymbol) => attribute.value.collect { case v: EnumSymbol => v }.map(_.toString)
        }
        if (attributeValues.isEmpty) true
        else attributeValues.distinct.size == attribute.value.size
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areLocalUnique ()"""
}

object D17_EdgeAttributesLocalUnique extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes.filter(_.localUnique).map(attr => new D17_EdgeAttributesLocalUnique(currentReference.name, attr.name))
    }
}
