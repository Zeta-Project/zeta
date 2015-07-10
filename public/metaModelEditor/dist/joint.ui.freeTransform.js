/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


/*

Copyright (C) 2011 by Yehuda Katz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

// lib/handlebars/browser-prefix.js
var Handlebars = {};

(function(Handlebars, undefined) {
;
// lib/handlebars/base.js

Handlebars.VERSION = "1.0.0";
Handlebars.COMPILER_REVISION = 4;

Handlebars.REVISION_CHANGES = {
  1: '<= 1.0.rc.2', // 1.0.rc.2 is actually rev2 but doesn't report it
  2: '== 1.0.0-rc.3',
  3: '== 1.0.0-rc.4',
  4: '>= 1.0.0'
};

Handlebars.helpers  = {};
Handlebars.partials = {};

var toString = Object.prototype.toString,
    functionType = '[object Function]',
    objectType = '[object Object]';

Handlebars.registerHelper = function(name, fn, inverse) {
  if (toString.call(name) === objectType) {
    if (inverse || fn) { throw new Handlebars.Exception('Arg not supported with multiple helpers'); }
    Handlebars.Utils.extend(this.helpers, name);
  } else {
    if (inverse) { fn.not = inverse; }
    this.helpers[name] = fn;
  }
};

Handlebars.registerPartial = function(name, str) {
  if (toString.call(name) === objectType) {
    Handlebars.Utils.extend(this.partials,  name);
  } else {
    this.partials[name] = str;
  }
};

Handlebars.registerHelper('helperMissing', function(arg) {
  if(arguments.length === 2) {
    return undefined;
  } else {
    throw new Error("Missing helper: '" + arg + "'");
  }
});

Handlebars.registerHelper('blockHelperMissing', function(context, options) {
  var inverse = options.inverse || function() {}, fn = options.fn;

  var type = toString.call(context);

  if(type === functionType) { context = context.call(this); }

  if(context === true) {
    return fn(this);
  } else if(context === false || context == null) {
    return inverse(this);
  } else if(type === "[object Array]") {
    if(context.length > 0) {
      return Handlebars.helpers.each(context, options);
    } else {
      return inverse(this);
    }
  } else {
    return fn(context);
  }
});

Handlebars.K = function() {};

Handlebars.createFrame = Object.create || function(object) {
  Handlebars.K.prototype = object;
  var obj = new Handlebars.K();
  Handlebars.K.prototype = null;
  return obj;
};

Handlebars.logger = {
  DEBUG: 0, INFO: 1, WARN: 2, ERROR: 3, level: 3,

  methodMap: {0: 'debug', 1: 'info', 2: 'warn', 3: 'error'},

  // can be overridden in the host environment
  log: function(level, obj) {
    if (Handlebars.logger.level <= level) {
      var method = Handlebars.logger.methodMap[level];
      if (typeof console !== 'undefined' && console[method]) {
        console[method].call(console, obj);
      }
    }
  }
};

Handlebars.log = function(level, obj) { Handlebars.logger.log(level, obj); };

Handlebars.registerHelper('each', function(context, options) {
  var fn = options.fn, inverse = options.inverse;
  var i = 0, ret = "", data;

  var type = toString.call(context);
  if(type === functionType) { context = context.call(this); }

  if (options.data) {
    data = Handlebars.createFrame(options.data);
  }

  if(context && typeof context === 'object') {
    if(context instanceof Array){
      for(var j = context.length; i<j; i++) {
        if (data) { data.index = i; }
        ret = ret + fn(context[i], { data: data });
      }
    } else {
      for(var key in context) {
        if(context.hasOwnProperty(key)) {
          if(data) { data.key = key; }
          ret = ret + fn(context[key], {data: data});
          i++;
        }
      }
    }
  }

  if(i === 0){
    ret = inverse(this);
  }

  return ret;
});

Handlebars.registerHelper('if', function(conditional, options) {
  var type = toString.call(conditional);
  if(type === functionType) { conditional = conditional.call(this); }

  if(!conditional || Handlebars.Utils.isEmpty(conditional)) {
    return options.inverse(this);
  } else {
    return options.fn(this);
  }
});

Handlebars.registerHelper('unless', function(conditional, options) {
  return Handlebars.helpers['if'].call(this, conditional, {fn: options.inverse, inverse: options.fn});
});

Handlebars.registerHelper('with', function(context, options) {
  var type = toString.call(context);
  if(type === functionType) { context = context.call(this); }

  if (!Handlebars.Utils.isEmpty(context)) return options.fn(context);
});

Handlebars.registerHelper('log', function(context, options) {
  var level = options.data && options.data.level != null ? parseInt(options.data.level, 10) : 1;
  Handlebars.log(level, context);
});
;
// lib/handlebars/utils.js

var errorProps = ['description', 'fileName', 'lineNumber', 'message', 'name', 'number', 'stack'];

Handlebars.Exception = function(message) {
  var tmp = Error.prototype.constructor.apply(this, arguments);

  // Unfortunately errors are not enumerable in Chrome (at least), so `for prop in tmp` doesn't work.
  for (var idx = 0; idx < errorProps.length; idx++) {
    this[errorProps[idx]] = tmp[errorProps[idx]];
  }
};
Handlebars.Exception.prototype = new Error();

// Build out our basic SafeString type
Handlebars.SafeString = function(string) {
  this.string = string;
};
Handlebars.SafeString.prototype.toString = function() {
  return this.string.toString();
};

var escape = {
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;",
  '"': "&quot;",
  "'": "&#x27;",
  "`": "&#x60;"
};

var badChars = /[&<>"'`]/g;
var possible = /[&<>"'`]/;

var escapeChar = function(chr) {
  return escape[chr] || "&amp;";
};

Handlebars.Utils = {
  extend: function(obj, value) {
    for(var key in value) {
      if(value.hasOwnProperty(key)) {
        obj[key] = value[key];
      }
    }
  },

  escapeExpression: function(string) {
    // don't escape SafeStrings, since they're already safe
    if (string instanceof Handlebars.SafeString) {
      return string.toString();
    } else if (string == null || string === false) {
      return "";
    }

    // Force a string conversion as this will be done by the append regardless and
    // the regex test will do this transparently behind the scenes, causing issues if
    // an object's to string has escaped characters in it.
    string = string.toString();

    if(!possible.test(string)) { return string; }
    return string.replace(badChars, escapeChar);
  },

  isEmpty: function(value) {
    if (!value && value !== 0) {
      return true;
    } else if(toString.call(value) === "[object Array]" && value.length === 0) {
      return true;
    } else {
      return false;
    }
  }
};
;
// lib/handlebars/runtime.js

Handlebars.VM = {
  template: function(templateSpec) {
    // Just add water
    var container = {
      escapeExpression: Handlebars.Utils.escapeExpression,
      invokePartial: Handlebars.VM.invokePartial,
      programs: [],
      program: function(i, fn, data) {
        var programWrapper = this.programs[i];
        if(data) {
          programWrapper = Handlebars.VM.program(i, fn, data);
        } else if (!programWrapper) {
          programWrapper = this.programs[i] = Handlebars.VM.program(i, fn);
        }
        return programWrapper;
      },
      merge: function(param, common) {
        var ret = param || common;

        if (param && common) {
          ret = {};
          Handlebars.Utils.extend(ret, common);
          Handlebars.Utils.extend(ret, param);
        }
        return ret;
      },
      programWithDepth: Handlebars.VM.programWithDepth,
      noop: Handlebars.VM.noop,
      compilerInfo: null
    };

    return function(context, options) {
      options = options || {};
      var result = templateSpec.call(container, Handlebars, context, options.helpers, options.partials, options.data);

      var compilerInfo = container.compilerInfo || [],
          compilerRevision = compilerInfo[0] || 1,
          currentRevision = Handlebars.COMPILER_REVISION;

      if (compilerRevision !== currentRevision) {
        if (compilerRevision < currentRevision) {
          var runtimeVersions = Handlebars.REVISION_CHANGES[currentRevision],
              compilerVersions = Handlebars.REVISION_CHANGES[compilerRevision];
          throw "Template was precompiled with an older version of Handlebars than the current runtime. "+
                "Please update your precompiler to a newer version ("+runtimeVersions+") or downgrade your runtime to an older version ("+compilerVersions+").";
        } else {
          // Use the embedded version info since the runtime doesn't know about this revision yet
          throw "Template was precompiled with a newer version of Handlebars than the current runtime. "+
                "Please update your runtime to a newer version ("+compilerInfo[1]+").";
        }
      }

      return result;
    };
  },

  programWithDepth: function(i, fn, data /*, $depth */) {
    var args = Array.prototype.slice.call(arguments, 3);

    var program = function(context, options) {
      options = options || {};

      return fn.apply(this, [context, options.data || data].concat(args));
    };
    program.program = i;
    program.depth = args.length;
    return program;
  },
  program: function(i, fn, data) {
    var program = function(context, options) {
      options = options || {};

      return fn(context, options.data || data);
    };
    program.program = i;
    program.depth = 0;
    return program;
  },
  noop: function() { return ""; },
  invokePartial: function(partial, name, context, helpers, partials, data) {
    var options = { helpers: helpers, partials: partials, data: data };

    if(partial === undefined) {
      throw new Handlebars.Exception("The partial " + name + " could not be found");
    } else if(partial instanceof Function) {
      return partial(context, options);
    } else if (!Handlebars.compile) {
      throw new Handlebars.Exception("The partial " + name + " could not be compiled when running in runtime-only mode");
    } else {
      partials[name] = Handlebars.compile(partial, {data: data !== undefined});
      return partials[name](context, options);
    }
  }
};

Handlebars.template = Handlebars.VM.template;
;
// lib/handlebars/browser-suffix.js
})(Handlebars);
;

this["joint"] = this["joint"] || {};
this["joint"]["templates"] = this["joint"]["templates"] || {};

this["joint"]["templates"]["freetransform.html"] = Handlebars.template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"resize\" data-position=\"top-left\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"top\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"top-right\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"right\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"bottom-right\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"bottom\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"bottom-left\" draggable=\"false\"/>\n<div class=\"resize\" data-position=\"left\" draggable=\"false\"/>\n<div class=\"rotate\" draggable=\"false\"/>\n\n";
  });
joint.ui.FreeTransform = Backbone.View.extend({

    className: 'free-transform',

    template: 'freetransform',

    events: {
	'mousedown .resize': 'startResizing',
        'mousedown .rotate': 'startRotating',
        'touchstart .resize': 'startResizing',
        'touchstart .rotate': 'startRotating'
    },

    options: {
	
	directions: ['nw','n','ne','e','se','s','sw','w']
    },

    initialize: function() {

        _.bindAll(this, 'update', 'remove', 'pointerup', 'pointermove');

        // inform an existing halo that a new halo is being created
        this.options.paper.trigger('freetransform:create');

	// Register mouse events.
        $(document.body).on('mousemove touchmove', this.pointermove);
        $(document).on('mouseup touchend', this.pointerup);

	// Update the freeTransform when the graph is changed.
        this.listenTo(this.options.graph, 'all', this.update);

	// Remove the freeTransform when the model is removed.
        this.listenTo(this.options.graph, 'reset', this.remove);
        this.listenTo(this.options.cell, 'remove', this.remove);

        // Hide the freeTransform when the user clicks anywhere in the paper or a new freeTransform is created.
        this.listenTo(this.options.paper, 'blank:pointerdown freetransform:create', this.remove);
        this.listenTo(this.options.paper, 'scale', this.update);

        this.options.paper.$el.append(this.el);
    },

    render: function() {

	this.$el.html(joint.templates['freetransform.html'](this.template));

	// We have to use `attr` as jQuery `data` doesn't update DOM
	this.$el.attr('data-type', this.options.cell.get('type'));

	this.update();
    },

    update: function() {

	var viewportCTM = this.options.paper.viewport.getCTM();

	var bbox = this.options.cell.getBBox();

	// Calculate the free transform size and position in viewport coordinate system.
	// TODO: take a viewport rotation in account.
	bbox.x *= viewportCTM.a;
	bbox.x += viewportCTM.e;
	bbox.y *= viewportCTM.d;
	bbox.y += viewportCTM.f;
	bbox.width *= viewportCTM.a;
	bbox.height *= viewportCTM.d;

	var angle = g.normalizeAngle(this.options.cell.get('angle') || 0);

	var transformVal =  'rotate(' + angle + 'deg)';

	this.$el.css({
            'width': bbox.width + 4,
	    'height': bbox.height + 4,
	    'left': bbox.x - 3,
	    'top': bbox.y - 3,
	    'transform': transformVal,
	    '-webkit-transform': transformVal, // chrome + safari
 	    '-ms-transform': transformVal // IE 9
        });

	// Update the directions on the halo divs while the element being rotated. The directions are represented
	// by cardinal points (N,S,E,W). For example the div originally pointed to north needs to be changed
	// to point to south if the element was rotated by 180 degrees.
	var shift = Math.floor(angle * (this.options.directions.length / 360));

	if (shift != this._previousDirectionsShift) {

	    // Create the current directions array based on the calculated shift.
	    var directions = _.rest(this.options.directions, shift).concat(_.first(this.options.directions, shift));

	    // Apply the array on the halo divs.
	    this.$('.resize').removeClass('nw n ne e se s sw w').each(function(index, el) {
		$(el).addClass(directions[index]);
	    });

	    this._previousDirectionsShift = shift;
	}
    },

    startResizing: function(evt) {

        evt.stopPropagation();

	this.options.graph.trigger('batch:start');

	// Target's data attribute can contain one of 8 positions. Each position defines the way how to
	// resize an element. Whether to change the size on x-axis, on y-axis or on both.

	var direction = $(evt.target).data('position');

	var rx = 0, ry = 0;

	_.each(direction.split('-'), function(singleDirection) {

	    rx = { 'left': -1, 'right': 1 }[singleDirection] || rx;
	    ry = { 'top': -1, 'bottom': 1 }[singleDirection] || ry;
	});

	// The direction has to be one of the 4 directions the element's resize method would accept (TL,BR,BL,TR).
	direction = {
	    'top': 'top-left',
	    'bottom': 'bottom-right',
	    'left' : 'bottom-left',
	    'right': 'top-right'
	}[direction] || direction;

	// The selector holds a function name to pick a corner point on a rectangle.
	// See object `rect` in `src/geometry.js`.
	var selector = {
	    'top-right' : 'bottomLeft',
	    'top-left': 'corner',
	    'bottom-left': 'topRight',
	    'bottom-right': 'origin'
	}[direction];

	// Expose the initial setup, so `pointermove` method can access it.
	this._initial = {
	    angle: g.normalizeAngle(this.options.cell.get('angle') || 0),
	    resizeX: rx, // to resize, not to resize or flip coordinates on x-axis (1,0,-1)
	    resizeY: ry, // to resize, not to resize or flip coordinates on y-axis (1,0,-1)
	    selector: selector,
	    direction: direction
	};

	this._action = 'resize';

	this.startOp(evt.target);
    },

    startRotating: function(evt) {

        evt.stopPropagation();

	this.options.graph.trigger('batch:start');
        
	var center = this.options.cell.getBBox().center();

	var clientCoords = this.options.paper.snapToGrid({ x: evt.clientX, y: evt.clientY }); 

	// Expose the initial setup, so `pointermove` method can acess it.
	this._initial = {
	    // the centre of the element is the centre of the rotation
	    centerRotation: center,
	    // an angle of the element before the rotating starts
	    modelAngle: g.normalizeAngle(this.options.cell.get('angle') || 0),
	    // an angle between the line starting at mouse coordinates, ending at the center of rotation
	    // and y-axis 
	    startAngle: g.point(clientCoords).theta(center)
	};

        this._action = 'rotate';

	this.startOp(evt.target);
    },

    pointermove: function(evt) {

	if (!this._action) return;

        evt = joint.util.normalizeEvent(evt);

	var clientCoords = this.options.paper.snapToGrid({ x: evt.clientX, y: evt.clientY });
	var gridSize = this.options.paper.options.gridSize;

	var model = this.options.cell, i = this._initial;

	switch (this._action) {

	case 'resize':

	    var currentRect = model.getBBox();

	    // The requested element's size has to be find on the unrotated element. Therefore we
	    // are rotating a mouse coordinates back (coimageCoords) by an angle the element is rotated by and
	    // with the center of rotation equals to the center of the unrotated element.
	    var coimageCoords= g.point(clientCoords).rotate(currentRect.center(), i.angle);

	    // The requested size is the difference between the fixed point and coimaged coordinates.
	    var requestedSize = coimageCoords.difference(currentRect[i.selector]());

	    // Calculate the new dimensions. `resizeX`/`resizeY` can hold a zero value if the resizing
	    // on x-axis/y-axis is not allowed.
	    var width = i.resizeX ? requestedSize.x * i.resizeX : currentRect.width;
	    var height = i.resizeY ? requestedSize.y * i.resizeY : currentRect.height;

	    // Constraint the dimensions.
	    width = width < gridSize ? gridSize : g.snapToGrid(width, gridSize);
	    height = height < gridSize ? gridSize : g.snapToGrid(height, gridSize);

	    // Resize the element only if the dimensions are changed.
	    if (currentRect.width != width || currentRect.height != height) {

		model.resize(width, height, { direction: i.direction });
	    }

	    break;

        case 'rotate':

	    // Calculate an angle between the line starting at mouse coordinates, ending at the centre
	    // of rotation and y-axis and deduct the angle from the start of rotation.
	    var theta = i.startAngle - g.point(clientCoords).theta(i.centerRotation);

            model.rotate(g.snapToGrid(i.modelAngle + theta, 15), true);

            break;
	}
    },

    pointerup: function(evt) {

	if (!this._action) return;

	this.stopOp();

	this.options.graph.trigger('batch:stop');

	delete this._action;
	delete this._initial;
    },

    remove: function(evt) {

	Backbone.View.prototype.remove.apply(this, arguments);

        $('body').off('mousemove touchmove', this.pointermove);
        $(document).off('mouseup touchend', this.pointerup);
    },

    startOp: function(el) {

	if (el) {
	    // Add a class to the element we are operating with
	    $(el).addClass('in-operation');
	    this._elementOp = el;
	}

	this.$el.addClass('in-operation');
    },

    stopOp: function() {

	if (this._elementOp) {
	    // Remove a class from the element we were operating with
	    $(this._elementOp).removeClass('in-operation');
	    delete this._elementOp;
	}

	this.$el.removeClass('in-operation');
    }
});