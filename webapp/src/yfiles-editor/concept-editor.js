import '../webpage'
import {YFilesZeta} from "./app";
import {ZetaApiWrapper} from "./utils/ZetaApiWrapper";
import {showSnackbar} from "./utils/Snackbar";

let loadedMetaModel = {};
loadedMetaModel.uuid = window.loadedMetaModel.uuid;
loadedMetaModel.name = window.loadedMetaModel.name;

const zetaApiWrapper = new ZetaApiWrapper();
zetaApiWrapper.getConceptDefinition(loadedMetaModel.uuid).then(data => {
    loadedMetaModel.concept = data

    new YFilesZeta(loadedMetaModel);
}).catch(reason => {
    showSnackbar("Problem to load concept definition: " + reason);
});

// override rootUrl for later intern save model calls
ZetaApiWrapper.prototype.rootUrl = "";


