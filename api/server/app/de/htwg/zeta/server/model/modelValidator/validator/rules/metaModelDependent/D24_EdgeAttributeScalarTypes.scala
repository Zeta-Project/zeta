package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.{DslRule, GeneratorRule, SingleEdgeRule}
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.{AttributeType, ScalarType}
import models.modelDefinitions.metaModel.elements.ScalarValue.{MBool, MDouble, MInt, MString}
import models.modelDefinitions.model.elements.Edge

class D24_EdgeAttributeScalarTypes(edgeType: String, attributeType: String, attributeDataType: AttributeType) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String = s"Attributes of type $attributeType in edges of type $edgeType must be of data type ${Util.getAttributeTypeClassName(attributeDataType)}."
  override val possibleFix: String = s"Remove attribute values of attribute $attributeType in edge $edgeType which are not of data type ${Util.getAttributeTypeClassName(attributeDataType)}."

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = edge.attributes.find(_.name == attributeType) match {
    case None => true
    case Some(attribute) => attribute.value.headOption match {
      case None => true
      case Some(head) => head match {
        case _: MString => attribute.value.collect { case v: MString => v }.forall(_.attributeType == attributeDataType)
        case _: MBool => attribute.value.collect { case v: MBool => v }.forall(_.attributeType == attributeDataType)
        case _: MInt => attribute.value.collect { case v: MInt => v }.forall(_.attributeType == attributeDataType)
        case _: MDouble => attribute.value.collect { case v: MDouble => v }.forall(_.attributeType == attributeDataType)
        case _ => true
      }
    }
  }

  override val dslStatement: String = s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfScalarType "${Util.getAttributeTypeClassName(attributeDataType)}""""
}

object D24_EdgeAttributeScalarTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes
        .filter(att => Seq(ScalarType.String, ScalarType.Int, ScalarType.Bool, ScalarType.Double).contains(att.`type`))
        .map(att => new D24_EdgeAttributeScalarTypes(currentReference.name, att.name, att.`type`))
    }
}
