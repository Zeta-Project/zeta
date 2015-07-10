/*! Rappid - the diagramming toolkit

 Copyright (c) 2014 client IO

 2014-04-14 


 This Source Code Form is subject to the terms of the Rappid Academic License
 , v. 1.0. If a copy of the Rappid License was not distributed with this
 file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


if (typeof exports === 'object') {

    var joint = {
        util : require('../src/core').util,
        shapes : {
            basic : require('./joint.shapes.basic')
        },
        dia : {
            ElementView : require('../src/joint.dia.element').ElementView,
            Link : require('../src/joint.dia.link').Link
        }
    };
    var _ = require('lodash');
}

joint.shapes.uml = {};

joint.shapes.uml.Class = joint.shapes.basic.Generic.extend({

    markup : [
        '<g class="rotatable">',
        '<g class="scalable">',
        '<rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/>',
        '</g>',
        '<text class="uml-class-name-text"/><text class="uml-class-attrs-text"/>',
        '</g>'
    ].join(''),

    defaults : joint.util.deepSupplement({

        type : 'uml.Class',

        attrs : {
            rect : {'width' : 200},

            '.uml-class-name-rect' : {'stroke' : 'none', 'stroke-width' : 1, 'fill' : '#3498db'},
            '.uml-class-attrs-rect' : {'stroke' : 'none', 'stroke-width' : 1, 'fill' : '#2980b9'},

            '.uml-class-name-text' : {
                'ref' : '.uml-class-name-rect',
                'ref-y' : .5,
                'ref-x' : .5,
                'text-anchor' : 'middle',
                'y-alignment' : 'middle',
                'font-weight' : 'bold',
                'fill' : 'black',
                'font-size' : 12,
                'font-family' : 'Times New Roman'
            },
            '.uml-class-attrs-text' : {
                'ref' : '.uml-class-attrs-rect', 'ref-y' : 5, 'ref-x' : 5,
                'fill' : 'black', 'font-size' : 12, 'font-family' : 'Times New Roman'
            }
        },

        name : [],
        m_attributes : []

    }, joint.shapes.basic.Generic.prototype.defaults),

    initialize : function () {

        _.bindAll(this, 'updateRectangles');

        this.on('change:name change:m_attributes', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        });

        this.updateRectangles();

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    getClassName : function () {
        return this.get('name');
    },

    updateRectangles : function () {
        var lines;
        var rectHeight;
        var attrs = this.get('attrs');

        var rects = {
            name : this.getClassName(),
            attrs : this.get('m_attributes')
        };

        var offsetY = 0;

        _.each(rects, function (text, type) {

            lines = [];

            if (_.isArray(text)) {
                _.each(text, function (obj) {
                    if (_.isObject(obj)) {
                        lines.push(obj.name || '');
                    } else {
                        lines.push(obj);
                    }
                });
            } else {
                lines.push(text);
            }

            rectHeight = lines.length * 20 + 20;

            attrs['.uml-class-' + type + '-text'].text = lines.join('\n');
            attrs['.uml-class-' + type + '-rect'].height = rectHeight;
            attrs['.uml-class-' + type + '-rect'].transform = 'translate(0,' + offsetY + ')';

            offsetY += rectHeight;

        });

    }

});

joint.shapes.uml.ClassView = joint.dia.ElementView.extend({

    initialize : function () {

        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.model.on('uml-update', _.bind(function () {
            this.update();
            this.resize();
        }, this));
    }
});

joint.shapes.uml.Abstract = joint.shapes.uml.Class.extend({

    defaults : joint.util.deepSupplement({
        type : 'uml.Abstract',
        attrs : {
            '.uml-class-name-rect' : {fill : '#e74c3c'},
            '.uml-class-attrs-rect' : {fill : '#c0392b'}
        }
    }, joint.shapes.uml.Class.prototype.defaults),

    getClassName : function () {
        return ['<<Abstract>>', this.get('name')];
    }

});
joint.shapes.uml.AbstractView = joint.shapes.uml.ClassView;

joint.shapes.uml.Generalization = joint.dia.Link.extend({
    defaults : {
        type : 'uml.Generalization',
        attrs : {'.marker-target' : {d : 'M 20 0 L 0 10 L 20 20 z', fill : 'white'}},
        name : 'Generalization'
    }
});

joint.shapes.uml.Aggregation = joint.dia.Link.extend({
    defaults : {
        type : 'uml.Aggregation',
        attrs : {'.marker-source' : {d : 'M 40 10 L 20 20 L 0 10 L 20 0 z', fill : 'white'}},
        name : 'Aggregation',
        sourceDeletionDeletesTarget : false,
        targetDeletionDeletesSource : false
    }
});

joint.shapes.uml.Composition = joint.dia.Link.extend({
    defaults : {
        type : 'uml.Composition',
        attrs : {'.marker-source' : {d : 'M 40 10 L 20 20 L 0 10 L 20 0 z', fill : 'black'}},
        name : 'Composition',
        sourceDeletionDeletesTarget : true,
        targetDeletionDeletesSource : false
    }
});

joint.shapes.uml.Association = joint.dia.Link.extend({
    defaults : {
        type : 'uml.Association',
        attrs : {'.marker-target' : {d : 'M 10 0 L 0 5 L 10 10 M 0 5 L 10 5', fill : 'none'}},
        name : 'Association',
        sourceDeletionDeletesTarget : false,
        targetDeletionDeletesSource : false
    }
});


if (typeof exports === 'object') {

    module.exports = joint.shapes.uml;
}
