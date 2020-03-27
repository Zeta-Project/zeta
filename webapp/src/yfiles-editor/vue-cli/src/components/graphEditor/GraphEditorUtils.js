import 'yfiles/yfiles.css';
import * as umlModel from '../../uml/nodes/UMLClassModel.js'
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
    PolylineEdgeRouterData, PolylineEdgeStyle,
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
import {createAggregationStyle, createAssociationStyle, createCompositionStyle, createGeneralizationStyle, isInheritance} from "../../uml/edges/styles/UMLEdgeStyleFactory";
import {configureDndInputMode} from "../../components/dnd/DndUtils";

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);

/**
 * Returns a zeta specific default input mode for the graph graphEditor.
 * - SnapContext specifies the snapping behaviour of elements in the graph. Especially
 *   interesting for grid related behaviour. Snapping is disabled (enabled = false) on default
 *
 * @returns {GraphEditorInputMode}
 */
export function getDefaultGraphEditorInputMode() {
    return new GraphEditorInputMode({
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
            snapDistance: 10,
            gridSnapType: GridSnapTypes.ALL,
            enabled: false,
            snapBendAdjacentSegments: true,
            snapNodesToSnapLines: true,
            snapPortAdjacentSegments: true,
            snapSegmentsToSnapLines: true,
            snapBendsToSnapLines: true,
            snapOrthogonalMovement: true,
        })
    });
}

/**
 * Validation TODO
 * @param graphComponent
 * @param loadedMetaModel
 */
export function saveGraph(graphComponent, loadedMetaModel) {
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
    if (edge.style instanceof PolylineEdgeStyle) {
        const edgeStyle = edge.style
        return isInheritance(edgeStyle) ? edgeStyle.stroke.dashStyle + marker : null
    }

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