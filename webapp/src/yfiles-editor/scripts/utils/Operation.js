export class Operation {

    constructor(data) {

        this.name = (data && data.name) || "default"
        this.parameters = (data && data.parameters) || {type: "String", name: "default"}
        this.description = (data && data.description) || ""
        this.returnType = (data && data.returnType) || ""
        this.code = (data && data.code) || ""
    }
}