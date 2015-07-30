/*! Rappid - the diagramming toolkit

Copyright (c) 2014 client IO

 2014-04-14 


This Source Code Form is subject to the terms of the Rappid Academic License
, v. 1.0. If a copy of the Rappid License was not distributed with this
file, You can obtain one at http://jointjs.com/license/rappid_academic_v1.txt
 or from the Rappid archive as was distributed by client IO. See the LICENSE file.*/


if (typeof exports === 'object') {

    var joint = {
        util: require('../src/core').util,
        shapes: {},
        dia: {
            Element: require('../src/joint.dia.element').Element,
            Link: require('../src/joint.dia.link').Link
        }
    };
}

joint.shapes.org = {};

joint.shapes.org.Member = joint.dia.Element.extend({

    markup: '<g class="rotatable"><g class="scalable"><rect class="card"/><image/></g><text class="rank"/><text class="name"/></g>',

    defaults: joint.util.deepSupplement({

        type: 'org.Member',
        size: { width: 180, height: 70 },
        attrs: {

            rect: { width: 170, height: 60 },

            '.card': {
                fill: '#FFFFFF', stroke: '#000000', 'stroke-width': 2,
                'pointer-events': 'visiblePainted', rx: 10, ry: 10
            },

            image: {
		width: 48, height: 48,
                ref: '.card', 'ref-x': 10, 'ref-y': 5
            },
            
            '.rank': {
                'text-decoration': 'underline',
                ref: '.card', 'ref-x': 0.9, 'ref-y': 0.2,
                'font-family': 'Courier New', 'font-size': 14,
		'text-anchor': 'end'
            },

            '.name': {
                'font-weight': 'bold',
                ref: '.card', 'ref-x': 0.9, 'ref-y': 0.6,
                'font-family': 'Courier New', 'font-size': 14,
		'text-anchor': 'end'
            }
        }
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.org.Arrow = joint.dia.Link.extend({

    defaults: {
        type: 'org.Arrow',
        source: { selector: '.card' }, target: { selector: '.card' },
        attrs: { '.connection': { stroke: '#585858', 'stroke-width': 3 }},
        z: -1
    }
});


if (typeof exports === 'object') {

    module.exports = joint.shapes.org;
}
