import * as joint from 'jointjs';

import StyleGenerator from './style/StyleGenerator'
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
        console.error("The GeneratorFactory needs to be initialized before getting any generator");
    }
}

function createGenerators(styleData, diagramData, shapeData, conceptData) {

    const style = new StyleGenerator(styleData);
    const shapeStyle = new ShapeStyleGenerator(shapeData, style); // TODO ShapeStyleGenerator is only partially updated to V2
    const shapeDefinition = new ShapeDefinitionGenerator(shapeData, shapeStyle);
    const connectionDefinition = new ConnectionDefinitionGenerator(shapeData, style);

    Object.assign(joint.shapes.zeta, shapeDefinition.zeta);

    const stencil = new StencilGenerator(diagramData, shapeData, conceptData, shapeStyle, style); // TODO update StencilGenerator to V2
    const inspector = new InspectorGenerator(shapeData, shapeDefinition); // TODO update InspectorGenerator to V2
    const linkHelper = new LinkHelperGenerator(diagramData); // TODO update LinkHelperGenerator to V2
    const validator = new ValidatorGenerator(shapeData, conceptData); // TODO update ValidatorGenerator to V2

    generators = {
        style,
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

            const metaModelId = window._global_graph_type;
            const credentials = {credentials: 'same-origin'};

            Promise.all([
                fetch(`/rest/v2/meta-models/${metaModelId}/style`, credentials).then(r => r.json()),
                fetch(`/rest/v2/meta-models/${metaModelId}/diagram`, credentials).then(r => r.json()),
                fetch(`/rest/v2/meta-models/${metaModelId}/shape`, credentials).then(r => r.json()),
                fetch(`/rest/v1/meta-models/${metaModelId}`, credentials).then(r => r.json()),
            ]).then(([style, diagram, shape, concept]) => {
                createGenerators(style['styles'], diagram, shape, concept['concept']);
                resolve();
            }).catch(error => {
                console.error(`Error fetching Rest-API: ${error}`);
                reject(error);
            });

        });
    }

    static get style() {
        checkInitialized();
        return generators.style;
    }

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
