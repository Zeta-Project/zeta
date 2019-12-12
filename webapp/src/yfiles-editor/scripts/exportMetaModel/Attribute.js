export default (function() {
  function Attribute(cellName, attributeKey) {
    this.cellName = cellName;
    this.attributeKey = attributeKey;
  }

  Attribute.prototype.getCellName = function() {
    return this.cellName;
  };

  Attribute.prototype.getAttributeKey = function() {
    return this.attributeKey;
  };

  return Attribute;
})();
