package de.htwg.zeta.parser.shape

import scala.collection.immutable.ListMap

import de.htwg.zeta.common.models.project.concept.elements.AttributeType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.IntType
import de.htwg.zeta.common.models.project.concept.elements.AttributeType.StringType
import de.htwg.zeta.common.models.project.concept.elements.AttributeValue.StringValue
import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass
import de.htwg.zeta.common.models.project.concept.elements.Method
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Editable
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.For
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Identifier
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Position
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Size
import de.htwg.zeta.parser.shape.parsetree.GeoModelAttributes.Style
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.GeoModelParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.RepeatingBoxParseTree
import de.htwg.zeta.parser.shape.parsetree.GeoModelParseTrees.TextfieldParseTree
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.NodeStyle
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMax
import de.htwg.zeta.parser.shape.parsetree.NodeAttributes.SizeMin
import de.htwg.zeta.parser.shape.parsetree.NodeParseTree

/**
 * Helper provides little helpers to create geo models with default values.
 */
//noinspection ScalaStyle
object Helper {

  def createTextfield(style: Option[Style] = None, identifier: String, children: List[GeoModelParseTree] = Nil): TextfieldParseTree = {
    TextfieldParseTree(
      style,
      Identifier(identifier),
      textBody = None,
      Position(100, 100),
      Size(100, 100),
      multiline = None,
      align = None,
      editable = None,
      children
    )
  }

  def createNode(identifier: String, conceptClass: String, edges: List[String] = Nil, style: Option[NodeStyle] = None, geoModels: List[GeoModelParseTree]): NodeParseTree = {
    NodeParseTree(
      identifier,
      conceptClass,
      edges,
      SizeMin(100, 100),
      SizeMax(200, 200),
      style,
      resizing = None,
      anchors = Nil,
      geoModels
    )
  }

  def createRepeatingBox(foreach: For, children: List[GeoModelParseTree]): RepeatingBoxParseTree = {
    RepeatingBoxParseTree(
      Editable(true),
      foreach,
      children
    )
  }

  def createConceptClass(name: String, inputReferences: List[String] = Nil, outputReferences: List[String] = Nil, attributes: List[MAttribute] = Nil, methods: List[Method] = Nil): MClass = {
    MClass(
      name,
      description = "",
      abstractness = false,
      superTypeNames = Nil,
      inputReferenceNames = inputReferences,
      outputReferenceNames = outputReferences,
      attributes = attributes,
      methods = methods
    )
  }

  def createConceptAttribute(name: String): MAttribute = {
    MAttribute(
      name,
      globalUnique = false,
      localUnique = false,
      StringType,
      StringValue(""),
      constant = false,
      singleAssignment = false,
      expression = "?",
      ordered = false,
      transient = true
    )
  }

  def createConceptMethod(name: String, returnType: AttributeType = IntType): Method = {
    Method(
      name,
      parameters = ListMap[String, AttributeType](),
      description = "",
      returnType,
      code = ""
    )
  }

}
