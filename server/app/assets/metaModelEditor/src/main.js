var Rappid = Backbone.Router.extend({

    routes: {
        '*path': 'home'
    },

    initialize: function (options) {

        this.options = options || {};
    },

    home: function () {

        this.initializeEditor();
    },

    initializeEditor: function () {

        this.inspectorClosedGroups = {};
        this.inspectorTooltips = [];

        this.initializePaper();
        this.initializeStencil();
        this.initializeSelection();
        this.initializeMEnum();

        if (window.loadedMetaModel.loadOnStart === true) {
            this.graph.fromJSON(_.extend({}, window.loadedMetaModel.graph));
        }

        this.initializeHaloAndInspector();
        this.initializeClipboard();
        this.initializeCommandManager();

        this.initializeToolbar();

        linkTypeSelector.init(this.graph);
        collaboration.init(this.graph);
    },

    // Create a graph, paper and wrap the paper in a PaperScroller.
    initializePaper: function () {

        this.graph = new joint.dia.Graph;

        this.paperScroller = new joint.ui.PaperScroller({autoResizePaper: true});

        this.paper = new joint.dia.Paper({
            el: this.paperScroller.el,
            width: 5000,
            height: 5000,
            gridSize: 10,
            perpendicularLinks: true,
            model: this.graph,
            defaultLink: new joint.shapes.uml.Association()
        });
        this.paperScroller.options.paper = this.paper;

        $('.paper-container').append(this.paperScroller.render().el);

        this.paperScroller.center();

        this.graph.on('add', this.initializeLinkTooltips, this);

    },

    initializeLinkTooltips: function (cell) {

        if (cell instanceof joint.dia.Link) {

            var linkView = this.paper.findViewByModel(cell);
            new joint.ui.Tooltip({
                className: 'tooltip small',
                target: linkView.$('.tool-options'),
                content: 'Click to open Inspector for this link',
                left: linkView.$('.tool-options'),
                direction: 'left'
            });
            new joint.ui.Tooltip({
                className: 'tooltip small',
                target: linkView.$('.tool-remove'),
                content: 'Remove Link',
                left: linkView.$('.tool-remove'),
                direction: 'left'
            });
        }
    },

    // Create and popoulate stencil.
    initializeStencil: function () {

        this.stencil = new joint.ui.Stencil({
            graph: this.graph,
            paper: this.paper,
            width: 240,
            groups: Stencil.groups
        });

        $('.stencil-container').append(this.stencil.render().el);

        _.each(Stencil.groups, function (group, name) {
            this.stencil.load(Stencil.shapes[name], name);

            joint.layout.GridLayout.layout(this.stencil.getGraph(name), {
                columnWidth: this.stencil.options.width / 2 - 10,
                columns: 2,
                rowHeight: 80,
                resizeToFit: true,
                dy: 10,
                dx: 10
            });

            this.stencil.getPaper(name).fitToContent(1, 1, 10);

        }, this);

    },


    initializeSelection: function () {

        this.selection = new Backbone.Collection;
        this.selectionView = new joint.ui.SelectionView({
            paper: this.paper,
            graph: this.graph,
            model: this.selection
        });

        // Initiate selecting when the user grabs the blank area of the paper while the Shift key is pressed.
        // Otherwise, initiate paper pan.
        this.paper.on('blank:pointerdown', function (evt, x, y) {

            if (_.contains(KeyboardJS.activeKeys(), 'shift')) {
                this.selectionView.startSelecting(evt, x, y);
            } else {
                this.paperScroller.startPanning(evt, x, y);
            }
        }, this);

        this.paper.on('cell:pointerdown', function (cellView, evt) {
            // Select an element if CTRL/Meta key is pressed while the element is clicked.
            if ((evt.ctrlKey || evt.metaKey) && !(cellView.model instanceof joint.dia.Link)) {
                this.selectionView.createSelectionBox(cellView);
                this.selection.add(cellView.model);
            }
        }, this);

        this.selectionView.on('selection-box:pointerdown', function (evt) {
            // Unselect an element if the CTRL/Meta key is pressed while a selected element is clicked.
            if (evt.ctrlKey || evt.metaKey) {
                var cell = this.selection.get($(evt.target).data('model'));
                this.selectionView.destroySelectionBox(this.paper.findViewByModel(cell));
                this.selection.reset(this.selection.without(cell));
            }
        }, this);

        // Disable context menu inside the paper.
        // This prevents from context menu being shown when selecting individual elements with Ctrl in OS X.
        this.paper.el.oncontextmenu = function (evt) {
            evt.preventDefault();
        };

        KeyboardJS.on('delete, backspace', _.bind(function () {

            this.commandManager.initBatchCommand();
            this.selection.invoke('remove');
            this.commandManager.storeBatchCommand();
            this.selectionView.cancelSelection();
        }, this));
    },

    createInspector: function (cellView) {
        this.destroyInspector();

        var inspectorDefs = inspector.getDefs(cellView.model, this.graph.getElements(), this.graph.getLinks());

        this.inspector = new joint.ui.Inspector({
            inputs: inspectorDefs.inputs,
            groups: inspectorDefs.groups,
            cellView: cellView,
            live: true
        });

        this.inspector.on('change:name', function (text) {
            if (mCoreUtil.isReference(cellView.model)) {
                cellView.model.label(0, {
                    position: 0.5,
                    attrs: {
                        text: {
                            text: text
                        }
                    }
                });
            }
        });

        this.inspector.render();

        $('.inspector-container-inner').html(this.inspector.el);

        if (this.inspectorClosedGroups[cellView.model.id]) {
            _.each(this.inspector.$('.group'), function (g, i) {
                if (_.contains(this.inspectorClosedGroups[cellView.model.id], $(g).index())) {
                    $(g).addClass('closed');
                }
            }, this);
        } else {
            this.inspector.$('.group:not(:first-child)').addClass('closed');
        }

        var collapseButtons = $(this.inspector.el).find('.custom-btn-list-collapse');
        for (var i = 0; i < collapseButtons.length; ++i) {
            collapseButtons[i].click();
        }

    },

    destroyInspector: function () {
        if (this.inspector) {

            this.inspectorClosedGroups[this.inspector.options.cellView.model.id] = _.map(app.inspector.$('.group.closed'), function (g) {
                return $(g).index()
            });

            // Clean up the old inspector if there was one.
            this.inspector.remove();
        }
    },

    initializeHaloAndInspector: function () {

        // MEnum-inspector by default.
        this.createInspector(this.paper.findViewByModel(mEnum.getMEnumContainer()));
        this.inspector.closeGroups();

        this.paper.on('blank:pointerdown', function () {
            this.createInspector(this.paper.findViewByModel(mEnum.getMEnumContainer()));
        }, this);

        this.paper.on('cell:pointerup', function (cellView, evt) {

            if (cellView.model instanceof joint.dia.Link || this.selection.contains(cellView.model)) {
                return;
            }

            // In order to display halo link magnets on top of the freetransform div we have to create the
            // freetransform first. This is necessary for IE9+ where pointer-events don't work and we wouldn't
            // be able to access magnets hidden behind the div.
            var freetransform = new joint.ui.FreeTransform({
                graph: this.graph,
                paper: this.paper,
                cell: cellView.model
            });
            var halo = new joint.ui.Halo({graph: this.graph, paper: this.paper, cellView: cellView});

            freetransform.render();
            halo.render();

            this.initializeHaloTooltips(halo);

            this.createInspector(cellView);

            this.selection.reset([cellView.model]);

        }, this);

        this.paper.on('link:options', function (evt, cellView, x, y) {
            this.createInspector(cellView);
        }, this);
    },

    initializeHaloTooltips: function (halo) {

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

    initializeClipboard: function () {

        this.clipboard = new joint.ui.Clipboard;

        KeyboardJS.on('ctrl + c', _.bind(function () {
            // Copy all selected elements and their associated links.
            this.clipboard.copyElements(this.selection, this.graph, {
                translate: {dx: 20, dy: 20},
                useLocalStorage: true
            });
        }, this));

        KeyboardJS.on('ctrl + v', _.bind(function () {
            this.clipboard.pasteCells(this.graph);
            this.selectionView.cancelSelection();

            this.clipboard.pasteCells(this.graph, {link: {z: -1}, useLocalStorage: true});

            // Make sure pasted elements get selected immediately. This makes the UX better as
            // the user can immediately manipulate the pasted elements.
            var selectionTmp = [];

            this.clipboard.each(function (cell) {

                if (cell.get('type') === 'link') {
                    return;
                }

                // Push to the selection not to the model from the clipboard but put the model into the graph.
                // Note that they are different models. There is no views associated with the models
                // in clipboard.
                selectionTmp.push(this.graph.getCell(cell.id));
                this.selectionView.createSelectionBox(this.paper.findViewByModel(cell));
            }, this);

            this.selection.reset(selectionTmp);
        }, this));

        KeyboardJS.on('ctrl + x', _.bind(function () {

            var originalCells = this.clipboard.copyElements(this.selection, this.graph, {useLocalStorage: true});
            this.commandManager.initBatchCommand();
            _.invoke(originalCells, 'remove');
            this.commandManager.storeBatchCommand();
            this.selectionView.cancelSelection();
        }, this));
    },

    initializeCommandManager: function () {

        this.commandManager = new joint.dia.CommandManager({graph: this.graph});

        KeyboardJS.on('ctrl + z', _.bind(function () {

            this.commandManager.undo();
            this.selectionView.cancelSelection();
        }, this));

        KeyboardJS.on('ctrl + y', _.bind(function () {

            this.commandManager.redo();
            this.selectionView.cancelSelection();
        }, this));
    },

    initializeToolbar: function () {

        this.initializeToolbarTooltips();

        $('#btn-undo').on('click', _.bind(this.commandManager.undo, this.commandManager));
        $('#btn-redo').on('click', _.bind(this.commandManager.redo, this.commandManager));

        $('#btn-clear').on('click', _.bind(function () {
            var enumContainer = mEnum.getMEnumContainer();
            this.graph.clear();
            this.graph.addCell(enumContainer);
        }, this));

        $('#btn-svg').on('click', _.bind(this.paper.openAsSVG, this.paper));
        $('#btn-png').on('click', _.bind(this.openAsPNG, this));
        $('#btn-zoom-in').on('click', _.bind(this.zoomIn, this));
        $('#btn-zoom-out').on('click', _.bind(this.zoomOut, this));
        $('#btn-fullscreen').on('click', _.bind(this.toggleFullscreen, this));
        $('#btn-print').on('click', _.bind(this.paper.print, this.paper));

        // toFront/toBack must be registered on mousedown. SelectionView empties the selection
        // on document mouseup which happens before the click event. @TODO fix SelectionView?
        $('#btn-to-front').on('mousedown', _.bind(function (evt) {
            this.selection.invoke('toFront');
        }, this));

        $('#btn-to-back').on('mousedown', _.bind(function (evt) {
            this.selection.invoke('toBack');
        }, this));

        $('#input-gridsize').on('change', _.bind(function (evt) {
            var gridSize = parseInt(evt.target.value, 10);
            $('#output-gridsize').text(gridSize);
            this.setGrid(gridSize);
        }, this));

        $('#btn-export-mm').on('click', _.bind(function (evt) {
            var exporter = new Exporter(this.graph);
            var metaModel = exporter.export();

            console.log(metaModel);

            if (metaModel.isValid()) {
                var metaModelWindow = window.open("", "", "width=800,height=600");
                metaModelWindow.document.write('<pre>' + metaModel.toString(true) + '</pre>');
                metaModelWindow.document.title = 'Exported Meta Model';

                // Send exported Metamodel to server
                var metaModelName = metaModelWindow.prompt('Enter a name for this meta model and click "ok" to save it. Click "cancel" if you don\'t want to save it.', window.loadedMetaModel.name || "");
                if (metaModelName) {
                    this.saveMetaModel(metaModel.getMetaModel(), this.graph.toJSON(), metaModelName);
                }

            } else {
                var errorMessage = "";
                metaModel.getMessages().forEach(function (message) {
                    errorMessage += message + '\n';
                });
                alert(errorMessage);
            }
        }, this));
    },

    saveMetaModel: function (metaModel, graph, metaModelName) {
        $.ajax({
            type: 'POST',
            url: '/metamodel/save',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                name: metaModelName,
                data: metaModel,
                graph: graph,
                uuid: window.loadedMetaModel.uuid
            }),
            success: function (data, textStatus, jqXHR) {
                alert("Saved meta model: " + data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Error saving meta model: " + errorThrown);
            }
        });
    },

    initializeToolbarTooltips: function () {
        _.each($('.toolbar-container .btn'), function (button) {
            var btn = $(button);
            var content = btn.data('tooltip');
            if (content) {
                new joint.ui.Tooltip({
                    className: 'tooltip',
                    target: btn,
                    content: content,
                    direction: 'top',
                    top: btn
                });
            }
        });
    },

    initializeMEnum: function () {
        mEnum.init(this.graph);
        if (!this.graph.getCell(mEnum.MENUM_CONTAINER_ID)) {
            this.graph.addCell(mEnum.getMEnumContainer());
        }
    },

    openAsPNG: function () {

        var windowFeatures = 'menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes';
        var windowName = _.uniqueId('png_output');
        var imageWindow = window.open('', windowName, windowFeatures);

        this.paper.toPNG(function (dataURL) {
            imageWindow.document.write('<img src="' + dataURL + '"/>');
        }, {padding: 10});
    },

    zoom: function (newZoomLevel, ox, oy) {

        if (_.isUndefined(this.zoomLevel)) {
            this.zoomLevel = 1;
        }

        if (newZoomLevel > 0.2 && newZoomLevel < 20) {

            ox = ox || (this.paper.el.scrollLeft + this.paper.el.clientWidth / 2) / this.zoomLevel;
            oy = oy || (this.paper.el.scrollTop + this.paper.el.clientHeight / 2) / this.zoomLevel;

            this.paper.scale(newZoomLevel, newZoomLevel, ox, oy);

            this.zoomLevel = newZoomLevel;
        }
    },

    zoomOut: function () {
        this.zoom((this.zoomLevel || 1) - 0.2);
    },
    zoomIn: function () {
        this.zoom((this.zoomLevel || 1) + 0.2);
    },

    toggleFullscreen: function () {

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

    setGrid: function (gridSize) {

        this.paper.options.gridSize = gridSize;

        var backgroundImage = this.getGridBackgroundImage(gridSize);
        $(this.paper.svg).css('background-image', 'url("' + backgroundImage + '")');
    },

    getGridBackgroundImage: function (gridSize, color) {

        var canvas = $('<canvas/>', {width: gridSize, height: gridSize});

        canvas[0].width = gridSize;
        canvas[0].height = gridSize;

        var context = canvas[0].getContext('2d');
        context.beginPath();
        context.rect(1, 1, 1, 1);
        context.fillStyle = color || '#AAAAAA';
        context.fill();

        return canvas[0].toDataURL('image/png');
    }
});
