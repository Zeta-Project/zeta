/**
 * The data model of the UML node style.
 * A modification counter tracks if there have been modifications since the last drawing of this model.
 */

export class UMLClassModel {

    get modCount() {
        return this.$modCount
    }

    get selectedIndex() {
        return this.$selectedIndex
    }

    set selectedIndex(v) {
        this.$selectedIndex = v
        this.modify()
    }

    constructor(data) {
        this.description = (data && data.description) || ''
        this.abstract = (data && data.abstract) || false
        this.stereotype = (data && data.stereotype) || '' //if data && data.stereotype != null, use data.stereotype. else ''
        this.constraint = (data && data.constraint) || ''
        this.className = (data && data.className) || 'UML Class Node'
        this.attributes = (data && data.attributes) || []
        this.operations = (data && data.operations) || []

        this.attributesOpen = this.attributes.length > 0
        this.operationsOpen = this.operations.length > 0
        this.$selectedIndex = -1
        this.selectedCategory = 1
        this.$modCount = 0

    }

    modify() {
        this.$modCount++
    }

    clone() {
        const clone = new UMLClassModel()
        clone.stereotype = this.stereotype
        clone.constraint = this.constraint
        clone.className = this.className
        clone.attributes = Array.from(this.attributes)
        clone.operations = Array.from(this.operations)
        clone.attributesOpen = this.attributesOpen
        clone.operationsOpen = this.operationsOpen
        clone.selectedIndex = this.selectedIndex
        return clone
    }
}

/**
 * Markup extension needed to (de-)serialize the UML model.

export const UMLClassModelExtension = Class('UMLClassModelExtension', {
    $extends: MarkupExtension,

    $stereotype: null,
    stereotype: {
        $meta() {
            return [TypeAttribute(YString.$class)]
        },
        get() {
            return this.$stereotype
        },
        set(stereotype) {
            this.$stereotype = stereotype
        }
    },

    $constraint: null,
    constraint: {
        $meta() {
            return [TypeAttribute(YString.$class)]
        },
        get() {
            return this.$constraint
        },
        set(constraint) {
            this.$constraint = constraint
        }
    },

    $className: null,
    className: {
        $meta() {
            return [TypeAttribute(YString.$class)]
        },
        get() {
            return this.$className
        },
        set(className) {
            this.$className = className
        }
    },

    $attributes: null,
    attributes: {
        $meta() {
            return [TypeAttribute(YObject.$class)]
        },
        get() {
            return this.$attributes
        },
        set(attributes) {
            this.$attributes = attributes
        }
    },

    $operations: null,
    operations: {
        $meta() {
            return [TypeAttribute(YObject.$class)]
        },
        get() {
            return this.$operations
        },
        set(operations) {
            this.$operations = operations
        }
    },

    $attributesOpen: null,
    attributesOpen: {
        $meta() {
            return [TypeAttribute(YBoolean.$class)]
        },
        get() {
            return this.$attributesOpen
        },
        set(attributesOpen) {
            this.$attributesOpen = attributesOpen
        }
    },

    $operationsOpen: null,
    operationsOpen: {
        $meta() {
            return [TypeAttribute(YBoolean.$class)]
        },
        get() {
            return this.$operationsOpen
        },
        set(operationsOpen) {
            this.$operationsOpen = operationsOpen
        }
    },

    provideValue(serviceProvider) {
        const umlClassModel = new UMLClassModel()
        umlClassModel.stereotype = this.stereotype
        umlClassModel.constraint = this.constraint
        umlClassModel.className = this.className
        umlClassModel.attributes = this.attributes
        umlClassModel.operations = this.operations
        umlClassModel.attributesOpen = this.attributesOpen
        umlClassModel.operationsOpen = this.operationsOpen
        return umlClassModel
    }
})
 */

/**
 * Listener that handles the serialization of the UML model.

export const UMLClassModelSerializationListener = (sender, args) => {
    const item = args.item
    if (item instanceof UMLClassModel) {
        const umlClassModelExtension = new UMLClassModelExtension()
        umlClassModelExtension.stereotype = item.stereotype
        umlClassModelExtension.constraint = item.constraint
        umlClassModelExtension.className = item.className
        umlClassModelExtension.attributes = item.attributes
        umlClassModelExtension.operations = item.operations
        umlClassModelExtension.attributesOpen = item.attributesOpen
        umlClassModelExtension.operationsOpen = item.operationsOpen
        const context = args.context
        context.serializeReplacement(UMLClassModelExtension.$class, item, umlClassModelExtension)
        args.handled = true
    }
}
 */