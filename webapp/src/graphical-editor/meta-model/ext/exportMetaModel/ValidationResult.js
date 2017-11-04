export default (function() {
  function ValidationResult() {
    this.valid = true;
    this.messages = [];
  }

  ValidationResult.prototype.addErrorMessage = function(message) {
    this.valid = false;
    return this.messages.push(message);
  };

  ValidationResult.prototype.isValid = function() {
    return this.valid;
  };

  ValidationResult.prototype.getMessages = function() {
    return this.messages;
  };

  return ValidationResult;
})();
