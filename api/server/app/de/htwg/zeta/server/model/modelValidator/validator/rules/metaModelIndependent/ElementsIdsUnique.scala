package de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelIndependent

import de.htwg.zeta.common.models.project.instance.GraphicalDslInstance
import de.htwg.zeta.server.model.modelValidator.validator.rules.ModelRule

/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
class ElementsIdsUnique extends ModelRule {
  override val name: String = getClass.getSimpleName
  override val description: String = "Element Identifiers must be unique."
  override val possibleFix: String = "Make duplicate identifiers unique."

  override def check(model: GraphicalDslInstance): Boolean = {
    val allNames = (model.nodeMap.keys ++ model.edgeMap.keys).toList

    // TODO this needs to be implemented an other way, since the structure of the model changed and nodes and edges are saved in own structures
    /*elements.groupBy(_).values
      .foldLeft(Seq[ModelValidationResult]()) { (acc, elements) => acc ++ elements.map {
        case n: Node => ModelValidationResult(rule = this, valid = elements.size == 1, modelElement = Some(n))
        case e: Edge => ModelValidationResult(rule = this, valid = elements.size == 1, modelElement = Some(e))
      }
    }*/
    true
  }
}
