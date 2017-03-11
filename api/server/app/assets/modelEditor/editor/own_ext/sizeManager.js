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

    /**
     * register listener on all existing cells, and all newly added cells
     */
    _registerListeners = function() {
        _graph.getCells().map(function(cell){_handleResize(cell)});
        _graph.on('add', function(cell){_handleResize(cell)});
    };

    /**
     * register listener to ensure size of cell if it changes size
     * and set cell to its init-size
     */
    _handleResize = function(cell) {
        cell.on('change:size', function() {
            _ensureSize(cell);
        });
        //set cell to actual size
        cell.prop('size/width', cell.prop('init-size/width'));
        cell.prop('size/height', cell.prop('init-size/height'));
        _ensureSize(cell);
    };

    /**
     * ensure the cells size does not violate size-min and size-max
     */
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
