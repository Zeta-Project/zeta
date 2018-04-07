class MatrixGenerator {
    create(references) {
        return references.reduce((result, reference) => {
            result[reference.type] = this.createBounds(reference);
            return result;
        }, {});
    }

    createBounds({lowerBound, upperBound}) {
        return {lowerBound, upperBound};
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
        return this.edges.filter(e => e.sourceClassName === node.conceptElement || superTypes.includes(e.sourceClassName));
    }

    filterTargetEdges(node) {
        const superTypes = this.getSuperTypes(node);
        return this.edges.filter(e => e.targetClassName === node.conceptElement || superTypes.includes(e.targetClassName));
    }

    getSuperTypes(node) {
        const nodeClass = this.classes.find(c => c.name === node.mClass);
        return nodeClass && nodeClass.superTypeNames ? nodeClass.superTypeNames : [];
    }
}


export default class {

    constructor(shape, concept) {
        this.nodes = shape.nodes || [];
        this.edges = shape.edges || [];
        this.classes = concept.classes || [];
        this.references = concept.references || [];
        this.matrix = new MatrixGenerator();
        this.validEdges = new EdgeGenerator(this.references, this.classes);
    }

    get inputMatrix() {
        return this.nodes.reduce((result, node) => {
            result[node.name] = node.inputs ? this.matrix.create(node.inputs) : {};
            return result;
        }, {});
    }

    get outputMatrix() {
        return this.nodes.reduce((result, node) => {
            result[node.name] = node.outputs ? this.matrix.create(node.outputs) : {};
            return result;
        }, {});
    }

    getEdgeData(edgeName) {
        const reference = this.references.find(e => e.name === edgeName);
        const edge = this.edges.find(e => e.name === edgeName); // TODO, it's currently undefined
        if (/*edge &&*/ reference) {
            return {
                type: edgeName, // TODO check if this is correct
                from: reference.sourceClassName,
                to: reference.targetClassName,
                style: edgeName // TODO find correct variable
            };
        }
    }

    getValidEdges(sourceName, targetName) {
        const source = this.nodes.find(n => n.name === sourceName);
        const target = this.nodes.find(n => n.name === targetName);
        return source && target ? this.validEdges.getValid(source, target) : [];
    }

}
