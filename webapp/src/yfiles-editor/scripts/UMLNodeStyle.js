
import {
    Class,
    DefaultLabelStyle,
    Fill,
    Font,
    FontStyle,
    GraphEditorInputMode,
    HorizontalTextAlignment,
    IClipboardHelper,
    IEditLabelHelper,
    INode,
    INodeSizeConstraintProvider,
    IRenderContext,
    Insets,
    InteriorStretchLabelModel,
    InteriorStretchLabelModelPosition,
    ItemClickedEventArgs,
    LabelEditingEventArgs,
    MarkupExtension,
    NodeStyleBase,
    Point,
    Rect,
    ShapeNodeStyle,
    SimpleLabel,
    SimpleNode,
    Size,
    SolidColorFill,
    Stroke,
    SvgVisual,
    TextRenderSupport,
    TypeAttribute,
    VerticalTextAlignment,
    Visual,
    YObject
} from 'yfiles'

import { UMLClassModel } from './UMLClassModel.js'
// additional spacing after certain elements
const VERTICAL_SPACING = 2

// empty space before the text elements
const LEFT_SPACING = 25

/**
 * An UML node style that visualizes an UMLClassModel.
 */
export class UMLNodeStyle extends NodeStyleBase {
    /**
     * Returns the background fill.
     * @return {Fill}
     */
    get fill() {
        return this.$fill
    }

    /**
     * Sets the fill for the background.
     * @param {Fill} value
     */
    set fill(value) {
        this.$fill = value
    }

    /**
     * Returns the highlight background fill.
     * @return {Fill}
     */
    get highlightFill() {
        return this.$highlightFill
    }

    /**
     * Sets the fill for the highlight background.
     * @param {Fill} value
     */
    set highlightFill(value) {
        this.$highlightFill = value
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
     * Creates a new instance of the UML node style.
     * @param {UMLClassModel?} model The UML data that should be visualization by this style
     * @param {Fill?} fill The background fill of the header sections.
     * @param {Fill?} highlightFill The background fill of the selected entry.
     */
    constructor(model, fill, highlightFill) {
        super()
        this.$model = model || new UMLClassModel()
        this.$fill = fill || new SolidColorFill(0x60, 0x7d, 0x8b)
        this.$highlightFill = highlightFill || new SolidColorFill(0xa3, 0xf1, 0xbb)
        this.initializeStyles()
    }

    /**
     * Creates the UML node style visual based on the UMLClassModel.
     * @param {IRenderContext} ctx The render context.
     * @param {INode} node The node to which this style instance is assigned.
     * @returns {Visual}
     */
    createVisual(ctx, node) {
        this.initializeStyles() // fill color might have changed
        const data = this.$model
        const layout = node.layout
        const g = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        g.setAttribute('class', 'uml-node')
        this.dummyNode.layout = new Rect(0, 0, layout.width, layout.height)
        g.appendChild(UMLNodeStyle.getCreator(this.dummyNode).createVisual(ctx).svgElement)

        let yOffset = 0

        // add the class label
        this.stretchLabelModel.insets = new Insets(0, yOffset, 0, 0)
        this.classLabel.text = ''
        g.appendChild(
            UMLNodeStyle.getCreator(this.classLabel, this.backgroundStyle).createVisual(ctx).svgElement
        )
        this.classLabel.text = data.className
        g.appendChild(UMLNodeStyle.getCreator(this.classLabel).createVisual(ctx).svgElement)

        // add stereotype
        if (data.stereotype) {
            this.stretchLabelModel.insets = new Insets(0, 5, 0, 0)
            this.stereotypeLabel.text = `<<${data.stereotype}>>`
            g.appendChild(UMLNodeStyle.getCreator(this.stereotypeLabel).createVisual(ctx).svgElement)
        }

        // add constraint
        if (data.constraint) {
            this.stretchLabelModel.insets = new Insets(0, 5, 0, 0)
            this.constraintLabel.text = `{${data.constraint}}`
            g.appendChild(UMLNodeStyle.getCreator(this.constraintLabel).createVisual(ctx).svgElement)
        }

        yOffset += this.classLabel.preferredSize.height

        let selectedIndex = -1
        if (ctx.canvasComponent.currentItem === node && data.selectedIndex >= 0) {
            selectedIndex = data.selectedIndex
        }

        yOffset += VERTICAL_SPACING
        this.stretchLabelModel.insets = new Insets(0, yOffset, 0, 0)
        this.categoryLabel.text = ''
        const attributesHeaderBackground = UMLNodeStyle.getCreator(
            this.categoryLabel,
            this.backgroundStyle
        ).createVisual(ctx).svgElement
        attributesHeaderBackground.setAttribute('cursor', 'pointer')
        g.appendChild(attributesHeaderBackground)
        this.categoryLabel.text = 'Attributes'
        this.stretchLabelModel.insets = new Insets(LEFT_SPACING, yOffset, 0, 0)
        const attributesTextElement = UMLNodeStyle.getCreator(this.categoryLabel).createVisual(ctx)
            .svgElement
        attributesTextElement.setAttribute('cursor', 'pointer')
        g.appendChild(attributesTextElement)

        yOffset += this.categoryLabel.preferredSize.height
        const attributesHeaderOffset = yOffset

        let counter = 0
        let hasLocalSelection = false
        let hasGlobalSelection = false
        if (data.attributesOpen) {
            for (let i = 0; i < data.attributes.length; i++, counter++) {
                if (counter === selectedIndex) {
                    hasGlobalSelection = true
                    hasLocalSelection = true
                    this.elementLabel.text = ''
                    this.elementLabel.style.backgroundFill = this.$highlightFill
                    this.stretchLabelModel.insets = new Insets(1, yOffset, 1, 0)
                    g.appendChild(UMLNodeStyle.getCreator(this.elementLabel).createVisual(ctx).svgElement)
                }
                if (data.attributes[i] !== null && typeof data.attributes[i] !== 'undefined') {
                    this.elementLabel.text = data.attributes[i]
                    this.elementLabel.style.backgroundFill = null
                    this.stretchLabelModel.insets = new Insets(LEFT_SPACING, yOffset, 5, 0)
                    g.appendChild(UMLNodeStyle.getCreator(this.elementLabel).createVisual(ctx).svgElement)
                    yOffset += this.elementLabel.preferredSize.height
                }
            }
        } else {
            counter += data.attributes.length
        }
        if (attributesHeaderOffset <= layout.height) {
            this.addControls(
                ctx,
                attributesHeaderBackground,
                layout,
                data.attributesOpen,
                hasLocalSelection
            )
        }

        this.stretchLabelModel.insets = new Insets(0, yOffset, 0, 0)
        this.categoryLabel.text = ''
        const operationsHeaderBackground = UMLNodeStyle.getCreator(
            this.categoryLabel,
            this.backgroundStyle
        ).createVisual(ctx).svgElement
        operationsHeaderBackground.setAttribute('cursor', 'pointer')
        g.appendChild(operationsHeaderBackground)
        this.categoryLabel.text = 'Operations'
        this.stretchLabelModel.insets = new Insets(LEFT_SPACING, yOffset, 0, 0)
        const operationsTextElement = UMLNodeStyle.getCreator(this.categoryLabel).createVisual(ctx)
            .svgElement
        operationsTextElement.setAttribute('cursor', 'pointer')
        g.appendChild(operationsTextElement)
        yOffset += this.categoryLabel.preferredSize.height
        const operationsHeaderOffset = yOffset

        hasLocalSelection = false
        if (data.operationsOpen) {
            for (let i = 0; i < data.operations.length; i++, counter++) {
                if (counter === selectedIndex) {
                    hasGlobalSelection = true
                    hasLocalSelection = true
                    this.elementLabel.text = ''
                    this.elementLabel.style.backgroundFill = this.$highlightFill
                    this.stretchLabelModel.insets = new Insets(1, yOffset, 1, 0)
                    g.appendChild(UMLNodeStyle.getCreator(this.elementLabel).createVisual(ctx).svgElement)
                }
                if (data.operations[i] !== null && typeof data.operations[i] !== 'undefined') {
                    this.elementLabel.text = data.operations[i]
                    this.elementLabel.style.backgroundFill = null
                    this.stretchLabelModel.insets = new Insets(LEFT_SPACING, yOffset, 5, 0)
                    g.appendChild(UMLNodeStyle.getCreator(this.elementLabel).createVisual(ctx).svgElement)
                    yOffset += this.elementLabel.preferredSize.height
                }
            }
        }
        if (operationsHeaderOffset <= layout.height) {
            this.addControls(
                ctx,
                operationsHeaderBackground,
                layout,
                data.operationsOpen,
                hasLocalSelection
            )
        }

        SvgVisual.setTranslate(g, layout.x, layout.y)

        const svgVisual = new SvgVisual(g)
        svgVisual.data = {
            x: layout.x,
            y: layout.y,
            width: layout.width,
            height: layout.height,
            dataModCount: data.modCount,
            hasSelection: hasGlobalSelection,
            fill: this.$fill,
            highlightFill: this.$highlightFill
        }
        return svgVisual
    }

    /**
     * Updates the location of the UML node style. If anything other changed, a new visual is created.
     * @param {IRenderContext} ctx The render context.
     * @param {Visual} oldVisual The visual that has been created in the call to
     *   {@link NodeStyleBase#createVisual}.
     * @param {INode} node The node to which this style instance is assigned.
     * @returns {Visual}
     */
    updateVisual(ctx, oldVisual, node) {
        const layout = node.layout

        const data = oldVisual.data
        const model = this.$model
        if (
            !data ||
            data.dataModCount !== model.modCount ||
            (data.width !== layout.width || data.height !== layout.height) ||
            (data.hasSelection && ctx.canvasComponent.currentItem !== node) ||
            data.fill !== node.style.fill ||
            data.highlightFill !== node.style.highlightFill
        ) {
            return this.createVisual(ctx, node)
        }

        if (data.x !== layout.x || data.y !== layout.y) {
            SvgVisual.setTranslate(oldVisual.svgElement, layout.x, layout.y)
            data.x = layout.x
            data.y = layout.y
        }
        return oldVisual
    }

    /**
     * Return the size of this node considering the associated UML data.
     * @param {INode} node The node of which the size should be determined.
     * @returns {Size} The preferred size of this node.
     */
    getPreferredSize(node) {
        const data = this.$model

        // determine height
        let entries = data.attributesOpen ? data.attributes.length : 0
        entries += data.operationsOpen ? data.operations.length : 0
        const height =
            this.classLabel.preferredSize.height +
            VERTICAL_SPACING + // title section
            this.categoryLabel.preferredSize.height * 2 + // both section titles
            this.elementLabel.preferredSize.height * entries // visible entries

        // determine width
        let width = 125
        const elementFont = this.elementLabel.style.font
        const elements = data.attributes.concat(data.operations)
        elements.forEach(element => {
            const size = TextRenderSupport.measureText(element, elementFont)
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
        width = Math.max(width, constraintSize.width)

        return new Size(width, height)
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
     * Adjusts the height of the given node to fit the UML data.
     * @param {INode} node The node whose size should be adjusted.
     * @param {GraphEditorInputMode} geim The responsible input mode.
     * @private
     */
    fitHeight(node, geim) {
        const layout = node.layout
        const newSize = this.getPreferredSize(node)
        // GEIM's setNodeLayout handles affected orthogonal edges automatically
        geim.setNodeLayout(node, new Rect(layout.x, layout.y, layout.width, newSize.height))
        geim.graphComponent.invalidate()
    }

    /**
     * Upon label edit, we check which UML entry was hit and adjust the label edit accordingly. Additionally, we provide
     * an {@link INodeSizeConstraintProvider} to limit the interactive node resizing to the node's size
     * considering the attached UML data.
     * @param {INode} node The node to use for the context lookup.
     * @param {Class} type The type to query.
     * @returns {Object}
     */
    lookup(node, type) {
        const outerThis = this
        if (type === IEditLabelHelper.$class) {
            const oldData = this.$model.clone()
            return new IEditLabelHelper({
                onLabelAdding(evt) {
                    outerThis.editLabel(evt, node, true).then(newData => {
                        outerThis.handleUndo(evt.context.canvasComponent.inputMode, node, newData, oldData)
                    })
                },
                onLabelEditing(evt) {
                    outerThis.editLabel(evt, node, false).then(newData => {
                        outerThis.handleUndo(evt.context.canvasComponent.inputMode, node, newData, oldData)
                    })
                }
            })
        } else if (type === INodeSizeConstraintProvider.$class) {
            return new INodeSizeConstraintProvider({
                getMinimumSize: item => this.getPreferredSize(item),
                getMaximumSize: item => Size.INFINITE,
                getMinimumEnclosedArea: item => Rect.EMPTY
            })
        } else if (type === IClipboardHelper.$class) {
            return new IClipboardHelper({
                shouldCopy: (context, item) => true,
                shouldCut: (context, item) => true,
                shouldPaste: (context, item, userData) => true,
                copy: (context, item) => null,
                cut: (context, item) => null,
                paste: (context, item, userData) => {
                    const style = item.style
                    if (style instanceof UMLNodeStyle) {
                        if (context.targetGraph.foldingView) {
                            context.targetGraph.foldingView.manager.masterGraph.setStyle(item, style.clone())
                        } else {
                            context.targetGraph.setStyle(item, style.clone())
                        }
                    }
                }
            })
        }
        return null
    }

    /**
     * Manages text edits on the {@link UMLNodeStyle} by preconfiguring the {@link TextEditorInputMode}
     * with the text that should be edited and its position such that the label edit text box is opened on top of the
     * clicked text.
     * @param {LabelEditingEventArgs} evt The event args with which the edit label was triggered.
     * @param {INode} node The node whose label should be edited.
     * @param {boolean} adding Whether a new label is added or an existing one should be edited.
     * @return {Promise}
     * @private
     */
    editLabel(evt, node, adding) {
        const data = this.$model
        const index = data.selectedIndex
        const categoryHit = data.selectedCategory
        evt.cancel = true
        evt.handled = true
        const graphComponent = evt.context.canvasComponent
        const editorInputMode = graphComponent.inputMode

        const layout = this.getRelativeSlotLayout(index, node, adding, categoryHit)

        let text = ''
        if (!adding) {
            if (index < 0) {
                text = data.className
            } else if (index >= data.attributes.length) {
                text = data.operations[index - data.attributes.length] || ''
            } else {
                text = data.attributes[index] || ''
            }
        } else {
            // we add a dummy entry to make space for the label editing
            // eslint-disable-next-line no-lonely-if
            if (categoryHit === 1) {
                data.attributes.push('')
                this.adjustSize(node, editorInputMode)
            } else if (categoryHit === 2) {
                data.operations.push('')
                this.adjustSize(node, editorInputMode)
            }
        }

        editorInputMode.textEditorInputMode.editorText = text
        editorInputMode.textEditorInputMode.upVector = new Point(0, -1)
        const leftPadding = index < 0 ? 0 : LEFT_SPACING
        editorInputMode.textEditorInputMode.location = new Point(
            layout.x + node.layout.x + leftPadding,
            layout.y + node.layout.y + layout.height
        )
        editorInputMode.textEditorInputMode.anchor = new Point(0, 1)

        // actually edit the text and update the UML data model
        return editorInputMode.textEditorInputMode.edit().then(res => {
            if (res !== null) {
                if (index < 0) {
                    data.className = res
                } else if (categoryHit === 1) {
                    if (adding) {
                        data.attributes[data.attributes.length - 1] = res
                        data.selectedIndex = data.attributes.length - 1
                    } else {
                        data.attributes[index] = res
                    }
                } else if (categoryHit === 2) {
                    if (adding) {
                        data.operations[data.operations.length - 1] = res
                        data.selectedIndex = data.attributes.length + (data.operations.length - 1)
                    } else {
                        data.operations[index - data.attributes.length] = res
                    }
                }
            } else {
                // canceled, maybe remove the dummy entry
                // eslint-disable-next-line no-lonely-if
                if (adding) {
                    if (categoryHit === 1) {
                        data.attributes.splice(data.attributes.length - 1, 1)
                    } else if (categoryHit === 2) {
                        data.operations.splice(data.operations.length - 1, 1)
                    }
                    this.adjustSize(node, editorInputMode)
                }
            }
            data.modify()
            evt.context.canvasComponent.invalidate()
            return data
        })
    }

    /**
     * Clones this style and the associated UML model.
     * @returns {UMLNodeStyle}
     */
    clone() {
        const clone = super.clone()
        clone.model = clone.model.clone()
        return clone
    }

    /**
     * Handles clicks on this style.
     * @param {GraphEditorInputMode} geim
     * @param {ItemClickedEventArgs} args
     */
    nodeClicked(geim, args) {
        const node = args.item
        const data = this.$model
        const location = args.location
        const x = location.x - node.layout.x
        const y = location.y - node.layout.y

        //Todo decide if hard selection or listener. Current Listener: app.js (selectionChangedListener) -> probably better to use in input mode?!
        geim.graphComponent.selection.selectedNodes.clear()
        geim.graphComponent.selection.selectedNodes.setSelected(args.item, true)

        // the vertical relative coordinates of the different interactive parts
        const topAttributesCategory = this.classLabel.preferredSize.height + VERTICAL_SPACING
        const bottomAttributesCategory = topAttributesCategory + this.categoryLabel.preferredSize.height
        let topOperationsCategory = bottomAttributesCategory
        let bottomOperationsCategory = topOperationsCategory + this.categoryLabel.preferredSize.height
        if (data.attributesOpen) {
            const attributesHeight = this.elementLabel.preferredSize.height * data.attributes.length
            topOperationsCategory += attributesHeight
            bottomOperationsCategory += attributesHeight
        }

        // determine which section or button was clicked
        if (y < topAttributesCategory) {
            if (data.selectedIndex !== -1) {
                data.selectedIndex = -1
            }
            return
        } else if (y >= topAttributesCategory && y <= bottomAttributesCategory) {
            // the attributes header was clicked
            if (x >= 5 && x < node.layout.width - 36) {
                this.toggleOpenState(1, geim, node)
            } else if (x >= node.layout.width - 36 && x <= node.layout.width - 20) {
                this.addLabel(1, geim, node)
            } else if (
                x >= node.layout.width - 18 &&
                x <= node.layout.width - 2 &&
                data.selectedIndex >= 0 &&
                data.selectedIndex <= data.attributes.length - 1
            ) {
                this.removeLabel(1, geim, node)
            } else {
                if (data.selectedIndex !== -1) {
                    data.selectedIndex = -1
                }
                return
            }
        } else if (data.attributesOpen && y > bottomAttributesCategory && y < topOperationsCategory) {
            // an attribute was clicked
            data.selectedCategory = 1
            data.selectedIndex =
                ((y - bottomAttributesCategory) / this.elementLabel.preferredSize.height) | 0
        } else if (y >= topOperationsCategory && y <= bottomOperationsCategory) {
            // the operations header was clicked
            if (x >= 5 && x < node.layout.width - 36) {
                this.toggleOpenState(2, geim, node)
            } else if (x >= node.layout.width - 36 && x <= node.layout.width - 20) {
                this.addLabel(2, geim, node)
            } else if (
                x >= node.layout.width - 18 &&
                x <= node.layout.width - 2 &&
                data.selectedIndex >= data.attributes.length &&
                data.selectedIndex <= data.attributes.length + data.operations.length - 1
            ) {
                this.removeLabel(2, geim, node)
            } else {
                if (data.selectedIndex !== -1) {
                    data.selectedIndex = -1
                }
                return
            }
        } else if (y > bottomOperationsCategory) {
            // an operation was clicked
            data.selectedCategory = 2
            data.selectedIndex =
                (data.attributes.length +
                    (y - (topOperationsCategory + this.categoryLabel.preferredSize.height)) /
                    this.elementLabel.preferredSize.height) |
                0
        } else {
            // a non-interactive part of the style was clicked, just do nothing
            return
        }

        //geim.inputModeContext.canvasComponent.currentItem = node
        geim.inputModeContext.canvasComponent.invalidate()
        args.handled = true
    }

    /**
     * Triggers interactive label adding.
     * @param {number} category 1 represents the attributes section, 2 represents the operations section
     * @param {GraphEditorInputMode} geim
     * @param {INode} node
     * @private
     */
    addLabel(category, geim, node) {
        const data = this.$model
        data.selectedCategory = category
        if (category === 1) {
            data.selectedIndex = Math.max(0, data.attributes.length)
            data.attributesOpen = true
        } else if (category === 2) {
            data.selectedIndex = Math.max(
                data.attributes.length,
                data.attributes.length + data.operations.length
            )
            data.operationsOpen = true
        }
        geim.clickInputMode.preventNextDoubleClick()
        geim.addLabel(node)
    }

    /**
     * Removes the selected label from the node.
     * @param {number} category 1 represents the attributes section, 2 represents the operations section
     * @param {GraphEditorInputMode} geim
     * @param {INode} node
     * @private
     */
    removeLabel(category, geim, node) {
        const data = this.$model
        const oldData = data.clone()
        data.selectedCategory = category
        if (category === 1) {
            data.attributes.splice(data.selectedIndex, 1)
        } else if (category === 2) {
            data.operations.splice(data.selectedIndex - data.attributes.length, 1)
        }
        data.selectedIndex = Math.min(
            data.selectedIndex,
            data.attributes.length + data.operations.length - 1
        )
        data.modify()
        geim.clickInputMode.preventNextDoubleClick()
        this.handleUndo(geim, node, data, oldData)
        this.adjustSize(node, geim)
    }

    /**
     * Toggles the open/closed state of the attributes or operations section.
     * @param {number} category 1 represents the attributes section, 2 represents the operations section
     * @param {GraphEditorInputMode} geim
     * @param {INode} node
     * @private
     */
    toggleOpenState(category, geim, node) {
        const data = this.$model
        if (category === 1) {
            data.attributesOpen = !data.attributesOpen
        } else if (category === 2) {
            data.operationsOpen = !data.operationsOpen
        }
        data.modify()
        this.fitHeight(node, geim)
        geim.clickInputMode.preventNextDoubleClick()
    }

    /**
     * Adds an undo unit to the graphs undo engine which may undo/redo the UML data change.
     * @param {GraphEditorInputMode} geim The responsible input mode.
     * @param {INode} node The node whose data is changed.
     * @param {UMLClassModel} newData The new data.
     * @param {UMLClassModel} oldData The previous data.
     * @private
     */
    handleUndo(geim, node, newData, oldData) {
        const graph = geim.graph
        const edit = graph.beginEdit('AddingLabel', 'AddingLabel')
        graph.addUndoUnit(
            'DataChanged',
            'DataChanged',
            () => {
                this.$model = oldData
                this.adjustSize(node, geim)
            },
            () => {
                this.$model = newData
                this.adjustSize(node, geim)
            }
        )
        edit.commit()
    }

    /**
     * Helper function to return the relative layout of a given slot index. A negative index indicates the class header
     * area.
     * @returns {Rect}
     * @private
     */
    getRelativeSlotLayout(slot, node, isAdding, category) {
        const data = this.$model
        const layout = node.layout

        if (slot < 0) {
            const classNameSize = TextRenderSupport.measureText(
                data.className,
                this.classLabel.style.font
            )
            return new Rect(
                layout.width / 2 - classNameSize.width / 2,
                0,
                layout.width,
                this.classLabel.preferredSize.height - 10
            )
        }

        // determine y-coordinate of the queried slot
        let top =
            this.classLabel.preferredSize.height +
            VERTICAL_SPACING +
            this.categoryLabel.preferredSize.height
        top += slot * this.elementLabel.preferredSize.height
        if ((isAdding && category === 2) || (!isAdding && slot >= data.attributes.length)) {
            // account for the operations category label
            top += this.categoryLabel.preferredSize.height
        }

        return new Rect(0, top, layout.width, this.elementLabel.preferredSize.height)
    }

    /**
     * Helper function to add the control buttons.
     * @private
     */
    addControls(ctx, container, nodeLayout, opened, enableRemoveButton) {
        const openMarker = document.createElementNS('http://www.w3.org/2000/svg', 'polygon')
        openMarker.setAttribute('fill', 'white')
        openMarker.setAttribute('points', opened ? '-5,-3.5 5,-3.5 0,6' : '-3.5,5 6,0 -3.5,-5')
        openMarker.setAttribute('transform', 'translate(12 10)')
        openMarker.setAttribute('class', 'uml-button')
        container.appendChild(openMarker)

        const plusBackground = document.createElementNS('http://www.w3.org/2000/svg', 'rect')
        Fill.setFill(this.$fill, plusBackground, ctx)
        plusBackground.setAttribute('x', `${nodeLayout.width - 37}px`)
        plusBackground.setAttribute('y', '2px')
        plusBackground.setAttribute('width', '16px')
        plusBackground.setAttribute('height', '16px')
        const plusButtonPath = document.createElementNS('http://www.w3.org/2000/svg', 'path')
        plusButtonPath.setAttribute('fill', 'white')
        plusButtonPath.setAttribute(
            'd',
            'M8.5,16.5c-4.418,0-8,3.582-8,8s3.582,8,8,8c4.418,0,8-3.582,8-8S12.918,16.5,8.5,16.5z M13.5,25.5h-4v4h-2v-4h-4v-2h4v-4h2v4h4V25.5z'
        )
        plusButtonPath.setAttribute('transform', `matrix(0.8 0 0 0.8 ${nodeLayout.width - 36} -10)`)
        const plus = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        plus.setAttribute('class', 'uml-button')
        plus.appendChild(plusBackground)
        plus.appendChild(plusButtonPath)
        container.appendChild(plus)

        const minusButtonBackground = document.createElementNS('http://www.w3.org/2000/svg', 'rect')
        Fill.setFill(this.$fill, minusButtonBackground, ctx)
        minusButtonBackground.setAttribute('x', `${nodeLayout.width - 19}px`)
        minusButtonBackground.setAttribute('y', '2px')
        minusButtonBackground.setAttribute('width', '16px')
        minusButtonBackground.setAttribute('height', '16px')
        const minusButtonPath = document.createElementNS('http://www.w3.org/2000/svg', 'path')
        minusButtonPath.setAttribute('fill', 'white')
        minusButtonPath.setAttribute('fill-opacity', enableRemoveButton ? '1' : '0.5')
        minusButtonPath.setAttribute(
            'd',
            'M8.5,16.5c-4.418,0-8,3.582-8,8s3.582,8,8,8c4.418,0,8-3.582,8-8S12.918,16.5,8.5,16.5z M13.5,25.5h-10v-2h10V25.5z'
        )
        minusButtonPath.setAttribute('transform', `matrix(0.8 0 0 0.8 ${nodeLayout.width - 18} -10)`)
        const minus = document.createElementNS('http://www.w3.org/2000/svg', 'g')
        minus.setAttribute('class', enableRemoveButton ? 'uml-button' : 'uml-button disabled')
        minus.appendChild(minusButtonBackground)
        minus.appendChild(minusButtonPath)
        container.appendChild(minus)
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

    /**
     * Helper function to obtain the visual creator of the item.
     * @param {INode|ILabel} item
     * @param {INodeStyle|ILabelStyle?} itemStyle
     * @private
     */
    static getCreator(item, itemStyle) {
        itemStyle = itemStyle || item.style
        return itemStyle.renderer.getVisualCreator(item, itemStyle)
    }
}

/**
 * Markup extension needed to (de-)serialize the UML style.
 */
export const UMLNodeStyleExtension = Class('UMLNodeStyleExtension', {
    $extends: MarkupExtension,

    $fill: null,
    fill: {
        $meta() {
            return [TypeAttribute(Fill.$class)]
        },
        get() {
            return this.$fill
        },
        set(fill) {
            this.$fill = fill
        }
    },

    $highlightFill: null,
    highlightFill: {
        $meta() {
            return [TypeAttribute(Fill.$class)]
        },
        get() {
            return this.$highlightFill
        },
        set(fill) {
            this.$highlightFill = fill
        }
    },

    $model: null,
    model: {
        $meta() {
            return [TypeAttribute(YObject.$class)]
        },
        get() {
            return this.$model
        },
        set(model) {
            this.$model = model
        }
    },

    provideValue(serviceProvider) {
        return new UMLNodeStyle(this.model, this.fill, this.highlightFill)
    }
})

/**
 * Listener that handles the serialization of the UML style.

export const UMLNodeStyleSerializationListener = (sender, args) => {
    const item = args.item
    if (item instanceof UMLNodeStyle) {
        const umlNodeStyleExtension = new UMLNodeStyleExtension()
        umlNodeStyleExtension.fill = item.fill
        umlNodeStyleExtension.highlightFill = item.highlightFill
        umlNodeStyleExtension.model = item.model
        const context = args.context
        context.serializeReplacement(UMLNodeStyleExtension.$class, item, umlNodeStyleExtension)
        args.handled = true
    }
}

// export a default object to be able to map a namespace to this module for serialization
export default { UMLNodeStyle, UMLNodeStyleExtension }
 */