/**
 * Hierarchical inspector configuration.
 * @type {{getDefs}}
 */
var inspector = (function inspector () {
    'use strict';

    var GROUPS;
    var M_OBJECT;
    var M_BOUNDS;
    var M_ATTRIBUTE;
    var M_LINKDEF;
    var M_CLASS;
    var M_REFERENCE;
    var M_ENUM;

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
            index : 3
        },

        target : {
            label : 'Target',
            index : 4
        },

        input : {
            label : 'Input',
            index : 3
        },

        output : {
            label : 'Output',
            index : 4
        },

        mEnum : {
            label : 'Enums',
            index : 0
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

    M_BOUNDS = {
        upperBound : {
            type : 'number',
            label : 'Upper bound',
            min : -1,
            defaultValue : 1,
            index : 1
        },

        lowerBound : {
            type : 'number',
            label : 'Lower bound',
            min : -1,
            defaultValue : 1,
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

        type : {
            type : 'select',
            label : 'Type',
            options : [
                'Integer',
                'Float',
                'String',
                'Boolean'
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

    M_LINKDEF = _.extend({
        type : {
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
        },

        'linkdef_input' : {
            type : 'list',
            label : 'Input',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_LINKDEF
            },
            group : 'input',
            index : 2
        },

        'linkdef_output' : {
            type : 'list',
            label : 'Output',
            item : {
                type : 'object',
                label : 'Link definitions',
                properties : M_LINKDEF
            },
            group : 'output',
            index : 3
        }
    }, M_OBJECT);

    M_REFERENCE = _.extend({
        m_attributes : {
            type : 'list',
            label : 'Attributes',
            item : {
                type : 'object',
                label : 'Attribute',
                properties : M_ATTRIBUTE
            },
            group : 'm_attribute'
        },

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
                properties : M_LINKDEF
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
                properties : M_LINKDEF
            },
            group : 'target',
            index : 3
        }

    }, M_OBJECT);

    M_ENUM = {
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
    };

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

            defs.inputs['linkdef_input'].item.properties.type.options = linkdefOptions;
            defs.inputs['linkdef_output'].item.properties.type.options = linkdefOptions;

        } else if (mCoreUtil.isReference(cell)) {
            defs.inputs = _.cloneDeep(M_REFERENCE);

            _.each(elements, function (element) {
                if (mCoreUtil.isElement(element) && linkdefOptions.indexOf(element.attributes.name) === -1) {
                    linkdefOptions.push(element.attributes.name);
                }
            });

            defs.inputs['linkdef_source'].item.properties.type.options = linkdefOptions;
            defs.inputs['linkdef_target'].item.properties.type.options = linkdefOptions;

        } else if (mCoreUtil.isMEnumContainer(cell)) {

            defs.inputs = _.cloneDeep(M_ENUM);

        }

        // Add enum types to m_attribute type dropdown.
        if (cell.attributes['m_attributes']) {
            defs.inputs['m_attributes'].item.properties.type.options = defs.inputs['m_attributes'].item.properties.type.options.concat(mEnum.getMEnumNames());
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
