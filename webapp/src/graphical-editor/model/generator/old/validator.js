/** This is a generated validator file for JointJS     */
var validator = {
    inputMatrix: {
        Entity: {
            link: {
                upperBound: 1,
                lowerBound: 1
            }
            ,
            reference: {
                upperBound: 1,
                lowerBound: 1
            }
            ,
            map: {
                upperBound: 1,
                lowerBound: 1
            }
            ,
            drop: {
                upperBound: 1,
                lowerBound: 1
            }
        }
    }
    ,
    outputMatrix: {
        Entity: {
            link: {
                upperBound: 1,
                lowerBound: 1
            }
            ,
            reference: {
                upperBound: 1,
                lowerBound: 1
            }
            ,
            map: {
                upperBound: 1,
                lowerBound: 1
            }
        },
        FrameworkAnchor: {
            drop: {
                upperBound: 1,
                lowerBound: 1
            }
        }
    }
    ,
    targetMatrix: {
        teamStart: {
            map: false, link:
                false, reference:
                false, drop:
                false
        }
        ,
        periodStart: {
            map: false, link:
                false, reference:
                false, drop:
                false
        }
        ,
        entity: {
            reference: true, link:
                true, drop:
                true, map:
                true
        }
    }
    ,
    sourceMatrix: {
        teamStart: {
            map: false, link:
                false, reference:
                false, drop:
                true
        }
        ,
        periodStart: {
            map: false, link:
                false, reference:
                false, drop:
                true
        }
        ,
        entity: {
            reference: true, link:
                true, map:
                true, drop:
                false
        }
    }
    ,
    edgeData: {
        drop: {
            type:
                "drop", from: "FrameworkAnchor", to: "Entity", style: "drop"
        },
        reference: {type: "reference", from: "Entity", to: "Entity", style: "reference"},
        link: {type: "link", from: "Entity", to: "Entity", style: "link"},
        map: {type: "link", from: "Entity", to: "Entity", style: "link"}
    }, isValidTarget: function (nodeName, edgeName) {
        return this.targetMatrix[nodeName][edgeName];
    }, isValidSource: function (nodeName, edgeName) {
        return this.sourceMatrix[nodeName][edgeName];
    }, getEdgeData: function (edgeName) {
        return this.edgeData[edgeName];
    }, getValidEdges: function (sourceName, targetName) {
        var validEdges = [];
        var candidateEdges = Object.keys(this.sourceMatrix[sourceName]);
        for (var i = 0; i < candidateEdges.length; i++) {
            if (this.isValidSource(sourceName, candidateEdges[i]) && this.isValidTarget(targetName, candidateEdges[i])) {
                validEdges.push(candidateEdges[i]);
            }
        }
        return validEdges;
    }, getValidCompartments: function (childName, parentName) {
        return this.compartmentMatrix[childName][parentName];
    }, isValidChild: function (childName, parentName) {
        return this.getValidCompartments(childName, parentName).length > 0;
    }
};