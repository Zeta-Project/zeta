//Todo check if classes are better located inside UMLClassModel

export class Operation {

    constructor(data) {

        this._name = (data && data._name) || ""
        this._parameters = (data && data._parameters) || []
        this._description = (data && data.localUnique) || ""
        this._returnType = (data && data._returnType) || ""
        this._code = (data && data._code) || ""
    }

    get name() {
        return this._name;
    }

    set name(value) {
        this._name = value;
    }

    get parameters() {
        return this._parameters;
    }

    set parameters(value) {
        this._parameters = value;
    }

    get description() {
        return this._description;
    }

    set description(value) {
        this._description = value;
    }

    get returnType() {
        return this._returnType;
    }

    set returnType(value) {
        this._returnType = value;
    }

    get code() {
        return this._code;
    }

    set code(value) {
        this._code = value;
    }
}