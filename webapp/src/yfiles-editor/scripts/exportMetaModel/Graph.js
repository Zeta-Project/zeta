import {UMLNodeStyle} from "../UMLNodeStyle";
import graph from 'yfiles'

/*
  Wrapper class around the internal yfiles graph.
  It provides methods for checking and extracting attributes from the graph.
 */
export default (function() {
  function Graph(graph) {
    this.graph = graph;
  }

  /*
    Returns all nodes (which are nodes of type UMLSNodeStyle).
   */
  Graph.prototype.getNodes = function() {
    let nodes = []
    this.graph.nodes.forEach(node => nodes.push(node))
    nodes = nodes.filter(node => node.style instanceof UMLNodeStyle)
    return nodes
  };

  /*
    Returns the names of all nodes.
   */
  Graph.prototype.getNodeNames = function() {
    let nodes = this.getNodes()
    let names = nodes.forEach(node => node.style.model.className)

    return names
  };

  /*
    Returns all references
   */
  Graph.prototype.getReferences = function() {
    let edges = []
    this.graph.edges.forEach(edge => edges.push(edge))
    return edges
  };

  /*
     Returns the index of all references.
   */
  Graph.prototype.getReferenceNames = function() {
    return this.getReferences().map(function(edge) {
      return edge.index;
    });
  };

  /*
    Returns the name of the given node.
   */
  Graph.prototype.getName = function(node) {
    let result;
    if(!(node instanceof UMLNodeStyle)) return ""
    result = node.style.model.className;
    if(result === "" || typeof result !== 'string') {
      return "";
    } else {
      return result;
    }
  };

  /*
    Returns the description of the given node.
   */
  Graph.prototype.getDescription = function(node) {
    let result;
    result = node.style.model.description;
    if (result === "" || typeof result !== 'string') {
      return "";
    }
  };

  //Todo check for duplicate attributes inside each node instead across all attributes maybe
  Graph.prototype.getDuplicateAttributes = function() {
    return []
  }

  /*
    Checks whether the element is abstract.
   */
  Graph.prototype.isAbstract = function(node) {
    return node.style.model.abstract;
  };

  //Todo check why node.inDegree is undefined -> should be at least 0
  Graph.prototype.getSuperTypes = function(node) {
    let superTypeNames = []
    if(node.inDegree > 0) {
      node.inEdges.forEach(() => superTypeNames.push(edge.source.style.model.className))
    }
    return superTypeNames
  }

  //Todo check if [Key] or Constants.field.ATTRIBUTES is necessary
  Graph.prototype.getAttributes = function(node) {
    if(node.style.model.attributes !== null) {
      return node.style.model.attributes
    } else {
      return []
    }
  }

  /*
   Convert a attribute type into json
  */
  Graph.prototype.attributeTypeToJson = function(type) {
    if(type === 'String' || type === 'Bool' || type === 'Int' || type === 'Double') {
      return type;
    } else {
      return {
        type: 'enum',
        name: type
      }
    }
  };

  /*
   Convert a attribute value into json
  */
  Graph.prototype.attributeValueToJson = function(type, value) {
    switch (type) {
      case 'Bool':
        return {
          type: 'Bool',
          value: (value === 'true')
        };
      case 'Int':
        if (!this.isNumeric(value)) {
          value = 0;
        }
        return {
          type: 'Int',
          value: parseInt(value)
        };
      case 'Double':
        if (!this.isNumeric(value)) {
          value = 0.0;
        }
        return  {
          type: 'Double',
          value: parseFloat(value)
        };
      case 'String':
        return {
          type: 'String',
          value
        };
      default:
        return {
          type: 'enum',
          enumName: type,
          valueName: value
        };
    }
  };

  //Todo check how to ise Constants.field.METHODS
  Graph.prototype.getEntityMethods = function(node) {
    return node.style.model.operations
  }

  Graph.prototype.isNumeric = function(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
  };


  //Todo mCoreUtil need to check validity like above (getInputs)
  Graph.prototype.getInputs = function(node) {
    if(node.inDegree > 0) {
      return node.inEdges.forEach(() => edge.index)
    } else {
      return []
    }

  }

  //Todo mCoreUtil need to check validity
  Graph.prototype.getOutputs = function(node) {
    if(node.outEdges) {
      return node.outEdges.map(() => node.style.model.className)
    } else {
      return []
    }
  }

  /*
    Returns all source classes of the reference.
   */
  Graph.prototype.getSourceName = function(edge) {
    return edge.sourceNode.style.model.className
  };

  /*
    Returns all target classes of the reference.
   */
  Graph.prototype.getTargetName = function(reference) {
    return reference.targetNode.style.model.className
  };

  /*
    Returns the sourceDeletionDeletesTarget value of the reference.

  Graph.prototype.getSourceDeletionDeletesTarget = function(reference) {
    return reference.attributes[Constants.field.SOURCE_DELETION_DELETES_TARGET] || false;
  };


    Returns the targetDeletionDeletesSource value of the reference.

  Graph.prototype.getTargetDeletionDeletesSource = function(reference) {
    return reference.attributes[Constants.field.TARGET_DELETION_DELETES_SOURCE] || false;
  };


  Returns all element-, reference and enum-names which are assigned more than once.

Graph.prototype.getDuplicateKeys = function() {
  let duplicateKeys, key, keys, _i, _len, _ref;
  keys = [];
  duplicateKeys = [];
  _ref = this.getElementNames().concat(this.getReferenceNames()).concat(mEnum.getMEnumNames());
  for (_i = 0, _len = _ref.length; _i < _len; _i++) {
    key = _ref[_i];
    if (keys.indexOf(key) === -1) {
      keys.push(key);
    } else {
      duplicateKeys.push(key);
    }
  }
  return duplicateKeys;
};


    Returns all attribute keys which are assigned more than once insinde an element.

  Graph.prototype.getDuplicateAttributes = function() {
    let attribute, attributes, cell, duplicateAttributes, key, _i, _j, _len, _len1, _ref, _ref1;
    duplicateAttributes = [];
    _ref = this.getElements().concat(this.getReferences());
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      cell = _ref[_i];
      if (cell.attributes[Constants.field.ATTRIBUTES] != null) {
        attributes = [];
        _ref1 = cell.attributes[Constants.field.ATTRIBUTES];
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          attribute = _ref1[_j];
          key = attribute.name;
          if (attributes.indexOf(key) === -1) {
            attributes.push(key);
          } else {
            duplicateAttributes.push(new Attribute(cell.attributes.name, key));
          }
        }
      }
    }
    return duplicateAttributes;
  };


  Returns all input references of the element.

Graph.prototype.getInputs = function(element) {
  return this.graph.getConnectedLinks(element, {
    inbound: true
  }).filter(function(link) {
    return mCoreUtil.isReference(link);
  }).map(input =>
    input.attributes.name
  );
};


  Returns the methods of the cell.

Graph.prototype.getEntityMethods = function(cell) {
  let attributes, key, mMethods, value, _i, _len, _ref;
  mMethods = [];
  if (cell.attributes[Constants.field.METHODS] != null) {
    _ref = cell.attributes[Constants.field.METHODS];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      attributes = _ref[_i];
      mMethods.push({});
      for (key in attributes) {
        value = attributes[key];
        mMethods[mMethods.length - 1][key] = value;
      }
    }
  }
  return mMethods;
};


     Returns the attributes of the cell.
     Default value is parsed from String because the inspector can't display the correct input
     based on the selected type, because the type is in a List
     See: http://stackoverflow.com/questions/37742721/display-field-based-on-another-in-jointjs

   Graph.prototype.getAttributes = function(cell) {
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


  Returns all superTypes of the given element (which is defined by the generalization reference type).

Graph.prototype.getSuperTypes = function(element) {
  return this.graph.getConnectedLinks(element, {
    outbound: true
  }).filter(function(link) {
    return mCoreUtil.isGeneralization(link);
  }).map((function(link) {
    return this.graph.getCell(link.attributes.target.id).attributes.name;
  }), this);
};
*/

  return Graph;
})();
