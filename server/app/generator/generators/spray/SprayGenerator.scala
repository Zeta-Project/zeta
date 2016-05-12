package generator.generators.spray

import java.nio.file.{Paths, Files}
import generator.model.diagram.Diagram
import generator.parser.Cache

/**
 * Created by julian on 07.02.16.
 */
object SprayGenerator {
  private val JOINTJS_STENCIL_FILENAME		= "stencil.js"
  private val JOINTJS_VALIDATOR_FILENAME	= "validator.js"
  private val JOINTJS_PATH 					= "jointjs-gen/diagram"
  private val JOINTJS_PATH_ROOT				= "jointjs-gen"
  private val JOINTJS_ZIP_ANT_FILENAME		= "buildZip.xml"
  private val JOINTJS_README_FILENAME		= "Readme.xml"
  private val JOINTJS_LINKHELPER_FILENAME	= "linkhelper.js"

  /**
   * This method is a long sequence of calling all templates for the code generation
   */
  def doGenerate(diagram: Diagram, location:String) {
    val DEFAULT_DIAGRAM_LOCATION = location
    val packageName = "Test"
    StencilGenerator.setPackageName(packageName)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_STENCIL_FILENAME), StencilGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_VALIDATOR_FILENAME), ValidatorGenerator.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_LINKHELPER_FILENAME), LinkhelperGenerator.generate(diagram).getBytes)
  }
}
