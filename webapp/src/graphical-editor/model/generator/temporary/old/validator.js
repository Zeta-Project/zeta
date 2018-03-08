// //OLD
// export var validator = {
//
//
//     inputMatrix: {
//
//         InterfaceKlasse: {
//             BaseClassRealization: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }, Realization: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }
//         }
//         ,
//         Klasse: {
//             Aggregation: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }, Component: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }
//         }
//         ,
//         AbstractKlasse: {
//             Inheritance: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }
//         }
//
//     },
//     outputMatrix: {
//
//         Klasse: {
//             Inheritance: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }, BaseClassRealization: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }, Aggregation: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }, Component: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }
//         }
//         ,
//         AbstractKlasse: {
//             Realization: {
//                 upperBound: 1,
//                 lowerBound: 1
//             }
//         }
//
//     },
//
//
//     targetMatrix: {
//         inClassNode: {
//
//             realization: true, inheritance: false, BaseClassRealization: true, aggregation: false, component: false
//
//         }, abClassNode: {
//
//             BaseClassRealization: false, aggregation: false, inheritance: true, component: false, realization: false
//
//         }, classNode: {
//
//             inheritance: false, BaseClassRealization: false, aggregation: true, component: true, realization: false
//
//         }
//     },
//
//
//     sourceMatrix: {
//         inClassNode: {
//
//             inheritance: false, BaseClassRealization: false, aggregation: false, component: false, realization: false
//
//         }, classNode: {
//
//             BaseClassRealization: true, aggregation: true, component: true, inheritance: true, realization: false
//
//         }, abClassNode: {
//
//             realization: true, inheritance: false, BaseClassRealization: false, aggregation: false, component: false
//
//         }
//     },
//
//
//     edgeData: {
//         inheritance: {
//             type: "Inheritance",
//             from: "Klasse",
//             to: "AbstractKlasse",
//             style: "inheritance"
//         }, realization: {
//             type: "Realization",
//             from: "AbstractKlasse",
//             to: "InterfaceKlasse",
//             style: "realization"
//         }, BaseClassRealization: {
//             type: "BaseClassRealization",
//             from: "Klasse",
//             to: "InterfaceKlasse",
//             style: "realization"
//         }, component: {
//             type: "Component",
//             from: "Klasse",
//             to: "Klasse",
//             style: "component"
//         }, aggregation: {
//             type: "Aggregation",
//             from: "Klasse",
//             to: "Klasse",
//             style: "aggregation"
//         }
//     },
//
//
//     isValidTarget: function (nodeName, edgeName) {
//         return this.targetMatrix[nodeName][edgeName];
//     },
//
//     isValidSource: function (nodeName, edgeName) {
//         return this.sourceMatrix[nodeName][edgeName];
//     },
//
//     getEdgeData: function (edgeName) {
//         return this.edgeData[edgeName];
//     },
//
//     getValidEdges: function (sourceName, targetName) {
//         var validEdges = [];
//         var candidateEdges = Object.keys(this.sourceMatrix[sourceName]);
//         for (var i = 0; i < candidateEdges.length; i++) {
//             if (this.isValidSource(sourceName, candidateEdges[i]) && this.isValidTarget(targetName, candidateEdges[i])) {
//                 validEdges.push(candidateEdges[i]);
//             }
//         }
//
//         return validEdges;
//     },
//
//     getValidCompartments: function (childName, parentName) {
//         return this.compartmentMatrix[childName][parentName];
//     },
//
//     isValidChild: function (childName, parentName) {
//         return this.getValidCompartments(childName, parentName).length > 0;
//     }
//
// };
