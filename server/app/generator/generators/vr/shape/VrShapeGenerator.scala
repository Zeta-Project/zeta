package generator.generators.vr.shape

/**
  * Created by steffen on 25/10/16.
  */

import java.nio.file._

import generator.generators.shape.GeneratorShapeDefinition
import generator.model.diagram.node.Node
import generator.model.shapecontainer.shape.Shape
import generator.parser.Cache

/**
  * The ShapeGenerator Object
  */
object VrShapeGenerator {


  def doGenerate(cache: Cache, location: String, nodes: List[Node]) {
    val DEFAULT_SHAPE_LOCATION = location
    val attrs = GeneratorShapeDefinition.attrsInspector
    val packageName = "zeta"
    val shapes = cache.shapeHierarchy.nodeView.values.map(s => s.data)

    val FILENAME = "shape.html"


    val jointJSShapeContent = generate(shapes, packageName)
    Files.write(Paths.get(DEFAULT_SHAPE_LOCATION + FILENAME), jointJSShapeContent.getBytes())


  }

  def generate(shapes: Iterable[Shape], packageName: String) =
    s"""
      <!DOCTYPE html>
       <html>
       <head>
           <title>Class Diagram</title>

           <meta charset="UTF-8">
           <meta name="viewport"
                 content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0, shrink-to-fit=no">
           <meta name="mobile-web-app-capable" content="yes">
           <meta name="apple-mobile-web-app-capable" content="yes"/>
           <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent"/>

           <script src="../../bower_components/webcomponentsjs/webcomponents-lite.min.js"></script>
           <script src="../../bower_components/underscore/underscore-min.js"></script>

           <link rel="import" href="../../elements/vr-scene.html">
           <link rel="import" href="../../elements/vr-ellipse.html">
           <link rel="import" href="../../elements/vr-class.html">
           <link rel="import" href="../../elements/vr-connection-aggregation.html">
           <link rel="import" href="../../elements/vr-connection-association.html">
           <link rel="import" href="../../elements/vr-connection-inheritance.html">
           <link rel="import" href="../../elements/vr-controlpanel.html">
           <link rel="import" href="../../elements/vr-control.html">


           <style>
               body {
                   background-color: #f0f0f0;
                   margin: 0;
                   overflow: hidden;
               }
           </style>
       </head>
       <body>
       <vr-scene>

           <vr-control id="ctr" x-pos="-50" y-pos="130" height="50" width="200"></vr-control>

           <vr-ellipse x="-300" y="0" type="0" width="100" height="100"></vr-ellipse>


       </vr-scene>
       </body>
       </html>

     """
}
