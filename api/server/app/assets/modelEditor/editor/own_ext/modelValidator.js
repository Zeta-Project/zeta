/**
 * This file was created by Tobias Droth as part of his master thesis at HTWG Konstanz (03/2017 - 09/2017).
 */
(function ($) {
    'use strict';

    var highlightInvalidCells, unhighlightCells;
    var graph = window.globalGraph;
    var paper = window.globalPaper;

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

            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseText);
            }
        });
    });

    $('#validatorShow').click(function () {
        modelValidatorUtil.show(this.dataset.metaModelId, {
            openWindow: true
        })
    });

    $('#validatorGenerate').click(function () {
        modelValidatorUtil.generate(this.dataset.metaModelId, {
            success: function (data, textStatus, jqXHR) {
                alert('Validator successfully generated.')
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseText);
            }
        })
    });

    $('#validatorUnhighlight').click(function () {
        unhighlightCells();
    });

})(jQuery);
