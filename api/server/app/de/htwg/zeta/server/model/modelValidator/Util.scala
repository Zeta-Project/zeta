package de.htwg.zeta.server.model.modelValidator

import scala.annotation.tailrec

import de.htwg.zeta.common.models.project.concept.elements.MAttribute
import de.htwg.zeta.common.models.project.concept.elements.MClass

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (Mar 2017 - Sep 2017).
 *
 * Utility methods for handling the model and meta model graph inside the model validator.
 */
object Util {

  def stringSeqToSeqString(seq: Seq[String]): String = seq.mkString("Seq(\"", "\", \"", "\")")

  /**
   * Inherit all the attributes down the inheritance relationships.
   *
   * @throws IllegalStateException On ambiguous attribute inheritance relationship.
   * @param graph The simplified meta model graph.
   * @return The graph with inherited attributes.
   */
  def inheritAttributes(graph: Seq[MClass]): Seq[MClass] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def attributeInheritanceValid(attributes: Seq[MAttribute]): Boolean = {
      attributes.groupBy(_.name).values.map { attList =>
        attList.headOption match {
          case Some(head) => attList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypeNames.map(superType => intermediateGraph.find(_.name == superType).get)
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
  def inheritInputs(graph: Seq[MClass]): Seq[MClass] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def inputInheritanceValid(inputs: Seq[String]): Boolean = {
      inputs.map { inputList =>
        inputList.headOption match {
          case Some(head) => inputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypeNames.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesInputs = superTypes.flatMap(_.inputReferenceNames)
    val inferredInputs = superTypesInputs.filterNot(input => element.inputReferenceNames.contains(input))

    if (!inputInheritanceValid(inferredInputs)) throw new IllegalStateException("ambiguous inputs")

    element.copy(inputReferenceNames = element.inputReferenceNames ++ inferredInputs.distinct)
  }

  /**
   * Inherit all the outputs down the inheritance relationships.
   *
   * @throws IllegalStateException On ambiguous output inheritance relationship.
   * @param graph The simplified meta model graph.
   * @return The graph with inherited outputs.
   */
  def inheritOutputs(graph: Seq[MClass]): Seq[MClass] = mapGraphElementsTopDown(graph) { (element, intermediateGraph) =>

    def outputInheritanceValid(outputs: Seq[String]): Boolean = {
      outputs.map { outputList =>
        outputList.headOption match {
          case Some(head) => outputList.forall(_ == head)
          case None => true
        }
      }.forall(identity)
    }

    val superTypes = element.superTypeNames.map(superType => intermediateGraph.find(_.name == superType).get)
    val superTypesOutputs = superTypes.flatMap(_.outputReferenceNames)
    val inferredOutputs = superTypesOutputs.filterNot(output => element.outputReferenceNames.contains(output))

    if (!outputInheritanceValid(inferredOutputs)) throw new IllegalStateException("ambiguous outputs")

    element.copy(outputReferenceNames = element.outputReferenceNames ++ inferredOutputs.distinct)
  }

  /**
   * Helper method for mapping the simplified graph elements from the root(s) top down the graph.
   *
   * @param graph     The graph.
   * @param mappingFn The function to apply to every element.
   * @return The mapped graph.
   */
  def mapGraphElementsTopDown(graph: Seq[MClass])(mappingFn: (MClass, Seq[MClass]) => MClass): Seq[MClass] = {

    @tailrec
    def mapGraphElementsTopDownRec(elements: Seq[MClass], intermediateGraph: Seq[MClass]): Seq[MClass] = {
      if (elements.isEmpty) {
        intermediateGraph
      } else {
        val filteredElements = elements.filterNot(el => intermediateGraph.map(_.name).contains(el.name))

        val newIntermediateGraph = intermediateGraph ++ filteredElements.map(mappingFn(_, intermediateGraph))
        val subElements = filteredElements.flatMap(clazz => graph.filter(_.superTypeNames == clazz.name))

        mapGraphElementsTopDownRec(subElements, newIntermediateGraph)
      }
    }

    val topElements = graph.filter(_.superTypeNames.isEmpty)
    mapGraphElementsTopDownRec(topElements, Seq[MClass]()).distinct

  }

}
