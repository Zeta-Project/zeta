<template>
    <div>
        <Toolbar
                class="toolbar"
                v-if="graphComponent"
                :graph-component="graphComponent"
                :isEditEnabled="isEditEnabled"
                :save-graph="saveGraph"
                @reload-graph="plotDefaultGraph()"
                @toggle-editable="toggleEditable"
                @toggle-grid="toggleGrid"
        />
        <aside
                class="demo-sidebar demo-description"
                :class="isDndExpanded ? 'expandedDnd' : 'collapsedDnd'"
                @mouseover="!isDndExpanded && toggleDnd()"
                @mouseleave="isDndExpanded && toggleDnd()"
                v-show="isEditEnabled"
        >
        </aside>
        <aside
                class="md-scrollbar property-panel"
                v-show="selectedItem !== null && isEditEnabled"
        >
            <PropertyPanel
                    :item="selectedItem"
                    :is-open="selectedItem != null"
                    :tag="sharedData.focusedNodeData"
            />
        </aside>
        <div class="graph-component-container" ref="GraphComponentElement"></div>
    </div>
</template>

<script>
    import Vue from 'vue'
    import licenseData from '../../../../../../../../yFiles-for-html/lib/license.json'
    import {
        DefaultLabelStyle,
        EdgeRouter,
        EdgeRouterScope,
        Font, GraphBuilder,
        GraphComponent,
        GraphEditorInputMode, GraphSnapContext,
        GraphViewerInputMode, GridSnapTypes,
        IEdge,
        INode,
        LabelSnapContext,
        LayoutExecutor,
        License,
        OrthogonalEdgeEditingContext,
        PolylineEdgeRouterData, Rect,
        Size, TreeBuilder,
        ShowFocusPolicy
    } from 'yfiles'
    // Custom components
    import Toolbar from '../toolbar/Toolbar'
    import PropertyPanel from "../propertyPanel/PropertyPanel";
    import DndPanel from "../dndPanel/DndPanel"
    import Node from "../Node"

    import {UMLNodeStyle} from "../../uml/nodes/styles/UMLNodeStyle";
    import * as umlModel from "../../uml/nodes/UMLClassModel";
    import {addEdgeStyleToEdges, addNodeStyleToNodes, executeLayout, getDefaultGraphEditorInputMode, getEdgesFromReferences, getNodesFromClasses} from "./GraphEditorUtils";
    import {UMLEdgeStyle} from "../../uml/edges/styles/UMLEdgeStyle";
    import * as umlEdgeModel from "../../uml/edges/UMLEdgeModel";
    import {getDefaultGraph} from "../../utils/RESTApi";
    import {getDefaultDndInputMode} from "../dndPanel/DndUtils";
    import UMLContextButtonsInputMode from "../../uml/utils/UMLContextButtonsInputMode";
    import {Grid} from "../../../../layout/grid/Grid";
    import VuejsNodeStyle from "../VuejsNodeStyle";

    License.value = licenseData

    export default {
        name: 'GraphEditorComponent',
        components: {
            Toolbar,
            PropertyPanel,
            //DndPanel,
            Node
        },
        mounted() {
            this.initGraphComponent().then(response => {
                this.isGraphComponentLoaded = response
                // Set the current edit mode to view only
                this.$graphComponent.inputMode = new GraphViewerInputMode()
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
                sharedData: {focusedNodeData: null}
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
                    this.initGrid()

                    // Load graph from definition
                    // TODO replace with actual api call in future
                    getDefaultGraph().then(response => {
                        this.concept = response.concept
                        this.plotDefaultGraph(response.concept);
                        this.executeLayout()
                            .then(() => {
                                const isLoaded = true;
                                resolve(isLoaded);
                            })
                            .catch(error => reject(error))
                    }).catch(error => reject(error));
                })
            },

            /**
             * Sets default styles for the graph.
             */
            initializeDefaultStyles() {
                const NodeConstructor = Vue.extend(Node);
                this.$graphComponent.graph.nodeDefaults.size = new Size(60, 40);
                this.$graphComponent.graph.nodeDefaults.style = new VuejsNodeStyle(NodeConstructor)
                this.$graphComponent.graph.nodeDefaults.shareStyleInstance = false;
                this.$graphComponent.graph.nodeDefaults.size = new Size(285, 100)
                this.$graphComponent.graph.nodeDefaults.labels.style = new DefaultLabelStyle({
                    textFill: '#fff',
                    font: new Font('Robot, sans-serif', 14)
                })
                this.$graphComponent.graph.edgeDefaults.style = new UMLEdgeStyle(new umlEdgeModel.UMLEdgeModel());
                this.$graphComponent.graph.undoEngineEnabled = true
            },

            /**
             * Initializes the grid. Makes use of the yFiles Grid.
             */
            initGrid() {
                this.grid = new Grid(this.$graphComponent)
                this.grid.initializeGrid()
                this.grid.grid.visible = false;
            },

            /**
             * Creates the default graph.
             */
            plotDefaultGraph(concept) {
                const graphNodes = this.plotNodes(concept);
                console.log(concept)
                this.plotEdges(concept, graphNodes);
                this.$graphComponent.fitGraphBounds();
            },

            /**
             * Plots nodes in the graph
             * @param concept: concept definition
             **/
            plotNodes(concept) {
                // Get the node constructor from the node component
                const NodeConstructor = Vue.extend(Node);
                // Get the graph from graph component
                const graph = this.$graphComponent.graph;
                // At this point nodes are only models and style
                let nodes = getNodesFromClasses(graph, concept.classes);
                // Create nodes that can be appended to the do by the builder
                const graphNodes = nodes.map(node => graph.createNode({
                    tag: node,
                    style: new VuejsNodeStyle(NodeConstructor)
                }));

                const treeBuilder = new TreeBuilder({
                    graphNodes,
                    childBinding: 'subordinates',
                    nodesSource: nodes
                })

                // use the VuejsNodeStyle, which uses a Vue component to display nodes
                treeBuilder.graph.nodeDefaults.style = graph.nodeDefaults.style
                treeBuilder.graph.nodeDefaults.size = graph.nodeDefaults.size
                treeBuilder.graph.edgeDefaults.style = graph.edgeDefaults.style
                treeBuilder.buildGraph()
                return graphNodes
            },

            /**
             * Plots the edges in the graph
             * @param concept: concept definition
             * @param graphNodes: has to be already existing nodes in the graph for it to work
             * */
            plotEdges(concept, graphNodes) {
                const graph = this.$graphComponent.graph;
                let edges = getEdgesFromReferences(graph, concept.references, graphNodes)
                edges = addEdgeStyleToEdges(edges);
                edges.forEach(edge => {
                    const tempEdge = graph.createEdge({
                        source: edge.source,
                        target: edge.target,
                        style: edge.style
                    });
                    graph.addLabel(tempEdge, edge.name)
                });
            },

            /**
             * Returns the default zeta input mode
             */
            getInputMode(graphComponent) {
                const mode = getDefaultGraphEditorInputMode();
                // Add buttons that appear above a selected node for the creation of a new edge
                const umlContextButtonsInputMode = new UMLContextButtonsInputMode()
                umlContextButtonsInputMode.priority = mode.clickInputMode.priority - 1
                mode.add(umlContextButtonsInputMode)
                // execute a layout after certain gestures
                mode.moveInputMode.addDragFinishedListener((src, args) => this.routeEdgesAtSelectedNodes())
                mode.handleInputMode.addDragFinishedListener((src, args) => this.routeEdgesAtSelectedNodes())
                mode.addCanvasClickedListener((src, args) => this.handleCanvasClicked(src, args));
                //mode.addItemClickedListener((src, args) => this.handleItemClicked(src, args))
                // Configure input mode for dndPanel actions
                mode.nodeDropInputMode = getDefaultDndInputMode(graphComponent.graph);
                this.$graphComponent.focusIndicatorManager.showFocusPolicy = ShowFocusPolicy.ALWAYS
                this.$graphComponent.focusIndicatorManager.addPropertyChangedListener(() => {
                    if (this.$graphComponent.focusIndicatorManager.focusedItem)
                        this.handleItemClicked(this.$graphComponent.focusIndicatorManager.focusedItem.tag)
                })

                return mode
            },

            routeEdgesAtSelectedNodes(src, args) {
                const edgeRouter = new EdgeRouter()
                edgeRouter.minimumNodeToEdgeDistance = 100 //Distance increased
                edgeRouter.scope = EdgeRouterScope.ROUTE_EDGES_AT_AFFECTED_NODES

                const layoutExecutor = new LayoutExecutor({
                    graphComponent: this.$graphComponent,
                    layout: edgeRouter,
                    layoutData: new PolylineEdgeRouterData({
                        affectedNodes: node => this.$graphComponent.selection.selectedNodes.isSelected(node)
                    }),
                    duration: '0.5s',
                    updateContentRect: false
                })
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
            handleItemClicked(tag) {
                this.sharedData.focusedNodeData = tag;
                this.selectedItem = tag;
            },

            handleCanvasClicked(src, args) {
                this.sharedData.focusedNodeData = null;
                this.selectedItem = null;
            },

            /**
             * Enables/disables interactive editing of the graph
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
             * Toggles the grid and snapping to the grid
             * @param isEnabled
             */
            toggleGrid(isEnabled) {
                this.$graphComponent.inputMode.labelSnapContext.enabled = isEnabled
                this.$graphComponent.inputMode.snapContext.enabled = isEnabled
                this.grid.grid.visible = isEnabled
                this.$graphComponent.invalidate()
            },

            saveGraph() {
                console.log("save")
                //saveGraph(this.$graphComponent, this.concept)
            },

            toggleDnd() {
                this.isDndExpanded = !this.isDndExpanded;
                this.$emit('on-toggle-dndPanel', this.isDndExpanded);
            }
        }
    }
</script>

<style scoped>
    @import '../../../../../../node_modules/yfiles/yfiles.css';

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

    .demo-sidebar {
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

    .property-panel {
        position: absolute;
        top: 101px;
        bottom: 0;
        width: 320px;
        box-sizing: border-box;
        background: #f7f7f7;
        z-index: 15;
        line-height: 150%;
        right: 0;
        overflow-y: auto;
    }

    .collapsedDnd {
        width: 100px
    }

    .expandedDnd {
        width: 200px;
    }
</style>
