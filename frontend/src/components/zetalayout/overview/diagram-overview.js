import jQuery from "jquery"
window.jQuery = jQuery;
window.$ = jQuery;
import modelValidatorUtil from "./modelValidatorUtil";
require('bootstrap');
import axios from 'axios'
import router from "@/router";
import {EventBus} from "@/eventbus/eventbus"

(function ($) {
    'use strict';
    const createProject = function () {

        const name = $("#inputProjectName").val();
        if (name === "") return;

        axios.post(
            "http://localhost:9000/rest/v1/meta-models",
            {name: name},
            {withCredentials: true}).then(
            (response) => {
                EventBus.$emit('metaModelAdded', {id: response.data.id, name: response.data.name});
            },
            (error) => alert("Could not create meta model: " + error)

        )

    };

    const createModelInstance = function () {
        const name = $("#inputModelName").val();
        if (name === "") {
            return;
        }

        const model = {
            name: name,
            graphicalDslId: window.metaModelId
        };

        $.ajax({
            type: 'POST',
            url: '/rest/v1/models',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(model),
            success: function (data, textStatus, jqXHR) {
                window.location.replace("/overview/" + window.metaModelId);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("failed creating model instance: " + textStatus);
            }
        });
    };

    const deleteModelInstance = function (modelId) {
        $.ajax({
            type: 'DELETE',
            url: '/rest/v1/models/' + modelId,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            success: function (data, textStatus, jqXHR) {
                window.location.replace("/overview/" + window.metaModelId);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("failed deleting model instance: " + textStatus);
            }
        });
    };


    $(function () {
        $('[data-toggle="tooltip"]').tooltip();

        $("#btnCreateMetaModel").click(createProject);

        $("#inputProjectName").keypress(function (e) {
            if (e.which === 13) {
                createProject();
                return false;
            }
        });

        $(".delete-project").click(function () {

            event.preventDefault();
            const metaModelId = this.dataset.metamodelId;

            /*$.ajax({
                type: 'DELETE',
                url: '/rest/v1/meta-models/' + metaModelId,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                success: function (data, textStatus, jqXHR) {
                    window.location.replace("/overview");
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert("Could not delete meta model: " + textStatus);
                }
            });*/
            axios.delete(
                "http://localhost:9000/rest/v1/meta-models/" + metaModelId,
                {withCredentials: true}
            ).then(
                (response) => EventBus.$emit("metaModelRemoved", metaModelId),
                (error) => alert("Could not delete meta model: " + error)
            )
        });


        let selectedProjectId = null;
        $(".invite-to-project").click(function () {
            selectedProjectId = this.dataset.metamodelId;
            jQuery('#inviteModal').modal('show');
        });

        $("#inviteProjectName").on('input', () => {
            const isValid = $("#inviteProjectName").val().trim().length !== 0;
            $("#start-invite-btn").prop("disabled", !isValid);
        });

        $("#start-invite-btn").click(function () {
            const metaModelId = selectedProjectId;
            const email = $("#inviteProjectName").val().trim();
            $.ajax({
                type: 'GET',
                url: `/rest/v2/invite-to-project/${metaModelId}/${email}`,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                success: () => {
                    location.reload()
                },
                error: () => {
                    alert("failed to invite the user to the project, probably there is no user with this email");
                }
            });
        });

        $(".duplicate-project").click(function () {
            selectedProjectId = this.dataset.metamodelId;
            jQuery('#duplicateModal').modal('show');
        });

        $("#duplicateProjectName").on('input', () => {
            const isValid = $("#duplicateProjectName").val().trim().length !== 0;
            $("#start-duplicate-btn").prop("disabled", !isValid);
        });

        $("#start-duplicate-btn").click(() => {
            const metaModelId = selectedProjectId;
            const name = $("#duplicateProjectName").val().trim();
            $.ajax({
                type: 'GET',
                url: `/rest/v2/duplicate-project/${metaModelId}/${name}`,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                success: () => {
                    location.reload()
                },
                error: () => {
                    alert("failed to duplicate the project");
                }
            });
        });


        $(".export-project").click(function (e) {
            const metaModelId = this.dataset.metamodelId
            if (metaModelId) {
                const url = 'http://localhost:9000/rest/v2/models/' + metaModelId + '/exportProject';
                window.open(url, '_blank');
            }
        });

        $("#btnGenerator").click(function () {
            $.ajax({
                type: 'GET',
                url: '/generator/' + window.metaModelId,
                //contentType: "application/json; charset=utf-8",
                //dataType: "json",
                success: function (data, textStatus, jqXHR) {
                    showSuccess(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText)
                }
            });
        });

        $('#validatorGenerate').click(function () {
            modelValidatorUtil.generate(window.metaModelId, {
                success: function(data, textStatus, jqXHR) {
                    showSuccess("Validator successfully generated");
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                }
            });
        });

        $('#validatorShow').click(function () {
            modelValidatorUtil.show(window.metaModelId, {
                openWindow: true,
                success: function(data, textStatus, jqXHR) {
                    switch (data.status) {
                        case 200:
                            showSuccess("Validator successfully generated.");
                            break;
                        case 201:
                            showSuccess("Existing validator successfully loaded.");
                            break;
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                }
            });
        });

        $(".validate-model-instance").click(function () {
            event.preventDefault();
            modelValidatorUtil.validate(this.dataset.modelId, {
                openWindow: true,
                error: function(jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                }
            });
        });

        $("#inputModelName").keypress(function (e) {
            if (e.which == 13) {
                createModelInstance();
                return false;
            }
        });

        $("#btnCreateModelInstance").click(createModelInstance);

        $(".delete-model-instance").click(function () {
            //prevent default otherwise href to modelEditor
            event.preventDefault();
            var modelId = this.dataset.modelId;
            deleteModelInstance(modelId);
        });

        const showError = function (text) {
            $("#error-panel").fadeOut('slow', function () {
                $("#error-panel").find("div").text(text);
                $("#error-panel").fadeIn('slow');
            });
        };

        const showSuccess = function (text) {
            $("#success-panel").fadeOut('slow', function () {
                $("#success-panel").show();
                $("#success-panel").find("div").text(text);
                $("#success-panel").fadeIn('slow');
            });
        };

        $("[data-hide]").on("click", function () {
            $("." + $(this).attr("data-hide")).hide();
        });


        /* IMPORT PROJECT */
        let file = undefined;

        // Open file selector on div click
        $("#uploadfile").click(function(){
            $("#file").click();
        });

        // preventing page from redirecting
        $("html").on("dragover", function(e) {
            e.preventDefault();
            e.stopPropagation();
            $("#uploadtext").text("Drag here");
        });

        // file selected
        $("#file").change(function(){
            file = $('#file')[0].files[0];
            onProjectSelected(file);
            //uploadData(fd);
        });

        // Drop
        $('.upload-area').on('drop', function (e) {
            //e.stopPropagation();
            e.preventDefault();
            file = e.originalEvent.dataTransfer.files[0];
            onProjectSelected(file);
            //uploadData(fd);
        });

        $("#start-import-btn").click(function() {
            if (isValidZetaProjectFile() && isValidProjectName()) {
                const fd = new FormData();
                fd.append('file', file);
                const projectName = getProjectName();
                uploadProject(file, projectName);
            }
        });

        function onProjectSelected() {
            if (isValidZetaProjectFile()) {
                const fd = new FormData();
                fd.append('file', file);
                $("#uploadtext").text(file.name);
                const projectNameFromFile = file.name.split("_")[0];
                $("#importProjectName").val(projectNameFromFile);
                enableOrDisableImportButton();
            } else {
                $("#uploadtext").text("Invalid zeta project file!");
            }
        }

        function isValidZetaProjectFile() {
            return file !== undefined && file.name.endsWith(".zeta");
        }

        function getProjectName() {
            return $("#importProjectName").val().trim();
        }

        function isValidProjectName() {
            return getProjectName() !== "";
        }

        $("#importProjectName").on('input', function() {
            enableOrDisableImportButton();
        });

        function enableOrDisableImportButton() {
            if (isValidZetaProjectFile() && isValidProjectName()) {
                $("#start-import-btn").prop("disabled", false);
            } else {
                $("#start-import-btn").prop("disabled", true);
            }
        }

        // Sending AJAX request and upload file
        function uploadProject(formdata, projectName){
            $("#close-import-modal").click();
            axios.post(
                'http://localhost:9000/rest/v2/projects/import?projectName=' + projectName,
                formdata,
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/zip',
                        'processData': false
                    }}
            ).then(
                (response) => EventBus.$emit('reloadProjects'),
                (error) => showError('Invalid .zeta project file!')
            )
        }

    });
}(jQuery) );
