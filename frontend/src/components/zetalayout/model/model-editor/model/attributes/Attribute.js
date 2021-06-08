export class Attribute {

    constructor(data) {
        this.name = (data && data.name) || "default"
        this.globalUnique = (data && data.globalUnique) || false
        this.localUnique = (data && data.localUnique) || false
        this.type = (data && data.type) || "String"
        this.default = (data && data.default) || {type: "String", value: ""}
        this.constant = (data && data.constant) || false
        this.singleAssignment = (data && data.singleAssignment) || false
        this.expression = (data && data.expression) || ""
        this.ordered = (data && data.ordered) || false
        this.transient = (data && data.transient) || false
        this.value = (data && data.value) || {type: "String", value: ""}
    }

    clone() {
        return new Attribute({
            name: this.name,
            globalUnique: this.globalUnique,
            localUnique: this.localUnique,
            type: this.type,
            default: this.default,
            constant: this.constant,
            singleAssignment: this.singleAssignment,
            expression: this.expression,
            ordered: this.ordered,
            transient: this.transient,
            value: this.value
        });
    }
}
