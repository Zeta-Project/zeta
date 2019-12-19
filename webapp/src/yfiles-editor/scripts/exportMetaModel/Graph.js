import {UMLNodeStyle} from "../UMLNodeStyle";

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
        return this.graph.nodes.filter(node => node.style instanceof UMLNodeStyle)
    };

    /*
      Returns the names of all nodes.
     */
    Graph.prototype.getNodeNames = function () {
        return this.getNodes().map(node => {
            return node.style.model.className
        })
    };

    /*
    Returns the name of the given node.
   */
    Graph.prototype.getNodeName = function (node) {
        let result = node.style.model.className;
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
        let result = node.style.model.description;
        if (typeof result !== 'string') {
            return "";
        }
        return result;
    };

    /*
      Checks whether the element is abstract.
     */
    Graph.prototype.isAbstract = function (node) {
        return node.style.model.abstract;
    };

    /*
    TODO: implement edge model and use CoreUtil.isGeneralization and load value from model
     */
    Graph.prototype.getSuperTypes = function (node) {
        // let generalizations = this.graph.outEdgesAt(node).filter(edge => isGeneralization(edge.style));
        //
        // let superTypes = generalizations.map(edge => {
        //     return edge.targetNode.style.model.className;
        // });
        //
        // return superTypes;
        return [];
    };

    Graph.prototype.getNodeAttributes = function (node) {
        if (node.style.model.attributes !== null) {
            return node.style.model.attributes
        } else {
            return []
        }
    };

    Graph.prototype.getNodeMethods = function (node) {
        if (node.style.model.attributes !== null) {
            return node.style.model.operations
        } else {
            return []
        }
    };

    //Todo mCoreUtil need to check validity like above (getInputReferenceNames)
    Graph.prototype.getInputReferenceNames = function (node) {
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
    Graph.prototype.getOutputReferenceNames = function (node) {
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
        return this.getReferences().map(edge => {
            if (Object.entries(edge.labels).length > 0) {
                let label = edge.labels.find(label => label !== undefined);
                return label.text;
            }
            return "";
        });
    };

    /*
      Returns the name of the given reference.

      TODO: Is a temporary solution. Reference Model is necessary and access to different labels of edge.
     */
    Graph.prototype.getReferenceName = function (edge) {
        if (Object.entries(edge.labels).length > 0) {
            let label = edge.labels.find(label => label !== undefined);
            return label.text;
        }
        return "";
    };

    /*
      Returns all source classes of the reference.
     */
    Graph.prototype.getSourceName = function (edge) {
        return edge.sourceNode.style.model.className
    };

    /*
      Returns all target classes of the reference.
     */
    Graph.prototype.getTargetName = function (reference) {
        return reference.targetNode.style.model.className
    };

    /*
    Returns all element- and reference-names which are assigned more than once.
    */
    Graph.prototype.getDuplicateKeys = function () {
        let keys = [];
        let duplicateKeys = [];
        let elements = this.getNodeNames().concat(this.getReferenceNames());

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
     Convert a attribute type into json
    */
    // Graph.prototype.attributeTypeToJson = function (type) {
    //     if (type === 'String' || type === 'Bool' || type === 'Int' || type === 'Double') {
    //         return type;
    //     } else {
    //         return {
    //             type: 'enum',
    //             name: type
    //         }
    //     }
    // };
    //
    // /*
    //  Convert a attribute value into json
    // */
    // Graph.prototype.attributeValueToJson = function (type, value) {
    //     switch (type) {
    //         case 'Bool':
    //             return {
    //                 type: 'Bool',
    //                 value: (value === 'true')
    //             };
    //         case 'Int':
    //             if (!this.isNumeric(value)) {
    //                 value = 0;
    //             }
    //             return {
    //                 type: 'Int',
    //                 value: parseInt(value)
    //             };
    //         case 'Double':
    //             if (!this.isNumeric(value)) {
    //                 value = 0.0;
    //             }
    //             return {
    //                 type: 'Double',
    //                 value: parseFloat(value)
    //             };
    //         case 'String':
    //             return {
    //                 type: 'String',
    //                 value
    //             };
    //         default:
    //             return {
    //                 type: 'enum',
    //                 enumName: type,
    //                 valueName: value
    //             };
    //     }
    // };
    //
    // Graph.prototype.isNumeric = function (n) {
    //     return !isNaN(parseFloat(n)) && isFinite(n);
    // };

    /*
      Returns the sourceDeletionDeletesTarget value of the reference.

    Graph.prototype.getSourceDeletionDeletesTarget = function(reference) {
      return reference.attributes[Constants.field.SOURCE_DELETION_DELETES_TARGET] || false;
    };


      Returns the targetDeletionDeletesSource value of the reference.

    Graph.prototype.getTargetDeletionDeletesSource = function(reference) {
      return reference.attributes[Constants.field.TARGET_DELETION_DELETES_SOURCE] || false;
    };


    Returns all input references of the element.

  Graph.prototype.getInputReferenceNames = function(element) {
    return this.graph.getConnectedLinks(element, {
      inbound: true
    }).filter(function(link) {
      return mCoreUtil.isReference(link);
    }).map(input =>
      input.attributes.name
    );
  };



       Returns the attributes of the cell.
       Default value is parsed from String because the inspector can't display the correct input
       based on the selected type, because the type is in a List
       See: http://stackoverflow.com/questions/37742721/display-field-based-on-another-in-jointjs

     Graph.prototype.getNodeAttributes = function(cell) {
       let attributes, key, mAttributes, value, _i, _len, _ref;
       mAttributes = [];
       if (cell.attributes[Constants.field.ATTRIBUTES] != null) {
         _ref = cell.attributes[Constants.field.ATTRIBUTES];
         for (_i = 0, _len = _ref.length; _i < _len; _i++) {
           attributes = _ref[_i];
           mAttributes.push({});
           for (key in attributes) {

             if (key === 'default') {
               mAttributes[mAttributes.length - 1][key] = this.attributeValueToJson(attributes.typ, attributes[key]);
             } else if(key === 'typ') {
               mAttributes[mAttributes.length - 1]['type'] = this.attributeTypeToJson(attributes.typ);
             } else {
               mAttributes[mAttributes.length - 1][key] = attributes[key];
             }
           }
         }
       }
       return mAttributes;
     };

  */

    return Graph;
})();
