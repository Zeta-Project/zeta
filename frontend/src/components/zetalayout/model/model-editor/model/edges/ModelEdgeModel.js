
export class ModelEdgeModel {

    get selectedIndex() {
        return this.$selectedIndex
    }

    set selectedIndex(v) {
        this.$selectedIndex = v
    }

    constructor(data) {

        this.name = (data && data.name) || '';
        this.description = (data && data.description) || '';
        this.sourceDeletionDeletesTarget = (data && data.sourceDeletionDeletesTarget) || false;
        this.targetDeletionDeletesSource = (data && data.targetDeletionDeletesSource) || false;
        this.sourceClassName = (data && data.sourceClassName) || "";
        this.targetClassName = (data && data.targetClassName) || "";
        this.sourceLowerBounds = (data && data.sourceLowerBounds) || 0;
        this.sourceUpperBounds = (data && data.sourceUpperBounds) || -1;
        this.targetLowerBounds = (data && data.targetLowerBounds) || 0;
        this.targetUpperBounds = (data && data.targetUpperBounds) || -1;
        this.methods = (data && data.methods) || []
        this.attributes = (data && data.attributes) || []
        this.labels = (data && data.labels) || []

    }
}
