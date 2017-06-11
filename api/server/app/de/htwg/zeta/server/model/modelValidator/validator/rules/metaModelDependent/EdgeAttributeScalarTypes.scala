package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeType
import models.modelDefinitions.metaModel.elements.AttributeValue
import models.modelDefinitions.metaModel.elements.ScalarType
import models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import models.modelDefinitions.metaModel.elements.ScalarValue.MString
import models.modelDefinitions.model.elements.Edge

class EdgeAttributeScalarTypes(edgeType: String, attributeType: String, attributeDataType: AttributeType) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"""Attributes of type $attributeType in edges of type $edgeType must be of data type
      |${Util.getAttributeTypeClassName(attributeDataType)}.""".stripMargin
  override val possibleFix: String =
    s"""Remove attribute values of attribute $attributeType in edge $edgeType which are not of data type
      |${Util.getAttributeTypeClassName(attributeDataType)}.""".stripMargin

  override def isValid(edge: Edge): Option[Boolean] = if (edge.reference.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Boolean = values.collect { case v: MString => v }.forall(_.attributeType == attributeDataType)
    def handleBooleans(values: Seq[AttributeValue]): Boolean = values.collect { case v: MBool => v }.forall(_.attributeType == attributeDataType)
    def handleInts(values: Seq[AttributeValue]): Boolean = values.collect { case v: MInt => v }.forall(_.attributeType == attributeDataType)
    def handleDoubles(values: Seq[AttributeValue]): Boolean = values.collect { case v: MDouble => v }.forall(_.attributeType == attributeDataType)

    edge.attributes.get(attributeType) match {
      case None => true
      case Some(attribute) => attribute.headOption match {
        case None => true
        case Some(head) => head match {
          case _: MString => handleStrings(attribute)
          case _: MBool => handleBooleans(attribute)
          case _: MInt => handleInts(attribute)
          case _: MDouble => handleDoubles(attribute)
          case _ => true
        }
      }
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfScalarType "${Util.getAttributeTypeClassName(attributeDataType)}""""
}

object EdgeAttributeScalarTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = metaModel.references.values
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes
        .filter(att => Seq(ScalarType.String, ScalarType.Int, ScalarType.Bool, ScalarType.Double).contains(att.`type`))
        .map(att => new EdgeAttributeScalarTypes(currentReference.name, att.name, att.`type`))
    }
}
