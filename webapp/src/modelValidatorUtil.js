/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
import jQuery from "jquery";

export default (function ($) {
    'use strict';

    var validate, show, generate;
    var validationResultToString, openWindow;

    validate = function (modelId, options) {

        var win;
        if (options.openWindow === true) {
            win = openWindow();
        }

        $.ajax({
            type: 'GET',
            url: '/rest/v1/models/' + modelId + '/validation',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            success: function (data, textStatus, jqXHR) {

                if (options.openWindow === true) {
                    win.document.body.innerHTML = "<pre>" + validationResultToString(data) + "</pre>";
                }

                if (typeof options.success === 'function') {
                    options.success(data, textStatus, jqXHR);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {

                if (options.openWindow === true) {
                    win.close();
                }

                if (typeof options.error === 'function') {
                    options.error(jqXHR, textStatus, errorThrown);
                }

            }
        });
    };

    show = function (metaModelId, options) {

        var win;
        if (options.openWindow === true) {
            win = openWindow();
        }

        $.ajax({
            type: 'GET',
            url: '/rest/v1/meta-models/' + metaModelId + '/validator?generate=false',
            success: function (data, textStatus, jqXHR) {

                if (options.openWindow === true) {
                    win.document.body.innerHTML = "<pre>" + data + "</pre>";
                }

                if (typeof options.success === 'function') {
                    options.success(data, textStatus, jqXHR);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {

                if (options.openWindow === true) {
                    win.close();
                }

                if (typeof options.error === 'function') {
                    options.error(jqXHR, textStatus, errorThrown);
                }

            }
        });
    };

    generate = function (metaModelId, options) {
        $.ajax({
            type: 'GET',
            url: '/rest/v1/meta-models/' + metaModelId + '/validator?generate=true',
            success: function (data, textStatus, jqXHR) {

                if (typeof options.success === 'function') {
                    options.success(data, textStatus, jqXHR);
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {

                if (typeof options.error === 'function') {
                    options.error(jqXHR, textStatus, errorThrown);
                }

            }
        });
    };

    validationResultToString = function (result) {
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
    };

    openWindow = function () {
        var win = window.open('', '', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=300');
        win.document.body.innerHTML = "waiting for data...";
        return win;
    };

    return {
        validate: validate,
        show: show,
        generate: generate
    };

})(jQuery);