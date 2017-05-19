package de.htwg.zeta.server.generator.generators.diagram

import de.htwg.zeta.server.generator.model.diagram.Diagram
import models.file.File
import de.htwg.zeta.server.models.result.Result

/**
 * The DiagramGenerator Object
 */
object DiagramGenerator {
  private val JOINTJS_STENCIL_FILENAME = "stencil.js"
  private val JOINTJS_VALIDATOR_FILENAME = "validator.js"
  private val JOINTJS_LINKHELPER_FILENAME = "linkhelper.js"

  /**
   * generates the files stencil.js, validator.js and linkhelper.js
   */
  def doGenerateResult(diagram: Diagram): Result[List[File]] = {
    Result(() => doGenerateGenerators(diagram), "failed trying to create the Diagram generators")
  }

  /**
   * generates the files stencil.js, validator.js and linkhelper.js
   */
  def doGenerateGenerators(diagram: Diagram): List[File] = {
    val packageName = "zeta"
    // FIXME setting variable in object
    StencilGenerator.setPackageName(packageName)

    List(
      File(JOINTJS_STENCIL_FILENAME, StencilGenerator.generate(diagram)),
      File(JOINTJS_VALIDATOR_FILENAME, ValidatorGenerator.generate(diagram)),
      File(JOINTJS_LINKHELPER_FILENAME, LinkhelperGenerator.generate(diagram))
    )
  }
}
