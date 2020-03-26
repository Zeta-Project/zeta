<template>
    <div>
        <demo-toolbar
                class="toolbar"
                :style="isDndExpanded ? {left: '320px'} : {left: '160px'}"
                @reload-graph="createDefaultGraph()"
                @toggle-editable="toggleEditable"
        />
        <aside
                class="demo-sidebar demo-description"
                :class="isDndExpanded ? 'expandedDnd' : 'collapsedDnd'"
                @mouseover="!isDndExpanded && toggleDnd()"
                @mouseleave="isDndExpanded && toggleDnd()"
        >
            <DndPanel
                    v-if="graphComponent"
                    :graph-component="graphComponent"
                    :is-expanded="isDndExpanded"
                    :on-drag-release="onDragRelease"
            />
        </aside>
        <div class="graph-component-container" ref="GraphComponentElement"></div>
    </div>
</template>

<script>
    import licenseData from '../../../../../../../yFiles-for-html/lib/license.json'
    import {DefaultLabelStyle, Font, GraphComponent, GraphEditorInputMode, GraphViewerInputMode, License, Size} from 'yfiles'
    // Custom components
    import DemoToolbar from './DemoToolbar'
    import PropertyPanel from "./PropertyPanel";
    import DndPanel from "./DndPanel"

    import {UMLNodeStyle} from "../../../uml/nodes/styles/UMLNodeStyle";
    import * as umlModel from "../../../uml/models/UMLClassModel";
    import {addEdgeStyleToEdges, addNodeStyleToNodes, buildGraphFromDefinition, executeLayout, getEdgesFromReferences, getGraphFromDefinition, getInputMode, getNodesFromClasses} from "../../../uml/utils/GraphComponentUtils";
    import {UMLEdgeStyle} from "../../../uml/edges/styles/UMLEdgeStyle";
    import * as umlEdgeModel from "../../../uml/edges/UMLEdgeModel";
    import {getDefaultGraph} from "../utils/RESTApi";

    License.value = licenseData

    export default {
        name: 'DiagramComponent',
        components: {
            DemoToolbar,
            PropertyPanel,
            DndPanel
        },
        mounted() {
            this.initGraphComponent().then(response => {
                this.isGraphComponentLoaded = response
            }).catch(error => {
                // TODO add snackbar error
                console.error(error)
            });
        },
        data: function () {
            return {
                isGraphComponentLoaded: false,
                isDndExpanded: false
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
                return new Promise((resolve) => {
                    this.$graphComponent = new GraphComponent(this.$refs.GraphComponentElement);
                    this.$graphComponent.inputMode = getInputMode(this.$graphComponent);
                    this.initializeDefaultStyles();

                    // Load graph from definition
                    // TODO replace with actual api call in future
                    getDefaultGraph().then(response => {
                        this.plotDefaultGraph(response.concept);
                        this.executeLayout()
                            .then(() => {
                                const isLoaded = true;
                                resolve(isLoaded);
                            })
                            .catch(error => console.error(error))
                    });
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
             * Enables/disables interactive editing of the graph
             */
            toggleEditable(editable) {
                if (editable) {
                    this.$graphComponent.inputMode = new GraphEditorInputMode()
                } else {
                    this.$graphComponent.inputMode = new GraphViewerInputMode()
                }
            },

            onDragRelease() {
                console.log("on drag release")
            },

            toggleDnd() {
                this.isDndExpanded = !this.isDndExpanded
                this.$emit('on-toggle-dnd', this.isDndExpanded)
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

    .demo-sidebar {
        position: absolute;
        top: 0;
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
        width: 160px
    }

    .expandedDnd {
        width: 320px;
    }
</style>
