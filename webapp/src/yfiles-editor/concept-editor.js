import {YFilesZeta} from "./scripts/app";
import {ZetaApiWrapper} from "./scripts/ZetaApiWrapper";

let loadedMetaModel = window.loadedMetaModel;

const zetaApiWrapper = new ZetaApiWrapper();
zetaApiWrapper.getConceptDefinition(loadedMetaModel.uuid).then(data => {
        loadedMetaModel.concept = data
    });

new YFilesZeta(loadedMetaModel);
