import { GeneralPath, MutablePoint, NodeStyleBase, SvgVisual } from 'yfiles'

/**
 * A very simple implementation of an {@link INodeStyle}
 * that uses the convenience class {@link NodeStyleBase}
 * as the base class.
 */
export default class BlueNodeStyle extends NodeStyleBase {
    constructor() {
        super()
        this.$nodeColor = 'rgba(0,130,180,1)'
    }

    /**
     * Counts the number of gradient fills used to generate a unique id.
     * @return {number}
     */
    static get fillCounter() {
        BlueNodeStyle.$fillCounter = (BlueNodeStyle.$fillCounter || 0) + 1
        return BlueNodeStyle.$fillCounter
    }

    /**
     * Creates the visual for a node.
     * @see Overrides {@link NodeStyleBase#createVisual}
     * @return {SvgVisual}
     */
    createVisual(context, node) {
        // This implementation creates a 'g' element and uses it as a container for the rendering of the node.
        const g = window.document.createElementNS('http://www.w3.org/2000/svg', 'g')
        // Render the node

        this.render(context, node, g)
        // set the location
        SvgVisual.setTranslate(g, node.layout.x, node.layout.y)
        return new SvgVisual(g)
    }


    /**
     * Actually creates the visual appearance of a node given the values provided by
     * {@link MySimpleNodeStyle#createRenderDataCache}. This renders the node and the edges to the labels and adds the
     * elements to the <code>container</code>. All items are arranged as if the node was located at (0,0).
     * {@link MySimpleNodeStyle#createVisual} and {@link MySimpleNodeStyle#updateVisual} finally arrange the container
     * so that the drawing is translated into the final position.
     */
    render(context, node, container) {
        // Create Defs section in container
        const defs = window.document.createElementNS('http://www.w3.org/2000/svg', 'defs')
        container.appendChild(defs)

        // determine the color to use for the rendering
        const color = 'rgba(0, 130, 180, 1)'

        // the size of node
        const nodeSize = node.layout.toSize()

        // add simple drop shadow
        const shadow = window.document.createElementNS('http://www.w3.org/2000/svg', 'ellipse')
        const shadowWidth = nodeSize.width * 0.5
        const shadowHeight = nodeSize.height * 0.5
        shadow.cx.baseVal.value = shadowWidth
        shadow.cy.baseVal.value = shadowHeight
        shadow.rx.baseVal.value = shadowWidth
        shadow.ry.baseVal.value = shadowHeight
        shadow.setAttribute('fill', 'black')
        shadow.setAttribute('fill-opacity', 0.2)
        shadow.setAttribute('transform', 'translate(3 3)')
        container.appendChild(shadow)

        const ellipse = window.document.createElementNS('http://www.w3.org/2000/svg', 'ellipse')
        const w = nodeSize.width * 0.5
        const h = nodeSize.height * 0.5
        ellipse.cx.baseVal.value = w
        ellipse.cy.baseVal.value = h
        ellipse.rx.baseVal.value = w
        ellipse.ry.baseVal.value = h

        // max and min needed for reflection effect calculation
        const max = Math.max(nodeSize.width, nodeSize.height)
        const min = Math.min(nodeSize.width, nodeSize.height)

        if (nodeSize.width > 0 && nodeSize.height > 0) {
            // Create Background gradient from specified background color
            const gradient = window.document.createElementNS(
                'http://www.w3.org/2000/svg',
                'linearGradient'
            )
            gradient.setAttribute('x1', 0)
            gradient.setAttribute('y1', 0)
            gradient.setAttribute('x2', 0.5 / (nodeSize.width / max))
            gradient.setAttribute('y2', 1 / (nodeSize.height / max))
            gradient.setAttribute('spreadMethod', 'pad')
            const stop1 = window.document.createElementNS('http://www.w3.org/2000/svg', 'stop')
            stop1.setAttribute('stop-color', 'white')
            stop1.setAttribute('stop-opacity', '0.7')
            stop1.setAttribute('offset', '0')
            const stop2 = window.document.createElementNS('http://www.w3.org/2000/svg', 'stop')
            stop2.setAttribute('stop-color', color)
            stop2.setAttribute('offset', '0.6')
            gradient.appendChild(stop1)
            gradient.appendChild(stop2)

            // creates the gradient id
            const fillId = `UMLNodeStyle_fill${BlueNodeStyle.fillCounter}`
            // assigns the id
            gradient.id = fillId
            // puts the gradient in the container's defs section
            defs.appendChild(gradient)
            // sets the fill reference in the ellipse
            ellipse.setAttribute('fill', `url(#${fillId})`)
        }

        // Create light reflection effects
        const reflection1 = window.document.createElementNS('http://www.w3.org/2000/svg', 'ellipse')
        const reflection1Size = min / 20
        reflection1.cx.baseVal.value = reflection1Size
        reflection1.cy.baseVal.value = reflection1Size
        reflection1.rx.baseVal.value = reflection1Size
        reflection1.ry.baseVal.value = reflection1Size
        reflection1.setAttribute('fill', 'white')
        const reflection2 = window.document.createElementNS('http://www.w3.org/2000/svg', 'ellipse')
        const reflection2Size = min / 14
        reflection2.cx.baseVal.value = reflection2Size
        reflection2.cy.baseVal.value = reflection2Size
        reflection2.rx.baseVal.value = reflection2Size
        reflection2.ry.baseVal.value = reflection2Size
        reflection2.setAttribute('fill', 'aliceblue')

        const reflection3Path = new GeneralPath()
        const startPoint = new MutablePoint(nodeSize.width / 2.5, nodeSize.height / 10 * 9)
        const endPoint = new MutablePoint(nodeSize.width / 10 * 9, nodeSize.height / 2.5)
        const ctrlPoint1 = new MutablePoint(
            startPoint.x + (endPoint.x - startPoint.x) / 2,
            nodeSize.height
        )
        const ctrlPoint2 = new MutablePoint(
            nodeSize.width,
            startPoint.y + (endPoint.y - startPoint.y) / 2
        )
        const ctrlPoint3 = new MutablePoint(ctrlPoint1.x, ctrlPoint1.y - nodeSize.height / 10)
        const ctrlPoint4 = new MutablePoint(ctrlPoint2.x - nodeSize.width / 10, ctrlPoint2.y)

        reflection3Path.moveTo(startPoint)
        reflection3Path.cubicTo(ctrlPoint1, ctrlPoint2, endPoint)
        reflection3Path.cubicTo(ctrlPoint4, ctrlPoint3, startPoint)

        const reflection3 = reflection3Path.createSvgPath()
        reflection3.setAttribute('fill', 'aliceblue')

        // place the reflections
        reflection1.setAttribute('transform', `translate(${nodeSize.width / 5} ${nodeSize.height / 5})`)
        reflection2.setAttribute(
            'transform',
            `translate(${nodeSize.width / 4.9} ${nodeSize.height / 4.9})`
        )
        // and add all to the container for the node
        container.appendChild(ellipse)
        container.appendChild(reflection2)
        container.appendChild(reflection1)
        container.appendChild(reflection3)
    }
}