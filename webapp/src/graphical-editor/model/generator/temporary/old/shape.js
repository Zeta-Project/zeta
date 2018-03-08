//OLD
// if (typeof exports === 'object') {
//
//     var joint = {
//         util: require('jointjs/src/core/').util,
//         shapes: {
//             basic: require('jointjs/plugins/shapes/joint.shapes.basic')
//         },
//         dia: {
//             ElementView: require('jointjs/src/joint.dia.element').ElementView,
//             Link: require('jointjs/src/joint.dia.link').Link
//         }
//     };
// }

import joint from 'jointjs';

joint.shapes.zeta = {};

joint.shapes.zeta.rootShape = joint.shapes.basic.Generic.extend({
    markup:  '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.rootShape',
        'init-size': {
            width: 0,
            height: 0},
        size: {
            width: 0, height: 0
        },




        resize:{
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box':{
                height: 0,
                width: 0
            },

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.klasse = joint.shapes.basic.Generic.extend({
    markup:  '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="0b800d24-7d92-4fad-89df-3203d277fe4f" /><text class="e477df6c-e8da-462e-9dc3-2f88d830547f text1" > </text><rect class="d62b0c84-6348-4cd3-b308-508d25012db9" /><text class="ade81ec0-d7d0-44e0-ab85-0e6253c45bc1 text2" > </text><rect class="cc40a695-e82d-4ea7-b5e6-86ac2ae249bd" /><text class="90a7d93a-5efd-4c40-a90f-e74a9f76bfe3 text3" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.klasse',
        'init-size': {
            width: 200,
            height: 250},
        size: {
            width: 64, height: 80
        },




        resize:{
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box':{
                height: 250,
                width: 200
            },

            '.0b800d24-7d92-4fad-89df-3203d277fe4f':{


                x: 0,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.e477df6c-e8da-462e-9dc3-2f88d830547f':{


                x: 0,
                y: 0,

                'id' : 'text1',
                'width': 10,
                'height': 40,
                text: ["Klasse"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.d62b0c84-6348-4cd3-b308-508d25012db9':{


                x: 0,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.ade81ec0-d7d0-44e0-ab85-0e6253c45bc1':{


                x: 0,
                y: 50,

                'id' : 'text2',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.cc40a695-e82d-4ea7-b5e6-86ac2ae249bd':{


                x: 0,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.90a7d93a-5efd-4c40-a90f-e74a9f76bfe3':{


                x: 0,
                y: 150,

                'id' : 'text3',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.abstractKlasse = joint.shapes.basic.Generic.extend({
    markup:  '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb" /><text class="b5762097-dfcf-41a9-8b11-2190c618e6e9 text11" > </text><rect class="bd0fa679-b080-4d84-9eeb-fe7ae99a42cd" /><text class="0685d1f3-9273-42f9-b15f-34ea4a6be378 text21" > </text><rect class="8586b658-768a-4273-b366-d4f1597c561e" /><text class="60cee325-f76b-4d41-b08f-e51427aadf66 text31" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.abstractKlasse',
        'init-size': {
            width: 210,
            height: 250},
        size: {
            width: 67, height: 80
        },




        resize:{
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box':{
                height: 250,
                width: 210
            },

            '.f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb':{


                x: 10,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.b5762097-dfcf-41a9-8b11-2190c618e6e9':{


                x: 10,
                y: 0,

                'id' : 'text11',
                'width': 10,
                'height': 40,
                text: ["<<AbstractClass>>"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.bd0fa679-b080-4d84-9eeb-fe7ae99a42cd':{


                x: 10,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.0685d1f3-9273-42f9-b15f-34ea4a6be378':{


                x: 10,
                y: 50,

                'id' : 'text21',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.8586b658-768a-4273-b366-d4f1597c561e':{


                x: 10,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.60cee325-f76b-4d41-b08f-e51427aadf66':{


                x: 10,
                y: 150,

                'id' : 'text31',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.zeta.interface = joint.shapes.basic.Generic.extend({
    markup:  '<g class="rotatable"><g class="scalable"><rect class="bounding-box" /><rect class="9461a54c-fbb2-49a2-94ac-77848fbc1f88" /><text class="418aa18b-d386-4d43-b74e-9b0701ef2dee text113" > </text><rect class="73e17224-4508-463a-a388-c299a5adde76" /><text class="5215ddbc-bcb1-414d-878c-4cea63c06ff5 text213" > </text><rect class="75f2205e-9a60-45e9-9d33-98e84de80d66" /><text class="2636f960-1374-46ab-b6a0-fc8e2cb3d80d text313" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.interface',
        'init-size': {
            width: 210,
            height: 250},
        size: {
            width: 67, height: 80
        },




        resize:{
            horizontal: true,
            vertical: true,
            proportional: true
        },
        attrs: {
            'rect.bounding-box':{
                height: 250,
                width: 210
            },

            '.9461a54c-fbb2-49a2-94ac-77848fbc1f88':{


                x: 10,
                y: 0,

                width: 200,
                height: 50

            }
            ,
            'text.418aa18b-d386-4d43-b74e-9b0701ef2dee':{


                x: 10,
                y: 0,

                'id' : 'text113',
                'width': 10,
                'height': 40,
                text: ["<<Interface>>"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.73e17224-4508-463a-a388-c299a5adde76':{


                x: 10,
                y: 50,

                width: 200,
                height: 100

            }
            ,
            'text.5215ddbc-bcb1-414d-878c-4cea63c06ff5':{


                x: 10,
                y: 50,

                'id' : 'text213',
                'width': 10,
                'height': 40,
                text: ["Attribute"] // Is overwritten in stencil, but needed here for scaling

            }
            ,
            '.75f2205e-9a60-45e9-9d33-98e84de80d66':{


                x: 10,
                y: 150,

                width: 200,
                height: 100

            }
            ,
            'text.2636f960-1374-46ab-b6a0-fc8e2cb3d80d':{


                x: 10,
                y: 150,

                'id' : 'text313',
                'width': 10,
                'height': 40,
                text: ["Methoden"] // Is overwritten in stencil, but needed here for scaling

            }

        },
        compartments: []
    }, joint.dia.Element.prototype.defaults)
});

