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

    createElement(element) {
        const style = this.styleGenerator.getStyle(element.style);
        if (this.mapper[element.type] && element.style) {
            return this.mapper[element.type](element, style);
        } else {
            return this.mapper[element.type](element, 'default-style');
        }
    }

    createLine(element, style) {
        return {[`line.${element.id}`]: style};
    }

    createRectangle(element, style) {
        return {[`rect.${element.id}`]: style};
    }

    createEllipse(element, style) {
        return {[`ellipse.${element.id}`]: style};
    }

    createStaticText(element, style) {
        return {
            [`text.${element.id}`]: style
        };
    }

    createPolygon(element, style) {
        return {[`polygon.${element.id}`]: style};
    }

    createPolyLine(element, style) {
        return {[`polyline.${element.id}`]: style};
    }
}

export default class {

    constructor(shape, styleGenerator) {
        this.shapes = shape.nodes ? shape.nodes : {};
        this.generator = new Generator(styleGenerator);
    }

    checkIfShapeExist() {
        if(Object.keys(this.shapes).length === 0) {
            return false;
        }
        return true
    }

    checkForGeoElements(geoElements) {
        if(!(Object.keys(geoElements).length === 0)){
            const objects = this.checkForStyle(geoElements);
            return objects;
        }
        return {};
    }

    getElementsFromShape(shape) {
        if(('geoElements' in shape)){
            return this.checkForGeoElements(shape.geoElements);
        }
        return {};
    }

    checkForStyle(geoElements) {
        //todo refactor
        var error = false;
        var self = this;
        var obj = {}

        if (Array.isArray(geoElements)) {

            geoElements.forEach(element => {
                if (!('style' in element)) {
                    error = true;
                }
            });

            geoElements.forEach(element => {
                var tmp;
                var key;
                var value;

                if (element.childGeoElements) {
                    element.childGeoElements.forEach(e => {
                        tmp = self.generator.createElement(e);
                        key = Object.keys(tmp)[0];
                        value = Object.values(tmp)[0];
                        obj[key] = value;
                    })
                }

                if (!("style" in element))
                    return {};
                tmp = self.generator.createElement(element);

                for (var i = 0; i < Object.keys(tmp).length; i++) {
                    key = Object.keys(tmp)[i];
                    value = Object.values(tmp)[i];
                    obj[key] = value;
                }
            });

            if (error)
                return {};

            return obj;
        }

        return {};

    }

    getShapeStyle(shapeName) {
        if(this.checkIfShapeExist()){
            let shape = this.shapes.find(e => e.name === shapeName);
            return this.getElementsFromShape(shape);
        } else {
            return {};
        }
    }
}