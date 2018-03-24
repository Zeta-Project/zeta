import StyleGenerator from './style/StyleGenerator'
import DiagramGenerator from './diagram/DiagramGenerator'

import InspectorGenerator from './editor/InspectorGenerator'
import LinkHelperGenerator from './editor/LinkHelperGenerator'
import StencilGenerator from './editor/StencilGenerator'
import ValidatorGenerator from './editor/ValidatorGenerator'

import ShapeDefinitionGenerator from './shape/ShapeDefinitionGenerator'
import ShapeStyleGenerator from './shape/ShapeStyleGenerator'
import ConnectionDefinitionGenerator from './shape/connectionDefinitionGenerator/ConnectionDefinitionGenerator'

let generators = null;

function checkInitialized() {
    if (generators === null) {
        alert("The GeneratorFactory needs to be initialized before getting any generator")
    }
}

function createGenerators(diagramData, styleData, shapeData, conceptData) {

    const style = new StyleGenerator(styleData);
    // const diagram = new DiagramGenerator(diagram), // TODO the diagramGenerator is currently not implemented
    const shapeDefinition = new ShapeStyleGenerator(shapeData, style);
    const shapeStyle = new ShapeStyleGenerator(shapeData, style);
    const connectionDefinition = new ConnectionDefinitionGenerator(shapeData, style);

    const inspector = new InspectorGenerator(shapeData, shapeDefinition);
    const linkHelper = new LinkHelperGenerator(diagramData);
    const stencil = new StencilGenerator(diagramData, conceptData, shapeStyle, style);
    const validator = new ValidatorGenerator(conceptData, diagramData);

    generators = {
        style,
        // diagram, // TODO the diagramGenerator is currently not implemented
        shapeDefinition,
        shapeStyle,
        connectionDefinition,

        inspector,
        linkHelper,
        stencil,
        validator
    };
}

export default class GeneratorFactory {

    static initialize() {
        return new Promise((resolve, reject) => {
            fetch('/rest/v1/totalDsl/' + window._global_graph_type, {
                credentials: 'same-origin'
            }).then(response => {
                return response.json()
            }).then(data => {
                createGenerators(data["diagram"], data["style"]["styles"], data["shape"], data["concept"]);
                resolve();
            }).catch(error => {
                console.log('Error fetching Rest-API');
                console.log('Error-Msg: ' + error);
                reject(error);
            });
        });
    }

    static get style() {
        checkInitialized();
        return generators.style;
    }

    /* TODO the diagramGenerator is currently not implemented
    static get diagram() {
        checkInitialized();
        return generators.diagram;
    }*/

    static get shapeDefinition() {
        checkInitialized();
        return generators.shapeDefinition;
    }

    static get shapeStyle() {
        checkInitialized();
        return generators.shapeStyle;
    }

    static get connectionDefinition() {
        checkInitialized();
        return generators.connectionDefinition;
    }

    static get inspector() {
        checkInitialized();
        return generators.inspector;
    }

    static get linkHelper() {
        checkInitialized();
        return generators.linkHelper;
    }

    static get stencil() {
        checkInitialized();
        return generators.stencil;
    }

    static get validator() {
        checkInitialized();
        return generators.validator;
    }

}
