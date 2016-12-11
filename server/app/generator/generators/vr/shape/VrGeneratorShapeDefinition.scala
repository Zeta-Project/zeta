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
    <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-move.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-resize.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-delete.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-highlight.html">
    <link rel="import" href="/assets/prototyp/behaviors/vr-look.html">
    <link rel="import" href="vr-connect-extended.html">
    ${generateImports(shape.shapes.getOrElse(List()))}


    <dom-module id="vr-${shape.name}">
      <template>
        ${generateHtmlTemplate(shape.shapes.getOrElse(List()))}
      </template>
    </dom-module>


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
        VrBehavior.Look
      ],

      observers: [ '_resizeConnection(xPos, yPos, width, height)' ],

      _resizeConnection(xPos, yPos, width, height) { this.fire('vr-resize', {}); },

      ready: function() {
       this.getThreeJS().on('mousedown', this._onMousedown.bind(this));
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
      },

      _onMousedown: function() {
        var self = this;

        if (!this.gui) {
          var Menu = function () {
            this.width = self.width;
            this.height = self.height;
            this.yPos = self.yPos;
          };
          var text = new Menu();
          this.gui = new dat.GUI();
          var yPosController = this.gui.add(text, 'yPos', -200, 200);
          var widthController = this.gui.add(text, 'width', 30, 200);
          var heightController = this.gui.add(text, 'height', 30, 200);

          yPosController.onFinishChange(function (yPos) {
            self.yPos = yPos;
          });
          widthController.onFinishChange(function (width) {
            self.width = width;
          });
          heightController.onFinishChange(function (height) {
            self.height = height;
          });
        } else {
          this.gui.destroy();
          this.gui = null;
        }
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

  def generateHtmlTemplate(geometrics: List[GeometricModel]) : String = {
    (for(g : GeometricModel <- geometrics) yield {
      g match {
        case g: Line => "Line"
        // caution: Order is important because ellipse extends rectangle
        case g: Ellipse => {
          val (x, y) = g.position.getOrElse((0,0))
          s"""<vr-ellipse x="${x}" y="${y}" width="${g.size_width}" height="${g.size_width}" depth="10">
              ${generateHtmlTemplate(g.children)}
          </vr-ellipse>"""
        }
        case g: Rectangle => {
          val (x, y) = g.position.getOrElse((0,0))
          s"""<vr-box x-pos="${x}" y-pos="${y}" width="${g.size_width}" height="${g.size_width}" depth="10">
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