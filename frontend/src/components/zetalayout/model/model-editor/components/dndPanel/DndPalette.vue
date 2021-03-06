<template>
  <div>
    <v-subheader>{{ title }}</v-subheader>
    <div :id="'drag-and-drop-panel-' + title"/>
  </div>
</template>

<script>
import {
  DefaultLabelStyle,
  DragDropEffects,
  DragDropItem,
  DragSource,
  Font,
  FontStyle,
  FontWeight,
  FreeNodeLabelModel,
  GraphComponent,
  IEdge,
  ILabel,
  INode,
  Insets,
  IPort,
  IStripe,
  LabelDropInputMode,
  ListEnumerable,
  NodeDropInputMode,
  Point,
  PortDropInputMode,
  Rect,
  ShapeNodeShape,
  ShapeNodeStyle,
  SimpleNode,
  SolidColorFill,
  SvgExport,
  VoidNodeStyle
} from "yfiles";
import {ModelClassModel} from "@/components/zetalayout/model/model-editor/model/nodes/ModelClassModel";
import {addClass, removeClass} from "@/components/zetalayout/model/model-editor/utils/Bindings";
import {Attribute} from "../../model/attributes/Attribute";
import {Method} from "../../model/methods/Method";

export default {
  name: "DndPalette",
  data: function () {
    return {
      maxItemWidth: 150,
    }
  },
  props: {
    graphComponent: {
      type: Object,
      required: true,
    },
    references: {
      type: Array,
      required: true,
    },
    nodes: {
      type: Array,
      required: true,
    },
    title: {
      type: String,
      default: function () {
        return 'Category Name N/A'
      }
    },
    passiveSupported: {
      type: Boolean,
      required: true
    },
    concept: {
      type: Object,
      required: true
    }
  },
  mounted() {
    let divElement = document.querySelector('#drag-and-drop-panel-' + this.title);
    let panelItems = this.getPanelItems(this.nodes);
    // Append svg images as draggable nodes to the drag-and-drop-panel
    this.appendVisuals(panelItems, divElement)
  },
  methods: {
    getPanelItems(nodes) {
      const graphComponent = new GraphComponent()
      const graph = graphComponent.graph;

      let numberOfNodes = nodes.length

      for (let i = 0; i < numberOfNodes; i++) {
        let conceptElement = nodes[i].conceptElement
        let geoElements = nodes[i].geoElements
        let name = nodes[i].name;

        for (let j = 0; j < geoElements.length; j++) {
          let shapeNode = geoElements[j]

          if (typeof shapeNode.size !== 'undefined') {
            //const NodeConstructor = Vue.extend(NodeExample)
            let conceptClass = this.concept.classes.find(c => c.name === conceptElement);

            const node = graph.createNode({
              layout: new Rect(0, 0, shapeNode.size.width, shapeNode.size.height),
              style: new ShapeNodeStyle({
                shape: this.selectedShape(shapeNode),
                fill: shapeNode.style.background.color.hex,
                stroke: shapeNode.style.line.color.hex
              }),
              tag: new ModelClassModel({
                attributes: conceptClass.attributes.map(a => new Attribute(a)),
                methods: conceptClass.methods.map(m => new Method(m)),
                name: conceptClass.name,
                description: conceptClass.description,
                abstractness: conceptClass.abstractness,
                className: name,
                sizeInfo: { resizing: nodes[i].resizing, size: nodes[i].size }
              })
            })

            // Node labels (defined in shape-DSL)
            shapeNode.childGeoElements.forEach(element => {
              // Currently, only textfield is supported
              if (element.type !== 'textfield') {
                return
              }

              let labelAlignX = 0;
              let labelAlignY = 0;

              if (element.align !== undefined) {
                switch (element.align.horizontal) {
                  case "left":
                    labelAlignX = 0;
                    break;
                  case "middle":
                    labelAlignX = 0.5;
                    break;
                  case "right":
                    labelAlignX = 1;
                    break;
                }

                switch (element.align.vertical) {
                  case "top":
                    labelAlignY = 0;
                    break;
                  case "middle":
                    labelAlignY = 0.5;
                    break;
                  case "bottom":
                    labelAlignY = 1;
                    break;
                }
              }

              const labelModel = new FreeNodeLabelModel().createParameter(
                  new Point(labelAlignX, labelAlignY),
                  new Point(element.position.x, element.position.y),
                  new Point(0, 0)
              );

              const style = element.style;

              const font = new Font({
                fontFamily: style.font.name,
                fontSize: style.font.size
              });

              if (style.font.bold)
                font.fontWeight = FontWeight.BOLD;
              if (style.font.italic)
                font.fontStyle = FontStyle.ITALIC;

              const fontColor = new SolidColorFill(
                  style.font.color.r,
                  style.font.color.g,
                  style.font.color.b,
                  style.font.color.a * 255    // Y-Files uses 255 as max alpha, we're using 1
              )

              const backgroundColor = new SolidColorFill(
                  style.background.color.r,
                  style.background.color.g,
                  style.background.color.b,
                  style.background.color.a * 255  // Y-Files uses 255 as max alpha, we're using 1
              );

              const labelStyle = new DefaultLabelStyle({
                maximumSize: element.size,
                font: font,
                textFill: fontColor,
                backgroundFill: backgroundColor,
                wrapping: "word"
              });

              if (element.textBody === "") {
                element.textBody = element.identifier;
              }

              graph.addLabel(
                  node,
                  element.textBody,
                  labelModel,
                  labelStyle,
                  null,
                  element.identifier
              );
            })
          }
        }
      }
      return this.createNodeList(graph)
    },
    selectedShape(shapeNode) {
      switch (shapeNode.type) {
        case "rectangle":
          return ShapeNodeShape.RECTANGLE;

        case "roundedRectangle":
          return ShapeNodeShape.ROUND_RECTANGLE;

        case "ellipse":
          return ShapeNodeShape.ELLIPSE;

        case "triangle":
          return ShapeNodeShape.TRIANGLE;

        case "shearedRectangle":
          return ShapeNodeShape.SHEARED_RECTANGLE;

        case "trapez":
          return ShapeNodeShape.TRAPEZ;

        case "star5":
          return ShapeNodeShape.STAR5;

        case "star6":
          return ShapeNodeShape.STAR6;

        case "star8":
          return ShapeNodeShape.STAR8;

        case "diamond":
          return ShapeNodeShape.DIAMOND;

        case "octagon":
          return ShapeNodeShape.OCTAGON;

        case "hexagon":
          return ShapeNodeShape.HEXAGON;

        default:
          return undefined
      }
    },
    createNodeList(graph) {
      const nodeList = []

      graph.nodes.forEach(node => {
        nodeList.push({element: node, tooltip: node.$f.className})
      });

      return nodeList
    },

    appendVisuals(itemFactory, divElement) {
      if (!itemFactory) {
        return
      }

      // Create the nodes that specify the visualizations for the panel.
      const items = itemFactory;

      // Convert the nodes into plain visualizations
      const graphComponent = new GraphComponent()

      items.forEach(item => {
        const modelItem = INode.isInstance(item) || IEdge.isInstance(item) ? item : item.element
        const visual = INode.isInstance(modelItem)
            ? this.createNodeVisual(item, graphComponent)
            : this.createEdgeVisual(item, graphComponent)
        this.addPointerDownListener(modelItem, visual, this.beginDragCallback)
        divElement.appendChild(visual)
      });
    },
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

      // Create nodes that can be appended to the graph by the builder
      const node = graph.createNode({
        style: originalNode.style,
        tag: new ModelClassModel()
      })

      this.updateViewport(graphComponent)

      return this.exportAndWrap(graphComponent, original.tooltip)
    },

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
    },

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
    },

    /**
     * Exports and wraps the original visualization in an HTML element.
     * @return
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
    },

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
          simpleNode.layout = item.layout;
          simpleNode.style = item.style.clone()
          simpleNode.tag = item.tag.clone();
          simpleNode.labels = item.labels
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
            this.passiveSupported ? {passive: false} : false
        )
      }
    },
    beginDragCallback(element, data) {
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
    },

  }
}
</script>

<style scoped>

</style>