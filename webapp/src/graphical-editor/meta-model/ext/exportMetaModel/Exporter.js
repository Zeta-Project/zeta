import mEnum from '../mEnum';
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
    var exportedModel, validationResult;
    exportedModel = new ExportedMetaModel;
    validationResult = this.checkValidity();
    exportedModel.setValid(validationResult.isValid());
    exportedModel.setMessages(validationResult.getMessages());
    if (validationResult.isValid()) {
      exportedModel.setClasses(this.createClasses());
      exportedModel.setReferences(this.createReferences());
      exportedModel.setEnums(this.createEnums());
      exportedModel.setAttributes(this.createAttributes());
      exportedModel.setMethods(this.createMethods());
    }
    return exportedModel;
  };


  /*
    Checks, if the graph is in a state that allows exporting it.
    returns an instance of the ValidationResult class.
   */

  Exporter.prototype.checkValidity = function() {
    var attribute, key, validationResult, _i, _j, _len, _len1, _ref, _ref1;
    validationResult = new ValidationResult;
    _ref = this.graph.getDuplicateKeys();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      key = _ref[_i];
      validationResult.addErrorMessage("Duplicate key '" + key + "'");
    }
    _ref1 = this.graph.getDuplicateAttributes();
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      attribute = _ref1[_j];
      validationResult.addErrorMessage("Duplicate attribute '" + (attribute.getAttributeKey()) + "' in cell '" + (attribute.getCellName()) + "'");
    }
    return validationResult;
  };

  Exporter.prototype.createClasses = function() {
    var classes, element, _i, _len, _ref;
    classes = [];
    _ref = this.graph.getElements();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      element = _ref[_i];
      classes.push({
        name: this.graph.getName(element),
        description: this.graph.getDescription(element),
        abstractness: this.graph.isAbstract(element),
        superTypeNames: this.graph.getSuperTypes(element),
        attributes: this.graph.getAttributes(element),
        methods: this.graph.getEntityMethods(element),
        inputs: this.graph.getInputs(element),
        outputs: this.graph.getOutputs(element)
      });
    }
    return classes;
  };

  Exporter.prototype.createReferences = function() {
    var reference, references, _i, _len, _ref;
    references = [];
    _ref = this.graph.getReferences();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      reference = _ref[_i];
      references.push({
        name: this.graph.getName(reference),
        description: "",
        sourceDeletionDeletesTarget: this.graph.getSourceDeletionDeletesTarget(reference),
        targetDeletionDeletesSource: this.graph.getTargetDeletionDeletesSource(reference),
        attributes: this.graph.getAttributes(reference),
        methods: this.graph.getEntityMethods(reference),
        sourceClassName: this.graph.getSources(reference),
        targetClassName: this.graph.getTargets(reference)
      });
    }
    return references;
  };

  Exporter.prototype.createEnums = function() {
    var enums, thisMEnum, _i, _len, _ref;
    enums = [];
    _ref = mEnum.getMEnums();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      thisMEnum = _ref[_i];
      enums.push({
        name: thisMEnum.name,
        values: thisMEnum.symbols
      });
    }
    return enums;
  };

  Exporter.prototype.createAttributes = function() {
    var attributes, thisAttribute, _i, _len, _ref;
    attributes = [];
    if (mEnum.getMEnumContainer().attributes.hasOwnProperty('m_attributes')) {
      _ref = mEnum.getMEnumContainer().attributes['m_attributes'];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        thisAttribute = _ref[_i];
        attributes.push({
          name: thisAttribute.name,
          upperBound: thisAttribute.upperBound,
          lowerBound: thisAttribute.lowerBound,
          default: {
            type: thisAttribute.typ,
            value: thisAttribute.default,
          },
          typ: thisAttribute.typ,
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
    var methods, thisMethod, _i, _len, _ref;
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

  return Exporter;
})();
