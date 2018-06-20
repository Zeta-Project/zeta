import _ from 'lodash';
import {CommonInspectorGroups, CommonInspectorInputs, inp} from '../../inspector';
import objectAssignDeep from 'object-assign-deep';

class EdgeGenerator {
    create(edge, edges) {
        let edgesWithTextField = edges.filter(fe => fe.placings.filter(p => p.geoElement.type === "textfield").length > 0);
        return Object.assign(this.getGroups(edgesWithTextField), this.getInputs(edgesWithTextField));
    }

    getGroups(edges) {
        let placings;
        let result = {};
        edges.forEach((edge) => {
            placings = edge.placings.filter(p => p.geoElement.type === "textfield");
            for (let i = 0; i < placings.length; i++) {
                result[(placings[i].geoElement.identifier).replace(".", "_")] = {
                    label: (placings[i].geoElement.identifier).replace(".", "_"),
                    index: i + 1
                }
            }
        });
        return {groups: result}
    }

    getInputs(edges) {
        let result = {};
        let placings;
        edges.forEach((edge) => {
            placings = edge.placings.filter(p => p.geoElement.type === "textfield");
            for (let i = 0; i < placings.length; i++) {
                if (placings[i].geoElement.multiline) {
                    result[(placings[i].geoElement.identifier).replace(".", "_")] = {
                        type: 'list',
                        item: {
                            type: 'text',
                            defaultValue: placings[i].geoElement.textBody
                        },
                        label: placings[i].geoElement.identifier,
                        index: i + 1,
                        group: edge.name
                    }
                } else {
                    result[(placings[i].geoElement.identifier).replace(".", "_")] = {
                        type: 'text',
                        label: placings[i].geoElement.identifier,
                        defaultValue: placings[i].geoElement.textBody,
                        index: i + 1,
                        group: edge.name
                    }
                }
            }
        });

        return {inputs: result}
    }

}

class ShapeGenerator {
    constructor(shapeDefinitionGenerator) {
        this.definitionGenerator = shapeDefinitionGenerator;
        this.mapper = {
            'ellipse': new EllipseGenerator(),
            'rectangle': new RectangleGenerator(),
            'textfield': new TextGenerator(),
            'statictext': new StaticTextGenerator(),
            'line': new LineGenerator(),
            'polygon': new PolygonGenerator(),
            'polyline': new PolyLineGenerator(),
            'roundedRectangle': new RoundedRectangleGenerator(),
        };
    }

    create(shape) {
        return {
            inputs: _.extend({
                attrs: this.processElements(shape),
            }, CommonInspectorInputs),
            groups: CommonInspectorGroups
        };
    }

    processElements(shape) {

        const elements = shape?.geoElements || [];
        const flatGeoElements = this.flattenGeoElement(elements);
        const maxHeight = this.definitionGenerator.calculateHeight(shape);
        const maxWidth = this.definitionGenerator.calculateWidth(shape);
        return flatGeoElements.reduce((result, element) => {
            const entry = this.processElement(element, flatGeoElements, maxHeight, maxWidth);
            return Object.assign(result, entry);
        }, {});
    }

    flattenGeoElement(geoElements) {
        const results = [];

        const flatten = function (elem, results) {
            results.push(elem);
            (elem.childGeoElements || []).forEach(e => {
                e.parent = elem.id;
                flatten(e, results);
            })
        };

        geoElements.forEach(e => flatten(e, results));
        return results;
    }

    processElement(element, elements, maxHeight, maxWidth) {
        if (this.mapper[element.type]) {
            const shapeGenerator = this.mapper[element.type];
            return shapeGenerator.create(element, maxHeight, maxWidth);
        } else {
            console.log("Error: not defined in Mapper: " + element.type);
        }
    }
}

class ElementGenerator {
    constructor() {
        this.counter = 0;
    }

    createFill(index, group, label) {
        return {index, group, label};
    }

    createFillOpacity(index, group, label) {
        return {index, group, label};
    }

    createStroke(index, group, label) {
        return {index, group, label};
    }

    createStrokeWidth(index, group, label) {
        return {
            group,
            label,
            index,
            min: 0,
            max: 30,
            defaultValue: 1,
        };
    }

    createStrokeDasharray(index, group, label) {
        return {index, group, label};
    }

    createRx(index, group, label, max) {
        return {index, group, label, max};
    }

    createRy(index, group, label, max) {
        return {index, group, label, max};
    }

    createX(index, group, label, element, maxWidth) {
        return {index, group, label, max: (maxWidth - element.size.width)};
    }

    createY(index, group, label, element, maxHeight) {
        return {index, group, label, max: (maxHeight - element.size.height)};
    }

    createHeight(index, group, label, max) {
        return {index, group, max, label};
    }

    createWidth(index, group, label, max) {
        return {index, group, max, label};
    }

    selector(tag, element) {
        return `${tag}.${element.id}`;
    }
}

class EllipseGenerator extends ElementGenerator {
    create(element, maxHeight, maxWidth) {
        this.counter++;
        return {
            [this.selector('ellipse', element)]: inp(this.createSpecificAttributes()),
            [`.${element.id}`]: inp(this.createGeneralAttributes(element, maxHeight, maxWidth)),
        };
    }

    createSpecificAttributes() {
        const group = `Presentation Ellipse ${this.counter}`;
        return {
            fill: this.createFill(1, group, 'Background-Color Ellipse'),
            'fill-opacity': this.createFillOpacity(2, group, 'Opacity Ellipse'),
            stroke: this.createStroke(3, group, 'Line-Color Ellipse'),
            'stroke-width': this.createStrokeWidth(4, group, 'Stroke Width Ellipse'),
            'stroke-dasharray': this.createStrokeDasharray(5, group, 'Stroke Dash Ellipse'),
        };
    }

    createGeneralAttributes(element, maxHeight, maxWidth) {
        const group = `Geometry Ellipse ${this.counter}`;
        return {
            cx: {
                index: 1,
                group,
                min: (element.sizeWidth / 2),
                max: (maxWidth - element.sizeWidth / 2),
            },
            cy: {
                index: 2,
                group,
                min: (element.sizeHeight / 2),
                max: (maxHeight - element.sizeHeight / 2),
            },
            rx: this.createRx(3, group, 'X-axis radius', maxWidth / 2),
            ry: this.createRy(3, group, 'Y-axis radius', maxHeight / 2),
        };
    }
}

class RectangleGenerator extends ElementGenerator {
    create(element, maxHeight, maxWidth) {
        this.counter++;
        return {
            [this.selector('rect', element)]: inp(this.createSpecificAttributes()),
            [`.${element.id}`]: inp(this.createGeneralAttributes(element, maxHeight, maxWidth)),
        };
    }

    createSpecificAttributes() {
        const group = `Presentation Rectangle ${this.counter}`
        return {
            fill: this.createFill(1, group, 'Background-Color Rectangle'),
            'fill-opacity': this.createFillOpacity(2, group, 'Opacity Rectangle'),
            stroke: this.createStroke(3, group, 'Line-Color Rectangle'),
            'stroke-width': this.createStrokeWidth(4, group, 'Stroke Width Rectangle'),
            'stroke-dasharray': this.createStrokeDasharray(5, group, 'Stroke Dash Rectangle'),
        };
    }

    createGeneralAttributes(element, maxHeight, maxWidth) {
        const group = `Geometry Rectangle ${this.counter}`;
        return {
            x: this.createX(1, group, 'x Position Rectangle', element, maxWidth),
            y: this.createY(2, group, 'y Position Rectangle', element, maxHeight),
            height: this.createHeight(3, group, 'Height Rectangle', maxHeight),
            width: this.createWidth(3, group, 'Width Rectangle', maxWidth),
        };
    }
}

class StaticTextGenerator extends ElementGenerator {
    create(element) {
        return {
            [`.${element.id}`]: {}
        };
    }
}

class TextGenerator extends ElementGenerator {
    create(element, maxHeight, maxWidth) {
        this.counter++;
        let textObject;
        if (element?.editable === true) {
            textObject = this.createFilledTextForInspector(element, maxHeight, maxWidth);
        } else {
            textObject = this.createEmptyTextForInspector(element);
        }
        return textObject;
    }

    createFilledTextForInspector(element, maxHeight, maxWidth) {
        return {
            [this.selector('text', element)]: inp(this.createSpecificAttributes(element, maxHeight, maxWidth)),
            [`.${element.id}`]: inp(this.createGeneralAttributes()),
        };
    }

    createEmptyTextForInspector(element) {
        return {
            [`.${element.id}`]: {}
        };
    }

    createSpecificAttributes(element, maxHeight, maxWidth) {
        const group = `Text Geometry ${this.counter}`;
        return Object.assign(this.createSpecificDefaultTextAttributes(element), this.createSpecificTextPositionAttributes(element, group, maxHeight, maxWidth));
    }

    createSpecificDefaultTextAttributes(element) {
        let defaultTextAttributes;
        if (element?.multiline === true) {
            defaultTextAttributes = {
                text: {
                    index: 1,
                    type: 'list',
                    item: {type: 'text'},
                    group: `Text ${this.counter}`,
                },
            }
        } else {
            defaultTextAttributes = {
                text: {
                    index: 1,
                    type: 'text',
                    group: `Text ${this.counter}`,
                },
            }
        }

        return defaultTextAttributes;

    }

    createSpecificTextPositionAttributes(element, group, maxHeight, maxWidth) {
        return {
            x: this.createX(1, group, 'x Position Text', element, maxWidth),
            y: this.createY(2, group, 'y Position Text', element, maxHeight),
        }
    }

    createGeneralAttributes() {
        const group = `Text Style ${this.counter}`;
        return {
            'font-size': {index: 2, group},
            'font-family': {index: 3, group},
            'font-weight': {index: 4, group},
            fill: this.createFill(6, group, 'Text Color'),
        };
    }
}

class LineGenerator extends ElementGenerator {
    create(element) {
        this.counter++;
        return {
            [this.selector('line', element)]: inp(this.createAttributes()),
        };
    }

    createAttributes() {
        const group = `Presentation Line ${this.counter}`;
        return {
            stroke: this.createStroke(2, group, 'Line-Color'),
            'stroke-width': this.createStrokeWidth(3, group, ' Stroke Width Line'),
            'stroke-dasharray': this.createStrokeDasharray(4, `Presentation ${this.counter}`, 'Stroke Dash Line'),
        };
    }
}

class PolygonGenerator extends ElementGenerator {
    create(element) {
        this.counter++;
        return {
            [this.selector('polygon', element)]: inp(this.createAttributes()),
        };
    }

    createAttributes() {
        const group = `Presentation Polygon ${this.counter}`;
        return {
            fill: this.createFill(1, group, 'Background-Color Polygon'),
            'fill-opacity': this.createFillOpacity(2, group, 'Opacity Polygon'),
            stroke: this.createStroke(3, group, 'Line-Color Polygon'),
            'stroke-width': this.createStrokeWidth(4, group, 'Stroke Width Polygon'),
            'stroke-dasharray': this.createStrokeDasharray(5, group, 'Stroke Dash Polygon'),
        };
    }
}

class PolyLineGenerator extends ElementGenerator {
    create(element) {
        this.counter++;
        return {
            [this.selector('polyline', element)]: inp(this.createAttributes()),
        };
    }

    createAttributes() {
        const group = `Presentation Polyline ${this.counter}`;
        return {
            fill: this.createFill(1, group, 'Background-Color Polyline'),
            stroke: this.createStroke(2, group, 'Line-Color'),
            'stroke-width': this.createStrokeWidth(3, group, ' Stroke Width Line'),
            'stroke-dasharray': this.createStrokeDasharray(4, group, 'Stroke Dash Line'),
        };
    }
}

class RoundedRectangleGenerator extends ElementGenerator {
    create(element, maxHeight, maxWidth) {
        this.counter++;
        return {
            [this.selector('rect', element)]: inp(this.createSpecificAttributes()),
            [`.${element.id}`]: inp(this.createGeneralAttributes(element, maxHeight, maxWidth)),
        };
    }

    createSpecificAttributes() {
        const group = `Presentation R-Rectangle ${this.counter}`;
        return {
            fill: this.createFill(1, group, "Fill color"),
            'fill-opacity': this.createFillOpacity(2, group, 'Opacity Rounded Rectangle'),
            stroke: this.createStroke(3, group, 'Line-Color Rounded Rectangle'),
            'stroke-width': this.createStrokeWidth(4, group, 'Stroke Width Rounded Rectangle'),
            'stroke-dasharray': this.createStrokeDasharray(5, group, 'Stroke Dash Rounded Rectangle'),
        };
    }

    createGeneralAttributes(element, maxHeight, maxWidth) {
        const group = `Geometry R-Rectangle ${this.counter}`;
        return {
            rx: this.createRx(6, group, 'Curve X', element.sizeWidth / 2),
            ry: this.createRy(7, group, 'Curve Y', element.sizeHeight / 2),
            x: this.createX(1, group, 'x Position Rounded Rectangle', element, maxWidth),
            y: this.createY(2, group, 'y Position Rounded Rectangle', element, maxHeight),
            height: this.createHeight(3, group, 'Height Rounded Rectangle', maxHeight),
            width: this.createWidth(3, group, 'Width Rounded Rectangle', maxWidth),
        };
    }
}

export default class {
    constructor(shape, shapeDefinitionGenerator, connectionDefinitionGenerator) {
        this.shapes = shape?.nodes || [];
        this.edges = shape?.edges || [];
        this.shapeGenerator = new ShapeGenerator(shapeDefinitionGenerator);
        this.edgeGenerator = new EdgeGenerator(connectionDefinitionGenerator);
    }

    get InspectorDefs() {
        return Object.assign(this.getShapeInspectorDefs(), this.getEdgeInspectorDefs());
    }

    getShapeInspectorDefs() {
        let shapeDefs = this.shapes.reduce((result, shape) => {
            result[`zeta.${shape.name}`] = this.shapeGenerator.create(shape);
            return result;
        }, {});
        return shapeDefs;
    }

    getEdgeInspectorDefs() {
        let mLink;
        let mergedEdges = {};
        let edgeDef = {};
        let edges = this.edges;
        edges.forEach((edge) => {
            let edgeObject = this.edgeGenerator.create(edge, edges);
            mergedEdges = objectAssignDeep(edgeDef, edgeObject);
        });
        mLink = {'zeta.MLink': mergedEdges};
        return mLink;
    }
}
