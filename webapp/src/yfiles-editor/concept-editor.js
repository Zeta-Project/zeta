import {YFilesZeta} from "./scripts/app";
import {ZetaApiWrapper} from "./scripts/ZetaApiWrapper";

let loadedMetaModel = {};
loadedMetaModel.uuid = window.loadedMetaModel.uuid;
loadedMetaModel.name = window.loadedMetaModel.name;
loadedMetaModel.concept = window.loadedMetaModel.concept;

console.log(loadedMetaModel);

// const zetaApiWrapper = new ZetaApiWrapper();
// zetaApiWrapper.getConceptDefinition(loadedMetaModel.uuid).then(data => {
//         loadedMetaModel.concept = data
//     });

new YFilesZeta(loadedMetaModel);
