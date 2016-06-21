joint.shapes.modigen.MLink = joint.dia.Link.extend({
    //Override default markup to add placing
    markup: [
        '<path class="connection" stroke="black"/>',
        '<path class="marker-source" fill="black" stroke="black" />',
        '<path class="marker-target" fill="black" stroke="black" />',
        '<path class="connection-wrap"/>',
        '<g class="labels"/>',
        '<g class="placings"/>',
        '<g class="marker-vertices"/>',
        '<g class="marker-arrowheads"/>',
        '<g class="link-tools"/>'
    ].join(''),

    //Template for placing
    placingMarkup: [
        '<g class="placing">',
        '</g>'
    ].join(''),

    //Set type
    defaults: joint.util.deepSupplement({
        type: 'modigen.MLink'
    }, joint.dia.Link.prototype.defaults)
});

joint.shapes.modigen.MLinkView = joint.dia.LinkView.extend({
    //We cache the placings for the update function
    _placingCache: {},

    /*
     For the most part, this is copied from joint.dia.Link, except the call to renderPlacings
     renderPlacings has to be called after this._V has been intitialised, but before update is called,
     so we had to copy the function.
     */
    render: function () {
        this.$el.empty();
        var children = V(this.model.get('markup') || this.model.markup);

        if (!_.isArray(children)) children = [children];

        this._V = {}; // vectorized markup;
        _.each(children, function (child) {
            var c = child.attr('class');
            c && (this._V[$.camelCase(c)] = child);
        }, this);

        // Only the connection path is mandatory
        if (!this._V.connection) throw new Error('link: no connection path in the markup');

        // partial rendering
        this.renderTools();
        this.renderVertexMarkers();
        this.renderArrowheadMarkers();
        this.renderPlacings();

        V(this.el).append(children);

        this.renderLabels();


        this.watchSource(this.model, this.model.get('source'))
            .watchTarget(this.model, this.model.get('target'))
            .update();

        return this;
    },

    update: function () {
        joint.dia.LinkView.prototype.update.apply(this, arguments);
        this.updatePlacingPositions();
        return this;
    },

    renderPlacings: function () {
        if (!this._V.placings) return this;

        var placings = this.model.get('placings') || []; //Get placings from model
        if (!placings.length) return this;

        var $placings = $(this._V.placings.node).empty(); //Get placings node in DOM (and clear it)

        var placingTemplate = _.template(this.model.get('placingMarkup') || this.model.placingMarkup);
        var placingNodeInstance = V(placingTemplate());

        _.each(placings, function (placing, idx) {
            var placingNode = placingNodeInstance.clone();

            //Apply the attributes to the placing markup and add it to the node
            placingNode.append(V(placing.markup).attr(placing.attrs));
            $placings.append(placingNode.node); //Add the node to the DOM
            this._placingCache[idx] = placingNode; //cache the placing for update()

        }, this);

        return this;
    },

    updatePlacingPositions: function () {
        if (!this._V.placings) return this;

        var placings = this.model.get('placings') || [];
        if (!placings.length) return this;

        //Since placings are set AFTER the link is drawn (context menu), renderPlacings has to be called here,
        // because they may not have been rendered yet!
        if (placings.length != this._placingCache.length) {
            this.renderPlacings();
        }

        var connectionElement = this._V.connection.node;
        var connectionLength = connectionElement.getTotalLength();

        _.each(placings, function (placing, idx) {
            var positionX = placing.position;
            positionX = (positionX > connectionLength) ? connectionLength : positionX; // sanity check
            positionX = (positionX < 0) ? connectionLength + positionX : positionX;
            positionX = positionX > 1 ? positionX : connectionLength * positionX;

            var placingCoordinates = connectionElement.getPointAtLength(positionX);
            //Get the reference point towards which the placing is oriented
            var referencePoint = this.getReferencePoint(placingCoordinates);

            this.rotateAndTranslatePlacing(this._placingCache[idx], placingCoordinates, referencePoint, this.paper.viewport);

        }, this);

        return this;
    },

    /*
     Rotates the placing and positions it according to the orientation of the link
     see also: joint.js: VElement.translateAndAutoOrient()
     */
    rotateAndTranslatePlacing: function (placing, position, reference, target) {
        var angle = g.point(position).changeInAngle(position.x - reference.x, position.y - reference.y, reference);
        placing.rotate(angle, 0, 0);

        var bbox = placing.bbox(false, target);
        var finalPosition = g.point(position).move(reference, bbox.width / 2);
        placing.translate(position.x + (position.x - finalPosition.x), position.y + (position.y - finalPosition.y));
    },

    /**
     * Checks all the vertices to see which is the reference point at a given point on the link
     * @param coordinates
     * @returns {x,y}
     */
    getReferencePoint: function (coordinates) {
        var vertices = this.model.get('vertices');
        //If no vertices are present, orient towards the target point
        if (!vertices || vertices.length < 1) {
            return this.targetPoint;
        }

        for (var idx = 0; idx < vertices.length; idx++) {

            //Last vertex: Check if target point is the reference
            if (idx == vertices.length - 1) {
                if (this.isReference(vertices[idx], this.targetPoint, coordinates)) {
                    return this.targetPoint;
                }
            }

            //First vertex, orientation needs to factor in source point
            if (idx == 0) {
                if (this.isReference(this.sourcePoint, vertices[0], coordinates)) {
                    return vertices[0];
                }
            } else {
                if (this.isReference(vertices[idx - 1], vertices[idx], coordinates)) {
                    return vertices[idx];
                }
            }
        }
        //No reference point found. This actually shouldn't happen. Ever.
        return this.sourcePoint; //Fallback.
    },

    /**
     * Checks, if a given point is on a line between two other points
     *
     * @param lineStart Start of the line
     * @param lineEnd End of the line
     * @param point Point to check
     * @returns boolean
     */
    isReference: function (lineStart, lineEnd, point) {
        var xLow;       //Lower x value of lineStart/lineEnd
        var xHigh;      //Lower x value of lineStart/lineEnd
        var yLow;       //Higher x value of lineStart/lineEnd
        var yHigh;      //Higher x value of lineStart/lineEnd

        if (lineStart.x < lineEnd.x) {
            xLow = lineStart.x;
            xHigh = lineEnd.x
        } else {
            xLow = lineEnd.x;
            xHigh = lineStart.x;
        }

        if (lineStart.y < lineEnd.y) {
            yLow = lineStart.y;
            yHigh = lineEnd.y;
        } else {
            yLow = lineEnd.y;
            yHigh = lineStart.y;
        }

        //Check if point is within bounding box of the line (if not, we don't have a valid reference point)
        if (point.x > xHigh || point.x < xLow || point.y > yHigh || point.y < yLow) {
            return false;
        } else if (xHigh == xLow) {
            // Vertical line, no further check is needed
            return true;
        }

        var m = this.getSlope(lineStart, lineEnd);
        var c = lineStart.y - m * lineStart.x;

        return this._floatEquals(point.y, m * point.x + c, 1);
    },

    /**
     * Calcualates the slope of a line from two coordinates
     *
     * @param p1
     * @param p2
     * @returns {number}
     */
    getSlope: function (p1, p2) {
        return (p2.y - p1.y) / (p2.x - p1.x);
    },

    /**
     * Checks, if floating point numbers f1 and f2 are equal, within a given tolerance (delta)
     *
     * @param f1
     * @param f2
     * @param delta
     * @returns {boolean}
     * @private
     */
    _floatEquals: function (f1, f2, delta) {
        return Math.abs(f1 - f2) < delta;
    }
});