import {
    DragDropEffects,
    GraphComponent,
    INodeStyle,
    Insets,
    NodeDropInputMode,
    PanelNodeStyle,
    Rect,
    SimpleNode,
    SvgExport
} from "yfiles";

export class Properties {

    constructor(graphComponent) {
        // retrieve the panel element
        const panel = document.getElementById('properties-panel')
    }

}

/**
 constructor(graphComponent) {
        // Obtain the input mode for handling dropped nodes from the GraphEditorInputMode.
        const nodeDropInputMode = graphComponent.inputMode.nodeDropInputMode
        // By default the mode available in GraphEditorInputMode is disabled, so first enable it.
        nodeDropInputMode.enabled = true
        // Certain nodes should be created as group nodes. In this case we distinguish them by their style.
        nodeDropInputMode.isGroupNodePredicate = draggedNode =>
            draggedNode.style instanceof PanelNodeStyle
        // When dragging the node within the GraphComponent, we want to show a preview of that node.
        nodeDropInputMode.showPreview = true

        initializeDragAndDropPanel(graphComponent)
    }
 }

 /**
 * Initializes the palette of nodes that can be dragged to the graph component.

function initializeDragAndDropPanel(graphComponent) {
    // retrieve the panel element
    const panel = document.getElementById('drag-and-drop-panel')
    // prepare node styles for the palette
    const defaultNodeStyle = graphComponent.graph.nodeDefaults.style
    const nodeStyles = [defaultNodeStyle]

    // add a visual for each node style to the palette
    nodeStyles.forEach((style) => {
        addNodeVisual(style, panel, graphComponent)
    })

}*/