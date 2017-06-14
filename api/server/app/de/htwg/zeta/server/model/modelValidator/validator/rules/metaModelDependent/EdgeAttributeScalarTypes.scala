package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent

import de.htwg.zeta.server.model.modelValidator.Util
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.GeneratorRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.SingleEdgeRule
import de.htwg.zeta.common.models.modelDefinitions.metaModel.MetaModel
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeValue
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MBool
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MDouble
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MInt
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarValue.MString
import de.htwg.zeta.common.models.modelDefinitions.model.elements.Edge

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class EdgeAttributeScalarTypes(val edgeType: String, val attributeType: String, val attributeDataType: AttributeType) extends SingleEdgeRule with DslRule {
  override val name: String = getClass.getSimpleName
  override val description: String =
    s"""Attributes of type $attributeType in edges of type $edgeType must be of data type
      |${Util.getAttributeTypeClassName(attributeDataType)}.""".stripMargin
  override val possibleFix: String =
    s"""Remove attribute values of attribute $attributeType in edge $edgeType which are not of data type
      |${Util.getAttributeTypeClassName(attributeDataType)}.""".stripMargin

  override def isValid(edge: Edge): Option[Boolean] = if (edge.`type`.name == edgeType) Some(rule(edge)) else None

  def rule(edge: Edge): Boolean = {

    def handleStrings(values: Seq[AttributeValue]): Boolean = values.collect { case v: MString => v }.forall(_.attributeType == attributeDataType)
    def handleBooleans(values: Seq[AttributeValue]): Boolean = values.collect { case v: MBool => v }.forall(_.attributeType == attributeDataType)
    def handleInts(values: Seq[AttributeValue]): Boolean = values.collect { case v: MInt => v }.forall(_.attributeType == attributeDataType)
    def handleDoubles(values: Seq[AttributeValue]): Boolean = values.collect { case v: MDouble => v }.forall(_.attributeType == attributeDataType)

    edge.attributes.find(_.name == attributeType) match {
      case None => true
      case Some(attribute) => attribute.value.headOption match {
        case None => true
        case Some(head) => head match {
          case _: MString => handleStrings(attribute.value)
          case _: MBool => handleBooleans(attribute.value)
          case _: MInt => handleInts(attribute.value)
          case _: MDouble => handleDoubles(attribute.value)
          case _ => true
        }
      }
    }
  }

  override val dslStatement: String =
    s"""Attributes ofType "$attributeType" inEdges "$edgeType" areOfScalarType "${Util.getAttributeTypeClassName(attributeDataType)}""""
}

object EdgeAttributeScalarTypes extends GeneratorRule {
  override def generateFor(metaModel: MetaModel): Seq[DslRule] = Util.getReferences(metaModel)
    .foldLeft(Seq[DslRule]()) { (acc, currentReference) =>
      acc ++ currentReference.attributes
        .filter(att => Seq(ScalarType.String, ScalarType.Int, ScalarType.Bool, ScalarType.Double).contains(att.`type`))
        .map(att => new EdgeAttributeScalarTypes(currentReference.name, att.name, att.`type`))
    }
}
