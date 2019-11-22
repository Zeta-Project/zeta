import {
    DefaultLabelStyle,
    ShapeNodeShape,
    DragDropEffects,
    GraphEditorInputMode,
    GraphSnapContext,
    InteriorStretchLabelModel,
    ListEnumerable,
    NodeDropInputMode,
    PanelNodeStyle,
    Rect,
    SimpleLabel,
    SimpleNode,
    GridSnapTypes,
    ILabel,
    LabelDropInputMode,
    PortDropInputMode,
    IEdge,
    DragSource,
    DragDropItem,
    IPort,
    ShapeNodeStyle,
    ShinyPlateNodeStyle,
    SimpleEdge,
    PolylineEdgeStyle,
    VoidNodeStyle,
    NodeStylePortStyleAdapter,
    SimplePort,
    ImageNodeStyle,
    FreeNodePortLocationModel,
    FreeNodeLabelModel
} from "yfiles";

import {DragAndDropPanel} from "./utils/DndPanel";
import {passiveSupported} from "./utils/Workarounds";
import {addClass, removeClass} from "./utils/Bindings";

export class DragAndDrop {

    constructor(graphComponent) {
        DnDConfigureInputModes(graphComponent)
        InitializeDnDPanel(graphComponent)
    }
}

function DnDConfigureInputModes(graphComponent) {
    const nodeDropInputMode = graphComponent.inputMode.nodeDropInputMode
    // By default the mode available in GraphEditorInputMode is disabled, so first enable it.
    nodeDropInputMode.enabled = true
    // Certain nodes should be created as group nodes. In this case we distinguish them by their style.
    nodeDropInputMode.isGroupNodePredicate = draggedNode =>
        draggedNode.style instanceof PanelNodeStyle
    // When dragging the node within the GraphComponent, we want to show a preview of that node.
    nodeDropInputMode.showPreview = true
    // initially disables snapping fo the dragged element to existing elements
    nodeDropInputMode.snappingEnabled = true
}

function InitializeDnDPanel(graphComponent) {

    // initialize panel for yFiles drag and drop
    let yZetaDragAndDropPanel = new DragAndDropPanel(document.getElementById('drag-and-drop-panel'), passiveSupported)

    // Set the callback that starts the actual drag and drop operation
    yZetaDragAndDropPanel.beginDragCallback = (element, data) => {
        const dragPreview = element.cloneNode(true)
        dragPreview.style.margin = ''
        let dragSource

        if (ILabel.isInstance(data)) {
            dragSource = LabelDropInputMode.startDrag(
                element,
                data,
                DragDropEffects.ALL,
                true,
                dragPreview
            )
        } else if (IPort.isInstance(data)) {
            dragSource = PortDropInputMode.startDrag(
                element,
                data,
                DragDropEffects.ALL,
                true,
                dragPreview
            )
        } else if (IEdge.isInstance(data)) {
            new DragSource(element).startDrag(
                new DragDropItem('yfiles.graph.IEdge', data),
                DragDropEffects.ALL
            )
        } else {
                dragSource = NodeDropInputMode.startDrag(
                element,
                data,
                DragDropEffects.ALL,
                true,
                dragPreview
            )
        }

        // let the GraphComponent handle the preview rendering if possible
        if (dragSource) {
            dragSource.addQueryContinueDragListener((src, args) => {
                if (args.dropTarget === null) {
                    removeClass(dragPreview, 'hidden')
                } else {
                    addClass(dragPreview, 'hidden')
                }
            })
        }


    }

    yZetaDragAndDropPanel.maxItemWidth = 100
    yZetaDragAndDropPanel.populatePanel(createDnDPanelItems(graphComponent))
}

function createDnDPanelItems(graphComponent) {
    const itemContainer = []

    // Create nodes and push them into the itemContainer
    const Node1 = new SimpleNode()
    Node1.layout = new Rect(0, 0, 150, 100)
    Node1.style = graphComponent.graph.nodeDefaults.style
    itemContainer.push({ element: Node1, tooltip: 'Node' })

    const Node2 = new SimpleNode()
    Node2.layout = new Rect(0, 0, 150, 100)
    Node2.style = graphComponent.graph.nodeDefaults.style
    itemContainer.push({ element: Node2, tooltip: 'Node' })

    const shinyPlateNode = new SimpleNode()
    shinyPlateNode.layout = new Rect(0, 0, 60, 40)
    shinyPlateNode.style = new ShinyPlateNodeStyle({
        fill: 'rgb(255, 140, 0)',
        drawShadow: false
    })
    itemContainer.push({ element: shinyPlateNode, tooltip: 'Shiny Plate Node' })
    // PUSH - Element in Container schieben

    /* More Shapes, Labels, Edges for the itemContainer

    const shapeStyleNode = new SimpleNode()
    shapeStyleNode.layout = new Rect(0, 0, 60, 40)
    shapeStyleNode.style = new ShapeNodeStyle({
        shape: ShapeNodeShape.ROUND_RECTANGLE,
        stroke: 'rgb(255, 140, 0)',
        fill: 'rgb(255, 140, 0)'
    })
    itemContainer.push({ element: shapeStyleNode, tooltip: 'Shape Node' })
    // PUSH - Element in Container schieben

    const imageStyleNode = new SimpleNode()
    imageStyleNode.layout = new Rect(0, 0, 60, 60)
    imageStyleNode.style = new ImageNodeStyle('resources/y.svg')
    itemContainer.push({ element: imageStyleNode, tooltip: 'Image Node' })
    // PUSH - Element in Container schieben

    const portNode = new SimpleNode()
    portNode.layout = new Rect(0, 0, 5, 5)
    portNode.style = new VoidNodeStyle()
    const port = new SimplePort(portNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED)
    port.style = new NodeStylePortStyleAdapter(
        new ShapeNodeStyle({
            fill: 'darkblue',
            stroke: 'cornflowerblue',
            shape: 'ellipse'
        })
    )
    portNode.tag = port
    portNode.ports = new ListEnumerable([port])
    itemContainer.push({ element: portNode, tooltip: 'Port' })
    // PUSH - Element in Container schieben

    const labelNode = new SimpleNode()
    labelNode.layout = new Rect(0, 0, 5, 5)
    labelNode.style = new VoidNodeStyle()

    const labelStyle = new DefaultLabelStyle({
        backgroundStroke: 'rgb(101, 152, 204)',
        backgroundFill: 'white',
        insets: [3, 5, 3, 5]
    })

    const label = new SimpleLabel(
        labelNode,
        'label',
        FreeNodeLabelModel.INSTANCE.createDefaultParameter()
    )
    label.style = labelStyle
    label.preferredSize = labelStyle.renderer.getPreferredSize(label, labelStyle)
    labelNode.tag = label
    labelNode.labels = new ListEnumerable([label])
    itemContainer.push({ element: labelNode, tooltip: 'Label' })
    // PUSH - Element in Container schieben
    */
    const edge1 = new SimpleEdge({
        style: new PolylineEdgeStyle({
            smoothingLength: 100,
            targetArrow: 'triangle'
        })
    })
    const edge2 = new SimpleEdge({
        style: new PolylineEdgeStyle({
            sourceArrow: 'triangle',
            targetArrow: 'triangle'
        })
    })
    const edge3 = new SimpleEdge({
        style: new PolylineEdgeStyle({
            stroke: '2px dashed gray',
            targetArrow: 'triangle'
        })
    })

    itemContainer.push({ element: edge1, tooltip: 'Default' })
    itemContainer.push({ element: edge2, tooltip: 'Bidirectional' })
    itemContainer.push({ element: edge3, tooltip: 'Dashed' })
    // PUSH - Element in Container schieben

    return itemContainer
}