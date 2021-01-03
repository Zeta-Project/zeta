export class Parameter {

    constructor(data) {

        this.name = (data && data.value) || "default"
        this.type = (data && data.type) || "String"
    }
}

