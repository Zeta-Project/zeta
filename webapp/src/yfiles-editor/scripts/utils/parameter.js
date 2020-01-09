export class Parameter {

    constructor(data) {

        this.value = (data && data.value) || "default"
        this.type = (data && data.type) || "String"
    }
}

