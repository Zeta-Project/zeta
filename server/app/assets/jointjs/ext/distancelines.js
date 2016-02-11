/*
 * This View manages the distancelines. The distancelines give the opportunity
 * to align the elements more accurately. The distancelines shows which elements
 * have the same distance like the active object.
 * @author Maximilian Göke
 */
Distancelines = Backbone.View.extend({
    options: {
        graph: void 0,
        paper: void 0,
        // tells at which distance the distacne to an element should be detected
        distance: 5,
        // saves if the plugin is active or not
        active: true,
        // tells at how many neighbours it should only lookup the neighbours and not
        // the whole adjacencyList
        onlyNeighbours: 500
    },

    /* Constructor. Set up distancelines */
    initialize: function () {
        this.$el.appendTo(this.options.paper.el);
        this.registerListener();
        // save element and it neighbours with the associated distance
        this.adjacencyList = {};
        this.initializeGlobals();
    },

    initializeGlobals: function () {
        this.lastDistance = {north: null, south: null, west: null, east: null};
        this.firstTranslate = {x: true, y: true};
        this._client = {x: 0, y: 0};
    },

    /* Register listener */
    registerListener: function () {
        this.listenTo(this.options.graph, 'add', this.initializeElementsDistances);
        this.listenTo(this.options.graph, 'change', this.elementChanges);
        this.listenTo(this.options.graph, 'remove', this.deleteElementFromAdjacencyList);
        this.listenTo(this.options.paper, 'cell:pointermove', this.startElementMoves);
        this.listenTo(this.options.paper, 'cell:pointerup', this.removeDistanceLines);
    },

    /* Unregisters listener. */
    unregisterListener: function () {
        this.stopListening();
    },

    /* This function gets called when the distancesline becomes turned on */
    start: function () {
        this.options.active = true;
        this.registerListener();
        this.options.graph.getElements().forEach(function (cell) {
            if (cell instanceof joint.dia.Link) return;
            this.adjacencyList[cell.get('id')] = this.getDistances(cell);
        }, this);
    },

    /* This function gets called when the distancesline becomes turned off */
    stop: function () {
        this.options.active = false;
        this.unregisterListener();
        this.adjacencyList = {};
    },

    /*
     * Initialize distances for Elements
     * Input structure:
     * cell: joint.dia.Element
     */
    initializeElementsDistances: function (cell) {
        // do nothing wenn cell is null or a Link
        if (cell === null || cell instanceof joint.dia.Link) return;
        // get distances, save them in the adjacencyList and update neighbours
        var distances = this.getDistances(cell);
        this.adjacencyList[cell.get('id')] = distances;
        this.updateNeighbours(cell, distances);
    },

    /*
     * Returns an object with the distances in each direction.
     * Input structure:
     * cell: joint.dia.Element
     *
     * Return:
     * {
     *  north: {
     *    neighboursID1: distanceToNeighbour,
     *    neighboursID2: distanceToNeighbour
     *  },
     *  south: {},
     *  east: {},
     *  west: {}
     * }
     */
    getDistances: function (cell) {
        var bbox = cell.getBBox().bbox(cell.get('angle'));
        // origin is the top left corner
        var origin = bbox.origin();
        // corner is the bottom right corner
        var corner = bbox.corner();
        var distance = 0;
        var result = {north: {}, east: {}, south: {}, west: {}};
        var cellID = cell.get('id');

        this.options.paper.model.getElements().every(function (model) {
            var modelID = model.get('id');
            if (cellID == modelID) return true;
            // calculate corners from tmp element
            //
            // tmpOrigin *-----------+
            //           |           |
            //           +-----------* tmpCorner
            //
            var tmpBBox = model.getBBox().bbox(model.get('angle'));
            var tmpOrigin = tmpBBox.origin();
            var tmpCorner = tmpBBox.corner();

            if (tmpCorner.x >= origin.x && tmpOrigin.x <= corner.x) {
                // direction is north or south
                if (tmpOrigin.y <= origin.y && tmpOrigin.y <= corner.y && tmpCorner.y <= corner.y) {
                    // direction is north
                    result.north[model.get('id')] = tmpCorner.y < origin.y ? (origin.y - tmpCorner.y) : -1;
                }

                if (origin.y <= tmpOrigin.y && origin.y <= tmpCorner.y && corner.y <= tmpCorner.y) {
                    //direction is south
                    result.south[model.get('id')] = corner.y < tmpOrigin.y ? (tmpOrigin.y - corner.y) : -1;
                }
            }
            if (tmpCorner.y >= origin.y && tmpOrigin.y <= corner.y) {
                // direction is west or east
                if (tmpOrigin.x <= origin.x && tmpOrigin.x <= corner.x && tmpCorner.x <= corner.x) {
                    // direction is west
                    result.west[model.get('id')] = tmpCorner.x < origin.x ? (origin.x - tmpCorner.x) : -1;
                }

                if (origin.x <= tmpOrigin.x && origin.x <= tmpCorner.x && corner.x <= tmpCorner.x) {
                    // direction is east
                    result.east[model.get('id')] = corner.x < tmpOrigin.x ? (tmpOrigin.x - corner.x) : -1;
                }
            }
            return true;
        }, this);
        return result;
    },

    /*
     * Updates Neighbours distance to *cell*.
     * Input structure:
     * cell: joint.dia.Element,
     * distances: return from getDistances()
     */
    updateNeighbours: function (cell, distances) {
        var cellID = cell.get('id');
        for (var direction in distances) {
            for (var element in distances[direction]) {
                this.adjacencyList[element][this.getOpposite(direction)][cellID] = distances[direction][element];
            }
        }
    },

    /* Returns the opposite direction */
    getOpposite: function (direction) {
        if (direction == 'north') return 'south';
        if (direction == 'south') return 'north';
        if (direction == 'east') return 'west';
        if (direction == 'west') return 'east';
    },

    /*
     * Gets called when an element gets moved.
     * This function evaluates if the distance should be shown to all element or
     * only to the neighbours. If only the distance to the neighbours should be
     * shown then it creates a list with the neighbours instead of using
     * adjacencyList which contains all elements.
     *
     * Input structure:
     * cellView: joint.dia.ElementView,
     * evt: Event
     */
    startElementMoves: function (cellView, evt) {
        if (cellView instanceof joint.dia.LinkView) return;
        var list = {};
        if (_.size(this.adjacencyList) > this.options.onlyNeighbours) {
            var cellID = cellView.model.get('id');
            this.getNearestNeighbours(cellID).forEach(function (cellID) {
                if (cellID !== null) list[cellID] = this.adjacencyList[cellID];
            }, this);
        } else {
            list = this.adjacencyList;
        }
        this.elementMoves(cellView.model, list, evt);
    },

    /*
     * Checks if elements have the same distances.
     * Input structure:
     * cell: joint.dia.Element,
     * list: list with the same structure like adjacencyList
     */
    elementMoves: function (cell, list, evt) {
        var cellID = cell.get('id');
        var shortestDistancesToElements = this.getShortestEdges(cellID);
        // find shapes with the same distance
        var sameDistance = {north: false, east: false, south: false, west: false};
        var sameDistanceY = [];
        var sameDistanceX = [];

        // iterates over complete adjacencyList or only the neighbours
        for (var modelID in list) {
            if (modelID == cellID) continue;
            // save old information from adjacencyList of current element
            var tmpInformation = this.adjacencyList[modelID];
            // get shortes Edge to all directions
            for (var direction in tmpInformation) {
                var shortestDistance = this.getShortestEdge(modelID, direction);

                if (shortestDistancesToElements[direction] !== null && shortestDistance !== null &&
                    shortestDistance != -1 && shortestDistancesToElements[direction] != -1) {
                    // It's possible that moved element and current element have the same distance
                    if (!(sameDistance[direction]) && Math.abs(shortestDistancesToElements[direction] - shortestDistance) <= this.options.distance) {
                        // elements have the same distance
                        // this only get called the first time a distance is found to a direction
                        sameDistance[direction] = shortestDistance;
                        moveElement(cell, sameDistance[direction], direction, this);
                    }
                    if (sameDistance[direction] && shortestDistance == sameDistance[direction]) {
                        // elements have the same distance
                        var obj = {cell: modelID, direction: direction};
                        if (direction == 'north' || direction == 'south' && sameDistance.north != sameDistance.south) {
                            sameDistanceY.push(obj);
                        } else if (direction == 'east' || direction == 'west' && sameDistance.east != sameDistance.west) {
                            sameDistanceX.push(obj);
                        }
                    }
                }
            }
        }

        var allFalse = true;
        // put active cell in the sameDistance lists too
        for (var key in sameDistance) {
            if (sameDistance[key]) {
                allFalse = false;
                // remove duplicated lines
                if (key == 'east' || key == 'west') {
                    sameDistanceX = removeDuplicates(sameDistanceX, cellID, key, sameDistance[key], this);
                } else {
                    sameDistanceY = removeDuplicates(sameDistanceY, cellID, key, sameDistance[key], this);
                }
                // insert active element in lists
                var obj = {cell: cellID, direction: key};
                key == 'north' || key == 'south' ? sameDistanceY.push(obj) : sameDistanceX.push(obj);
            }
        }

        if (allFalse) {
            this.lastDistance.north = null;
            this.lastDistance.south = null;
            this.lastDistance.east = null;
            this.lastDistance.west = null;
        }

        // show lines
        this.createAndShowDistanceLines(sameDistanceX, sameDistanceY);

        /*
         * Removes duplicated entrie in the list. For Example the distance in east
         * and west is the same. This would display from element 1 to element 2 two
         * lines because both entries are saved in the sameDistance list.
         *
         * +-+   +-----+
         * |1|   |  2  |
         * +-+   +-----+
         *
         * Input structure:
         * list: the list that saves the sameDistances,
         * cellID: cellID of the moved element,
         * key: is a direction(north || south ...),
         * distance: int,
         * context: this(distancelines)
         */
        function removeDuplicates(list, cellID, key, distance, context) {
            return _.without(list, _.findWhere(list, {
                cell: context.getNodeWithDirectionAndDistance(cellID, key, distance),
                direction: context.getOpposite(key)
            }));
        }

        /*
         * Moves Element using the translate function.
         * Input structure:
         * cell: joint.dia.Element,
         * distance: int,
         * direction: north || south || east || west (String),
         * context: context from distancelines
         */
        function moveElement(cell, distance, direction, context) {
            var distanceCell = context.options.graph.getCell(context.getNodeWithDirectionAndDistance(cell.get('id'), direction, context.getShortestEdge(cell.get('id'), direction)));
            var cellBBox = cell.getBBox().bbox(cell.get('angle'));
            var oldBBox = g.rect(_.extend({}, cellBBox));
            var distanceCellBBox = distanceCell.getBBox().bbox(distanceCell.get('angle'));
            var position = cell.get('position');
            var size = cell.get('size');

            if (direction == 'north') {
                cellBBox.y = distanceCellBBox.corner().y + distance;
                cellBBox.y = fixGlueBug(cellBBox.y, oldBBox.y, 'y', 'north', distance, context);
            } else if (direction == 'south') {
                cellBBox.y = distanceCellBBox.origin().y - distance - size.height;
                cellBBox.y = fixGlueBug(cellBBox.y, oldBBox.y, 'y', 'south', distance, context);
            } else if (direction == 'east') {
                cellBBox.x = distanceCellBBox.origin().x - distance - size.width;
                cellBBox.x = fixGlueBug(cellBBox.x, oldBBox.x, 'x', 'east', distance, context);
            } else if (direction == 'west') {
                cellBBox.x = distanceCellBBox.corner().x + distance;
                cellBBox.x = fixGlueBug(cellBBox.x, oldBBox.x, 'x', 'west', distance, context);
            } else {
                // no need to translate
                return;
            }

            // calculates new Position
            var center = cellBBox.center();
            var left = center.x - size.width / 2;
            var top = center.y - size.height / 2;
            cell.translate(left - position.x, top - position.y);
        }

        /*
         * Workaround that the element don't glue.
         * bbox: bbox.x || bbox.y expected
         * oldBBox: oldBBox.x || oldBBox.y
         * coord: 'x' || 'y'
         */
        function fixGlueBug(bbox, oldBBox, coord, direction, foundDistance, context) {
            // setup distance
            var distance = context.options.distance;

            // calculate distance if the same guideline is found multiple times in a row
            if (_.isEqual(context.lastDistance[direction], foundDistance) && foundDistance !== undefined) {
                distance = (coord == 'x' ? evt.clientX : evt.clientY) - context._client[coord];
            }

            // initial lasstCoord
            if (foundDistance !== undefined && context.lastDistance[direction] === null) {
                context.lastDistance[direction] = foundDistance;
                context._client[coord] = coord == 'x' ? evt.clientX : evt.clientY;
            }

            // reset lastDistnace if current position of active element is greater than
            // the distance defined in the options
            if (Math.abs(context.lastDistance[direction] - foundDistance) > context.options.distance) {// || foundDistance) {
                context.lastDistance[direction] = null;
                context.firstTranslate[coord] = true;
                context._client[coord] = null;
            }

            // reset firstTranslate. Important if elements gets dragged to the left or top
            if (Math.abs(bbox - oldBBox) <= context.options.distance && !context.firstTranslate[coord]) {
                context.firstTranslate[coord] = true;
            }

            if (Math.abs(distance) > context.options.distance) {

                if (distance < 0 && context.firstTranslate[coord]) {
                    // to top or left => use translate only the first time the elment gets moved
                    context.firstTranslate[coord] = false;
                    return bbox - (bbox % context.options.paper.options.gridSize);
                } else {
                    // return value from oldBBox
                    return oldBBox;
                }
            }

            //return current value
            return bbox;
        }
    },

    /*
     * Gets called when an element gets changed. The main task is to keep the
     * adjacencyList valid.
     * Input structure:
     * cell: joint.dia.Element
     */
    elementChanges: function (cell) {
        // console.log(cell);
        if (cell === null || cell instanceof joint.dia.Link) return;
        var cellID = cell.get('id');
        // save old distances and shapes
        var oldDistancesToElements = this.adjacencyList[cellID];
        // get current values for distance and shapes
        var distancesToElements = this.getDistances(cell);
        // console.log(distancesToElements);
        // get elements from old which are not in distancesToElements
        var oldElements = getDifferences(distancesToElements, oldDistancesToElements, this);
        // remove this cell from adjacencyList of the oldElements
        oldElements.forEach(function (obj) {
            delete this.adjacencyList[obj.id][obj.direction][cellID];
        }, this);
        //save new information
        this.adjacencyList[cellID] = distancesToElements;
        // update neighbours case 1 und 2
        this.updateNeighbours(cell, distancesToElements);

        /*
         * Returns the differences between distanceToElements and
         * oldDistancesToElements. Is needed to detect if a neighbour isn't a
         * neighbour anymore.
         */
        function getDifferences(distancesToElements, oldDistancesToElements, context) {
            var result = [];
            for (var direction in oldDistancesToElements) {
                for (var element in oldDistancesToElements[direction]) {
                    if (distancesToElements[direction][element] === undefined) {
                        result.push({id: element, direction: context.getOpposite(direction)});
                    }
                }
            }
            return result;
        }
    },

    /* Returns the shortes edges from cellID. cellID is a String */
    getShortestEdges: function (cellID) {
        var result = {};
        for (var direction in this.adjacencyList[cellID]) {
            result[direction] = this.getShortestEdge(cellID, direction);
        }
        return result;
    },

    /*
     * Returns the shortes edge from cellID in a direction. cellID and direction
     * are Strings
     */
    getShortestEdge: function (cellID, direction) {
        var elements = this.adjacencyList[cellID][direction];
        var shortest = null;
        _.values(elements).forEach(function (distance) {
            if (shortest === null || distance < shortest) shortest = distance;
        });
        return shortest;
    },

    /* Returns the nearest Neighbours. cellID is a String. */
    getNearestNeighbours: function (cellID) {
        var result = [];
        for (var direction in this.adjacencyList[cellID]) {
            result.push(this.getNearestNeighbour(cellID, direction));
        }
        return result;
    },

    /*
     * Returns the nearest Neighbour in a direction. cellID and directions
     * are Strings.
     */
    getNearestNeighbour: function (cellID, direction) {
        var elements = this.adjacencyList[cellID][direction];
        var nearest = null;
        _.keys(elements).forEach(function (cell) {
            if (nearest === null || elements[cell] < elements[nearest]) {
                nearest = cell;
            }
        });
        return nearest;
    },

    /*
     * Returns a node that is found through the cellID, direction and distance.
     * cellID and direction are Strings and distnace is a int.
     */
    getNodeWithDirectionAndDistance: function (cellID, direction, distance) {
        var elements = this.adjacencyList[cellID][direction];
        var node = null;
        _.keys(elements).forEach(function (modelID) {
            if (this.adjacencyList[cellID][direction][modelID] == distance) node = modelID;
        }, this);
        return node;
    },

    /*
     * Creates and shows the distance lines
     * Structure of content from both arrays:
     * {
     *   cell: id (string),
     *   direction: north || south || east || west (String)
     * }
     */
    createAndShowDistanceLines: function (arrayX, arrayY) {
        this.removeDistanceLines();
        var paperScale = V(this.options.paper.viewport).scale();
        arrayX.forEach(function (obj) {
            createDistanceLine(obj, 'y', this);
        }, this);

        arrayY.forEach(function (obj) {
            createDistanceLine(obj, 'x', this);
        }, this);

        /*
         * Creates a distanceline.
         * Structure of input:
         * obj: { cell: ..., direction: ...}
         * coord: 'x' || 'y'
         * context: this
         */
        function createDistanceLine(obj, coord, context) {
            var distance = context.getShortestEdge(obj.cell, obj.direction);
            var model = context.options.graph.getCell(obj.cell);
            var bbox = model.getBBox().bbox(model.get('angle'));
            // dlo = distanceLineObject => saves all necessary values
            var dlo = {
                startHelp: 0, leftOrTop: 0, startPoint: 0, distance: distance,
                topBorder: 0, bottomBorder: 0, leftBorder: 0, rightBorder: 0, center: bbox.center()[coord]
            };
            // startPoint is needed for the distanceline
            dlo.startPoint = obj.direction == 'west' || obj.direction == 'north' ? bbox[getOppositeSign(coord)] - distance :
            bbox[getOppositeSign(coord)] + (obj.direction == 'east' ? bbox.width : bbox.height);

            // get BBox of the neighbour from obj.cell in the direction obj.direction
            // with the distance distance
            var tmpModel = context.options.graph.getCell(context.getNodeWithDirectionAndDistance(obj.cell, obj.direction, distance));
            if (tmpModel === undefined) return;
            var tmpBBox = tmpModel.getBBox().bbox(tmpModel.get('angle'));

            // needed for dotted helpline
            switch (obj.direction) {
                case 'west':
                    dlo.leftBorder = 1;
                    break;
                case 'east':
                    dlo.rightBorder = 1;
                    break;
                case 'north':
                    dlo.topBorder = 1;
                    break;
                case 'south':
                    dlo.bottomBorder = 1;
                    break;
            }

            // needed for dotted helpline
            if (bbox.corner()[coord] > tmpBBox.corner()[coord]) {
                dlo.startHelp = bbox.corner()[coord] - tmpBBox.corner()[coord];
                dlo.leftOrTop = tmpBBox.corner()[coord];
                if (obj.direction == "east" || obj.direction == "west") {
                    dlo.bottomBorder = 1;
                } else {
                    dlo.rightBorder = 1;
                }
            } else if (bbox.origin()[coord] < tmpBBox.origin()[coord]) {
                dlo.startHelp = tmpBBox.origin()[coord] - bbox.origin()[coord];
                dlo.leftOrTop = bbox[coord];
                if (obj.direction == "east" || obj.direction == "west") {
                    dlo.topBorder = 1;
                } else {
                    dlo.leftBorder = 1;
                }
            }

            // Distinguish the direction because the command to create the lines are
            // different.
            if (coord == 'x') {
                createDistanceLineNorthOrSouth(dlo, context);
            } else {
                createDistanceLineEastOrWest(dlo, context);
            }
        }

        /*
         * Creates the concrete distanceline for north and south and adds a the dotted
         * helpline if it's necessary. dlo is created in createDistanceLine().
         */
        function createDistanceLineNorthOrSouth(dlo, context) {
            // This is the dotted help div that is shown to show to whiche element is
            // displayed
            if (dlo.startHelp) {
                $('<div>').css({
                    top: dlo.startPoint * paperScale.sy,
                    height: dlo.distance * paperScale.sy,
                    width: dlo.startHelp,
                    left: dlo.leftOrTop * paperScale.sx,
                    position: 'absolute',
                    'border-left': dlo.leftBorder + 'px green dotted',
                    'border-right': dlo.rightBorder + 'px green dotted',
                    'border-top': dlo.topBorder + 'px green dotted',
                    'border-bottom': dlo.bottomBorder + 'px green dotted'
                }).appendTo(context.el);
            }

            // Shows the distanceline
            $('<div>', {class: 'distancelineY'}).css({
                top: dlo.startPoint * paperScale.sy,
                height: dlo.distance * paperScale.sy,
                left: dlo.center * paperScale.sx,
            }).appendTo(context.el);
        }

        /*
         * Creates the concrete distanceline for east and west and adds a the dotted
         * helpline if it's necessary. dlo is created in createDistanceLine().
         */
        function createDistanceLineEastOrWest(dlo, context) {
            // This is the dotted help div that is shown to show to whiche element is
            // displayed
            if (dlo.startHelp) {
                $('<div>').css({
                    top: dlo.leftOrTop * paperScale.sy,
                    height: dlo.startHelp * paperScale.sy,
                    width: dlo.distance * paperScale.sx,
                    left: dlo.startPoint * paperScale.sx,
                    position: 'absolute',
                    'border-left': dlo.leftBorder + 'px green dotted',
                    'border-right': dlo.rightBorder + 'px green dotted',
                    'border-top': dlo.topBorder + 'px green dotted',
                    'border-bottom': dlo.bottomBorder + 'px green dotted'
                }).appendTo(context.el);
            }

            // Shows the distanceline
            $('<div>', {class: 'distancelineX'}).css({
                left: dlo.startPoint * paperScale.sx,
                width: dlo.distance * paperScale.sx,
                top: dlo.center * paperScale.sy
            }).appendTo(context.el);
        }

        /*
         * Returns the opposite sign. i.g. x turns to y.
         */
        function getOppositeSign(sign) {
            if (sign == 'x') return 'y';
            return 'x';
        }
    },

    /* Remove distancelines */
    removeDistanceLines: function () {
        this.$el.children().remove();
    },

    /* Gets called when a elemend gets removed. */
    deleteElementFromAdjacencyList: function (cell) {
        var cellID = cell.get('id');
        var adjacencyEntry = this.adjacencyList[cellID];
        for (var direction in adjacencyEntry) {
            var opposite = this.getOpposite(direction);
            for (var id in adjacencyEntry[direction]) {
                delete this.adjacencyList[id][opposite][cellID];
            }
        }

        // maybe delete is to slow?
        delete this.adjacencyList[cellID];
    }

});
