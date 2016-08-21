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
    var handleRemovedCell
    var getConnectionCount;

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
            V(_paper.findViewByModel(_focusedElement).el).addClass('linking-allowed');


        } else {
            menu += '<li class="list-group-item">';
            menu += 'These elements are not linkable!';
            menu += '</li>';
            V(_paper.findViewByModel(_focusedElement).el).addClass('linking-unallowed');
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
            V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-allowed');
            V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-unallowed');
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
            eventSourceType = _graph.getCell(eventSourceID).attributes.nodeName;

        } else if (link.attributes.target.id) {

            // event target === link source
            eventSourceID = link.attributes.target.id;
            eventSourceType = _graph.getCell(eventSourceID).attributes.nodeName;

        }

        var eventTarget = eventTargetModel.attributes.nodeName;

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
        if(_focusedElement) {
            V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-allowed');
            V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-unallowed');
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
     * Once the link is drawn, its type is some default link type,
     * so it has to be replaced with a link of the correct type.
     * Also check if bounds of link source/target are violated through
     * the added link. Highlighting is added if this is the case
     * @param {object} link - The link that has just been drawn.
     * @returns {object} The new link.

     */

    replaceLink = function replaceLink (link) {
        var cell = _graph.getCell(_linkID);
        var edgeData = validator.getEdgeData(link.attributes.subtype);
        var edgeType = edgeData.type;
        var targetId = link.attributes.target.id;
        var sourceId = link.attributes.source.id;
        var maxInputs = validator.inputMatrix[edgeData.to][edgeData.type].upperBound;
        var maxOutputs = validator.outputMatrix[edgeData.from][edgeData.type].upperBound;
        var minInputs = validator.inputMatrix[edgeData.to][edgeData.type].lowerBound;
        var minOutputs = validator.outputMatrix[edgeData.from][edgeData.type].lowerBound;
        var targetMaxReached = false;
        var sourceMaxReached = false;

        link.prop('placings', getPlacings(edgeData.style));
        link.prop('sourceAttribute', edgeData.from);
        link.prop('targetAttribute', edgeData.to);
        // edge type needs to be set before getting connection count
        // otherwise current link is ignored
        link.prop('mReference', edgeType);
        link.prop('labels', getLabels(edgeData.style));

        var ingoingTargetCount = getConnectionCount(targetId, edgeType, {inbound: true});
        var outgoingSourceCount = getConnectionCount(sourceId, edgeType, {outbound: true});

        if(maxInputs != -1 && ingoingTargetCount > maxInputs) {
            targetMaxReached = true;
            V(_paper.findViewByModel(targetId).el).addClass('invalid-edges');
        }
        if(minInputs != 0 && minInputs <= ingoingTargetCount && !targetMaxReached) {
            V(_paper.findViewByModel(targetId).el).removeClass('invalid-edges');
        }
        if(maxOutputs != -1 && outgoingSourceCount > maxOutputs) {
            sourceMaxReached = true;
            V(_paper.findViewByModel(sourceId).el).addClass('invalid-edges');
        }
        if(minOutputs != 0 && minOutputs <= outgoingSourceCount && !sourceMaxReached) {
            V(_paper.findViewByModel(sourceId).el).removeClass('invalid-edges');
        }
        var clone = link.clone();
        clone.attributes.attrs = getConnectionStyle(edgeData.style);
        clone.prop('styleSet', true);
        link.remove();
        _graph.addCell(clone);
        destroyMenu();
        return cell;
    };

    /**
     * register listeners for added and removed cells
     */
    registerListeners = function() {
        _graph.on('add', handleAddedCell);
        _graph.on('remove', handleRemovedCell);
    };

    /**
     * Checks if the elements lower bounds are violated, if this is the case a highlighting is added
     * @param cell the newly added cell
     */
    handleAddedCell = function(cell) {
        if(cell.isLink()) return;
        var inputs = [];
        var outputs = [];
        var inputMatrix = validator.inputMatrix[cell.attributes.mClass];
        var outputMatrix = validator.outputMatrix[cell.attributes.mClass];


        for(var inEdge in inputMatrix) {
            if (Object.prototype.hasOwnProperty.call(inputMatrix, inEdge)) {
                if (inputMatrix[inEdge].lowerBound > 0) {
                    inputs.push({'mReferenceName': inEdge, 'lowerBound': inputMatrix[inEdge].lowerBound });
                }
            }
        }

        for(var outEdge in outputMatrix) {
            if (Object.prototype.hasOwnProperty.call(outputMatrix, outEdge)) {
                if (outputMatrix[outEdge].lowerBound > 0) {
                    outputs.push({'mReferenceName': outEdge, 'lowerBound': outputMatrix[outEdge].lowerBound });
                }
            }
        }

        if(inputs.length != 0 || outputs.length != 0) {
            V(_paper.findViewByModel(cell).el).addClass('invalid-edges');
        }
    };

    /**
     * Checks if bounds are violated through a removed link,
     * highlighting is added if this is the case
     * @param link the removed link
     */
    handleRemovedCell = function(link) {
        // check if style is set, otherwise the removed dummy link will influence the counters
        // this also ignores all Elements
        if(!link.attributes.styleSet) return;
        var edgeType = validator.getEdgeData(link.attributes.subtype).type;
        var sourceMClass = _graph.getCell(link.attributes.source.id).attributes.mClass;
        var targetMClass = _graph.getCell(link.attributes.target.id).attributes.mClass;
        var minInputs = validator.inputMatrix[targetMClass][edgeType].lowerBound;
        var minOutputs = validator.outputMatrix[sourceMClass][edgeType].lowerBound;
        var maxInputs = validator.inputMatrix[targetMClass][edgeType].upperBound;
        var maxOutputs = validator.outputMatrix[sourceMClass][edgeType].upperBound;
        var ingoingTargetCount = getConnectionCount(link.attributes.target.id, edgeType, {inbound: true});
        var outgoingSourceCount = getConnectionCount(link.attributes.source.id, edgeType, {outbound: true});
        var minInUnderstepped = false;
        var minOutUnderstepped = false;

        if(minInputs > ingoingTargetCount) {
            minInUnderstepped = true;
            V(_paper.findViewByModel(link.attributes.target.id).el).addClass('invalid-edges');
        }

        if(maxInputs != -1 && maxInputs >= ingoingTargetCount && !minInUnderstepped) {
            V(_paper.findViewByModel(link.attributes.target.id).el).removeClass('invalid-edges');
        }

        if(minOutputs > outgoingSourceCount) {
            minOutUnderstepped = true;
            V(_paper.findViewByModel(link.attributes.source.id).el).addClass('invalid-edges');
        }
        if(maxOutputs != -1 && maxOutputs >= outgoingSourceCount && !minOutUnderstepped) {
            V(_paper.findViewByModel(link.attributes.source.id).el).removeClass('invalid-edges');
        }

    };

    /**
     * 
     * @param cellId id of a cell
     * @param edgeType type of the edge
     * @param opt indicates whether outbound or inbound links are counted
     * @returns {number} the connection count
     */
    getConnectionCount = function(cellId, edgeType, opt) {
        var links = _graph.getConnectedLinks(_graph.getCell(cellId), opt);
        var count = 0;
        links.forEach(function(l) {
            if(l.attributes.mReference === edgeType) {
                count += 1;
            }
        });
        return count
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