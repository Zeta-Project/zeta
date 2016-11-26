package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.node.Node

/**
  * Created by max on 12.11.16.
  */
object VrGeneratorScene {

  // TODO: remove dropRight(4)
  def generate(diagram: Diagram) = {
    s"""
       <script src="/assets/prototyp/bower_components/threejs/build/three.min.js"></script>
       <script src="/assets/prototyp/bower_components/threex.domevents/threex.domevents.js"></script>

       <link rel="import" href="/assets/prototyp/bower_components/polymer/polymer.html">
       <link rel="import" href="/assets/prototyp/behaviors/vr-zoom.html">
       <link rel="import" href="/assets/prototyp/behaviors/vr-axis-control.html">
       <!-- This would be the better solution: <link rel="import" href="vr-new-extended.html"> -->
       <link rel="import" href="/assets/prototyp/behaviors/vr-touch.html">
       <link rel="import" href="/assets/prototyp/behaviors/vr-webvr.html">
       <link rel="import" href="/assets/prototyp/behaviors/vr-scene.html">

       ${diagram.nodes.map(node => "<link rel=\"import\" href=\"./vr-" + node.name.dropRight(4) + ".html\">\n").mkString}

       <dom-module id="vr-scene">
                 <template>
                     <style>
                         button {
                             position: fixed;
                             top: 0;
                             left: 0;
                             padding: 10px;
                             z-index: 1;
                             background: white;
                         }
                     </style>

               <button on-click="_enterVR">Enter VR (WebVR/Mobile only)</button>

               <content id="content" select="*"></content>

               <div id="classItems">
                         <!--<template is="dom-repeat" items="{{classItems}}" strip-whitespace>
                             <vr-class id="{{item.id}}" x-pos="{{item.xPos}}" y-pos="{{item.yPos}}" class-type="{{item.classType}}"></vr-class>
                         </template>-->
                         ${diagram.nodes.map(generateTemplate(_)).mkString}
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
                         VrBehavior.Scene
                     ],

               properties: {
                         classItems: {
                             type: Array,
                             value: function() {
                                 return [];
                             }
                         },
                         ${diagram.nodes.map(generateProperties(_)).mkString.dropRight(1)}
                     },

               ready: function () {
                         var self = this;
                         Polymer.dom(self.root).appendChild(self.renderer.domElement);

                   // observe nodes that will added to this element
                         Polymer.dom(self.$$.content).observeNodes(observe);

                   // TODO: maybe generate
                         Polymer.dom(self.$$.classItems).observeNodes(observe);

                   function observe(nodes) {
                             nodes.addedNodes.forEach(function (node) {
                                 if (node.getThreeJS) {
                                     self.group.add(node.getThreeJS());
                                 }
                             });

                       nodes.removedNodes.forEach(function (node) {
                                 if (node.getThreeJS) {
                                     self.group.remove(node.getThreeJS());
                                 }
                             });
                         }

                   self._render();
                     }

           });
             </script>
    """
  }

  def generateTemplate(node: Node) = {
    s"""
    <template is="dom-repeat" items="{{${node.name.dropRight(4)}Items}}" strip-whitespace>
        <vr-${node.name.dropRight(4)} id="{{item.id}}" x-pos="{{item.xPos}}" y-pos="{{item.yPos}}" class-type="{{item.classType}}"></vr-${node.name.dropRight(4)}>
    </template>
    """
  }

  def generateProperties(node: Node) = {
    s"""
    ${node.name.dropRight(4)}Items: {
        type: Array,
        value: function() {
            return [];
        }
    },"""
  }
}
