package de.htwg.zeta.server.model.modelValidator.generator

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

import de.htwg.zeta.common.models.entity.GraphicalDsl
import de.htwg.zeta.common.models.modelDefinitions.concept.Concept
import de.htwg.zeta.server.model.modelValidator.validator.ModelValidator
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.MetaModelDependent
import de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl.AttributesInEdges

case class ValidatorGeneratorResult(success: Boolean, result: String)

class ValidatorGenerator(graphicalDsl: GraphicalDsl) {

  def generateValidator(): ValidatorGeneratorResult = {
    val consistencyChecker = new ConceptConsistencyChecker(graphicalDsl.concept)

    consistencyChecker.checkConsistency() match {
      case ConsistencyCheckResult(valid, _) if valid => ValidatorGeneratorResult(success = true, result = doGenerate())
      case ConsistencyCheckResult(_, Some(failedRule)) => ValidatorGeneratorResult(success = false, result = s"$failedRule")
      case _ => ValidatorGeneratorResult(success = false, result = "error checking meta model consistency")
    }
  }

  def doGenerate(): String = generateRules(graphicalDsl.concept).mkString(",\n")

  def generateRules(metaModel: Concept): Seq[String] = rules(metaModel).map(_.dslStatement)

  def rules(metaModel: Concept): Seq[DslRule] = MetaModelDependent.rules.flatMap(_.generateFor(metaModel))
}

object ValidatorGenerator {

  def create(validatorContents: String): Option[ModelValidator] = {
    val classContents = addBoilerplate(validatorContents)
    val mirror = universe.runtimeMirror(getClass.getClassLoader)
    val toolBox = mirror.mkToolBox()
    val tree = toolBox.parse(classContents)
    val compiledCode = toolBox.compile(tree)

    compiledCode() match {
      case v: ModelValidator => Some(v)
      case _ => None
    }
  }

  def addBoilerplate(validatorContents: String): String = {

    val validatorClass = classOf[ModelValidator]
    val validatorName = validatorClass.getSimpleName
    val validatorPackage = validatorClass.getPackage.getName

    // just needed one random class inside the validatorDsl-Package
    val dslPackage = classOf[AttributesInEdges].getPackage.getName

    s"""import $validatorPackage.$validatorName
      |import $dslPackage._
      |new $validatorName {
      |override val metaModelDependentRules = Seq($validatorContents)
      |}""".stripMargin
  }


}
