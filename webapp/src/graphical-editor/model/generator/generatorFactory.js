

import StyleGenerator from './style/StyleGenerator'

export default class GeneratorFactory {

    constructor(callback) {
        this.state = {
            styleGenerator : null
        };
        this.getAllRestJson();
    }


    createGenerator(diagram, style, shape) {
        this.state.styleGenerator = new StyleGenerator(style["styles"]);
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
            this.createGenerator(data["diagram"]["diagrams"], data["style"]["styles"], data["shape"]["shapes"]);
        }).catch(function (error) {
            console.log('Error fetching Rest-API');
            console.log('Error-Msg: ' + error);
        });
    }


}

