package generator.generators.vr.diagram

import java.nio.file.{Files, Paths}

import generator.model.diagram.Diagram

/**
  * Created by max on 12.11.16.
  */
object VrDiagramGenerator {
  private val EXTENDED_NEW_BEHAVIOR	= "vr-new-extended.html"
  private val SCENE	= "vr-scene.html"

  def doGenerate(diagram: Diagram, location:String) {
    val DEFAULT_DIAGRAM_LOCATION = location

    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+EXTENDED_NEW_BEHAVIOR), VrGeneratorNewBehavior.generate(diagram).getBytes)
    Files.write(Paths.get(DEFAULT_DIAGRAM_LOCATION+SCENE), VrGeneratorScene.generate(diagram).getBytes)
  }
}
