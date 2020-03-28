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
import {
  DefaultLabelStyle,
  Fill, Font, FontStyle, HorizontalTextAlignment,
  INode,
  InteriorStretchLabelModel,
  InteriorStretchLabelModelPosition,
  IRenderContext,
  NodeStyleBase,
  ShapeNodeStyle,
  SimpleLabel,
  SimpleNode, Size,
  SolidColorFill,
  Stroke,
  SvgVisual,
  VerticalTextAlignment
} from 'yfiles'
import {UMLClassModel} from "../uml/nodes/UMLClassModel";

/**
 * A node style which uses a Vuejs component to display a node.
 */
export default class VuejsNodeStyle extends NodeStyleBase {
  /**
   * Creates a new instance of the UML node style.
   * @param {UMLClassModel?} model The UML data that should be visualization by this style
   * @param {Fill?} fill The background fill of the header sections.
   * @param {Fill?} highlightFill The background fill of the selected entry.
   */
  constructor(vueComponentConstructor, model, fill, highlightFill) {
    super()
    this.$vueComponentConstructor = vueComponentConstructor;
    this.$model = model || new UMLClassModel()
    this.$fill = fill || new SolidColorFill(0x60, 0x7d, 0x8b)
    this.$highlightFill = highlightFill || new SolidColorFill(0xa3, 0xf1, 0xbb)
    this.initializeStyles()
  }

  /**
   * Gets the UML data of this style.
   * @returns {UMLClassModel}
   */
  get model() {
    return this.$model
  }

  /**
   * Sets the UML data for this style.
   * @param {UMLClassModel} model
   */
  set model(model) {
    this.$model = model
  }

  /**
   * Creates a visual that uses a Vuejs component to display a node.
   * @see Overrides {@link LabelStyleBase#createVisual}
   * @param {IRenderContext} context
   * @param {INode} node
   * @return {SvgVisual}
   */
  createVisual(context, node) {
    console.log(context, node)
    // create the Vue component
    const component = new this.$vueComponentConstructor()
    // Populate it with the node data.
    // The properties are reactive, which means the view will be automatically updated by Vue.js when the data
    // changes.
    component.$props.tag = node.tag
    component.$data.zoom = context.zoom
    // mount the component without passing in a DOM element
    component.$mount()

    const svgElement = component.$el

    console.log(node)
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
   * Helper method to initialize the dummy styles and label models that are used to build the UML node style.
   * @private
   */
  initializeStyles() {
    this.dummyNode = new SimpleNode()
    const stroke = new Stroke({
      fill: this.$fill,
      thickness: 2
    })
    stroke.freeze()
    this.dummyNode.style = new ShapeNodeStyle({ stroke })

    this.backgroundStyle = new DefaultLabelStyle({
      backgroundFill: this.$fill
    })

    this.stretchLabelModel = new InteriorStretchLabelModel()

    // initialize the category label visualization
    this.categoryLabel = new SimpleLabel(
        this.dummyNode,
        '',
        this.stretchLabelModel.createParameter(InteriorStretchLabelModelPosition.NORTH)
    )
    this.categoryLabel.style = new DefaultLabelStyle({
      textFill: Fill.WHITE,
      verticalTextAlignment: VerticalTextAlignment.CENTER
    })
    this.categoryLabel.preferredSize = new Size(1, 20)

    // initialize the element label visualization
    this.elementLabel = new SimpleLabel(
        this.dummyNode,
        '',
        this.stretchLabelModel.createParameter(InteriorStretchLabelModelPosition.NORTH)
    )
    this.elementLabel.style = new DefaultLabelStyle({
      verticalTextAlignment: VerticalTextAlignment.CENTER
    })
    this.elementLabel.preferredSize = new Size(1, 16)

    // initialize the class label visualization
    this.classLabel = new SimpleLabel(
        this.dummyNode,
        '',
        this.stretchLabelModel.createParameter(InteriorStretchLabelModelPosition.NORTH)
    )
    this.classLabel.style = new DefaultLabelStyle({
      textFill: Fill.WHITE,
      horizontalTextAlignment: HorizontalTextAlignment.CENTER,
      verticalTextAlignment: VerticalTextAlignment.CENTER
    })
    this.classLabel.preferredSize = new Size(1, 50)

    // initialize the stereotype label visualization
    this.stereotypeLabel = new SimpleLabel(
        this.dummyNode,
        '',
        this.stretchLabelModel.createParameter(InteriorStretchLabelModelPosition.NORTH)
    )
    this.stereotypeLabel.style = new DefaultLabelStyle({
      textFill: Fill.WHITE,
      horizontalTextAlignment: HorizontalTextAlignment.CENTER,
      font: new Font({
        fontStyle: FontStyle.ITALIC,
        fontSize: 10
      })
    })

    // initialize the constraint label visualization
    this.constraintLabel = new SimpleLabel(
        this.dummyNode,
        '',
        this.stretchLabelModel.createParameter(InteriorStretchLabelModelPosition.NORTH)
    )
    this.constraintLabel.style = new DefaultLabelStyle({
      textFill: Fill.WHITE,
      horizontalTextAlignment: HorizontalTextAlignment.CENTER,
      verticalTextAlignment: VerticalTextAlignment.BOTTOM,
      font: new Font({
        fontStyle: FontStyle.ITALIC,
        fontSize: 10
      })
    })
  }
}
