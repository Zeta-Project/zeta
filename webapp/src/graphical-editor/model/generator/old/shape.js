//OLD
if (typeof exports === 'object') {

    var joint = {
        util: require('../src/core').util,
        shapes: {
            basic: require('./joint.shapes.basic')
        },
        dia: {
            ElementView: require('../src/joint.dia.element').ElementView,
            Link: require('../src/joint.dia.link').Link
        }
    };
}

joint.shapes.zeta = {};


joint.shapes.zeta.rootShape = joint.shapes.basic.Generic.extend({
    markup: '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.rootShape',
        'init-size': {
            width: 0,
            height: 0
        },
        size: {
            width: 0, height: 0
        },


        resize: {
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box': {
                height: 0,
                width: 0
            },

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.klasse = joint.shapes.basic.Generic.extend({
    markup: '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="5c487482-2943-4980-8b65-b14498c354ba" /><text class="5c264f7b-4821-4dd5-9798-70c44b8333ab text1" > </text><rect class="7b5ada00-b651-45cf-a008-915ecd505140" /><text class="5d37c65b-879b-41a9-9afa-391b81d89368 text2" > </text><rect class="f48cfa94-c651-4ff2-a2bb-cb3eec43d065" /><text class="76794e3f-0e79-4f5f-8bf2-c84ba07327e0 text3" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.klasse',
        'init-size': {
            width: 200,
            height: 250
        },
        size: {
            width: 64, height: 80
        },


        resize: {
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box': {
                height: 250,
                width: 200
            },

            '.5c487482-2943-4980-8b65-b14498c354ba': {


                x: 0,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.5c264f7b-4821-4dd5-9798-70c44b8333ab': {


                x: 0,
                y: 0,

                'id': 'text1',
                'width': 10,
                'height': 40,
                text: ["Klasse"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.7b5ada00-b651-45cf-a008-915ecd505140': {


                x: 0,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.5d37c65b-879b-41a9-9afa-391b81d89368': {


                x: 0,
                y: 50,

                'id': 'text2',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.f48cfa94-c651-4ff2-a2bb-cb3eec43d065': {


                x: 0,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.76794e3f-0e79-4f5f-8bf2-c84ba07327e0': {


                x: 0,
                y: 150,

                'id': 'text3',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.abstractKlasse = joint.shapes.basic.Generic.extend({
    markup: '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="b39e7aa2-a142-4dea-9013-52bd0eab784d" /><text class="8ea01922-fbdf-4671-8bce-ce10658c49da text11" > </text><rect class="1780c339-5031-4181-9aa6-53294cfa4310" /><text class="3324620f-e3be-4809-b404-c46be93ece5a text21" > </text><rect class="163911d7-57f3-4f45-a434-e6d431025885" /><text class="7f1a0b19-b973-494a-9f99-cf52011eab4d text31" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.abstractKlasse',
        'init-size': {
            width: 210,
            height: 250
        },
        size: {
            width: 67, height: 80
        },


        resize: {
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box': {
                height: 250,
                width: 210
            },

            '.b39e7aa2-a142-4dea-9013-52bd0eab784d': {


                x: 10,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.8ea01922-fbdf-4671-8bce-ce10658c49da': {


                x: 10,
                y: 0,

                'id': 'text11',
                'width': 10,
                'height': 40,
                text: ["<<AbstractClass>>"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.1780c339-5031-4181-9aa6-53294cfa4310': {


                x: 10,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.3324620f-e3be-4809-b404-c46be93ece5a': {


                x: 10,
                y: 50,

                'id': 'text21',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.163911d7-57f3-4f45-a434-e6d431025885': {


                x: 10,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.7f1a0b19-b973-494a-9f99-cf52011eab4d': {


                x: 10,
                y: 150,

                'id': 'text31',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.interface = joint.shapes.basic.Generic.extend({
    markup: '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="9f393e03-e371-41a9-8065-a62b13991229" /><text class="f01bed45-534c-40c2-a17e-094afb22fc9d text113" > </text><rect class="1d879b3a-cfa3-4955-99ff-6af1f5bf2ced" /><text class="94cca154-a761-4824-9cc2-bb20a53e6cf9 text213" > </text><rect class="d89d5ff7-f9f1-4816-aa59-8d5df9c27424" /><text class="436e2eb9-27e7-493d-a527-fc1bf54616a6 text313" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.interface',
        'init-size': {
            width: 210,
            height: 250
        },
        size: {
            width: 67, height: 80
        },


        resize: {
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box': {
                height: 250,
                width: 210
            },

            '.9f393e03-e371-41a9-8065-a62b13991229': {


                x: 10,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.f01bed45-534c-40c2-a17e-094afb22fc9d': {


                x: 10,
                y: 0,

                'id': 'text113',
                'width': 10,
                'height': 40,
                text: ["<<Interface>>"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.1d879b3a-cfa3-4955-99ff-6af1f5bf2ced': {


                x: 10,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.94cca154-a761-4824-9cc2-bb20a53e6cf9': {


                x: 10,
                y: 50,

                'id': 'text213',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.d89d5ff7-f9f1-4816-aa59-8d5df9c27424': {


                x: 10,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.436e2eb9-27e7-493d-a527-fc1bf54616a6': {


                x: 10,
                y: 150,

                'id': 'text313',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

