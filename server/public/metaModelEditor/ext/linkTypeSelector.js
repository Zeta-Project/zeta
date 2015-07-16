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

    /**
     * Has to be called once before using the other methods!
     *
     * @param graph
     */
    init = function init (graph) {
        _graph = graph;
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
            eventSourceType = _graph.getCell(eventSourceID).attributes.type;

        } else if (link.attributes.target.id) {

            // event target === link source
            eventSourceID = link.attributes.target.id;
            eventSourceType = _graph.getCell(eventSourceID).attributes.type;

        }

        menuList = edgeValidator.validEdges(eventSourceType, eventTargetModel.attributes.type);

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
        var newLink;
        var subtype;
        var sourceID;
        var targetID;
        var linkClass;

        subtype = link.attributes.subtype;
        sourceID = link.attributes.source.id;
        targetID = link.attributes.target.id;

        link.remove();

        linkClass = joint.shapes.uml[subtype];
        if (typeof linkClass !== 'function') {
            console.error('Link type ' + subtype + ' is not valid!');
            return null;
        }

        newLink = new linkClass({
            source : {id : sourceID},
            target : {id : targetID}
        });

        newLink.attributes.subtype = subtype;

        _graph.addCell(newLink);

        if (mCoreUtil.isReference(newLink)) {
            newLink.label(0, {
                position : 0.5,
                attrs : {
                    text : {
                        text : newLink.attributes.name
                    }
                }
            });
        }

        return newLink;
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