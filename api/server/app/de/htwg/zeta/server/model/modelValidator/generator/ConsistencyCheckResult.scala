package de.htwg.zeta.server.model.modelValidator.generator

import de.htwg.zeta.server.model.modelValidator.generator.consistencyRules.ConceptRule

case class ConsistencyCheckResult(valid: Boolean = true, failedRule: Option[ConceptRule] = None)
