package generator.generators.vr.shape

import java.nio.file.{Files, Paths}

import generator.model.shapecontainer.shape.geometrics._
import generator.model.shapecontainer.shape.geometrics.layouts.CommonLayout
import generator.model.shapecontainer.shape.{Compartment, Shape}

/**
  * Created by max on 08.11.16.
  */
object VrGeneratorShapeDefinition {
  def generate(shapes: Iterable[Shape], packageName: String, location: String) = {
    for(shape <- shapes) {generateFile(shape, packageName, location)}
  }

  def generateFile(shape: Shape, packageName: String, DEFAULT_SHAPE_LOCATION: String) = {
    if(shape.name != "rootShape") {
      val FILENAME = "vr-" + shape.name + ".html"

      val polymerElement = generatePolymerElement(shape)

      Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), polymerElement.getBytes())
    }
  }

  def generatePolymerElement(shape: Shape) = {
    val geometrics = shape.shapes.getOrElse(List())
    val totalSize = (geometrics.map(_.asInstanceOf[CommonLayout]).map(g => g.size_height + g.y).max.asInstanceOf[Double],
                     geometrics.map(_.asInstanceOf[CommonLayout]).map(g => g.size_width + g.x).max.asInstanceOf[Double])

    s"""
    <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-move.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-resize.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-delete.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-highlight.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-look.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-inner-sizing.html">
    <link rel="import" href="vr-connect-extended.html">
    ${generateImports(geometrics)}


    <dom-module id="vr-${shape.name}"><template></template></dom-module>


    <script>
    window.VrElement = window.VrElement || {};
    VrElement.${shape.name.capitalize} = Polymer({
      is: "vr-${shape.name}",

      behaviors: [
        VrBehavior.Move,
        VrBehavior.Resize,
        VrBehavior.Highlight,
        VrBehavior.Delete,
        VrBehavior.ConnectExtended,
        VrBehavior.Look,
        VrBehavior.InnerSizing
      ],

      observers: [ '_resizeConnection(xPos, yPos, width, height)' ],

      ready: function() {
        var self = this;
        this.highlight = true
        this.resizeVertical = true
        this.resizeHorizontal = true
        this.moveHorizontal = true
        this.moveVertical = true
        this.height = ${totalSize._1.toInt}
        this.width = ${totalSize._2.toInt}
       ${createInnerSizing(geometrics, totalSize)}

        function create(text, center, min, max, percentage) {
          var element = new VrElement.Box();
          element.width = self.width;
          element.xPos = 0;
          element.text = text;
          element.textCener = center;
          Polymer.dom(self.root).appendChild(element);
          self.registerInnerSizingElement(element, min, max, percentage);
        }
      },

      // TODO: adjust
      _resizeConnection(xPos, yPos, width, height) { this.fire('vr-resize', {}); },

      _computeWidth: function () {
        var dynamicTexture = new THREEx.DynamicTexture(this.width * 10, this.height * 10);
        dynamicTexture.texture.minFilter = THREE.NearestFilter;
        var maxWidth = 0;

        TODO: Generate
        /*calcMax(this.text1);
        calcMax(this.text2);
        calcMax(this.text3);*/
        function calcMax(text) {
          var texts = text.split(THREEx.linebreak);
          texts.forEach(function (text) {
            var newWidth = dynamicTexture.computeWidth(text, "64px Verdana");
            if (newWidth > maxWidth) {
              maxWidth = newWidth;
            }
          });
        }
        return maxWidth / 10;
      }

    });
    </script>
    """
  }

  def generateImports(geometrics: List[GeometricModel]) : String = {
    (for(g : GeometricModel <- geometrics) yield {
      g match {
        case g: Line => "Line"
        // caution: Order is important because ellipse extends rectangle
        case g: Ellipse => s"""<link rel="import" href="/assets/prototyp/elements/vr-ellipse.html"> ${generateImports(g.children)}"""
        case g: Rectangle => s"""<link rel="import" href="/assets/prototyp/elements/vr-box.html"> ${generateImports(g.children)}"""
        case g: Polygon => "Polygon"
        case g: PolyLine => "PolyLine"
        case g: RoundedRectangle => "RoundedRectangle"
        case g: Text => "Text"
      }
    }).mkString
  }

  def createInnerSizing(geometrics: List[GeometricModel], totalSize: (Double, Double)) : String = {
    (for(g: GeometricModel <- geometrics) yield {
      g match {
        case g: CommonLayout =>  {
          val wrapper = g.asInstanceOf[Wrapper]
          s"""create("", true, null, null, { height: ${g.size_height / totalSize._1}, width: ${g.size_width / totalSize._2}});
             ${createInnerSizing(wrapper.children, totalSize)}
           """
        }
      }
    }).mkString
  }

}