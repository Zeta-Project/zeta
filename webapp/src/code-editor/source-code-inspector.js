import $ from "jquery";

export class SourceCodeInspector {

    constructor(element, metaModelId, dslType) {
        this.$element = $(element);
        this.metaModelId = metaModelId;
        this.dslType = dslType;
        this.inspection = $('#source-code-inspection');
        this.inspectionAlert = $('<div>');
        this.inspection.append(this.inspectionAlert);
        this.sourceCodeOk = true;
        this.runInspection();
    }

    runInspection() {
        fetch(`/rest/v2/meta-models/${this.metaModelId}/triggerParse`, {
            method: 'GET',
            credentials: 'same-origin'
        })
            .then(response => response.json())
            .then(response => {
                if (response.success) {
                    const hadErrorsBefore = !this.sourceCodeOk;
                    if (hadErrorsBefore) {
                        this.showHintAllErrorsRemoved();
                    }
                } else {
                    if (response.errorDsl === this.dslType) {
                        this.showHintFailureInCurrentDsl(response.messages)
                    } else {
                        this.showHintFailureInOtherDsl(response.errorDsl, response.messages);
                    }
                }
                this.sourceCodeOk = response.success;
            })
            .catch(err => {
                console.error(err);
                alert('an unexpected error occurred');
            });
    }

    showHintAllErrorsRemoved() {
        const errorMessage = 'All errors were removed, great!';
        this.inspectionAlert.text(errorMessage);
        this.inspectionAlert.removeClass().addClass('success');
        this.fadeInFadeOut();
        this.removeClickListener();
    }

    showHintFailureInCurrentDsl(errorMessages) {
        const errorMessage = errorMessages
            .map(errorMessage => `- ${errorMessage}`)
            .join('\n');
        this.inspectionAlert.text(errorMessage);
        this.inspectionAlert.removeClass().addClass('error');
        this.fadeIn();
        this.removeClickListener();
    }

    showHintFailureInOtherDsl(otherDsl, errors) {
        const errorMessage = `${SourceCodeInspector.capitalize(otherDsl)} contains ${errors.length} ${SourceCodeInspector.pluralize('error', errors.length)}!`;
        this.inspectionAlert.text(errorMessage);
        this.inspectionAlert.removeClass().addClass('warning');
        this.fadeIn();
        this.installClickListener(otherDsl);
    }

    removeClickListener() {
        this.inspection.click(() => {
        });
        this.inspection.css('cursor', 'auto');
    }

    installClickListener(dsl) {
        const editorUrl = document.URL.replace(`/${this.dslType}`, `/${dsl}`);
        this.inspection.click(() => window.location = editorUrl);
        this.inspection.css('cursor', 'pointer');
    }

    fadeIn() {
        let el = this.inspection.stop(true, true);
        SourceCodeInspector.animateToNewHeight(el);
    }

    fadeInFadeOut() {
        let el = this.inspection.stop(true, true);
        SourceCodeInspector.animateToNewHeight(el);
        setTimeout(function () {
            SourceCodeInspector.animateToZero(el);
        }, 3000);
    }

    static animateToNewHeight(el) {
        let curHeight = el.height(),
            autoHeight = el.css('height', 'auto').outerHeight();
        return el.height(curHeight)
            .animate({height: autoHeight}, 400)
            .height('auto');
    }

    static animateToZero(el) {
        return el.animate({height: 0}, 400);
    }

    // static helpers

    static pluralize(string, x) {
        if (x === 1) {
            return string;
        }
        return `${string}s`;
    }

    static capitalize(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

}