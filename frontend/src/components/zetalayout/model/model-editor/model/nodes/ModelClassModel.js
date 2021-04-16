/**
 * The data model of the Model node style.
 * A modification counter tracks if there have been modifications since the last drawing of this model.
 */

export class ModelClassModel {

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
        this.name = (data && data.name) || ''
        this.description = (data && data.description) || ''
        this.abstractness = (data && data.abstractness) || false
        this.superTypeNames = (data && data.superTypeNames) || []
        this.stereotype = (data && data.stereotype) || '' //if data && data.stereotype != null, use data.stereotype. else ''
        this.constraint = (data && data.constraint) || ''
        this.className = (data && data.className) || 'Model Class Node'
        this.attributes = (data && data.attributes) || []
        this.methods = (data && data.methods) || []
        this.attributesOpen = this.attributes.length > 0
        this.methodsOpen = this.methods.length > 0
        this.$selectedIndex = -1
        this.selectedCategory = 1
        this.$modCount = 0

    }

    modify() {
        this.$modCount++
    }

    clone() {
        const clone = new ModelClassModel()
        clone.stereotype = this.stereotype
        clone.constraint = this.constraint
        clone.className = this.className
        clone.name = this.name
        clone.abstractness = this.abstractness
        clone.attributes = Array.from(this.attributes)
        clone.methods = Array.from(this.methods)
        clone.attributesOpen = this.attributesOpen
        clone.methodsOpen = this.methodsOpen
        clone.selectedIndex = this.selectedIndex
        return clone
    }
}