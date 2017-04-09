package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.edge.{ Edge }
import generator.model.diagram.node.Node
import generator.model.shapecontainer.connection.{ Connection }

/**
 * Created by max on 12.11.16.
 */
object VrGeneratorScene {

  def generate(nodes: Iterable[Node], connections: Iterable[Connection]) = {
    s"""
      <link rel="stylesheet" href="/assets/prototyp/style/stylesheet.css">

      <script src="/assets/prototyp/bower_components/threejs/build/three.min.js"></script>
      <script src="/assets/prototyp/bower_components/threex.domevents/threex.domevents.js"></script>

      <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-zoom.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-axis-control.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-touch.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-webvr.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-scene.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-load-elements.html">
      <link rel="import" href="vr-save.html">
      <link rel="import" href="vr-new-extended.html">
      <link rel="import" href="vr-connect-extended.html">

      <!-- Import all generated elements -->
      ${nodes.map(node => "<link rel=\"import\" href=\"./vr-" + node.shape.get.getShape + ".html\">\n").mkString}

      <!-- Import all generated connections -->
      ${connections.map(connection => "<link rel=\"import\" href=\"./vr-connection-" + connection.name + ".html\">\n").mkString}

      <dom-module id="vr-scene">
        <template>
          <button class="top-left" on-click="_enterVR">Enter VR (WebVR/Mobile only)</button>
          <button class="top-left save" on-click="_save">Save</button>
          <button class="top-left save editor" on-click="_switchToEditor">2D</button>
          <button class="bottom-right" on-click="_resetPose">Reset Pose</button>
        </template>
      </dom-module>

      <script>
        window.VrElement = window.VrElement || {};
        VrElement.Scene = Polymer({
          is: "vr-scene",

          behaviors: [
            VrBehavior.Touch,
            VrBehavior.Zoom,
            VrBehavior.AxisControl,
            VrBehavior.NewExtended, // gets generated
            VrBehavior.ConnectExtended, // gets generated
            VrBehavior.Webvr,
            VrBehavior.Scene,
            VrBehavior.LoadElements,
            VrBehavior.Save
          ],

          _switchToEditor: function () {
            var location = window.location.href;
            location = location.replace("vreditor", "editor");
            window.location.href = location;
          }

        });
      </script>
    """
  }
}
