import $ from "jquery";

export class SourceCodeInspector {

    constructor(element, metaModelId, dslType, editor) {
        this.$element = $(element);
        this.metaModelId = metaModelId;
        this.editor = editor;
        this.markers = [];
        this.dslType = dslType;
        this.inspection = $('#source-code-inspection');
        this.inspectionAlert = $('<div>').addClass('clearfix');
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
                        this.showHintFailureInCurrentDsl(response.messages, response.position)
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
        this.removeAllMarkers();
        this.inspectionAlert.text(errorMessage);
        this.inspectionAlert.removeClass().addClass('success');
        this.fadeInFadeOut();
        this.removeClickListener();
    }

    showHintFailureInCurrentDsl(errorMessages, position) {
        const errorMessage = errorMessages
            .map(errorMessage => `- ${errorMessage}`)
            .join('\n');
        this.inspectionAlert.text(errorMessage);
        if(position.line > 0) {
            const lineEl = $("<span>").addClass("lineNumber").text(position.line);
            this.inspectionAlert.append(lineEl);
        }
        this.inspectionAlert.removeClass().addClass('error');
        this.markEditorLine(position);
        this.fadeIn();
        this.installClickListenerScroll(position);
    }

    showHintFailureInOtherDsl(otherDsl, errors) {
        const errorMessage = `${SourceCodeInspector.capitalize(otherDsl)} contains ${errors.length} ${SourceCodeInspector.pluralize('error', errors.length)}!`;
        this.inspectionAlert.text(errorMessage);
        this.inspectionAlert.removeClass().addClass('warning');
        this.fadeIn();
        this.installClickListener(otherDsl);
    }

    removeClickListener() {
        this.inspection.off("click");
        this.inspection.css('cursor', 'auto');
    }

    installClickListener(dsl) {
        const editorUrl = document.URL.replace(`/${this.dslType}`, `/${dsl}`);
        this.inspection.click(() => window.location = editorUrl);
        this.inspection.css('cursor', 'pointer');
    }

    installClickListenerScroll(position) {
        this.inspection.click(() => {
            this.editor.scrollToLine(position.line, true, true, function () {
            });
            this.editor.gotoLine(position.line, position.column - 1, true);
        });
        this.inspection.css('cursor', 'pointer');
    }

    fadeIn() {
        const el = this.inspection.stop(true, true);
        SourceCodeInspector.animateToNewHeight(el);
    }

    fadeInFadeOut() {
        const el = this.inspection.stop(true, true);
        SourceCodeInspector.animateToNewHeight(el);
        setTimeout(function () {
            SourceCodeInspector.animateToZero(el);
        }, 3000);
    }

    markEditorLine(position) {
        this.removeAllMarkers();
        const ace = require('brace');
        const Range = ace.acequire('ace/range').Range;
        const line = position.line - 1,
            from = position.column - 1,
            to = position.column;
        const lineErrorMarkerRange = new Range(line, 0, line, 1),
            charErrorMarkerRange = new Range(line, from, line, to);
        this.markers.push(this.editor.session.addMarker(lineErrorMarkerRange, "lineErrorMarker", "fullLine"));
        this.markers.push(this.editor.session.addMarker(charErrorMarkerRange, "charErrorMarker", "background"));
    }

    removeAllMarkers() {
        for (let i = 0; i < this.markers.length; i++)
            this.editor.session.removeMarker(this.markers[i]);
        this.markers = [];
    }

    static animateToNewHeight(el) {
        const curHeight = el.height(),
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