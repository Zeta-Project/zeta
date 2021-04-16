import {
  Animator,
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
  ShapeNodeStyle,
  SvgExport,
  SvgVisual,
  TextRenderSupport,
  TimeSpan,
  VoidNodeStyle
} from 'yfiles' //from 'yfiles/typings/yfiles-api-npm'
import {
  createAggregationStyle,
  createAssociationStyle,
  createCompositionStyle,
  createDirectedAssociationStyle,
  createGeneralizationStyle
} from '../edges/styles/UMLEdgeStyleFactory.js'

/**
 * Provides the visuals of the edge creation buttons.
 */
export default class ButtonVisualCreator extends BaseClass(IVisualCreator) {
  /**
   * The provided edge creation buttons.
   * @returns {IEdgeStyle[]}
   */
  static get edgeCreationButtons() {
    return [
      // createRealizationStyle(),
      createGeneralizationStyle(),
      createCompositionStyle(),
      createAggregationStyle(),
      // createDependencyStyle(),
      // createDirectedAssociationStyle(),
      createAssociationStyle()
    ]
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
      childg1.appendChild(child)
      childg2.appendChild(childg1)
      childg3.appendChild(childg2)
      container.appendChild(childg3)
      first += step
    }
    const layout = this.node.layout
    const topRight = layout.topRight
    SvgVisual.setTranslate(container, topRight.x, topRight.y)

    // add interface/abstract toggle buttons
    const interfaceButton = this.renderer.renderTextButton('I')
    SvgVisual.setTranslate(interfaceButton, layout.x - topRight.x, layout.y - topRight.y - 25)
    const abstractButton = this.renderer.renderTextButton('A')
    SvgVisual.setTranslate(abstractButton, layout.x - topRight.x + 25, layout.y - topRight.y - 25)
    container.appendChild(interfaceButton)
    container.appendChild(abstractButton)

    // visualize the button state
    if (this.node.tag.stereotype === 'interface') {
      interfaceButton.setAttribute('class', 'interface-toggle toggled')
    }
    if (this.node.tag.constraint === 'abstract') {
      abstractButton.setAttribute('class', 'abstract-toggle toggled')
    }

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

    // maybe update the toggle buttons
    const interfaceButton = svgElement.childNodes[svgElement.childNodes.length - 2]
    const abstractButton = svgElement.childNodes[svgElement.childNodes.length - 1]
    if (!interfaceButton || !abstractButton) {
      this.createVisual(ctx)
    }

    if (cache.width !== layout.width || cache.height !== layout.height) {
      SvgVisual.setTranslate(interfaceButton, layout.x - topRight.x, layout.y - topRight.y - 25)
      SvgVisual.setTranslate(abstractButton, layout.x - topRight.x + 25, layout.y - topRight.y - 25)
      cache.width = layout.width
      cache.height = layout.height
    }

    // update the button state if they have changed
    if (cache.interfaceToggle !== this.node.tag.stereotype) {
      interfaceButton.setAttribute(
        'class',
        this.node.style.model.stereotype.length > 0
          ? 'interface-toggle toggled'
          : 'interface-toggle'
      )
      cache.interfaceToggle = this.node.style.model.stereotype
    }
    if (cache.constraintToggle !== this.node.tag.constraint) {
      abstractButton.setAttribute(
        'class',
          this.node.tag.constraint.length > 0 ? 'abstract-toggle toggled' : 'abstract-toggle'
      )
      cache.constraintToggle = this.node.tag.constraint
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

  /**
   * Helper method to get the context button at the given location.
   * @param {INode} node The node who should be checked for a button.
   * @param {IPoint} location The world location to check for a button.
   * @returns {string|object} The context button at the given or null.
   */
  static getContextButtonAt(node, location) {
    const layout = node.layout
    if (
      location.x >= layout.x &&
      location.x <= layout.x + 20 &&
      location.y <= layout.y - 5 &&
      location.y >= layout.y - 25
    ) {
      return 'interface'
    }
    if (
      location.x >= layout.x + 25 &&
      location.x <= layout.x + 45 &&
      location.y <= layout.y - 5 &&
      location.y >= layout.y - 25
    ) {
      return 'abstract'
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

  cleanUp() {}

  initialize() {}
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
    container.setAttribute('class', text === 'I' ? 'interface-toggle' : 'abstract-toggle')
    return container
  }
}