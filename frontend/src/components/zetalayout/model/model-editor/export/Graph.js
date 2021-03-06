import VuejsNodeStyle from "../model/nodes/styles/VuejsNodeStyle";

/*
  Wrapper class around the internal yfiles graph.
  It provides methods for checking and extracting attributes from the graph.
 */
export default (function () {
    function Graph(graph) {
        this.graph = graph;
    }

    /*
      Returns all nodes (which are nodes of type UMLSNodeStyle).
     */
    Graph.prototype.getNodes = function () {
        return this.graph.nodes.filter(node => node.style instanceof VuejsNodeStyle)
    };

    /*
      Returns the names of all nodes.
     */
    Graph.prototype.getNodeNames = function() {
        return this.getNodes().map(node => node.tag.name);
    };

    /*
    Returns the name of the given node.
   */
    Graph.prototype.getNodeName = function (node) {
        let result = node.tag.name;
        if (typeof result !== 'string') {
            return "";
        } else {
            return result;
        }
    };

    /*
      Returns the description of the given node.
     */
    Graph.prototype.getNodeDescription = function (node) {
        let result = node.tag.description;
        if (typeof result !== 'string') {
            return "";
        }
        return result;
    };

    /*
      Returns the size info of the given node.
     */
    Graph.prototype.getSizeInfo = function (node) {
        if (node.tag.sizeInfo !== null) {
            return node.tag.sizeInfo;
        } else {
            return {}
        }
    };

    /*
      Checks whether the element is abstract.
     */
    Graph.prototype.isAbstract = function (node) {
        return node.tag.abstractness;
    };

    /*
    TODO: implement edge model and use CoreUtil.isGeneralization and load value from model
     */
    Graph.prototype.getSuperTypes = function () {
        /*let generalizations = this.graph.outEdgesAt(node).filter(edge => isGeneralization(edge.style));

        let superTypes = generalizations.map(edge => {
            return edge.targetNode.tag.className;
        });

        return superTypes;*/
        return []
    };

    Graph.prototype.getNodeAttributes = function (node) {
        if (node.tag.attributes !== null) {
            return node.tag.attributes
        } else {
            return []
        }
    };

    Graph.prototype.getNodeMethods = function (node) {
        if (node.tag.methods !== null) {
            return node.tag.methods
        } else {
            return []
        }
    };

    //Todo mCoreUtil need to check validity like above (getInputReferenceNames)
    Graph.prototype.getInputReferenceNames = function () {
        /*if (this.graph.inDegree(node) > 0) {
            return this.graph.inEdgesAt(node).map(edge => {
                let label = edge.labels.find(label => label !== undefined);
                return label.text;
            })
        } else*/
        {
            return []
        }
    };

    //Todo mCoreUtil need to check validity
    Graph.prototype.getOutputReferenceNames = function () {
        /*if (this.graph.outDegree(node) > 0) {
            return this.graph.outEdgesAt(node).map(edge => {
                let label = edge.labels.find(label => label !== undefined);
                return label.text;
            })
        } else*/
        {
            return []
        }
    };

    /*
      Returns all references
     */
    Graph.prototype.getReferences = function () {
        return this.graph.edges
    };

    /*
       Returns the index of all references.

       TODO: Is a temporary solution. Reference Model is necessary and access to different labels of edge.
     */
    Graph.prototype.getReferenceNames = function () {
        return this.getReferences().flatMap(r => r.labels.map(l => l.text || '')).map(r => r ? r : '');
    };

    /*
      Returns the name of the given reference.

      TODO: Is a temporary solution. Reference Model is necessary and access to different labels of edge.
      Not proud of this code :(
     */
    Graph.prototype.getReferenceName = function (reference) {
        return reference.labels.$f.text;
    };

    /*
      Returns the description of the given edge.
     */
    Graph.prototype.getReferenceDescription = function (reference) {
        let result = reference.tag.description;
        if (typeof result !== 'string') {
            return "";
        }
        return result;
    };

    /*
      Returns the sourceDeletionDeletesTarget value of the reference.
    */

    Graph.prototype.getSourceDeletionDeletesTarget = function (reference) {
        return reference.tag.sourceDeletionDeletesTarget || false;
    };

    /*
      Returns the targetDeletionDeletesSource value of the reference.
    */

    Graph.prototype.getTargetDeletionDeletesSource = function (reference) {
        return reference.tag.targetDeletionDeletesSource || false;
    };

    /*
      Returns all source classes of the reference.
     */
    Graph.prototype.getSourceClassName = function (reference) {
        return reference.sourceNode.tag.name
    };

    /*
      Returns all target classes of the reference.
     */
    Graph.prototype.getTargetClassName = function (reference) {
        return reference.targetNode.tag.name
    };

    Graph.prototype.getReferenceAttributes = function (reference) {
        if (reference.tag.attributes !== null) {
            return reference.tag.attributes
        } else {
            return []
        }
    };

    Graph.prototype.getReferenceMethods = function (reference) {
        if (reference.tag.methods !== null) {
            return reference.tag.methods
        } else {
            return []
        }
    };

    Graph.prototype.getSourceLowerBounds = function (reference) {
        return reference.tag.sourceLowerBounds
    };

    Graph.prototype.getSourceUpperBounds = function (reference) {
        return reference.tag.sourceUpperBounds
    };

    Graph.prototype.getTargetLowerBounds = function (reference) {
        return reference.tag.targetLowerBounds
    };

    Graph.prototype.getTargetUpperBounds = function (reference) {
        return reference.tag.targetUpperBounds
    };

    /*
    Returns all element- and reference-names which are assigned more than once.
    */
    Graph.prototype.getDuplicateKeys = function () {
        let keys = [];
        let duplicateKeys = [];
        let elements = this.getNodeNames().concat(this.getReferenceNames());
        elements = elements.map(e => e ? e : '')

        elements.forEach(key => {
            if (keys.includes(key)) {
                duplicateKeys.push(key);
            } else {
                keys.push(key);
            }
        });
        return duplicateKeys;
    };

    /*
    Returns all attribute keys which are assigned more than once inside an element.
     */

    Graph.prototype.getDuplicateNodeAttributes = function () {
        let duplicateAttributes = [];
        let attributes = [];

        this.getNodes().forEach(element => {
            this.getNodeAttributes(element).forEach(attribute => {
                if (attributes.includes(attribute.name)) {
                    duplicateAttributes.push({
                        nodeName: Graph.prototype.getNodeName(element),
                        attributeName: attribute.name
                    });
                } else {
                    attributes.push(attribute.name)
                }
            })
            // clear attributes array for new element
            attributes = [];
        });

        return duplicateAttributes;
    };

    /*
    Returns all methods which are assigned more than once inside an element.
    Overloaded methods are valid.
     */

    Graph.prototype.getDuplicateNodeMethods = function () {
        let duplicateMethods = [];
        let methods = [];

        this.getNodes().forEach(element => {
            this.getNodeAttributes(element).forEach(attribute => {
                if (methods.includes(attribute.name)) {
                    duplicateMethods.push({
                        nodeName: Graph.prototype.getNodeName(element),
                        attributeName: attribute.name
                    });
                } else {
                    methods.push(attribute.name)
                }
            })
            // clear attributes array for new element
            methods = [];
        });

        return duplicateMethods;
    };

    return Graph;
})();
