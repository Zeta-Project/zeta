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
            <DndPanel
                    v-if="graphComponent"
                    :graph-component="graphComponent"
                    :is-expanded="isDndExpanded"
                    :passiveSupported="true"
                    title="Drag and Drop"
            />
        </aside>
        <div class="graph-component-container" ref="GraphComponentElement"></div>
    </div>
</template>

<script>
    import licenseData from '../../../../../../../../yFiles-for-html/lib/license.json'
    import {
        DefaultLabelStyle,
        EdgeRouter,
        EdgeRouterScope,
        Font,
        GraphComponent,
        GraphEditorInputMode, GraphSnapContext,
        GraphViewerInputMode, GridSnapTypes,
        IEdge,
        INode,
        LabelSnapContext,
        LayoutExecutor,
        License,
        OrthogonalEdgeEditingContext,
        PolylineEdgeRouterData,
        Size
    } from 'yfiles'
    // Custom components
    import Toolbar from '../Toolbar'
    import PropertyPanel from "../PropertyPanel";
    import DndPanel from "../dnd/DndPanel"

    import {UMLNodeStyle} from "../../uml/nodes/styles/UMLNodeStyle";
    import * as umlModel from "../../uml/nodes/UMLClassModel";
    import {addEdgeStyleToEdges, addNodeStyleToNodes, executeLayout, getDefaultGraphEditorInputMode, getEdgesFromReferences, getNodesFromClasses} from "./GraphEditorUtils";
    import {UMLEdgeStyle} from "../../uml/edges/styles/UMLEdgeStyle";
    import * as umlEdgeModel from "../../uml/edges/UMLEdgeModel";
    import {getDefaultGraph} from "../../utils/RESTApi";
    import {getDefaultDndInputMode} from "../dnd/DndUtils";
    import UMLContextButtonsInputMode from "../../uml/utils/UMLContextButtonsInputMode";
    import {Grid} from "../../../../layout/grid/Grid";

    License.value = licenseData

    export default {
        name: 'GraphEditorComponent',
        components: {
            Toolbar,
            PropertyPanel,
            DndPanel
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
                this.$graphComponent.graph.nodeDefaults.size = new Size(60, 40);
                this.$graphComponent.graph.nodeDefaults.style = new UMLNodeStyle(new umlModel.UMLClassModel());
                this.$graphComponent.graph.nodeDefaults.shareStyleInstance = false;
                this.$graphComponent.graph.nodeDefaults.size = new Size(125, 100);
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
                this.plotEdges(concept, graphNodes);
                this.$graphComponent.fitGraphBounds();
            },

            /**
             * Plots nodes in the graph
             * @param concept: concept definition
             **/
            plotNodes(concept) {
                const graph = this.$graphComponent.graph;

                // At this point nodes are only models and style
                let nodes = getNodesFromClasses(graph, concept.classes);
                nodes = addNodeStyleToNodes(nodes);
                // Append nodes to actual graph at which point they can be referenced by edges.
                const graphNodes = nodes.map(node => graph.createNode({style: node.style}));
                graph.nodes.forEach(node => {
                    if (node.style instanceof UMLNodeStyle) {
                        node.style.adjustSize(node, this.$graphComponent.inputMode)
                    }
                });
                return graphNodes;
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
                mode.addItemClickedListener((src, args) => this.handleItemClicked(src, args))
                // Configure input mode for dnd actions
                mode.nodeDropInputMode = getDefaultDndInputMode(graphComponent.graph);

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
            handleItemClicked(src, args) {
                if (INode.isInstance(args.item) && args.item.style instanceof UMLNodeStyle) {
                    args.item.style.nodeClicked(src, args);
                    // openPropertyPanel();
                } else if (IEdge.isInstance(args.item) && args.item.style instanceof UMLEdgeStyle) {
                    // openPropertyPanel();
                }
            },

            handleCanvasClicked(src, args) {
                this.$graphComponent.currentItem = null;
                // closePropertyPanel();
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
                this.$emit('on-toggle-dnd', this.isDndExpanded);
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

    .collapsedDnd {
        width: 100px
    }

    .expandedDnd {
        width: 200px;
    }
</style>
