package de.htwg.zeta.server.generator.generators.diagram

import java.util.UUID

import de.htwg.zeta.common.models.entity.File
import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.model.result.Unreliable

/**
 * The DiagramGenerator Object
 */
object DiagramGenerator {
  private val jointsJsStencilFilename = "stencil.js"
  private val jointsJsValidatorFilename = "validator.js"
  private val jointsJsLinkHelperFilename = "linkhelper.js"

  /**
   * generates the files stencil.js, validator.js and linkhelper.js
   */
  def doGenerateResult(diagram: Diagram, metaModelId: UUID): Unreliable[List[File]] = {
    Unreliable(() => doGenerateGenerators(diagram, metaModelId), "failed trying to create the Diagram generators")
  }

  /**
   * generates the files stencil.js, validator.js and linkhelper.js
   */
  def doGenerateGenerators(diagram: Diagram, metaModelId: UUID): List[File] = {

    val packageName = "zeta"
    // FIXME setting variable in object
    StencilGenerator.setPackageName(packageName)

    List(
      File(metaModelId, jointsJsStencilFilename, StencilGenerator.generate(diagram)),
      File(metaModelId, jointsJsValidatorFilename, ValidatorGenerator.generate(diagram)),
      File(metaModelId, jointsJsLinkHelperFilename, LinkhelperGenerator.generate(diagram))
    )
  }
}
