import joint from 'jointjs';

const STENCIL_SIZE = 80;
const GEOMETRIC_MODEL = {
    RECTANGLE: 'rectangle',
    ELLIPSE: 'ellipse',
    LINE: 'line',
    ROUND_RECT: 'roundedRectangle',
    POLYGON: 'polygon',
    POLY_LINE: 'polyline',
    TEXT: 'text',
};

function findTop(elements) {
    return elements.filter(e => e.parent === undefined || e.parent === null)
}

function getInteger(value) {
    return value === undefined ? 0 : new Number(value).valueOf();
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
            [GEOMETRIC_MODEL.RECTANGLE]: (e, a) => `<rect class="${e.id}" />` + this.createChild(e, a),
            [GEOMETRIC_MODEL.ELLIPSE]: (e, a) => `<ellipse class="${e.id}" />` + this.createChild(e, a),
            [GEOMETRIC_MODEL.LINE]: e => `<line class="${e.id}" />`,
            [GEOMETRIC_MODEL.ROUND_RECT]: (e, a) => `<rect class="${e.id}" />` + this.createChild(e, a),
            [GEOMETRIC_MODEL.POLYGON]: (e, a) => `<polygon class="${e.id}" />` + this.createChild(e, a),
            [GEOMETRIC_MODEL.POLY_LINE]: e => `<polyline class="${e.id}" />`,
            [GEOMETRIC_MODEL.TEXT]: e => `<text class="${e.id} ${e.id}" > </text>`,
        };
    }

    create(elements) {
        const markup = findTop(elements).reduce((r, e) => {
            return r + this.processElement(e, elements);
        }, '');
        return `<g class="rotatable"><g class="scalable"><rect class="bounding-box" />${markup}</g></g>`;
    }

    processElement(element, elements) {
        return this.mapper[element.type] ? this.mapper[element.type](element, elements) : ''; 
    }

    createChild(element, elements) {
        const children = this.findChild(element, elements);
        return children.reduce((result, e) => {
            return result + this.processElement(e, elements);
        }, '');
    }

    findChild(parent, elements) {
        const children = parent.children ? parent.children : [];
        return children.map(id => {
            return elements.find(e => e.id === id);
        });
    }
}

class Calculator {
    constructor() {
        this.height = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => addition(e.position.y, e.sizeHeight),
            [GEOMETRIC_MODEL.ELLIPSE]: e => addition(e.position.y, e.sizeHeight),
            [GEOMETRIC_MODEL.LINE]: e => max([e.startPoint.y, e.endPoint.y]),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => addition(e.position.y, e.sizeHeight),
            [GEOMETRIC_MODEL.POLYGON]: e => max(e.points, point => point.y),
            [GEOMETRIC_MODEL.POLY_LINE]: e => max(e.points, point => point.y),
            [GEOMETRIC_MODEL.TEXT]: e => addition(e.position.y, e.sizeHeight),
        };
        this.width = {
            [GEOMETRIC_MODEL.RECTANGLE]: e => addition(e.position.x, e.sizeWidth),
            [GEOMETRIC_MODEL.ELLIPSE]: e => addition(e.position.x, e.sizeWidth),
            [GEOMETRIC_MODEL.LINE]: e => max([e.startPoint.x, e.endPoint.x]),
            [GEOMETRIC_MODEL.ROUND_RECT]: e => addition(e.position.x, e.sizeWidth),
            [GEOMETRIC_MODEL.POLYGON]: e => max(e.points, point => point.x),
            [GEOMETRIC_MODEL.POLY_LINE]: e => max(e.points, point => point.x),
            [GEOMETRIC_MODEL.TEXT]: e => addition(e.position.x, e.sizeWidth),
        };
    }

    calculateSizeHeight(elements) {
        const height = this.calculateHeight(elements);
        const width = this.calculateWidth(elements);
        return this.calculateSize(height, width);
    }

    calculateSizeWidth(elements) {
        const height = this.calculateHeight(elements);
        const width = this.calculateWidth(elements);
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

    calculateHeight(elements) {
        return findTop(elements).reduce((max, e) => {
            const value = this.height[e.type] ? this.height[e.type](e) : 0;
            return Math.max(max, value);
        }, 0);
    }

    calculateWidth(elements) {
        return findTop(elements).reduce((r, e) => {
            const value = this.width[e.type] ? this.width[e.type](e) : 0;
            return Math.max(r, value);
        }, 0);
    }
}

class AttrBuilder {
    constructor() {
        this.calculator = new Calculator();
        this.createMapper = {
            [GEOMETRIC_MODEL.RECTANGLE]: (e, a) => this.createRectangle(e, a),
            [GEOMETRIC_MODEL.ELLIPSE]: (e, a) => this.createEllipse(e, a),
            [GEOMETRIC_MODEL.LINE]: this.createLine,
            [GEOMETRIC_MODEL.ROUND_RECT]: (e, a) => this.createRoundRect(e, a),
            [GEOMETRIC_MODEL.POLYGON]: (e, a) => this.createPolygon(e, a),
            [GEOMETRIC_MODEL.POLY_LINE]: (e, a) => this.createPolygon(e, a),
            [GEOMETRIC_MODEL.TEXT]: (e, a) => this.createText(e, a),
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

    create(elements) {
        return elements.reduce((result, e) => {
            result[e.id] = this.processElement(e, elements);
            return result;
        }, this.createDefaultAttr(elements));
    }

    processElement(element, elements) {
        if (this.createMapper[element.type]) {
            return this.createMapper[element.type](element, elements);
        }
        throw new Error(`Unknown geometric model: ${element.type}`);
    }

    createRectangle(element, elements) {
        return {
            x: element.position.x + this.getParentPositionX(element, elements),
            y: element.position.y + this.getParentPositionY(element, elements),
            width: element.sizeWidth,
            height: element.sizeHeight,
        };
    }

    createEllipse(element, elements) {
        const rx = element.sizeWidth / 2;
        const ry = element.sizeHeight / 2;
        return {
            cx: rx + element.position.x + this.getParentPositionX(element, elements),
            cy: ry + element.position.y + this.getParentPositionY(element, elements),
            rx, // horizontal-radius
            ry, // vertical-radius
        };
    }

    createLine(element) {
        return {
            x1: element.startPoint.x,
            y1: element.startPoint.y,
            x2: element.endPoint.x,
            y2: element.endPoint.y,
        };
    }

    createRoundRect(element, elements) {
        return {
            x: element.position.x + this.getParentPositionX(element, elements),
            y: element.position.y + this.getParentPositionY(element, elements),
            width: element.sizeWidth,
            height: element.sizeHeight,
            rx: element.curveWidth,
            ry: element.curveHeight,
        };
    }

    createPolygon(element, elements) {
        const parentX = this.getParentPositionX(element, elements);
        const parentY = this.getParentPositionY(element, elements);
        return {
            points: element.points.reduce((string, point) => {
                const content = `${point.x + parentX},${point.y + parentY} `;
                return string + content;
            }, ''),
        };
    }

    createText(element, elements) {
        return {
            x: element.position.x + this.getParentPositionX(element, elements),
            y: element.position.y + this.getParentPositionY(element, elements),
            id: element.id,
            width: element.sizeWidth,
            height: element.sizeHeight,
            text: element.textBody,
        };
    }

    getParentPositionX(element, elements) {
        return this.getParentPosition(element, elements, this.xMapper);
    }

    getParentPositionY(element, elements) {
        return this.getParentPosition(element, elements, this.yMapper);;
    }

    getParentPosition(element, elements, mapper) {
        const parent = elements.find(e => e.id === element.parent);
        if (parent && mapper[parent.type]) {
            return mapper[parent.type](parent) + this.getParentPositionX(parent, elements);
        }
        return 0;
    }

    createDefaultAttr(elements) {
        return {
            'rect.bounding-box': {
                height: this.calculator.calculateHeight(elements),
                width: this.calculator.calculateWidth(elements),
            },
        };
    }
}

class ShapeGenerator {
    constructor() {
        this.svg = new SvgBuilder();
        this.calculator = new Calculator();
        this.attr = new AttrBuilder();
    }

    create(model) {
        const attributes = this.createAttributes(model);
        return joint.shapes.basic.Generic.extend(attributes);
    }

    createAttributes(model) {
        const elements = model.elements ? model.elements : [];
        const defaultsAttribute = this.buildDefaults(model, elements);
        const elementDefaults = joint.dia.Element.prototype.defaults;
        return {
            markup: this.svg.create(elements),
            defaults: joint.util.deepSupplement(defaultsAttribute, elementDefaults),
        }
    }

    buildDefaults(model, elements) {
        return Object.assign(
            this.createMandatoryDefaults(model, elements),
            this.createOptionalSizeMaxAttribute(model),
            this.createOptionalSizeMinAttribute(model)
        );
    }

    createMandatoryDefaults(model, elements) {
        return {
            type: `zeta.${model.name}`,
            'init-size': {
                height: this.calculator.calculateHeight(elements),
                width: this.calculator.calculateWidth(elements),
            },
            size: {
                height: this.calculator.calculateSizeHeight(elements),
                width: this.calculator.calculateSizeWidth(elements),
            },
            resize: {
                horizontal: this.getBoolean(model.stretchingHorizontal),
                vertical: this.getBoolean(model.stretchingVertical),
                proportional: this.getBoolean(model.proportional),
            },
            attr: this.attr.create(elements),
            compartments: [],
        }
    }

    getBoolean(value) {
        return value === undefined ? true : new Boolean(value);
    }

    createOptionalSizeMaxAttribute(model) {
        return model.sizeHeightMax && model.sizeWidthMax ? {
            'size-max': {
                height: new Number(model.sizeHeightMax).valueOf(),
                width: new Number(model.sizeWidthMax).valueOf(),
            }
        } : {};
    }

    createOptionalSizeMinAttribute(model) {
        return model.sizeHeightMin && model.sizeWidthMin ? {
            'size-min': {
                height: new Number(model.sizeHeightMin).valueOf(),
                width: new Number(model.sizeWidthMin).valueOf(),
            }
        } : {};
    }
}

export default class {
    constructor(shape) {
        this.shapes = shape.shapes ? shape.shapes : [];
        this.generator = new ShapeGenerator();
        this.calculator = new Calculator();
    }

    get zeta() {
        return this.shapes.reduce((result, shape) => {
            result[shape.name] = this.generator.create(shape);
            return result;
        }, {});
    }

    calculateHeight(shape) {
        const elements = shape.elements ? shape.elements : [];
        return this.calculator.calculateHeight(elements);
    }

    calculateWidth(shape) {
        const elements = shape.elements ? shape.elements : [];
        return this.calculator.calculateWidth(elements);
    }
}