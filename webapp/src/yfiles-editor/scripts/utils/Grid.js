import {
  Fill,
  GraphSnapContext,
  GridConstraintProvider,
  GridInfo,
  GridSnapTypes,
  GridStyle,
  GridVisualCreator,
  LabelSnapContext,
  RenderModes,
  Stroke
} from 'yfiles'
import { bindAction, bindChangeListener } from './Bindings'

/**
 * A palette of sample nodes. Users can drag and drop the nodes from this palette to a graph control.
 */
export class Grid {
  constructor (graphComponent) {
    this.graphComponent = graphComponent
  }

  /**
   * Initializes snapping for labels and other graph items. The default snapping behavior can easily
   * be enabled by setting the according snap context. Those snap contexts provide many options to
   * fine tune their behavior, in this case we use it to make the items snap only to the given grid
   * but not to other graph items. Please see the documentation of {@link GraphSnapContext} and
   * {@link LabelSnapContext} for more information.
   */
  initializeSnapping () {
    const geim = this.graphComponent.inputMode
    const graphSnapContext = new GraphSnapContext({
      enabled: true,
      // disable some of the default snapping behavior such that the graph items only snap to the grid and nowhere else
      snapBendAdjacentSegments: false,
      snapBendsToSnapLines: false,
      snapNodesToSnapLines: false,
      snapOrthogonalMovement: false,
      snapPortAdjacentSegments: false,
      snapSegmentsToSnapLines: false
    })
    const labelSnapContext = new LabelSnapContext()
    geim.snapContext = graphSnapContext
    geim.labelSnapContext = labelSnapContext
  }

  /**
   * Initializes the grid snapping types combobox and the {@link GridInfo} which is the actual grid to
   * which items can snap.
   */
  initializeGrid () {
    // Initializes GridInfo which holds the basic information about the grid
    // Sets horizontal and vertical space between grid lines
    let gridInfo = new GridInfo()
    gridInfo.horizontalSpacing = 50
    gridInfo.verticalSpacing = 50

    // Creates grid visualization and adds it to this.graphComponent
    let grid = new GridVisualCreator(gridInfo)
    grid.gridStyle = GridStyle.LINES
    grid.stroke = new Stroke(Fill.GRAY, 1)
    grid.renderMode = RenderModes.CANVAS
    this.grid = grid

    this.graphComponent.backgroundGroup.addChild(grid)
    // Sets constraint provider to make nodes and bends snap to grid
    const graphSnapContext = this.graphComponent.inputMode.snapContext
    graphSnapContext.nodeGridConstraintProvider = new GridConstraintProvider(gridInfo)
    graphSnapContext.bendGridConstraintProvider = new GridConstraintProvider(gridInfo)

    this.updateSnapType(GridSnapTypes.GRID_POINTS)
    this.updateGridStyle(GridStyle.DOTS)
  }

  /**
   * Sets the chosen grid snap type on the grid.
   * @param {GridSnapTypes} gridSnapType
   */
  updateSnapType (gridSnapType) {
    const graphSnapContext = this.graphComponent.inputMode.snapContext
    graphSnapContext.gridSnapType = GridSnapTypes.ALL
  }

  updateGridStyle (gridStyle) {
    this.grid.gridStyle = gridStyle
    this.updateGridThickness(2)
    this.graphComponent.invalidate()
  }

  /**
   * Sets the chosen thickness to the grid.
   */
  updateGridThickness (value) {
    let thickness = value
    if (this.grid.gridStyle === GridStyle.DOTS) {
      // make sure the grid is at least 2 pixels thick when 'Dots' is selected
      thickness = Math.max(2, thickness)
    }
    this.grid.stroke.thickness = thickness
    this.updateSvgTemplate()
    this.graphComponent.invalidate()
  }

  /**
   * Updates the svg template.
   */
  updateSvgTemplate () {
    if (this.grid.renderMode === RenderModes.SVG) {
      this.grid.renderMode = RenderModes.CANVAS
      this.graphComponent.updateVisual()
      this.grid.renderMode = RenderModes.SVG
    }
  }

  /**
   * Sets the chosen render mode on the grid.
   */
  updateRenderMode (renderMode) {
    this.grid.renderMode = renderMode
    this.graphComponent.invalidate()
  }

  /**
   * Sets the chosen color to the grid.
   * @param {Fill} fill
   */
  updateGridColor (fill) {
    this.grid.stroke.fill = fill
    this.updateSvgTemplate()
    this.graphComponent.invalidate()
  }

  registerCommand () {
    bindAction('#grid-button', () => {
      this.grid.visible = document.querySelector('#grid-button').checked
      this.graphComponent.invalidate() // triggers repaint
    })
    bindChangeListener('select[data-command=\'GridSnapTypeChanged\']', this.updateSnapType)
    bindChangeListener('select[data-command=\'GridRenderModeChanged\']', this.updateRenderMode)
    bindChangeListener('select[data-command=\'GridColorChanged\']', this.updateGridColor)
    bindChangeListener('#thickness', this.updateGridThickness)
  }
}
