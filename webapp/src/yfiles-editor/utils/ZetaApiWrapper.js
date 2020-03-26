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

        return new Promise((resolve, reject) => fetch(this.rootUrl + "/user")
            .then(isSuccessStatus).then(() => resolve(true)));
    }

    getConceptDefinition(metaModelId) {
        return new Promise((resolve, reject) => this.isAuthenticated().then(fetchDefinition(this.rootUrl, metaModelId).then(data =>
            resolve(data))).catch(() => {
            this.authenticate().then(isSuccessStatus).then(fetchDefinition(this.rootUrl, metaModelId).then(data => resolve(data))).catch(reason => reject(reason))
        }));
    }
}

ZetaApiWrapper.prototype.postConceptDefinition = function (metaModelId, jsonValue) {

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

export function isSuccessStatus(response) {

    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response)
    } else {
        return Promise.reject(new Error(response.statusText))
    }
}

function fetchDefinition(rootUrl, metaModelId) {
    const url = rootUrl + "/rest/v1/meta-models/" + metaModelId + "/definition"

    return fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include"
    }).then(isSuccessStatus).then(response => response.json())
}
