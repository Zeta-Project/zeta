/*
 * ExtendedSelectionView inherits joint.ui.SelectionView.
 *
 * This class expand the functionality of selectionview.
 * The new functions are:
 * - grouping
 * - cloning
 * - rotating
 * - remove selected elements
 *
 * The methods events, destroySelectionBox, createSelectionBox, adjustSelection
 * and stopSelecting also exist in selection view. But they're customized for
 * the new functionality.
 *
 * @author: Maximilian Göke
 */

ExtendedSelectionView = joint.ui.SelectionView.extend({

    // ---------- manipulate joint.ui.selectionView Methods start

    events: function () {
        // expand events from selectionView
        return _.extend({}, joint.ui.SelectionView.prototype.events, {
            'mousedown #remove-group': 'removeElements',
            'touchstart #remove-group': 'removeElements',
            'mousedown #group-group': 'groupElements',
            'touchstart #group-group': 'groupElements',
            'mousedown #ungroup-group': 'ungroupElements',
            'touchstart #ungroup-group': 'ungroupElements',
            'mousedown #ungroupall-group': 'ungroupAllElements',
            'touchstart #ungroupall-group': 'ungroupAllElements',
            'mousedown #clone-group': 'cloneElements',
            'touchstart #clone-group': 'cloneElements',
            'mousedown #rotate-group': 'startRotateElements',
            'touchstart #rotate-group': 'startRotateElements'
        });
    },

    initialize: function () {
        // javascript's ugly way to call super
        joint.ui.SelectionView.prototype.initialize.apply(this);
        this.listenTo(this.options.paper, 'cell:pointerdown', this.startSelectingInElement);
        this.listenTo(this.options.paper, 'cell:pointermove', this.adjustSelection);
        this.listenTo(this.options.paper, 'cell:pointerup', this.stopSelectingInElement);
    },

    setUpTooltips: function () {
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#remove-group'),
            content: 'Click to remove the selected elements',
            direction: 'right',
            right: this.$('#remove-group'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#clone-group'),
            content: 'Click to clone the selected elements',
            direction: 'left',
            left: this.$('#clone-group'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#rotate-group'),
            content: 'Click and drag to rotate the selected elements',
            direction: 'right',
            right: this.$('#rotate-group'),
            padding: 15
        });
    },

    setUpTooltipsGroup: function () {
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#group-group'),
            content: 'Click to group the selected elements',
            direction: 'left',
            left: this.$('#group-group'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#ungroup-group'),
            content: 'Click to ungroup the selected elements',
            direction: 'left',
            left: this.$('#ungroup-group'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: this.$('#ungroupall-group'),
            content: 'Click to remove all groups',
            direction: 'left',
            left: this.$('#ungroupall-group'),
            padding: 15
        });

    },

    startSelectingInElement: function (cellView, evt) {
        if (evt.shiftKey) {
            this.startSelecting(evt);
        }
    },

    stopSelectingInElement: function (cellView, evt) {
        // is needed because stopSelecting is a dirty methode.
        this._action = 'cherry-picking';
        this.stopSelecting(evt);
    },

    destroySelectionBox: function (elementView) {
        // javascript's ugly way to call super
        joint.ui.SelectionView.prototype.destroySelectionBox.apply(this, [elementView]);
        this.createSelectedView();
    },

    createSelectionBox: function (elementView, action) {
        // javascript's ugly way to call super
        joint.ui.SelectionView.prototype.createSelectionBox.apply(this, [elementView]);
        this.createSelectedView();
        this._action = action ? action : 'cherry-picking';
    },

    /*
     * This Method is copied from joint.ui.selectionView and is a little bit customized.
     * The customized part is commented.
     */
    adjustSelection: function (evt) {
        if (!this._action) return;
        if (evt.preventDefault) {
            evt.preventDefault();
        }
        if (evt.stopPropagation) {
            evt.stopPropagation();
        }
        evt = joint.util.normalizeEvent(evt);

        var dx;
        var dy;

        switch (this._action) {

            case 'selecting':
                dx = evt.clientX - this._clientX;
                dy = evt.clientY - this._clientY;

                var width = this.$el.width();
                var height = this.$el.height();
                var left = parseInt(this.$el.css('left'), 10);
                var top = parseInt(this.$el.css('top'), 10);

                this.$el.css({
                    left: dx < 0 ? this._offsetX + dx : left,
                    top: dy < 0 ? this._offsetY + dy : top,
                    width: Math.abs(dx),
                    height: Math.abs(dy)
                });
                break;

            case 'translating':
                var snappedClientCoords = this.options.paper.snapToGrid(g.point(evt.clientX, evt.clientY));
                var snappedClientX = snappedClientCoords.x;
                var snappedClientY = snappedClientCoords.y;

                dx = snappedClientX - this._snappedClientX;
                dy = snappedClientY - this._snappedClientY;

                // This hash of flags makes sure we're not adjusting vertices of one link twice.
                // This could happen as one link can be an inbound link of one element in the selection
                // and outbound link of another at the same time.
                var processedLinks = {};

                this.model.each(function (element) {

                    // adjustment needed because of new Backbone version
                    element = element.attributes;

                    // TODO: snap to grid.

                    // Translate the element itself.
                    element.translate(dx, dy);

                    // Translate link vertices as well.
                    var connectedLinks = this.options.graph.getConnectedLinks(element);

                    _.each(connectedLinks, function (link) {

                        if (processedLinks[link.id]) return;

                        var vertices = link.get('vertices');
                        if (vertices && vertices.length) {

                            var newVertices = [];
                            _.each(vertices, function (vertex) {

                                newVertices.push({x: vertex.x + dx, y: vertex.y + dy});
                            });

                            link.set('vertices', newVertices);
                        }

                        processedLinks[link.id] = true;
                    });

                }, this);

                if (dx || dy) {

                    var paperScale = V(this.options.paper.viewport).scale();
                    dx *= paperScale.sx;
                    dy *= paperScale.sy;

                    // Translate also each of the `selection-box`.
                    this.$('.selection-box').each(function () {

                        var left = parseFloat($(this).css('left'), 10);
                        var top = parseFloat($(this).css('top'), 10);
                        $(this).css({left: left + dx, top: top + dy});
                    });

                    // ---------- customization start
                    // This customization is neccessary to translate the selected-view
                    // so that it frames the selected elements
                    // @author: Maximilian Göke
                    this.translateSelectedView(dx, dy);
                    // ---------- customization end

                    this._snappedClientX = snappedClientX;
                    this._snappedClientY = snappedClientY;
                }

                this.trigger('selection-box:pointermove', evt);
                break;

            // ---------- customization start
            // This is neccessary for the rotation of the group.
            // @author: Maximilian Göke
            case 'rotate':
                this.rotateElements(evt);
                break;
            // ---------- customization end
        }
    },

    /*
     * This Method is copied from joint.ui.selectionView and is a little bit customized.
     * The customized part is commented.
     */
    stopSelecting: function (evt) {

        switch (this._action) {

            case 'selecting':
                var offset = this.$el.offset();
                var width = this.$el.width();
                var height = this.$el.height();

                // Convert offset coordinates to the local point of the <svg> root element.
                var localPoint = V(this.options.paper.svg).toLocalPoint(offset.left, offset.top);

                // Take page scroll into consideration.
                localPoint.x -= window.pageXOffset;
                localPoint.y -= window.pageYOffset;

                //var elementViews = this.options.paper.findViewsInArea(g.rect(localPoint.x, localPoint.y, width, height));
                var elementViews = this.findViewsInAreaInclusive(g.rect(localPoint.x, localPoint.y, width, height));

                if (elementViews.length) {
                    // ---------- customization start
                    // It's neccessary if not all elements from the group got selected.
                    // This call add the missing elements frome the groups to the elementView.
                    // @author: Maximilian Göke
                    elementViews = this.getAllGroupElements(elementViews);
                    // ---------- customization end

                    // ---------- customization start
                    // They forgot to write the elements to the model => so I added
                    // this functionality
                    // @author: Maximilian Göke
                    _.each(elementViews, function (elementView) {
                        this.model.add(elementView.model);
                    }, this);
                    // ---------- customization end

                    // ---------- customization start
                    // @author: Maximilian Göke
                    // _.filter(elementViews, function(elementView) { })
                    // _.each(elementViews, function(elementView) { this.model.add(elementView.model); }, this);
                    // ---------- customization end

                    // Create a `selection-box` `<div>` for each element covering its bounding box area.
                    _.each(elementViews, this.createSelectionBox, this);

                    // The root element of the selection switches `position` to `static` when `selected`. This
                    // is neccessary in order for the `selection-box` coordinates to be relative to the
                    // `paper` element, not the `selection` `<div>`.
                    this.$el.addClass('selected');
                } else {

                    // Hide the selection box if there was no element found in the area covered by the
                    // selection box.
                    this.$el.hide();
                }

                //this.model.reset(_.pluck(elementViews, 'model'));
                break;

            case 'translating':

                this.options.graph.trigger('batch:stop');
                this.trigger('selection-box:pointerup', evt);
                // Everything else is done during the translation.
                break;

            case 'cherry-picking':
                // noop;  All is done in the `createSelectionBox()` function.
                // This is here to avoid removing selection boxes as a reaction on mouseup event and
                // propagating to the `default` branch in this switch.
                break;

            // ---------- customization start
            // These cases are neccessary for the new functionalities.
            // without 'grouping' and 'clone' it would reach the default case and
            // this is not wanted.
            // @author: Maximilian Göke
            case 'grouping':
                // noop; All is done in the `groupElements()` function
                break;

            case 'clone':
                // noop; Everything is done in the `cloneElements()` function
                break;

            case 'rotate':
                this.options.graph.trigger('batch:stop');
                // noop: Everything is done in 'rotateElements()' function
                break;
            // ---------- customization end

            default:
                // Hide selection if the user clicked somehwere else in the document.
                this.$el.hide().empty();
                this.model.reset([]);
                break;
        }

        delete this._action;
    },

    // ---------- manipulate joint.ui.selectionView Methods end


    /* Creates border around all selected elements */
    createSelectedView: function () {
        // remove old selected-view
        $('#selected-view').remove();
        var content = this.getBorderPoints();
        // It's neccessary because of the zoom.
        var paperScale = V(this.options.paper.viewport).scale();

        var selectedView = $('<div>', {id: 'selected-view'}).css({
            width: content.rightX * paperScale.sx - content.leftX * paperScale.sx,
            height: content.rightY * paperScale.sy - content.leftY * paperScale.sy,
            left: content.leftX * paperScale.sx,
            top: content.leftY * paperScale.sy
        });

        // add selected-view to parent
        // prepend is used because the selected-view should be behind the selection boxes.
        this.$el.prepend(selectedView);

        // remove button
        selectedView.append($('<div>', {id: 'remove-group'}));
        this.displayGroupButtons(selectedView);
        $('#selected-view').append($('<div>', {id: 'clone-group'}));
        $('#selected-view').append($('<div>', {id: 'rotate-group'}));
        this.setUpTooltips();
    },

    /* Translates selected-view. */
    translateSelectedView: function (dx, dy) {
        var selectedView = $('#selected-view');
        var left = parseFloat(selectedView.css('left'), 10);
        var top = parseFloat(selectedView.css('top'), 10);
        selectedView.css({left: left + dx, top: top + dy});
    },

    /* Calculates and returns the border points for the selected-view. */
    getBorderPoints: function () {
        var result = {leftX: null, leftY: null, rightX: null, rightY: null};

        // walks through each element and checks if it has an border point
        this.model.each(function (model) {
            // needed adjustment because of new Backbone version
            model = model.attributes;
            // use bbox instead of element position
            var bbox = model.getBBox().bbox(model.get("angle"));
            var origin = bbox.origin();
            var corner = bbox.corner();
            // checks if borderpoint exist
            if (result.leftX === null || origin.x < result.leftX) {
                result.leftX = origin.x;
            }
            if (result.leftY === null || origin.y < result.leftY) {
                result.leftY = origin.y;
            }
            if (result.rightX === null || corner.x > result.rightX) {
                result.rightX = corner.x;
            }
            if (result.rightY === null || corner.y > result.rightY) {
                result.rightY = corner.y;
            }
        });

        return result;
    },

    /* Removes the selected Elements. Gets called when the remove button is pressed */
    removeElements: function () {
        // needed adjustment because of new Backbone version
        this.model.each(function (model) {
            this.options.group.removeElement(model.get('id'));
            model.attributes.remove();
        }, this);
        this.cancelSelection();
    },

    /* Creates group with selected Elements. */
    groupElements: function (evt) {
        this.handleEventForGrouping(evt);

        // create group
        var groupID = joint.util.uuid();
        // saves each element to group
        this.model.each(function (model) {
            this.options.group.add(model.get('id'), groupID);
        }, this);

        // changed displayed button from group to ungroup
        $('#group-group').remove();
        this.displayGroupButtons($('#selected-view'));
    },

    /* Removes a group. */
    ungroupElements: function (evt) {
        this.handleEventForGrouping(evt);

        // get groupID
        var groupID = this.options.group.findGroupFromElement(this.model.last().get('id'));
        // remove group
        this.options.group.removeGroup(groupID);

        // change displayed button from ungroup to group
        $('#ungroup-group').remove();
        $('#ungroupall-group').remove();
        this.displayGroupButtons($('#selected-view'));
    },

    /* Removes groups recursivley */
    ungroupAllElements: function (evt) {
        this.handleEventForGrouping(evt);
        // get groupID
        var groupID = this.options.group.findGroupFromElement(this.model.last().get('id'));
        var groupElementIDs = this.options.group.getElementIDsFromGroup(groupID);

        groupElementIDs.forEach(function (id) {
            this.options.group.removeFirstGroup(id);
        }, this);

        $('#ungroup-group').remove();
        $('#ungroupall-group').remove();
        $('#selected-view').append($('<div>', {id: 'group-group'}));
    },

    /* is needed to continue displaying the selection boxes */
    handleEventForGrouping: function (evt) {
        evt.stopPropagation();
        evt = joint.util.normalizeEvent(evt);
        this._action = 'grouping';
    },

    /* Calculates if group or ungroup button should be displayed */
    displayGroupButtons: function (selectedView) {
        // ignore buttons if selectionView has no group object
        if (this.options.group !== undefined && this.model.length > 1) {
            // saves if selected elements belong to one group
            var onlyGroupMembers = true;
            // ugly but don't know another way yet
            this.bufferGroupID = null;
            // check if selected elements belong to one group
            this.model.each(function (model) {
                if (this.bufferGroupID === null) this.bufferGroupID = this.options.group.findGroupFromElement(model.get('id'));
                if (!(this.options.group.isInAGroup(model.get('id'))) || this.options.group.findGroupFromElement(model.get('id')) != this.bufferGroupID) onlyGroupMembers = false;
            }, this);
            // delete value - sure is sure ;)
            this.bufferGroupID = null;

            if (onlyGroupMembers) {
                selectedView.append($('<div>', {id: 'ungroup-group'}));
                selectedView.append($('<div>', {id: 'ungroupall-group'}));
            } else {
                selectedView.append($('<div>', {id: 'group-group'}));
            }
        }

        this.setUpTooltipsGroup();
    },

    /* Clones elements and select the cloned elements. Didn't clone the groupiing. */
    cloneElements: function (evt) {
        evt.stopPropagation();
        evt = joint.util.normalizeEvent(evt);
        this._action = "clone";

        var clonedElements = [];

        // clone all elements
        // needed adjustment because of new Backbone version
        this.model.each(function (model) {
            clonedElements.push(model.attributes.clone());
        });

        // reset selection
        this.cancelSelection();

        // Add cloned elements to the graph and select them
        clonedElements.forEach(function (cell) {
            // needed adjustment because of new Backbone version
            this.options.graph.addCell(cell.attributes);
            var view = this.options.paper.findViewByModel(cell);
            this.model.add(view.model);
            this.createSelectionBox(view);
        }, this);
    },

    /* Copied from joint.ui.halo:startRotating and is modified so it works for groups. */
    startRotateElements: function (evt) {
        evt.preventDefault();
        evt.stopPropagation();
        evt = joint.util.normalizeEvent(evt);

        this.options.graph.trigger('batch:start');

        this._action = 'rotate';

        var borderPoints = this.getBorderPoints();
        var rect = g.rect({
            x: borderPoints.leftX,
            y: borderPoints.leftY,
            width: borderPoints.rightX - borderPoints.leftX,
            height: borderPoints.rightY - borderPoints.leftY
        });

        this._center = rect.center();

        //mousemove event in firefox has undefined offsetX and offsetY
        if (typeof evt.offsetX === "undefined" || typeof evt.offsetY === "undefined") {
            var targetOffset = $(evt.target).offset();
            evt.offsetX = evt.pageX - targetOffset.left;
            evt.offsetY = evt.pageY - targetOffset.top;
        }

        this._rotationStart = g.point(evt.offsetX + evt.target.parentNode.offsetLeft, evt.offsetY + evt.target.parentNode.offsetTop + evt.target.parentNode.offsetHeight);

        // ---------- customization start
        // Save the rotation start angle from every element.
        // @author: Maximilian Göke
        this._rotationStartAngle = {};

        this.model.each(function (cell) {
            // needed adjustment because of new Backbone version
            this._rotationStartAngle[cell.id] = cell.attributes.get("angle") || 0;
        }, this);
        // ---------- customization end

        this._clientX = evt.clientX;
        this._clientY = evt.clientY;
    },

    /*
     * Copied from joint.ui.halo:pointermove in the case 'rotate'.
     * It's modified so it works for groups.
     */
    rotateElements: function (evt) {
        // Fixed Bug: show and save tooltip when rotating
        $('.tooltip').remove();


        var dx = evt.clientX - this._clientX;
        var dy = evt.clientY - this._clientY;

        var p = g.point(this._rotationStart).offset(dx, dy);
        var a = p.distance(this._center);
        var b = this._center.distance(this._rotationStart);
        var c = this._rotationStart.distance(p);
        var sign = (this._center.x - this._rotationStart.x) * (p.y - this._rotationStart.y) - (this._center.y - this._rotationStart.y) * (p.x - this._rotationStart.x);

        var _angle = Math.acos((a * a + b * b - c * c) / (2 * a * b));

        // Quadrant correction.
        if (sign <= 0) {
            _angle = -_angle;
        }


        var angleDiff = -g.toDeg(_angle);

        angleDiff = g.snapToGrid(angleDiff, 15);

        // ---------- customization start
        // Change angle for every element
        // @author: Maximilian Göke
        this.model.each(function (cell) {
            cell = cell.attributes;
            cell.rotate(angleDiff + this._rotationStartAngle[cell.id], true, this._center);
            // remove old selectionBox
            this.$('[data-model="' + cell.get('id') + '"]').remove();
            // create new selectionBox
            this.createSelectionBox(this.options.paper.findViewByModel(cell), 'rotate');
        }, this);
        // ---------- customization end
    },

    /*
     * Selects all grouped Elements. Gets Called if one element of the group
     * gets selected.
     */
    getAllGroupElements: function (elementViews) {
        // checks how much groups are selected
        var groupIDs = [];
        elementViews.forEach(function (view) {
            groupIDs.push(this.options.group.findGroupFromElement(view.model.get('id')));
        }, this);

        // remove duplicated groupIDs
        _.uniq(groupIDs);

        // add all elements from group to elementViews
        groupIDs.forEach(function (groupID) {
            if (groupID !== undefined) {
                var elements = this.options.group.getElementIDsFromGroup(groupID);
                elements.forEach(function (id) {
                    elementViews.push(this.options.paper.findViewByModel(this.options.graph.getCell(id)));
                }, this);
            }
        }, this);

        return _.uniq(elementViews);
    },

    findViewsInAreaInclusive: function (r) {
        r = g.rect(r);

        // was planned this way but there is an error with the context
        // var views = _.map(this.options.graph.getElements(), this.options.paper.findViewByModel);
        // so it's solved with a forEach loop
        var views = [];
        this.options.graph.getElements().forEach(function (element) {
            views.push(this.options.paper.findViewByModel(element));
        }, this);

        return _.filter(views, function (view) {
            return r.containsRect(g.rect(V(view.el).bbox(false, this.viewport)));
        }, this);

    }
});
