import {
    Animator,
    Arrow, ArrowType, Fill,
    BaseClass,
    Font,
    GraphComponent,
    HierarchicNestingPolicy,
    IAnimation,
    INode,
    IPoint,
    IVisualCreator,
    Point,
    Rect,
    ShapeNodeShape,
    ShapeNodeStyle, Stroke,
    SvgExport,
    SvgVisual,
    TextRenderSupport,
    TimeSpan,
    VoidNodeStyle
} from 'yfiles'

import {ModelEdgeModel} from "@/components/zetalayout/model/model-editor/model/edges/ModelEdgeModel";
import {CustomPolyEdgeStyle} from "@/components/zetalayout/model/model-editor/model/edges/styles/CustomPolyEdgeStyle";

/**
 * Provides the visuals of the edge creation buttons.
 */
export default class ButtonVisualCreator extends BaseClass(IVisualCreator) {
    /**
     * The provided edge creation buttons.
     * @returns {IEdgeStyle[]}
     */

    static edgesForCurrentNode = [];
    static concept = {};

    static get edgeCreationButtons() {
        if (ButtonVisualCreator.edgesForCurrentNode && ButtonVisualCreator.concept) {
            return ButtonVisualCreator.edgesForCurrentNode.map(edge => {
                // ToDo: Is the concept element name for edges always "<node>.<edge-name>"? If not the next line must be adjusted
                const conceptEdge = ButtonVisualCreator.concept.references.find(r => r.name === edge.conceptElement.split(".")[1]);
                const model = new ModelEdgeModel({
                    name: conceptEdge.name,
                    description: conceptEdge.description,
                    sourceDeletionDeletesTarget: conceptEdge.sourceDeletionDeletesTarget,
                    targetDeletionDeletesSource: conceptEdge.targetDeletionDeletesSource,
                    sourceLowerBounds: conceptEdge.sourceLowerBounds,
                    sourceUpperBounds: conceptEdge.sourceUpperBounds,
                    targetLowerBounds: conceptEdge.targetLowerBounds,
                    targetUpperBounds: conceptEdge.targetUpperBounds,
                    methods: conceptEdge.methods,
                    attributes: conceptEdge.attributes
                })

                return new CustomPolyEdgeStyle(model, edge);
            })
        }
        return [];
    }

    /**
     * Creates the visual creator for the edge creation buttons.
     * @param {INode} node The node for which the buttons should be created.
     * @param {.GraphComponent} graphComponent The graph component in which the node resides.
     */
    constructor(node, graphComponent) {
        super()
        this.renderer = new ButtonIconRenderer()
        this.node = node
        this.animator = new Animator(graphComponent)
        this.animator.autoInvalidation = false
        this.animator.allowUserInteraction = true
        ButtonVisualCreator.buttons = []
    }

    /**
     * @param {.IRenderContext} ctx The context that describes where the visual will be used.
     * @returns {.Visual}
     */
    createVisual(ctx) {
        // save the button elements to conveniently use them for hit testing
        ButtonVisualCreator.buttons = []

        // the context button container
        const container = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        container.setAttribute('class', 'context-button')

        // create the edge creation buttons
        let first = -60
        const step = 40
        const animations = []

        // ToDo: Find better solution to not dependent on local storage
        const shape = JSON.parse(localStorage.getItem("shape"));
        const activeNodeName = this.node.tag.className;
        ButtonVisualCreator.edgesForCurrentNode = shape.nodes.filter(node => node.name === activeNodeName);
        ButtonVisualCreator.edgesForCurrentNode = ButtonVisualCreator.edgesForCurrentNode.length === 0 ? [] : ButtonVisualCreator.edgesForCurrentNode[0].edges;

        ButtonVisualCreator.concept = JSON.parse(localStorage.getItem("concept"));

        for (let i = 0; i < ButtonVisualCreator.edgeCreationButtons.length; i++) {
            const child = this.renderer.renderButton(ButtonVisualCreator.edgeCreationButtons[i])
            const childg1 = document.createElementNS('http://www.w3.org/2000/svg', 'g')
            const childg2 = document.createElementNS('http://www.w3.org/2000/svg', 'g')
            const childg3 = document.createElementNS('http://www.w3.org/2000/svg', 'g')
            childg1.setAttribute('transform', 'translate(-15 -15)')
            ButtonVisualCreator.buttons.push(childg1)
            childg2.setAttribute('transform', 'translate(0 0)')
            childg3.setAttribute('transform', `rotate(${-first})`)
            animations.push(new ButtonAnimation(childg1, first, childg2))

            // Tooltip on edge buttons (doesn't work properly yet)
            const title = document.createElement("title")
            title.style.setProperty("display", "inline")
            const titleText = document.createTextNode(ButtonVisualCreator.edgesForCurrentNode[i].conceptElement)
            title.appendChild(titleText)
            child.appendChild(title)

            childg1.appendChild(child)
            childg2.appendChild(childg1)
            childg3.appendChild(childg2)
            container.appendChild(childg3)
            first += step
        }
        const layout = this.node.layout
        const topRight = layout.topRight
        SvgVisual.setTranslate(container, topRight.x, topRight.y)


        // we fade the buttons via CSS
        container.setAttribute('opacity', '0')
        setTimeout(() => {
            container.setAttribute('opacity', '1')
        }, 0)

        // we animate the position 'manually' because doing it via CSS causes animation artifacts
        animations.forEach(animation => {
            this.animator.animate(animation)
        })

        // store the button state to update them if needed
        container['data-renderDataCache'] = {
            width: layout.width,
            height: layout.height,
            interfaceToggle: this.node.tag.stereotype,
            constraintToggle: this.node.tag.constraint
        }

        ButtonVisualCreator.buttons.reverse()

        return new SvgVisual(container)
    }

    /**
     * @param {.IRenderContext} ctx The context that describes where the visual will be used in.
     * @param {.Visual} oldVisual The visual instance that had been returned the last time the
     *   {@link .IVisualCreator#createVisual} method was called on this instance.
     * @returns {.Visual}
     */
    updateVisual(ctx, oldVisual) {
        const layout = this.node.layout
        const topRight = layout.topRight
        const svgElement = oldVisual.svgElement
        const cache = svgElement['data-renderDataCache']

        // update the container layout
        SvgVisual.setTranslate(svgElement, topRight.x, topRight.y)

        if (cache.width !== layout.width || cache.height !== layout.height) {
            cache.width = layout.width
            cache.height = layout.height
        }

        return oldVisual
    }

    /**
     * Helper method to get the edge style of the edge creation button if there is a button at the given location.
     * @param {.CanvasComponent} canvasComponent The canvas component in which the node resides.
     * @param {INode} node The node who should be checked for a button.
     * @param {IPoint} location The world location to check for a button.
     * @returns {IEdgeStyle} The edge style if there is a button at the given location, otherwise null.
     */
    static getStyleButtonAt(canvasComponent, node, location) {
        for (let i = 0; i < ButtonVisualCreator.buttons.length; i++) {
            const boundingRect = ButtonVisualCreator.buttons[i].getBoundingClientRect()
            const worldTopLeft = canvasComponent.toWorldFromPage(
                new Point(boundingRect.left, boundingRect.top)
            )
            if (
                location.x >= worldTopLeft.x &&
                location.x <= worldTopLeft.x + 30 &&
                location.y >= worldTopLeft.y &&
                location.y <= worldTopLeft.y + 30
            ) {
                return ButtonVisualCreator.edgeCreationButtons[i]
            }
        }
        return null
    }
}

/**
 * Executes the button fan out animation.
 */
class ButtonAnimation extends BaseClass(IAnimation) {
    constructor(rotationElement, finishAngle, translationElement) {
        super()
        this.rotationElement = rotationElement
        this.translationElement = translationElement
        this.finishAngle = finishAngle
    }

    get preferredDuration() {
        return TimeSpan.fromMilliseconds(200)
    }

    /**
     * @param {number} time - the animation time [0,1]
     */
    animate(time) {
        this.rotationElement.setAttribute(
            'transform',
            `rotate(${time * this.finishAngle}) translate(-15 -15)`
        )
        this.translationElement.setAttribute('transform', `translate(${time * 50} 0)`)
    }

    cleanUp() {
    }

    initialize() {
    }
}

/**
 * Helper class that creates a round button visual containing a given edge style visualization.
 */
class ButtonIconRenderer {
    constructor() {
        this.gc = new GraphComponent()
        this.gc.graphModelManager.hierarchicNestingPolicy = HierarchicNestingPolicy.NONE
        this.gc.graphModelManager.edgeGroup.above(this.gc.graphModelManager.nodeGroup)
    }

    renderButton(edgeStyle) {
        const graph = this.gc.graph
        graph.clear()
        const style = new ShapeNodeStyle({
            fill: 'white',
            stroke: '#607D8B',
            shape: ShapeNodeShape.ELLIPSE
        })
        graph.createNode(new Rect(-15, -15, 30, 30), style)
        const src = graph.createNode(new Rect(-10, 0, 1, 1), VoidNodeStyle.INSTANCE)
        const tgt = graph.createNode(new Rect(10, 0, 1, 1), VoidNodeStyle.INSTANCE)
        graph.createEdge({
            source: src,
            target: tgt,
            style: edgeStyle
        })
        const svgExport = new SvgExport(new Rect(-18, -18, 36, 36))
        return svgExport.exportSvg(this.gc)
    }

    renderTextButton(text) {
        const textSize = TextRenderSupport.measureText(
            text,
            new Font({
                fontFamily: 'monospace',
                fontSize: 18
            })
        )
        const container = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        const background = document.createElementNS('http://www.w3.org/2000/svg', 'rect')
        background.setAttribute('width', '20')
        background.setAttribute('height', '20')
        background.setAttribute('fill', '#FFF')
        background.setAttribute('stroke', '#607D8B')
        const textElement = document.createElementNS('http://www.w3.org/2000/svg', 'text')
        textElement.setAttribute('font-family', 'monospace')
        textElement.setAttribute('font-size', '18')
        textElement.setAttribute('x', `${(20 - textSize.width) / 2}`)
        textElement.setAttribute('y', '16')
        textElement.textContent = text
        container.appendChild(background)
        container.appendChild(textElement)
        return container
    }
}
