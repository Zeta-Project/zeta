package generator.generators.vr.shape

import java.nio.file.{Files, Paths}

import generator.model.shapecontainer.shape.geometrics._
import generator.model.shapecontainer.shape.geometrics.layouts.CommonLayout
import generator.model.shapecontainer.shape.Shape

import scala.util.Try

/**
  * Created by max on 08.11.16.
  */
object VrGeneratorShapeDefinition {
  def generate(shapes: Iterable[Shape], packageName: String, location: String) = {
    for(shape <- shapes) {generateFile(shape, packageName, location)}
  }

  private def generateFile(shape: Shape, packageName: String, DEFAULT_SHAPE_LOCATION: String) = {
    if(shape.name != "rootShape") {
      val FILENAME = "vr-" + shape.name + ".html"

      val polymerElement = generatePolymerElement(shape)

      Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), polymerElement.getBytes())
    }
  }

  private def generatePolymerElement(shape: Shape) = {
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

        function create(element, text, center, min, max, percentage) {
          //var element = new VrElement.Box();
          element.width = self.width;
          element.xPos = 0;
          element.text = text;
          element.textCener = center;
          Polymer.dom(self.root).appendChild(element);
          self.registerInnerSizingElement(element, min, max, percentage);
        }
      },

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

  private def generateImports(geometrics: List[GeometricModel]) : String = {
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
        case _ => g
      }
    }).mkString
  }

  private def createInnerSizing(geometrics: List[GeometricModel], totalSize: (Double, Double)) : String = {
    var textCount = 0
    val geos = geometrics.reverse
    (for(g: GeometricModel <- geos) yield {
      val element = getElement(g)
      g match {
        case text: Text => ""
        case c: CommonLayout =>  {
          val wrapper = c.asInstanceOf[Wrapper]
          val texts = wrapper.children.filter(_.isInstanceOf[Text]).map(_.asInstanceOf[Text])
          s"""${texts.map(text => (s"""this.text${textCount} = ${text.textBody};""".stripMargin, textCount += 1)).map(_._1).mkString}
              create(new VrElement.${element.capitalize}() , ${if(hasText(wrapper)) {"this.text" + (textCount - 1)} else { "\"\""}}, true, null, null, { height: ${c.size_height / totalSize._1}, width: ${c.size_width / totalSize._2}});
              ${createInnerSizing(wrapper.children, totalSize)}
          """
        }
        case _ => g.toString()
      }
    }).mkString
  }

  private def hasText(wrapper: Wrapper) : Boolean = {
    wrapper.children.exists(_.isInstanceOf[Text])
  }

  private def getElement(geometric: GeometricModel) = {
    geometric match {
      case g: Line => "Line"
      case g: Ellipse => "ellipse"
      case g: Rectangle => "box"
      case g: Polygon => "polygon"
      case g: PolyLine => "polyline"
      case g: RoundedRectangle => "roundedrectangle"
      case g: Text => "text"
      case _ => geometric.toString()
    }
  }

  private def generateCalcMax(goemetrics: List[GeometricModel]) = {
    val numberOfTexts = goemetrics.map(getAllTexts(_)).sum
    (for(i <- 0 until numberOfTexts) yield { "calcMax(this.text" + i + ");\n" }).mkString
  }

  private def generateTextArgs(geometrics: List[GeometricModel]) = {
    val numberOfTexts = geometrics.map(getAllTexts(_)).sum
    (for(i <- 0 until numberOfTexts) yield { " ,text" + i}).mkString
  }

  private def getAllTexts(geometric: GeometricModel) : Int = {
    val wrapper = Try(geometric.asInstanceOf[Wrapper])
    if(wrapper.isSuccess) {
      wrapper.get.children.count(_.isInstanceOf[Text])
    } else {
      0
    }
  }

}
