package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

import de.htwg.zeta.server.model.modelValidator.Util
import models.modelDefinitions.metaModel.MetaModel

import scala.collection.breakOut
import scala.collection.mutable

class NoCyclicInheritance extends MetaModelRule {

  override val name: String = getClass.getSimpleName
  override val description: String = "MClasses in the meta model must not have cyclic inheritance relationships."

  override def check(metaModel: MetaModel): Boolean = !isCyclic(Util.getClasses(metaModel).map(el => el.name -> el.superTypes.map(_.name)).toMap)

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
