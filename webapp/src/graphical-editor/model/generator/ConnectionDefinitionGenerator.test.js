import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';
import StyleGenerator from './StyleGenerator'

function create(connections) {
  const defaultStyle = [
    {
      name: 'DefaultStyle',
    }
  ];

  const styleGenerator = new StyleGenerator(defaultStyle)
  const generator = new ConnectionDefinitionGenerator(connections, styleGenerator)
  return generator;
}

describe('createLabel', () => {

  test('with empty connetions', () => {
    const generator = create({connections: []})

    expect(generator.getLabels('TestConnection')).toEqual([])
  })
});

describe('createPlacing', () => {

  test('with empty connetions', () => {
    const generator = create({connections: []});

    expect(generator.getPlacings('TestConnection')).toEqual([])
  })
});

describe('getConnectionStyle', () => {

  test('with empty connetions', () => {
    const generator = create({connections: []})

    expect(generator.getConnectionStyle('TestConnection')).toEqual({})
  })

  test('with an example Placing (Line)', () => {
    const shapeJSON = {
      connections: [
        {
          "name": "TestConnection",
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
    }

    const generator = create(shapeJSON);
    expect(generator.getConnectionStyle('TestConnection')).toEqual(
      {
        ".connection": {"stroke": "black"}, 
        ".marker-source": {
          "d": "M 0 0 L 1 1",
          "transform": "scale(1,1)"
        },
        ".marker-target": {"d": "M 0 0"}
      }
    )
  })

  test('a Connection without a Style', () => {
    const shapeJSON = {
      connections:  [
        {
          "name": "TestConnection"
        }
      ]
    }

    const generator = create(shapeJSON);
    expect(generator.getConnectionStyle('TestConnection')).toEqual({
      '.connection':{stroke: 'black'},
      '.marker-target': {"d": "M 0 0"}
    })
  })

  test('a Connection with a default Style', () => {
    const shapeJSON = {
      connections: [
        {
          "name": "TestConnection",
          "style": "DefaultStyle"
        }
      ]
    }

    const generator = create(shapeJSON);

    expect(generator.getConnectionStyle('TestConnection')).toEqual(
      {
        ".connection, .marker-target, .marker-source": { 
          "fill-opacity": 1,
          "stroke": "#000000",
          "stroke-dasharray": "0",
          "stroke-width": 0,
          "dominant-baseline": "text-before-edge",
          "fill": "#000000",
          "font-family": "DefaultStyle",
          "font-size": "11",
          "font-weight": "normal"
        },
        ".marker-target": {"d": "M 0 0"},
        "fill-opacity": 1,
        "stroke": "#000000",
        "stroke-dasharray": "0",
        "stroke-width": 0,
        "text": {
          "dominant-baseline": "text-before-edge",
          "fill": "#000000",
          "font-family": "sans-serif",
          "font-size": "11",
          "font-weight": "normal"
        }
      }

    )
  })

  test('a Connection with a default Style and an example mirrored Placing (Line)', () => {
    
    const shapeJSON = {
      connections: [
        {
          "name": "TestConnection",
          "placings": [
            {
              "positionOffset": 1.0,            
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
                "style": "DefaultStyle"
              }
            }
          ]      
        }
      ]
    }

    const generator = create(shapeJSON);

    expect(generator.getConnectionStyle('TestConnection')).toEqual(
      {
        ".connection": {"stroke": "black"},
        ".marker-target": {
          "d": "M 0 0 L 1 1",
          "fill-opacity": 1,
          "stroke": "#000000",
          "stroke-dasharray": "0",
          "stroke-width": 0,
          "text": {
            "dominant-baseline": "text-before-edge",
            "fill": "#000000",
            "font-family":"DefaultStyle",
            "font-size": "11",
            "font-weight":"normal"
          },
          "transform": "scale(1,1)"}
      }
    )
  })

  test('a Connection with a default Style and an example Placing (Line)', () => {
    
    const shapeJSON = {
        connections: [
        {
          "name": "TestConnection",
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
                "style": "DefaultStyle"
              }
            }
          ]      
        }
      ]
    }

    const generator = create(shapeJSON);

    expect(generator.getConnectionStyle('TestConnection')).toEqual(
      {
        ".connection": {"stroke": "black"},
        ".marker-source": {
          "d": "M 0 0 L 1 1",
          "fill-opacity": 1,
          "stroke": "#000000",
          "stroke-dasharray": "0",
          "stroke-width": 0,
          "text": {
            "dominant-baseline": "text-before-edge",
            "fill": "#000000",
            "font-family":"DefaultStyle",
            "font-size": "11",
            "font-weight":"normal"
          },
          "transform": "scale(1,1)"},
          ".marker-target": {"d": "M 0 0"}
      }
    )
  })
});