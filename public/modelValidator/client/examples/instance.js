var exampleInstance = {
    "846bc8a2-00fc-401f-b626-0b0252516aee": {
        "mClass": "Male",
        "outputs": {
            "isFather": ["8e9b1093-a589-4ae4-8e1e-1b3d63a3f842"],
            "isHusband": ["ee204744-6322-49d4-928e-1442e8bc70c4"]
        },
        "inputs": {
            "isWife": ["666d4de7-e0f2-4620-8c19-d5469b40be1f"]
        },
        "mAttributes": {
            "FirstName": ["Hans"],
            "Steuernummer": ["12"],
            "Geburtstag": ["12-02-2015"]
        }
    },
    "a264a43b-6f97-4257-9243-baddbf745490": {
        "mClass": "Female",
        "outputs": {
            "isMother": ["d5b00503-5378-4df3-9e27-4d2b0d018750"],
            "isWife": ["666d4de7-e0f2-4620-8c19-d5469b40be1f"]
        },
        "inputs": {
            "isHusband": ["ee204744-6322-49d4-928e-1442e8bc70c4"]
        },
        "mAttributes": {
            "FirstName": ["Magda"],
            "Steuernummer": ["13"],
            "Geburtstag": ["13-02-2015"]
        }
    },
    "1c2861fe-bbca-4842-9436-4d1b9d9a4d05": {
        "mClass": "Male",
        "outputs": [],
        "inputs": {
            "isFather" : ["8e9b1093-a589-4ae4-8e1e-1b3d63a3f842"],
            "isMother" : ["d5b00503-5378-4df3-9e27-4d2b0d018750"]
        },
        "mAttributes": {
            "FirstName": ["Kevin"],
            "Steuernummer": ["14"],
            "Geburtstag": ["14-02-2015"]
        }
    },
    "8e9b1093-a589-4ae4-8e1e-1b3d63a3f842": {
        "mRef": "isFather",
        "source": {
            "Male" : ["846bc8a2-00fc-401f-b626-0b0252516aee"]
        },
        "target": {
            "Male" : ["1c2861fe-bbca-4842-9436-4d1b9d9a4d05"]
        }
    },
    "ee204744-6322-49d4-928e-1442e8bc70c4": {
        "mRef": "isHusband",
        "source": {
            "Male" : ["846bc8a2-00fc-401f-b626-0b0252516aee"]
        },
        "target": {
            "Female" : ["a264a43b-6f97-4257-9243-baddbf745490"]
        }
    },
    "d5b00503-5378-4df3-9e27-4d2b0d018750": {
        "mRef": "isMother",
        "source": {
            "Female" : ["a264a43b-6f97-4257-9243-baddbf745490"]
        },
        "target": {
            "Male" : ["846bc8a2-00fc-401f-b626-0b0252516aee"]
        }
    },
    "666d4de7-e0f2-4620-8c19-d5469b40be1f": {
        "mRef": "isWife",
        "source": {
            "Female" : ["a264a43b-6f97-4257-9243-baddbf745490"]
        },
        "target": {
            "Male" : ["846bc8a2-00fc-401f-b626-0b0252516aee"]
        }
    }
};