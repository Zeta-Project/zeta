import joint from 'jointjs';

const STENCIL_SIZE = 80;
const GEOMETRIC_MODEL = {
    RECTANGLE: 'rectangle',
    ELLIPSE: 'ellipse',
    LINE: 'line',
    ROUND_RECT: 'roundedRectangle',
    POLYGON: 'polygon',
    POLY_LINE: 'polyline',
    TEXTFIELD: 'textfield',
};

function findTop(geoElements) {
    return geoElements.filter(e => e.parent === undefined || e.parent === null)
}

function getInteger(value) {
    return value === undefined ? 0 : Number(value);
}

function addition(value1, value2) {
    return getInteger(value1) + getInteger(value2);
}

function max(array, access = e => e) {
    return array.reduce((max, entry) => {
        return Math.max(max, getInteger(access(entry)));
    }, 0);
}

function min(array, access = e => e) {
    return array.reduce((min, entry) => {
        return Math.min(min, getInteger(access(entry)));
    }, Number.MAX_VALUE);
}

class SvgBuilder {
    constructor() {
        this.mapper = {
            [GEOMETRIC_MODEL.RECTANGLE]: (e, a) => `<rect class="${e.id}" />` + this.createChildGeoElement(e, a),
            [GEOMETRIC_MODEL.ELLIPSE]: (e, a) => `<ellipse class="${e.id}" />` + this.createChildGeoElement(e, a),
            [GEOMETRIC_MODEL.LINE]: e => `<line class="${e.id}" />`,
            [GEOMETRIC_MODEL.ROUND_RECT]: (e, a) => `<rect class="${e.id}" />` + this.createChildGeoElement(e, a),
            [GEOMETRIC_MODEL.POLYGON]: (e, a) => `<polygon class="${e.id}" />` + this.createChildGeoElement(e, a),
            [GEOMETRIC_MODEL.POLY_LINE]: e => `<polyline class="${e.id}" />`,
            [GEOMETRIC_MODEL.TEXTFIELD]: e => `<text class="${e.id} ${e.id}" ></text>`,
        };
    }

    create(geoElements) {
        const markup = findTop(geoElements).reduce((r, e) => {
            return r + this.processElement(e, geoElements);
        }, '');
        return `<g class="rotatable"><g class="scalable"><rect class="bounding-box" />${markup}</g></g>`;
    }

    processElement(geoElement, geoElements) {
        return this.mapper[geoElement.type] ? this.mapper[geoElement.type](geoElement, geoElements) : '';
    }

    createChildGeoElement(element, elements) {
        const children = this.findGeoElement(element, elements);
        return children.reduce((result, e) => {
            return result + this.processElement(e, elements);
        }, '');
    }

    findGeoElement(parent, elements) {
        return parent.childGeoElements ? parent.childGeoElements : [];
    }
}

class Calculator {
    constructor() {
        this.height = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => addition(e.position.y, e.size.height),
            [GEOMETRIC_MODEL.ELLIPSE]: e => addition(e.position.y, e.size.height),
            [GEOMETRIC_MODEL.LINE]: e => max([e.startPoint.y, e.endPoint.y]),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => addition(e.position.y, e.size.height),
            [GEOMETRIC_MODEL.POLYGON]: e => max(e.points, point => point.y),
            [GEOMETRIC_MODEL.POLY_LINE]: e => max(e.points, point => point.y),
            [GEOMETRIC_MODEL.TEXTFIELD]: e => addition(e.position.y, e.size.height),
        };
        this.width = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => addition(e.position.x, e.size.width),
            [GEOMETRIC_MODEL.ELLIPSE]: e => addition(e.position.x, e.size.width),
            [GEOMETRIC_MODEL.LINE]: e => max([e.startPoint.x, e.endPoint.x]),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => addition(e.position.x, e.size.width),
            [GEOMETRIC_MODEL.POLYGON]: e => max(e.points, point => point.x),
            [GEOMETRIC_MODEL.POLY_LINE]: e => max(e.points, point => point.x),
            [GEOMETRIC_MODEL.TEXTFIELD]: e => addition(e.position.x, e.size.width),
        };
    }

    calculateSizeHeight(geoElements) {
        const height = this.calculateHeight(geoElements);
        const width = this.calculateWidth(geoElements);
        return this.calculateSize(height, width);
    }

    calculateSizeWidth(geoElements) {
        const height = this.calculateHeight(geoElements);
        const width = this.calculateWidth(geoElements);
        return this.calculateSize(width, height);
    }

    calculateSize(actual, related) {
        if (actual <= STENCIL_SIZE && related <= STENCIL_SIZE) {
            return actual;
        }

        return (actual > related) ? STENCIL_SIZE : this.scaleSizeValue(actual, related, STENCIL_SIZE);
    }

    scaleSizeValue(value1, value2, max) {
        const result1 = Math.round(value1 / (value2 / max));
        const result2 = Math.round(value2 / (value1 / max));
        return Math.min(result1, result2);
    }

    calculateHeight(geoElements) {
        return findTop(geoElements).reduce((max, e) => {
            const value = this.height[e.type] ? this.height[e.type](e) : 0;
            return Math.max(max, value);
        }, 0);
    }

    calculateWidth(geoElements) {
        return findTop(geoElements).reduce((r, e) => {
            const value = this.width[e.type] ? this.width[e.type](e) : 0;
            return Math.max(r, value);
        }, 0);
    }
}

class AttrBuilder {
    constructor(shapeStyleGenerator, shape) {
        this.shapeStyleGenerator = shapeStyleGenerator;
        this.shape = shape;
        this.calculator = new Calculator();
        this.createMapper = {
            [GEOMETRIC_MODEL.RECTANGLE]: (e, a) => this.createRectangle(e, a),
            [GEOMETRIC_MODEL.ELLIPSE]: (e, a) => this.createEllipse(e, a),
            [GEOMETRIC_MODEL.LINE]: this.createLine,
            [GEOMETRIC_MODEL.ROUND_RECT]: (e, a) => this.createRoundRect(e, a),
            [GEOMETRIC_MODEL.POLYGON]: (e, a) => this.createPolygon(e, a),
            [GEOMETRIC_MODEL.POLY_LINE]: (e, a) => this.createPolygon(e, a),
            [GEOMETRIC_MODEL.TEXTFIELD]: (e, a) => this.createTextField(e, a),
        };
        this.xMapper = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => e.position.x,
            [GEOMETRIC_MODEL.ELLIPSE]: e => e.position.x,
            [GEOMETRIC_MODEL.POLYGON]: e => min(e.points, point => point.x),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => e.position.x,
        };
        this.yMapper = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => e.position.y,
            [GEOMETRIC_MODEL.ELLIPSE]: e => e.position.y,
            [GEOMETRIC_MODEL.POLYGON]: e => min(e.points, point => point.y),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => e.position.y,
        };
    }

    create(geoElements) {
        return geoElements.reduce((result, e) => {
            result[`.${e.id}`] = this.processElement(e, geoElements);
            return result;
        }, this.createDefaultAttr(geoElements));
    }

    processElement(geoElement, geoElements) {
        if (this.createMapper[geoElement.type]) {
            return this.createMapper[geoElement.type](geoElement, geoElements);
        }
        throw new Error(`Unknown geometric model: ${geoElement.type}`);
    }

    createRectangle(geoElement, geoElements) {
        return {
            x: geoElement.position.x + this.getParentPositionX(geoElement, geoElements),
            y: geoElement.position.y + this.getParentPositionY(geoElement, geoElements),
            width: geoElement.size.width,
            height: geoElement.size.height,
        };
    }

    createEllipse(geoElement, geoElements) {
        const rx = geoElement.size.width / 2;
        const ry = geoElement.size.height / 2;
        return {
            cx: rx + geoElement.position.x + this.getParentPositionX(geoElement, geoElements),
            cy: ry + geoElement.position.y + this.getParentPositionY(geoElement, geoElements),
            rx, // horizontal-radius
            ry, // vertical-radius
        };
    }

    createLine(geoElement) {
        return {
            x1: geoElement.startPoint.x,
            y1: geoElement.startPoint.y,
            x2: geoElement.endPoint.x,
            y2: geoElement.endPoint.y,
        };
    }

    createRoundRect(geoElement, geoElements) {
        return {
            x: geoElement.position.x + this.getParentPositionX(geoElement, geoElements),
            y: geoElement.position.y + this.getParentPositionY(geoElement, geoElements),
            width: geoElement.size.width,
            height: geoElement.size.height,
            rx: geoElement.curveWidth,
            ry: geoElement.curveHeight,
        };
    }

    createPolygon(geoElement, geoElements) {
        const parentX = this.getParentPositionX(geoElement, geoElements);
        const parentY = this.getParentPositionY(geoElement, geoElements);
        return {
            points: geoElement.points.reduce((string, point) => {
                const content = `${point.x + parentX},${point.y + parentY} `;
                return string + content;
            }, ''),
        };
    }

    createTextField(geoElement, geoElements) {
        return {
            x: geoElement.position.x + this.getParentPositionX(geoElement, geoElements),
            y: geoElement.position.y + this.getParentPositionY(geoElement, geoElements),
            id: geoElement.id,
            width: geoElement.size.width,
            height: geoElement.size.height,
            // editable: geoElement?.editable ? geoElement?.editable : false,
            // multiline: geoElement?.multiline ? geoElement?.multiline : false,
            // align:{
            //     horizontal: geoElement?.align?.horizontal ? geoElement?.align?.horizontal : 'middle',
            //     vertical: geoElement?.align?.vertical ? geoElement?.align?.vertical : 'middle',
            // },
            // todo:DefaultText forTextFields
            text: 'Default-Text',
        };
    }

    getParentPositionX(geoElement, geoElements) {
        return this.getParentPosition(geoElement, geoElements, this.xMapper);
    }

    getParentPositionY(geoElement, geoElements) {
        return this.getParentPosition(geoElement, geoElements, this.yMapper);
    }

    getParentPosition(geoElement, geoElements, mapper) {
        const parent = geoElements.find(e => e.id === geoElement.parent);
        if (parent && mapper[parent.type]) {
            return mapper[parent.type](parent) + this.getParentPositionX(parent, geoElements);
        }
        return 0;
    }

    createDefaultAttr(geoElements) {
        return {
            'rect.bounding-box': {
                height: this.calculator.calculateHeight(geoElements),
                width: this.calculator.calculateWidth(geoElements),
            },
        };
    }
}

class ShapeGenerator {
    constructor(shapeStyleGenerator, shape) {
        this.svg = new SvgBuilder();
        this.calculator = new Calculator();
        this.attrs = new AttrBuilder(shapeStyleGenerator, shape);
    }

    create(model) {
        const attributes = this.createAttributes(model);
        return joint.shapes.basic.Generic.extend(attributes);
    }

    createAttributes(model) {
        const geoElements = model?.geoElements ? model?.geoElements : [];
        const flatGeoElements = this.flattenGeoElement(geoElements);
        const defaultsAttribute = this.buildDefaults(model, flatGeoElements);
        const elementDefaults = joint.dia.Element.prototype.defaults;
        return {
            markup: this.svg.create(flatGeoElements),
            defaults: joint.util.deepSupplement(defaultsAttribute, elementDefaults),
        }
    }

    buildDefaults(model, geoElements) {
        return Object.assign(
            this.createMandatoryDefaults(model, geoElements),
            this.createOptionalSizeMaxAttribute(model),
            this.createOptionalSizeMinAttribute(model)
        );
    }

    createMandatoryDefaults(model, geoElements) {
        return {
            type: `zeta.${model?.name}`,
            'init-size': {
                height: this.calculator.calculateHeight(geoElements),
                width: this.calculator.calculateWidth(geoElements),
            },
            size: {
                height: this.calculator.calculateSizeHeight(geoElements),
                width: this.calculator.calculateSizeWidth(geoElements),
            },
            resize: {
                horizontal: this.getBoolean(model?.resizing?.horizontal),
                vertical: this.getBoolean(model?.resizing?.vertical),
                proportional: this.getBoolean(model?.resizing?.proportional),
            },
            attrs: this.attrs.create(geoElements),
            compartments: [],
        }
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


    getBoolean(value) {
        return value === undefined ? true : Boolean(value);
    }

    createOptionalSizeMaxAttribute(model) {
        return model?.size?.heightMax && model?.size?.widthMax ? {
            'size-max': {
                height: Number(model.size.heightMax),
                width: Number(model.size.widthMax),
            }
        } : {};
    }

    createOptionalSizeMinAttribute(model) {
        return model?.size?.heightMin && model?.size?.widthMin ? {
            'size-min': {
                height: Number(model.size.heightMin),
                width: Number(model.size.widthMin),
            }
        } : {};
    }
}

export default class {
    constructor(shape, shapeStyleGenerator) {
        this.nodes = shape.nodes ? shape.nodes : [];
        this.generator = new ShapeGenerator(shapeStyleGenerator, this.nodes);
        this.calculator = new Calculator();
    }

    get zeta() {
        return this.nodes.reduce((result, shape) => {
            result[shape.name] = this.generator.create(shape);
            return result;
        }, {});
    }

    calculateHeight(shape) {
        const geoElements = shape.geoElements ? shape.geoElements : [];
        return this.calculator.calculateHeight(geoElements);
    }

    calculateWidth(shape) {
        const geoElements = shape.geoElements ? shape.geoElements : [];
        return this.calculator.calculateWidth(geoElements);
    }
}