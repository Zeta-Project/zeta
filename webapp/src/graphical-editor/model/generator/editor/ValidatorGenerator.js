class MatrixGenerator {
    create(references) {
        return references.reduce((result, reference) => {
            result[reference.name] = this.createBounds(reference);
            return result;
        }, {});
    }

    createBounds({sourceLowerBounds, sourceUpperBounds, targetLowerBounds, targetUpperBounds}) {
        return {sourceLowerBounds, sourceUpperBounds, targetLowerBounds, targetUpperBounds};
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
        // const nodeClass = this.classes.find(c => c.name === node.conceptElement);
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

    getAllRefBounds() {
        return this.references ? this.matrix.create(this.references) : {};
    }

    inputMatrix(type) {
        let node = this.nodes.find(e => e.name === type);
        return this.getEdge(node);
    }

    getEdge(node) {
        return node.edges.reduce((result,edge) => {
            result[edge.name.toLowerCase()] = this.getEdgeBoundss(edge.conceptElement.split(".")[1]);
            return result;
        }, {});
    }

    getEdgeBoundss(edgeName) {
        var edge = this.references.find(e => e.name === edgeName);
        let mbo = this.createBounds(edge);
       // var obj = { [edge.name.toLowerCase()]: [mbo] };
        return mbo;
    }

    createBounds({sourceLowerBounds, sourceUpperBounds, targetLowerBounds, targetUpperBounds}) {
        return {sourceLowerBounds, sourceUpperBounds, targetLowerBounds, targetUpperBounds};
    }

    get outputMatrix() {
        return this.classes.reduce((result, clas) => {
            result[clas.name] = clas.outputReferenceNames ? this.matrix.create(this.references) : {};
            return result;
        }, {});
    }

    getEdgeData(edgeName) {
        const reference = this.references.find(e => e.name === edgeName);
        const edge = this.edges.find(e => e.conceptElement.split('.')[1] === edgeName);
        if (reference) {
            return {
                type: edgeName,
                from: reference.sourceClassName,
                to: reference.targetClassName,
                style: edge?.name || ""
            };
        }
    }

    getValidEdges(sourceName, targetName) {
        const source = this.nodes.find(n => n.name === sourceName);
        const target = this.nodes.find(n => n.name === targetName);
        return source && target ? this.validEdges.getValid(source, target) : [];
    }

}
