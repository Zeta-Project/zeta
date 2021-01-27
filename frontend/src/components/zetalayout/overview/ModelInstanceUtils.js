import axios from "axios";
import {EventBus} from "@/eventbus/eventbus";
import router from "@/router";

const createInstance = function(name, metaModelId) {
    if (name === "") {
        return;
    }
    const model = {
        name: name,
        graphicalDslId: metaModelId
    };

    axios.post(
        "http://localhost:9000/rest/v1/models",
        model,
        {withCredentials: true}
    ).then(
        (response) => {
            EventBus.$emit('reloadProjects')
        },
        (error) => EventBus.$emit("errorMessage", "Failed creating model instance: " + error)
    )
}

const deleteInstance = function(modelId) {
    axios.delete("http://localhost:9000/rest/v1/models/" + modelId, {withCredentials: true}).then(
        (response) => {
            EventBus.$emit('reloadProjects')
        },
        (error) => EventBus.$emit("errorMessage", "Failed deleting model instance: " + error)
    )
}

export default {
    createInstance,
    deleteInstance
}
