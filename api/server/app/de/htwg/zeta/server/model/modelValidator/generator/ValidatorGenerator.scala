package de.htwg.zeta.server.model.modelValidator.generator

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import de.htwg.zeta.server.model.modelValidator.validator.ModelValidator
import de.htwg.zeta.server.model.modelValidator.validator.rules.DslRule
import de.htwg.zeta.server.model.modelValidator.validator.rules.metaModelDependent.MetaModelDependent
import models.document.MetaModelEntity
import models.modelDefinitions.metaModel.MetaModel

import scala.util.Try

case class ValidatorGeneratorResult(success: Boolean, result: String, created: Boolean)

class ValidatorGenerator(metaModelEntity: MetaModelEntity) {

  val consistencyChecker = new MetaModelConsistencyChecker(metaModelEntity.metaModel)

  def getGenerator(forceRegenerate: Boolean): ValidatorGeneratorResult = {

    val validatorExists = ValidatorGenerator.validatorExists(metaModelEntity.id())

    lazy val validatorDeprecated = ValidatorGenerator.load(metaModelEntity.id()) match {
      case Some(validator) => validator.metaModelRevision != metaModelEntity._rev
      case _ => true
    }

    if (forceRegenerate || !validatorExists || validatorDeprecated) {

      consistencyChecker.checkConsistency() match {
        case ConsistencyCheckResult(valid, _) if valid =>

          val validator = doGenerate()
          new PrintWriter(ValidatorGenerator.filePath(metaModelEntity.id())) {
            write(validator)
            close()
          }
          ValidatorGeneratorResult(success = true, result = validator, created = true)

        case ConsistencyCheckResult(_, Some(failedRule)) => ValidatorGeneratorResult(success = false, result = s"failed rule: $failedRule", created = false)

        case _ => ValidatorGeneratorResult(success = false, result = "error checking meta model consistency", created = false)

      }

    } else {

      ValidatorGeneratorResult(success = true, ValidatorGenerator.readFile(ValidatorGenerator.filePath(metaModelEntity.id())), created = false)

    }

  }

  def doGenerate(): String =
    s"""override val metaModelId = "${metaModelEntity.id()}"
       |override val metaModelRevision = "${metaModelEntity._rev}"
       |
       |override val metaModelDependentRules = ${generateRules(metaModelEntity.metaModel).mkString("Seq(\n    ", ",\n    ", "\n  )")}
     """.stripMargin

  def generateRules(metaModel: MetaModel): Seq[String] = rules(metaModel).map(_.dslStatement)

  def rules(metaModel: MetaModel): Seq[DslRule] = MetaModelDependent.rules.flatMap(_.generateFor(metaModel))
}

object ValidatorGenerator {

  def deleteValidator(metaModelId: String): Boolean = deleteFile(filePath(metaModelId))

  def validatorExists(metaModelId: String): Boolean = Files.exists(Paths.get(filePath(metaModelId)))

  def filePath(metaModelUuid: String): String = {
    val root = {
      val pwd = System.getenv("PWD")
      if (pwd != null) pwd else System.getProperty("user.dir")
    }
    s"$root/server/app/assets/modelValidator/generated/$metaModelUuid"
  }

  def readFile(filePath: String): String = {
    val source = scala.io.Source.fromFile(filePath)
    val contents = Try(source.mkString).getOrElse("")
    source.close()
    contents
  }

  def deleteFile(filePath: String): Boolean = new File(filePath).delete()

  def load(metaModelId: String): Option[ModelValidator] = {

    import reflect.runtime.universe
    import scala.tools.reflect.ToolBox

    if (validatorExists(metaModelId)) {

      val fileContents = readFile(filePath(metaModelId))
      val mirror = universe.runtimeMirror(getClass.getClassLoader)
      val tb = mirror.mkToolBox()
      val tree = tb.parse(addBoilerplate(fileContents))
      val compiledCode = tb.compile(tree)

      compiledCode() match {
        case v: ModelValidator => Some(v)
        case _ => None
      }

    } else None
  }

  def addBoilerplate(fileContents: String): String =
    s"""import de.htwg.zeta.server.model.modelValidator.validator.ModelValidator
       |import de.htwg.zeta.server.model.modelValidator.validator.rules.validatorDsl._
       |new ModelValidator {
       |$fileContents
       |}
       |""".stripMargin

}
