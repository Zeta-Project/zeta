
//Todo import yfiles enum, make sure attributes and methods are needed additionally to model.attributes



import joint from 'jointjs';

//import graph from 'yfiles'

export default (function () {
    'use strict';

    var _graph;
    var MENUM_CONTAINER_ID = 'menum_container';

    var init;
    var getMEnumContainer;
    var getMEnumNames;
    var getMEnums;
    var getMEnum;

    init = function init(graph) {
        _graph = graph;
    };

    getMEnumContainer = function getMEnumContainer() {

        var mEnumContainer = _graph.getElementsByName(MENUM_CONTAINER_ID);
        if (!mEnumContainer) {
            mEnumContainer = new graph.createElement({
                size: {
                    width: 0,
                    height: 0
                },
                position: {
                    x: 0,
                    y: 0
                },
                id: MENUM_CONTAINER_ID,
                type: 'mcore.Enum',
                markup: '<g />',
                name: 'mEnumContainer',
                'm_enum': []
            });
        }
        return mEnumContainer;
    };

    getMEnumNames = function getMEnumNames() {
        var types = [];
        if (getMEnumContainer().attributes['m_enum']) {
            _.each(getMEnumContainer().attributes['m_enum'], function (mEnum) {
                types.push(mEnum.name);
            });
        }
        return types;
    };

    getMEnums = function getMEnums() {
        return getMEnumContainer().attributes['m_enum'];
    };

    getMEnum = function getMEnum(mEnumName) {
        var retEnum = {};

        _.each(getMEnums(), function (mEnum) {
            if (mEnum.name === mEnumName) {
                retEnum = mEnum;

                // stop iteration
                return false;
            }
        });

        return retEnum;
    };

    return {
        MENUM_CONTAINER_ID : MENUM_CONTAINER_ID,
        init: init,
        getMEnumContainer: getMEnumContainer,
        getMEnumNames: getMEnumNames,
        getMEnums: getMEnums,
        getMEnum: getMEnum
    };

})();

