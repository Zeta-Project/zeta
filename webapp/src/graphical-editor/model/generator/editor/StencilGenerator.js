import joint from 'jointjs';

function getPalettes(nodes) {
    const palettes = nodes.reduce((result, node) => {
        result[node.palette] = true;
        return result;
    }, {});
    return Object.keys(palettes);
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

    create(nodes, classes) {
        return getPalettes(nodes).reduce((result, palette) => {
            result[getVarName(palette)] = this.createShapeList(palette, nodes, classes);
            return result;
        }, {});
    }

    createShapeList(palette, nodes, classes) {
        return nodes.filter(n => n.palette === palette)
            .map(node => this.createShapeEntry(node, classes));
    }

    createShapeEntry(node, classes) {
        const shapeName = node.shape.name.replace(new RegExp("\\W", "g"), '');
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
        const mClass = classes.find(c => c.name === node.mClass);
        return mClass && mClass.attributes ? mClass.attributes.map(a => this.createMClassAttribute(a, node)) : [];
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

    createMClassAttributeText(attribute, node) {
        const vars = node.shape.vars ? node.shape.vars : [];
        const entry = vars.find(v => v.value === attribute.name);
        return entry ? { id: entry.key } : {};
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
            shape.attributes.attrs[typelesId].text = [shape.attributes.attrs[typelesId].text];
            shapeAttributes[id] = Object.assign(
                shapeAttributes[id].text,
                shape.attributes.attrs[typelesId]
            );
            shape.attributes.attrs[typelesId] = helper;
        });

        Object.assign(shape.attributes.attrs, shapeAttributes);
    }
}

/**
 * 
 */
export default class StencilGenerator {
    constructor(diagram, concept, shapeStyleGenerator, styleGenerator) {
        this.diagram = diagram;
        this.concept = concept;
        this.nodes = diagram.model && diagram.model.nodes ? diagram.model.nodes : [];
        this.shapesGenerator = new ShapesGenerator(shapeStyleGenerator);
        this.styleGenerator = styleGenerator;
    }

    get groups() {
        return getPalettes(this.nodes).reduce((result, palette, i) => {
            const key = getVarName(palette);
            result[key] = { index: i + 1, label: palette };
            return result;
        }, {});
    }

    get shapes() {
        const classes = this.concept && this.concept.classes ? this.concept.classes : [];
        return this.shapesGenerator.create(this.nodes, classes);
    }

    addStyleElementToDocument() {
        if (this.diagram.model && this.diagram.model.style) {
            const style = document.createElement('style');
            style.id = 'highlighting-style';
            style.type = 'text/css';
            style.innerHTML = this.styleGenerator.getDiagramHighlighting("${style.name}");
            document.getElementsByTagName('head')[0].appendChild(style);
        }
    }
}