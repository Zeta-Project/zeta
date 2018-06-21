import './webpage';
import jQuery from "jquery";
import modelValidatorUtil from './modelValidatorUtil';

(function ($) {
    'use strict';
    const createProject = function () {

        const name = $("#inputProjectName").val();
        if (name === "") return;

        const data = JSON.stringify({
            "name": name,
        });

        $.ajax({
            type: 'POST',
            url: '/rest/v1/meta-models',
            data: data,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                window.location.replace("/overview/" + data.id);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Could not create meta model: " + textStatus);
            }
        });

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

            $.ajax({
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
            });
        });

        $(".export-project").click(function() {
          const url = '/rest/v2/models/' + window.metaModelId + '/exportProject';
          window.open(url);
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

        $("#btnDeleteAllModelInstances").click(function () {
            $("#model-instance-container").children().map(function () {
                if ($(this).is('a')) {
                    var modelId = $(this).children(":first").data("model-id");
                    deleteModelInstance(modelId);
                }
            })
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

    });
}(jQuery) );
