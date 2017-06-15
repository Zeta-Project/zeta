package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType.StringType
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeEnumTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesLocalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.NodeAttributesUpperBound

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesOfTypeInNodes(attributeType: String, nodeType: String) {

  def haveUpperBound(upperBound: Int): NodeAttributesUpperBound = new NodeAttributesUpperBound(nodeType, attributeType, upperBound)

  def haveLowerBound(lowerBound: Int): NodeAttributesLowerBound = new NodeAttributesLowerBound(nodeType, attributeType, lowerBound)

  def areLocalUnique(): NodeAttributesLocalUnique = new NodeAttributesLocalUnique(nodeType, attributeType)

  def areOfScalarType(scalarType: String): NodeAttributeScalarTypes = scalarType match {
    case "String" => new NodeAttributeScalarTypes(nodeType, attributeType, StringType)
    case "Int" => new NodeAttributeScalarTypes(nodeType, attributeType, IntType)
    case "Bool" => new NodeAttributeScalarTypes(nodeType, attributeType, BoolType)
    case "Double" => new NodeAttributeScalarTypes(nodeType, attributeType, DoubleType)
  }

  def areOfEnumType(enumType: String): NodeAttributeEnumTypes = new NodeAttributeEnumTypes(nodeType, attributeType, enumType)

}
