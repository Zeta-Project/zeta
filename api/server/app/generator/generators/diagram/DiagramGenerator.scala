package generator.generators.diagram

import java.nio.file.Paths
import java.nio.file.Files

import generator.model.diagram.Diagram
import models.file.File

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
  def doGenerate(diagram: Diagram, location: String): Unit = {
    val diaGen = doGenerateFile(diagram, location)

    diaGen.foreach(f => Files.write(Paths.get(f.name), f.content.getBytes))
  }

  def doGenerateFile(diagram: Diagram, location: String): List[File] = {
    val DEFAULT_DIAGRAM_LOCATION = location
    val packageName = "zeta"
    //FIXME setting variable in object
    StencilGenerator.setPackageName(packageName)

    List(
      File(DEFAULT_DIAGRAM_LOCATION + JOINTJS_STENCIL_FILENAME, StencilGenerator.generate(diagram)),
      File(DEFAULT_DIAGRAM_LOCATION + JOINTJS_VALIDATOR_FILENAME, ValidatorGenerator.generate(diagram)),
      File(DEFAULT_DIAGRAM_LOCATION + JOINTJS_LINKHELPER_FILENAME, LinkhelperGenerator.generate(diagram))
    )
  }
}

case class DiagramGenerator(stencil: String, validator: String, linkhelper: String)
