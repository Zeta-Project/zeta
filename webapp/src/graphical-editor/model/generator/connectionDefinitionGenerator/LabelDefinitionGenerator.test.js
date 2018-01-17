import createLabelList from './LabelDefinitionGenerator';

describe('createLabel', () => {

  test('with empty placing', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": []
      }

    expect(createLabelList(connection)).toEqual([])
  })

  test('with one full defined connection and one placing', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "ExampleText",
              "id": 1234,
              "type": "text",
            }
          }
        ]
      }

    expect(createLabelList(connection)).toEqual(
        [
          {
            position: 1.0,
            attrs: {
                rect: {fill: 'transparent'},
                text: {
                y: 1,
                text: "ExampleText"
                }
            },
            id: 1234
        }
      ]
    )
  }) 

  test('with one full defined connection and two placing', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "ExampleText",
              "id": "placing1",
              "type": "text",
            }
          },
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "ExampleText",
              "id": "placing2",
              "type": "text",
            }
          }
        ]
      }

    expect(createLabelList(connection)).toEqual(
      [
        {
          position: 1.0,
          attrs: {
              rect: {fill: 'transparent'},
              text: {
              y: 1,
              text: "ExampleText"
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
            text: "ExampleText"
            }
        },
        id: "placing2"
        }
      ]
    )
  })

  test('with a Label with a positionOffset of 0.0', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 0.0,
            "shape": {
              "textBody": "ExampleText",
              "id": 1234,
              "type": "text",
            }
          }
        ]
      }

    expect(createLabelList(connection)).toEqual([])
  }) 

  test('with a Placing without type "text"', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 0.0,
            "shape": {
              "textBody": "ExampleText",
              "id": 1234,
              "type": "line",
            }
          }
        ]
      }

    expect(createLabelList(connection)).toEqual([])
  }) 
});