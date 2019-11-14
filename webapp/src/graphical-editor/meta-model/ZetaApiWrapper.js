export class ZetaApiWrapper {

    authenticate() {

        return fetch("http://zeta-dev.syslab.in.htwg-konstanz.de/signIn", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: "email=manuele.lorusso%40htwg-konstanz.de&password=zetadev",
            credentials: "include"
        });
    }

    isAuthenticated() {

        let isAuthenticated = false;

        fetch("http://zeta-dev.syslab.in.htwg-konstanz.de/user").then(checkStatus).then(() => {
            isAuthenticated = true;
        });

        return isAuthenticated;
    }

    getConceptDefinition(metaModelId) {

        const isAuthenticated = this.isAuthenticated();
        if (!isAuthenticated) {
            this.authenticate();
        }

        const url = "http://zeta-dev.syslab.in.htwg-konstanz.de/rest/v1/meta-models/" + metaModelId + "/definition"

        return fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include"
        }).then(checkStatus).then(json)
    }

    postConceptDefinition(metaModelId, jsonValue) {

        const url = "http://zeta-dev.syslab.in.htwg-konstanz.de/rest/v1/meta-models/" + metaModelId + "/definition"

        return fetch(url, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: jsonValue,
            credentials: "include"
        });
    }
}

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
