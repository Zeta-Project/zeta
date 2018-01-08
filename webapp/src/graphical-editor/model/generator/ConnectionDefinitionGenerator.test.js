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

  test('with empty placing', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": []
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual([])
  })

  test('with a Line as Shape with a Style', () => {
    const connections = createDefaultTestConnection(
      {
        "positionOffset": 1.0,
        "shape": {
          "endPoint": {
            "x": 0,
            "y": 0,
          },
          "startPoint": {
            "x": 0,
            "y": 0,
          },
          "style": "Style1",
          "type": "line",
        }
      }
    )

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<line />',
          attrs: {
              x1: 0,
              y1: 0,
              x2: 0,
              y2: 0,
              style: "Style1"
          }
        }
    ])
  })

  test('with a Line as Shape without a Style', () => {
    const connections = createDefaultTestConnection(
      {
        "positionOffset": 1.0,
        "shape": {
          "endPoint": {
            "x": 0,
            "y": 0,
          },
          "startPoint": {
            "x": 0,
            "y": 0,
          },
          "type": "line",
        }
      }
    )

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<line />',
          attrs: {
              x1: 0,
              y1: 0,
              x2: 0,
              y2: 0
          }
        }
    ])
  })

  test('with a PolyLine as Shape without a Style', () => {
    const connections = createDefaultTestConnection(
      {
        "positionOffset": 1.0,
        "shape": {
          "points": [
            {
              "x": 0,
              "y": 0,
            },
            {
              "x": 1,
              "y": 1,
            }
          ],
          "type": "polyline",
        }
      }
    )

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<polyline />',
          attrs: {
              points: "0,0 1,1",
              fill: 'transparent'
          }
        }
    ])
  })

  test('with a Polygon as Shape without a Style', () => {
    const connections = createDefaultTestConnection(
      {
        "positionOffset": 1.0,
        "shape": {
          "points": [
            {
              "x": 0,
              "y": 0,
            },
            {
              "x": 1,
              "y": 1,
            }
          ],
          "type": "polygon",
        }
      }
    )

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<polygon />',
          attrs: {
              points: "0,0 1,1",
          }
        }
    ])
  })

  test('with a Rectangle as Shape without a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 2,
            "positionOffset": 1.0,
            "shape": {
              "sizeHeight": 2,
              "sizeWidth": 1,
              "type": "rectangle",
            }
          }
        ]
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<rect />',
          attrs:{
              height: 2,
              width: 1,
              y: 1
          }
        }
    ])
  })

  test('with a RoundedRectangle as Shape without a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 2,
            "positionOffset": 1.0,
            "shape": {
              "curveHeight": 2,
              "curveWidth": 1,
              "sizeHeight": 2,
              "sizeWidth": 1,
              "type": "roundedRectangle",
            }
          }
        ]
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<rect />',
          attrs:{
            height: 2,
            width: 1,
            rx: 1,
            ry: 2,
            y: 1
          }
        }
    ])
  })

  test('with a Ellipse as Shape without a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,            
            "shape": {
              "sizeHeight": 2,
              "sizeWidth": 2,
              "type": "ellipse",
            }
          }
        ]
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<ellipse />',
          attrs:{
            rx: 1,
            ry: 1,
            cy: 1,
          }
        }
    ])
  })

  test('with a Text as Shape without a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 1.0,            
            "shape": {
              "textBody": "Hallo",
              "sizeHeight": 2,
              "type": "text",
            }
          }
        ]
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<text>Hallo</text>',
          attrs:{
            y: 1
          }
        }
    ])
  })

  test('with a Text as Shape with a Style', () => {
    const connections = [
      {
        "name": "Connection1",
        "placings": [
          {
            "positionOffset": 1.0,            
            "shape": {
              "textBody": "Hallo",
              "sizeHeight": 2,
              "type": "text",
              "style": "exampleStyle"
            }
          }
        ]
      }
    ]

    const generator = create(connections)

    expect(generator.getPlacings('Connection1')).toEqual(
      [
        {
          position: 1.0,
          markup: '<text>Hallo</text>',
          attrs:{
            y: 1
          }
        }
    ])
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
            "positionOffset": 1.0,            
            "shape": {

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
    })
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