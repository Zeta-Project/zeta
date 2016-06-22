$(document).ready(function () {
    $("#btnCreateMetaModel").click(function () {

        var name = window.prompt("Enter a name for your meta model");
        var data = JSON.stringify({
            "metaModel": {
                "name": name,
                "elements": [],
                "uiState": ""
            }
        });
        var fnCreateMetaModel = function (accessToken, tokenRefreshed, error) {
            if (error) {
                alert("Could not create meta model: " + error);
                return;
            }

            $.ajax({
                type: 'POST',
                url: '/metamodels',
                headers: {
                    Authorization: "Bearer " + accessToken,
                    'Content-Type': 'application/json'
                },
                data: data,
                success: function (data, textStatus, jqXHR) {
                    window.location.replace("/overview/"+data.insertId);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (!tokenRefreshed) {
                        accessToken.authorized(fnDelete, true);
                    } else {
                        alert("Could not create meta model: " + textStatus);
                    }
                }
            });
        };

        accessToken.authorized(fnCreateMetaModel);
    });

    $("#btnDeleteMetaModel").click(function () {
        var fnDelete = function (accessToken, tokenRefreshed, error) {
            if (error) {
                alert("Could not delete meta model: " + error);
                return;
            }

            $.ajax({
                type: 'DELETE',
                url: '/metamodels/' + window.metaModelId,
                headers: {
                    Authorization: "Bearer " + accessToken
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
        var fnStartGenerator = function (accessToken, tokenRefreshed, error) {
            if (error) {
                alert("Could not start generator " + error);
                return;
            }

            $.ajax({
                type: 'GET',
                url: '/generator/' + window.metaModelId,
                headers: {
                    Authorization: "Bearer " + accessToken
                },
                success: function (data, textStatus, jqXHR) {
                    alert(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (!tokenRefreshed) {
                        accessToken.authorized(fnStartGenerator, true);
                    } else {
                        alert(jqXHR.responseText);
                    }
                }
            });
        };

        accessToken.authorized(fnStartGenerator);
    });

    $("#btnCreateModelInstance").click(function () {
        var name = prompt("Enter Model Instance name");
        var id = window.metaModelId;
        var data = {
            "metaModelId": id,
            "model": {
                elements: [],
                name: name,
                uiState: ""
            }
        };

        var createModelInstance = function (accessToken, tokenRefreshed, error) {
            if (error) {
                alert("Could not create new Model Instance " + error);
                return;
            }

            $.ajax({
                type: 'POST',
                url: '/models',
                headers: {
                    Authorization: "Bearer " + accessToken,
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
                        accessToken.authorized(createModelInstance, true);
                    } else {
                        alert("failed creating model instance: " + textStatus);
                    }
                }
            });
        };

        accessToken.authorized(createModelInstance);
    });

    $(".deleteModelInstance").click(function () {
        //prevent default otherwise href to modelEditor
        event.preventDefault();

        var modelId = this.dataset.modelId;
        var fnDeleteModelInstance = function (accessToken, tokenRefreshed, error) {
            if (error) {
                alert("Could not delete model Instance " + error);
                return;
            }

            $.ajax({
                type: 'DELETE',
                url: '/models/' + modelId,
                headers: {
                    Authorization: "Bearer " + accessToken
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
    });
});
