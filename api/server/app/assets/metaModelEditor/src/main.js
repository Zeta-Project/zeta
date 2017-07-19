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

            $("[data-hide]").on("click", function(){
                $("." + $(this).attr("data-hide")).hide();
            });
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
                defaultLink: new joint.shapes.uml.Association(),
                /*
                 * Using own ElementView implementation. ExtendedElementView allows
                 * to Multiselect elements(Shift must be pressed).
                 * @author: Maximilian Göke
                 */
                elementView: ExtendedElementView
            });
            this.paperScroller.options.paper = this.paper;

            $('.paper-container').append(this.paperScroller.render().el);

            this.paperScroller.center();

            this.graph.on('add', this.initializeLinkTooltips, this);

            /*
             * Added distanceLines and guideLines.
             * @author: Maximilian Göke
             */
            this.guideLines = new Guidelines({paper: this.paper});
            this.distanceLines = new Distancelines({graph: this.graph, paper: this.paper});

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

            /*
             * Create instance of group.
             * @author: Maximilian Göke
             */
            this.group = new HtwgGroup();

            this.selection = new Backbone.Collection;

            /*
             * use own selectionView instead of default
             * @author: Maximilian Göke
             */
            this.selectionView = new ExtendedSelectionView({
                paper: this.paper,
                graph: this.graph,
                model: this.selection,
                group: this.group
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

            /*
             * Customized for new functions. Added behaviour when shift was pressed and
             * behaviour when a group of a elemt get slected.
             * @author: Maximilian Göke
             */
            this.paper.on('cell:pointerdown', function (cellView, evt) {
                if (evt.shiftKey) {
                    try {
                        this.halo.remove();
                        this.freetransform.remove();
                    } catch (TypeError) {
                    }
                    return;
                }
                // Select an element if CTRL/Meta key is pressed while the element is clicked.
                if ((evt.ctrlKey || evt.metaKey) && !(cellView.model instanceof joint.dia.Link)) {
                    //remove halo and transform and create a selection box
                    if (this.selection.length == 1 && this.halo !== undefined) {
                        this.halo.remove();
                        this.freetransform.remove();
                        this.selectionView.createSelectionBox(this.paper.findViewByModel(this.selection.first()));
                    }
                    // select single element or complete group
                    if (this.group.findGroupFromElement(cellView.model.get('id')) === undefined) {
                        this.selectElement(cellView);
                    } else {
                        this.selectCompleteGroup(cellView.model.get('id'));
                    }
                } else {
                    // if no other element is selected as the current selected element
                    // and it's in a group select the whole group. If it's not in a group
                    // show halo and transform
                    try {
                        this.halo.remove();
                        this.freetransform.remove();
                    } catch (TypeError) {
                    } finally {
                        this.selectionView.cancelSelection();
                        this.selectCompleteGroup(cellView.model.get('id'));
                    }
                }
            }, this);

            /*
             * Customized for new functions. Added behaviour when a element of a group
             * gets deseleceted.
             * @author: Maximilian Göke
             */
            this.selectionView.on('selection-box:pointerdown', function (evt) {
                // Unselect an element if the CTRL/Meta key is pressed while a selected element is clicked.
                if (evt.ctrlKey || evt.metaKey) {
                    var elementID = $(evt.target).data('model');
                    var groupID = this.group.findGroupFromElement(elementID);

                    // deselect single element or complete group
                    if (groupID === undefined) {
                        this.deselectElement(elementID);
                    } else {
                        this.deselectCompleteGroup(groupID);
                    }
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

        /*
         * Customized to create the possibility to remove freetransform or halo in
         * an other funciton and to set halo only when no group is selected.
         * @author:Maximilian Göke
         */
        initializeHaloAndInspector: function () {

            // MEnum-inspector by default.
            this.createInspector(this.paper.findViewByModel(mEnum.getMEnumContainer()));
            this.inspector.closeGroups();

            this.paper.on('blank:pointerdown', function () {
                this.createInspector(this.paper.findViewByModel(mEnum.getMEnumContainer()));
            }, this);

            this.paper.on('cell:pointerup', function (cellView, evt) {


                if (this.selection.length > 0) return;

                if (cellView.model instanceof joint.dia.Link || this.selection.contains(cellView.model)) return;

                // In order to display halo link magnets on top of the freetransform div we have to create the
                // freetransform first. This is necessary for IE9+ where pointer-events don't work and we wouldn't
                // be able to access magnets hidden behind the div.
                this.freetransform = new joint.ui.FreeTransform({
                    graph: this.graph,
                    paper: this.paper,
                    cell: cellView.model
                });
                this.halo = new joint.ui.Halo({graph: this.graph, paper: this.paper, cellView: cellView});

                this.freetransform.render();
                this.halo.render();

                this.initializeHaloTooltips(this.halo);

                this.createInspector(cellView);

                this.selectionView.cancelSelection();
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
            $('#btn-method-verification').on('click', _.bind(this.verifyMethods, this));

            // toFront/toBack must be registered on mousedown. SelectionView empties the selection
            // on document mouseup which happens before the click event. @TODO fix SelectionView?
            /*
             * Changed function in binding for btn-to-front and btn-to-back
             * This changes were needed because the call that was bind to these
             * buttons won't work after updating jointjs.
             * @author: Maximilian Göke
             */
            $('#btn-to-front').on('mousedown', _.bind(function (evt) {
                this.setPositionOfSelected('toFront');
            }, this));
            $('#btn-to-back').on('mousedown', _.bind(function (evt) {
                this.setPositionOfSelected('toBack');
            }, this));


            $('#input-gridsize').on('input', _.bind(function (evt) {
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

            /*
             * Added to update the zoom percentage that is displayes in the toolbar.
             * @author: Maximilian Göke
             */
            this.paper.on('scale', function (scale) {
                $('#zoom-percentage').text(Math.round(scale * 100) + '%');
            }, this);

            $('#btn-export-mm').on('click', _.bind(function (evt) {
                var exporter = new Exporter(this.graph);
                var metaModel = exporter.export();

                if (metaModel.isValid()) {
                    // Send exported Metamodel to server
                    this.saveMetaModel(metaModel.getMetaModel(), this.graph.toJSON());
                } else {
                    var errorMessage = "";
                    metaModel.getMessages().forEach(function (message) {
                        errorMessage += message + '\n';
                    });
                    this.showExportFailure(errorMessage);
                }
            }, this));

        },

        /**
         * Saves the meta model and graph on the server.
         * @param metaModel
         * @param graph
         */
        saveMetaModel: function (metaModel, graph) {
            var showFailure = this.showExportFailure;
            var showSuccess = this.showExportSuccess;

            var data = JSON.stringify({
                name: window.loadedMetaModel.name,
                elements: metaModel,
                uiState: JSON.stringify(graph)
            });

            console.log("SaveMetaModel - Data: ");
            console.log(data);

            $.ajax({
                type: 'PUT',
                url: '/rest/v1/meta-models/' + window.loadedMetaModel.uuid + '/definition',
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: data,
                success: function (data, textStatus, jqXHR) {
                    showSuccess();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    showFailure("Error saving meta model: " + errorThrown);
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
        }
        ,

        initializeMEnum: function () {
            mEnum.init(this.graph);
            if (!this.graph.getCell(mEnum.MENUM_CONTAINER_ID)) {
                this.graph.addCell(mEnum.getMEnumContainer());
            }
        }
        ,

        openAsPNG: function () {

            var windowFeatures = 'menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes';
            var windowName = _.uniqueId('png_output');
            var imageWindow = window.open('', windowName, windowFeatures);

            this.paper.toPNG(function (dataURL) {
                imageWindow.document.write('<img src="' + dataURL + '"/>');
            }, {padding: 10});
        }
        ,

        zoom: function (newZoomLevel, ox, oy) {

            if (_.isUndefined(this.zoomLevel)) {
                this.zoomLevel = 1;
            }

            if (newZoomLevel > 0.2 && newZoomLevel < 20) {

                ox = ox || (this.paper.el.scrollLeft + this.paper.el.clientWidth / 2) / this.zoomLevel;
                oy = oy || (this.paper.el.scrollTop + this.paper.el.clientHeight / 2) / this.zoomLevel;

                /*
                 * Changed function call. Removed two arguments. The removed arguments
                 * are used for paperScroller.center().
                 * @author: Maximilian Göke
                 */
                this.paper.scale(newZoomLevel, newZoomLevel);

                /*
                 * Added function call. Helps to align scaled SVG.
                 * @author: Maximilian Göke
                 */
                this.paperScroller.center(ox, oy);

                this.zoomLevel = newZoomLevel;
            }
        }
        ,

        zoomOut: function () {
            this.zoom((this.zoomLevel || 1) - 0.2);
        }
        ,
        zoomIn: function () {
            this.zoom((this.zoomLevel || 1) + 0.2);
        }
        ,

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
        }
        ,

        setGrid: function (gridSize) {

            this.paper.options.gridSize = gridSize;

            var backgroundImage = this.getGridBackgroundImage(gridSize);
            $(this.paper.svg).css('background-image', 'url("' + backgroundImage + '")');
        }
        ,

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
        ,

        verifyMethods: function() {

        }
        ,

// ---------- customization start
// Functions between customization start and end are created
// by Maximilian Göke
        /* zooms stencil objects. */
        zoomStencilElementsOnDrop: function () {
            this.graph.on('add', function (cell) {
                var type = cell.get('type');
                switch (type) {
                    case 'wireframe.SmartPhone':
                        cell.set('size', {width: 300, height: 600});
                        break;
                    case 'wireframe.ProgramPanel':
                        cell.set('size', {width: 276, height: 50});
                        break;
                    case 'wireframe.Statusbar':
                        cell.set('size', {width: 276, height: 30});
                        break;
                    default:
                        break;
                }
            });
        }
        ,

        /* Selects a complete group. */
        selectCompleteGroup: function (elementID) {
            // find group id
            var groupID = this.group.findGroupFromElement(elementID);

            // find all elements from group and select them
            if (groupID !== undefined) {
                var elementIDs = this.group.getElementIDsFromGroup(groupID);
                elementIDs.forEach(function (id) {
                    this.selectElement(this.paper.findViewByModel(this.graph.getCell(id)));
                }, this);
            }
        }
        ,

        /* Selects a single Element */
        selectElement: function (view) {
            this.selection.add(view.model);
            this.selectionView.createSelectionBox(view);
        }
        ,

        /* Deselects a complete group */
        deselectCompleteGroup: function (groupID) {
            // find all elements from group and deselect them
            var elementIDs = this.group.getElementIDsFromGroup(groupID);
            elementIDs.forEach(function (id) {
                this.deselectElement(id);
            }, this);
        }
        ,

        /* Deselects a single element. */
        deselectElement: function (elementID) {
            var cell = this.selection.get(elementID);
            this.selection.reset(this.selection.without(cell));
            this.selectionView.destroySelectionBox(this.paper.findViewByModel(cell));
        }
        ,

        /* Handles the toFront/toBack functionality */
        setPositionOfSelected: function (functionName) {
            this.selection.each(function (cell) {
                cell.attributes[functionName]();
                this.halo.remove();
                this.freetransform.remove();
            }, this);

        },

        showExportSuccess: function() {
            $("#success-panel").fadeOut('slow', function() {
                $("#error-panel").fadeOut('slow', function() {
                    $("#success-panel").show();
                    $("#success-panel").find("div").text("Success, metamodel saved!");
                    $("#success-panel").fadeIn('slow');
                });
            });
        },

        showExportFailure: function(reason) {
            $("#success-panel").fadeOut('slow', function() {
                $("#error-panel").fadeOut('slow', function() {
                    $("#error-panel").show();
                    $("#error-panel").find("div").text(reason);
                    $("#error-panel").fadeIn('slow');
                });
            });
        }
// ---------- customization end
    })
    ;
