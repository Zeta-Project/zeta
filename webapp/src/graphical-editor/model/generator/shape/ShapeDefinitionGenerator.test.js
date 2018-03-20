import ShapeDefinitionGenerator from './ShapeDefinitionGenerator';
jest.mock('jointjs');

describe('joint.shapes.zeta', () => {
    const joint = require('jointjs');
    const Generic = joint.shapes.basic.Generic;

    function assertGenericExtend(mock, expected) {
        expect(mock.calls).toHaveLength(1);
        expect(mock.calls[0]).toHaveLength(1);
        expect(mock.calls[0][0]).toBeDefined();
        expect(mock.calls[0][0].markup).toBeDefined();
        expect(mock.calls[0][0].defaults).toEqual(expected);
    }

    function assertDeepSupplement(mock, expected) {
        expect(mock.calls).toHaveLength(1);
        expect(mock.calls[0]).toHaveLength(2);
        expect(mock.calls[0][0]).toBeDefined();
        expect(mock.calls[0][1]).toEqual(expected);
    }

    function assertShape(input, expectMarkup, expectDefaults) {
        Generic.extend = jest.fn();
        Generic.extend.mockReturnValue('joint.shapes.basic.Generic.extend');
        joint.util.deepSupplement = jest.fn();
        joint.util.deepSupplement.mockReturnValue('joint.util.deepSupplement');

        const defaults = jest.fn();
        defaults.mockReturnValue('joint.dia.Element.prototype.defaults');
        Object.defineProperty(joint.dia.Element.prototype, 'defaults', {
            get: defaults,
        });

        const shapes = new ShapeDefinitionGenerator(input);
        const result = shapes.zeta;

        const keys = Object.keys(result);
        expect(keys).toHaveLength(1);
        keys.forEach(key => {
            expect(result[key]).toEqual('joint.shapes.basic.Generic.extend');
        });

        assertGenericExtend(Generic.extend.mock, 'joint.util.deepSupplement');
        assertDeepSupplement(joint.util.deepSupplement.mock, 'joint.dia.Element.prototype.defaults');

        expect(defaults.mock.calls).toHaveLength(1);
        expect(defaults.mock.calls[0]).toHaveLength(0);

        return {
            markup: Generic.extend.mock.calls[0][0].markup,
            defaults: joint.util.deepSupplement.mock.calls[0][0]
        }
    }

    test('shapes list is empty', () => {
        const shapes = new ShapeDefinitionGenerator({});
        expect(shapes.zeta).toEqual({});
    });

    test('shape with no elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with sizeHeightMax and sizeWidthMax', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        sizeHeightMax: 42,
                        sizeWidthMax: 1337,
                    }
                ],
            }
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'size-max': {
                'height' : 42,
                'width': 1337,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with sizeHeightMin and sizeWidthMin', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        sizeHeightMin: 1234,
                        sizeWidthMin: 4321,
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'size-min': {
                'height' : 1234,
                'width': 4321,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with stretchingHorizontal', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        stretchingHorizontal: false,
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'resize':{
                'horizontal': false,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with stretchingVertical', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        stretchingVertical: false,
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'resize':{
                'horizontal': true,
                'vertical': false,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with proportional', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        proportional: false,
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 0,
                'height': 0,
            },
            'size': {
                'width': 0,
                'height': 0,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': false,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 0,
                    'width': 0
                },
            },
            'compartments': [],
        });
    });

    test('shape with rectangle element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'rectangle',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeHeight: 4,
                                sizeWidth: 8,
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<rect class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 9,
                'height': 6,
            },
            'size': {
                'width': 9,
                'height': 6,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 6,
                    'width': 9,
                },
                'unique' : {
                    'x': 1,
                    'y': 2,
                    'width': 8,
                    'height': 4,
                }
            },
            'compartments': [],
        });
    });

    test('shape with ellipse element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'ellipse',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeHeight: 4,
                                sizeWidth: 8,
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<ellipse class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 9,
                'height': 6,
            },
            'size': {
                'width': 9,
                'height': 6,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 6,
                    'width': 9,
                },
                'unique' : {
                    'cx': 5,
                    'cy': 4,
                    'rx': 4,
                    'ry': 2,
                },
            },
            'compartments': [],
        });
    });

    test('shape with line element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'line',
                                startPoint: {
                                    x: 1,
                                    y: 2,
                                },
                                endPoint: {
                                    x: 4,
                                    y: 8,
                                },
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<line class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 4,
                'height': 8,
            },
            'size': {
                'width': 4,
                'height': 8,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 8,
                    'width': 4,
                },
                'unique' : {
                    'x1': 1,
                    'y1': 2,
                    'x2': 4,
                    'y2': 8,
                }
            },
            'compartments': [],
        });
    });

    test('shape with rounded-rectangle element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'roundedRectangle',
                                curveHeight: 1,
                                curveWidth: 2,
                                position: {
                                    x: 4,
                                    y: 8,
                                },
                                sizeHeight: 16,
                                sizeWidth: 32,
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<rect class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 36,
                'height': 24,
            },
            'size': {
                'width': 36,
                'height': 24,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 24,
                    'width': 36,
                },
                'unique' : {
                    'x': 4,
                    'y': 8,
                    'width': 32,
                    'height': 16,
                    'rx': 2,
                    'ry': 1,
                },
            },
            'compartments': [],
        });
    });

    test('shape with polygon element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 1,
                                        y: 2,
                                    },
                                    {
                                        x: 64,
                                        y: 128,
                                    },
                                    {
                                        x: 16,
                                        y: 32,
                                    },
                                    {
                                        x: 4,
                                        y: 8,
                                    },
                                ],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<polygon class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 64,
                'height': 128,
            },
            'size': {
                'width': 40,
                'height': 80,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 128,
                    'width': 64,
                },
                'unique' : {
                    'points': '1,2 64,128 16,32 4,8 ',
                },
            },
            'compartments': [],
        });
    });

    test('shape with poly-line element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'polyline',
                                points: [
                                    {
                                        x: 1,
                                        y: 2,
                                    },
                                    {
                                        x: 4,
                                        y: 8,
                                    },
                                ],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<polyline class="unique" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 4,
                'height': 8,
            },
            'size': {
                'width': 4,
                'height': 8,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 8,
                    'width': 4,
                },
                'unique' : {
                    'points': '1,2 4,8 ',
                },
            },
            'compartments': [],
        });
    });

    test('shape with text element', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique',
                                type: 'text',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeWidth: 4,
                                sizeHeight: 8,
                                textBody: 'example',
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<text class="unique unique" > </text>' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 5,
                'height': 10,
            },
            'size': {
                'width': 5,
                'height': 10,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 10,
                    'width': 5,
                },
                'unique' : {
                    'x': 1,
                    'y': 2,
                    'id': 'unique',
                    'width': 4,
                    'height': 8,
                    'text': 'example',
                },
            },
            'compartments': [],
        });
    });

    test('shape with multiple elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 1,
                                        y: 2,
                                    }
                                ],
                            },
                            {
                                id: 'unique2',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 16,
                                        y: 32,
                                    }
                                ],
                            },
                            {
                                id: 'unique3',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 4,
                                        y: 8,
                                    }
                                ],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<polygon class="unique1" />' +
                    '<polygon class="unique2" />' +
                    '<polygon class="unique3" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 16,
                'height': 32,
            },
            'size': {
                'width': 16,
                'height': 32,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 32,
                    'width': 16,
                },
                'unique1' : {
                    'points': '1,2 ',
                },
                'unique2' : {
                    'points': '16,32 ',
                },
                'unique3' : {
                    'points': '4,8 ',
                },
            },
            'compartments': [],
        });
    });

    test('shape with nested rectangle elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'rectangle',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeHeight: 4,
                                sizeWidth: 8,
                                parent: 'unique2',
                            },
                            {
                                id: 'unique2',
                                type: 'rectangle',
                                position: {
                                    x: 16,
                                    y: 32,
                                },
                                sizeHeight: 64,
                                sizeWidth: 128,
                                children: ['unique1'],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' + 
                    '<rect class="bounding-box" />' +
                    '<rect class="unique2" />' +
                    '<rect class="unique1" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 144,
                'height': 96,
            },
            'size': {
                'width': 80,
                'height': 53,
            },
            'resize':{
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 96,
                    'width': 144,
                },
                'unique2' : {
                    'x': 16,
                    'y': 32,
                    'width': 128,
                    'height': 64,
                },
                'unique1' : {
                    'x': 17,
                    'y': 34,
                    'width': 8,
                    'height': 4,
                },
            },
            'compartments': [],
        });
    });

    test('shape with nested ellipse elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'ellipse',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeHeight: 4,
                                sizeWidth: 8,
                                parent: 'unique2',
                            },
                            {
                                id: 'unique2',
                                type: 'ellipse',
                                position: {
                                    x: 16,
                                    y: 32,
                                },
                                sizeHeight: 64,
                                sizeWidth: 128,
                                children: ['unique1'],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' +
                    '<rect class="bounding-box" />' +
                    '<ellipse class="unique2" />' +
                    '<ellipse class="unique1" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 144,
                'height': 96,
            },
            'size': {
                'width': 80,
                'height': 53,
            },
            'resize': {
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 96,
                    'width': 144,
                },
                'unique2' : {
                    'cx': 80,
                    'cy': 64,
                    'rx': 64,
                    'ry': 32,
                },
                'unique1' : {
                    'cx': 21,
                    'cy': 36,
                    'rx': 4,
                    'ry': 2,
                },
            },
            'compartments': [],
        });
    });

    test('shape with nested polygon elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 1,
                                        y: 2,
                                    },
                                    {
                                        x: 1024, // Should be ignored on init-size
                                        y: 2048, // Should be ignored on init-size
                                    },
                                ],
                                parent: 'unique2',
                            },
                            {
                                id: 'unique2',
                                type: 'polygon',
                                points: [
                                    {
                                        x: 4,
                                        y: 8,
                                    },
                                    {
                                        x: 256,
                                        y: 512,
                                    },
                                    {
                                        x: 16,
                                        y: 32,
                                    },
                                    {
                                        x: 64,
                                        y: 128,
                                    },
                                ],
                                children: ['unique1'],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' +
                    '<rect class="bounding-box" />' +
                    '<polygon class="unique2" />' +
                    '<polygon class="unique1" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 256,
                'height': 512,
            },
            'size': {
                'width': 40,
                'height': 80,
            },
            'resize': {
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 512,
                    'width': 256,
                },
                'unique2' : {
                    'points': '4,8 256,512 16,32 64,128 ',
                },
                'unique1' : {
                    'points': '5,10 1028,2056 ',
                },
            },
            'compartments': [],
        });
    });

    test('shape with nested rounded-rectangle elements', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'roundedRectangle',
                                curveHeight: 1,
                                curveWidth: 2,
                                position: {
                                    x: 4,
                                    y: 8,
                                },
                                sizeHeight: 16,
                                sizeWidth: 32,
                                parent: 'unique2',
                            },
                            {
                                id: 'unique2',
                                type: 'roundedRectangle',
                                curveHeight: 64,
                                curveWidth: 128,
                                position: {
                                    x: 256,
                                    y: 512,
                                },
                                sizeHeight: 1024,
                                sizeWidth: 2048,
                                children: ['unique1'],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' +
                    '<rect class="bounding-box" />' +
                    '<rect class="unique2" />' +
                    '<rect class="unique1" />' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 2304,
                'height': 1536,
            },
            'size': {
                'width': 80,
                'height': 53,
            },
            'resize': {
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 1536,
                    'width': 2304,
                },
                'unique2' : {
                    'x': 256,
                    'y': 512,
                    'width': 2048,
                    'height': 1024,
                    'rx': 128,
                    'ry': 64,
                },
                'unique1' : {
                    'x': 260,
                    'y': 520,
                    'width': 32,
                    'height': 16,
                    'rx': 2,
                    'ry': 1,
                },
            },
            'compartments': [],
        });
    });

    test('shape with text element as child', () => {
        const result = assertShape(
            {
                shapes: [
                    {
                        name: 'Shape',
                        elements: [
                            {
                                id: 'unique1',
                                type: 'text',
                                position: {
                                    x: 1,
                                    y: 2,
                                },
                                sizeWidth: 4,
                                sizeHeight: 8,
                                textBody: 'example',
                                parent: 'unique2',
                            },
                            {
                                id: 'unique2',
                                type: 'rectangle',
                                position: {
                                    x: 16,
                                    y: 32,
                                },
                                sizeHeight: 64,
                                sizeWidth: 128,
                                children: ['unique1'],
                            },
                        ]
                    }
                ],
            },
        );
        expect(result.markup).toEqual(
            '<g class="rotatable">' +
                '<g class="scalable">' +
                    '<rect class="bounding-box" />' +
                    '<rect class="unique2" />' +
                    '<text class="unique1 unique1" > </text>' +
                '</g>' +
            '</g>'
        );
        expect(result.defaults).toEqual({
            'type': 'zeta.Shape',
            'init-size': {
                'width': 144,
                'height': 96,
            },
            'size': {
                'width': 80,
                'height': 53,
            },
            'resize': {
                'horizontal': true,
                'vertical': true,
                'proportional': true,
            },
            'attr': {
                'rect.bounding-box':{
                    'height': 96,
                    'width': 144,
                },
                'unique2' : {
                    'x': 16,
                    'y': 32,
                    'width': 128,
                    'height': 64,
                },
                'unique1' : {
                    'x': 17,
                    'y': 34,
                    'id': 'unique1',
                    'width': 4,
                    'height': 8,
                    'text': 'example',
                },
            },
            'compartments': [],
        });
    });
});