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
        
        const getStyle = create([style]);
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
        
        const getStyle = create([style]);
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
        
        const getStyle = create([style]);
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
        
        const getStyle = create([style]);
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
        
        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

        const getStyle = create([style]);
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

    test('with all attributes', () => {
        const style = {
            background: {
                color: '#ff00ff',
            },
            font: {
                bold: true,
                color: '#00ffff',
                italic: true,
                name: 'helvetica',
                size: 20,
            },
            line: {
                color: '#ffff00',
                style: 'dash',
                width: 5,
            },
            name: 'DefaultStyle',
            transparency: 0.3,
        };

        const getStyle = create([style]);
        expect(getStyle('DefaultStyle')).toEqual({
            'text': {
                'dominant-baseline': "text-before-edge",
                'fill': '#00ffff',
                'font-family': 'helvetica',
                'font-size': '20',
                'font-style': 'italic',
                'font-weight': 'bold'
            },
            'fill': '#ff00ff',
            'fill-opacity': 0.3,
            'stroke': '#ffff00',
            'stroke-dasharray': '10,10',
            'stroke-width': 5,
        });
    });
});

describe('getDiagramHighlighting', () => {
    function create(styles) {
        const generator = new StyleGenerator(styles)
        return (styleName) => generator.getDiagramHighlighting(styleName);
    }

    test('with style not found', () => {
        const getDiagramHighlighting = create([]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('');
    });

    test('with empty style', () => {
        const style = {
            name: 'DefaultStyle',
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('');
    });

    test('with selectedHighlighting `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            selectedHighlighting: {
                color: '#ffffff',
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .free-transform { border: 1px dashed  #ffffff; }');
    });

    test('with transparent selectedHighlighting', () => {
        const style = {
            name: 'DefaultStyle',
            selectedHighlighting: {
                transparent: true,
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .free-transform { border: 1px dashed  transparent; }');
    });

    test('with selectedHighlighting as gradient', () => {
        const style = {
            name: 'DefaultStyle',
            selectedHighlighting: {
                gradient: {},
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .free-transform { border: 1px dashed  ; }');
    });

    test('with multiselectedHighlighting `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            multiselectedHighlighting: {
                color: '#ffffff',
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .selection-box { border: 1px solid #ffffff; }');
    });

    test('with transparent multiselectedHighlighting', () => {
        const style = {
            name: 'DefaultStyle',
            multiselectedHighlighting: {
                transparent: true,
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .selection-box { border: 1px solid transparent; }');
    });

    test('with multiselectedHighlighting as gradient', () => {
        const style = {
            name: 'DefaultStyle',
            multiselectedHighlighting: {
                gradient: {},
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .selection-box { border: 1px solid ; }');
    });

    test('with allowedHighlighting `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            allowedHighlighting: {
                color: '#ffffff',
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-allowed { outline: 2px solid #ffffff; }');
    });

    test('with transparent allowedHighlighting', () => {
        const style = {
            name: 'DefaultStyle',
            allowedHighlighting: {
                transparent: true,
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-allowed { outline: 2px solid transparent; }');
    });

    test('with allowedHighlighting as gradient', () => {
        const style = {
            name: 'DefaultStyle',
            allowedHighlighting: {
                gradient: {},
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-allowed { outline: 2px solid ; }');
    });

    test('with unallowedHighlighting `#ffffff`', () => {
        const style = {
            name: 'DefaultStyle',
            unallowedHighlighting: {
                color: '#ffffff',
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-unallowed { outline: 2px solid #ffffff; }');
    });

    test('with transparent unallowedHighlighting', () => {
        const style = {
            name: 'DefaultStyle',
            unallowedHighlighting: {
                transparent: true,
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-unallowed { outline: 2px solid transparent; }');
    });

    test('with unallowedHighlighting as gradient', () => {
        const style = {
            name: 'DefaultStyle',
            unallowedHighlighting: {
                gradient: {},
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual('.paper-container .linking-unallowed { outline: 2px solid ; }');
    });

    test('with all highlightings', () => {
        const style = {
            name: 'DefaultStyle',
            allowedHighlighting: {
                transparent: true,
            },
            multiselectedHighlighting: {
                color: '#00ffff',
            },
            selectedHighlighting: {
                color: '#ffff00',
            },
            unallowedHighlighting: {
                gradient: {},
            }
        };

        const getDiagramHighlighting = create([style]);
        expect(getDiagramHighlighting('DefaultStyle')).toEqual(
            '.paper-container .free-transform { border: 1px dashed  #ffff00; }' + 
            '.paper-container .selection-box { border: 1px solid #00ffff; }' + 
            '.paper-container .linking-allowed { outline: 2px solid transparent; }' + 
            '.paper-container .linking-unallowed { outline: 2px solid ; }'
        );
    });
});