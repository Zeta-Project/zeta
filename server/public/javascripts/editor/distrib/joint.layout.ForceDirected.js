/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


// Force Directed layout implementation.
// =====================================

// Resources:
//      Efficient and High Quality Force-Directed Graph Drawing, Yifan Hu
//      Simple Algorithms for Network Visualization: A Tutorial, Michael J. McGufﬁn
//      Graph Drawing by Force-directed Placement, Thomas M. J. Fruchterman and Edward M. Reingold
//      D3.js, http://d3js.org

joint.layout.ForceDirected = Backbone.Model.extend({

    defaults: {

        linkDistance: 10,
        linkStrength: 1,
        charge: 10
    },

    initialize: function(opt) {

        this.elements = this.get('graph').getElements();
        this.links = this.get('graph').getLinks();
        this.cells = this.get('graph').get('cells');
        this.width = this.get('width');
        this.height = this.get('height');
        this.gravityCenter = this.get('gravityCenter');
        
        this.t = 1;
        this.energy = Infinity;
        this.progress = 0;
    },

    start: function() {
        
        var w = this.get('width');
        var h = this.get('height');

        var elementsJSON = [];
        var linksJSON = [];
        
        // Random layout.
        _.each(this.elements, function(el) {
            el.set('position', { x: Math.random() * w, y: Math.random() * h });

            // Cache important values for much quick access.
            el.charge = el.get('charge') || this.get('charge');
            el.weight = el.get('weight') || 1;
            var pos = el.get('position');
            el.x = pos.x;
            el.y = pos.y;
            
            // Previous position.
            el.px = el.x;
            el.py = el.y;

            el.fx = 0;
            el.fy = 0;

        }, this);

        _.each(this.links, function(link) {

            // Cache important values for quick access.
            link.strength = link.get('strength') || this.get('linkStrength');
            link.distance = link.get('distance') || this.get('linkDistance');
            
            link.source = this.cells.get(link.get('source').id);
            link.target = this.cells.get(link.get('target').id);

        }, this);
    },

    step: function() {

        if ((this.t * .99) < 0.005) return this.notifyEnd();
        
        var w = this.width;
        var h = this.height;
        
        var gravity = .1;
        var gravityCenter = this.gravityCenter;
        
        var energyBefore = this.energy;
        this.energy = 0;
        
        // Global positions update. Sum of all the position updates to elements.
        var xBefore = 0
        var yBefore = 0;
        var xAfter = 0;
        var yAfter = 0;

        var i, j;
        var nElements = this.elements.length;
        var nLinks = this.links.length;

        // Calculate repulsive forces.
        for (i = 0; i < nElements - 1; i++) {

            var v = this.elements[i];
            xBefore += v.x;
            yBefore += v.y;
            
            for (j = i + 1; j < nElements; j++) {

                var u = this.elements[j];

                var dx = u.x - v.x;
                var dy = u.y - v.y;
                var distanceSquared = dx*dx + dy*dy;
                var distance = Math.sqrt(distanceSquared);

                var fr = this.t * v.charge / distanceSquared;
                var fx = fr * dx;
                var fy = fr * dy;

                v.fx -= fx;
                v.fy -= fy;
                u.fx += fx;
                u.fy += fy;

                this.energy += fx*fx + fy*fy;
            }
        }

        // Add the last element positions as it couldn't be done in the loops above.
        xBefore += this.elements[nElements - 1].x;
        yBefore += this.elements[nElements - 1].y;
        
        // Calculate attractive forces.
        for (i = 0; i < nLinks; i++) {

            var link = this.links[i];

            var v = link.source;
            var u = link.target;

            var dx = u.x - v.x;
            var dy = u.y - v.y;
            var distanceSquared = dx*dx + dy*dy;
            var distance = Math.sqrt(distanceSquared);

            var fa = this.t * link.strength * (distance - link.distance) / distance;
            var fx = fa * dx;
            var fy = fa * dy;

            var k = v.weight / (v.weight + u.weight);

            // Gauss-seidel. Changing positions directly so that other iterations work with the new positions.
            v.x += fx * (1 - k);
            v.y += fy * (1 - k);
            u.x -= fx * k;
            u.y -= fy * k;

            this.energy += fx*fx + fy*fy;
        }

        
        // Set positions on elements.
        for (i = 0; i < nElements; i++) {

            var el = this.elements[i];
            var pos = { x: el.x, y: el.y };
            
            // Gravity force.
            if (gravityCenter) {

                pos.x += (gravityCenter.x - pos.x) * this.t * gravity;
                pos.y += (gravityCenter.y - pos.y) * this.t * gravity;
            }
        
            pos.x += el.fx;
            pos.y += el.fy;

            // Make sure positions don't go out of the paper area.
            pos.x = Math.max(0, Math.min(w, pos.x));
            pos.y = Math.max(0, Math.min(h, pos.y));

            // Position Verlet integration. 
            var friction = .9;
            pos.x += (el.px - pos.x) * friction;
            pos.y += (el.py - pos.y) * friction;
            
            el.px = pos.x;
            el.py = pos.y;
            
            el.fx = el.fy = 0;
            el.x = pos.x;
            el.y = pos.y;

            xAfter += el.x;
            yAfter += el.y;

            this.notify(el, i, pos);
        }

        this.t = this.cool(this.t, this.energy, energyBefore);

        // If the global distance hasn't change much, the layout converged and therefore trigger the `end` event.
        var gdx = xBefore - xAfter;
        var gdy = yBefore - yAfter;
        var gd = Math.sqrt(gdx*gdx + gdy*gdy);
        if (gd < 1) {
            this.notifyEnd();
        }
    },

    cool: function(t, energy, energyBefore) {

        // Adaptive cooling scheme (as per Yifan Hu). The temperature can also increase depending on the progress made.
        if (energy < energyBefore) {

            this.progress += 1;
            if (this.progress >= 5) {

                this.progress = 0;
                return t / .99;    // Warm up.
            }
        } else {

            this.progress = 0;
            return t * .99;      // Cool down.
        }
        return t;       // Keep the same temperature.
    },

    notify: function(el, i, pos) {

        el.set('position', pos);
    },

    notifyEnd: function() {

        this.trigger('end');
    }
});
