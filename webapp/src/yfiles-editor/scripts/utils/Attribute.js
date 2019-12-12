export class Attribute {

    constructor(data) {

        this.name = (data && data.name) || "default"
        this.globalUnique = (data && data.globalUnique) || false
        this.localUnique = (data && data.localUnique) || false
        this.type = (data && data.type) || ""
        this.default = (data && data.default) || {type: "String", value: ""}
        this.constant = (data && data.constant) || false
        this.singleAssignment = (data && data.singleAssignment) || false
        this.expression = (data && data.expression) || false
        this.transient = (data && data.transient) || false
    }
}