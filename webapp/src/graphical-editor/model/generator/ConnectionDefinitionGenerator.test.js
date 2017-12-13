import ConnectionDefinitionGenerator from './ConnectionDefinitionGenerator';

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

      const generator = create([])

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
                        "id": "123Auto",
                        "type": "Label",
                      }
                    }
                  ]
                }
              ]
                
        const generator = create(connections)

        expect(generator.getLabels('Connection1')).toEqual(
            [{
                position: 1.0,
                attrs: {
                    rect: {fill: 'transparent'},
                    text: {
                    y: 1,
                    text: "Hallo"
                    }
                },
                id: "123Auto"
            }]
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
          [{
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
        }]
      )
  })
});