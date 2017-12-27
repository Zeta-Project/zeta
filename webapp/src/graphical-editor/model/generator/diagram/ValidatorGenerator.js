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
    constructor(metaModel) {
        this.model = metaModel;
        this.matrix = new MatrixGenerator();
    }

    get inputMatrix() {
        return this.model.classes ? this.matrix.generateInput(this.model.classes) : {};
    }

    get outputMatrix() {
        return this.model.classes ? this.matrix.generateOuput(this.model.classes) : {};
    }
}