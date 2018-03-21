import $ from "jquery";

export class SourceCodeInspector {

  constructor(element, metaModelId, dslType) {
    this.$element = $(element);
    this.metaModelId = metaModelId;
    this.dslType = dslType;
    this.inspection = this.$element.find('.source-code-inspection');
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
            this.showHintFailureInOtherDsl(response.errorDsl);
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
    this.inspection.text(errorMessage);
    this.inspection.css('border-color', 'green');
    this.inspection.css('background-color', 'lightgreen');
    this.fadeInFadeOut();
    this.removeClickListener();
  }

  showHintFailureInCurrentDsl(errorMessages) {
    const errorMessage = errorMessages
      .map(errorMessage => `- ${errorMessage}`)
      .join('\n');
    this.inspection.text(errorMessage);
    this.inspection.css('border-color', 'red');
    this.inspection.css('background-color', 'salmon');
    this.fadeIn();
    this.removeClickListener();
  }

  showHintFailureInOtherDsl(otherDsl) {
    const errorMessage = `${SourceCodeInspector.capitalize(otherDsl)} contains one or more errors!`;
    this.inspection.text(errorMessage);
    this.inspection.css('border-color', 'red');
    this.inspection.css('background-color', 'lightsalmon');
    this.fadeIn();
    this.installClickListener(otherDsl);
  }

  static capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
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
    this.inspection.stop(true, true).fadeIn(0).delay(0).animate({opacity: 1});
  }

  fadeInFadeOut() {
    this.inspection.stop(true, true).fadeIn(400).delay(3000).animate({opacity: 0});
  }

}