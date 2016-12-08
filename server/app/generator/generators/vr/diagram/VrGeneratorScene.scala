package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.edge.{Edge}
import generator.model.diagram.node.Node
import generator.model.shapecontainer.connection.{Connection}

/**
  * Created by max on 12.11.16.
  */
object VrGeneratorScene {

  def generate(nodes: Iterable[Node], connections: Iterable[Connection]) = {
    s"""
      <script src="/assets/prototyp/bower_components/threejs/build/three.min.js"></script>
      <script src="/assets/prototyp/bower_components/threex.domevents/threex.domevents.js"></script>

      <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-zoom.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-axis-control.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-touch.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-webvr.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-scene.html">
      <link rel="import" href="/assets/prototyp/behaviors/vr-load-elements.html">
      <link rel="import" href="vr-new-extended.html">
      <link rel="import" href="vr-connect-extended.html">

      <!-- Import all generated elements -->
      ${nodes.map(node => "<link rel=\"import\" href=\"./vr-" + node.shape.get.getShape + ".html\">\n").mkString}

      <!-- Import all generated connections -->
      ${connections.map(connection => "<link rel=\"import\" href=\"./vr-connection-" + connection.name + ".html\">\n").mkString}

      <dom-module id="vr-scene">
        <template>
          <button class="top-left" on-click="_enterVR">Enter VR (WebVR/Mobile only)</button>
          <button class="bottom-right" on-click="_resetPose">Reset Pose</button>

          <!-- uneccessary? -->
          <content id="content" select="*"></content>

          <div id="itemsContent">
            <!-- elements -->
            ${nodes.map(generateTemplate(_)).mkString}
            <!-- connections -->
            ${connections.map(generateTemplate(_)).mkString}
          </div>
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
            VrBehavior.Webvr,
            VrBehavior.Scene,
            VrBehavior.LoadElements
          ],

          properties: {
            // Elements
            ${nodes.map(generateProperties(_)).mkString}
            // Connections
            ${connections.map(generateProperties(_)).mkString}
          },

          ready: function () {
            var self = this;
            Polymer.dom(self.root).appendChild(self.renderer.domElement);

            // uneccessary?
            Polymer.dom(self.$$.content).observeNodes(observe);

            Polymer.dom(self.$$.itemsContent).observeNodes(observe);

            function observe(nodes) {
              nodes.addedNodes.forEach(function (node) {
                if (node.getThreeJS) { self.group.add(node.getThreeJS()); }
              });

              nodes.removedNodes.forEach(function (node) {
                if (node.getThreeJS) { self.group.remove(node.getThreeJS()); }
              });
            }

            self._render();
          }

        });
      </script>
    """
  }

  def generateTemplate(node: Node) = {
    val name = node.shape.get.getShape
    s"""
    <template is="dom-repeat" items="{{${name}Items}}" strip-whitespace>
      <vr-${name} id="{{item.id}}" x-pos="{{item.xPos}}" y-pos="{{item.yPos}}" class-type="{{item.classType}}"></vr-${name}>
    </template>
    """
  }

  def generateProperties(node: Node) = {
    s"""
    ${node.shape.get.getShape}Items: {
      type: Array,
      value: function() { return []; }
    },"""
  }

  def generateTemplate(connection: Connection) = {
    val name = connection.name
    s"""
    <template id="dom-repeat" items="{{${name}Items}}" strip-whitespace>
      <vr-connection-${name} from="#{{item.from}}" to="#{{item.to}}"></vr-connection-${name}>
    </template>
    """
  }

  def generateProperties(connection: Connection) = {
    s"""
    connection${connection.name.capitalize}Items: {
      type: Array,
      value: function() { return []; }
    },"""
  }
}
