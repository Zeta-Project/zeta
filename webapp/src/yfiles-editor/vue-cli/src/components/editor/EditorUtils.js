import 'yfiles/yfiles.css';
import * as umlModel from '../../uml/models/UMLClassModel.js'
import {UMLNodeStyle} from '../../uml/nodes/styles/UMLNodeStyle.js'
import UMLContextButtonsInputMode from '../../uml/utils/UMLContextButtonsInputMode.js'
import {
    Class,
    EdgeRouter,
    EdgeRouterScope,
    Fill,
    GraphEditorInputMode,
    GraphSnapContext,
    GridSnapTypes,
    HierarchicLayout,
    HierarchicLayoutData,
    ICommand,
    IEdge,
    INode,
    LabelSnapContext,
    LayoutExecutor,
    OrthogonalEdgeEditingContext,
    PolylineEdgeRouterData,
    Size
} from 'yfiles'

import '../../../../styles/layout.css'
import '../../../../styles/paper.css'
import '../../../../styles/stencil.css'
import '../../../../styles/style.css'
import '../../../../styles/toolbar.css'
import '../../../../styles/sidebar.css'
import {UMLEdgeStyle} from '../../uml/edges/styles/UMLEdgeStyle'
import * as umlEdgeModel from '../../uml/edges/UMLEdgeModel'
import {bindAction, bindCommand} from "../../../../utils/Bindings";
import Exporter from "../../../../export/Exporter";
import {isSuccessStatus, ZetaApiWrapper} from "../../../../utils/ZetaApiWrapper";
import {showExportFailure, showSnackbar} from "../../../../utils/Snackbar";
import {Attribute} from "../../uml/attributes/Attribute";
import {Parameter} from "../../uml/parameters/Parameter";
import {Operation} from "../../uml/operations/Operation";
import {createAggregationStyle, createAssociationStyle, createCompositionStyle, createGeneralizationStyle} from "../../uml/edges/styles/UMLEdgeStyleFactory";
import {configureDndInputMode} from "../../components/dnd/DndUtils";

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);

export function setDefaultStyles(graphComponent) {
    // configures default styles for newly created graph elements
    graphComponent.graph.nodeDefaults.style = new UMLNodeStyle(new umlModel.UMLClassModel());
    graphComponent.graph.nodeDefaults.shareStyleInstance = false;
    graphComponent.graph.nodeDefaults.size = new Size(125, 100);

    graphComponent.graph.edgeDefaults.style = new UMLEdgeStyle(new umlEdgeModel.UMLEdgeModel());
}

export function getInputMode(graphComponent) {
    const mode = new GraphEditorInputMode({
        orthogonalEdgeEditingContext: new OrthogonalEdgeEditingContext(),
        allowAddLabel: false,
        allowGroupingOperations: false,
        allowCreateNode: false,
        labelSnapContext: new LabelSnapContext({
            enabled: false
        }),
        nodeDropInputMode: configureDndInputMode(graphComponent.graph),
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
    });

    // add input mode that handles the edge creations buttons
    const umlContextButtonsInputMode = new UMLContextButtonsInputMode()
    umlContextButtonsInputMode.priority = mode.clickInputMode.priority - 1
    mode.add(umlContextButtonsInputMode)

    // execute a layout after certain gestures
    mode.moveInputMode.addDragFinishedListener((src, args) => routeEdgesAtSelectedNodes())
    mode.handleInputMode.addDragFinishedListener((src, args) => routeEdgesAtSelectedNodes())

    // hide the edge creation buttons when the empty canvas was clicked
    mode.addCanvasClickedListener((src, args) => {
        graphComponent.currentItem = null;
        // closePropertyPanel();
    });

    // the UMLNodeStyle should handle clicks itself
    mode.addItemClickedListener((src, args) => {
        if (INode.isInstance(args.item) && args.item.style instanceof UMLNodeStyle) {
            args.item.style.nodeClicked(src, args);
            // openPropertyPanel();
        } else if (IEdge.isInstance(args.item) && args.item.style instanceof UMLEdgeStyle) {
            // openPropertyPanel();
        }
    });

    return mode
}


/**
 * Routes all edges that connect to selected nodes. This is used when a selection of nodes is moved or resized.
 */
export function routeEdgesAtSelectedNodes(src, args) {
    const edgeRouter = new EdgeRouter()
    edgeRouter.minimumNodeToEdgeDistance = 100 //Distance increased
    edgeRouter.scope = EdgeRouterScope.ROUTE_EDGES_AT_AFFECTED_NODES

    const layoutExecutor = new LayoutExecutor({
        graphComponent: GraphComponentUtils,
        layout: edgeRouter,
        layoutData: new PolylineEdgeRouterData({
            affectedNodes: node => GraphComponentUtils.selection.selectedNodes.isSelected(node)
        }),
        duration: '0.5s',
        updateContentRect: false
    })
    layoutExecutor.start()
}

export function registerCommands(graphComponent, loadedMetaModel) {
    bindAction("button[data-command='Save']", () => {

        if (loadedMetaModel.constructor === Object && Object.entries(loadedMetaModel).length > 0 && loadedMetaModel.name.length > 0 && loadedMetaModel.uuid.length > 0) {

            const graph = graphComponent.graph;

            const exporter = new Exporter(graph);
            const exportedMetaModel = exporter.export();

            if (exportedMetaModel.isValid()) {

                const data = JSON.stringify({
                    name: loadedMetaModel.name,
                    classes: exportedMetaModel.getClasses(),
                    references: exportedMetaModel.getReferences(),
                    enums: exportedMetaModel.getEnums(),
                    attributes: exportedMetaModel.getAttributes(),
                    methods: exportedMetaModel.getMethods(),
                    uiState: JSON.stringify({"empty": "value"})
                });

                ZetaApiWrapper.prototype.postConceptDefinition(loadedMetaModel.uuid, data).then(isSuccessStatus).then(() => {
                    showSnackbar("Meta model saved successfully!")
                }).catch(reason => {
                    showSnackbar("Problem to save meta model: " + reason)
                });

            } else {
                let errorMessage = "";
                exportedMetaModel.getMessages().forEach(message => {
                    errorMessage += message + '\n';
                });
                showExportFailure(errorMessage);
            }
        } else {
            showSnackbar("No loaded meta model found");
        }
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
    bindAction("button[data-command='Layout']", () => executeLayout(graphComponent))
}


/**
 * Sets new HierarchicLayout, target nodes are drawn on top
 * This method gets executed after building the sampleGraph since the nodes got no coordinates
 * @returns {Promise<any>}
 */
export function executeLayout(graphComponent) {
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

/**
 * Returns an edge group id according to the edge style.
 * @param {IEdge} edge
 * @param {string} marker
 * @return {object|null}
 */
export function getGroupId(edge, marker) {
    /*
    if (edge.style instanceof PolylineEdgeStyle) {
        const edgeStyle = edge.style
        return isInheritance(edgeStyle) ? edgeStyle.stroke.dashStyle + marker : null
    }

     */
    return null
}

/**
 * Returns a list of nodes based on the classes of the definition.
 * The nodes are modelled after the default zeta node model
 * @param graph: graphComponent.graph
 * @param classes: classes of definition
 * @returns {*}
 */
export function getNodesFromClasses(graph, classes) {
    return classes.map(node => {
        const attributes = node.attributes.map(attribute => new Attribute(attribute));
        const methods = node.methods.map(method => {
            const parameters = method.parameters.map(parameter => new Parameter(parameter));
            return new Operation({
                name: method.name,
                parameters: parameters,
                description: method.description,
                returnType: method.returnType,
                code: method.code
            })
        });

        return {
            ...node,
            attributes: attributes,
            methods: methods
        };
    });
}

/**
 * Adds the default zeta node style to every node given
 * @param nodes: list of nodes
 * @returns {*}
 */
export function addNodeStyleToNodes(nodes) {
    return nodes.map(node => {
        let style = new UMLNodeStyle(
            new umlModel.UMLClassModel({
                className: node.name,
                description: node.description,
                abstract: node.abstractness,
                superTypeNames: node.superTypeNames,
                attributes: node.attributes,
                operations: node.methods
            })
        );

        if (node.abstractness === true) {
            style.model.constraint = 'abstract';
            style.model.stereotype = '';
            style.fill = Fill.CRIMSON;
        }

        return {
            ...node,
            style: style
        };
    })
}

/**
 * Returns a list of edges based on the default zeta edge model. Takes a list of references
 * from the definition. Requires a list of all nodes to filter all relevant edges
 * @param graph
 * @param references
 * @param nodes
 * @returns {*}
 */
export function getEdgesFromReferences(graph, references, nodes) {
    // Filter all relevant references
    // Remove all references that don't have a target AND source
    references.filter(reference => {
        return nodes.some(node => (node.style.model.className === reference.sourceClassName) || (node.style.model.className === reference.targetClassName))
    });
    // Transform references to Zeta specific EdgeModels
    return references.map(reference => {
        const source = nodes.find(node => node.style.model.className === reference.sourceClassName);
        const target = nodes.find(node => node.style.model.className === reference.targetClassName);
        const model = new umlEdgeModel.UMLEdgeModel({
            name: reference.name,
            description: reference.description,
            sourceDeletionDeletesTarget: reference.sourceDeletionDeletesTarget,
            targetDeletionDeletesSource: reference.targetDeletionDeletesSource,
            sourceClassName: reference.sourceClassName,
            targetClassName: reference.targetClassName,
            sourceLowerBounds: reference.sourceLowerBounds,
            sourceUpperBounds: reference.sourceUpperBounds,
            targetLowerBounds: reference.targetLowerBounds,
            targetUpperBounds: reference.targetUpperBounds,
            operations: reference.operations,
            attributes: reference.attributes,

        });

        model.source = source;
        model.target = target;
        return model;
    });
}

/**
 *  Adds the default zeta edge style to every edge given
 * @param edges
 * @returns {*}
 */
export function addEdgeStyleToEdges(edges) {
    return edges.map(edge => {
        let edgeStyle;
        if (edge.sourceDeletionDeletesTarget === true && edge.targetDeletionDeletesSource === true) {
            edgeStyle = createCompositionStyle();
        } else if (edge.sourceDeletionDeletesTarget === false && edge.targetDeletionDeletesSource === true) {
            edgeStyle = createGeneralizationStyle();
        } else if (edge.sourceDeletionDeletesTarget === true && edge.targetDeletionDeletesSource === false) {
            edgeStyle = createAggregationStyle();
        } else {
            edgeStyle = createAssociationStyle();
        }

        edgeStyle.model = edge;

        return {
            ...edge,
            style: edgeStyle
        };
    })
}


export function buildGraphFromDefinition(graphComponent, data) {
    // legacy
}