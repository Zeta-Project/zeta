var InspectorDefs = {

    'link': {

        inputs: {
            attrs: {
                '.connection': {
                    'stroke-width': { type: 'range', min: 0, max: 50, defaultValue: 1, unit: 'px', group: 'connection', label: 'stroke width', index: 1 },
                    'stroke': { type: 'color', group: 'connection', label: 'stroke color', index: 2 },
                    'stroke-dasharray': { type: 'select', options: ['0', '1', '5,5', '5,10', '10,5', '5,1', '15,10,5,10,15'], group: 'connection', label: 'stroke dasharray', index: 3 }
                },
                '.marker-source': {
                    transform: { type: 'range', min: 1, max: 15, unit: 'x scale', defaultValue: 'scale(1)', valueRegExp: '(scale\\()(.*)(\\))', group: 'marker-source', label: 'source arrowhead size', index: 1 },
                    fill: { type: 'color', group: 'marker-source', label: 'soure arrowhead color', index: 2 }
                },
                '.marker-target': {
                    transform: { type: 'range', min: 1, max: 15, unit: 'x scale', defaultValue: 'scale(1)', valueRegExp: '(scale\\()(.*)(\\))', group: 'marker-target', label: 'target arrowhead size', index: 1 },
                    fill: { type: 'color', group: 'marker-target', label: 'target arrowhead color', index: 2 }
                }
            },
            smooth: { type: 'toggle', group: 'connection', index: 4 },
            manhattan: { type: 'toggle', group: 'connection', index: 5 },
            labels: {
                type: 'list',
                group: 'labels',
                attrs: {
                    label: { 'data-tooltip': 'Set (possibly multiple) labels for the link' }
                },
                item: {
                    type: 'object',
                    properties: {
                        position: { type: 'range', min: 0.1, max: .9, step: .1, defaultValue: .5, label: 'position', index: 2, attrs: { label: { 'data-tooltip': 'Position the label relative to the source of the link' } } },
                        attrs: {
                            text: {
                                text: { type: 'text', label: 'text', defaultValue: 'label', index: 1, attrs: { label: { 'data-tooltip': 'Set text of the label' } } }
                            }
                        }
                    }
                }
            }

        },
        groups: {
            labels: { label: 'Labels', index: 1 },
            'connection': { label: 'Connection', index: 2 },
            'marker-source': { label: 'Source marker', index: 3 },
            'marker-target': { label: 'Target marker', index: 4 }
        }
    },

    // Basic
    // -----

    'basic.Rect': {

        inputs: _.extend({
            attrs: {
                text: inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                rect: inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 3 },
                    rx: { group: 'presentation', index: 4 },
                    ry: { group: 'presentation', index: 5 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },
    
    'basic.Circle': {

        inputs: _.extend({
            attrs: {
                text: inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                circle: inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { type: 'select', options: ['0', '1', '5,5', '5,10', '10,5', '5,1', '15,10,5,10,15'], group: 'presentation', index: 3 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },
    
    'basic.Image': {

        inputs: _.extend({
            attrs: {
                text: inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-dy': { group: 'text', index: 9 }
                }),
                image: inp({
                    'xlink:href': { group: 'presentation', index: 1 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    // DEVS
    // ----
    
    'devs.Atomic': {
        
        inputs: _.extend({
            attrs: {
                '.label': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                rect: inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { min: 0, max: 30, defaultValue: 1, unit: 'px', group: 'presentation', index: 2 },
                    'stroke-dasharray': { group: 'presentation', index: 3 },
                    'rx': { group: 'presentation', index: 4 },
                    'ry': { group: 'presentation', index: 5 }
                }),
                '.inPorts circle': inp({
                    fill: { group: 'presentation', index: 6, label: 'Input ports fill color' }
                }),
                '.outPorts circle': inp({
                    fill: { group: 'presentation', index: 7, label: 'Output ports fill color' }
                })
            },
            inPorts: { type: 'list', item: { type: 'text' }, group: 'data', index: -2 },
            outPorts: { type: 'list', item: { type: 'text' }, group: 'data', index: -1 }
            
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    // FSA
    // ---

    'fsa.StartState': {

        inputs: _.extend({
            attrs: {
                circle: inp({
                    fill: { group: 'presentation', index: 1 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'fsa.EndState': {

        inputs: _.extend({
            attrs: {
                '.outer': inp({
                    fill: { group: 'presentation', index: 1, label: 'Outer circle fill color' },
                    'stroke-dasharray': { group: 'presentation', index: 2, label: 'Outer circle stroke dasharray' }
                }),
                '.inner': inp({
                    fill: { group: 'presentation', index: 3, label: 'Inner circle fill color' }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },
    
    'fsa.State': {

        inputs: _.extend({
            attrs: {
                text: inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                circle: inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { type: 'select', options: ['0', '1', '5,5', '5,10', '10,5', '5,1', '15,10,5,10,15'], group: 'presentation', index: 3 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    // PN
    // --
    
    'pn.Place': {

        inputs: _.extend({
            attrs: {
                '.label': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9, min: -50, max: 0, step: 1 }
                }),
                '.root': inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { type: 'select', options: ['0', '1', '5,5', '5,10', '10,5', '5,1', '15,10,5,10,15'], group: 'presentation', index: 3 }
                })
            },
            tokens: { type: 'number', min: 1, max: 500, group: 'data', index: 1 }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'pn.Transition': {

        inputs: _.extend({
            attrs: {
                '.label': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9, min: -50, max: 0, step: 1 }
                }),
                rect: inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 3 },
                    rx: { group: 'presentation', index: 4 },
                    ry: { group: 'presentation', index: 5 }
                })
            }
        }, CommonInspectorInputs),

        groups: CommonInspectorGroups
    },

    // ERD
    // ---

    'erd.Entity': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },
    
    'erd.WeakEntity': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'outer', index: 1 },
                    stroke: { group: 'outer', index: 2 },
                    'stroke-width': { group: 'outer', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'outer', index: 4 }
                }),
                '.inner': inp({
                    fill: { group: 'inner', index: 1 },
                    stroke: { group: 'inner', index: 2 },
                    'stroke-width': { group: 'inner', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'inner', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            text: { label: 'Text', index: 1 },
            outer: { label: 'Outer polygon', index: 2 },
            inner: { label: 'Inner polygon', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    'erd.Relationship': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'outer', index: 1 },
                    stroke: { group: 'outer', index: 2 },
                    'stroke-width': { group: 'outer', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'outer', index: 4 }
                }),
                '.inner': inp({
                    fill: { group: 'inner', index: 1 },
                    stroke: { group: 'inner', index: 2 },
                    'stroke-width': { group: 'inner', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'inner', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            text: { label: 'Text', index: 1 },
            outer: { label: 'Outer polygon', index: 2 },
            inner: { label: 'Inner polygon', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },
    
    'erd.IdentifyingRelationship': {
        
        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'outer', index: 1 },
                    stroke: { group: 'outer', index: 2 },
                    'stroke-width': { group: 'outer', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'outer', index: 4 }
                }),
                '.inner': inp({
                    fill: { group: 'inner', index: 1 },
                    stroke: { group: 'inner', index: 2 },
                    'stroke-width': { group: 'inner', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'inner', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            text: { label: 'Text', index: 1 },
            outer: { label: 'Outer polygon', index: 2 },
            inner: { label: 'Inner polygon', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    'erd.Key': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },
    
    'erd.Normal': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'erd.Multivalued': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'outer', index: 1 },
                    stroke: { group: 'outer', index: 2 },
                    'stroke-width': { group: 'outer', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'outer', index: 4 }
                }),
                '.inner': inp({
                    fill: { group: 'inner', index: 1 },
                    stroke: { group: 'inner', index: 2 },
                    'stroke-width': { group: 'inner', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'inner', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            text: { label: 'Text', index: 1 },
            outer: { label: 'Outer ellipse', index: 2 },
            inner: { label: 'Inner ellipse', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    'erd.Derived': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                '.outer': inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    'erd.ISA': {

        inputs: _.extend({
            attrs: {
                'text': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9 }
                }),
                'polygon': inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 3, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 4 }
                })
            }
        }, CommonInspectorInputs),
        groups: CommonInspectorGroups
    },

    // UML
    // ---

    'uml.Class': {

        inputs: _.extend({
            attrs: {
                '.uml-class-name-text': inp({
                    'font-size': { group: 'name', index: 2 },
                    'font-family': { group: 'name', index: 3 }
                }),
                '.uml-class-attrs-text': inp({
                    'font-size': { group: 'attributes', index: 2 },
                    'font-family': { group: 'attributes', index: 3 }
                }),
                '.uml-class-methods-text': inp({
                    'font-size': { group: 'methods', index: 2 },
                    'font-family': { group: 'methods', index: 3 }
                }),
                '.uml-class-name-rect': inp({
                    fill: { group: 'name', index: 4 },
                    'stroke-width': { group: 'name', index: 5, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'name', index: 6 },
                    rx: { group: 'name', index: 7 },
                    ry: { group: 'name', index: 8 }
                }),
                '.uml-class-attrs-rect': inp({
                    fill: { group: 'attributes', index: 4 },
                    'stroke-width': { group: 'attributes', index: 5, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'attributes', index: 6 },
                    rx: { group: 'attributes', index: 7 },
                    ry: { group: 'attributes', index: 8 }
                }),
                '.uml-class-methods-rect': inp({
                    fill: { group: 'methods', index: 4 },
                    'stroke-width': { group: 'methods', index: 5, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'methods', index: 6 },
                    rx: { group: 'methods', index: 7 },
                    ry: { group: 'methods', index: 8 }
                })
            },
            name: { type: 'text', group: 'name', index: 1, label: 'Class name' },
            attributes: { type: 'list', item: { type: 'text' }, group: 'attributes', index: 1, label: 'Attributes' },
            methods: { type: 'list', item: { type: 'text' }, group: 'methods', index: 1, label: 'Methods' }
        }, CommonInspectorInputs),
        groups: {
            name: { label: 'Class name', index: 1 },
            attributes: { label: 'Attributes', index: 2 },
            methods: { label: 'Methods', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },
    
    'uml.Interface': {

        inputs: _.extend({
            attrs: {
                '.uml-class-name-rect': inp({
                    fill: { group: 'name', index: 1 },
                    'stroke-width': { group: 'name', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'name', index: 3 },
                    rx: { group: 'name', index: 4 },
                    ry: { group: 'name', index: 5 }
                }),
                '.uml-class-attrs-rect': inp({
                    fill: { group: 'attributes', index: 1 },
                    'stroke-width': { group: 'attributes', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'attributes', index: 3 },
                    rx: { group: 'attributes', index: 4 },
                    ry: { group: 'attributes', index: 5 }
                }),
                '.uml-class-methods-rect': inp({
                    fill: { group: 'methods', index: 1 },
                    'stroke-width': { group: 'methods', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'methods', index: 3 },
                    rx: { group: 'methods', index: 4 },
                    ry: { group: 'methods', index: 5 }
                })
            },
            name: { type: 'text', group: 'name', index: 0, label: 'Interface name' },
            attributes: { type: 'list', item: { type: 'text' },  group: 'attributes', index: 0, label: 'Attributes' },
            methods: { type: 'list', item: { type: 'text' }, group: 'methods', index: 0, label: 'Methods' }
        }, CommonInspectorInputs),
        groups: {
            name: { label: 'Interface name', index: 1 },
            attributes: { label: 'Attributes', index: 2 },
            methods: { label: 'Methods', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    'uml.Abstract': {

        inputs: _.extend({
            attrs: {
                '.uml-class-name-rect': inp({
                    fill: { group: 'name', index: 1 },
                    'stroke-width': { group: 'name', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'name', index: 3 },
                    rx: { group: 'name', index: 4 },
                    ry: { group: 'name', index: 5 }
                }),
                '.uml-class-attrs-rect': inp({
                    fill: { group: 'attributes', index: 1 },
                    'stroke-width': { group: 'attributes', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'attributes', index: 3 },
                    rx: { group: 'attributes', index: 4 },
                    ry: { group: 'attributes', index: 5 }
                }),
                '.uml-class-methods-rect': inp({
                    fill: { group: 'methods', index: 1 },
                    'stroke-width': { group: 'methods', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'methods', index: 3 },
                    rx: { group: 'methods', index: 4 },
                    ry: { group: 'methods', index: 5 }
                })
            },
            name: { type: 'text', group: 'name', index: 0, label: 'Abstract class name' },
            attributes: { type: 'list', item: { type: 'text' }, group: 'attributes', index: 0, label: 'Attributes' },
            methods: { type: 'list', item: { type: 'text' }, group: 'methods', index: 0, label: 'Methods' }
        }, CommonInspectorInputs),
        groups: {
            name: { label: 'Abstract class name', index: 1 },
            attributes: { label: 'Attributes', index: 2 },
            methods: { label: 'Methods', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    'uml.State': {

        inputs: _.extend({
            attrs: {
                '.uml-state-name': inp({
                    text: { group: 'text', index: 1 },
                    'font-size': { group: 'text', index: 2 },
                    'font-family': { group: 'text', index: 3 },
                    'font-weight': { group: 'text', index: 4 },
                    fill: { group: 'text', index: 5 },
                    stroke: { group: 'text', index: 6 },
                    'stroke-width': { group: 'text', index: 7 },
                    'ref-x': { group: 'text', index: 8 },
                    'ref-y': { group: 'text', index: 9, min: -20, max: 20, step: 1 }
                }),
                rect: inp({
                    fill: { group: 'presentation', index: 1 },
                    stroke: { group: 'presentation', index: 2 },
                    'stroke-width': { group: 'presentation', index: 4, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 5 },
                    rx: { group: 'presentation', index: 6 },
                    ry: { group: 'presentation', index: 7 }
                }),
                path: inp({
                    stroke: { group: 'presentation', index: 3, label: 'Horizontal rule stroke color' }
                }),
                '.uml-state-events': inp({
                    text: { group: 'events', index: 1 },
                    'font-size': { group: 'events', index: 2 },
                    'font-family': { group: 'events', index: 3 },
                    'font-weight': { group: 'events', index: 4 },
                    fill: { group: 'events', index: 5 },
                    stroke: { group: 'events', index: 6 },
                    'stroke-width': { group: 'events', index: 7 },
                    'ref-x': { group: 'events', index: 8 },
                    'ref-y': { group: 'events', index: 9, min: -20, max: 20, step: 1 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            text: { label: 'State name text', index: 1 },
            events: { label: 'State events text', index: 2 },
            presentation: { label: 'Presentation', index: 3 },
            geometry: { label: 'Geometry', index: 4 },
            data: { label: 'Data', index: 5 }
        }
    },

    // Org
    // ---

    'org.Member': {
        
        inputs: _.extend({
            attrs: {
                '.rank': inp({
                    text: { group: 'rank', index: 1 },
                    'font-size': { group: 'rank', index: 2 },
                    'font-family': { group: 'rank', index: 3 },
                    'font-weight': { group: 'rank', index: 4 },
                    fill: { group: 'rank', index: 5 },
                    stroke: { group: 'rank', index: 6 },
                    'stroke-width': { group: 'rank', index: 7 },
                    'ref-x': { group: 'rank', index: 8 },
                    'ref-y': { group: 'rank', index: 9 }
                }),
                '.name': inp({
                    text: { group: 'name', index: 1 },
                    'font-size': { group: 'name', index: 2 },
                    'font-family': { group: 'name', index: 3 },
                    'font-weight': { group: 'name', index: 4 },
                    fill: { group: 'name', index: 5 },
                    stroke: { group: 'name', index: 6 },
                    'stroke-width': { group: 'name', index: 7 },
                    'ref-x': { group: 'name', index: 8 },
                    'ref-y': { group: 'name', index: 9 }
                }),
                '.card': inp({
                    fill: { group: 'presentation', index: 1 },
                    'stroke-width': { group: 'presentation', index: 2, min: 0, max: 30, defaultValue: 1 },
                    'stroke-dasharray': { group: 'presentation', index: 3 },
                    rx: { group: 'presentation', index: 4 },
                    ry: { group: 'presentation', index: 5 }
                }),
                image: inp({
                    'xlink:href': { group: 'photo', index: 1 }
                })
            }
        }, CommonInspectorInputs),
        groups: {
            rank: { label: 'Rank', index: 1 },
            name: { label: 'Name', index: 2 },
            photo: { label: 'Photo', index: 3 },
            presentation: { label: 'Presentation', index: 4 },
            geometry: { label: 'Geometry', index: 5 },
            data: { label: 'Data', index: 6 }
        }
    }
};
