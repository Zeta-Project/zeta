import './webpage';

import './code-editor/codeEditor.css';

import $ from "jquery";
import 'brace';
import 'brace/ext/language_tools';
import 'brace/theme/xcode';
import 'brace/mode/scala';
import { styleLanguage, testLanguage } from './code-editor/ace-grammar';

$(document).ready(() => {
  $('.code-editor').each((i, e) => new CodeEditor(e, $(e).data('meta-model-id'), $(e).data('dsl-type')));
});

const modesForModel = {
  'diagram': testLanguage,
  'shape': testLanguage,
  'style': styleLanguage
};

class CodeEditor {
  constructor(element, metaModelId, dslType) {
    this.$element = $(element);
    this.metaModelId = metaModelId;
    this.dslType = dslType;
    this.editor = this.initAceEditor(element.querySelector('.editor'));
    this.loadSourceCode();
    this.$element.on('click', '.js-save', () => this.saveSourceCode(this.editor.getValue()));
    this.sourceCodeInspection = this.$element.find('.source-code-inspection');
    this.sourceCodeOk = true;
    this.inspectSourceCode();
  }

  initAceEditor(element) {
    const editor = ace.edit(element);
    editor.setTheme("ace/theme/xcode");
    editor.getSession().setMode("ace/mode/scala");
    editor.$blockScrolling = Number.PositiveInfinity;
    editor.setOptions({
      "enableBasicAutocompletion": true,
      "enableLiveAutocompletion": true
    });
    return editor;
  }

  loadSourceCode() {
    fetch(`/rest/v1/meta-models/${this.metaModelId}`, {
      method: 'GET',
      credentials: 'same-origin'
    })
    .then(response => response.json())
    .then(metaModel => this.setAceEditorContent(metaModel[this.dslType]))
    .catch(err => console.log(`Error loading MetaModel '${this.metaModelId}': ${err}`));
  }

  setAceEditorContent(content) {
    const session = ace.createEditSession(content, modesForModel[this.dslType]);
    this.editor.setSession(session);
  }

  saveSourceCode(code) {
    fetch(`/rest/v1/meta-models/${this.metaModelId}/${this.dslType}`, {
      headers: {
        'Content-Type': 'application/json'
      },
      method: 'PUT',
      credentials: 'same-origin',
      body: JSON.stringify(code)
    })
    .then(() => {
      this.toggleSaveNotifications('.js-save-successful');
      this.inspectSourceCode();
    })
    .catch(err => {
      this.toggleSaveNotifications('.js-save-failed');
      console.error(`Save failed`, err);
    });
  }

  toggleSaveNotifications(element) {
    this.$element.find(element).stop(true, true).fadeIn(400).delay(3000).fadeOut(400);
  }

  inspectSourceCode() {
    fetch(`/rest/v2/meta-models/${this.metaModelId}/triggerParse`, {
      method: 'GET',
      credentials: 'same-origin'
    })
    .then(response => response.json())
    .then(response => {
      const inspection = this.sourceCodeInspection;
      if (response.success) {
        const hadErrorsBefore = !this.sourceCodeOk;
        if (hadErrorsBefore) {
          inspection.text('All errors were removed, great!');
          inspection.css('border-color', 'green');
          inspection.css('background-color', 'lightgreen');
          inspection.stop(true, true).fadeIn(400).delay(3000).animate({opacity: 0});
        }
        this.sourceCodeOk = true;
      } else {
        this.sourceCodeOk = false;
        const errors = response.messages.join();
        inspection.text(`some error:\n${errors}`);
        inspection.css('border-color', 'red');
        inspection.css('background-color', 'salmon');
        inspection.stop(true, true).fadeIn(0).delay(0).animate({opacity: 1});
      }
    })
    .catch(err => {
        console.error(err);
        alert('an unexpected error occurred');
    });
  }

}
