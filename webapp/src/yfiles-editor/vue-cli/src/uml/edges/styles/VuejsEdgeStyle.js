/****************************************************************************
 ** @license
 ** This demo file is part of yFiles for HTML 2.2.0.2.
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for HTML functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for HTML version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for HTML powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for HTML
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
import {Arrow, ArrowType, EdgeStyleBase, Fill, INode, IRenderContext, Rect, Size, Stroke, SvgVisual} from 'yfiles'
import {UMLEdgeModel} from "../UMLEdgeModel";


const defaultStyle = {
    targetArrow: new Arrow({
        stroke: Stroke.BLACK,
        fill: Fill.BLACK,
        type: ArrowType.DIAMOND
    })
}

/**
 * A node style which uses a Vuejs component to display a node.
 */
export default class VuejsEdgeStyle extends EdgeStyleBase {
    /**
     * Creates a new instance of the UML node style.
     * @param vueComponentConstructor: constructor of a vue js node
     * @param {Fill?} fill The background fill of the header sections.
     * @param {Fill?} highlightFill The background fill of the selected entry.
     */
    constructor(vueComponentConstructor, fill, highlightFill) {
        super()
        this.$vueComponentConstructor = vueComponentConstructor;
    }

    /**
     * Creates a visual that uses a Vuejs component to display a node.
     * @see Overrides {@link LabelStyleBase#createVisual}
     * @param {IRenderContext} context
     * @param edge
     * @return {SvgVisual}
     */
    createVisual(context, edge) {
        // create the Vue component
        const component = new this.$vueComponentConstructor()
        const cache = this.createRenderDataCache(context, edge)
        // Populate it with the edge data.
        // The properties are reactive, which means the view will be automatically updated by Vue.js when the data
        // changes.
        component.$props.tag = edge.tag
        component.$data.zoom = context.zoom
        component.$props.cache = cache

        // mount the component without passing in a DOM element
        component.$mount()

        const svgElement = component.$el

        // set the location
        // SvgVisual.setTranslate(svgElement, edge.sourcePort.location.x, edge.sourcePort.location.y);

        // save the component instance with the DOM element so we can retrieve it later
        svgElement['data-renderDataCache'] = component

        // return an SvgVisual that uses the DOM element of the component
        const svgVisual = new SvgVisual(svgElement)
        context.setDisposeCallback(svgVisual, (context, visual) => {
            // clean up vue component instance after the visual is disposed
            visual.svgElement['data-renderDataCache'].$destroy()
        })
        return svgVisual
    }

    /**
     * Updates the visual by returning the old visual, as Vuejs handles updating the component.
     * @see Overrides {@link LabelStyleBase#updateVisual}
     * @param {IRenderContext} context
     * @param {Visual} oldVisual
     * @param edge
     * @return {SvgVisual}
     */
    updateVisual(context, oldVisual, edge) {
        const svgElement = oldVisual.svgElement

        const cache = this.createRenderDataCache(context, edge)
        // Update the location
        // SvgVisual.setTranslate(svgElement, node.layout.x, node.layout.y)
        // the zoom property is a primitive value, so we must update it manually on the component
        svgElement['data-renderDataCache'].$data.zoom = context.zoom
        svgElement['data-renderDataCache'].$props.cache = cache
        // set the focused property of each component
        svgElement['data-renderDataCache'].$data.focused =
            context.canvasComponent.focusIndicatorManager.focusedItem === edge
        return oldVisual
    }


    /**
     * Creates an object containing all necessary data to create an edge visual.
     * @return {object}
     */
    createRenderDataCache (context, edge) {
        // Get the owner node's color
        const node = edge.sourcePort.owner
        let color
        const nodeStyle = node.style
        if (typeof nodeStyle.getNodeColor === 'function') {
            color = nodeStyle.getNodeColor(node)
        } else if (
            typeof nodeStyle.wrapper !== 'undefined' &&
            typeof nodeStyle.wrapper.getNodeColor === 'function'
        ) {
            color = nodeStyle.wrapper.getNodeColor(node)
        } else {
            color = 'black'
        }

        const selection = context.canvasComponent !== null ? context.canvasComponent.selection : null
        const selected = selection !== null && selection.isSelected(edge)
        return {
            thickness: this.pathThickness,
            selected,
            color,
            path: this.getPath(edge),
            arrows: this.arrows,
            equals (other) {
                return this.pathEquals(other) && this.stateEquals(other)
            },
            stateEquals (other) {
                return (
                    other.thickness === this.thickness &&
                    other.selected === this.selected &&
                    other.color === this.color &&
                    this.arrows.equals(other.arrows)
                )
            },
            pathEquals (other) {
                return other.path.hasSameValue(this.path)
            }
        }
    }
}
