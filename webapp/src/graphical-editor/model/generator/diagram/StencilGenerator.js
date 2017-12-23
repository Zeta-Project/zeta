function createGroups(diagram) {
    let i = 1;
    return diagram.model.nodes.reduce((result, node) => {
        const key = getVarName(node.palette);
        if (!result[key]) {
            result[key] = { index: i++, label: node.palette };
        }
        return result;
    }, {});
}

function getVarName(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

export default class StencilGenerator {
    constructor(diagram) {
        this.diagram = diagram;
    }

    get groups() {
        return this.diagram.model && this.diagram.model.nodes ? createGroups(this.diagram) : {};
    }
}