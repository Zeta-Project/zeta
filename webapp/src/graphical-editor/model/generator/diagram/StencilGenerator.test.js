jest.mock('jointjs');

describe('Stencil.groups', () => {
    const StencilGenerator = require('./StencilGenerator').default;

    test('without palettes', () => {
        const Stencil = new StencilGenerator({});
        expect(Stencil.groups).toEqual({});
    });

    test('with single palette', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: '-General-',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'general': {
                index: 1,
                label: '-General-',
            },
        });
    });

    test('with multiple palettes', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'General',
                    },
                    {
                        palette: 'Custom',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'general': {
                index: 1,
                label: 'General',
            },
            'custom': {
                index: 2,
                label: 'Custom',
            },
        });
    });

    test('with duplicate palettes', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'General',
                    },
                    {
                        palette: 'General',
                    },
                    {
                        palette: 'Custom',
                    }
                ]
            }
        });
        expect(Stencil.groups).toEqual({
            'general': {
                index: 1,
                label: 'General',
            },
            'custom': {
                index: 2,
                label: 'Custom',
            },
        });
    });
});

describe('Stencil.shapes', () => {
    const StencilGenerator = require('./StencilGenerator').default;
    class Box {
        constructor(parameter) {
            this.parameter = parameter;
            this.attr = jest.fn();
        }
    }
    class ShapeStyle {
        getShapeStyle() {
            return {};          
        }
    }
    require('jointjs').shapes.zeta = { Box };

    test('without palettes', () => {
        const Stencil = new StencilGenerator({});
        expect(Stencil.shapes).toEqual({});
    });

    test('with palette', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        name: 'BoxNode',
                        mClass: 'BoxModel',
                        palette: 'General',
                        shape: {
                            name: 'Box',
                        },
                    }
                ]
            }
        }, {}, new ShapeStyle());
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(1);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({})
            expect(shape.parameter).toEqual({
                'nodeName': 'BoxNode',
                'mClass': 'BoxModel',
                'mClassAttributeInfo': [],
            });
        });
    });

    test('with palette and onCreate', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        name: 'BoxNode',
                        mClass: 'BoxModel',
                        palette: 'General',
                        shape: {
                            name: 'Box',
                        },
                        onCreate: {
                            askFor: 'BoxModelAttribute'
                        }
                    }
                ]
            }
        }, {}, new ShapeStyle());
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(1);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({})
            expect(shape.parameter).toEqual({
                'nodeName': 'BoxNode',
                'mClass': 'BoxModel',
                'mcoreAttributes': [
                    {
                        'mcore': 'BoxModelAttribute',
                        'cellPath': ['attrs', '.label', 'text'],
                    }
                ],
                'mClassAttributeInfo': [],
            });
        });
    });

    test('with palette and meta-model attribute', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        name: 'BoxNode',
                        mClass: 'BoxModel',
                        palette: 'General',
                        shape: {
                            name: 'Box',
                        },
                    }
                ]
            }
        }, {
            classes: [
                {
                    name: 'BoxModel',
                    attributes: [
                        {
                            name: 'BoxModelAttribute',
                            typ: 'String',
                        }
                    ]
                }
            ],
        }, new ShapeStyle());
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(1);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({})
            expect(shape.parameter).toEqual({
                'nodeName': 'BoxNode',
                'mClass': 'BoxModel',
                'mClassAttributeInfo': [
                    {
                        'name': 'BoxModelAttribute',
                        'type': 'StringType',
                    }
                ],
            });
        });
    });

    test('with palette and meta-model attribute and vars', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'General',
                        mClass: 'BoxModel',
                        shape: {
                            name: 'Box',
                            vars: [
                                {
                                    key: 'text_id',
                                    value: 'BoxModelAttribute',
                                }
                            ] 
                        },
                    }
                ]
            }
        }, {
            classes: [
                {
                    name: 'BoxModel',
                    attributes: [
                        {
                            name: 'BoxModelAttribute',
                            typ: 'String',
                        }
                    ]
                }
            ],
        }, new ShapeStyle());
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(1);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({})
            expect(shape.parameter).toEqual({
                'nodeName': undefined,
                'mClass': 'BoxModel',
                'mClassAttributeInfo': [
                    {
                        'name': 'BoxModelAttribute',
                        'type': 'StringType',
                        'id': 'text_id',
                    }
                ],
            });
        });
    });

    test('with palette and shape-style', () => {
        const shapeStyle = new ShapeStyle();
        shapeStyle.getShapeStyle = function(shape) {
            return shape === 'Box' ? { font: 'Verdana' } : {};
        }
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'General',
                        shape: {
                            name: 'Box',
                        },
                    }
                ]
            }
        }, {}, shapeStyle);
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(1);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({ 'font': 'Verdana' })
        });
    });

    test('with palette and vals', () => {
        const Stencil = new StencilGenerator({
            model: {
                nodes: [
                    {
                        palette: 'General',
                        shape: {
                            name: 'Box',
                            vals: [
                                {
                                    key: 'text_id',
                                    value: 'label',
                                }
                            ] 
                        },
                    }
                ]
            }
        }, {}, new ShapeStyle());
        expect(Stencil.shapes).toHaveProperty('general');
        expect(Stencil.shapes.general).toBeInstanceOf(Array);
        expect(Stencil.shapes.general).not.toEqual([]);
        Stencil.shapes.general.forEach(shape => {
            expect(shape).toBeInstanceOf(Box);
            expect(shape.attr.mock.calls).toHaveLength(2);
            expect(shape.attr.mock.calls[0]).toHaveLength(1);
            expect(shape.attr.mock.calls[0][0]).toEqual({});
            expect(shape.attr.mock.calls[1]).toHaveLength(1);
            expect(shape.attr.mock.calls[1][0]).toEqual({ 'text_id': {'text': 'label' } });
        });
    });
});