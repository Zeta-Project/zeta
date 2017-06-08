(function ($) {

    var highlightInvalidCells;
    var graph = window.globalGraph;
    var paper = window.globalPaper;

    highlightInvalidCells = function (ids) {

        var invalidCells = graph.getCells().filter(function (cell) {
            return ids.indexOf(cell.id) !== -1;
        });

        var invalidCellViews = invalidCells.map(function (cell) {
            return paper.findViewByModel(cell);
        });

        invalidCellViews.forEach(function (cellView) {
            if (cellView) {
                cellView.highlight();
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
        var cellViews = graph.getCells().map(function (cell) {
            return paper.findViewByModel(cell);
        });

        cellViews.forEach(function (cellView) {
            if (cellView) {
                cellView.unhighlight();
            }
        });
    });

})(jQuery);
