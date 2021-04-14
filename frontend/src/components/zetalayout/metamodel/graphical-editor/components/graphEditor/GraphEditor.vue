<template>
  <div>
    <Toolbar
        v-if="graphComponent"
        :graph-component="graphComponent"
        :isEditEnabled="isEditEnabled"
        :save-graph="saveGraph"
        @reload-graph="plotDefaultGraph()"
        @toggle-editable="toggleEditable"
        @toggle-grid="toggleGrid"
    />
    <aside
        class="demo-description"
        :class="isDndExpanded ? 'expandedDnd demo-sidebar-extended' : 'demo-sidebar-collapsed collapsedDnd'"
        @mouseover="!isDndExpanded && toggleDnd()"
        @mouseleave="isDndExpanded && toggleDnd()"
        v-show="isEditEnabled"
    >
      <DndPanel
          v-if="graphComponent"
          :graph-component="graphComponent"
          :is-expanded="isDndExpanded"
          :passive-supported="true"
          @add-attribute-to-node="(node, attributeName) => addAttributeToNode(node, attributeName)"
          @add-operation-to-node="(node, operationName) => addOperationToNode(node, operationName)"
          @delete-attribute-from-node="(node, attributeName) => deleteAttributeFromNode(node, attributeName)"
          @delete-operation-from-node="(node, operationName) => deleteOperationFromNode(node, operationName)"
          @change-input-mode="() => changeInputMode()"
      />
    </aside>
    <aside
        class="property-panel"
        v-show="selectedItem !== null && isEditEnabled"
    >
      <PropertyPanel
          :item="selectedItem"
          :is-open="selectedItem !== null"
          :node="sharedData.focusedNodeData"
          :edge="sharedData.focusedEdgeData"
          @add-attribute-to-node="(node, attributeName) => addAttributeToNode(node, attributeName)"
          @add-operation-to-node="(node, operationName) => addOperationToNode(node, operationName)"
          @delete-attribute-from-node="(node, attributeName) => deleteAttributeFromNode(node, attributeName)"
          @delete-operation-from-node="(node, operationName) => deleteOperationFromNode(node, operationName)"

          @add-attribute-to-edge="(edge, attributeName) => addAttributeToEdge(edge, attributeName)"
          @add-operation-to-edge="(edge, operationName) => addOperationToEdge(edge, operationName)"
          @delete-attribute-from-edge="(edge, attributeName) => deleteAttributeFromEdge(edge, attributeName)"
          @delete-operation-from-edge="(edge, operationName) => deleteOperationFromEdge(edge, operationName)"
          @on-edge-name-change="(edge, name) => updateEdgeLabel(edge, name)"
          @on-edge-style-change="edge => updateEdgeStyle(edge)"
      />
    </aside>
    <div class="graph-component-container" ref="GraphComponentElement"></div>
  </div>
</template>

<script>
import Vue from 'vue'
import licenseData from '../../../../../../../../../yFiles-dev-key/license.json'
import {
  DefaultLabelStyle,
  EdgeRouter,
  EdgeRouterScope,
  Font,
  GraphComponent,
  GraphViewerInputMode,
  LayoutExecutor,
  License,
  PolylineEdgeRouterData,
  Size, TreeBuilder,
} from 'yfiles'
// Custom components
import Toolbar from '../toolbar/Toolbar.vue'
import PropertyPanel from "../propertyPanel/PropertyPanel.vue";
import DndPanel from "../dndPanel/DndPanel.vue"
import Node from "../nodes/Node.vue"

import {
  executeLayout,
  getDefaultGraphEditorInputMode,
  getEdgesFromReferences,
  getNodesFromClasses, getStyleForEdge, saveGraph
} from "./GraphEditorUtils";
import {UMLEdgeStyle} from "../../uml/edges/styles/UMLEdgeStyle";
import * as umlEdgeModel from "../../uml/edges/UMLEdgeModel";
import {getDefaultGraph} from "../../utils/RESTApi";
import {getDefaultDndInputMode} from "../dndPanel/DndUtils";
import UMLContextButtonsInputMode from "../../uml/utils/UMLContextButtonsInputMode";
import {Grid} from "../../layout/grid/Grid";
import VuejsNodeStyle from "../../uml/nodes/styles/VuejsNodeStyle";
import axios from "axios"
import {EventBus} from "../../../../../../eventbus/eventbus";
import { Attribute } from "../../uml/attributes/Attribute";
import { Method } from "../../uml/methods/Method";


License.value = licenseData;

/**
 * Comment from y-Files: Be aware not to pass y-Files properties (such as graphComponent) to other vue components.
 * This might result in performance issues.
 * Extensive refactoring would take to long for Team WS19/20-SS20.
 */


export default {
  name: 'GraphEditorComponent',
  components: {
    Toolbar,
    PropertyPanel,
    DndPanel
  },
  mounted() {
    this.initGraphComponent().then(response => {
      this.isGraphComponentLoaded = response;
      // Set the current edit mode to view only
      this.$graphComponent.inputMode = new GraphViewerInputMode();
    }).catch(error => {
      // TODO add snackbar error
      console.error(error)
    });
  },
  data: function () {
    return {
      concept: {},
      isGraphComponentLoaded: false,
      isEditEnabled: false,
      isDndExpanded: false,
      grid: null,
      selectedItem: null,
      sharedData: {focusedNodeData: null, focusedEdgeData: null}
    }
  },
  computed: {
    /**
     * Returns the graph component as a computed value. Separation of
     * yFiles $graphComponent and graphComponent as props
     **/
    graphComponent: function () {
      if (this.isGraphComponentLoaded) {
        return this.$graphComponent;
      } else {
        return null;
      }
    }
  },
  methods: {
    /**
     * Initialize and mount the y-Files graph component
     **/
    initGraphComponent() {
      return new Promise((resolve, reject) => {
        this.$graphComponent = new GraphComponent(this.$refs.GraphComponentElement);
        this.$graphComponent.inputMode = this.getInputMode(this.$graphComponent);
        this.initializeDefaultStyles();
        this.initGrid();
        const uuid = this.$route.params.id
        axios.get("http://localhost:9000/rest/v1/meta-models/" + uuid + "/definition", {withCredentials: true})
            .then(
                response => {
                  this.concept = response.data
                  this.plotDefaultGraph(this.concept)
                  this.executeLayout()
                      .then(() => {
                        const isLoaded = true;
                        resolve(isLoaded);
                      })
                      .catch(error => reject(error))
                }
            )
            .catch(error => reject(error))
      })
    },

    initializeDefaultStyles() {
      let methods = {}
      methods.addAttributeToNode = this.addAttributeToNode;
      methods.addOperationToNode = this.addOperationToNode;
      methods.deleteAttributeFromNode = this.deleteAttributeFromNode;
      methods.deleteOperationFromNode = this.deleteOperationFromNode;
      methods.changeInputMode = this.changeInputMode;
      const NodeConstructor = Vue.extend(Node);
      //this.$graphComponent.graph.nodeDefaults.size = new Size(60, 40);
      this.$graphComponent.graph.nodeDefaults.style = new VuejsNodeStyle(NodeConstructor, methods, this.$graphComponent.inputMode);
      this.$graphComponent.graph.nodeDefaults.shareStyleInstance = false;
      this.$graphComponent.graph.nodeDefaults.size = new Size(150, 250);
      // this.$graphComponent.graph.
      // this.$graphComponent.setNodeLayout(node, new Rect(layout.x, layout.y, 500, 50))
      //this.$graphComponent.graph.size = nodeData.size || [50, 50]
      this.$graphComponent.graph.nodeDefaults.labels.style = new DefaultLabelStyle({
        textFill: '#fff',
        font: new Font('Robot, sans-serif', 14)
      });
      this.$graphComponent.graph.edgeDefaults.style = new UMLEdgeStyle(new umlEdgeModel.UMLEdgeModel());
      this.$graphComponent.graph.undoEngineEnabled = true
    },

    /**
     * Initializes the grid. Makes use of the yFiles Grid.
     */
    initGrid() {
      this.grid = new Grid(this.$graphComponent);
      this.grid.initializeGrid();
      this.grid.grid.visible = false;
    },

    /**
     * Creates the default graph.
     */
    plotDefaultGraph(concept) {
      const graphNodes = this.plotNodes(concept);
      this.plotEdges(concept, graphNodes);
      this.$graphComponent.fitGraphBounds();
    },

    /**
     * Plots nodes in the graph
     * @param concept: concept definition
     **/
    plotNodes(concept) {
      // Get the graph from graph component
      const graph = this.$graphComponent.graph;
      // Map data from the concept to uml classes
      let nodes = getNodesFromClasses(graph, concept.classes);
      const NodeConstructor = Vue.extend(Node);
      let methods = {}
      methods.addAttributeToNode = this.addAttributeToNode;
      methods.addOperationToNode = this.addOperationToNode;
      methods.deleteAttributeFromNode = this.deleteAttributeFromNode;
      methods.deleteOperationFromNode = this.deleteOperationFromNode;
      methods.changeInputMode = this.changeInputMode;
      // Create nodes that can be appended to the graph by the builder
      const graphNodes = nodes.map(node => graph.createNode({
        tag: node,
        style: new VuejsNodeStyle(NodeConstructor, methods, this.$graphComponent.inputMode),
      }));

      const treeBuilder = new TreeBuilder({
        graphNodes,
        childBinding: 'subordinates',
        nodesSource: nodes
      });

      // use the VuejsNodeStyle, which uses a Vue component to display nodes
      treeBuilder.graph.nodeDefaults.style = graph.nodeDefaults.style;
      treeBuilder.graph.nodeDefaults.size = graph.nodeDefaults.size;
      treeBuilder.graph.edgeDefaults.style = graph.edgeDefaults.style;
      treeBuilder.buildGraph();
      /*graph.nodes.forEach(node => {
          node.style.adjustSize(node, this.$graphComponent.inputMode)
      })*/
      return graphNodes;
    },

    /**
     * Plots the edges in the graph
     * @param concept: concept definition
     * @param graphNodes: has to be already existing nodes in the graph for it to work
     * */
    plotEdges(concept, graphNodes) {
      // Get the node constructor from the node component
      const graph = this.$graphComponent.graph;
      let edges = getEdgesFromReferences(graph, concept.references, graphNodes);
      edges.forEach(edge => {
        const graphEdge = graph.createEdge({
          source: edge.source,
          target: edge.target,
          style: getStyleForEdge(edge),
          tag: edge
        });
        graph.addLabel(graphEdge, edge.name)
      });
      graph.addLabelTextChangedListener((sender, event) => {
        event.item.owner.tag.name = event.item.text;
      });
    },

    /**
     * It is possible for a single edge to have multiple labels but only one tag. In our use case
     * the name property on the tag object of an edge determines the label name.
     */
    updateEdgeLabel(edge, name) {
      const selectedEdges = this.$graphComponent.selection.selectedEdges;

      selectedEdges.forEach(edge => {
        edge.labels.forEach(label => {
          this.$graphComponent.graph.setLabelText(label, name)
        })
      });
    },

    /**
     * Updates the edge style for the given edge
     */
    updateEdgeStyle(edge) {
      const newStyle = getStyleForEdge(edge.style.model)
      this.$graphComponent.graph.setStyle(edge, newStyle)
    },

    /**
     * Returns the default zeta input mode
     */
    getInputMode(graphComponent) {
      const mode = getDefaultGraphEditorInputMode();
      // Add buttons that appear above a selected node for the creation of a new edge
      const umlContextButtonsInputMode = new UMLContextButtonsInputMode();
      umlContextButtonsInputMode.priority = mode.clickInputMode.priority - 1;
      mode.add(umlContextButtonsInputMode);
      // execute a layout after certain gestures
      mode.moveInputMode.addDragFinishedListener(() => this.routeEdgesAtSelectedNodes());
      mode.handleInputMode.addDragFinishedListener(() => this.routeEdgesAtSelectedNodes());
      mode.addCanvasClickedListener(() => this.handleCanvasClicked(umlContextButtonsInputMode));
      mode.addItemClickedListener((src, args) => {
        this.handleItemClicked(args, args.item.tag, args.item.style)

        if (args.item.style instanceof VuejsNodeStyle) {
          umlContextButtonsInputMode.onCurrentItemChanged()
        } else if (args.item.style instanceof UMLEdgeStyle) {
          umlContextButtonsInputMode.hideButtons()
        }
      });
      // Configure input mode for dndPanel actions
      mode.nodeDropInputMode = getDefaultDndInputMode(graphComponent.graph);

      return mode
    },

    /**
     * Routes all edges that connect to selected nodes.
     * This is used when a selection of nodes is moved or resized.
     */
    routeEdgesAtSelectedNodes() {
      const edgeRouter = new EdgeRouter();
      edgeRouter.minimumNodeToEdgeDistance = 100; //Distance increased
      edgeRouter.scope = EdgeRouterScope.ROUTE_EDGES_AT_AFFECTED_NODES;

      const layoutExecutor = new LayoutExecutor({
        graphComponent: this.$graphComponent,
        layout: edgeRouter,
        layoutData: new PolylineEdgeRouterData({
          affectedNodes: node => this.$graphComponent.selection.selectedNodes.isSelected(node)
        }),
        duration: '0.5s',
        updateContentRect: false
      });
      layoutExecutor.start()
    },

    /**
     * Executes the layout of the graph. Applies positions, directions, ... to elements in graph
     */
    executeLayout() {
      return new Promise((resolve, reject) => {
        executeLayout(this.$graphComponent)
            .then(() => {
              // the graph bootstrapping should not be undoable
              this.$graphComponent.graph.undoEngine.clear();
              resolve();
            })
            .catch(error => reject(error))
      })
    },

    /**
     * Handles the item click action. Used as a callback for a item-clicked-event.
     */
    handleItemClicked(args, tag, type) {
      if (tag || (type instanceof UMLEdgeStyle && type.model)) {
        if (type instanceof VuejsNodeStyle) {
          this.sharedData.focusedNodeData = tag;
          this.sharedData.focusedEdgeData = null;
        } else if (type instanceof UMLEdgeStyle) {
          this.sharedData.focusedEdgeData = tag;
          this.sharedData.focusedNodeData = null;
        }
        this.selectedItem = args;
      }
    },

    /**
     * Handles the canvas clicked event. The canvas is the "empty" part of the graph. Whenever a user
     * clicks the empty graph (neither a node, nor an edge), this function will be called.
     * The focused node as well as the focused edge will be set to null and no item is selected.
     */
    handleCanvasClicked(umlContextButtonsInputMode) {
      this.sharedData.focusedNodeData = null;
      this.sharedData.focusedEdgeData = null;
      this.selectedItem = null;
      umlContextButtonsInputMode.hideButtons()
    },

    /**
     * Enables/disables interactive editing of the graph.
     * If the graph is not editable, various functions will be disabled the graph will be put in
     * a view-mode. The user interactions possible with the graph are defined in the input mode of
     * the $graphComponent. Currently the input mode for an editable graph are predefined in the function
     * "this.getInputMode()".
     */
    toggleEditable(editable) {
      if (editable) {
        this.$graphComponent.inputMode = this.getInputMode(this.$graphComponent)
      } else {
        this.$graphComponent.inputMode = new GraphViewerInputMode()
      }
      this.isEditEnabled = editable
    },

    /**
     * Toggles the grid and snapping to the grid.
     * Needs a parameter on which the toggle state of the grid is based on, since the decision
     * to toggle the grid is made in the toolbar and is not directly controlled by this
     * component. It will be called as a callback from the child-event of the toolbar.
     * @param isEnabled
     */
    toggleGrid(isEnabled) {
      this.$graphComponent.inputMode.labelSnapContext.enabled = isEnabled;
      this.$graphComponent.inputMode.snapContext.enabled = isEnabled;
      this.grid.grid.visible = isEnabled;
      this.$graphComponent.invalidate()
    },

    saveGraph() {
      saveGraph(this.$graphComponent, this.concept, this.$route.params.id)
    },

    /**
     * Lets the drag and drop panel toggle. When called the open-state of the drag-and-drop-panel changes
     * based on its previous state. If it was previously open, the state of the drag-and-drop-panel will
     * be closed and vise-versa.
     *
     * Emits an event to let the parent know what the current state is.
     */
    toggleDnd() {
      this.isDndExpanded = !this.isDndExpanded;
      this.$emit('on-toggle-dnd', this.isDndExpanded);
    },

    /**
     * Adds an attribute to a given node.
     *
     * @param node: node the attribute should be added to.
     * @param name: name of the attribute to add.
     */
    addAttributeToNode(node, name) {
      node.attributes = node.attributes.concat(new Attribute({name: name}));
    },

    /**
     * Deletes an attribute from the given node by its name.
     * Deletes all attributes from the node with the same name.
     * This is currently a feature, not a bug, since multiple attributes with the same
     * name are not allowed. This might change in the future.
     *
     * @param node: node the attribute should be deleted from.
     * @param name: name of the attribute to delete.
     */
    deleteAttributeFromNode(node, name) {
      node.attributes = node.attributes.filter(attribute => attribute.name !== name);
    },

    /**
     * Adds an operation to the given node.
     *
     * @param node: node the operation should be added to.
     * @param name: name of the operation to add.
     */
    addOperationToNode(node, name) {
      node.methods = node.methods.concat(new Method({
        name: name,
        returnType: "String",
        }));
    },

    /**
     * Deletes an operation from the given node by its name.
     * Deletes all operation from the node with the same name.
     * This is currently a feature, not a bug, since multiple operations with the same
     * name are not allowed. This might change in the future.
     *
     * @param node: node the operation should be deleted from.
     * @param name: name of the operation to delete
     */
    deleteOperationFromNode(node, name) {
      node.methods = node.methods.filter(attribute => attribute.name !== name);
    },

    /**
     * Adds an attribute to a given edge.
     *
     * @param edge: edge the attribute should be added to.
     * @param name: name of the attribute to add.
     */
    addAttributeToEdge(edge, name) {
      edge.attributes = edge.attributes.concat(new Attribute({name: name}));
    },

    /**
     * Deletes an attribute from the given edge by its name.
     * Deletes all attributes from the edge with the same name.
     * This is currently a feature, not a bug, since multiple attributes with the same
     * name are not allowed. This might change in the future.
     *
     * @param edge: edge the attribute should be deleted from.
     * @param name: name of the attribute to delete.
     */
    deleteAttributeFromEdge(edge, name) {
      edge.attributes = edge.attributes.filter(attribute => attribute.name !== name);
    },

    /**
     * Adds an method to the given edge.
     *
     * @param edge: edge the method should be added to.
     * @param name: name of the method to add.
     */
    addOperationToEdge(edge, name) {
      edge.methods = edge.methods.concat(new Method({
        name: name,
        returnType: "String"
        })
      );
    },

    /**
     * Deletes an method from the given edge by its name.
     * Deletes all method from the node with the same name.
     * This is currently a feature, not a bug, since multiple methods with the same
     * name are not allowed. This might change in the future.
     *
     * @param edge: edge the method should be deleted from.
     * @param name: name of the method to delete
     */
    deleteOperationFromEdge(edge, name) {
      edge.methods = edge.methods.filter(attribute => attribute.name !== name);
    },

    changeInputMode(newInputMode) {
      this.$graphComponent.inputMode = newInputMode;
    }
  }
}
</script>

<style scoped>
@import '/node_modules/yfiles/yfiles.css';

.toolbar {
  position: absolute;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  top: 115px;
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
  top: 116px;
  left: 0;
  right: 0;
  bottom: 0;
}

.demo-sidebar-collapsed {
  position: absolute;
  top: 120px;
  bottom: 20px;
  width: 100px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 15;
  line-height: 150%;
  left: 0;
  overflow-y: auto;
}

.demo-sidebar-extended {
  position: absolute;
  top: 120px;
  bottom: 20px;
  width: 320px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 15;
  line-height: 150%;
  left: 0;
  overflow-y: auto;
}

.property-panel {
  position: absolute;
  top: 120px;
  bottom: 0;
  width: 320px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 15;
  line-height: 150%;
  right: 0;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
