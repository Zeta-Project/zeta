/* * This is a generated ShapeFile for JointJS */
if (typeof exports === 'object') {
    var joint = {
            util: require('../src/core').util,
            shapes: {
                basic: require('./joint.shapes.basic')
            }
            ,
            dia: {
                ElementView: require('../src/joint.dia.element').ElementView,
                Link: require('../src/joint.dia.link').Link
            }
        }
    ;
}
joint.shapes.zeta = {};
joint.shapes.zeta.rootShape = joint.shapes.basic.Generic.extend({
    markup: '<g class=\"rotatable\"><g class=\"scalable\"><rect class=\"bounding-box\" /></g></g>',
    defaults: joint.util.deepSupplement({
            type: 'zeta.rootShape',
            'init-size':
                {
                    width: 0,
                    height: 0
                }
            ,
            size: {
                width: 0, height:
                    0
            }
            ,
            resize: {
                horizontal: true, vertical: true, proportional: true
            }
            ,
            attrs: {
                'rect.bounding-box':
                    {
                        height: 0,
                        width: 0
                    }
                ,
            }
            ,
            compartments: []
        },
        joint.dia.Element.prototype.defaults
    )
})
;joint.shapes.zeta.entity = joint.shapes.basic.Generic.extend({
    markup: '<g class=\"rotatable\"><g class=\"scalable\"><rect class=\"bounding-box\" /><rect class=\"bdb2b0bf-32a3-4f1b-ab6f-1d1d3435602c\" /><text class=\"04e89195-b013-49bd-a7ee-b90571b87d9a entityName\" > </text><rect class=\"3142e6f7-d844-430b-a13f-122530c56862\" /><text class=\"99f3e4be-728d-4ddf-a1e6-65df237a8913 fixValues\" > </text><rect class=\"708f3a8f-96e2-4a02-967f-fa26a952ca80\" /><text class=\"f72584e6-c4cc-4bb9-beeb-ef4cf3d40396 inValues\" > </text><rect class=\"832c988f-f4b8-436f-9c44-dd9f09699fa4\" /><text class=\"9c2877f9-21bd-49d4-963e-ac2868be66c7 outValues\" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.entity',
        'init-size':
            {
                width: 200,
                height: 350
            }
        ,
        size: {
            width: 45, height:
                80
        }
        ,
        resize: {
            horizontal: true, vertical: true, proportional: true
        }
        ,
        attrs: {
            'rect.bounding-box':
                {
                    height: 350,
                    width: 200
                }
            ,
            '.bdb2b0bf-32a3-4f1b-ab6f-1d1d3435602c':
                {
                    x: 0, y: 0, width: 200, height: 50
                },
            'text.04e89195-b013-49bd-a7ee-b90571b87d9a':
                {
                    x: 10,
                    y: 5,
                    'id':
                        'entityName',
                    'width':
                        10,
                    'height':
                        40,
                    text: ["Entity"]
// Is overwritten in stencil, but needed here for scaling
            }
            ,
            '.3142e6f7-d844-430b-a13f-122530c56862': {x: 0, y: 50, width: 200, height: 100},
            'text.99f3e4be-728d-4ddf-a1e6-65df237a8913': {
                x: 10,
                y: 55,
                'id': 'fixValues',
                'width': 10,
                'height': 40,
                text: ["fix"]
// Is overwritten in stencil, but needed here for scaling
            }
            ,
            '.708f3a8f-96e2-4a02-967f-fa26a952ca80': {x: 0, y: 150, width: 200, height: 100},
            'text.f72584e6-c4cc-4bb9-beeb-ef4cf3d40396': {
                x: 10, y: 155, 'id': 'inValues', 'width': 10, 'height': 40, text: ["in"]
// Is overwritten in stencil, but needed here for scaling
            }
            , '.832c988f-f4b8-436f-9c44-dd9f09699fa4': {x: 0, y: 250, width: 200, height: 100}, 'text.9c2877f9-21bd-49d4-963e-ac2868be66c7': {
                x: 10, y: 255, 'id': 'outValues', 'width': 10, 'height': 40, text: ["out"]
// Is overwritten in stencil, but needed here for scaling
            }
        }, compartments: []
    }, joint.dia.Element.prototype.defaults)
});
joint.shapes.zeta.periodStart = joint.shapes.basic.Generic.extend({
    markup: '<g class=\"rotatable\"><g class=\"scalable\"><rect class=\"bounding-box\" /><ellipse class=\"c52e2e13-0c39-4276-84c0-256147f4f1c4\" /><text class=\"0946fd95-dec8-4e8a-85e3-02b16c2b09e4 textPeriodStart\" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.periodStart',
        'init-size': {width: 80, height: 80},
        size: {width: 80, height: 80},
        resize: {horizontal: true, vertical: true, proportional: true},
        attrs: {
            'rect.bounding-box': {height: 80, width: 80},
            '.c52e2e13-0c39-4276-84c0-256147f4f1c4': {cx: 40, cy: 40, rx: 40, ry: 40},
            'text.0946fd95-dec8-4e8a-85e3-02b16c2b09e4': {
                x: 3, y: 30, 'id': 'textPeriodStart', 'width': 10, 'height': 40, text: ["Period Start"]
// Is overwritten in stencil, but needed here for scaling
            }
        }, compartments: []
    }, joint.dia.Element.prototype.defaults)
});
joint.shapes.zeta.teamStart = joint.shapes.basic.Generic.extend({
    markup: '<g class=\"rotatable\"><g class=\"scalable\"><rect class=\"bounding-box\" /><ellipse class=\"5370cce1-af7a-43d2-bb82-686cc277c9f3\" /><text class=\"949d3660-0b0a-4671-ac55-14d3acc32dcd textTeamStart\" > </text></g></g>',
    defaults: joint.util.deepSupplement({
        type: 'zeta.teamStart',
        'init-size': {width: 80, height: 80},
        size: {width: 80, height: 80},
        resize: {horizontal: true, vertical: true, proportional: true},
        attrs: {
            'rect.bounding-box': {height: 80, width: 80},
            '.5370cce1-af7a-43d2-bb82-686cc277c9f3': {cx: 40, cy: 40, rx: 40, ry: 40},
            'text.949d3660-0b0a-4671-ac55-14d3acc32dcd': {
                x: 5, y: 30, 'id': 'textTeamStart', 'width': 10, 'height': 40, text: ["Team Start"]
// Is overwritten in stencil, but needed here for scaling
            }
        }, compartments: []
    }, joint.dia.Element.prototype.defaults)
});