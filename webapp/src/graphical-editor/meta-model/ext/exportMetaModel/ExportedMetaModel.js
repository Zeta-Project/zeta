/*
  An instance of this class will be returned from the exporting process.
  It contains the information about the exported meta model.
 */
export default (function() {
  function ExportedMetaModel() {
    this.valid = false;
    this.messages = null;
    this.classes = null;
    this.references = null;
    this.enums = null;
    this.attributes = null;
    this.methods = null;
  }

  ExportedMetaModel.prototype.setValid = function(valid) {
    return this.valid = valid;
  };

  ExportedMetaModel.prototype.isValid = function() {
    return this.valid;
  };

  ExportedMetaModel.prototype.setMessages = function(messages) {
    return this.messages = messages;
  };

  ExportedMetaModel.prototype.getMessages = function() {
    return this.messages;
  };

  ExportedMetaModel.prototype.setClasses = function(classes) {
    return this.classes = classes;
  };

  ExportedMetaModel.prototype.getClasses = function() {
    return this.classes;
  };

  ExportedMetaModel.prototype.setReferences = function(references) {
    return this.references = references;
  };

  ExportedMetaModel.prototype.getReferences = function() {
    return this.references;
  };

  ExportedMetaModel.prototype.setEnums = function(enums) {
    return this.enums = enums;
  };

  ExportedMetaModel.prototype.getEnums = function() {
    return this.enums;
  };

  ExportedMetaModel.prototype.setAttributes = function(attributes) {
    return this.attributes = attributes;
  };

  ExportedMetaModel.prototype.getAttributes = function() {
    return this.attributes;
  };

  ExportedMetaModel.prototype.setMethods = function(methods) {
    return this.methods = methods;
  };

  ExportedMetaModel.prototype.getMethods = function() {
    return this.methods;
  };

  return ExportedMetaModel;
})();
