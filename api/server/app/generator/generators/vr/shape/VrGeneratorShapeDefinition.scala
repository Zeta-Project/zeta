package generator.generators.vr.shape

import generator.model.shapecontainer.shape.Shape
import generator.model.shapecontainer.shape.geometrics.Ellipse
import generator.model.shapecontainer.shape.geometrics.GeometricModel
import generator.model.shapecontainer.shape.geometrics.Line
import generator.model.shapecontainer.shape.geometrics.PolyLine
import generator.model.shapecontainer.shape.geometrics.Polygon
import generator.model.shapecontainer.shape.geometrics.Rectangle
import generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import generator.model.shapecontainer.shape.geometrics.Text
import generator.model.shapecontainer.shape.geometrics.Wrapper
import generator.model.shapecontainer.shape.geometrics.layouts.CommonLayout
import models.file.File

import scala.util.Try


object VrGeneratorShapeDefinition {

  def doGenerateFile(shapes: List[Shape], location: String): List[File] = {
    shapes.filterNot(_.name != "rootShape").map(generateSingleFile(location))
  }


  private def generateSingleFile(location: String)(shape: Shape): File = {
    val FILENAME = "vr-" + shape.name + ".html"
    val polymerElement: String = generatePolymerElement(shape)
    File(location + FILENAME, polymerElement)
  }


  private def generatePolymerElement(shape: Shape): String = {
    val geometrics = shape.shapes.getOrElse(List())
    s"""
    ${generateImports()}
    ${generateImports(geometrics)}

    <dom-module id="vr-${shape.name}"><template></template></dom-module>

    <script>
    window.VrElement = window.VrElement || {};
    VrElement.${shape.name.capitalize} = Polymer({
      is: "vr-${shape.name}",
      observers: [ '_resizeConnection(xPos, yPos, width, height)' ],

      ${generateBehaviors()}
      ${generateReadyFunction(geometrics)}

      _resizeConnection(xPos, yPos, width, height ${generateTextArgs(geometrics)}) {
        if (this.getThreeJS()) {
          // compute width for fitting the text
          var newWidth = this._computeWidth();
          if (newWidth > 0) {
            this.width = newWidth < this.maxWidth ? newWidth : this.maxWidth;
          }
          this.fire('vr-resize', {});
        }
      },

      _computeWidth: function () {
        var dynamicTexture = new THREEx.DynamicTexture(this.width * 10, this.height * 10);
        dynamicTexture.texture.minFilter = THREE.NearestFilter;
        var maxWidth = 0;

        //{generateCalcMax(geometrics)}
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

  private def generateImports(): String = {
    s"""
       | <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-move.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-resize.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-delete.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-highlight.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-look.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-inner-sizing.html">
       | <link rel="import" href="/assets/prototyp/behaviors/vr-connect.html">
    """.stripMargin
  }

  private def generateImports(geometrics: List[GeometricModel]): String = {
    (for {g: GeometricModel <- geometrics} yield {
      g match {
        case g: Line => s"""<link rel="import" href="/assets/prototyp/elements/vr-polyline.html">"""
        // caution: Order is important because ellipse extends rectangle
        case g: Ellipse => s"""<link rel="import" href="/assets/prototyp/elements/vr-ellipse.html"> ${generateImports(g.children)}"""
        case g: Rectangle => s"""<link rel="import" href="/assets/prototyp/elements/vr-box.html"> ${generateImports(g.children)}"""
        case g: Polygon => s"""<link rel="import" href="/assets/prototyp/elements/vr-polygon.html"> ${generateImports(g.children)}"""
        case g: PolyLine => s"""<link rel="import" href="/assets/prototyp/elements/vr-polyline.html">"""
        case g: RoundedRectangle => "<!-- VR has no RoundedRectangle -->"
        case g: Text => "<!-- Text -->" // not needed. Just for develop purpose
        case _ => "<!-- no matching value -->" + g.toString
      }
    }).mkString
  }

  private def generateBehaviors(): String = {
    """
      | behaviors: [
      |   VrBehavior.Move,
      |   VrBehavior.Resize,
      |   VrBehavior.Highlight,
      |   VrBehavior.Delete,
      |   VrBehavior.Connect,
      |   VrBehavior.Look,
      |   VrBehavior.InnerSizing
      |],
    """.stripMargin
  }

  private def generateReadyFunction(geometrics: List[GeometricModel]): String = {
    val totalSize = (
      geometrics.map(_.asInstanceOf[CommonLayout]).map(g => g.size_height + g.y).max.asInstanceOf[Double],
      geometrics.map(_.asInstanceOf[CommonLayout]).map(g => g.size_width + g.x).max.asInstanceOf[Double]
    )
    s"""
       | ready: function() {
       |   var self = this;
       |   this.highlight = true;
       |   this.resizeVertical = true;
       |   this.resizeHorizontal = true;
       |   this.moveHorizontal = true;
       |   this.moveVertical = true;
       |   this.minMoveHorizontal = 0;
       |   this.maxMoveVertical = 0;
       |   this.height = ${totalSize._1.toInt}
       |   this.width = ${totalSize._2.toInt}
       |   ${createInnerSizing(geometrics, totalSize)}
       |
      |   function create(element, text, center, position, min, max, percentage) {
       |     //var element = new VrElement.Box();
       |     element.width = self.width;
       |     element.xPos = 0;
       |     element.text = text;
       |     element.textCener = center;
       |     Polymer.dom(self.root).appendChild(element);
       |     self.registerInnerSizingElement(element, position, min, max, percentage);
       |   }
       | },
    """.stripMargin
  }

  private def createInnerSizing(geometrics: List[GeometricModel], totalSize: (Double, Double)): String = {
    var textCount = 0
    (for {g: GeometricModel <- geometrics} yield {
      val element = getElement(g)
      g match {
        case text: Text => "" // just ignore texts at this point
        case c: CommonLayout => {
          val wrapper = c.asInstanceOf[Wrapper]
          val position = c.position.getOrElse((0, 0))
          val texts = wrapper.children.filter(_.isInstanceOf[Text]).map(_.asInstanceOf[Text])
          s"""
            ${texts.map(text => (s"""this.text${textCount} = ${text.textBody};""".stripMargin, textCount += 1)).map(_._1).mkString}
            create(
              new VrElement.${element.capitalize}(),
              ${
            if (hasText(wrapper)) {
              "this.text" + (textCount - 1)
            } else {
              "\"\""
            }
          },
              true,
              {
                x: ${position._1 / totalSize._2},
                y: -${position._2 / totalSize._1}
              },
              null,
              null,
              {
                height: ${c.size_height / totalSize._1},
                width: ${c.size_width / totalSize._2}
              }
            );
            ${createInnerSizing(wrapper.children, totalSize)}
          """
        }
        case _ => g.toString
      }
    }).mkString
  }

  private def hasText(wrapper: Wrapper): Boolean = {
    wrapper.children.exists(_.isInstanceOf[Text])
  }

  private def getElement(geometric: GeometricModel): String = {
    geometric match {
      case g: Line => "Line"
      case g: Ellipse => "ellipse"
      case g: Rectangle => "box"
      case g: Polygon => "polygon"
      case g: PolyLine => "polyline"
      case g: RoundedRectangle => "roundedrectangle"
      case g: Text => "text"
      case _ => geometric.toString
    }
  }

  private def generateCalcMax(goemetrics: List[GeometricModel]): String = {
    val numberOfTexts = goemetrics.map(getAllTexts).sum
    (for {i <- 0 until numberOfTexts} yield {
      "calcMax(this.text" + i + ");\n"
    }).mkString
  }

  private def generateTextArgs(geometrics: List[GeometricModel]): String = {
    val numberOfTexts = geometrics.map(getAllTexts).sum
    (for {i <- 0 until numberOfTexts} yield {
      " ,text" + i
    }).mkString
  }

  private def getAllTexts(geometric: GeometricModel): Int = {
    val wrapper = Try(geometric.asInstanceOf[Wrapper])
    if (wrapper.isSuccess) {
      wrapper.get.children.count(_.isInstanceOf[Text])
    } else {
      0
    }
  }

}
