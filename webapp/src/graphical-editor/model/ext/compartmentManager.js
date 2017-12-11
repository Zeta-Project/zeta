import $ from 'jquery';
import _ from 'lodash';
import joint from 'jointjs';

export default (function compartmentManager () {
    'use strict';

    var init;
    var _registerListeners;
    var _getScale;
    var _getRotation;
    var _getScaledElement;

    var _graph;
    var _paper;



    init = function(graph, paper) {
        _graph = graph;
        _paper = paper;
        _registerListeners();
    };

    _registerListeners = function() {

        // First, unembed the cell that has just been grabbed by the user.
        _paper.on('cell:pointerdown', function(cellView, evt, x, y) {

            var cell = cellView.model;

            if (!cell.get('embeds') || cell.get('embeds').length === 0) {
                // Show the dragged element above all the other cells (except when the
                // element is a parent).
                cell.toFront();
            }

            if (cell.get('parent')) {
                _graph.getCell(cell.get('parent')).unembed(cell);
            }
        });

        // When the dragged cell is dropped over another cell, let it become a child of the
        // element below.
        _paper.on('cell:pointerup', function(cellView, evt, x, y) {
            if(!cellView.hasOwnProperty('getBBox')) return;
            var cell = cellView.model;
            var cellViewsBelow = _paper.findViewsFromPoint(cell.getBBox().center());

            if (cellViewsBelow.length) {
                // Note that the findViewsFromPoint() returns the view for the `cell` itself.
                var cellViewBelow = _.find(cellViewsBelow, function(c) { return c.model.id !== cell.id });
                if(cellViewBelow && cellViewBelow.model.get('compartments').length == 0) {
                    console.log("shape has no compartments");
                    return;
                }
                if(cellViewBelow) {
                    var mId = cellViewBelow.model.get('id');
                    var cellViewBelowEle = $("g[model-id='"+mId+"']");
                    var cellViewPosition = cellViewBelow.model.get('position');
                    var compartmentFound = false;
                    cellViewBelow.model.get('compartments').forEach(function (comp) {
                        // find the ele which has class scalable
                        var scale = _getScale(cellViewBelowEle);
                        var rotation = _getRotation(cellViewBelowEle, cellViewPosition);
                        var scaledElement = _getScaledElement(cellViewBelowEle, cellViewPosition, scale, comp);
                        var cursor = joint.g.point(x,y);
                        // rotate cursor if needed
                        if(rotation.angle != 0) {
                            cursor = cursor.rotate(g.point(rotation.cx, rotation.cy), rotation.angle);
                        }
                        var rect = joint.g.rect(scaledElement.x, scaledElement.y, scaledElement.width, scaledElement.height);
                        if(rect.containsPoint(cursor))
                            compartmentFound = true;
                    });
                }

                // Prevent recursive embedding.
                if (cellViewBelow && compartmentFound && cellViewBelow.model.get('parent') !== cell.id) {
                    console.log('embedded');
                    cellViewBelow.model.embed(cell);
                }
            }
        });
    };

    _getScale = function(cellView) {
        var transform = cellView.find($(".scalable")).attr("transform");
        var scale = transform.split('(')[1].split(')')[0].split(',');
        return {
            x: scale[0],
            y: scale[1]
        };
    };

    _getRotation = function(cellView, cellViewPosition) {
        var rotate = cellView.find($(".rotatable")).attr("transform");
        var rotation = rotate.split('(')[1].split(')')[0].split(',');
        return {
            angle: Number.parseInt(rotation[0]),
            cx: Number.parseInt(rotation[1]) + cellViewPosition.x,
            cy: Number.parseInt(rotation[2]) + cellViewPosition.y
        };
    };

    _getScaledElement = function(cellViewBelowEle, cellViewPosition, scale, comp) {
        var ele = cellViewBelowEle.find($("." + comp.className));
        return {
            x : cellViewPosition.x + (ele.attr("x") * scale.x),
            y : cellViewPosition.y + (ele.attr("y") * scale.y),
            width : ele.attr('width') * scale.x,
            height : ele.attr('height') * scale.y
        };
    };

    return {
        init: init
    }


})();
