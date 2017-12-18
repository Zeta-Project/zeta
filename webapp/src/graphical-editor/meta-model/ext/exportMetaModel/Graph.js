import mCoreUtil from '../mCoreUtil';
import mEnum from '../mEnum';
import Constants from './Constants';
import Attribute from './Attribute';

/*
  Wrapper class around the internal JointJS graph.
  It provides methods for checking and extracting attributes from the graph.
 */
export default (function() {
  function Graph(graph) {
    this.graph = graph;
  }

  /*
    Returns all elements (which are real elements by MoDiGen metamodel semantics).
   */
  Graph.prototype.getElements = function() {
    return this.graph.getElements().filter(function(element) {
      return mCoreUtil.isElement(element);
    });
  };

  /*
    Returns the names of all elements.
   */
  Graph.prototype.getElementNames = function() {
    return this.getElements().map(function(element) {
      return element.attributes.name;
    });
  };

  /*
    Returns all references (which are real references by MoDiGen metamodel semantics).
   */
  Graph.prototype.getReferences = function() {
    return this.graph.getLinks().filter(function(link) {
      return mCoreUtil.isReference(link);
    });
  };

  /*
     Returns the names of all references.
   */
  Graph.prototype.getReferenceNames = function() {
    return this.getReferences().map(function(reference) {
      return reference.attributes.name;
    });
  };

  /*
    Returns the name of the given cell.
   */
  Graph.prototype.getName = function(cell) {
    return cell.attributes.name;
  };

  /*
    Returns the name of the given cell.
   */
  Graph.prototype.getDescription = function(cell) {
    var result;
    result = cell.attributes.description;
    if (result === "" || typeof result !== 'string') {
      return "";
    }
  };

  /*
    Returns all element-, reference and enum-names which are assigned more than once.
   */
  Graph.prototype.getDuplicateKeys = function() {
    var duplicateKeys, key, keys, _i, _len, _ref;
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

  /*
    Returns all attribute keys which are assigned more than once insinde an element.
   */
  Graph.prototype.getDuplicateAttributes = function() {
    var attribute, attributes, cell, duplicateAttributes, key, _i, _j, _len, _len1, _ref, _ref1;
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

  /*
    Checks whether the element is abstract.
   */
  Graph.prototype.isAbstract = function(element) {
    return mCoreUtil.isAbstract(element);
  };

  /*
    Returns all superTypes of the given element (which is defined by the generalization reference type).
   */
  Graph.prototype.getSuperTypes = function(element) {
    return this.graph.getConnectedLinks(element, {
      outbound: true
    }).filter(function(link) {
      return mCoreUtil.isGeneralization(link);
    }).map((function(link) {
      return this.graph.getCell(link.attributes.target.id).attributes.name;
    }), this);
  };

  /*
    Returns the attributes of the cell.
    Default value is parsed from String because the inspector can't display the correct input
    based on the selected type, because the type is in a List
    See: http://stackoverflow.com/questions/37742721/display-field-based-on-another-in-jointjs
   */
  Graph.prototype.getAttributes = function(cell) {
    var attributes, key, mAttributes, value, _i, _len, _ref;
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

  /*
    Returns the methods of the cell.
   */
  Graph.prototype.getEntityMethods = function(cell) {
    var attributes, key, mMethods, value, _i, _len, _ref;
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

  Graph.prototype.isNumeric = function(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
  };

  /*
    Returns all input references of the element.
   */
  Graph.prototype.getInputs = function(element) {
    return this.graph.getConnectedLinks(element, {
      inbound: true
    }).filter(function(link) {
      return mCoreUtil.isReference(link);
    }).map(input =>
      input.attributes.name
    );
  };

  /*
    Returns all output references of the element.
   */
  Graph.prototype.getOutputs = function(element) {
    return this.graph.getConnectedLinks(element, {
      outbound: true
    }).filter(function(link) {
      return mCoreUtil.isReference(link);
    }).map(output =>
      output.attributes.name
    );
  };

  /*
    Returns all source classes of the reference.
   */
  Graph.prototype.getSources = function(reference) {
    const source = [this.graph.getCell(reference.attributes.source.id)][0];
    return source.attributes.name;
  };

  /*
    Returns all target classes of the reference.
   */
  Graph.prototype.getTargets = function(reference) {
    const target = [this.graph.getCell(reference.attributes.target.id)][0];
    return target.attributes.name;
  };

  /*
    Returns the sourceDeletionDeletesTarget value of the reference.
   */
  Graph.prototype.getSourceDeletionDeletesTarget = function(reference) {
    return reference.attributes[Constants.field.SOURCE_DELETION_DELETES_TARGET] || false;
  };

  /*
    Returns the targetDeletionDeletesSource value of the reference.
   */
  Graph.prototype.getTargetDeletionDeletesSource = function(reference) {
    return reference.attributes[Constants.field.TARGET_DELETION_DELETES_SOURCE] || false;
  };

  return Graph;
})();
