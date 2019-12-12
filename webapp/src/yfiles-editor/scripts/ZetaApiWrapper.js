export class ZetaApiWrapper {

    constructor() {
        this.rootUrl = "";
        this.email = "";
        this.password = "";
    }


    authenticate() {

        return fetch(this.rootUrl + "/signIn", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: "email=" + encodeURIComponent(this.email) + "&password=" + encodeURIComponent(this.password),
            credentials: "include"
        });
    }

    isAuthenticated() {

        let isAuthenticated = false;

        fetch(this.rootUrl + "/user").then(checkStatus).then(() => {
            isAuthenticated = true;
        });

        return isAuthenticated;
    }

    getConceptDefinition(metaModelId) {

        const isAuthenticated = this.isAuthenticated();
        if (!isAuthenticated) {
            this.authenticate();
        }

        const url = this.rootUrl + "/rest/v1/meta-models/" + metaModelId + "/definition"

        return fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include"
        }).then(checkStatus).then(json)
    }
}

ZetaApiWrapper.prototype.postConceptDefinition = function(metaModelId, jsonValue) {

    const url = this.rootUrl + "/rest/v1/meta-models/" + metaModelId + "/definition";

    return fetch(url, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: jsonValue,
        credentials: "include"
    });
};

export function checkStatus(response) {

    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function json(response) {

    return response.json()
}
