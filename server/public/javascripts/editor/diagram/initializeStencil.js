Stencil.groups = {
    basic: { index: 1, label: 'Basic shapes' },
    fsa: { index: 2, label: 'State machine' },
    pn: { index: 3, label: 'Petri nets' },
    erd: { index: 4, label: 'Entity-relationship' },
    uml: { index: 5, label: 'UML' },
    org: { index: 6, label: 'ORG' }
};

// Stencil.filter = { name: 'dropShadow', args: { dx: 1, dy: 1, blur: 2 } };

Stencil.shapes = {

    basic: [
        new joint.shapes.basic.Rect({
            size: { width: 5, height: 3 },
            attrs: {
                '.': { filter: Stencil.filter },
                rect: {
                    rx: 2, ry: 2, width: 50, height: 30,
                    fill: '#27AE60'
                },
                text: { text: 'rect', fill: 'white', 'font-size': 10, stroke: 'black', 'stroke-width': 0 }
            }
        }),
        new joint.shapes.basic.Circle({
            size: { width: 5, height: 3 },
            attrs: {
                '.': { filter: Stencil.filter },
                circle: { width: 50, height: 30, fill: '#E74C3C' },
                text: { text: 'ellipse', fill: 'white', 'font-size': 10, stroke: 'black', 'stroke-width': 0 }
            }
        }),
        new joint.shapes.devs.Atomic({
            size: { width: 4, height: 3 },
            inPorts: ['in1','in2'],
            outPorts: ['out'],
            attrs: {
                '.': { filter: Stencil.filter },
	        rect: { fill: '#8e44ad', rx: 2, ry: 2 },
                '.label': { text: 'model', fill: 'white', 'font-size': 10, stroke: 'black', 'stroke-width': 0 },
	        '.inPorts circle': { fill: '#f1c40f', opacity: 0.9 },
                '.outPorts circle': { fill: '#f1c40f', opacity: 0.9 },
	        '.inPorts text, .outPorts text': { 'font-size': 9 }
            }
        }),
        new joint.shapes.basic.Image({
            attrs: {
                '.': { filter: Stencil.filter },
                image: { width: 50, height: 50, 'xlink:href': 'data:image/png;base64,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' },
                text: { text: 'image', 'font-size': 9, display: '', stroke: 'black', 'stroke-width': 0 }
            }
        })
    ],
    fsa: [
        new joint.shapes.fsa.StartState({ attrs: { '.': { filter: Stencil.filter } } }),
        new joint.shapes.fsa.EndState({ attrs: { '.': { filter: Stencil.filter } } }),
        new joint.shapes.fsa.State({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'state' } } })
    ],
    pn: [
        new joint.shapes.pn.Place({ tokens: 3, attrs: { '.': { filter: Stencil.filter } } }),
        new joint.shapes.pn.Transition({ attrs: { '.': { filter: Stencil.filter }, '.label': { text: 'transition' }} })
    ],
    erd: [
        new joint.shapes.erd.Entity({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Entity' } } }),
        new joint.shapes.erd.WeakEntity({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Weak entity', 'font-size': 10 } } }),
        new joint.shapes.erd.IdentifyingRelationship({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Relation', 'font-size': 8 } } }),
        new joint.shapes.erd.Relationship({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Relation' } } }),
        new joint.shapes.erd.ISA({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'ISA' } } }),
        new joint.shapes.erd.Key({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Key' } } }),
        new joint.shapes.erd.Normal({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Normal' } } }),
        new joint.shapes.erd.Multivalued({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'MultiValued', 'font-size': 10 } } }),
        new joint.shapes.erd.Derived({ attrs: { '.': { filter: Stencil.filter }, text: { text: 'Derived' } } })
    ],
    uml: [
        new joint.shapes.uml.Class({ name: 'Class', attributes: ['+attr1'], methods: ['-setAttr1()'], attrs: { '.': { filter: Stencil.filter }, '.uml-class-name-text': { 'font-size': 9 }, '.uml-class-attrs-text': { 'font-size': 9 }, '.uml-class-methods-text': { 'font-size': 9 } } }),
        new joint.shapes.uml.Interface({ name: 'Interface', attributes: ['+attr1'], methods: ['-setAttr1()'], attrs: { '.': { filter: Stencil.filter }, '.uml-class-name-text': { 'font-size': 9 }, '.uml-class-attrs-text': { 'font-size': 9 }, '.uml-class-methods-text': { 'font-size': 9 } } }),
        new joint.shapes.uml.Abstract({ name: 'Abstract', attributes: ['+attr1'], methods: ['-setAttr1()'], attrs: { '.': { filter: Stencil.filter }, '.uml-class-name-text': { 'font-size': 9 }, '.uml-class-attrs-text': { 'font-size': 9 }, '.uml-class-methods-text': { 'font-size': 9 } } }),
        new joint.shapes.uml.State({ name: 'State', events: ['entry/\ncreate()'], attrs: { '.': { filter: Stencil.filter }, '.uml-state-name': { 'font-size': 10 }, '.uml-state-events': { 'font-size': 10 } } })
    ],
    org: [
        new joint.shapes.org.Member({ attrs: { '.': { filter: Stencil.filter }, '.rank': { text: 'Rank' }, '.name': { text: 'Name' }, image: { 'xlink:href': 'http://media.screened.com/uploads/0/772/69980-1070155_executive_man_in_suit_cro_icon_icon.jpg' } } })
    ]
};