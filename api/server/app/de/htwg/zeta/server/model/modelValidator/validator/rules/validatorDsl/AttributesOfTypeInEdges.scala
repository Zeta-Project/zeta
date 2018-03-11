package de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl

import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.BoolType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.DoubleType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.AttributeType.StringType
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeEnumTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgeAttributeScalarTypes
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.EdgesAttributesGlobalUnique

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class AttributesOfTypeInEdges(attributeType: String, edgeType: String) {

  def areGlobalUnique(): EdgesAttributesGlobalUnique = new EdgesAttributesGlobalUnique(edgeType, attributeType)

  def areOfScalarType(scalarType: String): EdgeAttributeScalarTypes = scalarType match {
    case "String" => new EdgeAttributeScalarTypes(edgeType, attributeType, StringType)
    case "Int" => new EdgeAttributeScalarTypes(edgeType, attributeType, IntType)
    case "Bool" => new EdgeAttributeScalarTypes(edgeType, attributeType, BoolType)
    case "Double" => new EdgeAttributeScalarTypes(edgeType, attributeType, DoubleType)
  }

  def areOfEnumType(enumType: String): EdgeAttributeEnumTypes = new EdgeAttributeEnumTypes(edgeType, attributeType, enumType)

}
