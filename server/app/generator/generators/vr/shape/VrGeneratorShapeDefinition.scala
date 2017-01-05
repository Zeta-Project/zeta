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
    <link rel="import" href="/assets/prototyp/behaviors/vr-inner-sizing.html">
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
        //VrBehavior.Highlight,
        VrBehavior.Delete,
        VrBehavior.ConnectExtended,
        VrBehavior.Look,
        VrBehavior.InnerSizing
      ],

      observers: [ '_resizeConnection(xPos, yPos, width, height)' ],

      ready: function() {
        // TODO: create inner sizing
        // create(this.text1, true, {height: 25}, {height: 25}, {height: 0.2, width:1});

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

      /*ready: function() {
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
      }*/
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