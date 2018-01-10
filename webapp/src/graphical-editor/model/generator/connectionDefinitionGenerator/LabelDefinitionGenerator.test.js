
import LabelDefinitionGenerator from './LabelDefinitionGenerator';

const defaultTestId = "1234"

describe('createLabel', () => {
  function create() {
      const generator = new LabelDefinitionGenerator()
      return generator;
  }

  test('with empty placing', () => {
    const connection = 
      {
        "name": "Connection1",
        "placings": []
      }
    

    const generator = create()

    expect(generator.createLabelList(connection)).toEqual([])
  })

  test('with one full defined connection and one placing', () => {
    const connection = 
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
              "id": defaultTestId,
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
                text: "Hallo"
                }
            },
            id: defaultTestId
        }
      ]
    )
  }) 

  test('with one full defined connection and two placing', () => {
    const connection = 
      {
        "name": "Connection1",
        "placings": [
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
              "id": "placing1",
              "type": "label",
            }
          },
          {
            "positionDistance": 1,
            "positionOffset": 1.0,
            "shape": {
              "textBody": "Hallo",
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