import joint from 'jointjs';
import $ from 'jquery';
import _ from 'lodash';

import linkTypeSelector from './linkTypeSelector';

const g = joint.g;
const V = joint.V;

// checks recursively if any parent of element is the paperscroller
// used to prevent initial link dragging outside of paper
joint.dia.LinkView.prototype.isAnyParentPaperScroller = function(element){
    var parent = element.parent();
    if(parent.hasClass("body") || parent.length === 0){
        return false;
    }

    if(parent.hasClass("paper-scroller")){
        return true;
    }
    return this.isAnyParentPaperScroller(parent);
};

joint.dia.LinkView.prototype.pointermove = function(evt, x, y) {
    // Check if element at mouse position == paperscroller
    // else omit event and do nothing
    if(!this.isAnyParentPaperScroller($(document.elementFromPoint(evt.pageX, evt.pageY))))
    {
        return;
    }

    joint.dia.CellView.prototype.pointermove.apply(this, arguments);

    switch (this._action) {

        case 'vertex-move':

            var vertices = _.clone(this.model.get('vertices'));
            vertices[this._vertexIdx] = { x: x, y: y };
            this.model.set('vertices', vertices);
            break;

        case 'arrowhead-move':

            if (this.paper.options.snapLinks) {

                // checking view in close area of the pointer

                var r = this.paper.options.snapLinks.radius || 50;
                var viewsInArea = this.paper.findViewsInArea({ x: x - r, y: y - r, width: 2 * r, height: 2 * r });

                this._closestView && this._closestView.unhighlight(this._closestEnd.selector);
                this._closestView = this._closestEnd = null;

                var pointer = g.point(x,y);
                var distance, minDistance = Number.MAX_VALUE;

                _.each(viewsInArea, function(view) {

                    // skip connecting to the element in case '.': { magnet:
                    // false } attribute present

                    if (view.el.getAttribute('magnet') !== 'false') {
                        // find distance from the center of the model to pointer
                        // coordinates


                        distance = view.model.getBBox().center().distance(pointer);

                        // the connection is looked up in a circle area by
                        // `distance < r`

                        if (distance < r && distance < minDistance) {

                            if (this.paper.options.validateConnection.apply(
                                    this.paper, this._validateConnectionArgs(view, null)
                                )) {
                                minDistance = distance;
                                this._closestView = view;
                                this._closestEnd = { id: view.model.id };
                            }
                        }
                    }

                    view.$('[magnet]').each(_.bind(function(index, magnet) {

                        var bbox = V(magnet).bbox(false, this.paper.viewport);

                        distance = pointer.distance({
                            x: bbox.x + bbox.width / 2,
                            y: bbox.y + bbox.height / 2
                        });

                        if (distance < r && distance < minDistance) {

                            if (this.paper.options.validateConnection.apply(
                                    this.paper, this._validateConnectionArgs(view, magnet)
                                )) {
                                minDistance = distance;
                                this._closestView = view;
                                this._closestEnd = {
                                    id: view.model.id,
                                    selector: view.getSelector(magnet),
                                    port: magnet.getAttribute('port')
                                }
                            }
                        }

                    }, this));

                }, this);

                this._closestView && this._closestView.highlight(this._closestEnd.selector);

                this.model.set(this._arrowhead, this._closestEnd || { x: x, y: y });

            } else {

                // checking views right under the pointer

                // Touchmove event's target is not reflecting the element under
                // the coordinates as mousemove does.

                // It holds the element when a touchstart triggered.
                var target = (evt.type === 'mousemove')
                    ? evt.target
                    : document.elementFromPoint(evt.clientX, evt.clientY);

                if (this._targetEvent !== target) {
                    // Unhighlight the previous view under pointer if there was
                    // one.

                    this._magnetUnderPointer && this._viewUnderPointer.unhighlight(this._magnetUnderPointer);
                    this._viewUnderPointer = this.paper.findView(target);
                    if (this._viewUnderPointer) {
                        // If we found a view that is under the pointer, we need
                        // to find the closest

                        // magnet based on the real target element of the event.
                        this._magnetUnderPointer = this._viewUnderPointer.findMagnet(target);

                        if (this._magnetUnderPointer && this.paper.options.validateConnection.apply(
                                this.paper,
                                this._validateConnectionArgs(this._viewUnderPointer, this._magnetUnderPointer)
                            )) {
                            // If there was no magnet found, do not highlight
                            // anything and assume there
                            // is no view under pointer we're interested in
                            // reconnecting to.
                            // This can only happen if the overall element has
                            // the attribute `'.': { magnet: false }`.



                            this._magnetUnderPointer && this._viewUnderPointer.highlight(this._magnetUnderPointer);

                            linkTypeSelector.focusElement(this.model.id,
                                this._viewUnderPointer.model,
                                evt.clientX,
                                evt.clientY);
                        } else {
                            // This type of connection is not valid. Disregard
                            // this magnet.

                            this._magnetUnderPointer = null;
                        }
                    } else {
                        linkTypeSelector.lostFocus(document.elementFromPoint(evt.clientX, evt.clientY));

                        // Make sure we'll delete previous magnet
                        this._magnetUnderPointer = null;
                    }
                }

                this._targetEvent = target;

                this.model.set(this._arrowhead, { x:x, y:y });
            }

            break;
    }

    this._dx = x;
    this._dy = y;
};

joint.dia.LinkView.prototype.pointerup = function(evt) {
    joint.dia.CellView.prototype.pointerup.apply(this, arguments);

    if (this._action === 'arrowhead-move') {

        if (this.paper.options.snapLinks) {

            this._closestView && this._closestView.unhighlight(this._closestEnd.selector);
            this._closestView = this._closestEnd = null;

        } else {

            if (this._magnetUnderPointer) {
                this._viewUnderPointer.unhighlight(this._magnetUnderPointer);
                // Find a unique `selector` of the element under pointer
                // that is a magnet. If the
                // `this._magnetUnderPointer` is the root element of the
                // `this._viewUnderPointer` itself,
                // the returned `selector` will be `undefined`. That means
                // we can directly pass it to the



                // `source`/`target` attribute of the link model below.

                // custom
                if(linkTypeSelector.canSetLink()){

                    // joint.js Code to set the link
                    linkTypeSelector.replaceLink(this.model.set(this._arrowhead, {
                        id: this._viewUnderPointer.model.id,
                        selector: this._viewUnderPointer.getSelector(this._magnetUnderPointer),
                        port: $(this._magnetUnderPointer).attr('port')
                    }), this._viewUnderPointer.model);

                    // custom
                } else {
                    if (this.model) {
                        this.model.remove();
                    }
                    this.remove();
                }
            }

            // Custom Code
            if(!linkTypeSelector.canSetLink()){
                if (this.model) {
                    this.model.remove();
                }
                this.remove();
            } else {
                linkTypeSelector.canSetLink(false);

            }

            delete this._viewUnderPointer;
            delete this._magnetUnderPointer;
            delete this._staticView;
            delete this._staticMagnet;
        }

        this._afterArrowheadMove();
    }

    delete this._action;
};

joint.dia.LinkView.prototype.updateLabelPositions = function() {
    if (!this._V.labels) return this;

    // This method assumes all the label nodes are stored in the `this._labelCache` hash table
    // by their indexes in the `this.get('labels')` array. This is done in the `renderLabels()` method.

    var labels = this.model.get('labels') || [];
    if (!labels.length) return this;

    var connectionElement = this._V.connection.node;
    var connectionLength = connectionElement.getTotalLength();

    // Firefox returns connectionLength=NaN in odd cases (for bezier curves).
    // In that case we won't update labels at all.
    if (!_.isNaN(connectionLength)) {

        var samples;

        _.each(labels, function(label, idx) {

            var position = label.position;
            var distance = _.isObject(position) ? position.distance : position;
            var offset = _.isObject(position) ? position.offset : { x: 0, y: 0 };

            distance = (distance > connectionLength) ? connectionLength : distance; // sanity check
            distance = (distance < 0) ? connectionLength + distance : distance;
            distance = (distance > 1) ? distance : connectionLength * distance;

            // Fix for failing getPointAtLength, this is fixed in newer joint.js versions
            // see https://github.com/clientIO/joint/pull/183
            // ---- Custom code
            if(_.isNaN(distance)) {
                distance = connectionLength / 2;
            }
            // ---- Custom code end

            var labelCoordinates = connectionElement.getPointAtLength(distance);

            if (_.isObject(offset)) {

                // Just offset the label by the x,y provided in the offset object.
                labelCoordinates = g.point(labelCoordinates).offset(offset.x, offset.y);

            } else if (_.isNumber(offset)) {

                if (!samples) {
                    samples = this._samples || this._V.connection.sample(this.options.sampleInterval);
                }

                // Offset the label by the amount provided in `offset` to an either
                // side of the link.

                // 1. Find the closest sample & its left and right neighbours.
                var minSqDistance = Infinity;
                var closestSample;
                var closestSampleIndex;
                var p;
                var sqDistance;
                for (var i = 0, len = samples.length; i < len; i++) {
                    p = samples[i];
                    sqDistance = g.line(p, labelCoordinates).squaredLength();
                    if (sqDistance < minSqDistance) {
                        minSqDistance = sqDistance;
                        closestSample = p;
                        closestSampleIndex = i;
                    }
                }
                var prevSample = samples[closestSampleIndex - 1];
                var nextSample = samples[closestSampleIndex + 1];

                // 2. Offset the label on the perpendicular line between
                // the current label coordinate ("at `distance`") and
                // the next sample.
                var angle = 0;
                if (nextSample) {
                    angle = g.point(labelCoordinates).theta(nextSample);
                } else if (prevSample) {
                    angle = g.point(prevSample).theta(labelCoordinates);
                }
                labelCoordinates = g.point(labelCoordinates).offset(offset).rotate(labelCoordinates, angle - 90);
            }

            this._labelCache[idx].attr('transform', 'translate(' + labelCoordinates.x + ', ' + labelCoordinates.y + ')');

        }, this);
    }

    return this;
};