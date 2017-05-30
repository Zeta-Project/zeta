package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesUpperBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesLocalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeEnumTypes
import models.modelDefinitions.metaModel.elements.ScalarType

class AttributesOfTypeInNodes(attributeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int) = new NodeAttributesUpperBound(nodeType, attributeType, upperBound)

  def haveLowerBound(lowerBound: Int) = new NodeAttributesLowerBound(nodeType, attributeType, lowerBound)

  def areLocalUnique() = new NodeAttributesLocalUnique(nodeType, attributeType)

  def areOfScalarType(scalarType: String): NodeAttributeScalarTypes = scalarType match {
    case "String" => new NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.String)
    case "Int" => new NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Int)
    case "Bool" => new NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Bool)
    case "Double" => new NodeAttributeScalarTypes(nodeType, attributeType, ScalarType.Double)
  }

  def areOfEnumType(enumType: String): NodeAttributeEnumTypes = new NodeAttributeEnumTypes(nodeType, attributeType, enumType)

}
