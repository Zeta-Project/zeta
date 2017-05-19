package de.htwg.zeta.server.generator.generators.vr.shape

import scala.annotation.tailrec

import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Ellipse
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.GeometricModel
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Line
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.PolyLine
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Polygon
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Rectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.RoundedRectangle
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Text
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.Wrapper
import de.htwg.zeta.server.generator.model.shapecontainer.shape.geometrics.layouts.CommonLayout
import models.file.File
import de.htwg.zeta.server.models.result.Result


object VrGeneratorShapeDefinition {


  def doGenerateResult(shapes: List[Shape]): Result[List[File]] = {
    Result(() => doGenerateGenerators(shapes), "failed trying to create the vr Shape generators")
  }


  private def doGenerateGenerators(shapes: List[Shape]): List[File] = {
    shapes.filterNot(_.name != "rootShape").map(generateSingleFile)
  }


  private def generateSingleFile(shape: Shape): File = {
    val FILENAME = "vr-" + shape.name + ".html"
    val polymerElement: String = generatePolymerElement(shape)
    File(FILENAME, polymerElement)
  }


  private def generatePolymerElement(shape: Shape): String = {
    val geometrics = shape.shapes.getOrElse(List())
    s"""
      |${generateImports()}
      |${generateImports(geometrics)}
      |
      |<dom-module id="vr-${shape.name}"><template></template></dom-module>
      |
      |<script>
      |window.VrElement = window.VrElement || {};
      |VrElement.${shape.name.capitalize} = Polymer({
      |  is: "vr-${shape.name}",
      |  observers: [ '_resizeConnection(xPos, yPos, width, height)' ],
      |
      |  ${generateBehaviors()}
      |  ${generateReadyFunction(geometrics)}
      |
      |  _resizeConnection(xPos, yPos, width, height ${generateTextArgs(geometrics)}) {
      |    if (this.getThreeJS()) {
      |      // compute width for fitting the text
      |      var newWidth = this._computeWidth();
      |      if (newWidth > 0) {
      |        this.width = newWidth < this.maxWidth ? newWidth : this.maxWidth;
      |      }
      |      this.fire('vr-resize', {});
      |    }
      |  },
      |
      |  _computeWidth: function () {
      |    var dynamicTexture = new THREEx.DynamicTexture(this.width * 10, this.height * 10);
      |    dynamicTexture.texture.minFilter = THREE.NearestFilter;
      |    var maxWidth = 0;
      |
      |    //{generateCalcMax(geometrics)}
      |    function calcMax(text) {
      |      var texts = text.split(THREEx.linebreak);
      |      texts.forEach(function (text) {
      |        var newWidth = dynamicTexture.computeWidth(text, "64px Verdana");
      |        if (newWidth > maxWidth) {
      |          maxWidth = newWidth;
      |        }
      |      });
      |    }
      |    return maxWidth / 10;
      |  }
      |
      |});
      |</script>
      |""".stripMargin
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
      |""".stripMargin
  }

  private def generateImports(geometrics: List[GeometricModel]): String = {
    (for {g: GeometricModel <- geometrics} yield {
      g match {
        case _: Line => s"""<link rel="import" href="/assets/prototyp/elements/vr-polyline.html">"""
        // caution: Order is important because ellipse extends rectangle
        case e: Ellipse => s"""<link rel="import" href="/assets/prototyp/elements/vr-ellipse.html"> ${generateImports(e.children)}"""
        case r: Rectangle => s"""<link rel="import" href="/assets/prototyp/elements/vr-box.html"> ${generateImports(r.children)}"""
        case p: Polygon => s"""<link rel="import" href="/assets/prototyp/elements/vr-polygon.html"> ${generateImports(p.children)}"""
        case _: PolyLine => s"""<link rel="import" href="/assets/prototyp/elements/vr-polyline.html">"""
        case _: RoundedRectangle => "<!-- VR has no RoundedRectangle -->"
        case _: Text => "<!-- Text -->" // not needed. Just for develop purpose
        case _ => "<!-- no matching value -->" + g.toString
      }
    }).mkString
  }

  private def generateBehaviors(): String = {
    s"""
      | behaviors: [
      |   VrBehavior.Move,
      |   VrBehavior.Resize,
      |   VrBehavior.Highlight,
      |   VrBehavior.Delete,
      |   VrBehavior.Connect,
      |   VrBehavior.Look,
      |   VrBehavior.InnerSizing
      |],
      |""".stripMargin
  }

  private def generateReadyFunction(geometrics: List[GeometricModel]): String = {

    def calcMax(block: CommonLayout => Int): Int = {
      @tailrec
      def rek(carry: Int, gm: List[GeometricModel] = geometrics): Int = {
        gm match {
          case Nil => carry
          case (cl: CommonLayout) :: tail =>
            val cmp = block(cl)
            val nc = if (cmp > carry) cmp else carry
            rek(nc, tail)
          case _ :: tail => rek(carry, tail)
        }
      }

      rek(0, geometrics)
    }

    val totalSize: (Int, Int) = (
      calcMax(g => g.size_height + g.y),
      calcMax(g => g.size_width + g.x)
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
      |   this.height = ${totalSize._1}
      |   this.width = ${totalSize._2}
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
      |""".stripMargin
  }

  private def createInnerSizing(geometrics: List[GeometricModel], totalSize: (Int, Int)): String = {
    createInnerSizing(geometrics, (totalSize._1.toDouble, totalSize._2.toDouble))
  }

  private def createInnerSizing(geometrics: List[GeometricModel], totalSize: (Double, Double)): String = {
    var textCount = 0
    (for {g: GeometricModel <- geometrics} yield {
      val element = getElement(g)
      g match {
        case _: Text => "" // just ignore texts at this point
        case cw: CommonLayout with Wrapper =>
          val position = cw.position.getOrElse((0, 0))
          val texts = cw.children.flatMap {
            case text: Text => List(text)
            case _ => Nil
          }
          s"""
            |  ${
            texts.map(text => (
              s"this.text${textCount} = ${text.textBody};", textCount += 1)).map(_._1).mkString
          }
            |  create(
            |    new VrElement.${element.capitalize}(),
            |    ${
            if (hasText(cw)) {
              "this.text" + (textCount - 1)
            } else {
              "\"\""
            }
          },
            |    true,
            |    {
            |      x: ${position._1 / totalSize._2},
            |      y: -${position._2 / totalSize._1}
            |    },
            |    null,
            |    null,
            |    {
            |      height: ${cw.size_height / totalSize._1},
            |      width: ${cw.size_width / totalSize._2}
            |    }
            |  );
            |  ${createInnerSizing(cw.children, totalSize)}
            |""".stripMargin
        case _ => g.toString
      }
    }).mkString
  }

  private def hasText(wrapper: Wrapper): Boolean = {
    wrapper.children.exists(_.isInstanceOf[Text])
  }

  private def getElement(geometric: GeometricModel): String = {
    geometric match {
      case _: Line => "Line"
      case _: Ellipse => "ellipse"
      case _: Rectangle => "box"
      case _: Polygon => "polygon"
      case _: PolyLine => "polyline"
      case _: RoundedRectangle => "roundedrectangle"
      case _: Text => "text"
      case _ => geometric.toString
    }
  }

  private def generateCalcMax(goemetrics: List[GeometricModel]): String = {
    val numberOfTexts = goemetrics.map(countAllTexts).sum
    (for {i <- 0 until numberOfTexts} yield {
      "calcMax(this.text" + i + ");\n"
    }).mkString
  }

  private def generateTextArgs(geometrics: List[GeometricModel]): String = {
    val numberOfTexts = geometrics.map(countAllTexts).sum
    (for {i <- 0 until numberOfTexts} yield {
      " ,text" + i
    }).mkString
  }

  private def countAllTexts(geometric: GeometricModel): Int = {
    geometric match {
      case wrapper: Wrapper =>
        wrapper.children.foldLeft(0) {
          case (count, _: Text) => count + 1
          case (count, _) => count
        }
      case _ => 0
    }
  }

}
