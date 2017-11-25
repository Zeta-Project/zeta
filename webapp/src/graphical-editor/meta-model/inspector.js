import $ from 'jquery';
import mCoreUtil from './ext/mCoreUtil';
import mEnum from './ext/mEnum';

/**
 * Hierarchical inspector configuration.
 * @type {{getDefs}}
 */
export default (function inspector () {
    'use strict';

    var GROUPS;
    var M_OBJECT;
    var M_BOUNDS;
    var M_ATTRIBUTE;

    var M_CLASS_LINK_DEF;
    var M_REFERENCE_LINK_DEF;
    var M_CLASS;
    var M_REFERENCE;
    var M_ENUM;
    var M_METHOD;
    var M_DESCRIPTION;
    var M_PARAMETER;
    var M_ATTRIBUTE_LIST;
    var M_METHOD_LIST;


    var getDefs;

    GROUPS = {
        general : {
            label : 'General',
            index : 0
        },

        deletion : {
            label : 'Deletion',
            index : 1
        },

        m_attribute : {
            label : 'Attributes',
            index : 2
        },

        source : {
            label : 'Source',
            index : 4
        },

        target : {
            label : 'Target',
            index : 5
        },

        input : {
            label : 'Input',
            index : 4
        },

        output : {
            label : 'Output',
            index : 5
        },

        mEnum : {
            label : 'Enums',
            index : 0
        }
        ,

        m_method : {
            label : "Methods",
            index : 2
        }
    };

    M_OBJECT = {
        name : {
            type : 'text',
            label : 'Name',
            defaultValue : '',
            group : 'general',
            index : 0
        }
    };

    M_DESCRIPTION = {
        description : {
            type : 'text',
            label : 'Description',
            defaultValue : '',
            group : 'general',
            index : 1
        }
    };

    M_PARAMETER = _.extend({
        typ : {
            type : 'select',
            label : 'Type',
            options : [
                'Bool',
                'Int',
                'String',
                'Double'
            ],
            defaultValue : '',
            index : 2
        }
    }, M_OBJECT);

    M_BOUNDS = {
        upperBound : {
            type : 'number',
            label : 'Upper bound',
            min : -1,
            defaultValue : -1,
            index : 1
        },

        lowerBound : {
            type : 'number',
            label : 'Lower bound',
            min : -1,
            defaultValue : 0,
            index : 2
        }
    };

    M_ATTRIBUTE = _.extend({
        'default' : {
            type : 'text',
            label : 'Default',
            defaultValue : '',
            index : 3
        },

        typ : {
            type : 'select',
            label : 'Type',
            options : [
                'Bool',
                'Int',
                'String',
                'Double'
            ],
            defaultValue : 'String',
            index : 4
        },

        expression : {
            type : 'text',
            label : 'Expression',
            defaultValue : '',
            index : 5
        },

        localUnique : {
            type : 'toggle',
            label : 'Unique local',
            defaultValue : false,
            index : 6
        },

        globalUnique: {
            type : 'toggle',
            label : 'Unique global',
            defaultValue: false,
            index : 7
        },

        constant : {
            type : 'toggle',
            label : 'Constant',
            defaultValue : false,
            index : 8
        },

        ordered : {
            type : 'toggle',
            label : 'Ordered',
            defaultValue : false,
            index : 9
        },

        transient : {
            type : 'toggle',
            label : 'Transient',
            defaultValue : false,
            index : 10
        },

        singleAssignment : {
            type : 'toggle',
            label : 'Single Assignment',
            defaultValue : false,
            index : 11
        }
    }, M_OBJECT, M_BOUNDS);

    M_METHOD = _.extend({

        code : {
            type : 'text',
            label : 'Code',
            defaultValue : '',
            index : 1
        },
        parameters : {
            type : 'list',
            label : 'Parameter',
            item : {
                type : 'object',
                label : 'Parameter',
                properties : M_PARAMETER
            },
            index : 3
        },
        returnType : {
            type : 'select',
            label : 'ReturnType',
            options : [
                'Bool',
                'Int',
                'String',
                'Double',
                'Unit'
            ],
            index : 4,
            defaultValue : 'String'
        }

    }, M_OBJECT, M_DESCRIPTION);

    M_METHOD_LIST = {
        m_methods : {
            type : 'list',
            label : 'Methods',
            item : {
                type : 'object',
                label : 'Attribute',
                properties : M_METHOD
            },
            group : 'm_method',
            index : 2
        }
    };

    M_ATTRIBUTE_LIST = {
        m_attributes : {
            type : 'list',
            label : 'Attributes',
            item : {
                type : 'object',
                label : 'Attribute',
                properties : M_ATTRIBUTE
            },
            group : 'm_attribute',
            index : 1
        }
    };

    M_CLASS_LINK_DEF = _.extend({
        className : {
            type : 'select',
            options : [],
            label : 'Type',
            index : 0
        },

        deleteIfLower: {
            type : 'toggle',
            label : 'Delete if lower',
            defaultValue: false,
            index : 3
        }
    }, M_BOUNDS);

    M_REFERENCE_LINK_DEF = _.extend({
        referenceName : {
            type : 'select',
            options : [],
            label : 'Type',
            index : 0
        },

        deleteIfLower: {
            type : 'toggle',
            label : 'Delete if lower',
            defaultValue: false,
            index : 3
        }
    }, M_BOUNDS);

    M_CLASS = _.extend({

        'linkdef_input' : {
            type : 'list',
            label : 'Input',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_REFERENCE_LINK_DEF
            },
            group : 'input',
            index : 3
        },

        'linkdef_output' : {
            type : 'list',
            label : 'Output',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_REFERENCE_LINK_DEF
            },
            group : 'output',
            index : 4
        }
    }, M_OBJECT, M_DESCRIPTION, M_ATTRIBUTE_LIST, M_METHOD_LIST);

    M_REFERENCE = _.extend({

        sourceDeletionDeletesTarget : {
            type : 'toggle',
            label : 'Source deletion deletes target',
            group : 'deletion',
            readonly : true
        },

        targetDeletionDeletesSource : {
            type : 'toggle',
            label : 'Target deletion deletes source',
            group : 'deletion',
            readonly : true
        },

        'linkdef_source' : {
            type : 'list',
            label : 'Source',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_CLASS_LINK_DEF
            },
            group : 'source',
            index : 3
        },

        'linkdef_target' : {
            type : 'list',
            label : 'Target',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_CLASS_LINK_DEF
            },
            group : 'target',
            index : 3
        }

    }, M_OBJECT, M_ATTRIBUTE_LIST, M_METHOD_LIST);

    M_ENUM = _.extend({
        'm_enum' : {
            type : 'list',
            label : 'Enum',
            item : {
                type : 'object',
                properties : _.extend({
                    symbols : {
                        type : 'list',
                        label : 'Symbols',
                        item : {
                            type : 'text'
                        }
                    }
                }, M_OBJECT)
            },
            group : 'mEnum'
        }
    }, M_ATTRIBUTE_LIST, M_METHOD_LIST);

    /**
     * Creates the inspector definitions for the given cell.
     * @param cell An element or Reference.
     * @param elements An array of all elements in the graph.
     * @param links An array of all references in the graph.
     */
    getDefs = function (cell, elements, links) {
        var linkdefOptions = [];
        var defs = {
            groups : GROUPS
        };

        if (mCoreUtil.isElement(cell)) {

            defs.inputs = _.cloneDeep(M_CLASS);

            _.each(links, function (link) {
                if (mCoreUtil.isReference(link) && linkdefOptions.indexOf(link.attributes.name) === -1) {
                    linkdefOptions.push(link.attributes.name);
                }
            });

            defs.inputs.linkdef_input.item.properties.referenceName.options = linkdefOptions;
            defs.inputs.linkdef_output.item.properties.referenceName.options = linkdefOptions;

        } else if (mCoreUtil.isReference(cell)) {
            defs.inputs = _.cloneDeep(M_REFERENCE);

            _.each(elements, function (element) {
                if (mCoreUtil.isElement(element) && linkdefOptions.indexOf(element.attributes.name) === -1) {
                    linkdefOptions.push(element.attributes.name);
                }
            });

            defs.inputs.linkdef_source.item.properties.className.options = linkdefOptions;
            defs.inputs.linkdef_target.item.properties.className.options = linkdefOptions;

        } else if (mCoreUtil.isMEnumContainer(cell)) {

            defs.inputs = _.cloneDeep(M_ENUM);

        }

        // Add enum types to m_attribute type dropdown.
        if (cell.attributes.m_attributes) {
            defs.inputs.m_attributes.item.properties.typ.options = defs.inputs.m_attributes.item.properties.typ.options.concat(mEnum.getMEnumNames());
        }

        return defs;
    };

    return {
        getDefs : getDefs
    };
})();

$(".inspector-toggle-icon-wrapper").on("click", function() {
    $(".inspector-toggle-icon-wrapper").toggleClass("glyphicon-menu-right");
    $(".inspector-toggle-icon-wrapper").toggleClass("glyphicon-menu-left");
    $(".paper-container").toggleClass("paper-container-inspector-hidden");
    $(".inspector-container").toggleClass("inspector-container-hidden");
    $(".inspector-toggle-container").toggleClass("toggle-container-hidden");
});
