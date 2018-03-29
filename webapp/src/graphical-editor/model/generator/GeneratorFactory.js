import joint from 'jointjs';

import StyleGenerator from './style/StyleGenerator'
import DiagramGenerator from './diagram/DiagramGenerator'

import InspectorGenerator from './editor/InspectorGenerator'
import LinkHelperGenerator from './editor/LinkHelperGenerator'
import StencilGenerator from './editor/StencilGenerator'
import ValidatorGenerator from './editor/ValidatorGenerator'

import ShapeDefinitionGenerator from './shape/ShapeDefinitionGenerator'
import ShapeStyleGenerator from './shape/ShapeStyleGenerator'
import ConnectionDefinitionGenerator from './shape/connectionDefinitionGenerator/ConnectionDefinitionGenerator'


export default class GeneratorFactory {

    constructor(callback) {
        this.callback = callback;
        this.state = {
            styleGenerator : null,
            diagramGenerator : null,
            shapeDefinitionGenerator : null,
            shapeStyleGenerator : null,
            connectionDefinitionGenerator : null,
            inspectorGenerator : null,
            linkHelperGenerator : null,
            stencilGenerator : null,
            validatorGenerator : null
        };
        this.getAllRestJson();
    }

    getAllRestJson() {

        let protocol = location.protocol;
        let slashes = '//';
        let slash = '/';
        let host = window.location.hostname;
        let port = ':'.concat(window.location.port);
        let restV1call = '/rest/v1/totalDsl/';
        let modelId = window._global_graph_type;

        let url = protocol.concat(slashes).concat(host).concat(port).concat(restV1call).concat(modelId);

        fetch(url, {
            credentials: 'same-origin'
        }).then(function (response) {
            return response.json()
        }).then(data => {
            this.createGenerator(data["diagram"], data["style"]["styles"], data["shape"], data["concept"]);
        }).catch(function (error) {
            console.log('Error fetching Rest-API');
            console.log('Error-Msg: ' + error);
        });
    }

    createGenerator(diagram, style, shape, concept) {
        this.state.styleGenerator = new StyleGenerator(style);
        //this.state.diagramGenerator = new DiagramGenerator(diagram);

        this.state.shapeStyleGenerator = new ShapeStyleGenerator(shape, this.state.styleGenerator);
        this.state.shapeDefinitionGenerator = new ShapeDefinitionGenerator(shape, this.state.shapeStyleGenerator);
        this.state.connectionDefinitionGenerator = new ConnectionDefinitionGenerator(shape, this.state.styleGenerator);

        Object.assign(joint.shapes.zeta, this.state.shapeDefinitionGenerator.zeta);

        this.state.inspectorGenerator = new InspectorGenerator(shape, this.state.shapeDefinitionGenerator);
        this.state.linkHelperGenerator = new LinkHelperGenerator(diagram);
        this.state.stencilGenerator = new StencilGenerator(diagram, concept, this.state.shapeStyleGenerator, this.state.styleGenerator);
        this.state.validatorGenerator = new ValidatorGenerator(concept, diagram);

        this.callback();
    }

    getConnectionDefinitionGenerator() {
        return this.state.connectionDefinitionGenerator;
    }
}



