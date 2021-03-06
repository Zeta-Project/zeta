import {
    Arrow,
    ArrowType,
    DashStyle,
    Fill,
    Stroke
} from 'yfiles'
import {UMLEdgeStyle} from './UMLEdgeStyle'
import VuejsEdgeStyle from './VuejsEdgeStyle'
import {ModelEdgeModel} from '../ModelEdgeModel'
import Vue from "vue";
import Edge from "../../../components/edges/Edge.vue";

/**
 * Static helpers class to create UML styles and provide methods to check for certain styles.
 */
export function createAssociationStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: false,
        targetDeletionDeletesSource: false
    });

    return new UMLEdgeStyle(model)
}

export function createDirectedAssociationStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: false,
        targetDeletionDeletesSource: false
    });

    return new UMLEdgeStyle(model, {
        targetArrow: new Arrow({
            stroke: Stroke.BLACK,
            fill: Fill.BLACK,
            type: ArrowType.DEFAULT
        })
    })
}

export function createRealizationStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: true,
        targetDeletionDeletesSource: false
    })

    return new UMLEdgeStyle(model, {
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

export function createCompositionStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: true,
        targetDeletionDeletesSource: true
    })

    return new UMLEdgeStyle(model, {
        targetArrow: new Arrow({
            stroke: Stroke.BLACK,
            fill: Fill.BLACK,
            type: ArrowType.DIAMOND
        })
    })
}

export function createGeneralizationStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: false,
        targetDeletionDeletesSource: true
    })

    return new UMLEdgeStyle(model, {
        targetArrow: new Arrow({
            stroke: Stroke.BLACK,
            fill: Fill.WHITE,
            type: ArrowType.TRIANGLE
        })
    })
}

export function createAggregationStyle() {
    const model = new ModelEdgeModel({
        sourceDeletionDeletesTarget: true,
        targetDeletionDeletesSource: false
    })

    return new UMLEdgeStyle(model, {
        targetArrow: new Arrow({
            stroke: Stroke.BLACK,
            fill: Fill.WHITE,
            type: ArrowType.DIAMOND
        })
    })
}

export function createDependencyStyle() {
    return new UMLEdgeStyle({
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
