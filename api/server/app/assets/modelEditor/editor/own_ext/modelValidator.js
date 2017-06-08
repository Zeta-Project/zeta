/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
(function ($) {
    'use strict';

    var highlightInvalidCells, unhighlightCells, alertSuccess, alertError;
    var graph = window.globalGraph;
    var paper = window.globalPaper;
    var successPanel = $("#success-panel");
    var errorPanel = $("#error-panel");

    highlightInvalidCells = function (ids) {

        var invalidCells = graph.getCells().filter(function (cell) {
            return ids.indexOf(cell.id) !== -1;
        });

        var invalidCellViews = invalidCells.map(function (cell) {
            return paper.findViewByModel(cell);
        });

        unhighlightCells();
        invalidCellViews.forEach(function (cellView) {
            if (cellView) {
                cellView.highlight();
            }
        });

    };

    unhighlightCells = function () {
        var cellViews = graph.getCells().map(function (cell) {
            return paper.findViewByModel(cell);
        });

        cellViews.forEach(function (cellView) {
            if (cellView) {
                cellView.unhighlight();
            }
        });
    };

    alertSuccess = function (msg) {
        successPanel.fadeOut('slow', function () {
            errorPanel.fadeOut('slow', function () {
                successPanel.show();
                successPanel.find('div').text(msg);
                successPanel.fadeIn('slow');
            });
        });
    };

    alertError = function (msg) {
        successPanel.fadeOut('slow', function () {
            errorPanel.fadeOut('slow', function () {
                errorPanel.show();
                errorPanel.find('div').text(msg);
                errorPanel.fadeIn('slow');
            });
        });
    };

    $('#validatorValidate').click(function () {
        modelValidatorUtil.validate(this.dataset.modelId, {
            openWindow: true,
            success: function (data, textStatus, jqXHR) {

                var invalidResults = data.filter(function (res) {
                    return !res.valid;
                });

                var invalidElementRules = invalidResults.filter(function (res) {
                    return res.element !== null;
                });

                var invalidElementIds = invalidElementRules.map(function (res) {
                    return res.element.id;
                });

                highlightInvalidCells(invalidElementIds);

                alertSuccess("Validation successful. Results will be shown in a separate window.");

            },
            error: function (jqXHR, textStatus, errorThrown) {
                alertError(jqXHR.responseText);
            }
        });
    });

    $('#validatorShow').click(function () {
        modelValidatorUtil.show(this.dataset.metaModelId, {
            openWindow: true,
            success: function (data, textStatus, jqXHR) {
                alertSuccess("Validation rules successfully loaded. Rules will be shown in a separate window.")
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alertError(jqXHR.responseText);
            }
        })
    });

    $('#validatorGenerate').click(function () {
        modelValidatorUtil.generate(this.dataset.metaModelId, {
            success: function (data, textStatus, jqXHR) {
                alertSuccess('Validator successfully generated.')
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alertError(jqXHR.responseText);
            }
        })
    });

    $('#validatorUnhighlight').click(function () {
        unhighlightCells();
    });

})(jQuery);
