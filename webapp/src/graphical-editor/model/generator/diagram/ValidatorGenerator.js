class MatrixGenerator {
    create(references) {
        return references.reduce((result, reference) => {
            result[reference.type] = this.createBounds(reference);
            return result;
        }, {});
    }

    createBounds({lowerBound, upperBound }) {
        return { lowerBound, upperBound };
    }
}

class EdgeGenerator {
    constructor(edges, classes) {
        this.edges = edges;
        this.classes = classes;
    }

    getValid(sourceNode, targetNode) {
        const sourceEdges = this.filterSourceEdges(sourceNode).map(e => e.name);
        const targetEdges = this.filterTargetEdges(targetNode).map(e => e.name);
        return sourceEdges.filter(e => targetEdges.includes(e));
    }

    filterSourceEdges(node) {
        const superTypes = this.getSuperTypes(node);
        return this.edges.filter(e => e.from === node.mClass || superTypes.includes(e.from));
    }

    filterTargetEdges(node) {
        const superTypes = this.getSuperTypes(node);
        return this.edges.filter(e => e.to === node.mClass || superTypes.includes(e.to));
    }

    getSuperTypes(node) {
        const nodeClass = this.classes.find(c => c.name === node.mClass);
        return nodeClass && nodeClass.superTypeNames ? nodeClass.superTypeNames : [];
    }
}

export default class {
    constructor(metaModel, diagram) {
        this.classes = metaModel.classes ? metaModel.classes : [];
        this.edges = diagram.model && diagram.model.edges ? diagram.model.edges : [];
        this.nodes = diagram.model && diagram.model.nodes ? diagram.model.nodes : [];
        this.matrix = new MatrixGenerator();
        this.validEdges = new EdgeGenerator(this.edges, this.classes);
    }

    get inputMatrix() {
        return this.classes.reduce((result, node) => {
            result[node.name] = node.inputs ? this.matrix.create(node.inputs) : {};
            return result;
        }, {});
    }

    get outputMatrix() {
        return this.classes.reduce((result, node) => {
            result[node.name] = node.outputs ? this.matrix.create(node.outputs) : {};
            return result;
        }, {});
    }

    getEdgeData(edgeName) {
        const edge = this.edges.find(e => e.name === edgeName);
        if (edge) {
            return {
                type: edge.mReference,
                from: edge.from,
                to: edge.to,
                style: edge.connection.name,
            };
        }
    }

    getValidEdges(sourceName, targetName) {
        const source = this.nodes.find(n => n.name === sourceName);
        const target = this.nodes.find(n => n.name === targetName);
        return source && target ? this.validEdges.getValid(source, target) : [];
    }
}