import { Arrow, ArrowType, DashStyle, Fill, PolylineEdgeStyle, Stroke } from 'yfiles'

/**
 * Static helpers class to create UML styles and provide methods to check for certain styles.
 */
export function createAssociationStyle() {
  return new PolylineEdgeStyle()
}

export function createDirectedAssociationStyle() {
  return new PolylineEdgeStyle({
    targetArrow: new Arrow({
      stroke: Stroke.BLACK,
      fill: Fill.BLACK,
      type: ArrowType.DEFAULT
    })
  })
}

export function createRealizationStyle() {
  return new PolylineEdgeStyle({
    stroke: new Stroke({
      dashStyle: DashStyle.DASH
    }),
    sourceArrow: new Arrow({
      stroke: Stroke.BLACK,
      fill: Fill.WHITE,
      type: ArrowType.TRIANGLE
    })
  })
}

export function createGeneralizationStyle() {
  return new PolylineEdgeStyle({
    sourceArrow: new Arrow({
      stroke: Stroke.BLACK,
      fill: Fill.WHITE,
      type: ArrowType.TRIANGLE
    })
  })
}

export function createAggregationStyle() {
  return new PolylineEdgeStyle({
    sourceArrow: new Arrow({
      stroke: Stroke.BLACK,
      fill: Fill.WHITE,
      type: ArrowType.DIAMOND
    })
  })
}

export function createDependencyStyle() {
  return new PolylineEdgeStyle({
    stroke: new Stroke({
      dashStyle: DashStyle.DASH
    }),
    targetArrow: new Arrow({
      stroke: Stroke.BLACK,
      fill: Fill.BLACK,
      type: ArrowType.DEFAULT
    })
  })
}

/**
 * Inheritance styles, i.e. generalization or realization
 * @param style
 * @returns {boolean}
 */
export function isInheritance(style) {
  return isGeneralization(style) || isRealization(style)
}

/**
 * If the style symbolizes a generalization.
 * @param style
 * @returns {boolean}
 */
export function isGeneralization(style) {
  if (!style.stroke || !style.sourceArrow) {
    return false
  }
  return style.stroke.dashStyle === DashStyle.SOLID && style.sourceArrow.type === ArrowType.TRIANGLE
}

/**
 * If the style symbolizes a realization.
 * @param style
 * @returns {boolean}
 */
export function isRealization(style) {
  if (!style.stroke || !style.sourceArrow) {
    return false
  }
  return style.stroke.dashStyle === DashStyle.DASH && style.sourceArrow.type === ArrowType.TRIANGLE
}
