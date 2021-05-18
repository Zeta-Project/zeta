<template>
  <div>
    <Toolbar
        class="toolbar"
        v-if="graphComponent"
        :graph-component="graphComponent"
        :isEditEnabled="isEditEnabled"
        :save-graph="saveGraph"
        @toggle-editable="toggleEditable"
        @toggle-grid="toggleGrid"
    />
    <aside
        class="demo-description dnd-panel"
        :class="isDndExpanded ? 'expandedDnd demo-sidebar-extended' : 'demo-sidebar-collapsed collapsedDnd'"
        @mouseover="!isDndExpanded && toggleDnd()"
        @mouseleave="isDndExpanded && toggleDnd()"
        v-show="isEditEnabled"
        v-if="diagram !== null && shape !== null && concept !== null"
    >
      <div v-if="this.concept.references !== null">
        <DndPanel
            v-if="graphComponent"
            :graph-component="graphComponent"
            :shape="shape"
            :diagram="diagram"
            :concept="concept"
            :references="this.concept.references"
            :styleModel="styleModel"
            :is-expanded="isDndExpanded"
            :passive-supported="true"
        />
      </div>
    </aside>
    <aside
        class="md-scrollbar property-panel"
        v-show="selectedItem !== null && isEditEnabled"
    >
      <PropertyPanel
          :item="selectedItem"
          :is-open="selectedItem !== null"
          :node="sharedData.focusedNodeData"
          :edge="sharedData.focusedEdgeData"
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
  Size,
  TreeBuilder,
  ShowFocusPolicy,
  ShapeNodeStyle,
  GraphItemTypes,
  NodeSizeConstraintProvider, ReshapeHandleProviderBase, HandlePositions
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
  getStyleForEdge, saveGraph
} from "./GraphEditorUtils";
import * as umlEdgeModel from "../../model/edges/ModelEdgeModel";
import {getDefaultDndInputMode} from "../dndPanel/DndUtils";
import ModelContextButtonsInputMode from "../../model/utils/ModelContextButtonsInputMode";
import {Grid} from "../../layout/grid/Grid";
import axios from "axios";
import {CustomPolyEdgeStyle} from "../../model/edges/styles/CustomPolyEdgeStyle";
import {EventBus} from "@/eventbus/eventbus";
import NodeCandidateProvider from "@/components/zetalayout/model/model-editor/model/utils/NodeCandidateProvider";

License.value = licenseData;

class CustomReshapeHandlerProvider extends ReshapeHandleProviderBase {
  constructor(originalProvider, horizontal, vertical, proportional) {
    super()
    this.originalProvider = originalProvider
    this.horizontal = horizontal
    this.vertical = vertical
    this.proportional = proportional
  }

  getAvailableHandles(context) {
    if (!this.horizontal && !this.vertical) {
      return HandlePositions.NONE
    } else if (this.horizontal && !this.vertical) {
      return HandlePositions.HORIZONTAL
    } else if (!this.horizontal && this.vertical) {
      return HandlePositions.VERTICAL
    } else if (this.horizontal && this.vertical) {
      if (this.proportional) {
        return HandlePositions.CORNERS
      } else {
        return HandlePositions.BORDER
      }
    } else {
      return HandlePositions.NONE
    }
  }

  getHandle(context, position) {
    return this.originalProvider.getHandle(context, position)
  }
}

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
      EventBus.$emit('errorMessage', error.toString())
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
      sharedData: {focusedNodeData: null, focusedEdgeData: null},
      diagram: null,
      shape: null,
      styleModel: null
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
            axios.get("http://localhost:9000/rest/v1/models/" + uuid + "/definition", {withCredentials: true})
                .then(
                    response => {
                      if (this.concept.graphicalDslId) {
                        getMetaConcept(this.concept.graphicalDslId).then(metamodel => this.concept.references = metamodel.data.references)
                      }
                      // this.plotDefaultGraph(this.concept)
                      this.executeLayout()
                          .then(() => {
                            const isLoaded = true;
                            resolve(isLoaded);
                          })
                          .catch(error => reject(error))
                    }
                ).catch(error => reject(error))

            axios.get(
                "http://localhost:9000/rest/v1/models/" + uuid, {withCredentials: true}
            ).then(
                response => {
                  this.concept = response.data.concept
                  localStorage.setItem('concept', JSON.stringify(this.concept));

                  const metamodelId = response.data.model.graphicalDslId;

                  axios.get(
                      "http://localhost:9000/rest/v2/meta-models/" + metamodelId + "/shape", {withCredentials: true}
                  ).then(
                      response => {
                        this.shape = response.data
                        localStorage.setItem('shape', JSON.stringify(this.shape));
                      }
                  ).catch(error => reject(error))

                  axios.get(
                      "http://localhost:9000/rest/v2/meta-models/" + metamodelId + "/diagram", {withCredentials: true}
                  ).then(
                      response => {
                        this.diagram = response.data
                      }
                  ).catch(error => reject(error))

                  axios.get(
                      "http://localhost:9000/rest/v2/meta-models/" + metamodelId + "/style", {withCredentials: true}
                  ).then(
                      response => {
                        this.styleModel = response.data
                      }
                  ).catch(error => reject(error))
                }
            ).catch(error => reject(error))
          }
      )
    },

    initializeDefaultStyles() {
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
     * Returns the default zeta input mode
     */
    getInputMode(graphComponent) {
      const mode = getDefaultGraphEditorInputMode();
      // Add buttons that appear above a selected node for the creation of a new edge
      const umlContextButtonsInputMode = new ModelContextButtonsInputMode();
      umlContextButtonsInputMode.priority = mode.clickInputMode.priority - 1;
      mode.add(umlContextButtonsInputMode);

      mode.createEdgeInputMode.startOverCandidateOnly = true;

      mode.createEdgeInputMode.addEdgeCreationStartedListener((sender, args) => {
        let CurrentEdge;

        for (let i = 0; i < this.shape.nodes.length; i++) {
          if (this.shape.nodes[i].name === args.sourcePortOwner.tag.className) {
            if (this.shape.nodes[i].edges.length > 0) {
              for (let j = 0; j < this.shape.nodes[i].edges.length; j++) {
                let currentEdgeName = this.shape.nodes[i].edges[j].conceptElement.split(".").pop()
                if (currentEdgeName === window.currentEdge) {
                  CurrentEdge = this.shape.nodes[i].edges[j];
                }
              }
              if (!window.currentEdge) {
                CurrentEdge = this.shape.nodes[i].edges[0];
              }
            }
          }
        }
        const createEdgeInputMode = sender

        if (CurrentEdge) {
          createEdgeInputMode.dummyEdgeGraph.edgeDefaults.style = new CustomPolyEdgeStyle(null, CurrentEdge)
          createEdgeInputMode.dummyEdge.style = new CustomPolyEdgeStyle(null, CurrentEdge)
        } else {
          mode.createEdgeInputMode.cancel()
        }
      })

      mode.createEdgeInputMode.addMovingListener((sender, args) => {
        if (!window.currentEdge) {
          const sourceNode = this.nodeByPoint(graphComponent, sender.startPoint);
          const targetNode = this.nodeByPoint(graphComponent, sender.dragPoint);

          if (targetNode?.tag?.className) {
            let FirstEdge;
            for (let i = 0; i < this.shape.nodes.length; i++) {
              if (this.shape.nodes[i].name === sourceNode.tag.className) {
                if (this.shape.nodes[i].edges.length > 0) {
                  FirstEdge = this.shape.nodes[i].edges[0];
                }
              }
            }
            let target;
            if (this.shape?.edges) {
              this.shape.edges.forEach(edge => {
                if (edge.conceptElement && FirstEdge.conceptElement === edge.conceptElement && edge.target) {
                  const currentNodes = this.shape.nodes.filter(node => {
                    return node.conceptElement === edge.target
                  })
                  if (currentNodes.length) {
                    target = currentNodes[0].name
                  }
                }
              })
            }
            if (FirstEdge) {
              window.currentEdge = FirstEdge.conceptElement.split(".").pop()
              this.registerPortCandidateProvider(graphComponent.graph, target)
            }
          }
        }
      })

      // execute a layout after certain gestures
      mode.moveInputMode.addDragFinishedListener(() => this.routeEdgesAtSelectedNodes());
      mode.handleInputMode.addDragFinishedListener(() => this.routeEdgesAtSelectedNodes());
      mode.addCanvasClickedListener(() => this.handleCanvasClicked(umlContextButtonsInputMode));
      mode.addItemLeftClickedListener((src, args) => {
        this.handleItemClicked(args, args.item.tag, args.item.style)

        if (args.item.style instanceof ShapeNodeStyle) {
          umlContextButtonsInputMode.onCurrentItemChanged()
        } else if (args.item.style instanceof CustomPolyEdgeStyle) {
          umlContextButtonsInputMode.hideButtons()
        }

        graphComponent.graph.decorator.nodeDecorator.sizeConstraintProviderDecorator.setImplementation(
            node => node.tag !== null,
            new NodeSizeConstraintProvider(
                new Size(args.item.tag.description.size.widthMin, args.item.tag.description.size.heightMin),
                new Size(args.item.tag.description.size.widthMax, args.item.tag.description.size.heightMax)
            )
        )

        graphComponent.graph.decorator.nodeDecorator.reshapeHandleProviderDecorator.setImplementationWrapper(
            (node, originalProvider) => new CustomReshapeHandlerProvider(
                  originalProvider,
                  args.item.tag.description.resizing.horizontal,
                  args.item.tag.description.resizing.vertical,
                  args.item.tag.description.resizing.proportional
              )
        )
      });
      mode.addItemRightClickedListener((src, args) => {
        if (graphComponent.graph.isGroupNode(args.item)) {
          if (graphComponent.graph.getChildren(args.item).size !== 0) {
            EventBus.$emit('infoMessage', "The current item cannot be unset as group node as long as it has children")
          } else {
            graphComponent.graph.setIsGroupNode(args.item, false)
            EventBus.$emit('infoMessage', "Unset current item as group node")
          }
        } else {
          if (graphComponent.graph.getParent(args.item) == null) {
            graphComponent.graph.setIsGroupNode(args.item, true)
            EventBus.$emit('infoMessage', "Set current item to group node")
          }
        }
      });
      // Configure input mode for dndPanel actions
      mode.nodeDropInputMode = getDefaultDndInputMode(graphComponent.graph);

      return mode
    },
    registerPortCandidateProvider(graph, target) {
      graph.decorator.nodeDecorator.portCandidateProviderDecorator.setFactory(node => {
        // Obtain the tag from the edge
        const nodeTag = node.tag

        // Check if it is a known tag and choose the respective implementation
        if (nodeTag) {
          return new NodeCandidateProvider(node, target);
        }
      })
    },
    nodeByPoint(graphComponent, point) {
      const allNodes = graphComponent.graph.nodes;
      return allNodes.find(node => node.layout.contains(point));
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
      if (tag) {
        if (type instanceof ShapeNodeStyle) {
          this.sharedData.focusedNodeData = tag;
          this.sharedData.focusedEdgeData = null;
        } else if (type instanceof CustomPolyEdgeStyle) {
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
    }
  }
}
</script>

<style scoped>
@import '/node_modules/yfiles/yfiles.css';

.toolbar {
  position: fixed;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  top: 56px;
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

@media screen and (min-width: 960px) {
  .toolbar {
    top: 64px;
  }
}

.graph-component-container {
  position: absolute;
  top: 30px;
  left: 0;
  right: 0;
  bottom: 0;
}

.demo-sidebar-collapsed {
  position: absolute;
  top: 101px;
  bottom: 0;
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
  top: 101px;
  bottom: 0;
  width: 320px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 15;
  line-height: 150%;
  left: 0;
  overflow-y: auto;
}

.dnd-panel {
  position: absolute;
  top: -25px;
  bottom: 20px;
  left: -12px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 1;
  line-height: 150%;
  right: 0;
  overflow-y: auto;
}

.property-panel {
  position: absolute;
  top: -25px;
  bottom: -25px;
  right: -12px;
  width: 320px;
  box-sizing: border-box;
  background: #f7f7f7;
  z-index: 1;
  line-height: 150%;
  overflow-y: auto;
}
</style>
