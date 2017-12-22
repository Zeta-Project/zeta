function findEdge(diagram, name) {
    if (diagram && diagram.model && diagram.model.edges) {
        return diagram.model.edges.find(e => e.name === name);
    }
    return null;
}

function getEdgeVal(edge, key) {
    const val = findVal(edge, key);
    return val ? val.value : '';
}

function findVal(edge, key) {
    if (edge && edge.connection && edge.connection.vals) {
        return edge.connection.vals.find(v => v.key === key);
    }
    return null;
}

/**
 * Generator of linkhelper for JointJS to map Reference attribute to
 * textbox and resolve label text
 */
export default class LinkHelperGenerator {

    constructor(diagram) {
        this.diagram = diagram;
    }

    getLabelText(edgeName, textId) {
        const edge = findEdge(this.diagram, edgeName);
        return getEdgeVal(edge, textId);
    }
}