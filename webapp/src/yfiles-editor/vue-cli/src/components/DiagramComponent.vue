<template>
  <div>
    <demo-toolbar class="toolbar" @reload-graph="createDefaultGraph()" @toggle-editable="toggleEditable"></demo-toolbar>
    <div class="graph-component-container" ref="GraphComponentElement"></div>
  </div>
</template>

<script>
  import licenseData from '../../../../../../../yFiles-for-html/lib/license.json'
  import {DefaultLabelStyle, Font, GraphComponent, GraphEditorInputMode, GraphViewerInputMode, License, ShapeNodeStyle, Size} from 'yfiles'
  import DemoToolbar from './DemoToolbar'
  import PropertyPanel from "./PropertyPanel";
  import {UMLNodeStyle} from "../../../uml/nodes/styles/UMLNodeStyle";
  import * as umlModel from "../../../uml/models/UMLClassModel";
  import {getInputMode} from "../../../uml/utils/GraphComponentUtils";
  import {UMLEdgeStyle} from "../../../uml/edges/styles/UMLEdgeStyle";
  import * as umlEdgeModel from "../../../uml/edges/UMLEdgeModel";

  License.value = licenseData

  export default {
    name: 'DiagramComponent',
    components: {
      DemoToolbar,
      PropertyPanel
    },
    mounted() {
      this.$graphComponent = new GraphComponent(this.$refs.GraphComponentElement)
      this.$graphComponent.inputMode = new GraphViewerInputMode()
      this.initializeDefaultStyles()
      this.createDefaultGraph()
    },
    methods: {
      /**
       * Sets default styles for the graph.
       */
      initializeDefaultStyles() {
        //this.$graphComponent.inputMode = getInputMode(this.graphComponent)
        this.$graphComponent.graph.nodeDefaults.size = new Size(60, 40)
        this.$graphComponent.graph.nodeDefaults.style = new UMLNodeStyle(new umlModel.UMLClassModel());
        this.$graphComponent.graph.nodeDefaults.shareStyleInstance = false;
        this.$graphComponent.graph.nodeDefaults.size = new Size(125, 100);
        this.$graphComponent.graph.nodeDefaults.labels.style = new DefaultLabelStyle({
          textFill: '#fff',
          font: new Font('Robot, sans-serif', 14)
        })
        this.$graphComponent.graph.edgeDefaults.style = new UMLEdgeStyle(new umlEdgeModel.UMLEdgeModel());
      },

      /**
       * Creates the default graph.
       */
      createDefaultGraph() {
        const graph = this.$graphComponent.graph
        graph.clear()

        const n1 = graph.createNodeAt([150, 150])
        const n2 = graph.createNodeAt([250, 250])
        const n3 = graph.createNodeAt([350, 350])
        graph.createEdge(n1, n2)
        graph.createEdge(n1, n3)
        graph.createEdge(n2, n3)
        this.$graphComponent.fitGraphBounds()
      },

      /**
       * Enables/disables interactive editing of the graph
       */
      toggleEditable(editable) {
        if (editable) {
          this.$graphComponent.inputMode = new GraphEditorInputMode()
        } else {
          this.$graphComponent.inputMode = new GraphViewerInputMode()
        }
      }
    }
  }
</script>

<style scoped>
  @import '~yfiles/yfiles.css';
  .toolbar {
    position: absolute;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    top: 60px;
    left: 0;
    right: 0;
    height: 40px;
    line-height: 40px;
    padding: 0 5px;
    box-sizing: border-box;
    user-select: none;
    background-color: #f7f7f7;
    z-index: 10;
  }
  .graph-component-container {
    position: absolute;
    top: 100px;
    left: 0;
    right: 0;
    bottom: 0;
  }
</style>
