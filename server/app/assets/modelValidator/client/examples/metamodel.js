var exampleMetaModel = [
    {
        "mType" : "mReference",
        "name" : "isWife",
        "sourceDeletionDeletesTarget" : false,
        "targetDeletionDeletesSource" : false,
        "source" : [
            {
                "type" : "Female",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "target" : [
            {
                "type" : "Male",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    },
    {
        "mType" : "mEnum",
        "name" : "healthInsurance",
        "symbols" : [
            "private",
            "national",
            "none"
        ]
    },
    {
        "mType" : "mClass",
        "name" : "Person",
        "abstract" : true,
        "superTypes" : [],
        "inputs" : [
            {
                "type" : "isFather",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            },
            {
                "type" : "isMother",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "outputs" : [],
        "attributes" : [
            {
                "name" : "FirstName",
                "globalUnique" : false,
                "localUnique" : false,
                "type" : "String",
                "default" : "Hans",
                "constant" : false,
                "singleAssignment" : false,
                "expression" : "",
                "ordered" : false,
                "transient" : false,
                "upperBound" : 1,
                "lowerBound" : 1
            },
            {
                "name" : "Geburtstag",
                "globalUnique" : true,
                "localUnique" : true,
                "type" : "String",
                "default" : "",
                "constant" : false,
                "singleAssignment" : false,
                "expression" : "",
                "ordered" : false,
                "transient" : false,
                "upperBound" : 1,
                "lowerBound" : 1
            },
            {
                "name" : "Steuernummer",
                "globalUnique" : true,
                "localUnique" : true,
                "type" : "String",
                "default" : "",
                "constant" : false,
                "singleAssignment" : false,
                "expression" : "",
                "ordered" : false,
                "transient" : false,
                "upperBound" : -1,
                "lowerBound" : 1
            },
            {
                "name" : "Krankenversicherungs",
                "globalUnique" : false,
                "localUnique" : false,
                "type" : "healthInsurance",
                "default" : "none",
                "constant" : false,
                "singleAssignment" : false,
                "expression" : "",
                "ordered" : false,
                "transient" : false,
                "upperBound" : 1,
                "lowerBound" : 1
            }
        ]
    },
    {
        "mType" : "mReference",
        "name" : "isMother",
        "sourceDeletionDeletesTarget" : false,
        "targetDeletionDeletesSource" : false,
        "source" : [
            {
                "type" : "Female",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "target" : [
            {
                "type" : "Person",
                "upperBound" : -1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    },
    {
        "mType" : "mClass",
        "name" : "Female",
        "abstract" : false,
        "superTypes" : [
            "Person"
        ],
        "inputs" : [
            {
                "type" : "isHusband",
                "upperBound" : 1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "outputs" : [
            {
                "type" : "isWife",
                "upperBound" : 1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            },
            {
                "type" : "isMother",
                "upperBound" : -1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    },
    {
        "mType" : "mReference",
        "name" : "isFather",
        "sourceDeletionDeletesTarget" : false,
        "targetDeletionDeletesSource" : false,
        "source" : [
            {
                "type" : "Male",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "target" : [
            {
                "type" : "Person",
                "upperBound" : -1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    },
    {
        "mType" : "mReference",
        "name" : "isHusband",
        "sourceDeletionDeletesTarget" : false,
        "targetDeletionDeletesSource" : false,
        "source" : [
            {
                "type" : "Male",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "target" : [
            {
                "type" : "Female",
                "upperBound" : 1,
                "lowerBound" : 1,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    },
    {
        "mType" : "mClass",
        "name" : "Male",
        "abstract" : false,
        "superTypes" : [
            "Person"
        ],
        "inputs" : [
            {
                "type" : "isWife",
                "upperBound" : 1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "outputs" : [
            {
                "type" : "isHusband",
                "upperBound" : 1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            },
            {
                "type" : "isFather",
                "upperBound" : -1,
                "lowerBound" : 0,
                "deleteIfLower" : false
            }
        ],
        "attributes" : []
    }
];
