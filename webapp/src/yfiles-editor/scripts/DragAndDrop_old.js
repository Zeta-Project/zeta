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

import {addClass, removeClass} from "./utils/Bindings";
import {passiveSupported, pointerEventsSupported} from "./utils/Workarounds";
import {UMLNodeStyle} from './UMLNodeStyle.js';
import * as umlModel from "./UMLClassModel";
import {UMLClassModel} from "./UMLClassModel";

export class DragAndDrop_old {

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
 */
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

}

/**
 * Creates and adds a visual for the given style in the drag and drop panel.
 * @param {INodeStyle} style
 * @param {HTMLElement} panel
 * @param graphComponent
 */
function addNodeVisual(style, panel, graphComponent) {
    // Create the HTML element for the visual.
    const div = document.createElement('div')
    div.setAttribute('style', 'width: 110px; height: 50px; margin: 10px auto; cursor: grab;')
    const img = document.createElement('img')
    img.setAttribute('style', 'width: auto; height: auto;')
    // Create a visual for the style.
    img.setAttribute('src', createNodeVisual(style))

    const startDrag = () => {
        // Create preview node with which the GraphComponent can render a preview during the drag gesture.
        const simpleNode = new SimpleNode();
        simpleNode.style = style
        simpleNode.layout = new Rect(0, 0, 150,100) //created node size && preview on graphComponent

        // We also want to show a preview of dragged node, while the dragging is not within the GraphComponent.
        // For this, we can provide an element that will be placed at the mouse position during the drag gesture.
        // Of course, this should resemble the node that is currently dragged.
        const dragPreview = document.createElement('div')
        dragPreview.appendChild(img.cloneNode(true))

        // The core method that initiates a drag which is recognized by the GraphComponent.
        const dragSource = NodeDropInputMode.startDrag(
            div, // The source of the drag gesture, i.e. the element in the drag and drop panel.
            simpleNode, // The node that is dragged. This is used to provide a preview within the GC during the drag.
            DragDropEffects.ALL, // The allowed actions for this drag.
            true, // Whether to the cursor during the drag.
            pointerEventsSupported ? dragPreview : null // The optional preview element that is shown outside of the GC during the drag.
        )

        // Within the GraphComponent, it draws its own preview node. Therefore, we need to hide the additional
        // preview element that is used outside of the GraphComponent.
        // The GraphComponent uses its own preview node to support features like snap lines or snapping of the dragged node.
        dragSource.addQueryContinueDragListener((src, args) => {
            if (args.dropTarget === null) {
                removeClass(dragPreview, 'hidden')
            } else {
                addClass(dragPreview, 'hidden')
            }
        })
    }

    img.addEventListener(
        'mousedown',
        event => {
            startDrag()
            event.preventDefault()
        },
        false
    )
    img.addEventListener(
        'touchstart',
        event => {
            startDrag()
            event.preventDefault()
        },
        passiveSupported ? {passive: false} : false
    )
    div.appendChild(img)
    panel.appendChild(div)
}

/**
 * Creates an SVG data string for a node with the given style.
 * @param {INodeStyle} style
 * @return {string}
 */
function createNodeVisual(style) {
    // another GraphComponent is utilized to export a visual of the given style
    const exportComponent = new GraphComponent()
    const exportGraph = exportComponent.graph

    // we create a node in this GraphComponent that should be exported as SVG
    exportGraph.createNode(new Rect(0, 0, 100, 50), style) // panel node size
    exportComponent.updateContentRect(new Insets(5))

    // the SvgExport can export the content of any GraphComponent
    const svgExport = new SvgExport(exportComponent.contentRect)
    const svg = svgExport.exportSvg(exportComponent)
    const svgString = SvgExport.exportSvgString(svg)
    return SvgExport.encodeSvgDataUrl(svgString)
}