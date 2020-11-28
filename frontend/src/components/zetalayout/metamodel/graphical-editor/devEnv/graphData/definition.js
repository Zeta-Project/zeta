export default {
    "enums": [],
    "classes": [
    {
        "name": "AbstractKlasse",
        "description": "",
        "abstractness": true,
        "superTypeNames": [],
        "inputReferenceNames": [],
        "outputReferenceNames": [],
        "attributes": [
            {
                "name": "text11",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text21",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text31",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            }
        ],
        "methods": []
    },
    {
        "name": "InterfaceKlasse",
        "description": "",
        "abstractness": true,
        "superTypeNames": [],
        "inputReferenceNames": [],
        "outputReferenceNames": [],
        "attributes": [
            {
                "name": "text113",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text213",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text313",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            }
        ],
        "methods": []
    },
    {
        "name": "Klasse",
        "description": "",
        "abstractness": true,
        "superTypeNames": [],
        "inputReferenceNames": [],
        "outputReferenceNames": [
            "BaseClassRealization",
            "Inheritance",
            "Component",
            "Aggregation"
        ],
        "attributes": [
            {
                "name": "text1",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text2",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            },
            {
                "name": "text3",
                "globalUnique": true,
                "localUnique": true,
                "type": "String",
                "default": {
                    "type": "String",
                    "value": ""
                },
                "constant": false,
                "singleAssignment": false,
                "expression": "",
                "ordered": true,
                "transient": false
            }
        ],
        "methods": [
            {
                "name": "methodUnit",
                "parameters": [],
                "description": "default method",
                "returnType": "Unit",
                "code": "nice code"
            },
            {
                "name": "methodString",
                "parameters": [],
                "description": "default method",
                "returnType": "String",
                "code": "nice code"
            }
        ]
    }
],
    "references": [
    {
        "name": "BaseClassRealization",
        "description": "",
        "sourceDeletionDeletesTarget": true,
        "targetDeletionDeletesSource": true,
        "sourceClassName": "Klasse",
        "targetClassName": "InterfaceKlasse",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "Realization",
        "description": "",
        "sourceDeletionDeletesTarget": true,
        "targetDeletionDeletesSource": true,
        "sourceClassName": "Klasse",
        "targetClassName": "InterfaceKlasse",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "Inheritance",
        "description": "",
        "sourceDeletionDeletesTarget": true,
        "targetDeletionDeletesSource": true,
        "sourceClassName": "Klasse",
        "targetClassName": "AbstractKlasse",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "Component",
        "description": "",
        "sourceDeletionDeletesTarget": true,
        "targetDeletionDeletesSource": true,
        "sourceClassName": "Klasse",
        "targetClassName": "Klasse",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    },
    {
        "name": "Aggregation",
        "description": "",
        "sourceDeletionDeletesTarget": true,
        "targetDeletionDeletesSource": true,
        "sourceClassName": "Klasse",
        "targetClassName": "Klasse",
        "sourceLowerBounds": 0,
        "sourceUpperBounds": -1,
        "targetLowerBounds": 0,
        "targetUpperBounds": -1,
        "attributes": [],
        "methods": []
    }
],
    "attributes": [
    {
        "name": "text11",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text21",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text31",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text113",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text213",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text313",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text1",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text2",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    },
    {
        "name": "text3",
        "globalUnique": true,
        "localUnique": true,
        "type": "String",
        "default": {
            "type": "String",
            "value": ""
        },
        "constant": false,
        "singleAssignment": false,
        "expression": "",
        "ordered": true,
        "transient": false
    }
],
    "methods": [],
    "uiState": ""
}
