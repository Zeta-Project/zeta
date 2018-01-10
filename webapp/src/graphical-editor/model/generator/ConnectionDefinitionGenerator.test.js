import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';

const defaultTestId = "1234"

describe('createLabel', () => {
  function create(connections) {
      const generator = new ConnectionDefinitionGenerator(connections)
      return generator;
  }

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getLabels('Connection1')).toEqual([])
  })

  test('with empty placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": []
      }
    ]

    const generator = create(connections)

    expect(generator.getLabels('Connection1')).toEqual([])
  })

  test('with one full defined connection and one placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
              "id": defaultTestId,
              "type": "Label",
            }
          }
        ]
      }
    ]
              
    const generator = create(connections)

    expect(generator.getLabels('Connection1')).toEqual(
        [
          {
            position: 1.0,
            attrs: {
                rect: {fill: 'transparent'},
                text: {
                y: 1,
                text: "Hallo"
                }
            },
            id: defaultTestId
        }
      ]
    )
  }) 

  test('with one full defined connection and two placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
              "id": "placing1",
              "type": "Label",
            }
          },
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
              "id": "placing2",
              "type": "Label",
            }
          }
        ]
      }
    ]
            
    const generator = create(connections)

    expect(generator.getLabels('Connection1')).toEqual(
      [
        {
          position: 1.0,
          attrs: {
              rect: {fill: 'transparent'},
              text: {
              y: 1,
              text: "Hallo"
              }
          },
          id: "placing1"
      },
      {
        position: 1.0,
        attrs: {
            rect: {fill: 'transparent'},
            text: {
            y: 1,
            text: "Hallo"
            }
        },
        id: "placing2"
        }
      ]
    )
  })
});

describe('createPlacing', () => {
  function create(connections) {
    const generator = new ConnectionDefinitionGenerator(connections)
    return generator;
  }

  function createDefaultTestConnection(placing) {
    return [
      {
        "name": "Connection1",
        "placings": [
          placing
        ]
      }
    ]
  }

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getPlacings('Connection1')).toEqual([])
  })
});

describe('getConnectionStyle', () => {
  function create(connections) {
    const generator = new ConnectionDefinitionGenerator(connections)
    return generator;
  }

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getConnectionStyle('Connection1')).toEqual({})
  })

  // Braucht eine Version mit positionOffset 1.0
  test('a Style by a Connection with a Line Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
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
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {"stroke": "black"}, 
        ".marker-source": {"d": "M 0 0 L 1 1",
        "transform": "scale(1,1)"},
        ".marker-target": {"d": "M 0 0"}
      }
    )
  })

  test('a Style by a Connection with a Line Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
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
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {"stroke": "black"}, 
        ".marker-source": {"d": "M 0 0 L 1 1",
        "transform": "scale(1,1)"},
        ".marker-target": {"d": "M 0 0"}
      }
    )
  })

  test('a Style by a Connection with a Polyline Placing with one point', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
              "points": [
                {
                  "x": 0,
                  "y": 0
                }
              ],
              "type": "polyline",
            }
          }
          
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {
        "stroke": "black",
        },
         ".marker-source": {
           "d": "M 0 0 ",
           "transform": "scale(1,1)",
         },
         ".marker-target": {
             "d": "M 0 0",
         }
       })
  })

  test('a Style by a Connection with a Polyline Placing with one point (mirrored-point)', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 1.0,            
            "shape": {
              "points": [
                {
                  "x": 1,
                  "y": 1
                }
              ],
              "type": "polyline",
            }
          }
          
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {"stroke": "black"},
        ".marker-target": {
          "d": "M -1 1 ",
          "transform": "scale(1,1)"
        }
      })
  })

  test('a Style by a Connection with a Polyline Placing with multiple point', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
              "points": [
                {
                  "x": 0,
                  "y": 0
                },
                {
                  "x": 1,
                  "y": 1
                }
                ,
                {
                  "x": 2,
                  "y": 2
                }
              ],
              "type": "polyline",
            }
          }
          
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {
        "stroke": "black",
        },
         ".marker-source": {
           "d": "M 0 0 L 1 1L 2 2",
           "transform": "scale(1,1)",
         },
         ".marker-target": {
             "d": "M 0 0",
         }
       })
  })

  test('a Style by a Connection with a Polyline Placing with multiple point (mirrored points)', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 1.0,            
            "shape": {
              "points": [
                {
                  "x": 1,
                  "y": 1
                },
                {
                  "x": 2,
                  "y": 2
                }
                ,
                {
                  "x": 3,
                  "y": 3
                }
              ],
              "type": "polyline",
            }
          }
          
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        ".connection": {"stroke": "black"},
        ".marker-target": {
          "d": "M -1 1 L -2 2L -3 3",
          "transform": "scale(1,1)"
        }
      }
)
  })

  test('a Style by a Connection with a Polygon Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
              "points": [
                {
                  "x": 0,
                  "y": 0
                },
                {
                  "x": 1,
                  "y": 1
                }
                ,
                {
                  "x": 2,
                  "y": 2
                }
              ],
              "type": "polygon",
            }
          }
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual( {
         ".connection": {
           "stroke": "black",
         },
         ".marker-source": {
           "d": "M 0 0 L 1 1L 2 2z",
           "transform": "scale(1,1)",
         },
         ".marker-target": {
           "d": "M 0 0",
         },
       }
    )
  })

  test('a Style by a Connection with a Polygon Placing (mirrored points)', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 1.0,            
            "shape": {
              "points": [
                {
                  "x": 0,
                  "y": 0
                },
                {
                  "x": 1,
                  "y": 1
                }
                ,
                {
                  "x": 2,
                  "y": 2
                }
              ],
              "type": "polygon",
            }
          }
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual( {
         ".connection": {
           "stroke": "black",
         },
         ".marker-target": {
           "d": "M 0 0 L -1 -1L -2 -2z",
           "transform": "scale(1,1)",
         }
       }
    )
  })

  test('a Style by a Connection with a Rectangle Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
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
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual({
      ".connection": {"stroke": "black"},
      ".marker-source": {
        "d": "M 0 0l 3 0 l 0 2 l -3 0 z", 
        "transform": "scale(1,1)"},
      ".marker-target": {"d": "M 0 0"}}
  )
  })

  test('a Style by a Connection with a RoundedRectangle Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
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
        ]
      }
    ]
    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual({
      ".connection": {"stroke": "black"},
      ".marker-source": {
        "d": "M 1 4 1 4 l -5l 0 a 4 4 0 0 1 4 4l 0 -5 a 4 4 0 0 1 -4 4 l --5 0 a 4 4 0 0 1 -4 -4 l 0 --5 a 4 4 0 0 1 4 -4",
        "transform": "scale(1,1)"
      },
      ".marker-target": {"d": "M 0 0"}}
  )
  })

  test('a Style by a Connection with a Ellipse Placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "style": "testStyle",
        "placings": [
          {
            "positionOffset": 0.0,            
            "shape": {
              "sizeHeight": 4,
              "sizeWidth": 4,
              "position": {
                "x": 1,
                "y": 1
              },
              "type": "ellipse"
            }
          }
        ]
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual({
      ".marker-source": {
        "d": "M 1 1 a  2 2 0 0 1 2 -2 a  2 2 0 0 1 2 2 a  2 2 0 0 1 -2 2 a  2 2 0 0 1 -2 -2",
        "transform": "scale(1,1)"
      },
      ".marker-target": {"d": "M 0 0"}
    }
  )
  })

  test('a Connection without a Style', () => {
    const connections = [
      {
        "name": "Connection1"
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual({
      '.connection':{stroke: 'black'},
      '.marker-target': {"d": "M 0 0"}
    })
  })

  test('a Connection with a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "style": "testStyle"
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual({'.marker-target': {"d": "M 0 0"}}
    )
  })

});