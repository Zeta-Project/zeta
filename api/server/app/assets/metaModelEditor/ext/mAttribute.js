var mAttribute = (function () {
    'use strict';

    var _graph;
    var MATTRIBUTE_CONTAINER_ID = 'mattribute_container';

    var init;
    var getMAttributeContainer;
    var getMAttributeNames;
    var getMAttributes;
    var getMAttribute;

    init = function init(graph) {
        _graph = graph;
    };

    getMAttributeContainer = function getMAttributeContainer() {
        var mAttributeContainer = _graph.getCell(MATTRIBUTE_CONTAINER_ID);

        if (!mAttributeContainer) {
            mAttributeContainer = new joint.dia.Element({
                size: {
                    width: 0,
                    height: 0
                },
                position: {
                    x: 0,
                    y: 0
                },
                id: MATTRIBUTE_CONTAINER_ID,
                type: 'mcore.Attribute',
                markup: '<g />',
                name: 'mAttributeContainer',
                'm_Attribute': []
            });
        }
        return mAttributeContainer;
    };

    getMAttributeNames = function getMAttributeNames() {
        var types = [];
        if (getMAttributeContainer().attributes['m_Attribute']) {
            _.each(getMAttributeContainer().attributes['m_Attribute'], function (mAttribute) {
                types.push(mAttribute.name);
            });
        }
        return types;
    };

    getMAttributes = function getMAttributes() {
        return getMAttributeContainer().attributes['m_Attribute'];
    };

    getMAttribute = function getMAttribute(mAttributeName) {
        var retAttribute = {};

        _.each(getMAttributes(), function (mAttribute) {
            if (mAttribute.name === mAttributeName) {
                retAttribute = mAttribute;

                // stop iteration
                return false;
            }
        });

        return retAttribute;
    };

    return {
        MATTRIBUTE_CONTAINER_ID : MATTRIBUTE_CONTAINER_ID,
        init: init,
        getMAttributeContainer: getMAttributeContainer,
        getMAttributeNames: getMAttributeNames,
        getMAttributes: getMAttributes,
        getMAttribute: getMAttribute
    };

})();