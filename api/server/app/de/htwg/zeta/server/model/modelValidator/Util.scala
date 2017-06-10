package de.htwg.zeta.server.model.modelValidator

import scala.annotation.tailrec

import models.modelDefinitions.metaModel.MetaModel
import models.modelDefinitions.metaModel.elements.AttributeType
import models.modelDefinitions.metaModel.elements.MAttribute
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReferenceLinkDef
import models.modelDefinitions.model.Model
import models.modelDefinitions.model.elements.Edge
import models.modelDefinitions.model.elements.ModelElement
import models.modelDefinitions.model.elements.Node

object Util {

  /* Model Util */

  def getNodes(elements: Seq[ModelElement]): Seq[Node] = elements.collect { case n: Node => n }

  def getNodes(model: Model): Seq[Node] = getNodes(model.elements.values.toSeq)

  def getEdges(elements: Seq[ModelElement]): Seq[Edge] = elements.collect { case e: Edge => e }

  def getEdges(model: Model): Seq[Edge] = getEdges(model.elements.values.toSeq)

  def stringSeqToSeqString(seq: Seq[String]): String = seq.mkString("Seq(\"", "\", \"", "\")")

  /* Meta Model Util */

  def getAttributeTypeClassName(attributeType: AttributeType): String = attributeType.getClass.getSimpleName.split("\\$").last

  case class Att(
      name: String,
      `type`: AttributeType,
      lowerBound: Int,
      upperBound: Int,
      localUnique: Boolean,
      globalUnique: Boolean,
      constant: Boolean,
      ordered: Boolean,
      singleAssignment: Boolean,
      transient: Boolean,
      expression: String,
      default: String
    )

  case class El(
      name: String,
      superTypes: Seq[String],
      subTypes: Seq[String],
      attributes: Seq[Att],
      abstractness: Boolean,
      inputs: Seq[LinkDef],
      outputs: Seq[LinkDef]
    )

  case class LinkDef(name: String, lowerBound: Int, upperBound: Int)

  def generateResolvedInheritanceGraph(metaModel: MetaModel): Seq[El] = {
    val simplifiedGraph = simplifyMetaModelGraph(metaModel)
    val inheritedAttributesGraph = inheritAttributes(simplifiedGraph)
    val inheritedInputsGraph = inheritInputs(inheritedAttributesGraph)
    val inheritedOutputsGraph = inheritOutputs(inheritedInputsGraph)
    inheritedOutputsGraph
  }

  def simplifyMetaModelGraph(metaModel: MetaModel): Seq[El] = {

    val allClasses = metaModel.classes.values.toSeq

    def mapElement(el: MClass): El = El(
      name = el.name,
      superTypes = el.superTypeNames,
      subTypes = allClasses.filter(_.superTypeNames.contains(el.name)).map(_.name),
      attributes = el.attributes.map(mapAttribute),
      abstractness = el.abstractness,
      inputs = el.inputs.map(mapLinkDef),
      outputs = el.outputs.map(mapLinkDef)
    )

    def mapAttribute(att: MAttribute): Att = Att(
      name = att.name,
      `type` = att.`type`,
      lowerBound = att.lowerBound,
      upperBound = att.upperBound,
      localUnique = att.localUnique,
      globalUnique = att.globalUnique,
      constant = att.constant,
      ordered = att.ordered,
      singleAssignment = att.singleAssignment,
      transient = att.transient,
      expression = att.expression,
      default = att.default.toString
    )

    def mapLinkDef(linkDef: MReferenceLinkDef): LinkDef = LinkDef(
      name = linkDef.referenceName,
      lowerBound = linkDef.lowerBound,
      upperBound = linkDef.upperBound
    )

    allClasses.map(mapElement)
  }

  def inheritAttributes(graph: Seq[El]): Seq[El] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def attributeInheritanceValid(attributes: Seq[Att]): Boolean = {
      attributes.groupBy(_.name).values.map { attList =>
        attList.headOption match {
          case Some(head) => attList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypes.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesAttributes = superTypes.flatMap(_.attributes)
    val inferredAttributes = superTypesAttributes.filterNot(att => element.attributes.map(_.name).contains(att.name))

    if (!attributeInheritanceValid(inferredAttributes)) throw new IllegalStateException("ambiguous attributes")

    element.copy(attributes = element.attributes ++ inferredAttributes.distinct)
  }

  def inheritInputs(graph: Seq[El]): Seq[El] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def inputInheritanceValid(inputs: Seq[LinkDef]): Boolean = {
      inputs.groupBy(_.name).values.map { inputList =>
        inputList.headOption match {
          case Some(head) => inputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypes.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesInputs = superTypes.flatMap(_.inputs)
    val inferredInputs = superTypesInputs.filterNot(input => element.inputs.map(_.name).contains(input.name))

    if (!inputInheritanceValid(inferredInputs)) throw new IllegalStateException("ambiguous inputs")

    element.copy(inputs = element.inputs ++ inferredInputs.distinct)
  }

  def inheritOutputs(graph: Seq[El]): Seq[El] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def outputInheritanceValid(outputs: Seq[LinkDef]): Boolean = {
      outputs.groupBy(_.name).values.map { outputList =>
        outputList.headOption match {
          case Some(head) => outputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypes.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesOutputs = superTypes.flatMap(_.outputs)
    val inferredOutputs = superTypesOutputs.filterNot(output => element.outputs.map(_.name).contains(output.name))

    if (!outputInheritanceValid(inferredOutputs)) throw new IllegalStateException("ambiguous outputs")

    element.copy(outputs = element.outputs ++ inferredOutputs.distinct)
  }

  def mapGraphElementsTopDown(graph: Seq[El])(mappingFn: (El, Seq[El]) => El): Seq[El] = {

    @tailrec
    def mapGraphElementsTopDownRec(elements: Seq[El], intermediateGraph: Seq[El]): Seq[El] = {
      if (elements.isEmpty) {
        intermediateGraph
      } else {
        val filteredElements = elements.filterNot(el => intermediateGraph.map(_.name).contains(el.name))

        val newIntermediateGraph = intermediateGraph ++ filteredElements.map(mappingFn(_, intermediateGraph))
        val subElements = filteredElements.flatMap(_.subTypes).map(elementName => graph.find(_.name == elementName).get)

        mapGraphElementsTopDownRec(subElements, newIntermediateGraph)
      }
    }

    val topElements = graph.filter(_.superTypes.isEmpty)
    mapGraphElementsTopDownRec(topElements, Seq[El]()).distinct

  }

}
