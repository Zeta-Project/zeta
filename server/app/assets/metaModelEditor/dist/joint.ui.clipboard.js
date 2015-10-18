/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


// Implements Clipboard for copy-pasting elements.
// Note that the clipboard is also able to copy elements and their assocaited links from one graph
// and paste them to another.

// Usage:

//       var selection = new Backbone.Collection;
//       var graph = new joint.dia.Graph;
//       // ... now something that adds elements to the selection ...
//       var clipboard = new joint.ui.Clipboard;
//       KeyboardJS.on('ctrl + c', function() { clipboard.copyElements(selection, graph); });
//       KeyboardJS.on('ctrl + v', function() { clipboard.pasteCells(graph); });


joint.ui.Clipboard = Backbone.Collection.extend({

    // `selection` is a Backbone collection containing elements that should be copied to the clipboard.
    // Note that with these elements, also all the associated links are copied. That's why we
    // also need the `graph` parameter, to find these links.
    // This function returns the elements and links from the original graph that were copied. This is useful
    // for implements the Cut operation where the original cells should be removed from the graph.
    // if `opt.translate` object with `dx` and `dy` properties is passed, the copied elements will
    // be translated by the specified amount. This is useful for e.g. the 'cut' operation where
    // we'd like to have the pasted elements moved by an offset to see they were pasted to the paper.
    // if `opt.useLocalStorage` is `true`, the copied elements will be saved to the localStorage
    // (if present) making it possible to copy-paste elements between browser tabs or sessions.
    copyElements: function(selection, graph, opt) {

        opt = opt || {};
        
        // The method:
        // 1. Find all links that have BOTH source and target in `this.selection`.
        // 2. Clone these links.
        // 3. Clone elements and map their original id to their new id and put them to the clipboard.
        // 4. Reset the target/source id of the cloned links to point to the appropriate cloned elements.
        // 5. Put the modified links to the clipboard too.
        
        var links = [];
        var elements = [];
        var elementsIdMap = {};
        
        selection.each(function(element) {

            var connectedLinks = graph.getConnectedLinks(element);
            
            // filter only those having both source/target in the selection.
            links = links.concat(_.filter(connectedLinks, function(link) {

                if (selection.get(link.get('source').id) && selection.get(link.get('target').id)) {
                    return true;
                }
                return false;
                
            }));

            var clonedElement = element.clone();
            if (opt.translate) {
                clonedElement.translate(opt.translate.dx || 20, opt.translate.dy || 20);
            }
            elements.push(clonedElement);
            elementsIdMap[element.get('id')] = clonedElement.get('id');
            
        });

        // Store the original links so that we can return them from the function together with the
        // original elements.
        var originalLinks = _.unique(links);

        links = _.map(originalLinks, function(link) {

            var clonedLink = link.clone();
            var source = clonedLink.get('source');
            var target = clonedLink.get('target');
            
            source.id = elementsIdMap[source.id];
            target.id = elementsIdMap[target.id];

            clonedLink.set({
                source: _.clone(source),
                target: _.clone(target)
            });

            // Translate vertices as well if necessary.
            if (opt.translate) {

                _.each(clonedLink.get('vertices'), function(vertex) {

                    vertex.x += opt.translate.dx || 20;
                    vertex.y += opt.translate.dy || 20;
                });
            }

            return clonedLink;
        });
        
        this.reset(elements.concat(links));

        if (opt.useLocalStorage && window.localStorage) {

            localStorage.setItem('joint.ui.Clipboard.cells', JSON.stringify(this.toJSON()));
        }

        return (selection.models || []).concat(originalLinks);
    },

    // `opt.link` is attributes that will be set all links before they are added to the `graph`.
    // This is useful for e.g. setting `z: -1` for links in order to always put them to the bottom of the paper.
    pasteCells: function(graph, opt) {

        opt = opt || {};

        if (opt.useLocalStorage && this.length === 0 && window.localStorage) {

            this.reset(JSON.parse(localStorage.getItem('joint.ui.Clipboard.cells')));
        }

	graph.trigger('batch:start');

        this.each(function(cell) {

            cell.unset('z');
            if ((cell instanceof joint.dia.Link) && opt.link) {

                cell.set(opt.link);
            }
            
            graph.addCell(cell.toJSON());
        });

	graph.trigger('batch:stop');
    },

    clear: function() {

        this.reset([]);

        if (window.localStorage) {

            localStorage.removeItem('joint.ui.Clipboard.cells');
        }
    }
});
