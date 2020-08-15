import {isSuccessStatus, ZetaApiWrapper} from "../../../utils/ZetaApiWrapper";
import {showSnackbar} from "../../../utils/Snackbar";
import {saveAs} from "file-saver";
import definition from "../../../devEnv/graphData/definition";

export let defaultGraph = null;

/**
 * Simulates an REST call to return a predefined, hard-coded graph
 */
export function getDefaultGraph() {
    let uuid = "d882f50c-7e89-48cf-8fea-1e0ea5feb8b7";
    const args = process.argv.slice(2);

    return new Promise((resolve, reject) => {
        if (process.env.NODE_ENV === 'remote') {
            const zetaApiWrapper = new ZetaApiWrapper();
            zetaApiWrapper.rootUrl = process.env.ZETA_DEV_ROOT_URL;
            zetaApiWrapper.email = process.env.ZETA_DEV_USER_EMAIL;
            zetaApiWrapper.password = process.env.ZETA_DEV_USER_PASSWORD;
            uuid = process.env.ZETA_DEV_PROJECT_UUID;
            zetaApiWrapper.getConceptDefinition(uuid)
                .then(data => {
                    const loadedMetaModel = {
                        uuid: uuid,
                        name: "petrinet",
                        concept: data
                    };

                    resolve(loadedMetaModel)
                })
                .catch(error => reject(error));

            // override rootUrl for later save model calls
            ZetaApiWrapper.prototype.rootUrl = process.env.ZETA_DEV_ROOT_URL;

        } else {
            // override postConceptDefinition with local storage logic
            ZetaApiWrapper.prototype.postConceptDefinition = function (metaModelId, jsonValue) {
                return new Promise(function (resolve, reject) {
                    const blob = new Blob([jsonValue], {type: "application/json;charset=utf-8"});
                    saveAs(blob, metaModelId + ".json");
                });
            };

            const loadedMetaModel = {
                uuid: uuid,
                name: "petrinet",
                concept: definition
            };

            resolve(loadedMetaModel)
        }
    })
}
