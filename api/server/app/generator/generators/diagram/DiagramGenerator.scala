package generator.generators.diagram

import java.nio.file.Paths
import java.nio.file.Files
import generator.model.diagram.Diagram

/**
 * The DiagramGenerator Object
 */
object DiagramGenerator {
  private val JOINTJS_STENCIL_FILENAME = "stencil.js"
  private val JOINTJS_VALIDATOR_FILENAME = "validator.js"
  private val JOINTJS_LINKHELPER_FILENAME = "linkhelper.js"

  /**
   *  generates the files stencil.js, validator.js and linkhelper.js
   */
  def doGenerate(diagram: Diagram, location: String): Unit = {
    val DEFAULT_DIAGRAM_LOCATION = location
    val packageName = "zeta"
    StencilGenerator.setPackageName(packageName)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_STENCIL_FILENAME), StencilGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_VALIDATOR_FILENAME), ValidatorGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_LINKHELPER_FILENAME), LinkhelperGenerator.generate(diagram).getBytes)
  }
}
