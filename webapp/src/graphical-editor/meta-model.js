import $ from 'jquery';
import Backbone from 'backbone1.0';

import Exporter from './meta-model/ext/exportMetaModel/Exporter';
import {Class,
    EdgeRouter,
    EdgeRouterScope, Fill, GridSnapTypes,
    GraphComponent,
    GraphEditorInputMode,
    HierarchicLayout,
    HierarchicLayoutData,
    ICommand,
    INode,
    GraphSnapContext,
    LayoutExecutor,
    License,
    List,
    OrthogonalEdgeEditingContext,
    PolylineEdgeRouterData,
    Size, LabelSnapContext} from "yfiles";
import {bindAction, bindCommand} from "./meta-model/utils/Bindings";
import {showSnackbar} from "./meta-model/utils/AppStyle";
import {DragAndDrop} from "./meta-model/DragAndDrop";
import {ZetaApiWrapper} from "./meta-model/ZetaApiWrapper";
import {UMLNodeStyle} from "./meta-model/UMLNodeStyle";
import * as umlModel from "./meta-model/UMLClassModel";
import UMLContextButtonsInputMode from "./meta-model/UMLContextButtonsInputMode";

License.value = require('../../../../yFiles-for-HTML-Complete-2.2.0.2/lib/license.json');

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);

export default Backbone.Router.extend({

    routes: {
        '*path': 'home'
    },

    initialize: function (options) {

        this.options = options || {};
    },

    home: function () {

        this.initializeEditor();
    },

    initializeEditor: function () {

        let graphComponent = new GraphComponent('#graphComponent');
        const graph = graphComponent.graph;
        graph.undoEngineEnabled = true
        graphComponent.inputMode = this.createInputMode(graphComponent)

        // configures default styles for newly created graph elements
        graphComponent.graph.nodeDefaults.style = new UMLNodeStyle(new umlModel.UMLClassModel())
        //clone or share styleInstance
        graphComponent.graph.nodeDefaults.shareStyleInstance = false
        graphComponent.graph.nodeDefaults.size = new Size(125, 100)

        // configure and initialize drag and drop panel
        let dragAndDropPanel = new DragAndDrop(graphComponent);

        const zetaApiWrapper = new ZetaApiWrapper();
        zetaApiWrapper.getConceptDefinition("d882f50c-7e89-48cf-8fea-1e0ea5feb8b7").then(data => {
            this.buildGraphFromDefinition(graphComponent, data)

            // bootstrap the sample graph
            this.executeLayout(graphComponent).then(() => {
                // the sample graph bootstrapping should not be undoable
                graphComponent.graph.undoEngine.clear()
            })
        }).catch(reason => {
            alert("Problem to load concept definition: " + reason)
        });
        //graphComponent.fitGraphBounds();

        // bind toolbar commands
        this.registerCommands(graphComponent)
    },

    createInputMode(graphComponent) {
        console.log("createMode")
        const mode = new GraphEditorInputMode({
            orthogonalEdgeEditingContext: new OrthogonalEdgeEditingContext(),
            allowAddLabel: false,
            allowGroupingOperations: false,
            allowCreateNode: false,
            labelSnapContext: new LabelSnapContext({
                enabled: false
            }),
            snapContext: new GraphSnapContext({
                nodeToNodeDistance: 30,
                nodeToEdgeDistance: 20,
                snapOrthogonalMovement: false,
                snapDistance: 10,
                snapSegmentsToSnapLines: true,
                snapBendsToSnapLines: true,
                gridSnapType: GridSnapTypes.ALL,
                enabled: false
            })
        })

        // add input mode that handles the edge creations buttons
        const umlContextButtonsInputMode = new UMLContextButtonsInputMode()
        umlContextButtonsInputMode.priority = mode.clickInputMode.priority - 1
        mode.add(umlContextButtonsInputMode)

        // execute a layout after certain gestures
        mode.moveInputMode.addDragFinishedListener((src, args) => this.routeEdgesAtSelectedNodes(graphComponent))
        mode.handleInputMode.addDragFinishedListener((src, args) => this.routeEdgesAtSelectedNodes(graphComponent))

        // hide the edge creation buttons when the empty canvas was clicked
        mode.addCanvasClickedListener((src, args) => {
            graphComponent.currentItem = null
        })

        // the UMLNodeStyle should handle clicks itself
        mode.addItemClickedListener((src, args) => {
            if (INode.isInstance(args.item) && args.item.style instanceof UMLNodeStyle) {
                args.item.style.nodeClicked(src, args)
            }
        })

        return mode
    },

    /**
     * Routes all edges that connect to selected nodes. This is used when a selection of nodes is moved or resized.
     */
    routeEdgesAtSelectedNodes(graphComponent) {
    const edgeRouter = new EdgeRouter()
    edgeRouter.scope = EdgeRouterScope.ROUTE_EDGES_AT_AFFECTED_NODES

    const layoutExecutor = new LayoutExecutor({
        graphComponent,
        layout: edgeRouter,
        layoutData: new PolylineEdgeRouterData({
            affectedNodes: node => graphComponent.selection.selectedNodes.isSelected(node)
        }),
        duration: '0.5s',
        updateContentRect: false
    })
    layoutExecutor.start()
},

    /**
     * Sets new HierarchicLayout, target nodes are drawn on top
     * This method gets executed after building the sampleGraph since the nodes got no coordinates
     * @returns {Promise<any>}
     */
    executeLayout(graphComponent) {
    // configures the hierarchic layout
    const layout = new HierarchicLayout({
        orthogonalRouting: true
    })
    layout.edgeLayoutDescriptor.minimumFirstSegmentLength = 100
    layout.edgeLayoutDescriptor.minimumLastSegmentLength = 100
    layout.edgeLayoutDescriptor.minimumDistance = 100

    const layoutData = new HierarchicLayoutData({
        // mark all inheritance edges (generalization, realization) as directed so their target nodes
        // will be placed above their source nodes
        // all other edges are treated as undirected
        edgeDirectedness: edge => (0),
        // combine all inheritance edges (generalization, realization) in edge groups according to
        // their line type
        // do not group the other edges
        sourceGroupIds: edge => getGroupId(edge, 'src'),
        targetGroupIds: edge => getGroupId(edge, 'tgt')
    })

    return graphComponent.morphLayout(layout, '500ms', layoutData)
},

buildGraphFromDefinition(graphComponent, data) {

        const graph = graphComponent.graph
    const classes = data.classes
    const references = data.references

    const nodeList = new List()

    //create a node for each class
    //fill them with existing attributes, operations and names
    classes.forEach(function (node) {
        const attributeNames = []
        for (let i = 0; i < node.attributes.length; i++) {
            attributeNames[i] = node.attributes[i].name
        }
        const methodNames = []
        for (let i = 0; i < node.methods.length; i++) {
            methodNames[i] = node.methods[i].name
        }
        const tempNode = (graph.createNode({
            style: new UMLNodeStyle(
                new umlModel.UMLClassModel({
                    className: node.name.toString(),
                    attributes: attributeNames,
                    operations: methodNames
                })
            )
        }));
        if (node.abstractness === true) {
            tempNode.style.model.constraint = 'abstract'
            tempNode.style.model.stereotype = ''
            tempNode.style.fill = Fill.CRIMSON
        }
        nodeList.add(tempNode)
        console.log(nodeList.size)
    });


    graph.nodes.forEach(node => {
        if (node.style instanceof UMLNodeStyle) {
            node.style.adjustSize(node, graphComponent.inputMode)
        }
    })

    //connect each class
    let source = null;
    let target = null;
    //const nodes = graph.getNodeArray() --> not a function???
    references.forEach(function (reference) {
        nodeList.forEach(function (node) {
            if (node.style.model.className === reference.sourceClassName) {
                source = node
            }
            if (node.style.model.className === reference.targetClassName) {
                target = node
            }
        })
        if (source != null && target != null) {
            const edge = graph.createEdge(source, target);
            // add a label to the node
            if (reference.name !== '') {
                graph.addLabel(edge, reference.name)
            }

        }
        source = null
        target = null

    })
},

    registerCommands(graphComponent) {
        bindAction("button[data-command='Save']", () => {
            const zetaApiWrapper = new ZetaApiWrapper();
            zetaApiWrapper.postConceptDefinition("d882f50c-7e89-48cf-8fea-1e0ea5feb8b7", JSON.stringify(definition)).then(checkStatus).then(() => {
                showSnackbar("Metamodel saved successfully!")
            }).catch(reason => {
                showSnackbar("Problem to save Metamodel: " + reason)
            })
        });
        bindCommand("button[data-command='Cut']", ICommand.CUT, graphComponent)
        bindCommand("button[data-command='Copy']", ICommand.COPY, graphComponent)
        bindCommand("button[data-command='Paste']", ICommand.PASTE, graphComponent)
        bindCommand("button[data-command='FitContent']", ICommand.FIT_GRAPH_BOUNDS, graphComponent)
        bindCommand("button[data-command='ZoomOriginal']", ICommand.ZOOM, graphComponent, 1.0)
        bindCommand("button[data-command='Undo']", ICommand.UNDO, graphComponent)
        bindCommand("button[data-command='Redo']", ICommand.REDO, graphComponent)

        bindAction('#snapping-button', () => {
            const snappingEnabled = document.querySelector('#snapping-button').checked
            graphComponent.inputMode.snapContext.enabled = snappingEnabled
            graphComponent.inputMode.labelSnapContext.enabled = snappingEnabled
        })
        bindAction("button[data-command='Layout']", this.executeLayout)
    },

    /**
     * Saves the meta model and graph on the server.
     * @param metaModel
     * @param graph
     */
    saveMetaModel: function (metaModel, graph) {
        const showFailure = this.showExportFailure;
        const showSuccess = this.showExportSuccess;

        const data = JSON.stringify({
            name: window.loadedMetaModel.name,
            classes: metaModel.getClasses(),
            references: metaModel.getReferences(),
            enums: metaModel.getEnums(),
            attributes: metaModel.getAttributes(),
            methods: metaModel.getMethods(),
            uiState: JSON.stringify(graph)
        });


        $.ajax({
            type: 'PUT',
            url: '/rest/v1/meta-models/' + window.loadedMetaModel.uuid + '/definition',
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: data,
            success: function (data, textStatus, jqXHR) {
                showSuccess();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                showFailure("Error saving meta model: " + errorThrown);
            }
        });
    },

    externalSaveCall: function (mainElement) {
        const exporter = new Exporter(mainElement.graph);
        const metaModel = exporter.export();

        if (metaModel.isValid()) {
            // Send exported Metamodel to server
            mainElement.saveMetaModel(metaModel, mainElement.graph.toJSON());
        } else {
            let errorMessage = "";
            metaModel.getMessages().forEach(function (message) {
                errorMessage += message + '\n';
            });
            mainElement.showExportFailure(errorMessage);
        }
    },

    showExportSuccess: function () {
        $("#success-panel").fadeOut('slow', function () {
            $("#error-panel").fadeOut('slow', function () {
                $("#success-panel").show();
                $("#success-panel").find("div").text("Success, metamodel saved!");
                $("#success-panel").fadeIn('slow');
            });
        });
    },

    showExportFailure: function (reason) {
        $("#success-panel").fadeOut('slow', function () {
            $("#error-panel").fadeOut('slow', function () {
                $("#error-panel").show();
                $("#error-panel").find("div").text(reason);
                $("#error-panel").fadeIn('slow');
            });
        });
    }
})
;

/**
 * Returns an edge group id according to the edge style.
 * @param {IEdge} edge
 * @param {string} marker
 * @return {object|null}
 */
function getGroupId(edge, marker) {
    /*
    if (edge.style instanceof PolylineEdgeStyle) {
        const edgeStyle = edge.style
        return isInheritance(edgeStyle) ? edgeStyle.stroke.dashStyle + marker : null
    }

     */
    return null
}
