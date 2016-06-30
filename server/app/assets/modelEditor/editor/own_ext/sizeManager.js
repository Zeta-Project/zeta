var sizeManager = (function sizeManager () {
    'use strict';

    var init;
    var _registerListeners;
    var _handleResize;
    var _ensureSize;
    var _graph;


    init = function(graph) {
        _graph = graph;
        _registerListeners();
    };

    _registerListeners = function() {
        _graph.getCells().map(function(cell){_handleResize(cell)});
        _graph.on('add', function(cell){_handleResize(cell)});
    };

    _handleResize = function(cell) {
        cell.on('change:size', function() {
            _ensureSize(cell);
        });
        //set cell to actual size
        console.log(cell.prop('init-size/width'));
        console.log(cell.prop('init-size/height'));
        cell.prop('size/width', cell.prop('init-size/width'));
        cell.prop('size/height', cell.prop('init-size/height'));
        _ensureSize(cell);
    };

    _ensureSize = function(cell) {
        if(cell.prop('size').width < cell.prop('size-min/width')) {
            cell.prop('size/width', cell.prop('size-min/width'));
        }
        if(cell.prop('size').height < cell.prop('size-min/height')) {
            cell.prop('size/height', cell.prop('size-min/height'));
        }
        if(cell.prop('size').height > cell.prop('size-max/height')) {
            cell.prop('size/height', cell.prop('size-max/height'));
        }
        if(cell.prop('size').width > cell.prop('size-max/width')) {
            cell.prop('size/width', cell.prop('size-max/width'));
        }
    };

    return {
        init : init
    };

})();
