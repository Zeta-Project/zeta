export class Method {

    constructor(data) {
        this.name = (data && data.name) || "default"
        this.parameters = (data && data.parameters) || []
        this.description = (data && data.description) || ""
        this.returnType = (data && data.returnType) || ""
        this.code = (data && data.code) || ""
        this.value = (data && data.value) || ""
    }

    clone() {
        return new Method({
            name: this.name,
            parameters: this.parameters,
            description: this.description,
            returnType: this.returnType,
            code: this.code,
            value: this.value
        });
    }
}

