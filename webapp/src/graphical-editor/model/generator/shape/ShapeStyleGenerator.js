class Generator {
    constructor(styleGenerator) {
        this.styleGenerator = styleGenerator;
        this.mapper = {
            'line': (element, style) => this.createLine(element, style),
            'roundedRectangle': (element, style) => this.createRectangle(element, style),
            'rectangle': (element, style) => this.createRectangle(element, style),
            'ellipse': (element, style) => this.createEllipse(element, style),
            'statictext': (element, style) => this.createStaticText(element, style),
            'polygon': (element, style) => this.createPolygon(element, style),
            'polyline': (element, style) => this.createPolyLine(element, style),
        };
    }


    createShapeStyle(shape) {
        if (shape.geoElements.length > 0) {
            const geoElements = shape.geoElements;
            if ('style' in geoElements[0]) {
                var testo = this.createElement(geoElements[0]);
                return testo;
            }
            else
                return {};


        }

        return [];
    }

    findTop(elements) {
        return elements.filter(e => e.parent === undefined || e.parent === null);
    }

    checkForChildGeoElements(geoElement) {
        var liste = [];
        if ('childGeoElements' in geoElement) {
            geoElement.childGeoElements.forEach(function (child) {
                var m = child.type + "." + child.id;
                liste.push(Object.assign({[m]: 'style-definition'}));
            });
            return liste;
        }
    }

        processElement(result, element, elements)
        {
            var m = element.geoElements[0].type + "." + element.geoElements[0].id;
            var object = this.checkForChildGeoElements(element.geoElements[0]);
            // here function create object with single or multiple geomodels
            return Object.assign({[m]: 'style-definition'});
        }

        createElement(element)
        {
            const style = this.styleGenerator.getStyle(element.style);
            if (this.mapper[element.type] && element.style) {
                return this.mapper[element.type](element, style);
            } else {
                return this.mapper[element.type](element, 'default-style');
            }
        }

        processChildren(parent, elements)
        {
            return this.findChildren(parent, elements).reduce((result, element) => {
                return this.processElement(result, element, elements);
            }, {});
        }

        findChildren(parent, elements)
        {
            const children = parent.children ? parent.children : [];
            return children.map(id => {
                return elements.find(e => e.id === id);
            });
        }

        createLine(element, style)
        {
            return {[`line.${element.id}`]: style};
        }

        createRectangle(element, style)
        {
            return {[`rect.${element.id}`]: style};
        }

        createEllipse(element, style)
        {
            return {[`ellipse.${element.id}`]: style};
        }

        createStaticText(element, style)
        {
            return {
                [`text.${element.id}`]: style,
                [`.${element.id}`]: style,
            };
        }

        createPolygon(element, style)
        {
            return {[`polygon.${element.id}`]: style};
        }

        createPolyLine(element, style)
        {
            return {[`polyline.${element.id}`]: style};
        }
    }

    export
    default
    class {
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
        if ('geoElements' in shape) {
            //if ('style' in shape.geoElements[0]) {
            return shape ? this.generator.createShapeStyle(shape) : {};
            //}
        } else
            return {};

    }
}