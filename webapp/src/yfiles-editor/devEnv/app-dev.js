import 'yfiles/yfiles.css';
import {Class, LayoutExecutor, License} from 'yfiles'

import '../styles/layout.css'
import '../styles/paper.css'
import '../styles/stencil.css'
import '../styles/style.css'
import '../styles/toolbar.css'
import '../styles/sidebar.css'
import {YFilesZeta} from "../scripts/app";
import definition from "./graphData/definition";
import {ZetaApiWrapper} from "../scripts/ZetaApiWrapper";
import {showSnackbar} from "../scripts/utils/AppStyle";
import {saveAs} from "file-saver";


// Tell the library about the license contents
License.value = require('../../../../yFiles-dev-key/license.json');

// We need to load the yfiles/view-layout-bridge module explicitly to prevent the webpack
// tree shaker from removing this dependency which is needed for 'morphLayout' in this demo.
Class.ensure(LayoutExecutor);


/**
 * A simple yFiles application that creates a GraphComponent and enables basic input gestures.
 */

//move graph inside class YFilesZeta?
let graphComponent = null;

class YFilesZetaDev {

    constructor() {
        this.initialize();
    }

    initialize() {

        let uuid = "d882f50c-7e89-48cf-8fea-1e0ea5feb8b7";

        const args = process.argv.slice(2);
        if(process.env.NODE_ENV === 'remote') {
            const zetaApiWrapper = new ZetaApiWrapper();
            zetaApiWrapper.rootUrl = process.env.ZETA_DEV_ROOT_URL;
            zetaApiWrapper.email = process.env.ZETA_DEV_USER_EMAIL;
            zetaApiWrapper.password = process.env.ZETA_DEV_USER_PASSWORD;
            uuid = process.env.ZETA_DEV_PROJECT_UUID;
            zetaApiWrapper.getConceptDefinition(uuid).then(data => {
                const loadedMetaModel = {
                    uuid: uuid,
                    name: "petrinet",
                    concept: data
                };

                new YFilesZeta(loadedMetaModel);
            }).catch(reason => {
                showSnackbar("Problem to load concept definition from server: " + reason);
            });

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

            new YFilesZeta(loadedMetaModel);
        }
    }
}

new YFilesZetaDev();
