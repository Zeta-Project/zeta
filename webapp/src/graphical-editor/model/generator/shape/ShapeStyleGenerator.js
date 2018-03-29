class Generator{
    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        this.mapper = {
            'line': (element, style) => this.createLine(element, style),
            'roundedRectangle': (element, style) => this.createRectangle(element, style),
            'rectangle': (element, style) => this.createRectangle(element, style),
            'ellipse': (element, style) => this.createEllipse(element, style),
            'text': (element, style) => this.createText(element, style),
            'polygon': (element, style) => this.createPolygon(element, style),
            'polyLine': (element, style) => this.createPolyLine(element, style),
        };
    }

    createShapeStyle(shape) {
        const elements = shape.elements ? shape.elements : [];
        return this.findTop(elements).reduce((result, element) => {
            return this.processElement(result, element, elements);
        }, {});
    }

    findTop(elements) { 
        return elements.filter(e => e.parent === undefined || e.parent === null);
    }

    processElement(result, element, elements) {
        const entry = element.style ? this.createElement(element) : {};
        const children = this.processChildren(element, elements);
        return Object.assign(result, entry, children);
    }

    createElement(element) {
        const style = this.styleGenerator.getStyle(element.style);
        return this.mapper[element.type] ? this.mapper[element.type](element, style) : {};
    }

    processChildren(parent, elements) {
        return this.findChildren(parent, elements).reduce((result, element) => {
            return this.processElement(result, element, elements);
        }, {});
    }

    findChildren(parent, elements) {
        const children = parent.children ? parent.children : [];
        return children.map(id => {
            return elements.find(e => e.id === id);
        });
    }

    createLine(element, style) {
        return { [`line.${element.id}`]: style };
    }

    createRectangle(element, style) {
        return { [`rect.${element.id}`]: style };
    }

    createEllipse(element, style) {
        return { [`ellipse.${element.id}`]: style };
    }

    createText(element, style) {
        return { 
            [`text.${element.id}`]:  style
            //[`.${element.id}`]:  style,
        };
    }

    createPolygon(element, style) {
        return { [`polygon.${element.id}`]: style };
    }

    createPolyLine(element, style) {
        return { [`polyline.${element.id}`]: style };
    }
}

export default class {
    constructor(shape, styleGenerator) {
        this.shapes = shape.shapes ? shape.shapes : [];
        this.generator = new Generator(styleGenerator);
    }

    getShapeStyle(shapeName) {
        const shape = this.shapes.find(e => e.name === shapeName);
        return shape ? this.generator.createShapeStyle(shape) : {};
    }
}