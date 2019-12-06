import 'yfiles/yfiles.css';
import {Class, LayoutExecutor, License} from 'yfiles'

import '../styles/layout.css'
import '../styles/paper.css'
import '../styles/stencil.css'
import '../styles/style.css'
import '../styles/toolbar.css'
import {YFilesZeta} from "../scripts/app";
import definition from "./graphData/definition";
import {ZetaApiWrapper} from "../scripts/ZetaApiWrapper";
import {showSnackbar} from "../scripts/utils/AppStyle";
import {saveAs} from "file-saver";


// Tell the library about the license contents
License.value = require('../../../../../yFiles-for-HTML-Complete-2.2.0.2/lib/license.json');

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

        let conceptDefinition = definition;
        let uuid = "d882f50c-7e89-48cf-8fea-1e0ea5feb8b7";

        const args = process.argv.slice(2);
        if(args.length === 4) {
            const zetaApiWrapper = new ZetaApiWrapper();
            zetaApiWrapper.rootUrl = args[0];
            zetaApiWrapper.email = args[1];
            zetaApiWrapper.password = args[2];
            uuid = args[3];
            zetaApiWrapper.getConceptDefinition(uuid).then(data => {
                conceptDefinition = data;
            }).catch(reason => {
                showSnackbar("Problem to load concept definition from server: " + reason);
            })
        } else {
            // override postConceptDefinition with local storage logic
            ZetaApiWrapper.prototype.postConceptDefinition = function (metaModelId, jsonValue) {
                return new Promise(function (resolve, reject) {
                    const blob = new Blob([jsonValue], {type: "application/json;charset=utf-8"});
                    saveAs(blob, metaModelId + ".json");
                });
            }
        }

        const loadedMetaModel = {
            uuid: uuid,
            name: "petrinet",
            concept: conceptDefinition
        }

        new YFilesZeta(loadedMetaModel);

    }
}

new YFilesZetaDev();
