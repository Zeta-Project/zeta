package generator.generators.vr.diagram

import java.nio.file.{Files, Paths}

import generator.generators.diagram.{LinkhelperGenerator, StencilGenerator, ValidatorGenerator}
import generator.model.diagram.Diagram

/**
  * Created by max on 12.11.16.
  */
object VrDiagramGenerator {
  private val EXTENDED_NEW_BEHAVIOR	= "vr-new-extended.html"

  def doGenerate(diagram: Diagram, location:String) {
    val DEFAULT_DIAGRAM_LOCATION = location
    val packageName = "zeta"
//    StencilGenerator.setPackageName(packageName)

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+EXTENDED_NEW_BEHAVIOR), VrGeneratorNewBehavior.generate(diagram).getBytes)
//    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_VALIDATOR_FILENAME), ValidatorGenerator.generate(diagram).getBytes)
//    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+JOINTJS_LINKHELPER_FILENAME), LinkhelperGenerator.generate(diagram).getBytes)

  }
}
