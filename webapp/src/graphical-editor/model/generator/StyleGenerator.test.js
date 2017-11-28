import StyleGenerator from './StyleGenerator';

describe('getStyle', () => {
    function createGetStyle(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.getStyle(styleName);
    }

    test('with style not found', () => {
        const getStyle = createGetStyle([]);
        expect(getStyle('DefaultStyle')).toEqual({});
    });

    test('with empty style', () => {
        const style = {
            name: 'DefaultStyle',
        };
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'helvetica',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '20',
                'fill': '#000000',
                'font-weight': 'normal'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with font-color `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            font: {
                color: '#ffffff',
            }
        };
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#ffffff',
                'font-weight': 'normal'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'bold'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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
        
        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal'
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
                'font-style': 'italic',
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with background color `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            background: {
                color: '#ffffff',
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': '#ffffff',
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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
                            color: '#000000'
                        },
                        {
                            offset: 100,
                            color: '#ffffff'
                        }
                    ],
                },
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [
                    {
                        'offset': 0,
                        'color': '#000000'
                    },
                    {
                        'offset': 100,
                        'color': '#ffffff'
                    }
                ],
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill': {
                'type': 'linearGradient',
                'stops': [],
                'attrs': { x1: '0%', y1: '0%', x2: '0%', y2: '100%' },
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with transparency `0.5`', () => {
        const style = {
            name: 'DefaultStyle',
            transparency: 0.5,
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 0.5,
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with transparent line', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                transparent: true,
            }
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke-opacity': 0,
        });
    });

    test('with line-color `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
            }
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
        });
    });

    test('with line-width `5`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                width: 5,
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
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

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#000000',
            'stroke-width': 0,
            'stroke-dasharray': "0"
        });
    });

    test('with line-style `DASH`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                style: 'dash',
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
            'stroke-dasharray': '10,10',
        });
    });

    test('with line-style `DOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                style: 'dot',                
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
            'stroke-dasharray': '5,5',
        });
    });

    test('with line-style `DASHDOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                style: 'dashdot',
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
            'stroke-dasharray': '10,5,5,5',
        });
    });

    test('with lineStyle `DASHDOTDOT`', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                style: 'dashdotdot',
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
            'stroke-dasharray': '10,5,5,5,5,5',
        });
    });

    test('with lineStyle fallback', () => {
        const style = {
            name: 'DefaultStyle',
            line: {
                color: '#ffffff',
                style: 'unkown-style',
            },
        };

        const getStyle = createGetStyle([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'font-family': 'sans-serif',
                'font-size': '11',
                'fill': '#000000',
                'font-weight': 'normal',
            },
            'fill-opacity': 1.0,
            'stroke': '#ffffff',
            'stroke-dasharray': '0',
        });
    });
});