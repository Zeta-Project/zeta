package de.htwg.zeta.server.controller.restApi

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.zeta.server.silhouette.ZetaEnv
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Result


/**
 * REST-ful API for concept-API definitions
 */
class DslRestApi @Inject()() extends Controller with Logging {

  /**
   * Get a single concept-API v1 JSON
   *
   * @param id      Identifier of Concept
   * @param apiType Type of the asked concept-API instance
   * @param request The request
   * @return A JSON of a concept-API
   */
  def getDSL(id: UUID, apiType: String)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    apiType match {
      case "diagram" => Future(Ok(diagramDslV1()))
      case "shape" => Future(Ok(shapeDslV1()))
      case "style" => Future(Ok(styleDslV1()))
    }
  }

  def getTotalApiV1(id: UUID)(request: SecuredRequest[ZetaEnv, AnyContent]): Future[Result] = {
    Future(Ok(allDslV1()))
  }



  private def diagramDslV1(): JsValue = {
    Json.parse(
      """
        {
       |  "model": {
       |    "edges": [
       |      {
       |        "connection": {
       |          "name": "inheritance"
       |        },
       |        "from": "Klasse",
       |        "mReference": "Inheritance",
       |        "name": "inheritance",
       |        "to": "AbstractKlasse"
       |      },
       |      {
       |        "connection": {
       |          "name": "realization"
       |        },
       |        "from": "AbstractKlasse",
       |        "mReference": "Realization",
       |        "name": "realization",
       |        "to": "InterfaceKlasse"
       |      },
       |      {
       |        "connection": {
       |          "name": "realization"
       |        },
       |        "from": "Klasse",
       |        "mReference": "BaseClassRealization",
       |        "name": "BaseClassRealization",
       |        "to": "InterfaceKlasse"
       |      },
       |      {
       |        "connection": {
       |          "name": "component"
       |        },
       |        "from": "Klasse",
       |        "mReference": "Component",
       |        "name": "component",
       |        "to": "Klasse"
       |      },
       |      {
       |        "connection": {
       |          "name": "aggregation"
       |        },
       |        "from": "Klasse",
       |        "mReference": "Aggregation",
       |        "name": "aggregation",
       |        "to": "Klasse"
       |      }
       |    ],
       |    "nodes":[
       |      {
       |        "mClass": "Klasse",
       |        "name": "classNode",
       |        "palette": "Class",
       |        "shape": {
       |          "name": "klasse"
       |        }
       |      },
       |      {
       |        "mClass": "AbstractKlasse",
       |        "name": "abClassNode",
       |        "palette": "AbstractClass",
       |        "shape": {
       |          "name": "abstractKlasse"
       |        }
       |      },
       |      {
       |        "mClass": "InterfaceKlasse",
       |        "name": "inClassNode",
       |        "palette": "Interface",
       |        "shape": {
       |          "name": "interface"
       |        }
       |      }
       |    ]
       |  }
       |}
      """.stripMargin)
  }

  private def styleDslV1(): JsValue = {
    Json.parse(
      """
       |{
       |  "styles": [
       |    {
       |      "background": {
       |        "color": "#FFFFFF"
       |      },
       |      "font": {
       |        "size":20
       |      },
       |      "line": {
       |        "color": "#000000",
       |        "style": "DASH",
       |        "transparent": false,
       |        "width": 1
       |      },
       |      "name": "Y",
       |      "transparency": 1.0
       |    },
       |    {
       |      "background": {
       |        "color": "#FFFFFF"
       |      },
       |      "font": {
       |        "size":10
       |      },
       |      "line": {
       |        "color": "#000000",
       |        "style": "DASH",
       |        "transparent": false,
       |        "width": 1
       |      },
       |      "name": "ClassText",
       |      "transparency": 1.0
       |    },
       |    {
       |      "background": {
       |        "color": "#FFFFFF"
       |      },
       |      "font": {
       |        "size":20
       |      },
       |      "line": {
       |        "color": "#000000",
       |        "style": "SOLID",
       |        "transparent": false,
       |        "width": 1
       |      },
       |      "name": "X",
       |      "transparency": 1.0
       |    }
       |  ]
       |}
       |
      """.stripMargin)

  }

  private def shapeDslV1(): JsValue = {
    Json.parse(
      """
       |{
       |  "shapes": [
       |    {
       |      "name": "klasse",
       |      "style": "X",
       |      "elements": [
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle1",
       |          "sizeHeight": 50,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 0,
       |            "y": 0
       |          },
       |          "children": "text1"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text1",
       |          "textBody": "Klasse",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle1"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle2",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 0,
       |            "y": 50
       |          },
       |          "children": "text2"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text2",
       |          "textBody": "Attribute",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle2"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle3",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 0,
       |            "y": 150
       |          },
       |          "children": "text3"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text3",
       |          "textBody": "Methoden",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle3"
       |        }
       |      ]
       |    },
       |    {
       |      "name": "abstractKlasse",
       |      "style": "X",
       |      "elements": [
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle11",
       |          "sizeHeight": 50,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 0
       |          },
       |          "children": "text11"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text11",
       |          "textBody": "<<AbstractClass>>",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle11"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle21",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 50
       |          },
       |          "children": "text21"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text21",
       |          "textBody": "Attribute",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle21"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle31",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 150
       |          },
       |          "children": "text31"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text31",
       |          "textBody": "Methoden",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle31"
       |        }
       |      ]
       |    },
       |    {
       |      "name": "interface",
       |      "style": "X",
       |      "elements": [
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle113",
       |          "sizeHeight": 50,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 0
       |          },
       |          "children": "text113"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text113",
       |          "textBody": "<<Interface>>",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle113"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle213",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 50
       |          },
       |          "children": "text213"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text213",
       |          "textBody": "Attribute",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle213"
       |        },
       |        {
       |          "type": "rectangle",
       |          "id": "rectangle313",
       |          "sizeHeight": 100,
       |          "sizeWidth": 200,
       |          "position": {
       |            "x": 10,
       |            "y": 150
       |          },
       |          "children": "text313"
       |        },
       |        {
       |          "type": "text",
       |          "id": "text313",
       |          "textBody": "Methoden",
       |          "sizeHeight": 40,
       |          "sizeWidth": 10,
       |          "parent": "rectangle313"
       |        }
       |      ]
       |    }
       |  ],
       |  "connections": [
       |    {
       |      "name": "inheritance",
       |      "style": "X",
       |      "placings": [
       |        {
       |          "positionOffset": 1.0,
       |          "shape": {
       |            "type": "polygon",
       |            "points": [
       |              {
       |                "x": -10,
       |                "y": 10
       |              },
       |              {
       |                "x": 0,
       |                "y": 0
       |              },
       |              {
       |                "x": -10,
       |                "y": -10
       |              }
       |            ]
       |          }
       |        }
       |      ]
       |    },
       |    {
       |      "name": "realization",
       |      "style": "X",
       |      "placings": [
       |        {
       |          "positionOffset": 1.0,
       |          "shape": {
       |            "type": "polygon",
       |            "points": [
       |              {
       |                "x": -10,
       |                "y": 10
       |              },
       |              {
       |                "x": 0,
       |                "y": 0
       |              },
       |              {
       |                "x": -10,
       |                "y": -10
       |              }
       |            ],
       |            "style": {
       |              "line": {
       |                "style": "SOLID"
       |              }
       |            }
       |          }
       |        }
       |      ]
       |    },
       |    {
       |      "name": "component",
       |      "style": "X",
       |      "placings": [
       |        {
       |          "positionOffset": 1.0,
       |          "shape": {
       |            "type": "polygon",
       |            "points": [
       |              {
       |                "x": 0,
       |                "y": 0
       |              },
       |              {
       |                "x": 20,
       |                "y": 10
       |              },
       |              {
       |                "x": 40,
       |                "y": 0
       |              },
       |              {
       |                "x": 20,
       |                "y": -10
       |              }
       |            ],
       |            "style": {
       |              "background": {
       |                "color": "#000000"
       |              }
       |            }
       |          }
       |        }
       |      ]
       |    },
       |    {
       |      "name": "aggregation",
       |      "style": "X",
       |      "placings": [
       |        {
       |          "positionOffset": 1.0,
       |          "shape": {
       |            "type": "polygon",
       |            "points": [
       |              {
       |                "x": 0,
       |                "y": 0
       |              },
       |              {
       |                "x": -20,
       |                "y": 10
       |              },
       |              {
       |                "x": -40,
       |                "y": 0
       |              },
       |              {
       |                "x": -20,
       |                "y": -10
       |              }
       |            ],
       |            "style": {
       |              "background": {
       |                "color": "#FFFFFF"
       |              }
       |            }
       |          }
       |        }
       |      ]
       |    }
       |  ]
       |}
       |
       |
      """.stripMargin
    )
  }

  private def diagramDslV2(): JsValue = {
    Json.parse(
      """
        {
 |  "diagrams": [
 |    {
 |      "name": "Test",
 |      "palettes": [
 |        {
 |          "name": "Class",
 |          "nodes": [
 |            "classNode"
 |          ]
 |        },
 |        {
 |          "name": "AbstractClass",
 |          "nodes": [
 |            "abClassNode"
 |          ]
 |        },
 |        {
 |          "name": "Interface",
 |          "nodes": [
 |            "inClassNode"
 |          ]
 |        }
 |      ]
 |    }
 |  ]
 |}
      """.stripMargin)
  }

  private def styleDslV2(): JsValue = {
    Json.parse(
      """
        {
        |  "styles": [
        |    {
        |      "background": {
        |        "color": "rgba(255, 255, 255, 1)"
        |      },
        |      "font": {
        |        "size":20
        |      },
        |      "line": {
        |        "color": "rgba(0, 0, 0, 1)",
        |        "style": "DASH",
        |        "width": 1
        |      },
        |      "name": "Y"
        |    },
        |    {
        |      "background": {
        |        "color": "rgba(255, 255, 255, 1)"
        |      },
        |      "font": {
        |        "size":10
        |      },
        |      "line": {
        |        "color": "rgba(0, 0, 0, 1)",
        |        "style": "DASH",
        |        "width": 1
        |      },
        |      "name": "ClassText",
        |      "transparency": 1.0
        |    },
        |    {
        |      "background": {
        |        "color": "rgba(255, 255, 255, 1)"
        |      },
        |      "font": {
        |        "size":20
        |      },
        |      "line": {
        |        "color": "rgba(0, 0, 0, 1)",
        |        "style": "SOLID",
        |        "width": 1
        |      },
        |      "name": "X"
        |    }
        |  ]
        |}
      """.stripMargin)

  }

  private def shapeDslV2(): JsValue = {
    Json.parse(
      """
        {
 |  "shapes": {
 |    "nodes": [
 |      {
 |        "name": "classNode",
 |        "conceptElement": "Klasse",
 |        "style": "X",
 |        "edges": [
 |          "inheritance",
 |          "BaseClassRealization",
 |          "component",
 |          "aggregation"
 |        ],
 |        "geoElements": [
 |          {
 |            "type": "rectangle",
 |            "id":"rt1",
 |            "size": {
 |              "width": 200,
 |              "height": 50
 |            },
 |            "position": {
 |              "x": 0,
 |              "y": 0
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st1",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Klasse"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt2",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 0,
 |              "y": 50
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st2",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Attribute"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt3",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 0,
 |              "y": 150
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st3",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Methoden"
 |              }
 |            ]
 |          }
 |        ]
 |      },
 |      {
 |        "name": "abClassNode",
 |        "conceptElement": "AbstractKlasse",
 |        "style": "X",
 |        "edges": [
 |          "realization"
 |        ],
 |        "geoElements": [
 |          {
 |            "type": "rectangle",
 |            "id":"rt4",
 |            "size": {
 |              "width": 200,
 |              "height": 50
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 0
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st4",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "<<AbstractClass>>"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt5",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 50
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st5",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Attribute"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt6",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 150
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st6",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Methoden"
 |              }
 |            ]
 |          }
 |        ]
 |      },
 |      {
 |        "name": "inClassNode",
 |        "conceptElement": "InterfaceKlasse",
 |        "style": "X",
 |        "edges": [
 |        ],
 |        "geoElements": [
 |          {
 |            "type": "rectangle",
 |            "id":"rt7",
 |            "size": {
 |              "width": 200,
 |              "height": 50
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 0
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st7",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "<<Interface>>"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt8",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 50
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st8",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Attribute"
 |              }
 |            ]
 |          },
 |          {
 |            "type": "rectangle",
 |            "id":"rt9",
 |            "size": {
 |              "width": 200,
 |              "height": 100
 |            },
 |            "position": {
 |              "x": 10,
 |              "y": 150
 |            },
 |            "childGeoElements": [
 |              {
 |                "type": "statictext",
 |                "id":"st9",
 |                "size": {
 |                  "width": 10,
 |                  "height": 40
 |                },
 |                "text": "Methoden"
 |              }
 |            ]
 |          }
 |        ]
 |      }
 |    ],
 |    "edges": [
 |      {
 |        "name": "inheritance",
 |        "style": "X",
 |        "conceptElement": "Inheritance",
 |        "target": "AbstractKlasse",
 |        "placings": [
 |          {
 |            "position": {
 |              "offset": 1.0
 |            },
 |            "geoElement": {
 |              "type": "polygon",
 |              "id":"py1",
 |              "points": [
 |                {
 |                  "x": -10,
 |                  "y": 10
 |                },
 |                {
 |                  "x": 0,
 |                  "y": 0
 |                },
 |                {
 |                  "x": -10,
 |                  "y": -10
 |                }
 |              ]
 |            }
 |          }
 |        ]
 |      },
 |      {
 |        "name": "realization",
 |        "style": "Y",
 |        "conceptElement": "Realization",
 |        "target": "InterfaceKlasse",
 |        "placings": [
 |          {
 |            "position": {
 |              "offset": 1.0
 |            },
 |            "geoElement": {
 |              "type": "polygon",
 |              "id":"py2",
 |              "style": {
 |                "line": {
 |                  "style": "SOLID"
 |                }
 |              },
 |              "points": [
 |                {
 |                  "x": -10,
 |                  "y": 10
 |                },
 |                {
 |                  "x": 0,
 |                  "y": 0
 |                },
 |                {
 |                  "x": -10,
 |                  "y": -10
 |                }
 |              ]
 |            }
 |          }
 |        ]
 |      },
 |      {
 |        "name": "component",
 |        "style": "X",
 |        "conceptElement": "Component",
 |        "target": "Klasse",
 |        "placings": [
 |          {
 |            "position": {
 |              "offset": 1.0
 |            },
 |            "geoElement": {
 |              "type": "polygon",
 |              "id":"py3",
 |              "style": {
 |                "background-color": "rgba(0, 0, 0, 1)"
 |              },
 |              "points": [
 |                {
 |                  "x": 0,
 |                  "y": 0
 |                },
 |                {
 |                  "x": 20,
 |                  "y": 10
 |                },
 |                {
 |                  "x": 40,
 |                  "y": 0
 |                },
 |                {
 |                  "x": 20,
 |                  "y": -10
 |                }
 |              ]
 |            }
 |          }
 |        ]
 |      },
 |      {
 |        "name": "aggregation",
 |        "style": "X",
 |        "conceptElement": "Aggregation",
 |        "target": "Klasse",
 |        "placings": [
 |          {
 |            "position": {
 |              "offset": 1.0
 |            },
 |            "geoElement": {
 |              "type": "polygon",
 |              "id":"py4",
 |              "style": {
 |                "background-color": "rgba(255, 255, 255, 1)"
 |              },
 |              "points": [
 |                {
 |                  "x": 0,
 |                  "y": 0
 |                },
 |                {
 |                  "x": -20,
 |                  "y": 10
 |                },
 |                {
 |                  "x": -40,
 |                  "y": 0
 |                },
 |                {
 |                  "x": -20,
 |                  "y": -10
 |                }
 |              ]
 |            }
 |          }
 |        ]
 |      },
 |      {
 |        "name": "BaseClassRealization",
 |        "style": "Y",
 |        "conceptElement": "BaseClassRealization",
 |        "target": "InterfaceKlasse",
 |        "placings": [
 |          {
 |            "position": {
 |              "offset": 1.0
 |            },
 |            "geoElement": {
 |              "type": "polygon",
 |              "id":"py5",
 |              "style": {
 |                "line": {
 |                  "style": "SOLID"
 |                }
 |              },
 |              "points": [
 |                {
 |                  "x": -10,
 |                  "y": 10
 |                },
 |                {
 |                  "x": 0,
 |                  "y": 0
 |                },
 |                {
 |                  "x": -10,
 |                  "y": -10
 |                }
 |              ]
 |            }
 |          }
 |        ]
 |      }
 |    ]
 |  }
 |}
      """.stripMargin
    )
  }

  private def allDslV1(): JsValue = {
    Json.obj(
      "diagram" -> diagramDslV1(),
      "style" -> styleDslV1(),
      "shape" -> shapeDslV1()
    )
  }
}
