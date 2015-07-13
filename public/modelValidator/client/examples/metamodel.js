var exampleMetaModel = {
    "Male": {
        "mType": "mClass",
        "name": "Male",
        "abstract": false,
        "superTypes": [
            "Person"
        ],
        "mAttributes": {},
        "inputs": [
            {
                "type": "isWife",
                "upperBound": 1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ],
        "outputs": [
            {
                "type": "isHusband",
                "upperBound": 1,
                "lowerBound": 0,
                "deleteIfLower": false
            },
            {
                "type": "isFather",
                "upperBound": -1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ]
    },
    "Person": {
        "mType": "mClass",
        "name": "Person",
        "abstract": true,
        "superTypes": [],
        "mAttributes": {
            "FirstName": {
                "name": "FirstName",
                "upperBound": 1,
                "lowerBound": 1,
                "default": "",
                "type": "String",
                "expression": "",
                "uniqueLocal": false,
                "uniqueGlobal": false,
                "constant": false,
                "ordered": false,
                "transient": false,
                "singleAssignment": false
            },
            "Geburtstag": {
                "name": "Geburtstag",
                "upperBound": 1,
                "lowerBound": 1,
                "default": "",
                "type": "String",
                "expression": "",
                "uniqueLocal": false,
                "uniqueGlobal": false,
                "constant": false,
                "ordered": false,
                "transient": false,
                "singleAssignment": false
            },
            "Steuernummer": {
                "name": "Steuernummer",
                "upperBound": -1,
                "lowerBound": 1,
                "default": "",
                "type": "String",
                "expression": "",
                "uniqueLocal": true,
                "uniqueGlobal": true,
                "constant": false,
                "ordered": false,
                "transient": false,
                "singleAssignment": false
            }
        },
        "inputs": [
            {
                "type": "isFather",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            },
            {
                "type": "isMother",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ],
        "outputs": []
    },
    "Female": {
        "mType": "mClass",
        "name": "Female",
        "abstract": false,
        "superTypes": [
            "Person"
        ],
        "mAttributes": {},
        "inputs": [
            {
                "type": "isHusband",
                "upperBound": 1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ],
        "outputs": [
            {
                "type": "isWife",
                "upperBound": 1,
                "lowerBound": 0,
                "deleteIfLower": false
            },
            {
                "type": "isMother",
                "upperBound": -1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ]
    },
    "isHusband": {
        "mType": "mRef",
        "name": "isHusband",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "mAttributes": {},
        "source": [
            {
                "type": "Male",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ],
        "target": [
            {
                "type": "Female",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ]
    },
    "isWife": {
        "mType": "mRef",
        "name": "isWife",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "mAttributes": {},
        "source": [
            {
                "type": "Female",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ],
        "target": [
            {
                "type": "Male",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ]
    },
    "isFather": {
        "mType": "mRef",
        "name": "isFather",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "mAttributes": {},
        "source": [
            {
                "type": "Male",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ],
        "target": [
            {
                "type": "Person",
                "upperBound": -1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ]
    },
    "isMother": {
        "mType": "mRef",
        "name": "isMother",
        "sourceDeletionDeletesTarget": false,
        "targetDeletionDeletesSource": false,
        "mAttributes": {},
        "source": [
            {
                "type": "Female",
                "upperBound": 1,
                "lowerBound": 1,
                "deleteIfLower": false
            }
        ],
        "target": [
            {
                "type": "Person",
                "upperBound": -1,
                "lowerBound": 0,
                "deleteIfLower": false
            }
        ]
    }
};
