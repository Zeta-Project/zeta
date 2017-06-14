package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeEnumTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributesGlobalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributesLocalUnique
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributesLowerBound
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributesUpperBound
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.ScalarType

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesOfTypeInEdges(attributeType: String, edgeType: String) {

  def haveUpperBound(upperBound: Int): EdgeAttributesUpperBound = new EdgeAttributesUpperBound(edgeType, attributeType, upperBound)

  def haveLowerBound(lowerBound: Int): EdgeAttributesLowerBound = new EdgeAttributesLowerBound(edgeType, attributeType, lowerBound)

  def areLocalUnique(): EdgeAttributesLocalUnique = new EdgeAttributesLocalUnique(edgeType, attributeType)

  def areGlobalUnique(): EdgeAttributesGlobalUnique = new EdgeAttributesGlobalUnique(edgeType, attributeType)

  def areOfScalarType(scalarType: String): EdgeAttributeScalarTypes = scalarType match {
    case "String" => new EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.String)
    case "Int" => new EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Int)
    case "Bool" => new EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Bool)
    case "Double" => new EdgeAttributeScalarTypes(edgeType, attributeType, ScalarType.Double)
  }

  def areOfEnumType(enumType: String): EdgeAttributeEnumTypes = new EdgeAttributeEnumTypes(edgeType, attributeType, enumType)

}
