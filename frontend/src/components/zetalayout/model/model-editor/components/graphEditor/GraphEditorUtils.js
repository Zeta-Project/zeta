import 'yfiles/yfiles.css';
import * as umlModel from '../../model/nodes/ModelClassModel.js'

import {
    Class,
    GraphEditorInputMode,
    GraphSnapContext,
    GridSnapTypes,
    HierarchicLayout,
    HierarchicLayoutData,
    //IEdge,
    LabelSnapContext,
    LayoutExecutor,
    OrthogonalEdgeEditingContext,
    PolylineEdgeStyle,
} from 'yfiles'

import '../../styles/layout.css'
import '../../styles/paper.css'
import '../../styles/stencil.css'
import '../../styles/style.css'
import '../../styles/sidebar.css'
import * as umlEdgeModel from '../../model/edges/ModelEdgeModel'
import Exporter from "../../export/Exporter";
import {isSuccessStatus, ZetaApiWrapper} from "../../utils/ZetaApiWrapper";
import {showExportFailure, showSnackbar} from "../../utils/Snackbar";
import {Attribute} from "../../model/attributes/Attribute";
import {Parameter} from "../../model/parameters/Parameter";
import {Operation} from "../../model/operations/Operation";
import {createAggregationStyle, createAssociationStyle, createCompositionStyle, createGeneralizationStyle, isInheritance} from "../../model/edges/styles/UMLEdgeStyleFactory";
import VuejsNodeStyle from "../../model/nodes/styles/VuejsNodeStyle";
import axios from "axios";
import { EventBus } from "@/eventbus/eventbus";

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
export function saveGraph(graphComponent, loadedMetaModel, metamodelID) {
    if (loadedMetaModel.constructor === Object && Object.entries(loadedMetaModel).length > 0) {

        const graph = graphComponent.graph;

        const exporter = new Exporter(graph);
        const exportedMetaModel = exporter.export();
        if (exportedMetaModel.isValid()) {
            console.log(exportedMetaModel)
            let data = JSON.stringify({
                name: loadedMetaModel.name,
                classes: exportedMetaModel.getClasses(),
                references: exportedMetaModel.getReferences(),
                enums: exportedMetaModel.getEnums(),
                attributes: exportedMetaModel.getAttributes(),
                methods: exportedMetaModel.getMethods(),
                uiState: JSON.stringify({"empty": "value"})
            });

            // ZetaApiWrapper.prototype.postConceptDefinition(loadedMetaModel.uuid, data)
            const url = "http://localhost:9000" + "/rest/v1/meta-models/" + metamodelID + "/definition";
            axios.put(
                url,
                data,
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }
            ).then(
                response => {
                    EventBus.$emit('successMessage', "Successfully saved concept")
                },
                error => {
                    EventBus.$emit('errorMessage', "Failed to save concept: " + error)
                }
            )


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
        edgeDirectedness: () => (0),
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
export function addNodeStyleToNodes(vuejsComponent, nodes) {
    return nodes.map(node => {
        let style = new VuejsNodeStyle(
            vuejsComponent,
            new umlModel.ModelClassModel({
                className: node.name,
                description: node.description,
                abstract: node.abstractness,
                superTypeNames: node.superTypeNames,
                attributes: node.attributes,
                operations: node.methods
            })
        );

        /*if (node.abstractness === true) {
            style.model.constraint = 'abstract';
            style.model.stereotype = '';
            style.fill = Fill.CRIMSON;
        }*/

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
    references = references.filter(reference => {
        return nodes.some(node => (node.tag.name === reference.sourceClassName) || (node.tag.name === reference.targetClassName))
    });
    // Transform references to Zeta specific EdgeModels
    return references.map(reference => {
        const source = nodes.find(node => node.tag.name === reference.sourceClassName);
        const target = nodes.find(node => node.tag.name === reference.targetClassName);
        const model = new umlEdgeModel.ModelEdgeModel({
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
 * @returns {*}
 * @param edge
 */
export function getStyleForEdge(edge) {
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

    return edgeStyle
}


export function buildGraphFromDefinition() {
    // legacy
}
