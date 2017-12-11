package de.htwg.zeta.server.model.modelValidator.generator.consistencyRules

object ConsistencyRules {

  val rules: Seq[ConceptRule] = Seq(
    new NoCyclicInheritance,
    new NoAmbiguousAttributes,
    new NoAmbiguousInputs,
    new NoAmbiguousOutputs
  )

}
