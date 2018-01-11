import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';
import StyleGenerator from './StyleGenerator'

describe('createLabel', () => {
  function create(connections) {
      const generator = new ConnectionDefinitionGenerator(connections)
      return generator;
  }

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getLabels('Connection1')).toEqual([])
  })
});

describe('createPlacing', () => {

  test('with empty connetions', () => {
    const generator = new ConnectionDefinitionGenerator([])

    expect(generator.getPlacings('Connection1')).toEqual([])
  })
});

describe('getConnectionStyle', () => {
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

  test('with empty connetions', () => {
    const generator = create([])

    expect(generator.getConnectionStyle('Connection1')).toEqual({})
  })

  test('with an example Placing (Line)', () => {
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
        ".marker-source": {
          "d": "M 0 0 L 1 1",
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
        "style": "DefaultStyle"
      }
    ]

    const generator = create(connections);
    expect(generator.getConnectionStyle('Connection1')).toEqual(
      {
        '.marker-target': {"d": "M 0 0"},
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

});