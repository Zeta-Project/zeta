package generator.generators.vr.shape

import java.nio.file.{Files, Paths}

import generator.model.shapecontainer.shape.geometrics._
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
    s"""
    <link rel="import" href="../bower_components/polymer/polymer.html">
    <link rel="import" href="../behaviors/vr-three.html">
    ${generateImports(shape.shapes.getOrElse(List()))}


    <dom-module id="vr-${shape.name}">
      <template>
        ${generateHtmlTemplate(shape.shapes.getOrElse(List()))}
      </template>
    </dom-module>


    <script>
    Polymer({
      is: "vr-${shape.name}",

      behaviors: [VrBehavior.ThreeJS],

      properties: {
          xPos: {
              type: Number,
              value: 0
          },
          yPos: {
              type: Number,
              value: 0
          }
      },

      ready: function() {
          this.getThreeJS().position.setX(this.xPos);
          this.getThreeJS().position.setY(this.yPos);
      },

      attached: function () {
          this._getChildren().forEach(function (box) {
              this.getThreeJS().add(box.getThreeJS());
          }.bind(this));
      },

      _getChildren: function () {
          // return all children except template element
          return Polymer.dom(this.root).children.filter(function (node) {
              return node.localName != 'template';
          });
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
        case g: Ellipse => s"""<link rel="import" href="./vr-ellipse.html"> ${generateImports(g.children)}"""
        case g: Rectangle => s"""<link rel="import" href="./vr-box.html"> ${generateImports(g.children)}"""
        case g: Polygon => "Polygon"
        case g: PolyLine => "PolyLine"
        case g: RoundedRectangle => "RoundedRectangle"
        case g: Text => "Text"
      }
    }).mkString
  }

  def generateHtmlTemplate(geometrics: List[GeometricModel]) : String = {
    (for(g : GeometricModel <- geometrics) yield {
      g match {
        case g: Line => "Line"
        // caution: Order is important because ellipse extends rectangle
        case g: Ellipse => {
          val (x, y) = g.position.getOrElse((0,0))
          s"""<vr-ellipse x="${x}" y="${y}" width="${g.size_width}" height="${g.size_width}">
              ${generateHtmlTemplate(g.children)}
          </vr-ellipse>"""
        }
        case g: Rectangle => {
          val (x, y) = g.position.getOrElse((0,0))
          s"""<vr-box x-pos="${x}" y-pos="${y}" width="${g.size_width}" height="${g.size_width}">
              ${generateHtmlTemplate(g.children)}
          </vr-box>"""
        }
        case g: Polygon => "Polygon"
        case g: PolyLine => "PolyLine"
        case g: RoundedRectangle => "RoundedRectangle"
        case g: Text => "Text"
      }
    }).mkString
  }

}