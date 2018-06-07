import joint from 'jointjs';

function getPalettes(diagrams) {
    //ToDo: multiDiagram impl. not yet.
    return diagrams[0].palettes.map( palette => palette.name);
}

function getVarName(string) {
    string = string.replace(new RegExp("\\W", "g"), '');
    return string.charAt(0).toLowerCase() + string.slice(1);
}

const stringToTypeMapper = {
    'String': 'StringType',
    'Boolean': 'BoolType',
    'Int': 'IntType',
    'Double': 'DoubleType',
    'Unit': 'UnitType',
};

class ShapesGenerator {

    constructor(shapeStyleGenerator) {
        this.shapeStyleGenerator = shapeStyleGenerator;
    }

    create(nodes, classes, diagrams) {
        return getPalettes(diagrams).reduce((result, palette) => {
            result[getVarName(palette)] = this.createShapeList(palette, nodes, classes, diagrams);
            return result;
        }, {});
    }

    createShapeList(palette, nodes, classes, diagrams) {
        const paletteNodes = diagrams[0].palettes.find(p => p.name === palette).nodes;
        const shapeNodes = nodes.filter( n => paletteNodes.includes(n.name));
        return shapeNodes.map(node => this.createShapeEntry(node, classes));
        /*this.createShapeEntry()

        return nodes.filter(n => n.palette === palette)
            .map(node => this.createShapeEntry(node, classes));*/
    }

    createShapeEntry(node, classes) {
        const shapeName = node.name.replace(new RegExp("\\W", "g"), '');
        const attributes = this.createShapeAttributes(node, classes);
        const shape = new joint.shapes.zeta[shapeName](attributes);
        this.setShapeAttributes(shape, shapeName);
        return shape;
    }

    createShapeAttributes(node, classes) {
        return Object.assign(
            {
                nodeName: node.name,
                mClass: node.mClass,
                mClassAttributeInfo: this.createMClassAttributeInfo(node, classes),
            },
            this.createOptionalMcoreAttributes(node),
        );
    }

    createMClassAttributeInfo(node, classes) {
        const mClass = classes.find(c => c.name === node.conceptElement);
        const attributeInfo =  mClass && mClass.attributes ? mClass.attributes.map(a => this.createMClassAttribute(a, node)) : [];
        globalMClassAttributeInfo = globalMClassAttributeInfo.concat(attributeInfo);
        return attributeInfo;
    }

    createMClassAttribute(attribute, node) {
        return Object.assign(
            {
                name: attribute.name,
                type: stringToTypeMapper[attribute.typ] ? stringToTypeMapper[attribute.typ] : null,
            },
            this.createMClassAttributeText(attribute, node),
        );
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

    createMClassAttributeText(attribute, node) {
        const vars = node.geoElements ? node.geoElements : [];
        const flatGeoElements = this.flattenGeoElement(vars);
        const entry = flatGeoElements.find(v => v.identifier === attribute.name);
        return entry ? { id: entry.id } : {};
    }

    createOptionalMcoreAttributes(node) {
        return node.onCreate && node.onCreate.askFor ? {
            mcoreAttributes: [
                this.createMcoreAttribute(node.onCreate.askFor),
            ]
        } : {};
    }

    createMcoreAttribute(name) {
        return {
            mcore: name,
            cellPath: ['attrs', '.label', 'text'],
        };
    }

    setShapeAttributes(shape, shapeName) {
        let shapeAttributes = this.shapeStyleGenerator.getShapeStyle(shapeName);
        const filteredText = Object.keys(shapeAttributes).filter((shapeId) => shapeId.includes("text")).reduce((obj, key) => {
            obj[key] = shapeAttributes[key];
            return obj;
        }, {});

        Object.keys(filteredText).forEach((id) => {
            let typelesId = `.${id.split(".")[1]}`;

            let helper = Object.assign({}, shapeAttributes[id].text);
            // ToDo possible bug in Inspector
            // shape.attributes.attrs[typelesId].text = [shape.attributes.attrs[typelesId].text];
            shapeAttributes[id] = Object.assign(
                shapeAttributes[id].text,
                shape.attributes.attrs[typelesId]
            );
            shape.attributes.attrs[typelesId] = helper;
        });

        const regex = new RegExp('\\w+\\.(\\w+|\\-)+', 'g');

        const typeAttrs = Object.keys(shapeAttributes).filter((shapeId) => shapeId.match(regex)).reduce((obj, key) => {
            obj[key] = shapeAttributes[key];
            return obj;
        }, {});

        Object.assign(shape.attributes.attrs, typeAttrs);
    }
}

/**
 * 
 */
export default class StencilGenerator {
    constructor(diagram, shape, concept, shapeStyleGenerator, styleGenerator) {
        this.diagrams = diagram.diagrams;
        this.shape = shape;
        this.concept = concept;
        this.nodes = shape?.nodes || [];
        this.shapesGenerator = new ShapesGenerator(shapeStyleGenerator);
        this.styleGenerator = styleGenerator;
    }

    get groups() {
        return getPalettes(this.diagrams).reduce((result, palette, i) => {
            const key = getVarName(palette);
            result[key] = { index: i + 1, label: palette };
            return result;
        }, {});
    }

    get shapes() {
        const classes = this.concept && this.concept.classes ? this.concept.classes : [];
        return this.shapesGenerator.create(this.nodes, classes, this.diagrams);
    }
}