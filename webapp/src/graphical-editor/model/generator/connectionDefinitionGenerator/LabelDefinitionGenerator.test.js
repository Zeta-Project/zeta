
import LabelDefinitionGenerator from './LabelDefinitionGenerator';

describe('createLabel', () => {
  function create() {
      const generator = new LabelDefinitionGenerator()
      return generator;
  }

  test('with empty placing', () => {
    const connection = 
      {
        "name": "TestConnection",
        "placings": []
      }
    

    const generator = create()

    expect(generator.createLabelList(connection)).toEqual([])
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
              "type": "label",
            }
          }
        ]
      }
    
              
    const generator = create()

    expect(generator.createLabelList(connection)).toEqual(
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
              "type": "label",
            }
          },
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "ExampleText",
              "id": "placing2",
              "type": "label",
            }
          }
        ]
      }
    
            
    const generator = create()

    expect(generator.createLabelList(connection)).toEqual(
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
});