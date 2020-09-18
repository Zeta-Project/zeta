import axios from "axios";
import {EventBus} from "@/eventbus/eventbus";

const importProject = function(file, projectName) {

    projectName = projectName.trim();
    axios.post(
        'http://localhost:9000/rest/v2/projects/import?projectName=' + projectName,
        file,
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/zip',
                'processData': false
            }}
    ).then(
        (response) => EventBus.$emit('reloadProjects'),
        (error) => EventBus.$emit("errorMessage", 'Invalid .zeta project file!')
    )
}

const createProject = function(name) {
    if (name === "") return;
    axios.post(
        "http://localhost:9000/rest/v1/meta-models",
        {name: name},
        {withCredentials: true}
    ).then(
        (response) => {
            EventBus.$emit('metaModelAdded', {id: response.data.id, name: response.data.name});
            setProjectDefinition(response.data.id)
        },
        (error) => EventBus.$emit("errorMessage","Could not create metamodel: " + error)
    )
}

const setProjectDefinition = function(metaModelId) {
    const defaultMetamodelDefinition = require('./defaultMetamodelDefinition.json')
    axios.put(
        "http://localhost:9000/rest/v1/meta-models/" + metaModelId +"/definition",
        defaultMetamodelDefinition,
        { withCredentials: true}
    ).then(
        (response) => EventBus.$emit("successMessage","Successfully created new metamodel"),
        (error) => EventBus.$emit("errorMessage","Failed to created metamodel-definition: " + error)
    )
}

const deleteProject = function(metaModelId) {
    axios.delete(
        "http://localhost:9000/rest/v1/meta-models/" + metaModelId,
        {withCredentials: true}
    ).then(
        (response) => EventBus.$emit("metaModelRemoved", metaModelId),
        (error) => EventBus.$emit("errorMessage","Could not delete meta model: " + error)
    )
}

const inviteToProject = function(metaModelId, email) {
    email = email.trim()
    axios.get(
        "http://localhost:9000/rest/v2/invite-to-project/" + metaModelId + "/" + email,
        {withCredentials: true}
    ).then(
        (response) => location.reload(),
        (error) => EventBus.$emit("errorMessage","Failed to invite the user to the project," +
            "probably there is no user with this email")
    )
}

const duplicateProject = function (metaModelId, name) {
    name = name.trim()
    axios.get(
        "http://localhost:9000/rest/v2/duplicate-project/" + metaModelId + "/" + name,
        {withCredentials: true}
    ).then(
        (response) => EventBus.$emit("reloadProjects"),
        (error) => EventBus.$emit("errorMessage", "Failed to duplicate the project")
    )
}

const exportProject = function(metaModelId) {
    if (metaModelId) {
        const url = 'http://localhost:9000/rest/v2/models/' + metaModelId + '/exportProject';
        window.open(url, '_blank');
    }
}

export default {
    importProject,
    createProject,
    deleteProject,
    inviteToProject,
    duplicateProject,
    exportProject
}
