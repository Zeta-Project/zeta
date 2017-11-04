
/**
 * edgeValidator is used to determine which types of links can be used with which types of elements.
 *
 * @returns {{validEdges: Function}}
 */
export default (function edgeValidator() {
    'use strict';

    var _targetMatrix;
    var _sourceMatrix;

    var isValidTarget;
    var isValidSource;
    var validEdges;

    /**
     * Which elements can be source of which links.
     *
     * @type {object}
     * @private
     */
    _sourceMatrix = {
        'uml.Class': {
            'uml.Generalization': {
                name: 'Generalization',
                connectable: true
            },
            'uml.Association': {
                name: 'Association',
                connectable: true
            },
            'uml.Aggregation': {
                name: 'Aggregation',
                connectable: true
            },
            'uml.Composition': {
                name: 'Composition',
                connectable: true
            }
        },
        'uml.Abstract': {
            'uml.Generalization': {
                name: 'Generalization',
                connectable: true
            },
            'uml.Association': {
                name: 'Association',
                connectable: true
            },
            'uml.Aggregation': {
                name: 'Aggregation',
                connectable: true
            },
            'uml.Composition': {
                name: 'Composition',
                connectable: true
            }
        }
    };

    /**
     * Which elements can be target of which links.
     *
     * @type {object}
     * @private
     */
    _targetMatrix = {
        'uml.Class': {
            'uml.Generalization': {
                name: 'Generalization',
                connectable: true
            },
            'uml.Association': {
                name: 'Association',
                connectable: true
            },
            'uml.Aggregation': {
                name: 'Aggregation',
                connectable: true
            },
            'uml.Composition': {
                name: 'Composition',
                connectable: true
            }
        },
        'uml.Abstract': {
            'uml.Generalization': {
                name: 'Generalization',
                connectable: true
            },
            'uml.Association': {
                name: 'Association',
                connectable: true
            },
            'uml.Aggregation': {
                name: 'Aggregation',
                connectable: true
            },
            'uml.Composition': {
                name: 'Composition',
                connectable: true
            }
        }
    };

    /**
     * Checks whether the nodeType is a valid target for the edgeType.
     *
     * @param {string} nodeType - Type of the node (e.g. 'uml.Class')
     * @param {string} edgeType - Type of the link (e.g. 'uml.Generalization')
     * @returns {boolean}
     */
    isValidTarget = function isValidTarget(nodeType, edgeType) {
        var check = false;
        if (_targetMatrix[nodeType] && _targetMatrix[nodeType][edgeType]) {
            check = _targetMatrix[nodeType][edgeType].connectable || false;
        }
        return check;
    };

    /**
     * Checks whether the nodeType is a valid source for the edgeType.
     *
     * @param {string} nodeType - Type of the node (e.g. 'uml.Class')
     * @param {string} edgeType - Type of the link (e.g. 'uml.Generalization')
     * @returns {boolean}
     */
    isValidSource = function isValidSource(nodeType, edgeType) {
        var check = false;
        if (_sourceMatrix[nodeType] && _sourceMatrix[nodeType][edgeType]) {
            check = _sourceMatrix[nodeType][edgeType].connectable || false;
        }
        return check;
    };

    /**
     * Returns an array of strings, which contains all possible nodeTypes for the specified sourceType and targetType.
     *
     * @param {string} sourceType
     * @param {string} targetType
     * @returns {Array}
     */
    validEdges = function validEdges(sourceType, targetType) {

        var edges = [];
        var candidateEdges = _sourceMatrix[sourceType] || {};

        _.each(candidateEdges, function (value, edgeType) {
            if (isValidSource(sourceType, edgeType) && isValidTarget(targetType, edgeType)) {
                edges.push(value.name);
            }
        });

        return edges;
    };

    /**
     * Provide publicly avaliable functions und variables.
     */
    return {
        validEdges: validEdges
    };

})();