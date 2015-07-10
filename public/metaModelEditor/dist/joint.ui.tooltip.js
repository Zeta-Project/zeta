/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


joint.ui.Tooltip = Backbone.View.extend({

    className: 'tooltip',

    options: {
        // `left` allows you to set a selector (or DOM element) that
        // will be used as the left edge of the tooltip. This is useful when configuring a tooltip
        // that should be shown "after" some other element. Other sides are analogous.
        left: undefined,
        right: undefined,
        top: undefined,
        bottom: undefined,
        padding: 10,
        target: undefined,
        rootTarget: undefined
    },

    initialize: function() {

        _.bindAll(this, 'render', 'hide', 'position');

        if (this.options.rootTarget) {
            
            this.$rootTarget = $(this.options.rootTarget);
            
            this.$rootTarget.on('mouseover', this.options.target, this.render);
            this.$rootTarget.on('mouseout', this.options.target, this.hide);
            this.$rootTarget.on('mousedown', this.options.target, this.hide);

        } else {
        
            this.$target = $(this.options.target);
            
            this.$target.on('mouseover', this.render);
            this.$target.on('mouseout', this.hide);
            this.$target.on('mousedown', this.hide);
        }

        this.$el.addClass(this.options.direction);
    },

    remove: function() {

        this.$target.off('mouseover', this.render);
        this.$target.off('mouseout', this.hide);
        this.$target.off('mousedown', this.hide);
        
        Backbone.View.prototype.remove.apply(this, arguments);
    },

    hide: function() {

        Backbone.View.prototype.remove.apply(this, arguments);
    },
    
    render: function(evt) {

        var target;
        var isPoint = !_.isUndefined(evt.x) && !_.isUndefined(evt.y);
        
        if (isPoint) {
            
            target = evt;
            
        } else {

            this.$target = $(evt.target).closest(this.options.target);
            target = this.$target[0];
        }
        
        this.$el.html(_.isFunction(this.options.content) ? this.options.content(target) : this.options.content);
        
        // Hide the element first so that we don't get a jumping effect during the image loading.
        this.$el.hide();
        $(document.body).append(this.$el);

        // If there is an image in the `content`, wait till it's loaded as only after that
        // we know the dimension of the tooltip.
        var $images = this.$('img');
        if ($images.length) {

            $images.on('load', _.bind(function() { this.position(isPoint ? target : undefined); }, this));
            
        } else {

            this.position(isPoint ? target : undefined);
        }
    },

    getElementBBox: function(el) {

        var $el = $(el);
        var offset = $el.offset();
        var bbox;

        // Compensate for the document scroll.
        // Safari uses `document.body.scrollTop` only while Firefox uses `document.documentElement.scrollTop` only.
        // Google Chrome is the winner here as it uses both.
        var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
        var scrollLeft = document.body.scrollLeft || document.documentElement.scrollLeft;
        
        offset.top -= (scrollTop || 0);
        offset.left -= (scrollLeft || 0);
        
        if (el.ownerSVGElement) {

            // Use Vectorizer to get the dimensions of the element if it is an SVG element.
            bbox = V(el).bbox();
            // The `bbox()` returns coordinates relative to the SVG viewport, therefore, use the
            // ones returned from the `offset()` method that are relative to the document.
            bbox.x = offset.left;
            bbox.y = offset.top;
            
        } else {
            
            bbox = { x: offset.left, y: offset.top, width: $el.outerWidth(), height: $el.outerHeight() };
        }

        return bbox;
    },

    position: function(p) {

        var bbox;

        if (p) {
            
            bbox = { x: p.x, y: p.y, width: 1, height: 1 };
            
        } else {

            bbox = this.getElementBBox(this.$target[0]);            
        }
        
        var padding = this.options.padding;

        // Show the tooltip. Do this before we ask for its dimension, otherwise they won't be defined yet.
        this.$el.show();
        
        var height = this.$el.outerHeight();
        var width = this.$el.outerWidth();
        
        // If `options.left` selector or DOM element is defined, we use its right coordinate
        // as a left coordinate for the tooltip. This is useful when you want to tooltip to
        // appear "after" a certain element.
        if (this.options.left) {

            var $left = $(this.options.left);
            var leftBbox = this.getElementBBox($left[0]);
            this.$el.css({
                left: leftBbox.x + leftBbox.width + padding,
                top: bbox.y + bbox.height/2 - height/2
            });
            
        } else if (this.options.right) {

            var $right = $(this.options.right);
            var rightBbox = this.getElementBBox($right[0]);
            this.$el.css({
                left: rightBbox.x - width - padding,
                top: bbox.y + bbox.height/2 - height/2
            });

        } else if (this.options.top) {

            var $top = $(this.options.top);
            var topBbox = this.getElementBBox($top[0]);
            this.$el.css({
                top: topBbox.y + topBbox.height + padding,
                left: bbox.x + bbox.width/2 - width/2
            });

        } else if (this.options.bottom) {

            var $bottom = $(this.options.bottom);
            var bottomBbox = this.getElementBBox($bottom[0]);
            this.$el.css({
                top: bottomBbox.y - height - padding,
                left: bbox.x + bbox.width/2 - width/2
            });
            
        } else {

            this.$el.css({
                left: bbox.x + bbox.width + padding,
                top: bbox.y + bbox.height/2 - height/2
            });
        }
    }
});