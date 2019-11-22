import 'yfiles/yfiles.css';

import {bindAction, bindCommand} from "./utils/Bindings";
import {DragAndDrop} from "./DragAndDrop";
import * as umlModel from './UMLClassModel.js'
import {UMLNodeStyle} from './UMLNodeStyle.js'
import UMLContextButtonsInputMode from './UMLContextButtonsInputMode.js'
import definition from '../graphData/definition.js'
import {checkStatus, ZetaApiWrapper} from "./ZetaApiWrapper";
import {showSnackbar} from "./utils/AppStyle";
import {
    Class,
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
    Size, LabelSnapContext, Rect, Edge
} from 'yfiles'
import {Properties} from "./Properties";
import Exporter from "./exportMetaModel/Exporter"
import Attribute from "./exportMetaModel/Attribute";

import '../styles/layout.css'
import '../styles/paper.css'
import '../styles/stencil.css'
import '../styles/style.css'
import '../styles/toolbar.css'


// Tell the library about the license contents
License.value = require('../../../yFiles-for-HTML-Complete-2.2.0.2/lib/license.json');

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);


/**
 * A simple yFiles application that creates a GraphComponent and enables basic input gestures.
 */

//move graph inside class YFilesZeta?
let graphComponent = null;

class YFilesZeta {

    constructor() {
        this.initialize();
    }

    initialize() {

        graphComponent = new GraphComponent('#graphComponent');
        const graph = graphComponent.graph;
        graph.undoEngineEnabled = true
        graphComponent.inputMode = createInputMode()

        // configures default styles for newly created graph elements
        graphComponent.graph.nodeDefaults.style = new UMLNodeStyle(new umlModel.UMLClassModel())
        //clone or share styleInstance
        graphComponent.graph.nodeDefaults.shareStyleInstance = false
        graphComponent.graph.nodeDefaults.size = new Size(125, 100)

        this.buldSampleGraph(graph)

        const exporter = new Exporter(graph);
        console.log(exporter.export())

        // configure and initialize drag and drop panel
        let dragAndDropPanel = new DragAndDrop(graphComponent);
        let propertyPanel = new Properties(graphComponent);

        //Question: Why does this work but the bottom one doesn't? -> graphComponent.selection.addItemSelectionChangedListener(propertyPanel.updateProperties)
        graphComponent.selection.addItemSelectionChangedListener((src, args) => {
            //if (INode.isInstance(args.item) && args.item.style instanceof UMLNodeStyle)
            propertyPanel.updateProperties(src, args)
        })

/*
        const zetaApiWrapper = new ZetaApiWrapper();
        zetaApiWrapper.getConceptDefinition("d882f50c-7e89-48cf-8fea-1e0ea5feb8b7").then(data => {
            buildGraphFromDefinition(graph, data)
            // bootstrap the sample graph
            executeLayout().then(() => {
                // the sample graph bootstrapping should not be undoable
                graphComponent.graph.undoEngine.clear()
            })
        }).catch(reason => {
            alert("Problem to load concept definition: " + reason)
        })
        //graphComponent.fitGraphBounds();
        // bind toolbar commands
 */
        this.registerCommands(graphComponent)
    }

    /**
     * Wires up the UI.
     * @param {GraphComponent} graphComponent
     */
    registerCommands(graphComponent) {
        bindAction("button[data-command='Save']", () => {
            const zetaApiWrapper = new ZetaApiWrapper();
            zetaApiWrapper.postConceptDefinition("d882f50c-7e89-48cf-8fea-1e0ea5feb8b7", JSON.stringify(definition)).then(checkStatus).then(() => {
                showSnackbar("Metamodel saved successfully!")
            }).catch(reason => {
                showSnackbar("Problem to save Metamodel: " + reason)
            })
        })
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
        bindAction("button[data-command='Layout']", executeLayout)
    }

    buldSampleGraph(graph) {
        let node = graph.createNode({
            style: new UMLNodeStyle(
                new umlModel.UMLClassModel({
                    className: 'FirstNode',
                    attributes: ["ATTR", "Another One"
                    ],
                    operations: ['OP1', 'OP2']
                })
            )
        })
        node.style.adjustSize(node, graphComponent.inputMode)

        let node2 = graph.createNode({
            style: new UMLNodeStyle(
                new umlModel.UMLClassModel({
                    className: 'SecondNode',
                    attributes: ['FirstAttribute', 'second'],
                    operations: ['OperationZero', 'OperationSecond']
                })
            )
        })
        node2.style.adjustSize(node, graphComponent.inputMode)
        let edge1 = graph.createEdge(node,node2)
        let edge2 = graph.createEdge(node2,node)
        executeLayout()
    }
}

/**
 * Configure interaction.
 */
function createInputMode() {
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
    mode.moveInputMode.addDragFinishedListener((src, args) => routeEdgesAtSelectedNodes())
    mode.handleInputMode.addDragFinishedListener((src, args) => routeEdgesAtSelectedNodes())

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
}

/**
 * Routes all edges that connect to selected nodes. This is used when a selection of nodes is moved or resized.
 */
function routeEdgesAtSelectedNodes(src, args) {
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
}

/**
 * Routes just the given edge without adjusting the view port. This is used for applying an initial layout to newly
 * created edges.
 * @param affectedEdge
 */
function routeEdge(affectedEdge) {
    const edgeRouter = new EdgeRouter()
    edgeRouter.scope = EdgeRouterScope.ROUTE_AFFECTED_EDGES

    const layoutExecutor = new LayoutExecutor({
        graphComponent,
        layout: edgeRouter,
        layoutData: new PolylineEdgeRouterData({
            affectedEdges: edge => edge === affectedEdge
        }),
        duration: '0.5s',
        animateViewport: false,
        updateContentRect: false
    })
    layoutExecutor.start()
}

/**
 * Sets new HierarchicLayout, target nodes are drawn on top
 * This method gets executed after building the sampleGraph since the nodes got no coordinates
 * @returns {Promise<any>}
 */
function executeLayout() {
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
}

function buildGraphFromDefinition(graph, data) {

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
        if ( node.style instanceof UMLNodeStyle) {
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


}


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

new YFilesZeta();
