(function ($) {
    'use strict';
    var createProject = function () {

        var name = $("#inputProjectName").val();
        if (name === "") return;

        var data = JSON.stringify({
            "metaModel": {
                "name": name,
                "elements": [],
                "uiState": ""
            }
        });
        var fnCreateProject = function (accessTokenString, tokenRefreshed, error) {
            if (error) {
                if(error === 'Unauthorized') window.location.replace('/auth/login');
                return;
            }

            $.ajax({
                type: 'POST',
                url: '/metamodels',
                headers: {
                    Authorization: "Bearer " + accessTokenString,
                    'Content-Type': 'application/json'
                },
                data: data,
                success: function (data, textStatus, jqXHR) {
                    window.location.replace("/overview/" + data.insertId);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (!tokenRefreshed) {
                        accessToken.authorized(fnCreateProject, true);
                    } else {
                        alert("Could not create meta model: " + textStatus);
                    }
                }
            });
        };

        accessToken.authorized(fnCreateProject);
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

        var fnCreateModelInstance = function (accessTokenString, tokenRefreshed, error) {
            if (error) {
                alert("Could not create new Model Instance " + error);
                return;
            }

            $.ajax({
                type: 'POST',
                url: '/models',
                headers: {
                    Authorization: "Bearer " + accessTokenString,
                    Accept: 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: "json",
                data: JSON.stringify(data),
                success: function (data, textStatus, jqXHR) {
                    window.location.replace("/overview/" + window.metaModelId);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (!tokenRefreshed) {
                        accessToken.authorized(fnCreateModelInstance, true);
                    } else {
                        alert("failed creating model instance: " + textStatus);
                    }
                }
            });
        };

        accessToken.authorized(fnCreateModelInstance);
    };

    var deleteModelInstance = function (modelId) {
        var fnDeleteModelInstance = function (accessTokenString, tokenRefreshed, error) {
            if (error) {
                alert("Could not delete model Instance " + error);
                return;
            }
            $.ajax({
                type: 'DELETE',
                url: '/models/' + modelId,
                headers: {
                    Authorization: "Bearer " + accessTokenString
                },
                success: function (data, textStatus, jqXHR) {
                    window.location.replace("/overview/" + window.metaModelId);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (!tokenRefreshed) {
                        accessToken.authorized(fnDeleteModelInstance, true);
                    } else {
                        alert("failed deleting model instance: " + textStatus);
                    }
                }
            });
        };
        accessToken.authorized(fnDeleteModelInstance);
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

            var fnDelete = function (accessTokenString, tokenRefreshed, error) {
                if (error) {
                    alert("Could not delete meta model: " + error);
                    return;
                }

                $.ajax({
                    type: 'DELETE',
                    url: '/metamodels/' + metamodelId,
                    headers: {
                        Authorization: "Bearer " + accessTokenString
                    },
                    success: function (data, textStatus, jqXHR) {
                        window.location.replace("/overview");
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        if (!tokenRefreshed) {
                            accessToken.authorized(fnDelete, true);
                        } else {
                            alert("Could not delete meta model: " + textStatus);
                        }
                    }
                });
            };

            accessToken.authorized(fnDelete);
        });

        $("#btnGenerator").click(function () {
            var fnStartGenerator = function (accessTokenString, tokenRefreshed, error) {
                if (error) {
                    if(error === 'Unauthorized') window.location.replace('/auth/login');
                    return;
                }

                $.ajax({
                    type: 'GET',
                    url: '/generator/' + window.metaModelId,
                    headers: {
                        Authorization: "Bearer " + accessTokenString
                    },
                    success: function (data, textStatus, jqXHR) {
                        showSuccess(data);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        if (!tokenRefreshed) {
                            accessToken.authorized(fnStartGenerator, true);
                        } else {
                            showError(jqXHR.responseText)
                        }
                    }
                });
            };

            accessToken.authorized(fnStartGenerator);
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

        var showError = function(text) {
            $("#error-panel").fadeOut('slow', function() {
                $("#error-panel").find("div").text(text);
                $("#error-panel").fadeIn('slow');
            });
        };

        var showSuccess = function(text) {
            $("#success-panel").fadeOut('slow', function() {
                $("#success-panel").show();
                $("#success-panel").find("div").text(text);
                $("#success-panel").fadeIn('slow');
            });
        };

        $("[data-hide]").on("click", function(){
            $("." + $(this).attr("data-hide")).hide();
        });
    });
}(jQuery) );


