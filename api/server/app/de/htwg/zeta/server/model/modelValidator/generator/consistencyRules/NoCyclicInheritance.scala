package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import scala.collection.breakOut
import scala.collection.mutable

import de.htwg.zeta.common.models.modelDefinitions.concept.Concept

class NoCyclicInheritance extends ConceptRule {

  override val name: String = getClass.getSimpleName
  override val description: String = "MClasses in the meta model must not have cyclic inheritance relationships."

  override def check(metaModel: Concept): Boolean = !isCyclic(metaModel.classes.map(el => el.name -> el.superTypeNames.toSeq).toMap)

  // http://www.geeksforgeeks.org/detect-cycle-in-a-graph/
  def isCyclic(adjacencyMap: Map[String, Seq[String]]): Boolean = {

    val visited = mutable.Map(adjacencyMap.keys.map(_ -> false)(breakOut): _*)
    val inStack = mutable.Map(adjacencyMap.keys.map(_ -> false)(breakOut): _*)

    def isCyclicRec(current: String): Boolean = {

      if (!visited(current)) {
        visited(current) = true
        inStack(current) = true

        val cycleFound = adjacencyMap(current).exists(adj => (!visited(adj) && isCyclicRec(adj)) || inStack(adj))

        if (cycleFound) {
          true
        } else {
          inStack(current) = false
          false
        }

      } else {
        inStack(current) = false
        false
      }

    }

    adjacencyMap.keys.exists(isCyclicRec)

  }

}
