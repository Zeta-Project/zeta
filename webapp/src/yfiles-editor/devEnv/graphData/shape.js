export default {
  "shapes": {
    "nodes": [
      {
        "name": "KnotenNode",
        "conceptElement": "Knoten",
        "edges": [
          "hatKindEdge"
        ],
        "size": {
          "width": 10,
          "height": 15,
          "widthMax": 20,
          "widthMin": 10,
          "heightMax": 25,
          "heightMin": 15
        },
        "svg": "SVG",
        "style": "Style",
        "resizing": {
          "horizontal": false,
          "vertical": false,
          "proportional": true
        },
        "geoElements": [
          {
            "size": {
              "width": 10,
              "height": 15
            },
            "position": {
              "x": 3,
              "y": 4
            },
            "childGeoElements": [
              {
                "identifier": "überschrift",
                "size": {
                  "width": 10,
                  "height": 15
                },
                "position": {
                  "x": 3,
                  "y": 4
                },
                "multiline": false,
                "align": {
                  "horizontal": "middle",
                  "vertical": "middle"
                },
                "style": "Style",
                "type": "textfield",
                "id": "UniqueID"
              },
              {
                "identifier": "compartment",
                "size": {
                  "width": 10,
                  "height": 15
                },
                "position": {
                  "x": 3,
                  "y": 4
                },
                "childGeoElements": [
                  {
                    "text": "kennNummer = ",
                    "size": {
                      "width": 10,
                      "height": 15
                    },
                    "position": {
                      "x": 3,
                      "y": 4
                    },
                    "style": "Style",
                    "type": "statictext",
                    "id": "UniqueID"
                  },
                  {
                    "identifier": "kennNummerWert",
                    "size": {
                      "width": 10,
                      "height": 15
                    },
                    "position": {
                      "x": 3,
                      "y": 4
                    },
                    "multiline": false,
                    "align": {
                      "horizontal": "middle",
                      "vertical": "middle"
                    },
                    "style": "Style",
                    "type": "textField",
                    "id": "UniqueID"
                  }
                ],
                "style": "Style",
                "type": "compartement",
                "id": "UniqueID"
              }
            ],
            "style": "BlackWhiteStyle",
            "type": "ellipse",
            "id": "UniqueID"
          },
          {
            "size": {
              "width": 10,
              "height": 15
            },
            "position": {
              "x": 3,
              "y": 4
            },
            "editable": true,
            "for": {
              "each": "hatAttribut",
              "as": "attribut"
            },
            "childGeoElements": [
              {
              }
            ],
            "style": "Style",
            "type": "repeating",
            "id": "UniqueID"
          }
        ]
      },
      {
        "name": "MatroschkaNode",
        "conceptElement": "Knoten",
        "edges": [],
        "size": {
          "width": 10,
          "height": 15,
          "widthMax": 20,
          "widthMin": 10,
          "heightMax": 25,
          "heightMin": 15
        },
        "svg": "SVG",
        "style": "Style",
        "resizing": {
          "horizontal": false,
          "vertical": false,
          "proportional": true
        },
        "geoElements": [
        ]
      }
    ],
    "edges": [
      {
        "name": "hatKindEdge",
        "conceptElement": "Knoten.hatKind",
        "target": "Knoten",
        "placings": [
          {
            "style": "PfeilStyle",
            "position": {
              "distance": 1,
              "offset": 1.0
            },
            "geoElement": {
              "geoElement": {
                "points": [
                  {
                    "x": 0,
                    "y": 0
                  },
                  {
                    "x": 20,
                    "y": 10
                  },
                  {
                    "x": 40,
                    "y": 0
                  },
                  {
                    "x": 20,
                    "y": -10
                  }
                ],
                "type": "polygon",
                "id": "UniqueID"
              }
            }
          }
          ]
      }
      ]
  }
}