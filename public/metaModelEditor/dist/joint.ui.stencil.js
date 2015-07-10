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

(function (Handlebars, undefined) {
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

    Handlebars.helpers = {};
    Handlebars.partials = {};

    var toString = Object.prototype.toString,
        functionType = '[object Function]',
        objectType = '[object Object]';

    Handlebars.registerHelper = function (name, fn, inverse) {
        if (toString.call(name) === objectType) {
            if (inverse || fn) {
                throw new Handlebars.Exception('Arg not supported with multiple helpers');
            }
            Handlebars.Utils.extend(this.helpers, name);
        } else {
            if (inverse) {
                fn.not = inverse;
            }
            this.helpers[name] = fn;
        }
    };

    Handlebars.registerPartial = function (name, str) {
        if (toString.call(name) === objectType) {
            Handlebars.Utils.extend(this.partials, name);
        } else {
            this.partials[name] = str;
        }
    };

    Handlebars.registerHelper('helperMissing', function (arg) {
        if (arguments.length === 2) {
            return undefined;
        } else {
            throw new Error("Missing helper: '" + arg + "'");
        }
    });

    Handlebars.registerHelper('blockHelperMissing', function (context, options) {
        var inverse = options.inverse || function () {
            }, fn = options.fn;

        var type = toString.call(context);

        if (type === functionType) {
            context = context.call(this);
        }

        if (context === true) {
            return fn(this);
        } else if (context === false || context == null) {
            return inverse(this);
        } else if (type === "[object Array]") {
            if (context.length > 0) {
                return Handlebars.helpers.each(context, options);
            } else {
                return inverse(this);
            }
        } else {
            return fn(context);
        }
    });

    Handlebars.K = function () {
    };

    Handlebars.createFrame = Object.create || function (object) {
            Handlebars.K.prototype = object;
            var obj = new Handlebars.K();
            Handlebars.K.prototype = null;
            return obj;
        };

    Handlebars.logger = {
        DEBUG: 0, INFO: 1, WARN: 2, ERROR: 3, level: 3,

        methodMap: {0: 'debug', 1: 'info', 2: 'warn', 3: 'error'},

        // can be overridden in the host environment
        log: function (level, obj) {
            if (Handlebars.logger.level <= level) {
                var method = Handlebars.logger.methodMap[level];
                if (typeof console !== 'undefined' && console[method]) {
                    console[method].call(console, obj);
                }
            }
        }
    };

    Handlebars.log = function (level, obj) {
        Handlebars.logger.log(level, obj);
    };

    Handlebars.registerHelper('each', function (context, options) {
        var fn = options.fn, inverse = options.inverse;
        var i = 0, ret = "", data;

        var type = toString.call(context);
        if (type === functionType) {
            context = context.call(this);
        }

        if (options.data) {
            data = Handlebars.createFrame(options.data);
        }

        if (context && typeof context === 'object') {
            if (context instanceof Array) {
                for (var j = context.length; i < j; i++) {
                    if (data) {
                        data.index = i;
                    }
                    ret = ret + fn(context[i], {data: data});
                }
            } else {
                for (var key in context) {
                    if (context.hasOwnProperty(key)) {
                        if (data) {
                            data.key = key;
                        }
                        ret = ret + fn(context[key], {data: data});
                        i++;
                    }
                }
            }
        }

        if (i === 0) {
            ret = inverse(this);
        }

        return ret;
    });

    Handlebars.registerHelper('if', function (conditional, options) {
        var type = toString.call(conditional);
        if (type === functionType) {
            conditional = conditional.call(this);
        }

        if (!conditional || Handlebars.Utils.isEmpty(conditional)) {
            return options.inverse(this);
        } else {
            return options.fn(this);
        }
    });

    Handlebars.registerHelper('unless', function (conditional, options) {
        return Handlebars.helpers['if'].call(this, conditional, {fn: options.inverse, inverse: options.fn});
    });

    Handlebars.registerHelper('with', function (context, options) {
        var type = toString.call(context);
        if (type === functionType) {
            context = context.call(this);
        }

        if (!Handlebars.Utils.isEmpty(context)) return options.fn(context);
    });

    Handlebars.registerHelper('log', function (context, options) {
        var level = options.data && options.data.level != null ? parseInt(options.data.level, 10) : 1;
        Handlebars.log(level, context);
    });
    ;
// lib/handlebars/utils.js

    var errorProps = ['description', 'fileName', 'lineNumber', 'message', 'name', 'number', 'stack'];

    Handlebars.Exception = function (message) {
        var tmp = Error.prototype.constructor.apply(this, arguments);

        // Unfortunately errors are not enumerable in Chrome (at least), so `for prop in tmp` doesn't work.
        for (var idx = 0; idx < errorProps.length; idx++) {
            this[errorProps[idx]] = tmp[errorProps[idx]];
        }
    };
    Handlebars.Exception.prototype = new Error();

// Build out our basic SafeString type
    Handlebars.SafeString = function (string) {
        this.string = string;
    };
    Handlebars.SafeString.prototype.toString = function () {
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

    var escapeChar = function (chr) {
        return escape[chr] || "&amp;";
    };

    Handlebars.Utils = {
        extend: function (obj, value) {
            for (var key in value) {
                if (value.hasOwnProperty(key)) {
                    obj[key] = value[key];
                }
            }
        },

        escapeExpression: function (string) {
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

            if (!possible.test(string)) {
                return string;
            }
            return string.replace(badChars, escapeChar);
        },

        isEmpty: function (value) {
            if (!value && value !== 0) {
                return true;
            } else if (toString.call(value) === "[object Array]" && value.length === 0) {
                return true;
            } else {
                return false;
            }
        }
    };
    ;
// lib/handlebars/runtime.js

    Handlebars.VM = {
        template: function (templateSpec) {
            // Just add water
            var container = {
                escapeExpression: Handlebars.Utils.escapeExpression,
                invokePartial: Handlebars.VM.invokePartial,
                programs: [],
                program: function (i, fn, data) {
                    var programWrapper = this.programs[i];
                    if (data) {
                        programWrapper = Handlebars.VM.program(i, fn, data);
                    } else if (!programWrapper) {
                        programWrapper = this.programs[i] = Handlebars.VM.program(i, fn);
                    }
                    return programWrapper;
                },
                merge: function (param, common) {
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

            return function (context, options) {
                options = options || {};
                var result = templateSpec.call(container, Handlebars, context, options.helpers, options.partials, options.data);

                var compilerInfo = container.compilerInfo || [],
                    compilerRevision = compilerInfo[0] || 1,
                    currentRevision = Handlebars.COMPILER_REVISION;

                if (compilerRevision !== currentRevision) {
                    if (compilerRevision < currentRevision) {
                        var runtimeVersions = Handlebars.REVISION_CHANGES[currentRevision],
                            compilerVersions = Handlebars.REVISION_CHANGES[compilerRevision];
                        throw "Template was precompiled with an older version of Handlebars than the current runtime. " +
                        "Please update your precompiler to a newer version (" + runtimeVersions + ") or downgrade your runtime to an older version (" + compilerVersions + ").";
                    } else {
                        // Use the embedded version info since the runtime doesn't know about this revision yet
                        throw "Template was precompiled with a newer version of Handlebars than the current runtime. " +
                        "Please update your runtime to a newer version (" + compilerInfo[1] + ").";
                    }
                }

                return result;
            };
        },

        programWithDepth: function (i, fn, data /*, $depth */) {
            var args = Array.prototype.slice.call(arguments, 3);

            var program = function (context, options) {
                options = options || {};

                return fn.apply(this, [context, options.data || data].concat(args));
            };
            program.program = i;
            program.depth = args.length;
            return program;
        },
        program: function (i, fn, data) {
            var program = function (context, options) {
                options = options || {};

                return fn(context, options.data || data);
            };
            program.program = i;
            program.depth = 0;
            return program;
        },
        noop: function () {
            return "";
        },
        invokePartial: function (partial, name, context, helpers, partials, data) {
            var options = {helpers: helpers, partials: partials, data: data};

            if (partial === undefined) {
                throw new Handlebars.Exception("The partial " + name + " could not be found");
            } else if (partial instanceof Function) {
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
this["joint"]["templates"]["stencil"] = this["joint"]["templates"]["stencil"] || {};

this["joint"]["templates"]["stencil"]["elements.html"] = Handlebars.template(function (Handlebars, depth0, helpers, partials, data) {
    this.compilerInfo = [4, '>= 1.0.0'];
    helpers = this.merge(helpers, Handlebars.helpers);
    data = data || {};


    return "<div class=\"elements\"></div>\n";
});

this["joint"]["templates"]["stencil"]["group.html"] = Handlebars.template(function (Handlebars, depth0, helpers, partials, data) {
    this.compilerInfo = [4, '>= 1.0.0'];
    helpers = this.merge(helpers, Handlebars.helpers);
    data = data || {};
    var buffer = "", stack1, functionType = "function", escapeExpression = this.escapeExpression;


    buffer += "<div class=\"group panel panel-default\">\n    <div class=\"group-label panel-heading\"><strong>";
    if (stack1 = helpers.label) {
        stack1 = stack1.call(depth0, {hash: {}, data: data});
    }
    else {
        stack1 = depth0.label;
        stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1;
    }
    buffer += escapeExpression(stack1)
        + "</strong></div>\n</div>\n";
    return buffer;
});

this["joint"]["templates"]["stencil"]["stencil.html"] = Handlebars.template(function (Handlebars, depth0, helpers, partials, data) {
    this.compilerInfo = [4, '>= 1.0.0'];
    helpers = this.merge(helpers, Handlebars.helpers);
    data = data || {};


    return "<div class=\"stencil-paper-drag\"></div>\n";
});
// JointJS Stencil ui plugin.
// --------------------------

// USAGE:
// var graph = new joint.dia.Graph;
// var paper = new joint.dia.Paper({
//    el: $('#paper'),
//    width: 500,
//    height: 300,
//    gridSize: 20,
//    perpendicularLinks: true,
//    model: graph
// });
// 
// var stencil = new joint.ui.Stencil({ graph: graph, paper: paper });
// $('#stencil-holder').append(stencil.render().el);


joint.ui.Stencil = Backbone.View.extend({

    className: 'stencil panel-body',

    events: {

        'click .group-label': 'onGroupLabelClick',
        'touchstart .group-label': 'onGroupLabelClick'
    },

    options: {
        width: 200,
        height: 800
    },

    initialize: function () {

        this.graphs = {};
        this.papers = {};

        $(document.body).on('mousemove touchmove', _.bind(this.onDrag, this));
        $(document.body).on('mouseup touchend', _.bind(this.onDragEnd, this));
    },

    render: function () {

        this.$el.html(joint.templates.stencil['stencil.html'](this.template));

        var paperOptions = {
            width: this.options.width,
            height: this.options.height,
            interactive: false
        };

        if (this.options.groups) {
            // Render as many papers as there are groups and put them inside the `'group.html'` template.

            var sortedGroups = _.sortBy(_.pairs(this.options.groups), function (pair) {
                return pair[1].index
            });
            _.each(sortedGroups, function (groupArray) {

                var name = groupArray[0];
                var group = groupArray[1];

                var $group = $(joint.templates.stencil['group.html']({label: group.label || name}));
                $group.attr('data-name', name);
                if (group.closed) $group.addClass('closed');
                $group.append($(joint.templates.stencil['elements.html']()));
                this.$el.append($group);

                var graph = new joint.dia.Graph;
                this.graphs[name] = graph;
                var paper = new joint.dia.Paper(_.extend({}, paperOptions, {
                    el: $group.find('.elements'),
                    model: graph,
                    width: group.width || paperOptions.width,
                    height: group.height || paperOptions.height
                }));
                this.papers[name] = paper;

            }, this);

        } else {
            // Groups are not used. Render just one paper for the whole stencil.

            this.$el.append($(joint.templates.stencil['elements.html']()));
            var graph = new joint.dia.Graph;
            // `this.graphs` object contains only one graph in this case that we store under the key `'__default__'`.
            this.graphs['__default__'] = graph;
            var paper = new joint.dia.Paper(_.extend(paperOptions, {
                el: this.$('.elements'),
                model: graph
            }));
            this.papers['__default__'] = paper;
        }

        // Create graph and paper objects for the, temporary, dragging phase.
        // Elements travel the following way when the user drags an element from the stencil and drops
        // it into the main, associated, paper: `[One of the Stencil graphs] -> [_graphDrag] -> [this.options.graph]`.
        this._graphDrag = new joint.dia.Graph;
        this._paperDrag = new joint.dia.Paper({

            el: this.$('.stencil-paper-drag'),
            width: 1,
            height: 1,
            model: this._graphDrag
        });

        // `cell:pointerdown` on any of the Stencil papers triggers element dragging.
        _.each(this.papers, function (paper) {
            paper.on('cell:pointerdown', this.onDragStart, this);
        }, this);

        return this;
    },

    // @public Populate stencil with `cells`. If `group` is passed, only the graph in the named group
    // will be populated.
    load: function (cells, group) {

        var graph = this.graphs[group || '__default__'];
        if (graph) {
            graph.resetCells(cells);
            // If height is not defined in neither the global `options.height` or local
            // `height` for this specific group, fit the paper to the content automatically.
            var paperHeight = this.options.height;
            if (group && this.options.groups[group]) {
                paperHeight = this.options.groups[group].height;
            }
            if (!paperHeight) {
                this.papers[group || '__default__'].fitToContent(1, 1, this.options.paperPadding || 10);
            }
        } else {
            throw new Error('Stencil: group ' + group + ' does not exist.');
        }
    },

    getGraph: function (group) {

        return this.graphs[group || '__default__'];
    },

    getPaper: function (group) {

        return this.papers[group || '__default__'];
    },

    onDragStart: function (cellView, evt) {

        this.$el.addClass('dragging');
        this._paperDrag.$el.addClass('dragging');
        // Move the .stencil-paper-drag element to the document body so that even though
        // the stencil is set to overflow: hidden or auto, the .stencil-paper-drag will
        // be visible.
        $(document.body).append(this._paperDrag.$el);

        this._clone = cellView.model.clone();
        this._cloneBbox = cellView.getBBox();

        // Leave some padding so that e.g. the cell shadow or thick border is visible.
        // This workaround can be removed once browsers start supporting getStrokeBBox() (http://www.w3.org/TR/SVG2/types.html#__svg__SVGGraphicsElement__getStrokeBBox).
        var padding = 5;

        // Compute the difference between the real (view) bounding box and the model bounding box position.
        // This makes sure that elements that are outside the model bounding box get accounted for too.
        var shift = g.point(this._cloneBbox.x - this._clone.get('position').x, this._cloneBbox.y - this._clone.get('position').y);

        this._clone.set('position', {x: -shift.x + padding, y: -shift.y + padding});
        this._graphDrag.addCell(this._clone);
        this._paperDrag.setDimensions(this._cloneBbox.width + 2 * padding, this._cloneBbox.height + 2 * padding);

        // Safari uses `document.body.scrollTop` only while Firefox uses `document.documentElement.scrollTop` only.
        // Google Chrome is the winner here as it uses both.
        var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;

        // Offset the paper so that the mouse cursor points to the center of the stencil element.
        this._paperDrag.$el.offset({
            left: evt.clientX - this._cloneBbox.width / 2,
            top: evt.clientY + scrollTop - this._cloneBbox.height / 2
        });
    },

    onDrag: function (evt) {

        evt = joint.util.normalizeEvent(evt);

        if (this._clone) {

            var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;

            // Offset the paper so that the mouse cursor points to the center of the stencil element.
            this._paperDrag.$el.offset({
                left: evt.clientX - this._cloneBbox.width / 2,
                top: evt.clientY + scrollTop - this._cloneBbox.height / 2
            });
        }
    },

    onDragEnd: function (evt) {

        evt = joint.util.normalizeEvent(evt);

        if (this._clone && this._cloneBbox) {

            this.drop(evt, this._clone.clone(), this._cloneBbox);

            // Move the .stencil-paper-drag from the document body back to the stencil element.
            this.$el.append(this._paperDrag.$el);

            this.$el.removeClass('dragging');
            this._paperDrag.$el.removeClass('dragging');

            this._clone.remove();
            this._clone = undefined;
        }
    },

    drop: function (evt, cell, cellViewBBox) {

        var paper = this.options.paper;
        var graph = this.options.graph;

        var paperPosition = paper.$el.offset();
        var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
        var scrollLeft = document.body.scrollLeft || document.documentElement.scrollLeft;

        var paperArea = g.rect(
            paperPosition.left + parseInt(paper.$el.css("border-left-width"), 10) - scrollLeft,
            paperPosition.top + parseInt(paper.$el.css("border-top-width"), 10) - scrollTop,
            paper.$el.innerWidth(),
            paper.$el.innerHeight()
        );

        var p = paper.svg.createSVGPoint();
        p.x = evt.clientX;
        p.y = evt.clientY;

        // Check if the cell is dropped inside the paper.
        if (paperArea.containsPoint(p)) {

            // This is a hack for Firefox! If there wasn't a fake (non-visible) rectangle covering the
            // whole SVG area, `$(paper.svg).offset()` used below won't work.
            var fakeRect = V('rect', {
                width: paper.options.width,
                height: paper.options.height,
                x: 0,
                y: 0,
                opacity: 0
            });
            V(paper.svg).prepend(fakeRect);

            var paperOffset = $(paper.svg).offset();

            // Clean up the fake rectangle once we have the offset of the SVG document.
            fakeRect.remove();

            p.x += scrollLeft - paperOffset.left;
            p.y += scrollTop - paperOffset.top;

            // Transform point into the viewport coordinate system.
            var pointTransformed = p.matrixTransform(paper.viewport.getCTM().inverse());

            var cellBBox = cell.getBBox();
            pointTransformed.x += cellBBox.x - cellViewBBox.width / 2;
            pointTransformed.y += cellBBox.y - cellViewBBox.height / 2;

            cell.set('position', {
                x: g.snapToGrid(pointTransformed.x, paper.options.gridSize),
                y: g.snapToGrid(pointTransformed.y, paper.options.gridSize)
            });

            // `z` level will be set automatically in the `this.graph.addCell()` method.
            // We don't want the cell to have the same `z` level as it had in the temporary paper.
            cell.unset('z');

            graph.addCell(cell);
        }
    },

    onGroupLabelClick: function (evt) {

        // Prevent default action for iPad not to handle this event twice.
        evt.preventDefault();

        var $group = $(evt.target).closest('.group');
        this.toggleGroup($group.data('name'));
    },

    toggleGroup: function (name) {

        this.$('.group[data-name="' + name + '"]').toggleClass('closed');
    },

    closeGroup: function (name) {

        this.$('.group[data-name="' + name + '"]').addClass('closed');
    },

    openGroup: function (name) {

        this.$('.group[data-name="' + name + '"]').removeClass('closed');
    },

    closeGroups: function () {

        this.$('.group').addClass('closed');
    },

    openGroups: function () {

        this.$('.group').removeClass('closed');
    }
});
