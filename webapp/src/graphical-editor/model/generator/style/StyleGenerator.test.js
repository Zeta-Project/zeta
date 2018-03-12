import StyleGenerator from './StyleGenerator';

describe('getStyle', () => {
    function create(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.getStyle(styleName);
    }

    test('with style not found', () => {
        const getStyle = create([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });

    test('with empty style', () => {
        const style = {
            name: 'DefaultStyle',
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-name `helvetica`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                name: 'helvetica',
            }
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'helvetica',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });
    
    test('with font-size `20`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                size: 20,
            }
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '20',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-color `rgba(255,255,255,1.0)`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                color: 'rgba(255,255,255,1.0)',
            }
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(255,255,255,1.0)',
                'font-weight': 'normal'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });
    
    test('with font-bold `true`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                bold: true,
            }
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'bold'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-bold `false`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                bold: false,
            }
        };
        
        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal'
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-italic `true`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                italic: true,
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
                'font-style': 'italic',
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-italic `false`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                italic: false,
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with background color `rgba(255,255,255,1.0)`', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                color: 'rgba(255,255,255,1.0)',
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': 'rgba(255,255,255,1.0)',
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });
    
    test('with background gradient', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                gradient: {},
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with background gradient with empty area', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                gradient: {
                    area: [],
                },
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with background gradient with area', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                gradient: {
                    area: [
                        {
                            offset: 0,
                            color: 'rgba(0,0,0,1.0)'
                        },
                        {
                            offset: 100,
                            color: 'rgba(255,255,255,1.0)'
                        }
                    ],
                },
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [
                    {
                        'offset': 0,
                        'color': 'rgba(0,0,0,1.0)'
                    },
                    {
                        'offset': 100,
                        'color': 'rgba(255,255,255,1.0)'
                    }
                ],
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with horizontal background gradient', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                gradient: {
                    horizontal: true
                },
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with vertical background gradient', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                gradient: {
                    horizontal: false
                },
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
                'attrs': { x1: '0%', y1: '0%', x2: '0%', y2: '100%' },
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with text-color-transparency `0.5`', () => {
        const style = {
            name: 'DefaultStyle',
            font:{
                color: 'rgba(0,0,0,0.5)'
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,0.5)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with transparent line', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color:  'rgba(0,0,0,0.5)'
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(0,0,0,0.5)'
        });
    });

    test('with line-color `rgba(255,255,255,1.0)`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
        });
    });

    test('with line-width `5`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                width: 5,
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-width': 5,
        });
    });

    test('with line-width `5` and without line-color', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                width: 5,
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with line-style `DASH`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                style: 'dash',
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-dasharray': '10,10',
        });
    });

    test('with line-style `DOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                style: 'dot',                
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-dasharray': '5,5',
        });
    });

    test('with line-style `DASHDOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                style: 'dashdot',
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-dasharray': '10,5,5,5',
        });
    });

    test('with lineStyle `DASHDOTDOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                style: 'dashdotdot',
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-dasharray': '10,5,5,5,5,5',
        });
    });

    test('with lineStyle fallback', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: 'rgba(255,255,255,1.0)',
                style: 'unkown-style',
            },
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': 'rgba(0,0,0,1.0)',
                'font-weight': 'normal',
            },
            'stroke': 'rgba(255,255,255,1.0)',
            'stroke-dasharray': '0',
        });
    });

    test('with all attributes', () => {
        const style = {
            background: {
                color: 'rgba(255,0,255,1.0)',
            },
            font: {
                bold: true,
                color: 'rgba(0,255,255,1.0)',
                italic: true,
                name: 'helvetica',
                size: 20,
            },
            line: {
                color: 'rgba(255,255,0,1.0)',
                style: 'dash',
                width: 5,
            },
            name: 'DefaultStyle'
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'fill': 'rgba(0,255,255,1.0)',
                'font-family': 'helvetica',
                'font-size': '20',
                'font-style': 'italic',
                'font-weight': 'bold'
            },
            'fill': 'rgba(255,0,255,1.0)',
            'stroke': 'rgba(255,255,0,1.0)',
            'stroke-dasharray': '10,10',
            'stroke-width': 5,
        });
    });
});

describe('createCommonAttributes', () => {
    function create(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.createCommonAttributes(styleName);
    }

    test('with style not found', () => {
        const getStyle = create([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });

    test('with style not found', () => {
        const getStyle = create([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });

    test('with transparency `0.5`', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                color: 'rgba(0,0,0,0.5)'
            }
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'fill': 'rgba(0,0,0,0.5)',
            'stroke': 'rgba(0,0,0,1.0)',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });
});

describe('createFontAttributes', () => {
    function create(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.createFontAttributes(styleName);
    }

    test('with style not found', () => {
        const getStyle = create([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });
});