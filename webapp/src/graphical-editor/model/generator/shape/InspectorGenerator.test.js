import InspectorGenerator from './InspectorGenerator';
    
describe('InspectorDefs', () => {
    class ShapeGenerator {
        calculateHeight() {

        }

        calculateWidth() {

        }
    }

    function assertGroups(groups) {
        expect(groups).toEqual({
            'presentation': {
                'label': 'Presentation',
                'index': 2
            },
            'geometry': {
                'label': 'Geometry Shape',
                'index': 3
            },
            'data': {
                'label': 'Custom Attributes',
                'index': 4
            }
        });
    }

    function assertInputs(actual, expected) {
        expect(actual).toBeDefined();
        expect(actual.size).toEqual({
            'width': {
                'type': 'number',
                'min': 1, 'max': 500,
                'group': 'geometry',
                'label': 'width',
                'index': 1
            },
            'height': {
                'type': 'number',
                'min': 1,
                'max': 500,
                'group': 'geometry',
                'label': 'height',
                'index': 2
            }
        });
        expect(actual.position).toEqual({
            'x': {
                'type': 'number',
                'min': 1,
                'max': 2000,
                'group': 'geometry',
                'label': 'x',
                'index': 3
            },
            'y': {
                'type': 'number',
                'min': 1,
                'max': 2000,
                'group': 'geometry',
                'label': 'y',
                'index': 4
            }
        });
        expect(actual.attrs).toEqual(expected);
    }

    function assertInputs2(actual) {
        expect(actual).toBeDefined();
        expect(actual.size).toEqual({
            'width': {
                'type': 'number',
                'min': 1, 'max': 500,
                'group': 'geometry',
                'label': 'width',
                'index': 1
            },
            'height': {
                'type': 'number',
                'min': 1,
                'max': 500,
                'group': 'geometry',
                'label': 'height',
                'index': 2
            }
        });
        expect(actual.position).toEqual({
            'x': {
                'type': 'number',
                'min': 1,
                'max': 2000,
                'group': 'geometry',
                'label': 'x',
                'index': 3
            },
            'y': {
                'type': 'number',
                'min': 1,
                'max': 2000,
                'group': 'geometry',
                'label': 'y',
                'index': 4
            }
        });
        expect(actual.attrs).toBeDefined();
    }

    function assertLineElement(actual, group1, group2) {
        expect(actual).toEqual({
            'stroke': {
                'group': group1,
                'index': 2,
                'label': 'Line-Color',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group1,
                'index': 3,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'label': ' Stroke Width Line',
                'type': 'range',
                'step': .5,
                'unit': 'px',
            },
            'stroke-dasharray': {
                'group': group2,
                'index': 4,
                'label': 'Stroke Dash Line',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10',
            }
        });
    }
    function assertPolygonElement(actual, group) {
        expect(actual).toEqual({
            'fill': {
                'group': group,
                'index': 1,
                'label':'Background-Color Polygon',
                'type': 'color',
            },
            'fill-opacity': {
                'group': group,
                'index': 2,
                'label': 'Opacity Polygon',
                'type': 'range',
                'min': 0,
                'max': 1,
                'step': .1,
                'defaultValue': 0,
            },
            'stroke': {
                'group': group,
                'index': 3,
                'label': 'Line-Color Polygon',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group,
                'index': 4,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'label': 'Stroke Width Polygon',
                'type': 'range',
                'step': .5,
                'unit': 'px'
            },
            'stroke-dasharray': {
                'group': group,
                'index': 5,
                'label': 'Stroke Dash Polygon',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10',
            }
        });
    }
    function assertPolyLineElement(actual, group) {
        expect(actual).toEqual({
            'fill': {
                'group': group,
                'index': 1,
                'label': 'Background-Color Polyline',
                'type': 'color',
            },
            'stroke': {
                'group': group,
                'index': 2,
                'label': 'Line-Color',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group,
                'index': 3,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'label': ' Stroke Width Line',
                'type': 'range',
                'step': .5,
                'unit': 'px',
            },
            'stroke-dasharray': {
                'group': group,
                'index': 4,
                'label': 'Stroke Dash Line',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10',
            }
        });
    }
    function assertRoundRectangleSpecific(actual, group) {
        expect(actual).toEqual({
            'fill': {
                'group': group,
                'index': 1,
                'type': 'color',
                'label': 'Fill color',
            },
            'fill-opacity': {
                'group': group,
                'index': 2,
                'label': 'Opacity Rounded Rectangle',
                'type': 'range',
                'min': 0,
                'max': 1,
                'step': .1,
                'defaultValue': 0,
            },
            'stroke': {
                'group': group,
                'index': 3,
                'label': 'Line-Color Rounded Rectangle',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group,
                'index': 4,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'label': 'Stroke Width Rounded Rectangle',
                'type': 'range',
                'step': .5,
                'unit': 'px',
            },
            'stroke-dasharray': {
                'group': group,
                'index': 5,
                'label': 'Stroke Dash Rounded Rectangle',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10',
            },
        });
    }
    function assertRoundRectangleGeneral(actual, height, width, x, y, rx, ry, group) {
        expect(actual).toEqual({
            'rx': {
                'group': group,
                'index': 6,
                'max': rx,
                'label': 'Curve X',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'ry': {
                'group': group,
                'index': 7,
                'max': ry,
                'label': 'Curve Y',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'x': {
                'group': group,
                'index': 1,
                'max': x,
                'label': 'x Position Rounded Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'y': {
                'group': group,
                'index': 2,
                'max': y,
                'label': 'y Position Rounded Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'height': {
                'group': group,
                'index': 3,
                'max': height,
                'label': 'Height Rounded Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'width': {
                'group': group,
                'index': 3,
                'max': width,
                'label': 'Width Rounded Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            }
        });
    }
    function assertTextSpecific(actual, textGroup, x, y, group) {
        expect(actual).toEqual({
            'text': {
                'type': 'list',
                'item': {
                    'type': 'text'
                },
                'group': textGroup,
                'index': 1,
                'label': 'Text',
            },
            'x': {
                'group': group,
                'index': 1,
                'max': x,
                'label': 'x Position Text',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'y': {
                'group': group,
                'index': 2,
                'max': y,
                'label': 'y Position Text',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            }
        });
    }
    function assertTextGeneral(actual, group) {
        expect(actual).toEqual({
            'font-size': {
                'group': group,
                'index': 2,
                'type': 'range',
                'min': 5,
                'max': 80,
                'unit': 'px',
                'label': 'Font size',
            },
            'font-family': {
                'group': group,
                'index': 3,
                'type': 'select',
                'options': ['Arial', 'Helvetica', 'Times New Roman', 'Courier New', 'Georgia', 'Garamond', 'Tahoma', 'Lucida Console', 'Comic Sans MS'],
                'label': 'Font family',
            },
            'font-weight': {
                'group': group,
                'index': 4,
                'type': 'range',
                'min': 100,
                'max': 900,
                'step': 100,
                'defaultValue': 400,
                'label': 'Font weight',
            },
            'fill': {
                'group': group,
                'index': 6,
                'label': 'Text Color',
                'type': 'color',
            },
        });
    }
    function assertRectangleSpecific(actual, group) {
        expect(actual).toEqual({
            'fill': {
                'group': group,
                'index': 1,
                'label': 'Background-Color Rectangle',
                'type': 'color',
            },
            'fill-opacity': {
                'group': group,
                'index': 2,
                'label': 'Opacity Rectangle',
                'type': 'range',
                'min': 0,
                'max': 1,
                'step': .1,
                'defaultValue': 0,
            },
            'stroke': {
                'group': group,
                'index': 3,
                'label': 'Line-Color Rectangle',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group,
                'index': 4,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'type': 'range',
                'step': .5,
                'unit': 'px',
                'label': 'Stroke Width Rectangle',
            },
            'stroke-dasharray': {
                'group': group,
                'index': 5,
                'label': 'Stroke Dash Rectangle',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10'
            }
        });
    }
    function assertRectangleGeneral(actual, height, width, x, y, group) {
        expect(actual).toEqual({
            'x': {
                'group': group,
                'index': 1,
                'max': x,
                'label': 'x Position Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'y': {
                'group': group,
                'index': 2,
                'max': y,
                'label': 'y Position Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'height': {
                'group': group,
                'index': 3,
                'max': height,
                'label': 'Height Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            },
            'width': {
                'group': group,
                'index': 3,
                'max': width,
                'label': 'Width Rectangle',
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
            }
        });
    }
    function assertEllipseSpecific(actual, group) {
        expect(actual).toEqual({
            'fill': {
                'group': group,
                'index': 1,
                'label': 'Background-Color Ellipse',
                'type': 'color',
            },
            'fill-opacity': {
                'group': group,
                'index': 2,
                'label': 'Opacity Ellipse',
                'type': 'range',
                'min': 0,
                'max': 1,
                'step': .1,
                'defaultValue': 0,
            },
            'stroke': {
                'group': group,
                'index': 3,
                'label': 'Line-Color Ellipse',
                'type': 'color',
                'defaultValue': '#000000',
            },
            'stroke-width': {
                'group': group,
                'index': 4,
                'min': 0,
                'max': 30,
                'defaultValue': 1,
                'label': 'Stroke Width Ellipse',
                'type': 'range',
                'step': .5,
                'unit': 'px',
            },
            'stroke-dasharray': {
                'group': group,
                'index': 5,
                'label': 'Stroke Dash Ellipse',
                'type': 'select',
                'options': ['0', '1', '5,5', '5,10', '10,10', '10,5', '3,5', '5,1', '15,10,5,10,15'],
                'defaultValue': '10,10',
            }
        });
    }
    function assertEllipseGeneral(actual, cxMin, cxMax, cyMin, cyMax, rx, ry, group) {
        expect(actual).toEqual({
            'cx': {
                'group': group,
                'index': 1,
                'min': cxMin,
                'max': cxMax,
                'type': 'range',
                'step': 1,
                'defaultValue': 0,
                'label': 'Horizontal position (center)',
            },
            'cy': {
                'group': group,
                'index': 2,
                'min': cyMin,
                'max': cyMax,
                'type': 'range',
                'step': 1,
                'defaultValue': 0,
                'label': 'Vertical position (center)',
            },
            'rx': {
                'group': group,
                'index': 3,
                'max': rx,
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
                'label': 'X-axis radius'
            },
            'ry': {
                'group': group,
                'index': 3,
                'max': ry,
                'type': 'range',
                'min': 0,
                'step': 1,
                'defaultValue': 0,
                'label': 'Y-axis radius'
            },
        });
    }

    test('without shapes', () => {
        const target = new InspectorGenerator({});
        expect(target.InspectorDefs).toEqual({
            'zeta.MLink' : {
                'inputs': {
                    'labels': {
                        'type': 'list',
                        'group': 'labels',
                        'attrs': {
                            'label': {
                                'data-tooltip': 'Set (possibly multiple) labels for the link'
                            }
                        },
                        'item': {
                            'type': 'object',
                            'properties': {
                                'position': {
                                    'type': 'range',
                                    'min': 0.1,
                                    'max': .9,
                                    'step': .1,
                                    'defaultValue': .5,
                                    'label': 'position',
                                    'index': 2,
                                    'attrs': {
                                        'label': {
                                            'data-tooltip': 'Position the label relative to the source of the link'
                                        }
                                    }
                                },
                                'attrs': {
                                    'text': {
                                        'text': {
                                            'type': 'text',
                                            'label': 'text',
                                            'defaultValue': 'label',
                                            'index': 1,
                                            'attrs': {
                                                'label': {
                                                    'data-tooltip': 'Set text of the label'
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                'groups': {
                    'labels': {
                        'label': 'Labels',
                        'index': 1
                    }
                }
            }
        });
    });
    
    test('shape with different elements', () => {
        const target = new InspectorGenerator({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique1',
                            type: 'LINE',
                            style: 'default-style',
                        },
                        {
                            id: 'unique1',
                            type: 'POLYGON',
                            style: 'default-style',
                        }
                    ],
                } 
            ],
        }, new ShapeGenerator());

        const InspectorDefs = target.InspectorDefs;
        expect(InspectorDefs['zeta.MLink']).toBeDefined();
        expect(InspectorDefs['zeta.Shape']).toBeDefined();

        const shape = InspectorDefs['zeta.Shape'];
        assertGroups(shape.groups);
        assertInputs2(shape.inputs);
        assertLineElement(shape.inputs.attrs['line.unique1'], 'Presentation Line 1', 'Presentation 1');
        assertPolygonElement(shape.inputs.attrs['polygon.unique1'], 'Presentation Polygon 1');
    });

    describe('shape with single element', () => {
        test('as ellipse', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'ELLIPSE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertEllipseSpecific(shape.inputs.attrs['ellipse.unique'], 'Presentation Ellipse 1');
            assertEllipseGeneral(shape.inputs.attrs['.unique'], 1, 7, 0.5, 3.5, 4, 2, 'Geometry Ellipse 1');
        });
    
        test('as rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();

            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRectangleSpecific(shape.inputs.attrs['rect.unique'], 'Presentation Rectangle 1');
            assertRectangleGeneral(shape.inputs.attrs['.unique'], 4, 8, 6, 3, 'Geometry Rectangle 1');
        });
    
        test('as text', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'TEXT',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
            
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertTextSpecific(shape.inputs.attrs['text.unique'], 'Text unique', 6, 3, 'Text Geometry 1');
            assertTextGeneral(shape.inputs.attrs['.unique'], 'Text Style 1');
        });
    
        test('as line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'LINE',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertLineElement(shape.inputs.attrs['line.unique'], 'Presentation Line 1', 'Presentation 1');
        });
    
        test('as polygon', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'POLYGON',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolygonElement(shape.inputs.attrs['polygon.unique'], 'Presentation Polygon 1');
        });
    
        test('as poly-line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'POLY_LINE',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolyLineElement(shape.inputs.attrs['polyline.unique'], 'Presentation Polyline 1');
        });
    
        test('as rounded-rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'ROUNDED_RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRoundRectangleSpecific(shape.inputs.attrs['rect.unique'], 'Presentation R-Rectangle 1');
            assertRoundRectangleGeneral(shape.inputs.attrs['.unique'], 4, 8, 6, 3, 1, 0.5, 'Geometry R-Rectangle 1');
        });
    });

    describe('shape with multiple elements',() => {
        test('as ellipse', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'ELLIPSE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            },
                            {
                                id: 'unique2',
                                type: 'ELLIPSE',
                                style: 'default-style',
                                sizeHeight: 4,
                                sizeWidth: 8,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 16;
            generator.calculateWidth = () => 32;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertEllipseSpecific(shape.inputs.attrs['ellipse.unique1'], 'Presentation Ellipse 1');
            assertEllipseGeneral(shape.inputs.attrs['.unique1'], 1, 31, 0.5, 15.5, 16, 8, 'Geometry Ellipse 1');
            assertEllipseSpecific(shape.inputs.attrs['ellipse.unique2'], 'Presentation Ellipse 2');
            assertEllipseGeneral(shape.inputs.attrs['.unique2'], 4, 28, 2, 14, 16, 8, 'Geometry Ellipse 2');
        });
    
        test('as rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            },
                            {
                                id: 'unique2',
                                type: 'RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 16;
            generator.calculateWidth = () => 32;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRectangleSpecific(shape.inputs.attrs['rect.unique1'], 'Presentation Rectangle 1');
            assertRectangleGeneral(shape.inputs.attrs['.unique1'], 16, 32, 30, 15, 'Geometry Rectangle 1');
            assertRectangleSpecific(shape.inputs.attrs['rect.unique2'], 'Presentation Rectangle 2');
            assertRectangleGeneral(shape.inputs.attrs['.unique2'], 16, 32, 30, 15, 'Geometry Rectangle 2');
        });

        test('as text', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'TEXT',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            },
                            {
                                id: 'unique2',
                                type: 'TEXT',
                                style: 'default-style',
                                sizeHeight: 4,
                                sizeWidth: 8,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 16;
            generator.calculateWidth = () => 32;
            
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertTextSpecific(shape.inputs.attrs['text.unique1'], 'Text unique1', 30, 15, 'Text Geometry 1');
            assertTextGeneral(shape.inputs.attrs['.unique1'], 'Text Style 1');
            assertTextSpecific(shape.inputs.attrs['text.unique2'], 'Text unique2', 24, 12, 'Text Geometry 2');
            assertTextGeneral(shape.inputs.attrs['.unique2'], 'Text Style 2');
        });
    
        test('as line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'LINE',
                                style: 'default-style',
                            },
                            {
                                id: 'unique2',
                                type: 'LINE',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertLineElement(shape.inputs.attrs['line.unique1'], 'Presentation Line 1', 'Presentation 1');
            assertLineElement(shape.inputs.attrs['line.unique2'], 'Presentation Line 2', 'Presentation 2');
        });
    
        test('as polygon', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'POLYGON',
                                style: 'default-style',
                            },
                            {
                                id: 'unique2',
                                type: 'POLYGON',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolygonElement(shape.inputs.attrs['polygon.unique1'], 'Presentation Polygon 1');
            assertPolygonElement(shape.inputs.attrs['polygon.unique2'], 'Presentation Polygon 2');
        });
    
        test('as poly-line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'POLY_LINE',
                                style: 'default-style',
                            },
                            {
                                id: 'unique2',
                                type: 'POLY_LINE',
                                style: 'default-style',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolyLineElement(shape.inputs.attrs['polyline.unique1'], 'Presentation Polyline 1');
            assertPolyLineElement(shape.inputs.attrs['polyline.unique2'], 'Presentation Polyline 2');
        });
    
        test('as rounded-rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'ROUNDED_RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            },
                            {
                                id: 'unique2',
                                type: 'ROUNDED_RECTANGLE',
                                style: 'default-style',
                                sizeHeight: 4,
                                sizeWidth: 8,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 16;
            generator.calculateWidth = () => 32;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRoundRectangleSpecific(shape.inputs.attrs['rect.unique1'], 'Presentation R-Rectangle 1');
            assertRoundRectangleGeneral(shape.inputs.attrs['.unique1'], 16, 32, 30, 15, 1, 0.5, 'Geometry R-Rectangle 1');
            assertRoundRectangleSpecific(shape.inputs.attrs['rect.unique2'], 'Presentation R-Rectangle 2');
            assertRoundRectangleGeneral(shape.inputs.attrs['.unique2'], 16, 32, 24, 12, 4, 2, 'Geometry R-Rectangle 2');
        });
    });

    describe('shape element without style',() => {
        test('as ellipse', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'ELLIPSE',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertEllipseSpecific(shape.inputs.attrs['ellipse'], 'Presentation Ellipse 1');
            assertEllipseGeneral(shape.inputs.attrs['.unique1'], 1, 7, 0.5, 3.5, 4, 2, 'Geometry Ellipse 1');
        });
    
        test('as rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'RECTANGLE',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();

            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRectangleSpecific(shape.inputs.attrs['rect'], 'Presentation Rectangle 1');
            assertRectangleGeneral(shape.inputs.attrs['.unique'], 4, 8, 6, 3, 'Geometry Rectangle 1');
        });
    
        test('as text', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'TEXT',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
            
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertTextSpecific(shape.inputs.attrs['text'], 'Text unique', 6, 3, 'Text Geometry 1');
            assertTextGeneral(shape.inputs.attrs['.unique'], 'Text Style 1');
        });
    
        test('as line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'LINE',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertLineElement(shape.inputs.attrs['line'], 'Presentation Line 1', 'Presentation 1');
        });
    
        test('as polygon', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'POLYGON',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolygonElement(shape.inputs.attrs['polygon'], 'Presentation Polygon 1');
        });
    
        test('as poly-line', () => {
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'POLY_LINE',
                            }
                        ],
                    } 
                ],
            }, new ShapeGenerator());
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertPolyLineElement(shape.inputs.attrs['polyline'], 'Presentation Polyline 1');
        });
    
        test('as rounded-rectangle', () => {
            const generator = new ShapeGenerator();
            const target = new InspectorGenerator({ 
                shapes: [ 
                    { 
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'ROUNDED_RECTANGLE',
                                sizeHeight: 1,
                                sizeWidth: 2,
                            }
                        ],
                    } 
                ],
            }, generator);
            generator.calculateHeight = () => 4;
            generator.calculateWidth = () => 8;
    
            const InspectorDefs = target.InspectorDefs;
            expect(InspectorDefs['zeta.MLink']).toBeDefined();
            expect(InspectorDefs['zeta.Shape']).toBeDefined();
    
    
            const shape = InspectorDefs['zeta.Shape'];
            assertGroups(shape.groups);
            assertInputs2(shape.inputs);
            assertRoundRectangleSpecific(shape.inputs.attrs['rect'], 'Presentation R-Rectangle 1');
            assertRoundRectangleGeneral(shape.inputs.attrs['.unique'], 4, 8, 6, 3, 1, 0.5, 'Geometry R-Rectangle 1');
        });
    });
});