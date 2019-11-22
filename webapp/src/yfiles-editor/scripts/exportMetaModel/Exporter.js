import ExportedMetaModel from './ExportedMetaModel';
import Graph from './Graph';
import ValidationResult from './ValidationResult';


export default (function() {

  /*
    Constructor function.
    graph: the internal representation of the JointJS graph.
   */
  function Exporter(graph) {
    this.graph = new Graph(graph);
  }


  /*
    Exports the graph.
    This is the only public method of this class!
    returns an instance of the ExportedMetaModel class which contains the exported JSON graph and error messages.
   */

  Exporter.prototype["export"] = function() {
    let exportedModel, validationResult;
    exportedModel = new ExportedMetaModel;
    validationResult = this.checkValidity();
    //exportedModel.setValid(validationResult.isValid());
    //exportedModel.setMessages(validationResult.getMessages());
    if (validationResult.isValid()) {
      exportedModel.setClasses(this.createClasses());
      exportedModel.setReferences(this.createReferences());

      //Todo check if enuMs are needed, also compare to yfiles enuMs
      //exportedModel.setEnums(this.createEnums());
      //exportedModel.setEnums([]);
      //exportedModel.setAttributes(this.createAttributes());
      exportedModel.setAttributes([]);
      //exportedModel.setMethods(this.createMethods());
      exportedModel.setMethods([]);
    }
    return exportedModel;
  };


  /*
    Checks, if the graph is in a state that allows exporting it.
    returns an instance of the ValidationResult class.
   */

  Exporter.prototype.checkValidity = function() {
    let attribute, key, validationResult, _i, _j, _len, _len1, _ref, _ref1;
    validationResult = new ValidationResult;
/*
    _ref1 = this.graph.getDuplicateAttributes();
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      attribute = _ref1[_j];
      validationResult.addErrorMessage("Duplicate attribute '" + (attribute.getAttributeKey()) + "' in " +
          " '" + (attribute.getCellName()) + "'");
    }
 */
    return validationResult;
  };

  Exporter.prototype.createClasses = function() {
    let classes;
    classes = [];
    let nodes = this.graph.getNodes()
    nodes.forEach(node => {
      classes.push({
          name: this.graph.getName(node),
          description: this.graph.getDescription(node),
          abstractness: this.graph.isAbstract(node),
          superTypeNames: this.graph.getSuperTypes(node),
          attributes: this.graph.getAttributes(node),
          methods: this.graph.getEntityMethods(node),
          inputReferenceNames: this.graph.getInputs(node),
          outputReferenceNames: this.graph.getOutputs(node)
        });
    })
    return classes;
  };

  Exporter.prototype.createReferences = function() {
    let edges = []
    let ret = this.graph.getReferences()
    ret.forEach(edge => {
      edges.push({
        name: this.graph.getName(edge),
        description: "",
        sourceDeletionDeletesTarget: "", //Todo check if this is necessary
        targetDeletionDeletesSource: "",
        attributes: [],//this.graph.getAttributes(edge),
        methods: [],//this.graph.getEntityMethods(edge),
        sourceClassName: this.graph.getSourceName(edge),
        targetClassName: this.graph.getTargetName(edge),
        sourceLowerBounds: 0,//reference.attributes.linkdef_source[0]?.lowerBound || 0,
        sourceUpperBounds: -1,//reference.attributes.linkdef_source[0]?.upperBound || -1,
        targetLowerBounds: 0,//reference.attributes.linkdef_target[0]?.lowerBound || 0,
        targetUpperBounds: -1,//reference.attributes.linkdef_target[0]?.upperBound || -1,
      });
    })
    return edges;
  };
/*
  Exporter.prototype.createEnums = function() {
    let enums, thisMEnum, _i, _len, _ref;
    enums = [];
    _ref = mEnum.getMEnums();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      thisMEnum = _ref[_i];
      enums.push({
        name: thisMEnum.name,
        valueNames: thisMEnum.symbols
      });
    }
    return enums;
  };

  Exporter.prototype.createAttributes = function() {
    let attributes, thisAttribute, _i, _len, _ref;
    attributes = [];
    if (mEnum.getMEnumContainer().attributes.hasOwnProperty('m_attributes')) {
      _ref = mEnum.getMEnumContainer().attributes['m_attributes'];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        thisAttribute = _ref[_i];
        attributes.push({
          name: thisAttribute.name,
          upperBound: thisAttribute.upperBound,
          lowerBound: thisAttribute.lowerBound,
          default: this.graph.attributeValueToJson(thisAttribute.typ, thisAttribute.default),
          type: this.graph.attributeTypeToJson(thisAttribute.typ),
          expression: thisAttribute.expression,
          localUnique: thisAttribute.localUnique,
          globalUnique: thisAttribute.globalUnique,
          constant: thisAttribute.constant,
          ordered: thisAttribute.ordered,
          transient: thisAttribute.transient,
          singleAssignment: thisAttribute.singleAssignment
        });
      }
    }
    return attributes;
  };

  Exporter.prototype.createMethods = function() {
    let methods, thisMethod, _i, _len, _ref;
    methods = [];
    if (mEnum.getMEnumContainer().attributes.hasOwnProperty('m_methods')) {
      _ref = mEnum.getMEnumContainer().attributes['m_methods'];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        thisMethod = _ref[_i];
        methods.push({
          name: thisMethod.name,
          parameters: thisMethod.parameters,
          description: thisMethod.description,
          returnType: thisMethod.returnType,
          code: thisMethod.code
        });
      }
    }
    return methods;
  };
*/
  return Exporter;
})();
