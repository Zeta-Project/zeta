export class Operation {

    constructor(data) {

        this.name = (data && data.name) || "default"
        this.parameters = (data && data.parameters) || []
        this.description = (data && data.description) || ""
        this.returnType = (data && data.returnType) || ""
        this.code = (data && data.code) || ""
    }
}