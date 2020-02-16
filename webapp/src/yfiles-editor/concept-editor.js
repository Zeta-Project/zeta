import {YFilesZeta} from "./scripts/app";
import {ZetaApiWrapper} from "./scripts/ZetaApiWrapper";

let loadedMetaModel = {
    uuid: window.loadedMetaModel.uuid,
    name: window.loadedMetaModel.name,
    concept: window.loadedMetaModel.concept
};

console.log(loadedMetaModel)

// const zetaApiWrapper = new ZetaApiWrapper();
// zetaApiWrapper.getConceptDefinition(loadedMetaModel.uuid).then(data => {
//         loadedMetaModel.concept = data
//     });

new YFilesZeta(loadedMetaModel);
