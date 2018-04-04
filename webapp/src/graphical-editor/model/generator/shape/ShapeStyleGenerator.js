class Generator{
    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        this.mapper = {
            'line': (element, style) => this.createLine(element, style),
            'roundedRectangle': (element, style) => this.createRectangle(element, style),
            'rectangle': (element, style) => this.createRectangle(element, style),
            'ellipse': (element, style) => this.createEllipse(element, style),
            'textfield': (element, style) => this.createTextField(element, style),
            'polygon': (element, style) => this.createPolygon(element, style),
            'polyLine': (element, style) => this.createPolyLine(element, style),
        };
    }

    createShapeStyle(shape) {
        const elements = shape?.geoElements || [];
        const flatGeoElements = this.flattenGeoElement(elements);
        return this.findTop(flatGeoElements).reduce((result, element) => {
            return this.processElement(result, element, flatGeoElements);
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
        const children = parent.childGeoElements ? parent.childGeoElements : [];
        return children.map(child => {
            return elements.find(e => e.id === child.id);
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

    createTextField(element, style) {
        return {
            [`text.${element.id}`]:  style,
            [`.${element.id}`]:  style
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
        this.nodes = shape.nodes ? shape.nodes : [];
        this.generator = new Generator(styleGenerator);
    }

    getShapeByName(shapeName) {
        if (this.nodes) {
            return this.nodes.find(e => e.name === shapeName);
        } else {
            return [];
        }

    }

    getShapeStyle(shapeName) {
        //todo: checker functions nostyle, no geoelements etc
        const shape = this.getShapeByName(shapeName);
        if (shape?.geoElements) {
            //if ('style' in shape.geoElements[0]) {
            return shape ? this.generator.createShapeStyle(shape) : {};
            //}
        } else
            return {};

    }
}