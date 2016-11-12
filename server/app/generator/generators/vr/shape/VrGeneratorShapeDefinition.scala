package generator.generators.vr.shape

import java.nio.file.{Files, Paths}
import generator.model.shapecontainer.shape.{Compartment, Shape}

/**
  * Created by max on 08.11.16.
  */
object VrGeneratorShapeDefinition {
  def generate(shapes: Iterable[Shape], packageName: String, location: String) = {
    for(shape <- shapes) {generateFile(shape, packageName, location)}
  }

  def generateFile(shape: Shape, packageName: String, DEFAULT_SHAPE_LOCATION: String) = {
    val FILENAME = "vr-" + shape.name + ".html"

    val polymerElement = generatePolymerElement(shape)

    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), polymerElement.getBytes())
  }

  def generatePolymerElement(shape: Shape) = {
    s"""
    <dom-module id="vr-${shape.name}">
      <template>
      </template>
    </dom-module>

    <script>
    Polymer({
      is: "vr-${shape.name}",
    });
    </script>

     """
  }

}