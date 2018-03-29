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
       |        "color": "#FFFFFF"
       |      },
       |      "font": {
       |        "size":20
       |      },
       |      "line": {
       |        "color": "#000000",
       |        "style": "dash",
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
       |        "style": "dash",
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
       |        "style": "solid",
       |        "transparent": false,
       |        "width": 1
       |      },
       |      "name": "X",
       |      "transparency": 1.0
       |    },
       |    {
       |      "background": {
       |        "color": "#FFFFFF"
       |      },
       |      "name": "realization"
       |    },
       |    {
       |      "background": {
       |        "color": "#000000"
       |      },
       |      "name": "component"
       |    },
       |    {
       |      "background": {
       |        "color": "#FFFFFF"
       |      },
       |      "name": "aggregation"
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
        |          "id": "0b800d24-7d92-4fad-89df-3203d277fe4f",
        |          "style": "X",
        |          "sizeHeight": 50,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 0,
        |            "y": 0
        |          },
        |          "children": ["e477df6c-e8da-462e-9dc3-2f88d830547f"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "e477df6c-e8da-462e-9dc3-2f88d830547f",
        |          "style": "X",
        |          "textBody": "Klasse",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "0b800d24-7d92-4fad-89df-3203d277fe4f"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "d62b0c84-6348-4cd3-b308-508d25012db9",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 0,
        |            "y": 50
        |          },
        |          "children": ["ade81ec0-d7d0-44e0-ab85-0e6253c45bc1"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "ade81ec0-d7d0-44e0-ab85-0e6253c45bc1",
        |          "style": "X",
        |          "textBody": "Attribute",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "d62b0c84-6348-4cd3-b308-508d25012db9"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "cc40a695-e82d-4ea7-b5e6-86ac2ae249bd",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 0,
        |            "y": 150
        |          },
        |          "children": ["90a7d93a-5efd-4c40-a90f-e74a9f76bfe3"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "90a7d93a-5efd-4c40-a90f-e74a9f76bfe3",
        |          "style": "X",
        |          "textBody": "Methoden",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "cc40a695-e82d-4ea7-b5e6-86ac2ae249bd"
        |        }
        |      ]
        |    },
        |    {
        |      "name": "abstractKlasse",
        |      "style": "X",
        |      "elements": [
        |        {
        |          "type": "rectangle",
        |          "id": "f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb",
        |          "style": "X",
        |          "sizeHeight": 50,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "children": ["b5762097-dfcf-41a9-8b11-2190c618e6e9"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "b5762097-dfcf-41a9-8b11-2190c618e6e9",
        |          "style": "X",
        |          "textBody": "<<AbstractClass>>",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "bd0fa679-b080-4d84-9eeb-fe7ae99a42cd",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 50
        |          },
        |          "children": ["0685d1f3-9273-42f9-b15f-34ea4a6be378"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "0685d1f3-9273-42f9-b15f-34ea4a6be378",
        |          "style": "X",
        |          "textBody": "Attribute",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "bd0fa679-b080-4d84-9eeb-fe7ae99a42cd"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "8586b658-768a-4273-b366-d4f1597c561e",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 150
        |          },
        |          "children": ["60cee325-f76b-4d41-b08f-e51427aadf66"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "60cee325-f76b-4d41-b08f-e51427aadf66",
        |          "style": "X",
        |          "textBody": "Methoden",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "8586b658-768a-4273-b366-d4f1597c561e"
        |        }
        |      ]
        |    },
        |    {
        |      "name": "interface",
        |      "style": "X",
        |      "elements": [
        |        {
        |          "type": "rectangle",
        |          "id": "9461a54c-fbb2-49a2-94ac-77848fbc1f88",
        |          "style": "X",
        |          "sizeHeight": 50,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "children": ["418aa18b-d386-4d43-b74e-9b0701ef2dee"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "418aa18b-d386-4d43-b74e-9b0701ef2dee",
        |          "style": "X",
        |          "textBody": "<<Interface>>",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "9461a54c-fbb2-49a2-94ac-77848fbc1f88"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "73e17224-4508-463a-a388-c299a5adde76",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 50
        |          },
        |          "children": ["5215ddbc-bcb1-414d-878c-4cea63c06ff5"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "5215ddbc-bcb1-414d-878c-4cea63c06ff5",
        |          "style": "X",
        |          "textBody": "Attribute",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "73e17224-4508-463a-a388-c299a5adde76"
        |        },
        |        {
        |          "type": "rectangle",
        |          "id": "75f2205e-9a60-45e9-9d33-98e84de80d66",
        |          "style": "X",
        |          "sizeHeight": 100,
        |          "sizeWidth": 200,
        |          "position": {
        |            "x": 10,
        |            "y": 150
        |          },
        |          "children": ["2636f960-1374-46ab-b6a0-fc8e2cb3d80d"]
        |        },
        |        {
        |          "type": "text",
        |          "id": "2636f960-1374-46ab-b6a0-fc8e2cb3d80d",
        |          "style": "X",
        |          "textBody": "Methoden",
        |          "sizeHeight": 40,
        |          "sizeWidth": 10,
        |          "position": {
        |            "x": 10,
        |            "y": 0
        |          },
        |          "parent": "75f2205e-9a60-45e9-9d33-98e84de80d66"
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
        |      "style": "Y",
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
        |            "style": "realization"
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
        |            "style": "component"
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
        |            "style": "aggregation"
        |          }
        |        }
        |      ]
        |    }
        |  ]
        |}
        |
        |
      """
      .stripMargin
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
