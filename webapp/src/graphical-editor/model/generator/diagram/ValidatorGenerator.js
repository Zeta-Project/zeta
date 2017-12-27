class MatrixGenerator {

    generateInput(classes) {
        return classes.reduce((result, node) => {
            result[node.name] = node.inputs ? this.createMatrix(node.inputs) : {};
            return result;
        }, {});
    }

    createMatrix(references) {
        return references.reduce((result, reference) => {
            result[reference.type] = this.createBounds(reference);
            return result;
        }, {});
    }

    createBounds({lowerBound, upperBound }) {
        return { lowerBound, upperBound };
    }

    generateOuput(classes) {
        return classes.reduce((result, node) => {
            result[node.name] = node.outputs ? this.createMatrix(node.outputs) : {};
            return result;
        }, {});
    }
}

export default class {
    constructor(metaModel, diagram) {
        this.model = metaModel;
        this.diagram = diagram;
        this.matrix = new MatrixGenerator();
    }

    get inputMatrix() {
        return this.model.classes ? this.matrix.generateInput(this.model.classes) : {};
    }

    get outputMatrix() {
        return this.model.classes ? this.matrix.generateOuput(this.model.classes) : {};
    }

    getEdgeData(edgeName) {
        if (this.diagram.model && this.diagram.model.edges) {
            const edge = this.diagram.model.edges.find(e => e.name === edgeName);
            if (edge) {
                return {
                    type: edge.mReference,
                    from: edge.from,
                    to: edge.to,
                    style: edge.connection.name,
                };
            }
        }
    }
}