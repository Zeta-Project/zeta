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

function createMappingEdges(diagram) {
    const mapper = {};
    diagram.model.edges.forEach(edge => {
        const attributes = edge.connection && edge.connection.vars ? createMappingAttribute(edge) : {};
        mapper[edge.mReference] = attributes;
    });
    return mapper;
}

function createMappingAttribute(edge) {
    const mapper = {};
    edge.connection.vars.forEach(entry => {
        mapper[entry.key] = entry.value;
    });
    return mapper;
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

    get mapping() {
        const condition = this.diagram && this.diagram.model && this.diagram.model.edges;
        return condition ? createMappingEdges(this.diagram) : {};
    }
}