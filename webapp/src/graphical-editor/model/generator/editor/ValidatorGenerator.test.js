import ValidatorGenerator from './ValidatorGenerator';

describe('validator.inputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({}, {});
        expect(validator.inputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({},{
            classes: [
                {
                    name: 'Class',
                    inputs: [
                        {
                            type: 'Reference',
                            lowerBound: 2,
                            upperBound: 3,
                        }
                    ],
                }
            ],
        });
        expect(validator.inputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 3,
                    'lowerBound': 2,
                }
            },
        });
    });
});

describe('validator.outputMatrix', () => {
    test('metaModel is empty', () => {
        const validator = new ValidatorGenerator({}, {});
        expect(validator.outputMatrix).toEqual({});
    });

    test('metaModel has class and class has input', () => {
        const validator = new ValidatorGenerator({},{
            classes: [
                {
                    name: 'Class',
                    outputs: [
                        {
                            type: 'Reference',
                            lowerBound: 5,
                            upperBound: 7,
                        }
                    ],
                }
            ],
        });
        expect(validator.outputMatrix).toEqual({
            'Class': {
                'Reference': {
                    'upperBound': 7,
                    'lowerBound': 5,
                }
            },
        });
    });
});

describe('validator.getEdgeData', () => {
    test('diagram has no edges', () => {
        const validator = new ValidatorGenerator({}, {});
        expect(validator.getEdgeData('Edge')).not.toBeDefined();
    });

    test('diagram has edge', () => {
        const validator = new ValidatorGenerator({
                edges: [
                    {
                        name: 'Edge',
                        mReference: 'Reference',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                        connection: {
                            name: 'Connection'
                        }
                    }
                ]
        }, {});
        expect(validator.getEdgeData('Edge')).toEqual({
            'type': 'Reference',
            'from': 'SourceMClass',
            'to': 'TargetMClass',
            'style': 'Connection',
        });
    });
});

describe('validator.getValidEdges', () => {
    test('diagram has no nodes', () => {
        const validator = new ValidatorGenerator({}, {});
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual([]);
    });

    test('diagram has nodes with same edge', () => {
        const validator = new ValidatorGenerator({}, {
            model: {
                nodes: [
                    {
                        name: 'SourceNode',
                        mClass: 'SourceMClass',
                    },
                    {
                        name: 'TargetNode',
                        mClass: 'TargetMClass',
                    }
                ],
                edges: [
                    {
                        name: 'Edge',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                    }
                ]
            }
        });
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual(['Edge']);
    });

    test('diagram has nodes with same multiple edges', () => {
        const validator = new ValidatorGenerator({}, {
            model: {
                nodes: [
                    {
                        name: 'SourceNode',
                        mClass: 'SourceMClass',
                    },
                    {
                        name: 'TargetNode',
                        mClass: 'TargetMClass',
                    }
                ],
                edges: [
                    {
                        name: 'FirstEdge',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                    },
                    {
                        name: 'SecondEdge',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                    }
                ]
            }
        });
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual(['FirstEdge', 'SecondEdge']);
    });

    test('diagram has nodes with different edges', () => {
        const validator = new ValidatorGenerator({}, {
            model: {
                nodes: [
                    {
                        name: 'SourceNode',
                        mClass: 'SourceMClass',
                    },
                    {
                        name: 'TargetNode',
                        mClass: 'TargetMClass',
                    }
                ],
                edges: [
                    {
                        name: 'SourceEdge',
                        from: 'SourceMClass',
                    },
                    {
                        name: 'TargetEdge',
                        to: 'TargetMClass',
                    }
                ]
            }
        });
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual([]);
    });

    test('diagram has nodes with matching source superType', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'SourceChildMClass',
                    superTypeNames: ['SourceMClass'],
                }
            ]
        }, {
            model: {
                nodes: [
                    {
                        name: 'SourceNode',
                        mClass: 'SourceChildMClass',
                    },
                    {
                        name: 'TargetNode',
                        mClass: 'TargetMClass',
                    }
                ],
                edges: [
                    {
                        name: 'Edge',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                    }
                ]
            }
        });
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual(['Edge']);
    });

    test('diagram has nodes with matching target superType', () => {
        const validator = new ValidatorGenerator({
            classes: [
                {
                    name: 'TargetChildMClass',
                    superTypeNames: ['TargetMClass'],
                }
            ]
        }, {
            model: {
                nodes: [
                    {
                        name: 'SourceNode',
                        mClass: 'SourceMClass',
                    },
                    {
                        name: 'TargetNode',
                        mClass: 'TargetChildMClass',
                    }
                ],
                edges: [
                    {
                        name: 'Edge',
                        from: 'SourceMClass',
                        to: 'TargetMClass',
                    }
                ]
            }
        });
        expect(validator.getValidEdges('SourceNode', 'TargetNode')).toEqual(['Edge']);
    });
});