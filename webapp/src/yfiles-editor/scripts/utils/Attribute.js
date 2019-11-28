//Todo check if classes are better located inside UMLClassModel

export class Attribute {

    constructor(data) {

        this._name = (data && data._name) || ""
        this._globalUnique = (data && data._globalUnique) || false
        this._localUnique = (data && data._localUnique) || false
        this._type = (data && data._type) || ""
        this._default = {type: "String", value: ""}
        this._constant = (data && data._constant) || false
        this._singleAssignment = (data && data._singleAssignment) || false
        this._expression = (data && data._expression) || false
        this._transient = (data && data._transient) || false
    }

    get name() {
        return this._name;
    }

    set name(value) {
        this._name = value;
    }

    get globalUnique() {
        return this._globalUnique;
    }

    set globalUnique(value) {
        this._globalUnique = value;
    }

    get localUnique() {
        return this._localUnique;
    }

    set localUnique(value) {
        this._localUnique = value;
    }

    get type() {
        return this._type;
    }

    set type(value) {
        this._type = value;
    }

    get default() {
        return this._default;
    }

    set default(value) {
        this._default = value;
    }

    get constant() {
        return this._constant;
    }

    set constant(value) {
        this._constant = value;
    }

    get singleAssignment() {
        return this._singleAssignment;
    }

    set singleAssignment(value) {
        this._singleAssignment = value;
    }

    get expression() {
        return this._expression;
    }

    set expression(value) {
        this._expression = value;
    }

    get transient() {
        return this._transient;
    }

    set transient(value) {
        this._transient = value;
    }
}