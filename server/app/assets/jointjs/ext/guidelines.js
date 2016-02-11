/*
 * This View managed the guidelines. The guidelines give the opportunity
 * to align the elements more accurately.
 * @author Maximilian Göke
 */
guidelines = Backbone.View.extend({

  options: {
    paper: void 0,
    distance: 5,
    active: true
  },

  className: 'guidelines',

  /* Constructor. Set up guidelines. */
  initialize: function() {
    this.$guidelineX = $('<div>',  { id: 'guidelineX' }).css({'height': this.options.paper.options.height}).appendTo(this.el);
    this.$guidelineY = $('<div>',  { id: 'guidelineY' }).css({'width': this.options.paper.options.width}).appendTo(this.el);
    this.$el.appendTo(this.options.paper.el);
    this.$guidelineX.hide();
    this.$guidelineY.hide();
    this.registerListener();
    this.initializeGlobals();
  },

  initializeGlobals: function() {
    this.lastCoords = {x: {}, y:{}};
    this.firstTranslate = {x: true, y: true};
    this._client = {x: 0, y: 0};
  },

  /* Registers listener. */
  registerListener: function() {
    this.listenTo(this.options.paper, 'cell:pointermove', this.guide);
    this.listenTo(this.options.paper, 'cell:pointerup', this.end);
  },

  /* Unregisters listener. */
  unregisterListener: function() { this.stopListening(); },

  start: function() {
    this.options.active = true;
    this.registerListener();
  },

  stop: function() {
    this.options.active = false;
    this.unregisterListener();
  },

  /*
   * Gets called when a element move. Check if element is near another element.
   */
  guide: function(cellView, evt) {
    if(!(cellView instanceof joint.dia.LinkView)) {
      // it's not a link
      var model = cellView.model;
      var position = model.get('position');
      var size = model.get('size');
      var bbox = model.getBBox().bbox(model.get('angle'));
      var coordinates = this.calculateCoordinate(bbox);
      var distance = this.options.distance;
      var x = null , y = null;

      this.options.paper.model.getElements().every( function(element) {
        if(element === model) return true;
        var tmpCoordinates = this.calculateCoordinate(element.getBBox().bbox(element.get('angle')));
        if(x === null) x = this.checkCoordinates(coordinates.x, tmpCoordinates.x, model.get('id'), element);
        if(y === null) y = this.checkCoordinates(coordinates.y, tmpCoordinates.y, model.get('id'), element);
        if(x !== null && y !== null) return false;
        return true;
      }, this);

      // save bbox
      var oldBBox = g.rect(_.extend({}, bbox));


      x !== null ? bbox.x = x.coords - (x.type / 2.0) * bbox.width : x = { coords: null};
      y !== null ? bbox.y = y.coords - (y.type / 2.0) * bbox.height : y = { coords: null};

      bbox.x = fixGlueBug(bbox.x, oldBBox.x, 'x', x, this);
      bbox.y = fixGlueBug(bbox.y, oldBBox.y, 'y', y, this);

      // Fixed Bug guideline an element not on one line
      // This is needed if the guideline is a number that you can't reach in the
      // current grid size.
      var center = bbox.center();
      var left = center.x - size.width / 2;
      var top = center.y - size.height / 2;
      model.translate(left - position.x, top - position.y);

      // Fixed Bug Zoom
      // current transformation matrix

      this.show(x.coords, y.coords);
    }

    /*
     * Workaround that the element don't glue.
     * bbox: bbox.x || bbox.y expected
     * oldBBox: oldBBox.x || oldBBox.y
     * coord: 'x' || 'y'
     */
    function fixGlueBug(bbox, oldBBox, coord, coordValue, context) {
      // setup distance
      var distance = context.options.distance;

      // calculate distance if the same guideline is found multiple times in a row
      if(_.isEqual(context.lastCoords[coord], coordValue) && coordValue.type !== undefined) {
        distance = (coord == 'x' ? evt.clientX :evt.clientY) - context._client[coord];
      }

      // initial lasstCoord
      if(coordValue.type !== undefined && _.isEmpty(context.lastCoords[coord])) {
        context.lastCoords[coord] = coordValue;
        context._client[coord] = coord == 'x' ?  evt.clientX : evt.clientY;
      }

      // reset lastCoords if current position of active element is greater than
      // the distance defined in the options
      if(Math.abs(context.lastCoords[coord].coords - coordValue.coords) > context.options.distance) {
        context.lastCoords[coord] = {};
        context.firstTranslate[coord] = true;
      }

      // reset firstTranslate. Important if elements gets dragged to the left or top
      if(Math.abs(bbox - oldBBox) <= context.options.distance && !context.firstTranslate[coord]) {context.firstTranslate[coord] = true;}

      if(Math.abs(distance) > context.options.distance) {
        // ugly: don't show guideline
        coordValue.coords = null;

        context.hide();
        if(distance < 0 && context.firstTranslate[coord]) {
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
   * Calculates the coordinates of an element.
   * Returns an Object. Structure of the object:
   * {
   *   x: [left, middle, right],
   *   y: [top, center, bottom]
   * }
   */
  calculateCoordinate: function(bbox) {
    var result = {x:[],y:[]};
    createResultObject(bbox.origin(), result);
    createResultObject(bbox.center(), result);
    createResultObject(bbox.corner(), result);
    return result;

    // saves the values of obj into the right list
    function createResultObject(obj, result) {
      result.x.push(obj.x);
      result.y.push(obj.y);
    }
  },

  /*
   * Check if coordinates of the elements are matching.
   * Structure of input(values, tmpValues):
   * [left/top, middle/center, right/bottom]
   */
  checkCoordinates: function(values, tmpValues, cellID, cell) {
    var result = null;

    if(Math.abs(values[1]- tmpValues[1]) <= this.options.distance) {
      result = { coords: tmpValues[1], type: 1};
    } else if(Math.abs(values[0]- tmpValues[2]) <= this.options.distance) {
      result = { coords: tmpValues[2], type: 0};
    } else if(Math.abs(values[0]- tmpValues[0]) <= this.options.distance) {
      result = { coords: tmpValues[0], type: 0};
    } else if(Math.abs(values[2]- tmpValues[0]) <= this.options.distance) {
      result = { coords: tmpValues[0], type: 2};
    } else if(Math.abs(values[2]- tmpValues[2]) <= this.options.distance) {
      result = { coords: tmpValues[2], type: 2};
    }

    return result;
  },

  /* Hides the guidelines. */
  hide: function() {
    this.$guidelineX.hide();
    this.$guidelineY.hide();
  },

  /* Shows the guidelines */
  show: function(x,y) {
    var paperScale = V(this.options.paper.viewport).scale();
    x !== null && x !== 0 ? this.$guidelineX.show().css({'left': x *paperScale.sx, 'height': this.options.paper.options.height *paperScale.sx}) : this.$guidelineX.hide();
    y !== null && y !== 0 ? this.$guidelineY.show().css({'top': y *paperScale.sy, 'width': this.options.paper.options.width *paperScale.sy}) : this.$guidelineY.hide();
  },

  end: function() {
    this.hide();
    this.initializeGlobals();
  }
});
