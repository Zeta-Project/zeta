import {
    ConcurrencyController,
    DefaultPortCandidate,
    DefaultSelectionModel,
    Fill,
    FreeNodePortLocationModel,
    GraphEditorInputMode,
    ICanvasObjectDescriptor,
    IInputModeContext,
    IModelItem,
    INode,
    ISelectionIndicatorInstaller,
    InputModeBase,
    ModelManager,
    Point
} from 'yfiles'

import ButtonVisualCreator from './ButtonVisualCreator.js'
import NodeCandidateProvider from './NodeCandidateProvider.js'

/**
 * An {@link IInputMode} which will provide buttons for edge creation for the graph component's current item.
 * When one of the buttons is dragged, a new edge with the specified style is created.
 */
export default class ModelContextButtonsInputMode extends InputModeBase {
    constructor() {
        super()
        this.buttonNodes = new DefaultSelectionModel()

        // initializes listener functions in order to install/uninstall them
        this.onCurrentItemChangedListener = () => this.onCurrentItemChanged()
        this.onNodeRemovedListener = (sender, evt) => this.onNodeRemoved(evt.item)
        this.startEdgeCreationListener = (sender, evt) => this.startEdgeCreation(evt.location)

        this.target = ""
    }

    /**
     * Installs the necessary listeners of this input mode.
     * @param {IInputModeContext} context the context to install this mode into
     * @param {ConcurrencyController} controller The {@link InputModeBase#controller} for
     *   this mode.
     */
    install(context, controller) {
        super.install(context, controller)

        // use a selection indicator manager which only "selects" the current item
        // so the buttons are only displayed for one node
        const graphComponent = context.canvasComponent
        this.manager = new MySelectionIndicatorManager(graphComponent, this.buttonNodes)

        // keep buttons updated and their add interaction
        graphComponent.addCurrentItemChangedListener(this.onCurrentItemChangedListener)
        graphComponent.addMouseDragListener(this.startEdgeCreationListener)
        graphComponent.addMouseClickListener(this.startEdgeCreationListener)
        graphComponent.addTouchMoveListener(this.startEdgeCreationListener)
        graphComponent.addTouchDownListener(this.startEdgeCreationListener)
        graphComponent.graph.addNodeRemovedListener(this.onNodeRemovedListener)
    }

    /**
     * Called when the graph component's current item changed to move the buttons to the current item.
     */
    onCurrentItemChanged() {
        const graphComponent = this.inputModeContext.canvasComponent
        if (INode.isInstance(graphComponent.currentItem)) {
            this.buttonNodes.clear()
            this.buttonNodes.setSelected(graphComponent.currentItem, true)
        } else {
            this.buttonNodes.clear()
        }
    }

    /**
     * Called when a user clicks the empty graph (neither a node, nor an edge).
     */
    hideButtons() {
        this.buttonNodes.clear()
    }

    /**
     * Remove the button visuals when the node is deleted.
     * @param {IModelItem} item
     */
    onNodeRemoved(item) {
        this.buttonNodes.setSelected(item, false)
    }

    /**
     * Called when the mouse button is pressed to initiate edge creation in case a button is hit.
     * @param {Point} location
     */
    startEdgeCreation(location) {
        if (this.active && this.canRequestMutex()) {
            const graphComponent = this.inputModeContext.canvasComponent

            // check which node currently has the buttons and invoke create edge input mode to create a new edge
            for (let enumerator = this.buttonNodes.getEnumerator(); enumerator.moveNext();) {
                const buttonNode = enumerator.current
                const styleButton = ButtonVisualCreator.getStyleButtonAt(
                    graphComponent,
                    buttonNode,
                    location
                )

                const edgeModel = styleButton?.model?.edgeModel;
                if (styleButton) {
                    if (edgeModel?.name) {
                        const shape = JSON.parse(window.localStorage.getItem("shape"))
                        if (shape?.edges) {
                            shape.edges.forEach(edge => {
                                if (edge.conceptElement && edgeModel.name === edge.conceptElement.split(".").pop() && edge.target) {
                                    const currentNodes = shape.nodes.filter(node => {
                                        return node.conceptElement === edge.target
                                    })
                                    if (currentNodes.length) {
                                        this.target = currentNodes[0].name
                                    }
                                }
                            })
                        }
                    }

                    const parentInputMode = this.inputModeContext.parentInputMode
                    if (parentInputMode instanceof GraphEditorInputMode) {
                        const createEdgeInputMode = parentInputMode.createEdgeInputMode
                        registerPortCandidateProvider(graphComponent.graph, this.target)

                        // initialize dummy edge
                        const modelEdgeType = styleButton
                        const dummyEdgeGraph = createEdgeInputMode.dummyEdgeGraph
                        const dummyEdge = createEdgeInputMode.dummyEdge

                        // Mirror the model content to the 'tag' property to be able to access the model via the 'tag'
                        // property (for consistency between node and edge handling)
                        dummyEdge.tag = edgeModel;
                        dummyEdgeGraph.setStyle(dummyEdge, modelEdgeType)
                        dummyEdgeGraph.edgeDefaults.style = modelEdgeType




                        // Currently only labels of type "textfield" are displayed
                        const labelPlacing = styleButton.model.placings
                            .find(p => p.geoElement.type === "textfield");

                        console.log("labelPlacing", JSON.stringify(labelPlacing));
                        console.log("edgeModel", JSON.stringify(edgeModel));

                        if(labelPlacing){
                            const text = labelPlacing.geoElement.textBody;
                            dummyEdgeGraph.addLabel(dummyEdge, text);
                        }

                        // dummyEdgeGraph.addLabel(dummyEdge, "lorem ipsum test x 123 lol foo ba", null, new DefaultLabelStyle({
                        //     wrapping: "word",
                        //     maximumSize: new Size(100, 200)
                        // }))

                        // const x = new FreeLabelModel()
                        // const y = x.createDefaultParameter()
                        //
                        // const v = new DefaultLabelStyle({wrapping: "word"})
                        // const c = dummyEdgeGraph.addLabel(dummyEdge, "",)
                        // dummyEdgeGraph.label




                        // start edge creation and hide buttons until the edge is finished
                        this.buttonNodes.clear()
                        createEdgeInputMode.doStartEdgeCreation(
                            new DefaultPortCandidate(buttonNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED)
                        )
                        const listener = () => {
                            graphComponent.selection.clear()
                            graphComponent.currentItem = null
                            createEdgeInputMode.removeGestureCanceledListener(listener)
                            createEdgeInputMode.removeGestureFinishedListener(listener)
                        }
                        createEdgeInputMode.addGestureFinishedListener(listener)
                        createEdgeInputMode.addGestureCanceledListener(listener)
                        return
                    }
                }
            }
        }
    }

    /**
     * Removed the installed listeners when they are not needed anymore.
     * @param {IInputModeContext} context - The context to remove this mode from. This is the same
     *   instance that has been passed to {@link InputModeBase#install}.
     */
    uninstall(context) {
        const graphComponent = context.canvasComponent
        graphComponent.removeCurrentItemChangedListener(this.onCurrentItemChangedListener)
        graphComponent.removeMouseDragListener(this.startEdgeCreationListener)
        graphComponent.removeMouseClickListener(this.startEdgeCreationListener)
        graphComponent.removeTouchMoveListener(this.startEdgeCreationListener)
        graphComponent.removeTouchDownListener(this.startEdgeCreationListener)
        graphComponent.graph.removeNodeRemovedListener(this.onNodeRemovedListener)
        this.buttonNodes.clear()
        this.manager.dispose()
        this.manager = null
        super.uninstall(context)
    }
}

/**
 * A selection manager that draws the selection visualization in front of the input mode objects, such that
 * the edge creation buttons are drawn on top of the selected etc.
 */
class MySelectionIndicatorManager extends ModelManager {
    constructor(canvas, model) {
        super(canvas)
        this.model = model
        this.buttonGroup = canvas.inputModeGroup.addGroup()
        this.selectionChangedListener = (sender, evt) =>
            this.selectionChanged(evt.item, evt.itemSelected)
        this.model.addItemSelectionChangedListener(this.selectionChangedListener)
    }

    /**
     * @param {T} item The item to find an installer for.
     * @returns {ICanvasObjectInstaller}
     */
    getInstaller(item) {
        return new ISelectionIndicatorInstaller((iCanvasContext, iCanvasObjectGroup, node) =>
            iCanvasObjectGroup.addChild(
                new ButtonVisualCreator(node, this.canvasComponent),
                ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE
            )
        )
    }

    /**
     * @param {T} item The item to find a canvas object group for.
     * @returns {ICanvasObjectGroup}
     */
    getCanvasObjectGroup(item) {
        return this.buttonGroup
    }

    /**
     * Called when the selection of the internal model changes.
     * @param {IModelItem} item The item that is the subject of the event
     * @param {boolean} itemSelected Whether the item is selected
     */
    selectionChanged(item, itemSelected) {
        if (itemSelected) {
            this.add(item)
        } else {
            this.remove(item)
        }
    }

    /**
     * Cleanup the selection manager.
     */
    dispose() {
        this.model.removeItemSelectionChangedListener(this.selectionChangedListener)
        this.buttonGroup.remove()
    }

    onDisabled() {
    }

    onEnabled() {
    }
}

/**
 * Registers a callback function as decorator that provides a custom
 * {@link IPortCandidateProvider} for each node.
 * This callback function is called whenever a node in the graph is queried
 * for its <code>IPortCandidateProvider</code>. In this case, the 'node'
 * parameter will be assigned that node.
 * @param {IGraph} graph The given graph
 */
function registerPortCandidateProvider(graph, target) {
    graph.decorator.nodeDecorator.portCandidateProviderDecorator.setFactory(node => {
        // Obtain the tag from the edge
        const nodeTag = node.tag

        // Check if it is a known tag and choose the respective implementation
        if (nodeTag) {
            return new NodeCandidateProvider(node, target);
        }
    })
}