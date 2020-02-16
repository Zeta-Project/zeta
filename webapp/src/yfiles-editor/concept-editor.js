import {YFilesZeta} from "./scripts/app";
import {ZetaApiWrapper} from "./scripts/ZetaApiWrapper";
import {showSnackbar} from "./scripts/utils/AppStyle";

let loadedMetaModel = {};
loadedMetaModel.uuid = window.loadedMetaModel.uuid;
loadedMetaModel.name = window.loadedMetaModel.name;

console.log(loadedMetaModel);

const zetaApiWrapper = new ZetaApiWrapper();
zetaApiWrapper.getConceptDefinition(loadedMetaModel.uuid).then(data => {
    loadedMetaModel.concept = data

    new YFilesZeta(loadedMetaModel);
}).catch(reason => {
    showSnackbar("Problem to load concept definition: " + reason);
});


