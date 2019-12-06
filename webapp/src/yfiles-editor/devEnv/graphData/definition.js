export default {
    "enums": [],
    "classes": [
    {
        "name": "Place",
        "description": "",
        "abstractness": true,
        "superTypeNames": [],
        "inputReferenceNames": [
            "toPlace",
            "placeToPlace"
        ],
        "outputReferenceNames": [
            "toTransition",
            "placeToPlace"
        ],
        "attributes": [
            {
                "name": "Test",
                "globalUnique": false,
                "localUnique": false,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": false,
                "transient": false
            }
        ],
        "methods": [
            {
                "name": "something",
                "parameters": [],
                "description": "",
                "returnType": "String",
                "code": ""
            },
            {
                "name": "somethingElse",
                "parameters": [],
                "description": "",
                "returnType": "String",
                "code": ""
            }
        ]
    },
    {
        "name": "Transition",
        "description": "",
        "abstractness": false,
        "superTypeNames": [],
        "inputReferenceNames": [
            "toTransition"
        ],
        "outputReferenceNames": [
            "toPlace"
        ],
        "attributes": [],
        "methods": []
    },
    {
        "name": "Token",
        "description": "",
        "abstractness": false,
        "superTypeNames": [],
        "inputReferenceNames": [],
        "outputReferenceNames": [],
        "attributes": [],
        "methods": []
    }
],
    "references": [
    {
        "name": "toTransition",
        "description": "",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "sourceClassName": "Place",
        "targetClassName": "Transition",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "toPlace",
        "description": "",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "sourceClassName": "Transition",
        "targetClassName": "Place",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "placeToPlace",
        "description": "",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "sourceClassName": "Place",
        "targetClassName": "Place",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    }
],
    "attributes": [],
    "methods": [],
    "uiState": "{\"cells\":[{\"position\":{\"x\":0,\"y\":0},\"size\":{\"width\":0,\"height\":0},\"angle\":0,\"id\":\"menum_container\",\"type\":\"mcore.Enum\",\"markup\":\"<g />\",\"name\":\"mEnumContainer\",\"m_enum\":[],\"z\":1,\"attrs\":{}},{\"type\":\"uml.Class\",\"name\":\"Place\",\"m_attributes\":[{\"name\":\"Test\",\"upperBound\":-1,\"lowerBound\":0,\"default\":\"\",\"typ\":\"String\",\"expression\":\"\",\"localUnique\":false,\"globalUnique\":false,\"constant\":false,\"ordered\":false,\"transient\":false,\"singleAssignment\":false}],\"position\":{\"x\":119.99999999999989,\"y\":90.00000000000006},\"size\":{\"width\":90,\"height\":180},\"angle\":0,\"id\":\"c88b240a-0e4b-4a50-ab2c-a302510cfe8c\",\"z\":2,\"description\":\"\",\"m_methods\":[{\"name\":\"\",\"code\":\"\",\"description\":\"\",\"parameters\":[],\"returnType\":\"String\"},{\"name\":\"\",\"code\":\"\",\"description\":\"\",\"parameters\":[],\"returnType\":\"String\"}],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":40,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"Place\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"Test\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Class\",\"name\":\"Transition\",\"m_attributes\":[],\"position\":{\"x\":340,\"y\":90},\"size\":{\"width\":60,\"height\":60},\"angle\":0,\"id\":\"d1f6046e-2733-4571-ae0c-4f586d1f6f52\",\"z\":3,\"description\":\"\",\"m_methods\":[],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":20,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"Transition\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Class\",\"name\":\"Token\",\"m_attributes\":[],\"position\":{\"x\":300,\"y\":210.00000000000006},\"size\":{\"width\":60,\"height\":60},\"angle\":0,\"id\":\"7665e9b0-b563-4c70-9390-76b5e3c99a56\",\"z\":4,\"description\":\"\",\"m_methods\":[],\"attrs\":{\".uml-class-name-rect\":{\"height\":40,\"transform\":\"translate(0,0)\"},\".uml-class-attrs-rect\":{\"height\":20,\"transform\":\"translate(0,40)\"},\".uml-class-name-text\":{\"font-size\":9,\"text\":\"Token\"},\".uml-class-attrs-text\":{\"font-size\":9,\"text\":\"\"},\".uml-class-methods-text\":{\"font-size\":9}}},{\"type\":\"uml.Association\",\"name\":\"toTransition\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"c88b240a-0e4b-4a50-ab2c-a302510cfe8c\"},\"target\":{\"id\":\"d1f6046e-2733-4571-ae0c-4f586d1f6f52\"},\"id\":\"98d8d071-6b49-490c-8837-ec6c86920826\",\"subtype\":\"Association\",\"z\":5,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"toTransition\"}}}],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"vertices\":[{\"x\":310,\"y\":110}],\"attrs\":{}},{\"type\":\"uml.Association\",\"name\":\"toPlace\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"d1f6046e-2733-4571-ae0c-4f586d1f6f52\"},\"target\":{\"id\":\"c88b240a-0e4b-4a50-ab2c-a302510cfe8c\"},\"id\":\"c147792d-0eac-4b1a-b531-9593d8d91959\",\"subtype\":\"Association\",\"z\":6,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"toPlace\"}}}],\"vertices\":[{\"x\":220,\"y\":130}],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"attrs\":{}},{\"type\":\"uml.Association\",\"name\":\"placeToPlace\",\"sourceDeletionDeletesTarget\":false,\"targetDeletionDeletesSource\":false,\"source\":{\"id\":\"c88b240a-0e4b-4a50-ab2c-a302510cfe8c\"},\"target\":{\"id\":\"c88b240a-0e4b-4a50-ab2c-a302510cfe8c\"},\"id\":\"ea8aac72-f6b3-4a05-b0e8-e82f1c2e6ae9\",\"vertices\":[{\"x\":90,\"y\":132},{\"x\":90,\"y\":50},{\"x\":172,\"y\":50}],\"subtype\":\"Association\",\"z\":7,\"labels\":[{\"position\":0.5,\"attrs\":{\"text\":{\"text\":\"placeToPlace\"}}}],\"m_attributes\":[],\"m_methods\":[],\"linkdef_source\":[],\"linkdef_target\":[],\"attrs\":{}}]}"
}