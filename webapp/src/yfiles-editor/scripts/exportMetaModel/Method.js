const Method = (function () {
    function Method(cellName, methodKey) {
        this.cellName = cellName;
        this.methodKey = methodKey;
    }

    Method.prototype.getCellName = function () {
        return this.cellName;
    };

    Method.prototype.getMethodKey = function () {
        return this.methodKey;
    };

    return Method;
})();
