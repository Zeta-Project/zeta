import ShapeStyleGenerator from './ShapeStyleGenerator';
import StyleGenerator from '../style/StyleGenerator';

describe('getShapeStyle', () => {
    let style;

    function create(shape, style) {
        const generator = new ShapeStyleGenerator(shape, style);
        return (shapeName) => generator.getShapeStyle(shapeName);
    }

    function assertGetStyle(expectedStyle, expectedCalls = 1) {
        const mock = style.getStyle.mock; 
        expect(mock.calls).toHaveLength(expectedCalls);
        mock.calls.forEach(call => {
            expect(call).toHaveLength(1);
            expect(call[0]).toEqual(expectedStyle);
        })
    }

    function assertElement(input, expectedStyle, expected) {
        expect(input).toEqual(expected);
        assertGetStyle(expectedStyle);
    }

    beforeEach(() => {
        style = new StyleGenerator([]);
        style.getStyle = jest.fn();
        style.getStyle.mockReturnValue('style-definition');
    });

    test('without shapes', () => {
        const getShapeStyle = create({});
        expect(getShapeStyle('example')).toEqual({});

        expect(style.getStyle.mock.calls).toHaveLength(0);
    });

    test('shape with line element', () => {
        const getShapeStyle = create({ 
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
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'line.unique': 'style-definition',
        });
    });

    test('shape with rounded rectangle element', () => {
        const getShapeStyle = create({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique',
                            type: 'ROUNDED_RECTANGLE',
                            style: 'default-style',
                        }
                    ],
                } 
            ],
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'rect.unique': 'style-definition',
        });
    });

    test('shape with rectangle element', () => {
        const getShapeStyle = create({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique',
                            type: 'RECTANGLE',
                            style: 'default-style',
                        }
                    ],
                } 
            ],
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'rect.unique': 'style-definition',
        });
    });

    test('shape with ellipse element', () => {
        const getShapeStyle = create({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique',
                            type: 'ELLIPSE',
                            style: 'default-style',
                        }
                    ],
                } 
            ],
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'ellipse.unique': 'style-definition',
        });
    });

    test('shape with text element', () => {
        const getShapeStyle = create({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique',
                            type: 'TEXT',
                            style: 'default-style',
                        }
                    ],
                } 
            ],
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'text.unique': 'style-definition',
            '.unique': 'style-definition',
        });
    });

    test('shape with polygon element', () => {
        const getShapeStyle = create({ 
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
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'polygon.unique': 'style-definition',
        });
    });

    test('shape with poly-line element', () => {
        const getShapeStyle = create({ 
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
        }, style);
        assertElement(getShapeStyle('Shape'), 'default-style', {
            'polyline.unique': 'style-definition',
        });
    });

    test('shape with element has no style', () => {
        const getShapeStyle = create({ 
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
        }, style);
        expect(getShapeStyle('Shape')).toEqual({});
        assertGetStyle('default-style', 0);
    });

    test('shape with nested ellipse elements', () => {
        const getShapeStyle = create({ 
            shapes: [ 
                { 
                    name: 'Shape',
                    elements: [
                        {
                            id: 'unique1',
                            type: 'ELLIPSE',
                            style: 'default-style',
                            parent: 'unique2',
                        },
                        {
                            id: 'unique2',
                            type: 'ELLIPSE',
                            style: 'default-style',
                            children: ['unique1'],
                        },
                        {
                            id: 'unique3',
                            type: 'ELLIPSE',
                            parent: 'unique2',
                        }
                    ],
                } 
            ],
        }, style);
        expect(getShapeStyle('Shape')).toEqual({
            'ellipse.unique2': 'style-definition',
            'ellipse.unique1': 'style-definition',
        });
        assertGetStyle('default-style', 2);
    });
});