//OLD

var validator = {


    inputMatrix: {

        InterfaceKlasse: {
            BaseClassRealization: {
                upperBound: 1,
                lowerBound: 1
            }, Realization: {
                upperBound: 1,
                lowerBound: 1
            }
        }
        ,
        AbstractKlasse: {
            Inheritance: {
                upperBound: 1,
                lowerBound: 1
            }
        }

    },
    outputMatrix: {

        Klasse: {
            Inheritance: {
                upperBound: 1,
                lowerBound: 1
            }, BaseClassRealization: {
                upperBound: 1,
                lowerBound: 1
            }
        }
        ,
        AbstractKlasse: {
            Realization: {
                upperBound: 1,
                lowerBound: 1
            }
        }

    },


    targetMatrix: {
        abClassNode: {

            BaseClassRealization: false, inheritance: true, realization: false

        }, classNode: {

            inheritance: false, BaseClassRealization: false, realization: false

        }, inClassNode: {

            realization: true, inheritance: false, BaseClassRealization: true

        }
    },


    sourceMatrix: {
        abClassNode: {

            realization: true, inheritance: false, BaseClassRealization: false

        }, classNode: {

            BaseClassRealization: true, inheritance: true, realization: false

        }, inClassNode: {

            inheritance: false, BaseClassRealization: false, realization: false

        }
    },


    edgeData: {
        inheritance: {
            type: "Inheritance",
            from: "Klasse",
            to: "AbstractKlasse",
            style: "inheritance"
        }, realization: {
            type: "Realization",
            from: "AbstractKlasse",
            to: "InterfaceKlasse",
            style: "realization"
        }, BaseClassRealization: {
            type: "BaseClassRealization",
            from: "Klasse",
            to: "InterfaceKlasse",
            style: "realization"
        }
    },


    isValidTarget: function (nodeName, edgeName) {
        return this.targetMatrix[nodeName][edgeName];
    },

    isValidSource: function (nodeName, edgeName) {
        return this.sourceMatrix[nodeName][edgeName];
    },

    getEdgeData: function (edgeName) {
        return this.edgeData[edgeName];
    },

    getValidEdges: function (sourceName, targetName) {
        var validEdges = [];
        var candidateEdges = Object.keys(this.sourceMatrix[sourceName]);
        for (var i = 0; i < candidateEdges.length; i++) {
            if (this.isValidSource(sourceName, candidateEdges[i]) && this.isValidTarget(targetName, candidateEdges[i])) {
                validEdges.push(candidateEdges[i]);
            }
        }

        return validEdges;
    },

    getValidCompartments: function (childName, parentName) {
        return this.compartmentMatrix[childName][parentName];
    },

    isValidChild: function (childName, parentName) {
        return this.getValidCompartments(childName, parentName).length > 0;
    }

};
    