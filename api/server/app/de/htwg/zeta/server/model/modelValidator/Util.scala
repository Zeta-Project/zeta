package de.htwg.zeta.server.model.modelValidator

import scala.annotation.tailrec

import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.AttributeType
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MAttribute
import de.htwg.zeta.common.models.modelDefinitions.metaModel.elements.MClass

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (Mar 2017 - Sep 2017).
 *
 * Utility methods for handling the model and meta model graph inside the model validator.
 */
object Util {

  def stringSeqToSeqString(seq: Seq[String]): String = seq.mkString("Seq(\"", "\", \"", "\")")

  /* Meta Model Util */

  /**
   * Internal representation of [[MAttribute]].
   *
   * @param name             The name.
   * @param `type`           The type.
   * @param localUnique      Is it local unique?
   * @param globalUnique     Is it global unique?
   * @param constant         Is it constant?
   * @param ordered          Is it ordered?
   * @param singleAssignment Is it single assignment?
   * @param transient        Is it transient?
   * @param expression       The expression.
   * @param default          The default value.
   */
  case class Att(
      name: String,
      `type`: AttributeType,
      localUnique: Boolean,
      globalUnique: Boolean,
      constant: Boolean,
      ordered: Boolean,
      singleAssignment: Boolean,
      transient: Boolean,
      expression: String,
      default: String
  )

  /**
   * Internal representation of [[MClass]].
   *
   * @param name         The name.
   * @param superTypes   The super types.
   * @param subTypes     The sub types.
   * @param attributes   The attributes.
   * @param abstractness Is it abstract?
   * @param inputs       The inputs.
   * @param outputs      The outputs.
   */
  case class El(
      name: String,
      superTypes: Seq[String],
      subTypes: Seq[String],
      attributes: Seq[Att],
      abstractness: Boolean,
      inputs: Seq[String],
      outputs: Seq[String]
  )

  /**
   * Generates a graph from the meta model, which has all inheritable properties inherited downwards.
   *
   * Inheritable properties are:
   * * Attributes of classes.
   * * Inputs of classes.
   * * Outputs of classes.
   *
   * @throws IllegalStateException If the properties could not be inherited because of ambiguous inheritance relationships.
   * @param metaModel The meta model.
   * @return The sequence of elements containing the inherited properties.
   */
  def generateResolvedInheritanceGraph(metaModel: Concept): Seq[El] = {
    val simplifiedGraph = simplifyMetaModelGraph(metaModel)
    val inheritedAttributesGraph = inheritAttributes(simplifiedGraph)
    val inheritedInputsGraph = inheritInputs(inheritedAttributesGraph)
    val inheritedOutputsGraph = inheritOutputs(inheritedInputsGraph)
    inheritedOutputsGraph
  }

  /**
   * Translates the given meta model into the simpler representation using the internal classes El, Att, etc.
   * Nothing will be inherited yet, this is just a 1:1 mapping.
   *
   * @param metaModel The meta model.
   * @return The simplified translated graph.
   */
  def simplifyMetaModelGraph(metaModel: Concept): Seq[El] = {

    val allClasses = metaModel.classMap.values.toSeq

    def mapElement(el: MClass): El = El(
      name = el.name,
      superTypes = el.superTypeNames,
      subTypes = allClasses.filter(_.superTypeNames.contains(el.name)).map(_.name),
      attributes = el.attributes.map(mapAttribute),
      abstractness = el.abstractness,
      inputs = el.inputReferenceNames,
      outputs = el.outputReferenceNames
    )

    def mapAttribute(att: MAttribute): Att = Att(
      name = att.name,
      `type` = att.typ,
      localUnique = att.localUnique,
      globalUnique = att.globalUnique,
      constant = att.constant,
      ordered = att.ordered,
      singleAssignment = att.singleAssignment,
      transient = att.transient,
      expression = att.expression,
      default = att.default.toString
    )

    allClasses.map(mapElement)
  }

  /**
   * Inherit all the attributes down the inheritance relationships.
   *
   * @throws IllegalStateException On ambiguous attribute inheritance relationship.
   * @param graph The simplified meta model graph.
   * @return The graph with inherited attributes.
   */
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

  /**
   * Inherit all the inputs down the inheritance relationships.
   *
   * @throws IllegalStateException On ambiguous input inheritance relationship.
   * @param graph The simplified meta model graph.
   * @return The graph with inherited inputs.
   */
  def inheritInputs(graph: Seq[El]): Seq[El] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def inputInheritanceValid(inputs: Seq[String]): Boolean = {
      inputs.map { inputList =>
        inputList.headOption match {
          case Some(head) => inputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypes.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesInputs = superTypes.flatMap(_.inputs)
    val inferredInputs = superTypesInputs.filterNot(input => element.inputs.contains(input))

    if (!inputInheritanceValid(inferredInputs)) throw new IllegalStateException("ambiguous inputs")

    element.copy(inputs = element.inputs ++ inferredInputs.distinct)
  }

  /**
   * Inherit all the outputs down the inheritance relationships.
   *
   * @throws IllegalStateException On ambiguous output inheritance relationship.
   * @param graph The simplified meta model graph.
   * @return The graph with inherited outputs.
   */
  def inheritOutputs(graph: Seq[El]): Seq[El] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def outputInheritanceValid(outputs: Seq[String]): Boolean = {
      outputs.map { outputList =>
        outputList.headOption match {
          case Some(head) => outputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypes.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesOutputs = superTypes.flatMap(_.outputs)
    val inferredOutputs = superTypesOutputs.filterNot(output => element.outputs.contains(output))

    if (!outputInheritanceValid(inferredOutputs)) throw new IllegalStateException("ambiguous outputs")

    element.copy(outputs = element.outputs ++ inferredOutputs.distinct)
  }

  /**
   * Helper method for mapping the simplified graph elements from the root(s) top down the graph.
   *
   * @param graph     The graph.
   * @param mappingFn The function to apply to every element.
   * @return The mapped graph.
   */
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
