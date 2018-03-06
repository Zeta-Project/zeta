import $ from 'jquery';
import joint from 'jointjs';
import Backbone from 'backbone1.0';
import KeyboardJS from 'keyboardjs/keyboard';

import Distancelines from './jointjs/distancelines';
import Guidelines from './jointjs/guidelines';

import linkTypeSelector from './model/ext/linkTypeSelector';
import modelExporter from './model/ext/exportModel';
import sizeManager from './model/ext/sizeManager';
import compartmentManager from './model/ext/compartmentManager';
import Stencil from './model/generator/temporary/old/stencil';
import {InspectorDefs} from './model/generator/temporary/old/inspector';

export default Backbone.Router.extend({

    routes: {
        '*path': 'home'
    },

    initialize: function(options) {

        this.options = options || {};
    },

    home: function() {

        this.initializeEditor();
    },

    toSVG: function () {
    	return this.paper.toSVG();
    },

    initializeEditor: function() {

        this.inspectorClosedGroups = {};
        this.initializePaper();
        this.initializeStencil();
        this.initializeSelection();
        this.initializeHaloAndInspector();
        this.initializeClipboard();
        this.initializeCommandManager();
        this.initializeToolbar();
        this.initializeToggleSidePanels();
        // Intentionally commented out. See the `initializeValidator()` method for reasons.
        // Uncomment for demo purposes.
        // this.initializeValidator();
        // Commented out by default. You need to run `node channelHub.js` in order to make
        // channels working. See the documentation to the joint.com.Channel plugin for details.
        // this.initializeChannel('ws://jointjs.com:4141');
        if (this.options.channelUrl) {
            this.initializeChannel(this.options.channelUrl);
        }
        this.initializeGraph();
        sizeManager.init(this.graph);
        compartmentManager.init(this.graph, this.paper);

        window.globalPaper = this.paper;
        window.globalGraph = this.graph;

        // Custom code
        $("#metamodel-name").text("Model name: "+window._global_model_name);
        linkTypeSelector.init(this.graph, this.paper);
    },

    initializeGraph: function() {
        if (window._global_loadOnStart === true) {
            this.graph.fromJSON(_.extend({}, window._global_graph));
        }
    },

    // Create a graph, paper and wrap the paper in a PaperScroller.
    initializePaper: function() {

        this.graph = new joint.dia.Graph;

        this.paperScroller = new joint.ui.PaperScroller({ autoResizePaper: true });
        this.paper = new joint.dia.Paper({
            el: this.paperScroller.el,
            width: 5000,
            height: 5000,
            gridSize: 10,
            perpendicularLinks: true,
            model: this.graph,
            defaultLink: new joint.shapes.zeta.MLink({
                attrs: {
                    // @TODO: scale(0) fails in Firefox
                    '.marker-source': {d: 'M 10 0 L 0 5 L 10 10 z', transform: 'scale(0.001)'},
                    '.marker-target': {d: 'M 10 0 L 0 5 L 10 10 z'}, transform: 'rotate(45)',
                    '.connection': {
                        stroke: 'black'
                        // filter: { name: 'dropShadow', args: { dx: 1, dy: 1, blur: 2 } }
                    }
                }
            })
        });
        this.paperScroller.options.paper = this.paper;

        $('.paper-container').append(this.paperScroller.render().el);

        this.paperScroller.center();

        this.graph.on('add', this.initializeLinkTooltips, this);

        this.guideLines = new Guidelines({paper: this.paper});
        this.distanceLines = new Distancelines({graph: this.graph, paper: this.paper});
    },

    initializeLinkTooltips: function(cell) {

        if (cell instanceof joint.dia.Link) {

            var linkView = this.paper.findViewByModel(cell);
            new joint.ui.Tooltip({
                className: 'tooltip small',
                target: linkView.$('.tool-options'),
                content: 'Click to open Inspector for this link',
                left: linkView.$('.tool-options'),
                direction: 'left'
            });
        }
    },

    // Create and popoulate stencil.
    initializeStencil: function() {

        this.stencil = new joint.ui.Stencil({ graph: this.graph, paper: this.paper, width: 240, groups: Stencil.groups });
        $('.stencil-container').append(this.stencil.render().el);

        _.each(Stencil.groups, function(group, name) {

            this.stencil.load(Stencil.shapes[name], name);
            joint.layout.GridLayout.layout(this.stencil.getGraph(name), {
                columnWidth: this.stencil.options.width / 2 - 10,
                columns: 2,
                rowHeight: 90,
                resizeToFit: false,
                dy: 10,
                dx: 10
            });
            this.stencil.getPaper(name).fitToContent(1, 1, 10);

        }, this);

        $('.stencil-container .btn-expand').on('click', _.bind(this.stencil.openGroups, this.stencil));
        $('.stencil-container .btn-collapse').on('click', _.bind(this.stencil.closeGroups, this.stencil));

        this.initializeStencilTooltips();
    },

    initializeStencilTooltips: function() {

        // Create tooltips for all the shapes in stencil.
        _.each(this.stencil.graphs, function(graph) {

            graph.get('cells').each(function(cell) {

                new joint.ui.Tooltip({
                    target: '.stencil [model-id="' + cell.id + '"]',
                    content: cell.get('type').split('.').join(' '),
                    left: '.stencil',
                    direction: 'left'
                });
            });
        });
    },

    initializeSelection: function() {

        this.selection = new Backbone.Collection;
        this.selectionView = new joint.ui.SelectionView({ paper: this.paper, graph: this.graph, model: this.selection });

        // Initiate selecting when the user grabs the blank area of the paper while the Shift key is pressed.
        // Otherwise, initiate paper pan.
        this.paper.on('blank:pointerdown', function(evt, x, y) {

            if (_.contains(KeyboardJS.activeKeys(), 'shift')) {
                this.selectionView.startSelecting(evt, x, y);
            } else {
                this.paperScroller.startPanning(evt, x, y);
            }
        }, this);

        this.paper.on('cell:pointerdown', function(cellView, evt) {
            // Select an element if CTRL/Meta key is pressed while the element is clicked.
            if ((evt.ctrlKey || evt.metaKey) && !(cellView.model instanceof joint.dia.Link)) {
                this.selectionView.createSelectionBox(cellView);
                this.selection.add(cellView.model);
            }
        }, this);

        this.selectionView.on('selection-box:pointerdown', function(evt) {
            // Unselect an element if the CTRL/Meta key is pressed while a selected element is clicked.
            if (evt.ctrlKey || evt.metaKey) {
                var cell = this.selection.get($(evt.target).data('model'));
                this.selectionView.destroySelectionBox(this.paper.findViewByModel(cell));
                this.selection.reset(this.selection.without(cell));
            }
        }, this);

        // Disable context menu inside the paper.
        // This prevents from context menu being shown when selecting individual elements with Ctrl in OS X.
        this.paper.el.oncontextmenu = function(evt) { evt.preventDefault(); };

        KeyboardJS.on('delete, backspace', _.bind(function() {

            this.commandManager.initBatchCommand();
            this.selection.invoke('remove');
            this.commandManager.storeBatchCommand();
            this.selectionView.cancelSelection();
        }, this));
    },

    createInspector: function(cellView) {

        // No need to re-render inspector if the cellView didn't change.
        if (!this.inspector || this.inspector.options.cell !== cellView.model) {

            if (this.inspector) {

                this.inspectorClosedGroups[this.inspector.options.cell.id] = _.map(this.inspector.$('.group.closed'), function(g) { return $(g).index() });

                // Clean up the old inspector if there was one.
                this.inspector.remove();
            }

            var inspectorDefs = InspectorDefs[cellView.model.get('type')];

            this.inspector = new joint.ui.Inspector({
                inputs: inspectorDefs ? inspectorDefs.inputs : CommonInspectorInputs,
                groups: inspectorDefs ? inspectorDefs.groups : CommonInspectorGroups,
                cell: cellView.model
            });

            this.initializeInspectorTooltips();

            this.inspector.render();
            $('.inspector-container').html(this.inspector.el);

            if (this.inspectorClosedGroups[cellView.model.id]) {
                _.each(this.inspector.$('.group'), function(g, i) {
                    if (_.contains(this.inspectorClosedGroups[cellView.model.id], $(g).index())) {
                        $(g).addClass('closed');
                    }
                }, this);
            } else {
                this.inspector.$('.group:not(:first-child)').addClass('closed');
            }
        }
    },

    initializeInspectorTooltips: function() {

        this.inspector.on('render', function() {

            this.inspector.$('[data-tooltip]').each(function() {

                var $label = $(this);
                new joint.ui.Tooltip({
                    target: $label,
                    content: $label.data('tooltip'),
                    right: '.inspector',
                    direction: 'right'
                });
            });

        }, this);
    },

    initializeHaloAndInspector: function() {

        this.paper.on('cell:pointerup', function(cellView, evt) {

            if (cellView.model instanceof joint.dia.Link || this.selection.contains(cellView.model)) return;

            // In order to display halo link magnets on top of the freetransform div we have to create the
            // freetransform first. This is necessary for IE9+ where pointer-events don't work and we wouldn't
            // be able to access magnets hidden behind the div.
            var freetransform = new joint.ui.FreeTransform({ graph: this.graph, paper: this.paper, cell: cellView.model });
            this.halo = new joint.ui.Halo({ graph: this.graph, paper: this.paper, cellView: cellView });

            freetransform.render();
            this.halo.render();

            this.initializeHaloTooltips(this.halo);

            this.createInspector(cellView);

            this.selection.reset([cellView.model]);

        }, this);

        this.paper.on('link:options', function(cellView, evt, x, y) {
            this.createInspector(cellView);
        }, this);
    },

    initializeHaloTooltips: function(halo) {

        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.remove'),
            content: 'Click to remove the object',
            direction: 'right',
            right: halo.$('.remove'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.fork'),
            content: 'Click and drag to clone and connect the object in one go',
            direction: 'left',
            left: halo.$('.fork'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.clone'),
            content: 'Click and drag to clone the object',
            direction: 'left',
            left: halo.$('.clone'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.unlink'),
            content: 'Click to break all connections to other objects',
            direction: 'right',
            right: halo.$('.unlink'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.link'),
            content: 'Click and drag to connect the object',
            direction: 'left',
            left: halo.$('.link'),
            padding: 15
        });
        new joint.ui.Tooltip({
            className: 'tooltip small',
            target: halo.$('.rotate'),
            content: 'Click and drag to rotate the object',
            direction: 'right',
            right: halo.$('.rotate'),
            padding: 15
        });
    },

    initializeClipboard: function() {

        this.clipboard = new joint.ui.Clipboard;

        KeyboardJS.on('ctrl + c', _.bind(function() {
            // Copy all selected elements and their associated links.
            this.clipboard.copyElements(this.selection, this.graph, { translate: { dx: 20, dy: 20 }, useLocalStorage: true });
        }, this));

        KeyboardJS.on('ctrl + v', _.bind(function() {
            this.clipboard.pasteCells(this.graph);
            this.selectionView.cancelSelection();

            this.clipboard.pasteCells(this.graph, { link: { z: -1 }, useLocalStorage: true });

            // Make sure pasted elements get selected immediately. This makes the UX better as
            // the user can immediately manipulate the pasted elements.
            var selectionTmp = [];

            this.clipboard.each(function(cell) {

                if (cell.get('type') === 'link') return;

                // Push to the selection not to the model from the clipboard but put the model into the graph.
                // Note that they are different models. There is no views associated with the models
                // in clipboard.
                selectionTmp.push(this.graph.getCell(cell.id));
                this.selectionView.createSelectionBox(this.paper.findViewByModel(cell));
            }, this);

            this.selection.reset(selectionTmp);
        }, this));

        KeyboardJS.on('ctrl + x', _.bind(function() {

            var originalCells = this.clipboard.copyElements(this.selection, this.graph, { useLocalStorage: true });
            this.commandManager.initBatchCommand();
            _.invoke(originalCells, 'remove');
            this.commandManager.storeBatchCommand();
            this.selectionView.cancelSelection();
        }, this));
    },

    initializeCommandManager: function() {

        this.commandManager = new joint.dia.CommandManager({ graph: this.graph });

        KeyboardJS.on('ctrl + z', _.bind(function() {

            this.commandManager.undo();
            this.selectionView.cancelSelection();
        }, this));

        KeyboardJS.on('ctrl + y', _.bind(function() {

            this.commandManager.redo();
            this.selectionView.cancelSelection();
        }, this));
    },

    initializeValidator: function() {

        // This is just for demo purposes. Every application has its own validation rules or no validation
        // rules at all.

        this.validator = new joint.dia.Validator({ commandManager: this.commandManager });

        this.validator.validate('change:position change:size add', _.bind(function(err, command, next) {

            if (command.action === 'add' && command.batch) return next();

            var cell = command.data.attributes || this.graph.getCell(command.data.id).toJSON();
            var area = joint.g.rect(cell.position.x, cell.position.y, cell.size.width, cell.size.height);

            if (_.find(this.graph.getElements(), function(e) {

	        var position = e.get('position');
                var size = e.get('size');
	        return (e.id !== cell.id && area.intersect(g.rect(position.x, position.y, size.width, size.height)));

            })) return next("Another cell in the way!");
        }, this));

        this.validator.on('invalid',function(message) {

            $('.statusbar-container').text(message).addClass('error');

            _.delay(function() {

                $('.statusbar-container').text('').removeClass('error');

            }, 1500);
        });
    },

    initializeToolbar: function() {
        var self = this;
        this.initializeToolbarTooltips();

        $('#btn-export-model').on('click', _.bind(function() {
            modelExporter.exportModel(this.graph);
            var msg = {
              action: 'SavedModel',
              model: window._global_uuid
            };
            connection.send(JSON.stringify(msg));
        }, this));
        $('#btn-undo').on('click', _.bind(this.commandManager.undo, this.commandManager));
        $('#btn-redo').on('click', _.bind(this.commandManager.redo, this.commandManager));
        $('#btn-clear').on('click', _.bind(this.graph.clear, this.graph));
        $('#btn-svg').on('click', _.bind(this.paper.openAsSVG, this.paper));
        $('#btn-png').on('click', _.bind(this.openAsPNG, this));
        $('#btn-zoom-in').on('click', _.bind(this.zoomIn, this));
        $('#btn-zoom-out').on('click', _.bind(this.zoomOut, this));
        $('#btn-fullscreen').on('click', _.bind(this.toggleFullscreen, this));
        $('#btn-print').on('click', _.bind(this.paper.print, this.paper));
        $('#btn-to-vr').on('click', _.bind(this.switchToVr, this));
        $('#btn-download').on('click', _.bind(this.download, this));

        // toFront/toBack must be registered on mousedown. SelectionView empties the selection
        // on document mouseup which happens before the click event. @TODO fix SelectionView?
        $('#btn-to-front').on('mousedown', _.bind(function (evt) {
            this.setPositionOfSelected('toFront');
        }, this));
        $('#btn-to-back').on('mousedown', _.bind(function (evt) {
            this.setPositionOfSelected('toBack');
        }, this));

        $('#btn-layout').on('click', _.bind(this.layoutDirectedGraph, this));

        $('#input-gridsize').on('change', _.bind(function(evt) {
            var gridSize = parseInt(evt.target.value, 10);
            $('#output-gridsize').text(gridSize);
            this.setGrid(gridSize);
        }, this));

        $('#input-distancelines').on('input', _.bind(function (evt) {
            var distance = parseInt(evt.target.value, 10);
            $('#output-distancelines').text(distance);
            this.distanceLines.options.distance = distance;
        }, this));

        $('#checkbox-distancelines').on('change', _.bind(function (evt) {
            var input = $('#input-distancelines');
            if ($('#checkbox-distancelines').is(':checked')) {
                this.distanceLines.start();
                $('#btn-distancelines').addClass('active');
                input.prop("disabled", false);
            } else {
                this.distanceLines.stop();
                $('#btn-distancelines').removeClass('active');
                input.prop("disabled", true);
            }
        }, this));

        $('#input-guidelines').on('input', _.bind(function (evt) {
            var distance = parseInt(evt.target.value, 10);
            $('#output-guidelines').text(distance);
            this.guideLines.options.distance = distance;
        }, this));

        $('#checkbox-guidelines').on('change', _.bind(function (evt) {
            var input = $('#input-guidelines');
            if ($('#checkbox-guidelines').is(':checked')) {
                this.guideLines.start();
                input.prop("disabled", false);
                $('#btn-guidelines').addClass('active');
            } else {
                this.guideLines.stop();
                input.prop("disabled", true);
                $('#btn-guidelines').removeClass('active');
            }
        }, this));


        // generators
        var connection = new WebSocket('ws://' + window.location.host + '/socket/user/' + window._global_uuid)

        connection.onopen = function () {
          console.log('is open');
        };

        // Log errors
        connection.onerror = function (error) {
          console.log('WebSocket Error ' + error);
        };

        // Log messages from the server
        connection.onmessage = function (e) {
          var obj = JSON.parse(e.data);
          if (obj.type === "BondedTaskList") {
            $('#generators').html("");
            for (var i = 0; i < obj.tasks.length; i++) {
              var item = obj.tasks[i];
              addGenerator(item);
            }
          } else if (obj.type === 'BondedTaskStarted') {
            self.showSuccess("Started generator. Note that execution may take several minutes.");
          } else if (obj.type === 'BondedTaskCompleted') {
            if (obj.status === 0) {
              self.showSuccess("Successfully executed generator!");
            } else {
              self.showError("Generator execution failed!");
            }
          } else {
            console.log("unhandled message!");
            console.log(obj);
          }
        };

        function addGenerator(obj) {
          var element = document.createElement("li");

          element.innerHTML = '<a href="javascript:void(0)">' + obj.item + '</a>';
          element.addEventListener("click", function() {
            var msg = {
              action: 'ExecuteBondedTask',
              model: window._global_uuid,
              task: obj.task
            };
            connection.send(JSON.stringify(msg));
          });

          $('#generators').append(element);
        }
    },

    showSuccess: function(message){
       $("#success-panel").fadeOut('slow', function() {
            $("#error-panel").fadeOut('slow', function() {
                $("#success-panel").show();
                $("#success-panel").find("div").text(message);
                $("#success-panel").fadeIn('slow');
                setTimeout(function(){
                    $("#success-panel").fadeOut('slow');
                }, 10000);
            });
        });
    },

    showError: function(message){
     $("#success-panel").fadeOut('slow', function() {
            $("#error-panel").fadeOut('slow', function() {
                $("#error-panel").show();
                $("#error-panel").find("div").text(message);
                $("#error-panel").fadeIn('slow');
                setTimeout(function(){
                    $("#success-panel").fadeOut('slow');
                }, 10000);
            });
        });
    },

    initializeToolbarTooltips: function() {

        $('.toolbar-container [data-tooltip]').each(function() {

            new joint.ui.Tooltip({
                target: $(this),
                content: $(this).data('tooltip'),
                top: '.toolbar-container',
                direction: 'top'
            });
        });
    },

    openAsPNG: function() {

        var windowFeatures = 'menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes';
        var windowName = _.uniqueId('png_output');
        var imageWindow = window.open('', windowName, windowFeatures);

        this.paper.toPNG(function(dataURL) {
	    imageWindow.document.write('<img src="' + dataURL + '"/>');
        }, { padding: 10 });
    },

    zoom: function(newZoomLevel, ox, oy) {

        if (_.isUndefined(this.zoomLevel)) { this.zoomLevel = 1; }

        if (newZoomLevel > 0.2 && newZoomLevel < 20) {

	        ox = ox || (this.paper.el.scrollLeft + this.paper.el.clientWidth / 2) / this.zoomLevel;
	        oy = oy || (this.paper.el.scrollTop + this.paper.el.clientHeight / 2) / this.zoomLevel;

	        this.paper.scale(newZoomLevel, newZoomLevel);
            this.paperScroller.center(ox, oy);

            this.zoomLevel = newZoomLevel;
        }
    },

    zoomOut: function() { this.zoom((this.zoomLevel || 1) - 0.2); },
    zoomIn: function() { this.zoom((this.zoomLevel || 1) + 0.2); },

    toggleFullscreen: function() {

        var el = document.body;

        function prefixedResult(el, prop) {

            var prefixes = ['webkit', 'moz', 'ms', 'o', ''];
            for (var i = 0; i < prefixes.length; i++) {
                var prefix = prefixes[i];
                var propName = prefix ? (prefix + prop) : (prop.substr(0, 1).toLowerCase() + prop.substr(1));
                if (!_.isUndefined(el[propName])) {
                    return _.isFunction(el[propName]) ? el[propName]() : el[propName];
                }
            }
        }

        if (prefixedResult(document, 'FullScreen') || prefixedResult(document, 'IsFullScreen')) {
            prefixedResult(document, 'CancelFullScreen');
        } else {
            prefixedResult(el, 'RequestFullScreen');
        }
    },
    switchToVr: function () {
        var location = window.location.href;
        location = location.replace("editor", "vreditor");
        window.location.href = location;
    },

    download: function () {
        var self = this;
        $('#downloads').html("");

        $.ajax({
            type: 'GET',
            url: '/db/' + window._global_uuid,
            success: function (data, textStatus, jqXHR) {
              for (var file in data._attachments) {
                if (data._attachments.hasOwnProperty(file)) {
                  var element = document.createElement("li");
                  element.innerHTML = '<a href="/db/' + window._global_uuid + '/' + file + '" download>' + file + '</a>';
                  $('#downloads').append(element);
                }
              }
            },
            error: function (jqXHR, textStatus, errorThrown) {
              self.showError(jqXHR.responseText)
            }
        });
    },

    setGrid: function(gridSize) {

        this.paper.options.gridSize = gridSize;

        var backgroundImage = this.getGridBackgroundImage(gridSize);
        $(this.paper.svg).css('background-image', 'url("' + backgroundImage + '")');
    },

    getGridBackgroundImage: function(gridSize, color) {

        var canvas = $('<canvas/>', { width: gridSize, height: gridSize });

        canvas[0].width = gridSize;
        canvas[0].height = gridSize;

        var context = canvas[0].getContext('2d');
        context.beginPath();
        context.rect(1, 1, 1, 1);
        context.fillStyle = color || '#AAAAAA';
        context.fill();

        return canvas[0].toDataURL('image/png');
    },

    layoutDirectedGraph: function() {

        this.commandManager.initBatchCommand();

        _.each(this.graph.getLinks(), function(link) {

            // Reset vertices.
            link.set('vertices', []);

            // Remove all the non-connected links.
            if (!link.get('source').id || !link.get('target').id) {
                link.remove();
            }
        });

        joint.layout.DirectedGraph.layout(this.graph, { setLinkVertices: false, rankDir: 'LR', rankDir: 'TB' });

        // Scroll to the top-left corner as this is the initial position for the DirectedGraph layout.
        this.paperScroller.el.scrollLeft = 0;
        this.paperScroller.el.scrollTop = 0;

        this.commandManager.storeBatchCommand();
    },

    initializeChannel: function(url) {
        // Example usage of the Channel plugin. Note that this assumes the `node channelHub` is running.
        // See the channelHub.js file for furhter instructions.

        var room = (location.hash && location.hash.substr(1));
        if (!room) {
            room = joint.util.uuid();
            this.navigate('#' + room);
        }

        var channel = this.channel = new joint.com.Channel({ graph: this.graph, url: url || 'ws://localhost:4141', query: { room: room } });
        console.log('room', room, 'channel', channel.id);

        var roomUrl = location.href.replace(location.hash, '') + '#' + room;
        $('.statusbar-container .rt-colab').html('Send this link to a friend to <b>collaborate in real-time</b>: <a href="' + roomUrl + '" target="_blank">' + roomUrl + '</a>');
    },

    initializeToggleSidePanels: function() {
        $(".stencil-toggle-icon-wrapper").on("click", function() {
            $(".stencil-toggle-icon-wrapper").toggleClass("glyphicon-menu-right");
            $(".paper-container").toggleClass("paper-container-stencil-hidden");
            $(".stencil-container").toggleClass("stencil-container-hidden");
            $(".stencil-toggle-container").toggleClass("toggle-container-hidden");
        });
        $(".inspector-toggle-icon-wrapper").on("click", function() {
            $(".inspector-toggle-icon-wrapper").toggleClass("glyphicon-menu-right");
            $(".inspector-toggle-icon-wrapper").toggleClass("glyphicon-menu-left");
            $(".paper-container").toggleClass("paper-container-inspector-hidden");
            $(".inspector-container").toggleClass("inspector-container-hidden");
            $(".inspector-toggle-container").toggleClass("toggle-container-hidden");
        });
    },

    /* Handles the toFront/toBack functionality */
    setPositionOfSelected: function (functionName) {
        this.selection.each(function (cell) {
            cell.attributes[functionName]();
            this.halo.remove();
            this.freetransform.remove();
        }, this);
    }
});
