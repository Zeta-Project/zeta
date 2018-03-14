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

  private def allDslV1(): JsValue = {
    Json.obj(
      "diagram" -> diagramDslV1(),
      "style" -> styleDslV1(),
      "shape" -> shapeDslV1(),
      "concept" -> getConcept()
    )
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
       |        "color": "#ffffff"
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
       |        "color": "#ffffff"
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
       |        "color": "#ffffff"
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
       |                "color": "#ffffff"
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

  private def getConcept(): JsValue = {
    Json.parse(
      """
        |{
    |        "enums": [],
    |        "classes": [
    |            {
    |                "name": "InterfaceKlasse",
    |                "description": "",
    |                "abstractness": false,
    |                "superTypeNames": [],
    |                "inputReferenceNames": [
    |                    "BaseClassRealization",
    |                    "Realization"
    |                ],
    |                "outputReferenceNames": [],
    |                "attributes": [],
    |                "methods": []
    |            },
    |            {
    |                "name": "Klasse",
    |                "description": "",
    |                "abstractness": false,
    |                "superTypeNames": [],
    |                "inputReferenceNames": [],
    |                "outputReferenceNames": [
    |                    "Inheritance",
    |                    "BaseClassRealization"
    |                ],
    |                "attributes": [],
    |                "methods": []
    |            },
    |            {
    |                "name": "AbstractKlasse",
    |                "description": "",
    |                "abstractness": false,
    |                "superTypeNames": [],
    |                "inputReferenceNames": [
    |                    "Inheritance"
    |                ],
    |                "outputReferenceNames": [
    |                    "Realization"
    |                ],
    |                "attributes": [],
    |                "methods": []
    |            }
    |        ],
    |        "references": [
    |            {
    |                "name": "Inheritance",
    |                "description": "",
    |                "sourceDeletionDeletesTarget": false,
    |                "targetDeletionDeletesSource": false,
    |                "sourceClassName": "Klasse",
    |                "targetClassName": "AbstractKlasse",
    |                "attributes": [],
    |                "methods": []
    |            },
    |            {
    |                "name": "BaseClassRealization",
    |                "description": "",
    |                "sourceDeletionDeletesTarget": false,
    |                "targetDeletionDeletesSource": false,
    |                "sourceClassName": "Klasse",
    |                "targetClassName": "InterfaceKlasse",
    |                "attributes": [],
    |                "methods": []
    |            },
    |            {
    |                "name": "Realization",
    |                "description": "",
    |                "sourceDeletionDeletesTarget": false,
    |                "targetDeletionDeletesSource": false,
    |                "sourceClassName": "AbstractKlasse",
    |                "targetClassName": "InterfaceKlasse",
    |                "attributes": [],
    |                "methods": []
    |            }
    |        ],
    |        "attributes": [
    |            {
    |                "name": "",
    |                "globalUnique": false,
    |                "localUnique": false,
    |                "type": "string",
    |                "default": {
    |                    "type": "string",
    |                    "value": ""
    |                },
    |                "constant": false,
    |                "singleAssignment": false,
    |                "expression": "",
    |                "ordered": false,
    |                "transient": false
    |            },
    |            {
    |                "name": "",
    |                "globalUnique": false,
    |                "localUnique": false,
    |                "type": "string",
    |                "default": {
    |                    "type": "string",
    |                    "value": ""
    |                },
    |                "constant": false,
    |                "singleAssignment": false,
    |                "expression": "",
    |                "ordered": false,
    |                "transient": false
    |            },
    |            {
    |                "name": "",
    |                "globalUnique": false,
    |                "localUnique": false,
    |                "type": "string",
    |                "default": {
    |                    "type": "string",
    |                    "value": ""
    |                },
    |                "constant": false,
    |                "singleAssignment": false,
    |                "expression": "",
    |                "ordered": false,
    |                "transient": false
    |            }
    |        ],
    |        "methods": [],
    |        "uiState": "{\"cells\":[{\"position\":{\"x\":0,\"y\":0},\"size\":{\"width\":0,\"height\":0},\"angle\":0,\"id\":\"menum_container\",\"type\":\"mcore.Enum\",\"markup\":\"<g />\",\"name\":\"mEnumContainer\",\"m_enum\":[],\"z\":1,\"m_attributes\":[{\"name\":\"\",\"upperBound\":-1,\"lowerBound\":0,\"default\":\"\",\"typ\":\"String\",\"expression\":\"\",\"localUnique\":false,\"globalUnique\":false,\"constant\":false,\"ordered\":false,\"transient\":false,\"singleAssignment\":false},{\"name\":\"\",\"upperBound\":-1,\"lowerBound\":0,\"default\":\"\",\"typ\":\"String\",\"expression\":\"\",\"localUnique\":false,\"globalUnique\":false,\"constant\":false,\"ordered\":false,\"transient\":false,\"singleAssignment\":false},{\"name\":\"\",\"upperBound\":-1,\"lowerBound\":0,\"default\":\"\",\"typ\":\"String\",\"expression\":\"\",\"localUnique\":false,\"globalUnique\":false,\"constant\":false,\"ordered\":false,\"transient\":false,\"singleAssignment\":false}],\"m_methods\":[],\"attrs\":{}},{\"position\":{\"x\":0,\"y\":0},\"size\":{\"width\":0,\"height\":0},\"angle\":0,\"id\":\"mattribute_container\",\"type\":\"mcore.Attribute\",\"markup\":\"<g />\",\"name\":\"mAttributeContainer\",\"m_Attribute\":[],\"z\":2,\"attrs\":{}},{\"type\":\"uml.Class\",\"name\":\"InterfaceKlasse\",\"m_attributes\":[],\"position\":{\"x\":30,\"y\":50},\"size\":{\"width\":120,\"height\":70},\"angle\":0,\"id\":\"1a94c26d-0b4d-43a7-a052-cb1f38c63996\",\"z\":3,\"description\":\"\",\"m_methods\":[],\"linkdef_input\":[],\"linkdef_output\":[],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":20,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"InterfaceKlasse\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Class\",\"name\":\"Klasse\",\"m_attributes\":[],\"position\":{\"x\":130,\"y\":420},\"size\":{\"width\":130,\"height\":70},\"angle\":0,\"id\":\"cfc1c745-6dc0-4b8f-a80c-cfbbd10ca902\",\"z\":4,\"description\":\"\",\"m_methods\":[],\"linkdef_input\":[],\"linkdef_output\":[],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":20,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"Klasse\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Class\",\"name\":\"AbstractKlasse\",\"m_attributes\":[],\"position\":{\"x\":430,\"y\":50},\"size\":{\"width\":150,\"height\":60},\"angle\":0,\"id\":\"136868ee-9cf1-4c29-9950-61b8ac85c656\",\"z\":6,\"description\":\"\",\"m_methods\":[],\"linkdef_input\":[],\"linkdef_output\":[],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":20,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"AbstractKlasse\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Association\",\"name\":\"Inheritance\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"cfc1c745-6dc0-4b8f-a80c-cfbbd10ca902\"},\"target\":{\"id\":\"136868ee-9cf1-4c29-9950-61b8ac85c656\"},\"id\":\"73dbe443-068b-4889-9003-581f155b4b57\",\"subtype\":\"Association\",\"z\":7,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"Inheritance\"}}}],\"vertices\":[],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"attrs\":{}},{\"type\":\"uml.Association\",\"name\":\"BaseClassRealization\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"cfc1c745-6dc0-4b8f-a80c-cfbbd10ca902\"},\"target\":{\"id\":\"1a94c26d-0b4d-43a7-a052-cb1f38c63996\"},\"id\":\"b2afa12d-2f3e-4b73-bd44-d4f4821b2279\",\"subtype\":\"Association\",\"z\":8,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"BaseClassRealization\"}}}],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"attrs\":{}},{\"type\":\"uml.Association\",\"name\":\"Realization\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"136868ee-9cf1-4c29-9950-61b8ac85c656\"},\"target\":{\"id\":\"1a94c26d-0b4d-43a7-a052-cb1f38c63996\"},\"id\":\"27ec6821-aad9-4849-b435-697a19915a3a\",\"subtype\":\"Association\",\"z\":9,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"Realization\"}}}],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"attrs\":{}}]}"
    |    }
      """.stripMargin)
  }
}
