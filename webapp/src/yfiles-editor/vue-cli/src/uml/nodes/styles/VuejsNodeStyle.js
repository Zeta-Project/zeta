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
import {Fill, INode, IRenderContext, NodeStyleBase, Rect, Size, SvgVisual} from 'yfiles'


/**
 * A node style which uses a Vuejs component to display a node.
 */
export default class VuejsNodeStyle extends NodeStyleBase {
    /**
     * Creates a new instance of the UML node style.
     * @param vueComponentConstructor: constructor of a vue js node
     * @param {Fill?} fill The background fill of the header sections.
     * @param {Fill?} highlightFill The background fill of the selected entry.
     */
    constructor(vueComponentConstructor, methods, fill, highlightFill) {
        super()
        this.$vueComponentConstructor = vueComponentConstructor;
        this.methods = methods;
        //console.log("methods", methods)
        //console.log(this.addAttribute, typeof addAttribute)
    }

    /**
     * Creates a visual that uses a Vuejs component to display a node.
     * @see Overrides {@link LabelStyleBase#createVisual}
     * @param {IRenderContext} context
     * @param {INode} node
     * @return {SvgVisual}
     */
    test(value){
        console.log(value)
    }

    createVisual(context, node) {
        // create the Vue component
        const component = new this.$vueComponentConstructor()
        // Populate it with the node data.
        // The properties are reactive, which means the view will be automatically updated by Vue.js when the data
        // changes.
        component.$props.node = node
        component.$props.tag = node.tag
        //console.log(node.layout)
        component.$props.layout = node.layout
        //console.log("methods in create visual", this.methods)
        //console.log("addAttributeToNode in create visual", this.methods.addAttributeToNode)
        component.$props.methods = this.methods
        //component.$props.tag = node.tag
        component.$data.zoom = context.zoom
        // mount the component without passing in a DOM element
        component.$mount()

        const svgElement = component.$el

        // set the location
        SvgVisual.setTranslate(svgElement, node.layout.x, node.layout.y)

        // save the component instance with the DOM element so we can retrieve it later
        svgElement['data-vueComponent'] = component

        // return an SvgVisual that uses the DOM element of the component
        const svgVisual = new SvgVisual(svgElement)
        context.setDisposeCallback(svgVisual, (context, visual) => {
            // clean up vue component instance after the visual is disposed
            visual.svgElement['data-vueComponent'].$destroy()
        })
        return svgVisual
    }

    /**
     * Updates the visual by returning the old visual, as Vuejs handles updating the component.
     * @see Overrides {@link LabelStyleBase#updateVisual}
     * @param {IRenderContext} context
     * @param {SvgVisual} oldVisual
     * @param {INode} node
     * @return {SvgVisual}
     */
    updateVisual(context, oldVisual, node) {
        const svgElement = oldVisual.svgElement

        // Update the location
        SvgVisual.setTranslate(svgElement, node.layout.x, node.layout.y)
        // the zoom property is a primitive value, so we must update it manually on the component
        svgElement['data-vueComponent'].$data.zoom = context.zoom
        // set the focused property of each component
        svgElement['data-vueComponent'].$data.focused =
            context.canvasComponent.focusIndicatorManager.focusedItem === node
        return oldVisual
    }

    /**
     * Adjusts the size of the given node considering UML data of the node. If the current node layout is bigger than
     * the minimal needed size for the UML data then the current node layout will be used instead.
     * @param {INode} node The node whose size should be adjusted.
     * @param {GraphEditorInputMode} geim The responsible input mode.
     */
    adjustSize(node, geim) {
        const layout = node.layout
        const minSize = this.getPreferredSize(node)
        const width = Math.max(minSize.width, layout.width)
        const height = Math.max(minSize.height, layout.height)
        // GEIM's setNodeLayout handles affected orthogonal edges automatically
        geim.setNodeLayout(node, new Rect(layout.x, layout.y, width, height))
        geim.graphComponent.invalidate()
    }

    /**
     * Return the size of this node considering the associated UML data.
     * @param {INode} node The node of which the size should be determined.
     * @returns {Size} The preferred size of this node.
     */
    getPreferredSize(node) {
        const data = node.tag
        const entriesCount = Object.keys(data).length

        // determine width
        /*let width = 125
        const elementFont = this.elementLabel.style.font
        const elements = data.attributes.concat(data.operations)
        elements.forEach(element => {
            const size = TextRenderSupport.measureText(element.name, elementFont)
            width = Math.max(width, size.width + LEFT_SPACING + 5)
        })
        const classNameSize = TextRenderSupport.measureText(data.className, this.classLabel.style.font)
        width = Math.max(width, classNameSize.width)
        const stereotypeSize = TextRenderSupport.measureText(
            data.stereotype,
            this.stereotypeLabel.style.font
        )
        width = Math.max(width, stereotypeSize.width)
        const constraintSize = TextRenderSupport.measureText(
            data.className,
            this.constraintLabel.style.font
        )
        width = Math.max(width, constraintSize.width)*/

        return new Size(100, 50)
    }
}
