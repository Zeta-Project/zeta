package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent._
import models.modelDefinitions.metaModel.elements.ScalarType

class AttributesOfTypeInEdges(attributeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int) = new D03_EdgeAttributesUpperBound(edgeType, attributeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D04_EdgeAttributesLowerBound(edgeType, attributeType, lowerBound)

  def areLocalUnique() = new D17_EdgeAttributesLocalUnique(edgeType, attributeType)

  def areGlobalUnique() = new D18_EdgeAttributesGlobalUnique(edgeType, attributeType)

  def areOfScalarType(scalarType: String): D24_EdgeAttributeScalarTypes = scalarType match {
    case "String" => new D24_EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.String)
    case "Int" => new D24_EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Int)
    case "Bool" => new D24_EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Bool)
    case "Double" => new D24_EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Double)
  }

  def areOfEnumType(enumType: String): D25_EdgeAttributeEnumTypes = new D25_EdgeAttributeEnumTypes(edgeType, attributeType, enumType)

}
