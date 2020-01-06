
export class UMLEdgeModel {

    get selectedIndex() {
        return this.$selectedIndex
    }

    set selectedIndex(v) {
        this.$selectedIndex = v
    }

    constructor(data) {

        this.description = (data && data.description) || '';
        this.sourceDeletionDeletesTarget = (data && data.sourceDeletionDeletesTarget) || false;
        this.targetDeletionDeletesSource = (data && data.targetDeletionDeletesSource) || false;
        this.sourceClassName = (data && data.sourceClassName) || "";
        this.targetClassName = (data && data.targetClassName) || "";
        this.sourceLowerBounds = (data && data.sourceLowerBounds) || 0;
        this.sourceUpperBounds = (data && data.sourceUpperBounds) || -1;
        this.targetLowerBounds = (data && data.targetLowerBounds) || 0;
        this.targetUpperBounds = (data && data.targetUpperBounds) || -1;
        this.operations = (data && data.operations) || []
        this.attributes = (data && data.attributes) || []

    }
/* maybe some kind of clone is needed for edge handling
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
    */
}