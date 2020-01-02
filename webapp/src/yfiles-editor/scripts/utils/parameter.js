export class Parameter {

    constructor(data) {

        this.value = (data && data.value) || ""
        this.type = (data && data.type) || ""
    }
}

