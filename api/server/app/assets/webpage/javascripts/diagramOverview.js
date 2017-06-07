(function ($) {
    'use strict';
    var createProject = function () {

        var name = $("#inputProjectName").val();
        if (name === "") return;

        var data = JSON.stringify({
            "name": name,
            "elements": [],
            "uiState": ""
        });

        $.ajax({
            type: 'POST',
            url: '/metamodels',
            data: data,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                window.location.replace("/overview/" + data._id);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Could not create meta model: " + textStatus);
            }
        });

    };

    var createModelInstance = function () {
        var name = $("#inputModelName").val();
        if (name === "") return;
        var id = window.metaModelId;
        var data = {
            "metaModelId": id,
            "model": {
                elements: [],
                name: name,
                uiState: ""
            }
        };

        $.ajax({
            type: 'POST',
            url: '/models',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function (data, textStatus, jqXHR) {
                window.location.replace("/overview/" + window.metaModelId);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("failed creating model instance: " + textStatus);
            }
        });
    };

    var deleteModelInstance = function (modelId) {
        $.ajax({
            type: 'DELETE',
            url: '/models/' + modelId,
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
            if (e.which == 13) {
                createProject();
                return false;
            }
        });

        $(".delete-project").click(function () {

            event.preventDefault();
            var metamodelId = this.dataset.metamodelId;

            $.ajax({
                type: 'DELETE',
                url: '/metamodels/' + metamodelId,
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
            $.ajax({
                type: 'GET',
                url: '/metamodels/' + window.metaModelId + '/validator?generate=true&noContent=true',
                success: function (data, textStatus, jqXHR) {
                    showSuccess("Validator successfully generated");
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                }
            });
        });

        $('#validatorShow').click(function () {
            var win = window.open('', '', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=600');
            win.document.body.innerHTML = "waiting for data...";
            $.ajax({
                type: 'GET',
                url: '/metamodels/' + window.metaModelId + '/validator?generate=false&noContent=false',
                success: function (data, textStatus, jqXHR) {
                    switch (data.status) {
                        case 200:
                            showSuccess("Validator successfully generated.");
                            break;
                        case 201:
                            showSuccess("Existing validator successfully loaded.");
                            break;
                    }
                    win.document.body.innerHTML = "<pre>" + data + "</pre>";
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                    win.close();
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

        $(".validate-model-instance").click(function () {
            event.preventDefault();
            var modelId = this.dataset.modelId;
            validateModelInstance(modelId);
        });

        $("#btnDeleteAllModelInstances").click(function () {
            $("#model-instance-container").children().map(function () {
                if ($(this).is('a')) {
                    var modelId = $(this).children(":first").data("model-id");
                    deleteModelInstance(modelId);
                }
            })
        });

        var showError = function (text) {
            $("#error-panel").fadeOut('slow', function () {
                $("#error-panel").find("div").text(text);
                $("#error-panel").fadeIn('slow');
            });
        };

        var showSuccess = function (text) {
            $("#success-panel").fadeOut('slow', function () {
                $("#success-panel").show();
                $("#success-panel").find("div").text(text);
                $("#success-panel").fadeIn('slow');
            });
        };

        $("[data-hide]").on("click", function () {
            $("." + $(this).attr("data-hide")).hide();
        });

        var validationResultToString = function (result) {

            var list = result.map(function (res) {
                var string = "Rule \"" + res.rule.name + "\" failed";
                if (res.element !== null) {
                    string += " for " + res.element.type + " of type \"" + res.element.typeName + "\" (" + res.element.type + "-id: " + res.element.id + ")"
                }
                string += ".\n";
                string += "\tdescription: \"" + res.rule.description + "\"\n";
                string += "\tpossible fix: \"" + res.rule.possibleFix + "\"";
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
        };

        var validateModelInstance = function (modelId) {
            var win = window.open('', '', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=1000, height=800');
            win.document.body.innerHTML = "waiting for data...";
            $.ajax({
                type: 'GET',
                url: '/models/' + modelId + '/validation',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                success: function (data, textStatus, jqXHR) {
                    win.document.body.innerHTML = "<pre>" + validationResultToString(data) + "</pre>";
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showError(jqXHR.responseText);
                    win.close();
                }
            });
        };

    });
}(jQuery) );
