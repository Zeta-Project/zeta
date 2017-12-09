package de.htwg.zeta.server.model.modelValidator.generator

import de.htwg.zeta.server.model.modelValidator.generator.consistencyRules.ConsistencyRules
import de.htwg.zeta.common.models.modelDefinitions.metaModel.Concept

class ConceptConsistencyChecker(concept: Concept) {

  def checkConsistency(): ConsistencyCheckResult = ConsistencyRules.rules.foldLeft(ConsistencyCheckResult()) { (acc, rule) =>
    if (acc.valid) {
      if (rule.check(concept)) {
        acc
      }
      else {
        acc.copy(valid = false, failedRule = Some(rule))
      }
    } else {
      acc
    }
  }

}
