package de.htwg.zeta.server.model.modelValidator.generator

import de.htwg.zeta.server.model.modelValidator.generator.consistencyRules.MetaModelRule

case class ConsistencyCheckResult(valid: Boolean = true, failedRule: Option[MetaModelRule] = None)
