<template>
    <div v-show="isExpanded">
        <h1 class="demo-sidebar-header">{{title}}</h1>
        <div class="demo-sidebar-content">
            <div id="drag-and-drop-panel"/>
        </div>
    </div>
</template>

<script>
import {
  DefaultLabelStyle,
  DragDropEffects,
  DragDropItem,
  DragSource,
  GraphComponent,
  IEdge,
  ILabel,
  IListEnumerable,
  INode,
  Insets, InteriorLabelModel,
  IPort,
  IStripe,
  LabelDropInputMode,
  ListEnumerable,
  NodeDropInputMode,
  Point,
  PortDropInputMode,
  Rect, ShapeNodeShape, ShapeNodeStyle, SimpleLabel,
  VoidNodeStyle, Font, FontStyle,
  SvgExport,
  SimpleNode, Size
} from "yfiles";
    import {addClass, removeClass} from "../../utils/Bindings";
    import VuejsNodeStyle from "../../uml/nodes/styles/VuejsNodeStyle";
    import Vue from "vue";
    import Node from "../nodes/Node.vue";
    import {UMLClassModel} from "../../uml/nodes/UMLClassModel";

    export default {
        name: 'DndPanel',
        mounted() {
            this.panelItems = this.getPanelItems(this.graphComponent, this.shape, this.diagram, this.styleModel)
            const nodes = document.querySelectorAll('#drag-and-drop-panel')
            this.div = nodes[nodes.length - 1]
            // Append svg images as draggable nodes to the drag-and-drop-panel
            this.appendVisuals(this.panelItems)
        },
        data: function () {
            return {
                panelItems: [],
                maxItemWidth: 150,
                div: ''
            }
        },
        props: {
          graphComponent: {
            type: Object,
            required: true,
          },
          shape: {
            type: Object,
            required: true,
          },
          diagram: {
            type: Object,
            required: true,
          },
          styleModel: {
            type: Object,
            required: true,
          },
          isExpanded: {
            type: Boolean,
            required: true
          },
          passiveSupported: {
            type: Boolean,
            required: true
          },
          title: {
            type: String,
            default: function () {
              return 'Drag and Drop Panel'
            }
          }
        },
        methods: {
            /**
             * Return panel items
             **/
            getPanelItems(graphComponent, shape, diagram, styleModel) {
                /*let methods = {}
                methods.addAttributeToNode = (node, attribute) => this.$emit('add-attribute-to-node', node, attribute);
                methods.addOperationToNode = (node, attribute) => this.$emit('add-operation-to-node', node, attribute);
                methods.deleteAttributeFromNode = (node, attribute) => this.$emit('delete-attribute-from-node', node, attribute);
                methods.deleteOperationFromNode = (node, attribute) => this.$emit('delete-operation-from-node', node, attribute);
                methods.changeInputMode = () => this.$emit('change-input-mode');*/
                //const NodeConstructor = Vue.extend(Node);

                // Create node and push them into the itemContainer

                /*const node = new SimpleNode();
                node.layout = new Rect(0, 0, 150, 250);
                // Set the style of the node in the dnd panel
                node.style = new VuejsNodeStyle(NodeConstructor, methods, graphComponent.inputMode);*/

                const nodeList = [] // {element: node, tooltip: 'Node'}

                console.log(graphComponent)
                console.log(diagram)
                console.log(shape)
                console.log(styleModel)

                diagram.diagrams[0].palettes.forEach(diagramKey => {
                  const shapeNodes = shape.nodes.filter(x => {
                    return x.name === diagramKey.nodes[0]
                  })[0]


                  shapeNodes.geoElements.forEach(function(shapeNode) {
                    console.log(shapeNode);

                    const fontStyle = new Font({
                      fontFamily: shapeNode.style.fontFamily,
                      fontSize:shapeNode.style.font.size
                    })

                    const node = new SimpleNode();
                    let simpleLabel = new SimpleLabel()
                    simpleLabel.owner = node
                    simpleLabel.layoutParameter = InteriorLabelModel.CENTER
                    simpleLabel.style = new DefaultLabelStyle({
                      font: fontStyle,
                      verticalTextAlignment: "center",
                      horizontalTextAlignment: "center"
                    })

                    if(typeof shapeNode.childGeoElements[0] !== 'undefined') {
                      simpleLabel.text = shapeNode.childGeoElements[0].identifier
                    }

                    switch (type) {
                      case "rectangle":
                        node.layout = new Rect(0, 0, shapeNode.size.width, shapeNode.size.height);
                        node.style = new ShapeNodeStyle({shape: ShapeNodeShape.RECTANGLE, fill: shapeNode.style.background.color.hex, stroke: shapeNode.style.line.color.hex})
                        node.labels = new ListEnumerable([simpleLabel])

                        nodeList.push({element: node, tooltip: diagramKey.name})
                        break;
                      case "roundedRectangle":
                        node.layout = new Rect(0, 0, shapeNode.size.width, shapeNode.size.height);
                        node.style = new ShapeNodeStyle({shape: ShapeNodeShape.ROUND_RECTANGLE, fill: shapeNode.style.background.color.hex, stroke: shapeNode.style.line.color.hex})
                        node.labels = new ListEnumerable([simpleLabel])

                        nodeList.push({element: node, tooltip: diagramKey.name})
                        break;
                      case "ellipse":
                        node.layout = new Rect(0, 0, shapeNode.size.width, shapeNode.size.height);
                        node.style = new ShapeNodeStyle({shape: ShapeNodeShape.ELLIPSE, fill: shapeNode.style.background.color.hex, stroke: shapeNode.style.line.color.hex})
                        node.labels = new ListEnumerable([simpleLabel])

                        nodeList.push({element: node, tooltip: diagramKey.name})
                        break;
                      default:
                        console.log("Not implemented yet: " + type);
                    }
                  })
                })

                return nodeList
            },

            /**
             * Adds the items provided by the given factory to this palette.
             * This method delegates the creation of the visualization of each node
             * to createNodeVisual.
             */
            appendVisuals(itemFactory) {
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
                    this.div.appendChild(visual)
                });
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
                    tag: new UMLClassModel()
                })
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
                        this.passiveSupported ? {passive: false} : false
                    )
                }
            }
        }
    }
</script>

<style scoped>
    .demo-sidebar-header {
        color: #666666;
        font-size: 1.8em;
        height: 60px;
        line-height: 60px;
        margin: 0;
        box-sizing: border-box;
        padding-left: 10px;
    }

    .demo-sidebar-content {
        overflow-y: auto;
        height: calc(100% - 70px);
        padding: 0 25px;
    }

    .demo-sidebar-content h1,
    .demo-sidebar-content h2 {
        color: #666666;
    }

    .demo-sidebar-content a,
    .demo-sidebar-content a:visited {
        text-decoration: none;
        color: #1871bd;
    }

    .demo-sidebar-content a:hover {
        text-decoration: none;
        color: #18468c;
    }

    .demo-sidebar-content ul {
        padding-left: 1.3em;
    }

    .demo-sidebar-content li {
        margin: 0.5em 0;
    }
</style>
