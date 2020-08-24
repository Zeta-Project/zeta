import {Fill, GridConstraintProvider, GridInfo, GridSnapTypes, GridStyle, GridVisualCreator, RenderModes, Stroke} from 'yfiles'

/**
 * A palette of sample nodes. Users can drag and drop the nodes from this palette to a graph control.
 */
export class Grid {
    constructor(graphComponent) {
        this.graphComponent = graphComponent
    }

    /**
     * Initializes the grid snapping types combobox and the {@link GridInfo} which is the actual grid to
     * which items can snap.
     */
    initializeGrid() {
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
        this.grid = grid;

        this.graphComponent.backgroundGroup.addChild(grid)
        // Sets constraint provider to make nodes and bends snap to grid
        const graphSnapContext = this.graphComponent.inputMode.snapContext
        graphSnapContext.nodeGridConstraintProvider = new GridConstraintProvider(gridInfo);
        graphSnapContext.bendGridConstraintProvider = new GridConstraintProvider(gridInfo);

        this.updateSnapType(GridSnapTypes.GRID_POINTS)
        this.updateGridStyle(GridStyle.DOTS)
    }

    /**
     * Sets the chosen grid snap type on the grid.
     * @param {GridSnapTypes} gridSnapType
     */
    updateSnapType() {
        this.graphComponent.inputMode.snapContext.gridSnapType = GridSnapTypes.ALL
    }

    updateGridStyle(gridStyle) {
        this.grid.gridStyle = gridStyle
        this.updateGridThickness(2)
        this.graphComponent.invalidate()
    }

    /**
     * Sets the chosen thickness to the grid.
     */
    updateGridThickness(value) {
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
    updateSvgTemplate() {
        if (this.grid.renderMode === RenderModes.SVG) {
            this.grid.renderMode = RenderModes.CANVAS
            this.graphComponent.updateVisual()
            this.grid.renderMode = RenderModes.SVG
        }
    }
}
