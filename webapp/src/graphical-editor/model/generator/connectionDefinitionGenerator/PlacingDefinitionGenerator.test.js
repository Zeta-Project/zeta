import PlacingDefinitionGenerator from './PlacingDefinitionGenerator';
import StyleGenerator from '../StyleGenerator';

describe('createPlacing', () => {
  function create() {
    const defaultStyle = [
      {
        name: 'DefaultStyle',
      }
    ];

    const styleGenerator = new StyleGenerator(defaultStyle);
    return new PlacingDefinitionGenerator(styleGenerator);
  }

  function createDefaultTestConnection(placing) {
    return {
        "name": "TestConnection",
        "placings": [
          placing
        ]
      }
    
  }

  test('with empty placing', () => {
      const connection = {
          "name": "TestConnection",
          "placings": []
      };
      const generator = create();

    expect(generator.createPlacingList(connection)).toEqual([])
  })

  test('with a Line as Shape with a Style', () => {
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.5,
        "shape": {
          "endPoint": {
            "x": 0,
            "y": 0,
          },
          "startPoint": {
            "x": 0,
            "y": 0,
          },
          "style": "DefaultStyle",
          "type": "line",
        }
      }
    )

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
          markup: '<line />',
          attrs: {
              x1: 0,
              y1: 0,
              x2: 0,
              y2: 0,
              "fill-opacity": 1,
              "stroke": "#000000",
              "stroke-dasharray": "0",
              "stroke-width": 0
          }
        }
    ])
  })

  test('with a Line as Shape without a Style', () => {
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.5,
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

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
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
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.5,
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

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
          markup: '<polyline />',
          attrs: {
              points: "0,0 1,1",
              fill: 'transparent'
          }
        }
    ])
  })

  test('with a Polygon as Shape without a Style', () => {
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.5,
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

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
          markup: '<polygon />',
          attrs: {
              points: "0,0 1,1",
          }
        }
    ])
  })

  test('with a Rectangle as Shape without a Style', () => {
      const connection = createDefaultTestConnection(
      {
          "positionDistance": 2,
          "positionOffset": 0.5,
          "shape": {
              "sizeHeight": 2,
              "sizeWidth": 1,
              "type": "rectangle",
          }
      });
      
    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
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
    const connection = createDefaultTestConnection(
      {
          "positionDistance": 2,
          "positionOffset": 0.5,
          "shape": {
            "curveHeight": 2,
            "curveWidth": 1,
            "sizeHeight": 2,
            "sizeWidth": 1,
            "type": "roundedRectangle",
          }
        }
    );

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
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
      const connection = createDefaultTestConnection(
      {
          "positionDistance": 1,
          "positionOffset": 0.5,            
          "shape": {
              "sizeHeight": 2,
              "sizeWidth": 2,
              "type": "ellipse",
          } 
      });
        
    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
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
    const connection = createDefaultTestConnection(
      {
          "positionOffset": 0.5,            
          "shape": {
              "textBody": "Hallo",
              "sizeHeight": 2,
              "type": "text",
          }
      });

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
          markup: '<text>Hallo</text>',
          attrs:{
            y: 1
          }
        }
    ])
  })

  test('with a Text as Shape with a Style', () => {
      const connection = createDefaultTestConnection(
      {
          "positionOffset":0.5,            
          "shape": {
            "textBody": "Hallo",
            "sizeHeight": 2,
            "type": "text",
            "style": "exampleStyle"
          }
      });

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual(
      [
        {
          position: 0.5,
          markup: '<text>Hallo</text>',
          attrs:{
            y: 1
          }
        }
    ])
  })

  test('with an example Shape (Text) with positionOffset 1.0', () => {
    const connection = createDefaultTestConnection(
      {
          "positionOffset": 1.0,            
          "shape": {
              "textBody": "Hallo",
              "sizeHeight": 2,
              "type": "text",
          }
      });

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual([])
  })

  test('with a example Shape (Line) with positionOffset of 0.0', () => {
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.0,
        "shape": {
          "endPoint": {
            "x": 0,
            "y": 0,
          },
          "startPoint": {
            "x": 0,
            "y": 0,
          },
          "style": "DefaultStyle",
          "type": "line",
        }
      }
    )

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual([])
  })

  test('with a example Shape (Line) with positionOffset of 1.0', () => {
    const connection = createDefaultTestConnection(
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
          "style": "DefaultStyle",
          "type": "line",
        }
      }
    )

    const generator = create();

    expect(generator.createPlacingList(connection)).toEqual([])
  })

  test('with a placing with a unknown type', () => {
    const connection = createDefaultTestConnection(
      {
        "positionOffset": 0.5,
        "shape": {
          "type": "unknownType",
        }
      }
    )

    const generator = create();

    expect(() => generator.createPlacingList(connection)).toThrowError('Unknown placing: unknownType');
  })

});