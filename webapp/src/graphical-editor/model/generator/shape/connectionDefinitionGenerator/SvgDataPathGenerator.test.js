import SvgDataPathGenerator from './SvgDataPathGenerator';

describe('generateMarker', () => {
    function create() {
        return new SvgDataPathGenerator();
    }

    test('with a Line Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "endPoint": {
                    "x": 1,
                    "y": 1,
                },
                "startPoint": {
                    "x": 0,
                    "y": 0,
                },
                "type": "line",
            }
        }

        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({"d": "M 0 0 L 1 1"})
    })
   
    test('with a Polyline Placing with one point', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "points": [
                    {
                      "x": 0,
                      "y": 0
                    }
                ],
                "type": "polyline",
            }
        };
    
        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({"d": "M 0 0 "})
    })
     
    test('with a Polyline Placing with multiple point', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "points": [
                    {
                        "x": 0,
                        "y": 0
                    },
                    {
                        "x": 1,
                        "y": 1
                    },
                    {
                        "x": 2,
                        "y": 2
                    }
                ],
                "type": "polyline",
            }
        }
             
        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({"d": "M 0 0 L 1 1L 2 2"})
    })
    
    test('with a Polygon Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "points": [
                    {
                        "x": 0,
                        "y": 0
                    },
                    {
                        "x": 1,
                        "y": 1
                    },
                    {
                        "x": 2,
                        "y": 2
                    }
                ],
                "type": "polygon",
            }
        }
        const generator = create();
        expect(generator.generateMarker(placing)).toEqual( {"d": "M 0 0 L 1 1L 2 2z"})
    })
    
    test('with a Rectangle Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "textBody": "positionOffset1",
                "sizeHeight": 2,
                "sizeWidth": 3,
                "position": {
                "x": 0,
                "y": 0,
                },
                "type": "rectangle",
            }
        }
  
        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({"d": "M 0 0l 3 0 l 0 2 l -3 0 z"})
    })
    
    test('with a RoundedRectangle Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "curveWidth": 4,
                "curveHeight": 4,
                "position": {
                "x": 1,
                "y": 1
                },
                "sizeHeight": 3,
                "sizeWidth": 3,
                "type": "roundedRectangle",
            }
        }

        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({
            "d": "M 1 4 1 4 l -5l 0 a 4 4 0 0 1 4 4l 0 -5 a 4 4 0 0 1 -4 4 l --5 0 a 4 4 0 0 1 -4 -4 l 0 --5 a 4 4 0 0 1 4 -4"
        })
    })
    
    test('with a Ellipse Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "sizeHeight": 4,
                "sizeWidth": 4,
                "position": {
                "x": 1,
                "y": 1
                },
                "type": "ellipse"
            }
        }

        const generator = create();
        expect(generator.generateMarker(placing)).toEqual({
            "d": "M 1 1 a  2 2 0 0 1 2 -2 a  2 2 0 0 1 2 2 a  2 2 0 0 1 -2 2 a  2 2 0 0 1 -2 -2"
        })
    })
    
    test('with a Placing with an unknown type', () => {
        const placing = {
            "position": {
                "offset": 0.5
            },
            "geoElement": {
                "type": "unknownType"
            }
        }
      
        const generator = create();
    
        expect(() => generator.generateMarker(placing)).toThrowError('Unknown placing: unknownType');
    })
});

describe('generateMirroredMarker', () => {
    function create() {
        return new SvgDataPathGenerator();
    }

    test('with a Polyline Placing with one mirrored point', () => {
        const placing = {
            "position": {
                "offset": 1.0
            },
            "geoElement": {
                "points": [
                {
                    "x": 1,
                    "y": 1
                }
                ],
                "type": "polyline",
            }
        };
              
        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({"d": "M -1 1 "})
    })
    
 
    test('with a Polyline Placing with multiple, mirrored points', () => {
        const placing = {
            "position": {
                "offset": 1.0
            },
            "geoElement": {
                "points": [
                    {
                        "x": 1,
                        "y": 1
                    },
                    {
                        "x": 2,
                        "y": 2
                    },
                    {
                        "x": 3,
                        "y": 3
                    }
                ],
                "type": "polyline",
            }
        }
              
    
        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({"d": "M -1 1 L -2 2L -3 3"})
    })
    
    test('with a mirrored Polygon Placing', () => {
        const placing ={
            "position": {
                "offset": 1.0
            },
            "geoElement": {
                "points": [
                    {
                        "x": 0,
                        "y": 0
                    },
                    {
                        "x": 1,
                        "y": 1
                    },
                    {
                        "x": 2,
                        "y": 2
                    }
                ],
                "type": "polygon",
            }
        }

    
        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({"d": "M 0 0 L -1 -1L -2 -2z"})
    })

    test('with a Line Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "endPoint": {
                    "x": 1,
                    "y": 1,
                },
                "startPoint": {
                    "x": 0,
                    "y": 0,
                },
                "type": "line",
            }
        }

        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({"d": "M 0 0 L 1 1"})
    })

    test('with a Rectangle Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "textBody": "positionOffset1",
                "sizeHeight": 2,
                "sizeWidth": 3,
                "position": {
                "x": 0,
                "y": 0,
                },
                "type": "rectangle",
            }
        }
  
        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({"d": "M 0 0l 3 0 l 0 2 l -3 0 z"})
    })
    
    test('with a RoundedRectangle Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "curveWidth": 4,
                "curveHeight": 4,
                "position": {
                "x": 1,
                "y": 1
                },
                "sizeHeight": 3,
                "sizeWidth": 3,
                "type": "roundedRectangle",
            }
        }

        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({
            "d": "M 1 4 1 4 l -5l 0 a 4 4 0 0 1 4 4l 0 -5 a 4 4 0 0 1 -4 4 l --5 0 a 4 4 0 0 1 -4 -4 l 0 --5 a 4 4 0 0 1 4 -4"
        })
    })
    
    test('with a Ellipse Placing', () => {
        const placing = {
            "position": {
                "offset": 0.0
            },
            "geoElement": {
                "sizeHeight": 4,
                "sizeWidth": 4,
                "position": {
                "x": 1,
                "y": 1
                },
                "type": "ellipse"
            }
        }

        const generator = create();
        expect(generator.generateMirroredMarker(placing)).toEqual({
            "d": "M 1 1 a  2 2 0 0 1 2 -2 a  2 2 0 0 1 2 2 a  2 2 0 0 1 -2 2 a  2 2 0 0 1 -2 -2"
        })
    })
});