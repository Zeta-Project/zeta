package generator.generators.diagram

import java.nio.file.{Paths, Files}
import generator.model.diagram.Diagram

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
    val DEFAULT_DIAGRAM_LOCATION = location

    val diaGen = doGenerateFile(diagram)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_STENCIL_FILENAME), diaGen.stencil.getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_VALIDATOR_FILENAME), diaGen.validator.getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION + JOINTJS_LINKHELPER_FILENAME), diaGen.linkhelper.getBytes)
  }

  def doGenerateFile(diagram: Diagram): DiagramGenerator = {
    val packageName = "zeta"
    //FIXME setting variable in object
    StencilGenerator.setPackageName(packageName)
    DiagramGenerator(
      stencil = StencilGenerator.generate(diagram),
      validator = ValidatorGenerator.generate(diagram),
      linkhelper = LinkhelperGenerator.generate(diagram)
    )
  }
}

case class DiagramGenerator(stencil: String, validator: String, linkhelper: String)
