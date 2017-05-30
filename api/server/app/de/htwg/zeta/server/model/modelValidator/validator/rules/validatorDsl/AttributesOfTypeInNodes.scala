package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent._
import models.modelDefinitions.metaModel.elements.ScalarType

class AttributesOfTypeInNodes(attributeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new D03_NodeAttributesUpperBound(nodeType, attributeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new D04_NodeAttributesLowerBound(nodeType, attributeType, lowerBound)

  def areLocalUnique() = new D17_NodeAttributesLocalUnique(nodeType, attributeType)

  def areOfScalarType(scalarType: String): D24_NodeAttributeScalarTypes = scalarType match {
    case "String" => new D24_NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.String)
    case "Int" => new D24_NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Int)
    case "Bool" => new D24_NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Bool)
    case "Double" => new D24_NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Double)
  }

  def areOfEnumType(enumType: String): D25_NodeAttributeEnumTypes = new D25_NodeAttributeEnumTypes(nodeType, attributeType, enumType)

}
