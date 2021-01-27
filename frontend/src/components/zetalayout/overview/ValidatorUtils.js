import axios from "axios";
import {EventBus} from "@/eventbus/eventbus";

const generate = function (metaModelId) {
    axios.get(
        "http://localhost:9000/rest/v1/meta-models/" + metaModelId + "/validator?generate=true",
        {withCredentials: true}
    ).then(
        (response) => EventBus.$emit("successMessage", "Validator successfully generated"),
        (error) => EventBus.$emit("errorMessage", "Failed to generate Validator: " + error)
    )
}

const show = function(metaModelId) {
    axios.get(
        "http://localhost:9000/rest/v1/meta-models/" + metaModelId + "/validator?generate=true",
        {withCredentials: true}
    ).then(
        (response) => {
            openWindow("<pre>" + response.data + "</pre>")
            switch (response.status) {
                case 200:
                    EventBus.$emit("successMessage", "Validator successfully generated")
                    break;
                case 201:
                    EventBus.$emit("successMessage", "Existing validator successfully loaded")
                    break;
            }
        },
        (error) => EventBus.$emit("errorMessage", error)
    )
}
const validate = function(modelId) {
    axios.get("" +
        "http://localhost:9000/rest/v1/models/" + modelId + "/validation",
        {withCredentials: true}
    ).then(
        (response) => openWindow("<pre>" + validationResultToString(response.data) + "</pre>"),
        (error) => EventBus.$emit("errorMessage", error)
    )
}
const validationResultToString = function(result) {
    var list = result.map(function (res) {
        var string = "Rule \"" + res.rule.name + "\" failed";
        if (res.element !== null) {
            string += " for " + res.element.type + " of type \"" + res.element.typeName + "\" (" + res.element.type + "-id: " + res.element.id + ")"
        }
        string += ".\n";
        string += "\t- description: \"" + res.rule.description + "\"\n";
        string += "\t- possible fix: \"" + res.rule.possibleFix + "\"";
        return string;
    });

    var listString = "";
    for (var i = 0; i < list.length; ++i) {
        listString += "* " + list[i] + "\n\n";
    }

    if (result.length === 0) {
        return "Model instance is valid."
    } else {
        return "Model instance is invalid:\n\n" + listString;
    }
}

const openWindow = function(data) {
    var win = window.open('', '', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=300');
    win.document.body.innerHTML = data;
    return win;
}

export default {
    validate,
    show,
    generate
}
