<template>
  <v-card>
    <v-overlay v-if="modelInstances.length" :absolute="true" :opacity="0.85">
      <v-icon x-large>mdi-folder-lock</v-icon>
      <div>
        Locked because there are model instances
      </div>
    </v-overlay>

    <v-card-title>
      <span class="headline">Edit project {{ gdslProject.name }}</span>
    </v-card-title>

    <v-divider class="ma-0"></v-divider>

    <v-card-text>
      <v-stepper v-model="stepCounter">
        <v-stepper-header>
          <v-stepper-step :complete="stepCounter > 1" step="1">Concept<br>Editor</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="stepCounter > 2" step="2">Shape</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="stepCounter > 3" step="3">Style</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="stepCounter === 4" step="4">Diagram</v-stepper-step>
        </v-stepper-header>
      </v-stepper>

      <v-dialog v-model="editProjectDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
        <template v-slot:activator="{ on, attrs }">
          <div id="app" data-app>
            <v-btn v-on:click="showStepElement(stepCounter)" class="mt-4" color="primary" depressed v-bind="attrs"
                   v-on="on">
              Edit project
            </v-btn>
          </div>
        </template>

        <v-card>
          <v-toolbar dark color="primary" class="rounded-0">
            <v-btn icon dark @click="editProjectDialog = false">
              <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Edit project</v-toolbar-title>
            <v-spacer></v-spacer>

            <v-toolbar-items>
              <v-stepper v-model="stepCounter" class="elevation-0 rounded-0">
                <v-stepper-header>
                  <v-stepper-step @click="showStepElement(stepCounter=1)" :complete="stepCounter > 1" step="1">
                    {{ step1 }}
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step @click="showStepElement(stepCounter=2)" :complete="stepCounter > 2" step="2">
                    {{ step2 }}
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step @click="showStepElement(stepCounter=3)" :complete="stepCounter > 3" step="3">
                    {{ step3 }}
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step @click="showStepElement(stepCounter=4)" :complete="stepCounter === 4" step="4">
                    {{ step4 }}
                  </v-stepper-step>
                </v-stepper-header>
              </v-stepper>

              <v-btn :disabled="continueBtnIsHidden" text
                     v-on:click="initializeEditor(), stepCounter++, showStepElement(stepCounter)">Continue
              </v-btn>
            </v-toolbar-items>
          </v-toolbar>

          <GraphicalEditor v-if="!step1IsHidden"></GraphicalEditor>

          <v-container fluid class="py-0">
            <v-row v-show="step1IsHidden">
              <v-col md="3">
                <div id="source-code-inspection"></div>
                <div id="online-users"></div>
                <div id="outline-nodes"></div>
              </v-col>

              <v-col md="3" class="code-editor" :data-meta-model-id="gdslProject.id" :data-dsl-type="step2">
                <v-card>
                  <v-card-title>
                    <span>{{ step2 }}</span>
                    <v-spacer></v-spacer>
                    <span class="editor-button">
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div :onfocusin="true" class="editor"></div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col md="3" class="code-editor" :data-meta-model-id="gdslProject.id" :data-dsl-type="step3">
                <v-card>
                  <v-card-title>
                    <span>{{ step3 }}</span>
                    <v-spacer></v-spacer>
                    <span class="editor-button">
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn :disabled="step3IsHidden" class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div v-show="!step3IsHidden" class="editor"></div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col md="3" class="code-editor" :data-meta-model-id="gdslProject.id" :data-dsl-type="step4">
                <v-card>
                  <v-card-title>
                    <span>{{ step4 }}</span>
                    <v-spacer></v-spacer>
                    <span class="editor-button">
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn :disabled="step4IsHidden" class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div v-show="!step4IsHidden" class="editor"></div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-container>
        </v-card>
      </v-dialog>
    </v-card-text>

    <v-divider class="ma-0"></v-divider>

    <v-card-actions>
      <v-menu offset-y>
        <template v-slot:activator="{ on }">
          <v-btn color="secondary" outlined v-on="on">
            Validator
            <v-icon class="ml-1">mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item v-on:click="validatorGenerate" id="validatorGenerate">
            <v-list-item-title>Generate / Update Validator</v-list-item-title>
          </v-list-item>
          <v-list-item v-on:click="validatorShow" id="validatorShow">
            <v-list-item-title>Show Validation Rules</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-card-actions>
  </v-card>
</template>
<script>
import ValidatorUtils from "./ValidatorUtils";
import GraphicalEditor from './GraphicalEditor'
import {EventBus} from '../../../eventbus/eventbus'

export default {
  name: 'EditorSelection',
  props: {
    gdslProject: {},
    modelInstances: {},
  },
  components: {
    GraphicalEditor,
  },
  data() {
    return {
      step1: "Concept Editor",
      step2: "shape",
      step3: "style",
      step4: "diagram",
      editProjectDialog: false,
      step1IsHidden: false,
      step2IsHidden: true,
      step3IsHidden: true,
      step4IsHidden: true,
      dialogTextEditor: true,
      continueBtnIsHidden: false,
      stepCounter: 1,
      dslType: ""
    }
  },
  mounted() {
    EventBus.$on("initSteps", (data) => {
      this.stepCounter = data
    })
    EventBus.$on("gdslProjectSelected", gdslProject => {
      metamodelId = gdslProject.id
    })
  },
  methods: {
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
    showStepElement(step) {
      if (step === 1) {
        if (!this.step1IsHidden) {
          this.step1IsHidden = false;
          this.step2IsHidden = true
        } else {
          this.step1IsHidden = false;
          this.step2IsHidden = true;
          this.step3IsHidden = true;
          this.step4IsHidden = true;
          this.continueBtnIsHidden = false;
          this.dslType = "shape"
        }
      }
      if (step === 2) {
        this.step1IsHidden = true, new EditorSelection(elements[0], metamodelId, $(elements[0]).data('dsl-type'))
      }
      if (step === 3) {
        this.step3IsHidden = false, new EditorSelection(elements[1], metamodelId, $(elements[1]).data('dsl-type'))
      }
      if (step === 4) {
        this.step4IsHidden = false, new EditorSelection(elements[2], metamodelId, $(elements[2]).data('dsl-type'));
        this.continueBtnIsHidden = true
      }
    },
    initializeEditor() {
      elements = []
      $('.code-editor').each((i, e) => elements.push(e))
    },

  }
}
import $ from "jquery";
import 'brace';
import 'brace/ext/language_tools';
import 'brace/theme/xcode';
import 'brace/mode/scala';
import {styleLanguage, diagramLanguage, shapeLanguage} from './code-editor/ace-grammar';
import {SourceCodeInspector} from "./code-editor/source-code-inspector";
import {CodeOutline} from "./code-editor/code-outline";
import {OnlineSocket} from "./code-editor/online-socket";
import axios from 'axios'

const modesForModel = {
  'diagram': diagramLanguage,
  'shape': shapeLanguage,
  'style': styleLanguage
};

var elements = []
var metamodelId

class EditorSelection {
  constructor(element, metaModelId, dslType) {
    this.$element = $(element);
    this.metaModelId = metaModelId;
    this.dslType = dslType;
    this.editor = this.initAceEditor(element.querySelector('.editor'));
    this.loadSourceCode();
    this.$element.on('click', '.js-save', () => this.saveSourceCode(this.editor.getValue()));
    this.sourceCodeInspector = new SourceCodeInspector(element, metaModelId, dslType, this.editor);
    this.sourceCodeInspector.runInspection();
    this.codeOutline = new CodeOutline(element, metaModelId, dslType, this.editor);
    let area = "codeEditor-" + dslType + "-" + metaModelId;
    this.onlineSocket = new OnlineSocket(area);
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
    axios.get(
        'http://localhost:9000/rest/v1/meta-models/' + this.metaModelId,
        {withCredentials: true}).then(
        (response) => {
          this.setAceEditorContent(response.data[this.dslType]);
          this.codeOutline.createCodeOutline();
        },
        (error) => console.log('Error loading MetaModel ' + this.metaModelId + ': ' + error)
    )
  }

  setAceEditorContent(content) {
    const session = ace.createEditSession(content, modesForModel[this.dslType]);
    this.editor.setSession(session);
  }

  saveSourceCode(code) {
    axios.put(
        'http://localhost:9000/rest/v1/meta-models/' + this.metaModelId + '/' + this.dslType,
        JSON.stringify(code),
        {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json'
          }
        }
    ).then(
        (response) => {
          this.toggleSaveNotifications('.js-save-successful');
          this.sourceCodeInspector.runInspection();
        },
        (error) => {
          this.toggleSaveNotifications('.js-save-failed');
          console.error(`Save failed`, error);
        }
    )
  }

  toggleSaveNotifications(element) {
    this.$element.find(element).stop(true, true).fadeIn(400).delay(3000).fadeOut(400);
  }
}

</script>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>