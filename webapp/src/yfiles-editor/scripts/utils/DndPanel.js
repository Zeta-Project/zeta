
import {
  GraphComponent,
  IEdge,
  ILabel,
  IListEnumerable,
  INode,
  Insets,
  IPort,
  IStripe,
  ListEnumerable,
  Point,
  Rect,
  SimpleNode,
  SvgExport,
  VoidNodeStyle
} from 'yfiles'

/**
 * A palette of sample nodes. Users can drag and drop the nodes from this palette to a graph control.
 */
export class DragAndDropPanel {
  /**
   * Create a new style panel in the given element.
   * @param {HTMLElement} div The element that will display the palette items.
   * @param {boolean} passiveSupported Whether or not the browser supports active and passive event listeners.
   */
  constructor(div, passiveSupported) {
    this.divField = div
    this.$maxItemWidth = 150
    this.passiveSupported = !!passiveSupported
    this.$copyNodeLabels = true
  }

  /**
   * The main element of this panel.
   */
  get div() {
    return this.divField
  }

  set div(div) {
    this.divField = div
  }

  /**
   * The desired maximum width of each item. This value is used to decide whether or not a
   * visualization must be scaled down.
   */
  get maxItemWidth() {
    return this.$maxItemWidth
  }

  set maxItemWidth(width) {
    this.$maxItemWidth = width
  }

  /**
   * A callback that is called then the user presses the mouse button on an item.
   * It should start the actual drag and drop operation.
   */
  get beginDragCallback() {
    return this.$beginDragCallback
  }

  set beginDragCallback(callback) {
    this.$beginDragCallback = callback
  }

  /**
   * Whether the labels of the DnD node visual should be transferred to the created node or discarded.
   * @returns {Boolean}
   */
  get copyNodeLabels() {
    return this.$copyNodeLabels
  }

  set copyNodeLabels(value) {
    this.$copyNodeLabels = value
  }

  /**
   * Adds the items provided by the given factory to this palette.
   * This method delegates the creation of the visualization of each node
   * to createNodeVisual.
   */
  populatePanel(itemFactory) {
    if (!itemFactory) {
      return
    }

    // Create the nodes that specify the visualizations for the panel.
    const items = itemFactory

    // Convert the nodes into plain visualizations
    const graphComponent = new GraphComponent()
    for (let i = 0; i < items.length; i++) {
      const item = items[i]
      const modelItem = INode.isInstance(item) || IEdge.isInstance(item) ? item : item.element
      const visual = INode.isInstance(modelItem)
        ? this.createNodeVisual(item, graphComponent)
        : this.createEdgeVisual(item, graphComponent)
      this.addPointerDownListener(modelItem, visual, this.beginDragCallback)
      this.div.appendChild(visual)
    }
  }

  /**
   * Creates an element that contains the visualization of the given node.
   * This method is used by populatePanel to create the visualization
   * for each node provided by the factory.
   * @return {HTMLDivElement}
   */
  createNodeVisual(original, graphComponent) {
    const graph = graphComponent.graph
    graph.clear()

    const originalNode = INode.isInstance(original) ? original : original.element
    const node = graph.createNode(
      new Rect(0,0,100,50),
      // enable instead of new Rect to show the whole Node
      //originalNode.layout.toRect(),
      originalNode.style,
      originalNode.tag
    )
    originalNode.labels.forEach(label => {
      graph.addLabel(
        node,
        label.text,
        label.layoutParameter,
        label.style,
        label.preferredSize,
        label.tag
      )
    })
    originalNode.ports.forEach(port => {
      graph.addPort(node, port.locationParameter, port.style, port.tag)
    })
    this.updateViewport(graphComponent)

    return this.exportAndWrap(graphComponent, original.tooltip)
  }

  /**
   * Creates an element that contains the visualization of the given edge.
   * @return {HTMLDivElement}
   */
  createEdgeVisual(original, graphComponent) {
    const graph = graphComponent.graph
    graph.clear()

    const originalEdge = IEdge.isInstance(original) ? original : original.element

    const n1 = graph.createNode(new Rect(0, 10, 0, 0), VoidNodeStyle.INSTANCE)
    const n2 = graph.createNode(new Rect(50, 40, 0, 0), VoidNodeStyle.INSTANCE)
    const edge = graph.createEdge(n1, n2, originalEdge.style)
    graph.addBend(edge, new Point(25, 10))
    graph.addBend(edge, new Point(25, 40))

    this.updateViewport(graphComponent)

    // provide some more insets to account for the arrow heads
    graphComponent.updateContentRect(new Insets(5))

    return this.exportAndWrap(graphComponent, original.tooltip)
  }

  updateViewport(graphComponent) {
    const graph = graphComponent.graph
    let viewport = Rect.EMPTY
    graph.nodes.forEach(node => {
      viewport = Rect.add(viewport, node.layout.toRect())
      node.labels.forEach(label => {
        viewport = Rect.add(viewport, label.layout.bounds)
      })
    })
    graph.edges.forEach(edge => {
      viewport = viewport.add(edge.sourcePort.location)
      viewport = viewport.add(edge.targetPort.location)
      edge.bends.forEach(bend => {
        viewport = viewport.add(bend.location.toPoint())
      })
    })
    viewport = viewport.getEnlarged(5)
    graphComponent.contentRect = viewport
    graphComponent.zoomTo(viewport)
  }

  /**
   * Exports and wraps the original visualization in an HTML element.
   * @return {HTMLDivElement}
   */
  exportAndWrap(graphComponent, tooltip) {
    const exporter = new SvgExport(graphComponent.contentRect)
    exporter.margins = new Insets(5)

    exporter.scale = exporter.calculateScaleForWidth(
      Math.min(this.maxItemWidth, graphComponent.contentRect.width)
    )
    const visual = exporter.exportSvg(graphComponent)

    // Firefox does not display the SVG correctly because of the clip - so we remove it.
    visual.removeAttribute('clip-path')

    const div = document.createElement('div')
    div.setAttribute('class', 'dndPanelItem')
    div.appendChild(visual)
    div.style.setProperty('width', visual.getAttribute('width'), '')
    div.style.setProperty('height', visual.getAttribute('height'), '')
    div.style.setProperty('touch-action', 'none')
    try {
      div.style.setProperty('cursor', 'grab', '')
    } catch (e) {
      /* IE9 doesn't support grab cursor */
    }
    if (tooltip) {
      div.title = tooltip
    }
    return div
  }

  /**
   * Adds a mousedown listener to the given element that starts the drag operation.
   */
  addPointerDownListener(item, element, callback) {
    if (!callback) {
      return
    }

    // the actual drag operation
    const doDragOperation = () => {
      if (typeof IStripe !== 'undefined' && IStripe.isInstance(item.tag)) {
        // If the dummy node has a stripe as its tag, we use the stripe directly
        // This allows StripeDropInputMode to take over
        callback(element, item.tag)
      } else if (ILabel.isInstance(item.tag) || IPort.isInstance(item.tag)) {
        callback(element, item.tag)
      } else if (IEdge.isInstance(item)) {
        callback(element, item)
      } else {
        // Otherwise, we just use the node itself and let (hopefully) NodeDropInputMode take over
        const simpleNode = new SimpleNode()
        simpleNode.layout = item.layout
        simpleNode.style = item.style.clone()
        simpleNode.tag = item.tag
        simpleNode.labels = this.$copyNodeLabels ? item.labels : IListEnumerable.EMPTY
        if (item.ports.size > 0) {
          simpleNode.ports = new ListEnumerable(item.ports)
        }
        callback(element, simpleNode)
      }
    }

    element.addEventListener(
      'mousedown',
      evt => {
        if (evt.button !== 0) {
          return
        }
        doDragOperation()
        evt.preventDefault()
      },
      false
    )

    const touchStartListener = evt => {
      doDragOperation()
      evt.preventDefault()
    }

    if (window.PointerEvent !== undefined) {
      element.addEventListener(
        'pointerdown',
        evt => {
          if (evt.pointerType === 'touch' || evt.pointerType === 'pen') {
            touchStartListener(evt)
          }
        },
        true
      )
    } else if (window.MSPointerEvent !== undefined) {
      element.addEventListener(
        'MSPointerDown',
        evt => {
          if (
            evt.pointerType === evt.MSPOINTER_TYPE_TOUCH ||
            evt.pointerType === evt.MSPOINTER_TYPE_PEN
          ) {
            touchStartListener(evt)
          }
        },
        true
      )
    } else {
      element.addEventListener(
        'touchstart',
        touchStartListener,
        this.passiveSupported ? { passive: false } : false
      )
    }
  }
}
