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
        const nodeClass = this.classes.find(c => c.name === node.conceptElement);
        return nodeClass && nodeClass.superTypeNames ? nodeClass.superTypeNames : [];
    }
}


export default class {

    constructor(shape, concept) {
        this.nodes = shape.nodes || [];
        this.edges = shape.edges || [];
        const classes = concept.classes || [];
        const references = concept.references || [];

        // If edges contains meta, merge assigned references and remove the edge
        this.edges.filter(edge => edge.meta).forEach(edge => {

            // Use the source reference as merged reference
            const source = references.find(ref => ref.name === edge.meta.source.mref);
            source.name = edge.name;
            source.sourceClassName = edge.meta.source.mclass;
            source.targetClassName = edge.meta.target.mclass;

            // Remove the target reference
            const target = references.find(ref => ref.name === edge.meta.target.mref);
            references.splice(references.indexOf(target), 1);

            // Update the source class outputReferenceNames
            const sourceClass = classes.find(clazz => clazz.name === edge.meta.source.mclass).outputReferenceNames;
            const targetIndex = sourceClass.findIndex(n => n === edge.meta.target.mref);
            sourceClass[targetIndex] = edge.name;

            // Update the source class inputReferenceNames
            const targetClass = classes.find(clazz => clazz.name === edge.meta.target.mclass).inputReferenceNames;
            const sourceIndex = targetClass.findIndex(n => n === edge.meta.source.mref);
            targetClass[sourceIndex] = edge.name;

        });
        this.references = references;
        this.edges = this.edges.filter(edge => !edge.meta);
        this.classes = classes;

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
