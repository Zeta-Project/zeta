export function getRestJsonByType(dslType) {

    let protocol = location.protocol;
    let slashes = '//';
    let slash = '/';
    let host = window.location.hostname;
    let port = ':'.concat(window.location.port);
    let restV1call = '/rest/v1/dsl/';
    let modelId = window._global_graph_type;

    let url = protocol.concat(slashes).concat(host).concat(port).concat(restV1call).concat(modelId).concat(slash).concat(dslType);

    fetch(url, {
        credentials: 'same-origin'
    })
        .then(function (response) {
            return response.json()
        }).then(function (data) {
        console.log(data)
    })
        .catch(function (error) {
            console.log('Error fetching Rest-API for ' + dslType);
            console.log('Error-Msg: ' + error);
        });
}