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
      exportedModel.setEnums([]);
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
          name: this.graph.getNodeName(node),
          description: this.graph.getNodeDescription(node),
          abstractness: this.graph.isAbstract(node),
          superTypeNames: this.graph.getSuperTypes(node),
          attributes: this.graph.getNodeAttributes(node),
          methods: this.graph.getNodeMethods(node),
          inputReferenceNames: this.graph.getInputReferenceNames(node),
          outputReferenceNames: this.graph.getOutputReferenceNames(node)
        });
    })
    return classes;
  };

  Exporter.prototype.createReferences = function() {
    let references = []
    let ref = this.graph.getReferences()
    ref.forEach(reference => {
      references.push({
        name: this.graph.getReferenceName(reference),
        description: "",
        sourceDeletionDeletesTarget: false,
        targetDeletionDeletesSource: false,
        attributes: [],//this.graph.getNodeAttributes(reference),
        methods: [],//this.graph.getNodeMethods(reference),
        sourceClassName: this.graph.getSourceName(reference),
        targetClassName: this.graph.getTargetName(reference),
        sourceLowerBounds: 0,//reference.attributes.linkdef_source[0]?.lowerBound || 0,
        sourceUpperBounds: -1,//reference.attributes.linkdef_source[0]?.upperBound || -1,
        targetLowerBounds: 0,//reference.attributes.linkdef_target[0]?.lowerBound || 0,
        targetUpperBounds: -1,//reference.attributes.linkdef_target[0]?.upperBound || -1,
      });
    })
    return references;
  };

  return Exporter;
})();
