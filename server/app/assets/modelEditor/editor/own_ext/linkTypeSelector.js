/**
 * linkTypeSelector provides functions for creating and managing the context menu to choose a link type from.
 *
 * @returns {{init: Function, focusElement: Function, lostFocus: Function, canSetLink: Function, replaceLink: Function}}
 */
var linkTypeSelector = (function linkTypeSelector () {
    'use strict';

    var _menu = null;
    var _focusedElement = null;
    var _graph = null;
    var _paper = null;
    var _linkID = null;
    var _canSetLink = false;

    var _inputs = {};
    var _outputs = {};

    var _removedLinks = [];

    var init;
    var createMenu;
    var itemMouseUp;
    var destroyMenu;
    var showMenu;
    var focusElement;
    var lostFocus;
    var canSetLink;
    var replaceLink;
    var registerListeners;
    var handleAddedCell;
    var handleRemovedCell;

    /**
     * Has to be called once before using the other methods!
     *
     * @param graph
     */
    init = function init (graph, paper) {
        _graph = graph;
        _paper = paper;
        registerListeners();
    };

    /**
     * Creates the context menu with the specified elements.
     *
     * @param {array} elements - An array of strings which will be the contents of the context menu.
     */
    createMenu = function createMenu (elements) {

        if (_menu) {
            destroyMenu();
        }

        var menu = '';
        elements = elements || [];

        menu += '<ul id="contextMenu" class="list-group">';


        if (elements.length > 0) {

            elements.forEach(function (name, index) {
                menu += '<li class="list-group-item" id="menuItem' + index + '">';
                menu += name;
                menu += '</li>';
            });

        } else {
            menu += '<li class="list-group-item">';
            menu += 'These elements are not linkable!';
            menu += '</li>';
            canSetLink(false);
        }


        menu += '</ul>';

        $('#contextMenuContainer').html(menu);

        // set handler
        elements.forEach(function (name, index) {
            var item = $('#menuItem' + index);

            item.mouseup(function () {
                itemMouseUp(name);
            });

            item.mouseenter(function () {
                canSetLink(true);
                // item.css('background', '#27ae60');
            });

            item.mouseleave(function () {
                // item.css('background', '#2c3e50');
            });
        });

        _menu = $('#contextMenu');

        _menu.mouseleave(function () {
            canSetLink(false);
        });

        $('body').mouseup(function () {
            destroyMenu();
        });

    };

    /**
     * Is called on mouseup on a link type in the context menu.
     *
     * @param linkName
     */
    itemMouseUp = function itemMouseUp (linkName) {
        var link = _graph.getCell(_linkID);
        link.attributes.subtype = linkName;
        destroyMenu();
    };

    /**
     * Is called when user is creating a link and is hovering over an element.
     *
     * @param {string} linkID - ID of the link that the user is just creating.
     * @param {object} eventTargetModel - The element over which the mouse is hovering.
     * @param {int} x - X-coordinate of the mouse.
     * @param {int} y - Y-coordinate of the mouse.
     */
    focusElement = function focusElement (linkID, eventTargetModel, x, y) {
        var link;
        var eventSourceID;
        var eventSourceType;
        var menuList;

        if (!eventTargetModel || _focusedElement) {
            return;
        }

        _linkID = linkID;
        _focusedElement = eventTargetModel;

        link = _graph.getCell(linkID);

        if (link.attributes.source.id) {

            // event target === link target
            eventSourceID = link.attributes.source.id;
            eventSourceType = _graph.getCell(eventSourceID).attributes.type.split(".")[1];

        } else if (link.attributes.target.id) {

            // event target === link source
            eventSourceID = link.attributes.target.id;
            eventSourceType = _graph.getCell(eventSourceID).attributes.type.split(".")[1];

        }

        var eventTarget = eventTargetModel.attributes.type.split(".")[1];

        menuList = validator.getValidEdges(eventSourceType, eventTarget);

        createMenu(menuList);
        showMenu(x, y);
    };

    /**
     * Is called when the focus for the element is lost (not hovering over the element anymore).
     *
     * @param newFocusedElement
     */
    lostFocus = function lostFocus (newFocusedElement) {
        var element = $(newFocusedElement);

        if (element.hasClass('contextItem') || element.hasClass('menuItem') || element.hasClass('menuTable')) {
            return;
        }

        _focusedElement = null;
        destroyMenu();
    };

    /**
     * Destroys the current context menu.
     */
    destroyMenu = function destroyMenu () {
        if (_menu) {
            _menu.remove();
            _menu = null;
            $('#contextMenuContainer').empty();
        }
    };

    /**
     * Shows the current context menu near the specified coordinates.
     * @param {int} x
     * @param {int} y
     */
    showMenu = function showMenu (x, y) {
        y -= 20;
        _menu.css('top', y + 'px');
        _menu.css('left', x + 'px');
        _menu.css('display', 'inline');
    };

    /**
     * Getter and setter for _canSetLink.
     * @param {boolean} [canSet]
     * @returns {boolean}
     */
    canSetLink = function canSetLink (canSet) {
        if (typeof canSet === 'boolean') {
            _canSetLink = canSet;
        }

        return _canSetLink;
    };

    /**
     * Once the link is drawn, its type is some default link type, so it has to be replaced with a link of the correct type.
     *
     * @param {object} link - The link that has just been drawn.
     * @returns {object} The new link.

     */

    replaceLink = function replaceLink (link) {

        var cell = _graph.getCell(_linkID);
        var edgeData = validator.getEdgeData(link.attributes.subtype);
        var edgeType = edgeData.type;
        var targetId = link.attributes.target.id;
        var sourceId = link.attributes.source.id;

        if (!_inputs.hasOwnProperty(targetId)) _inputs[targetId] = {};
        if (!_outputs.hasOwnProperty(sourceId)) _outputs[sourceId] = {};
        if (!_inputs[targetId].hasOwnProperty(edgeType)) _inputs[targetId][edgeType] = 0;
        if (!_outputs[sourceId].hasOwnProperty(edgeType)) _outputs[sourceId][edgeType] = 0;

        var maxInputs = validator.inputMatrix[edgeData.to][edgeData.type].upperBound;
        var maxOutputs = validator.outputMatrix[edgeData.from][edgeData.type].upperBound;


        if(maxOutputs === _outputs[sourceId][edgeType] && _outputs[sourceId][edgeType] != -1
            || maxInputs ===_inputs[targetId][edgeType] && _inputs[targetId][edgeType] != -1) {
            cell.remove();
            return cell;
        } else {
            _inputs[targetId][edgeType] += 1;
            _outputs[sourceId][edgeType] += 1;
        }


        var clone = cell.clone();

        clone.attributes.attrs = getConnectionStyle(edgeData.style);
        clone.attributes.placings = getPlacings(edgeData.style);
        clone.attributes.ecoreName = edgeData.type;
        clone.attributes.styleSet = true;
        clone.attributes.sourceAttribute = edgeData.from;
        clone.attributes.targetAttribute = edgeData.to;

        // add cloned link to graph, because changed attributes otherwise wont be directly reflected in the view

        cell.remove();
        _graph.addCell(clone);
        destroyMenu();
        return cell;
    };

    registerListeners = function() {
        _graph.on('add', handleAddedCell);
        _graph.on('remove', handleRemovedCell);
    };

    handleAddedCell = function(cell) {
        var inputs = [];
        var outputs = [];
        var inputMatrix = validator.inputMatrix[cell.attributes.mClass];
        var outputMatrix = validator.outputMatrix[cell.attributes.mClass];

        for(var key in inputMatrix) {
            if (Object.prototype.hasOwnProperty.call(inputMatrix, key)) {
                if (inputMatrix[key].lowerBound > 0) {
                    inputs.push({'mReferenceName': key, 'lowerBound': inputMatrix[key].lowerBound });
                }
            }
        }

        for(var key in outputMatrix) {
            if (Object.prototype.hasOwnProperty.call(outputMatrix, key)) {
                if (outputMatrix[key].lowerBound > 0) {
                    outputs.push({'mReferenceName': key, 'lowerBound': outputMatrix[key].lowerBound });
                }
            }
        }

        if(inputs.length != 0 || outputs.length != 0) {
            V(_paper.findViewByModel(cell).el).addClass('invalid-edges');
        }
    };

    handleRemovedCell = function(cell) {

        // check if style is set, otherwise the removed dummy link will influence the counters
        if(cell.isLink() && cell.attributes.styleSet) {
            var edgeType = validator.getEdgeData(cell.attributes.subtype).type;
            var sourceId = cell.attributes.source.id;
            var targetId = cell.attributes.target.id;
            _outputs[sourceId][edgeType] -= 1;
            _inputs[targetId][edgeType] -= 1;
        }

    };

    /**
     * Provide publicly avaliable functions und variables.
     */
    return {
        init : init,
        focusElement : focusElement,
        lostFocus : lostFocus,
        canSetLink : canSetLink,
        replaceLink : replaceLink
    };
})();