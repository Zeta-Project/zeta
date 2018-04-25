import $ from 'jquery';
import joint from 'jointjs';
import GeneratorFactory from "../generator/GeneratorFactory";
// DEBUG
// import {validator} from '../generator/temporary/old/validator'

/**
 * linkTypeSelector provides functions for creating and managing the context menu to choose a link type from.
 *
 * @returns {{init: Function, focusElement: Function, lostFocus: Function, canSetLink: Function, replaceLink: Function}}
 */
export default (function linkTypeSelector() {
    'use strict';

    let _menu = null;
    let _focusedElement = null;
    let _graph = null;
    let _paper = null;
    let _linkID = null;
    let _canSetLink = false;

    let init;
    let createMenu;
    let itemMouseUp;
    let destroyMenu;
    let showMenu;
    let focusElement;
    let lostFocus;
    let canSetLink;
    let replaceLink;
    let registerListeners;
    let handleAddedCell;
    let handleRemovedCell;
    let getConnectionCount;
    let connectionDefinitionGenerator;

    const removeFocus = () => {
        if (_focusedElement) {
            joint.V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-allowed');
            joint.V(_paper.findViewByModel(_focusedElement).el).removeClass('linking-unallowed');
            _focusedElement = null;
        }
    };

    /**
     * Has to be called once before using the other methods!
     *
     * @param graph
     */
    init = function init(graph, paper) {
        _graph = graph;
        _paper = paper;
        connectionDefinitionGenerator = GeneratorFactory.connectionDefinition;
        registerListeners();
    };

    /**
     * Creates the context menu with the specified elements.
     *
     * @param {array} elements - An array of strings which will be the contents of the context menu.
     */
    createMenu = function createMenu(elements) {

        if (_menu) {
            destroyMenu();
        }

        let menu = '';
        elements = elements || [];

        menu += '<ul id="contextMenu" class="list-group">';


        if (elements.length > 0) {

            elements.forEach(function (name, index) {
                menu += '<li class="list-group-item" id="menuItem' + index + '">';
                menu += name;
                menu += '</li>';
            });
            joint.V(_paper.findViewByModel(_focusedElement).el).addClass('linking-allowed');


        } else {
            menu += '<li class="list-group-item">';
            menu += 'These elements are not linkable!';
            menu += '</li>';
            joint.V(_paper.findViewByModel(_focusedElement).el).addClass('linking-unallowed');
            canSetLink(false);
        }


        menu += '</ul>';

        $('#contextMenuContainer').html(menu);

        // set handler
        elements.forEach(function (name, index) {
            let item = $('#menuItem' + index);

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
            removeFocus();
            destroyMenu();
        });

    };

    /**
     * Is called on mouseup on a link type in the context menu.
     *
     * @param linkName
     */
    itemMouseUp = function itemMouseUp(linkName) {
        let link = _graph.getCell(_linkID);
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
    focusElement = function focusElement(linkID, eventTargetModel, x, y) {
        let link;
        let eventSourceID;
        let eventSourceType;
        let menuList;

        removeFocus();
        if (!eventTargetModel) {
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

        let eventTarget = eventTargetModel.attributes.nodeName;

        menuList = GeneratorFactory.validator.getValidEdges(eventSourceType, eventTarget);

        //DEBUG
        // let menuList_OLD = validator.getValidEdges(eventSourceType, eventTarget);


        createMenu(menuList);
        showMenu(x, y);
    };

    /**
     * Is called when the focus for the element is lost (not hovering over the element anymore).
     *
     * @param newFocusedElement
     */
    lostFocus = function lostFocus(newFocusedElement) {
        let element = $(newFocusedElement);

        if (element.hasClass('contextItem') || element.hasClass('menuItem') || element.hasClass('menuTable')) {
            return;
        }
        removeFocus();
        destroyMenu();
    };

    /**
     * Destroys the current context menu.
     */
    destroyMenu = function destroyMenu() {
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
    showMenu = function showMenu(x, y) {
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
    canSetLink = function canSetLink(canSet) {
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

    replaceLink = function replaceLink(link) {
        let cell = _graph.getCell(_linkID);

        let edgeData = GeneratorFactory.validator.getEdgeData(link.attributes.subtype);

        let edgeType = edgeData.type;
        let targetId = link.attributes.target.id;
        let sourceId = link.attributes.source.id;

        let sourceMClass = _graph.getCell(sourceId);
        let targetMClass = _graph.getCell(targetId);

        let sourceEdges = GeneratorFactory.validator.inputMatrix(sourceMClass.attributes.nodeName);
        let targetEdges = GeneratorFactory.validator.inputMatrix(targetMClass.attributes.nodeName);

        link.prop('placings', connectionDefinitionGenerator.getPlacings(edgeData.style));
        // link.prop('placings', getPlacings(edgeData.style));
        link.prop('sourceAttribute', edgeData.from);
        link.prop('targetAttribute', edgeData.to);
        // edge type needs to be set before getting connection count
        // otherwise current link is ignored
        link.prop('mReference', edgeType);
        // link.prop('labels', getLabels(edgeData.style));
        link.prop('labels', connectionDefinitionGenerator.getLabels(edgeData.style));

        let sourceTypes = Object.keys(sourceEdges);
        let targetTypes = Object.keys(targetEdges);

        let sourceActualValues  = getActualValues(sourceTypes,sourceId);
        let targetActualValues  = getActualValues(targetTypes,targetId);

        let sourceDesiredValues = getDesiredValues(sourceTypes,sourceEdges);
        let targetDesiredValues = getDesiredValues(targetTypes,targetEdges);

        let validSourceNode = checkForValidNodes(sourceDesiredValues,sourceActualValues, sourceTypes);
        let validTargetNode = checkForValidNodes(targetDesiredValues,targetActualValues, targetTypes);

        setPaperElementValidation(validSourceNode,sourceId, _paper);
        setPaperElementValidation(validTargetNode,targetId, _paper);

        let clone = link.clone();
        clone.attributes.attrs = connectionDefinitionGenerator.getConnectionStyle(edgeData.style);
        // clone.attributes.attrs = getConnectionStyle(edgeData.style);

        console.log(clone.attributes.attrs);
        clone.prop('styleSet', true);
        link.remove();
        _graph.addCell(clone);
        destroyMenu();
        return cell;
    };

    function setPaperElementValidation(validNodeResults, id, paper){
        if(checkAllTrue(validNodeResults)) {
            joint.V(paper.findViewByModel(id).el).removeClass('invalid-edges');
        } else {
            joint.V(paper.findViewByModel(id).el).addClass('invalid-edges');
        }
    }

    function checkAllTrue(validCheckResult) {
        return !Object.values(validCheckResult).includes(false);
    }

    function isInfinity(num) {
        if(num === -1){
            return Infinity;
        }
        return num;
    }

    function isBetween(n, a, b) {
        a = isInfinity(a);
        b = isInfinity(b);

        return n >= a && n <=b;
    }

    function checkForValidNodes(soll, ist, types) {
        return types.reduce((result,type) => {
            result[type] = isBetween(ist[type].outgoing,soll[type].sourceLowerBounds,soll[type].sourceUpperBounds);
            return result;
        },{});
    }

    function getNowConnectionCount(id, edgeType){

        let ingoingCount = getConnectionCount(id, edgeType.charAt(0).toUpperCase()+edgeType.slice(1),  {inbound: true});
        let outgoingCount = getConnectionCount(id, edgeType.charAt(0).toUpperCase()+edgeType.slice(1), {outbound: true});

        return {[edgeType]: ""+edgeType,"outgoing": ""+outgoingCount,"ingoing":  ""+ingoingCount};
    }

    /**
     * register listeners for added and removed cells
     */
    registerListeners = function () {
        _graph.on('add', handleAddedCell);
        _graph.on('remove', handleRemovedCell);
    };

    /**
     * Checks if the elements lower bounds are violated, if this is the case a highlighting is added
     * @param cell the newly added cell
     */
    handleAddedCell = function (cell) {
        if (cell.isLink()) return;
        let inoutMatrix = GeneratorFactory.validator.inputMatrix(cell.attributes.nodeName);
        checkTypeBounds(inoutMatrix, cell);
    };

    function checkTypeBounds(inoutMatrix, cell) {
        let inputs = [];
        let outputs = [];

        for (let inEdge in inoutMatrix) {
            if (Object.prototype.hasOwnProperty.call(inoutMatrix, inEdge)) {
                if (inoutMatrix[inEdge].sourceLowerBounds > 0) {
                    inputs.push({'mReferenceName': inEdge, 'lowerBound': inoutMatrix[inEdge].sourceLowerBounds});
                }
            }
        }

        for (let outEdge in inoutMatrix) {
            if (Object.prototype.hasOwnProperty.call(inoutMatrix, outEdge)) {
                if (inoutMatrix[outEdge].targetLowerBounds > 0) {
                    outputs.push({'mReferenceName': outEdge, 'lowerBound': inoutMatrix[outEdge].targetLowerBounds});
                }
            }
        }

        if (inputs.length != 0 || outputs.length != 0) {
            joint.V(_paper.findViewByModel(cell).el).addClass('invalid-edges');
        }
    }

    /**
     * Checks if bounds are violated through a removed link,
     * highlighting is added if this is the case
     * @param link the removed link
     */
    handleRemovedCell = function (link) {
        // check if style is set, otherwise the removed dummy link will influence the counters
        // this also ignores all Elements
        if (!link.attributes.styleSet) return;

        let targetId = link.attributes.target.id;
        let sourceId = link.attributes.source.id;
        let sourceMClass = _graph.getCell(link.attributes.source.id);
        let targetMClass = _graph.getCell(link.attributes.target.id);
        let sourceEdges = GeneratorFactory.validator.inputMatrix(sourceMClass.attributes.nodeName);
        let targetEdges = GeneratorFactory.validator.inputMatrix(targetMClass.attributes.nodeName);

        let sourceTypes = Object.keys(sourceEdges);
        let targetTypes = Object.keys(targetEdges);

        let sourceActualValues  = getActualValues(sourceTypes,sourceId);
        let targetActualValues  = getActualValues(targetTypes,targetId);

        let sourceDesiredValues = getDesiredValues(sourceTypes,sourceEdges);
        let targetDesiredValues = getDesiredValues(targetTypes,targetEdges);

        let validSourceNode = checkForValidNodes(sourceDesiredValues,sourceActualValues, sourceTypes);
        let validTargetNode = checkForValidNodes(targetDesiredValues,targetActualValues, targetTypes);

        setPaperElementValidation(validSourceNode,sourceId, _paper);
        setPaperElementValidation(validTargetNode,targetId, _paper);

    };

    /**
     *
     * @param cellId id of a cell
     * @param edgeType type of the edge
     * @param opt indicates whether outbound or inbound links are counted
     * @returns {number} the connection count
     */
    getConnectionCount = function (cellId, edgeType, opt) {
        let links = _graph.getConnectedLinks(_graph.getCell(cellId), opt);
        let count = 0;
        links.forEach(function (l) {
            if (l.attributes.mReference === edgeType) {
                count += 1;
            }
        });
        return count
    };

    function getDesiredValues(types,edges) {
        return types.reduce((result,type) => {
            result[type] = edges[type];
            return result;
        },{});
    }

    function getActualValues(types, id) {
        return types.reduce((result,type) => {
            result[type] = getNowConnectionCount(id,type);// type in und outs IST
            return result;
        },{});
    }



    /**
     * Provide publicly avaliable functions und variables.
     */
    return {
        init: init,
        focusElement: focusElement,
        lostFocus: lostFocus,
        canSetLink: canSetLink,
        replaceLink: replaceLink
    };
})();