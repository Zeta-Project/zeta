<template>
  <v-card>
      <v-overlay v-if="hasModelInstanceForProject(modelInstances)" :absolute="true" :opacity="0.85">
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
      <v-stepper>
        <v-stepper-header>
          <v-stepper-step :complete="!isConceptEmpty" step="1">Concept<br>Editor</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="!isShapeEmpty" step="2">Shape</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="!isStyleEmpty" step="3">Style</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="!isDiagramEmpty" step="4">Diagram</v-stepper-step>
        </v-stepper-header>
      </v-stepper>

      <!-- Set 'eager' on dialog to force rendering of the dialog components even if the dialog is still closed,
       we need this to be able to get the editor elements to initialize the ACE editors before opening the dialog -->
      <v-dialog v-model="editProjectDialog" eager fullscreen hide-overlay transition="dialog-bottom-transition">
        <template v-slot:activator="{ on, attrs }">
          <div id="app" data-app>
            <v-btn v-on:click="onOpenEditorDialog" class="mt-4" color="primary" depressed v-bind="attrs"
                   v-on="on">
              Edit project
            </v-btn>
          </div>
        </template>

        <v-card>
          <v-toolbar dark color="primary" class="rounded-0">
            <v-btn icon dark @click="closeEditorDialog">
              <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Edit project</v-toolbar-title>
            <v-spacer></v-spacer>

            <v-toolbar-items>
              <v-stepper v-model="currentStep" class="elevation-0 rounded-0">
                <v-stepper-header>
                  <v-stepper-step class="stepper-step-hover" @click="setCurrentStep(1)" :complete="isConceptStepCompleted" step="1">
                    <span class="stepper-step-hover-text">{{ step1 }}</span>
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step class="stepper-step-hover" @click="setCurrentStep(2)" :complete="isShapeStepCompleted" step="2">
                    <span class="stepper-step-hover-text">{{ step2 }}</span>
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step class="stepper-step-hover" @click="setCurrentStep(3)" :complete="isStyleStepCompleted" step="3">
                    <span class="stepper-step-hover-text">{{ step3 }}</span>
                  </v-stepper-step>
                  <v-divider></v-divider>
                  <v-stepper-step class="stepper-step-hover" @click="setCurrentStep(4)" :complete="isDiagramStepCompleted" step="4">
                    <span class="stepper-step-hover-text">{{ step4 }}</span>
                  </v-stepper-step>
                </v-stepper-header>
              </v-stepper>

              <v-btn :disabled="!allowContinue" text v-on:click="setCurrentStep(++currentStep)">Continue</v-btn>
            </v-toolbar-items>
          </v-toolbar>

          <GraphEditor v-if="isConceptActive" ref="GraphComponent"></GraphEditor>

          <v-container fluid class="py-0">
            <v-row v-show="!isConceptActive">
              <v-col md="3">
                <div id="source-code-inspection"></div>
                <div id="online-users"></div>
                <div id="outline-nodes-container"></div>
              </v-col>

              <v-col md="3" class="code-editor" ref="shapeEditorElement" :data-meta-model-id="gdslProject.id"
                     :data-dsl-type="step2" @click="setCurrentStep(2)" v-bind:class="{pointer: !isShapeActive}">
                <v-card :disabled="!isShapeActive">
                  <v-card-title>
                    <span>{{ step2 }}</span>
                    <v-spacer></v-spacer>
                    <span>
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn :disabled="!isShapeActive" class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div v-show="isShapeActive || !isShapeEmpty" class="editor"></div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col md="3" class="code-editor" ref="styleEditorElement" :data-meta-model-id="gdslProject.id"
                     :data-dsl-type="step3" @click="setCurrentStep(3)" v-bind:class="{pointer: !isStyleActive}">
                <v-card :disabled="!isStyleActive">
                  <v-card-title>
                    <span>{{ step3 }}</span>
                    <v-spacer></v-spacer>
                    <span>
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn :disabled="!isStyleActive" class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div v-show="isStyleActive || !isStyleEmpty" class="editor"></div>
                  </v-card-text>
                </v-card>
              </v-col>

              <v-col md="3" class="code-editor" ref="diagramEditorElement" :data-meta-model-id="gdslProject.id"
                     :data-dsl-type="step4" @click="setCurrentStep(4)" v-bind:class="{pointer: !isDiagramActive}">
                <v-card :disabled="!isDiagramActive">
                  <v-card-title>
                    <span>{{ step4 }}</span>
                    <v-spacer></v-spacer>
                    <span>
                      <v-chip class="js-save-successful" style="display: none" color="green" text-color="white">
                        Saving succeed
                      </v-chip>
                      <v-chip class="js-save-failed" style="display: none" color="red" text-color="white">
                        Saving failed
                      </v-chip>
                      <v-btn :disabled="!isDiagramActive" class="js-save" icon color="primary">
                        <v-icon>mdi-content-save</v-icon>
                      </v-btn>
                    </span>
                  </v-card-title>

                  <v-card-text>
                    <div v-show="isDiagramActive || !isDiagramEmpty" class="editor"></div>
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
import GraphEditor from '../metamodel/graphical-editor/components/graphEditor/GraphEditor'
import {EventBus} from '../../../eventbus/eventbus'

export default {
  name: 'EditorSelection',
  props: {
    gdslProject: {},
    modelInstances: {}
  },
  components: {
    GraphEditor
  },
  data() {
    return {
      step1: "Concept Editor",
      step2: "shape",
      step3: "style",
      step4: "diagram",
      editProjectDialog: false,
      dialogTextEditor: true,
      currentStep: 1,
      shapeEditor: {},
      styleEditor: {},
      diagramEditor: {}
    }
  },
  mounted() {
    EventBus.$on("initSteps", (data) => {
      this.currentStep = data
    })
  },
  methods: {
    hasModelInstanceForProject(modelInstances) {
    const projectId = this.gdslProject.id;
    let result = false;
    modelInstances.forEach(modelInstance => {
      if(modelInstance.graphicalDslId == projectId) {
        result = true;
      }
    })
    return result;
  },
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
    getInitialStep() {
      if (this.isConceptEmpty)
        return 1;

      if (this.isShapeEmpty)
        return 2;

      if (this.isStyleEmpty)
        return 3;

      if (this.isDiagramEmpty)
        return 4;

      return 4; // Every step is already completed, go to last step
    },
    onOpenEditorDialog() {
      const editorElements = this.getEditorElements();

      this.shapeEditor = new EditorSelection(editorElements[0], this.gdslProject.id, this.step2, this.gdslProject.shape)
      this.styleEditor = new EditorSelection(editorElements[1], this.gdslProject.id, this.step3, this.gdslProject.style)
      this.diagramEditor = new EditorSelection(editorElements[2], this.gdslProject.id, this.step4, this.gdslProject.diagram)

      this.setCurrentStep(this.getInitialStep());
    },
    getEditorElements() {
      return [this.$refs.shapeEditorElement, this.$refs.styleEditorElement, this.$refs.diagramEditorElement]
    },
    closeEditorDialog() {
      this.editProjectDialog = false;

      this.shapeEditor?.destroy();
      this.shapeEditor = null;

      this.styleEditor?.destroy();
      this.styleEditor = null;

      this.diagramEditor?.destroy();
      this.diagramEditor = null;
    },
    setCurrentStep(step){
      if(this.currentStep === step) {
        return;
      }

      this.shapeEditor.reset();
      this.styleEditor.reset();
      this.diagramEditor.reset();

      this.currentStep = step;

      switch (step) {
        case 2:
          this.shapeEditor.focus();
          break;
        case 3:
          this.styleEditor.focus();
          break;
        case 4:
          this.diagramEditor.focus();
          break;
      }
    }
  },
  computed: {
    isConceptEmpty: function () {
      return !((this.gdslProject.concept.enums && this.gdslProject.concept.enums.length) ||
          (this.gdslProject.concept.classes && this.gdslProject.concept.classes.length) ||
          (this.gdslProject.concept.references && this.gdslProject.concept.references.length) ||
          (this.gdslProject.concept.attributes && this.gdslProject.concept.attributes.length) ||
          (this.gdslProject.concept.methods && this.gdslProject.concept.methods.length))
    },
    isShapeEmpty: function () {
      return !this.gdslProject.shape;
    },
    isStyleEmpty: function () {
      return !this.gdslProject.style;
    },
    isDiagramEmpty: function () {
      return !this.gdslProject.diagram;
    },
    isConceptStepCompleted: function () {
      return !this.isConceptEmpty && this.currentStep !== 1;
    },
    isShapeStepCompleted: function () {
      return !this.isShapeEmpty && this.currentStep !== 2;
    },
    isStyleStepCompleted: function () {
      return !this.isStyleEmpty && this.currentStep !== 3;
    },
    isDiagramStepCompleted: function () {
      return !this.isDiagramEmpty && this.currentStep !== 4;
    },
    isConceptActive: function () {
      return this.currentStep === 1;
    },
    isShapeActive: function () {
      return this.currentStep === 2;
    },
    isStyleActive: function () {
      return this.currentStep === 3;
    },
    isDiagramActive: function () {
      return this.currentStep === 4;
    },
    allowContinue: function () {
      return this.currentStep < 4;
    }
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
import * as ace from "brace";

const modesForModel = {
  'diagram': diagramLanguage,
  'shape': shapeLanguage,
  'style': styleLanguage
};

class EditorSelection {
  constructor(element, metaModelId, dslType, content) {
    this.$element = $(element);
    this.metaModelId = metaModelId;
    this.dslType = dslType;
    this.editor = this.initAceEditor(element.querySelector('.editor'));
    this.$element.on('click', '.js-save', () => this.saveSourceCode(this.editor.getValue()));
    this.sourceCodeInspector = new SourceCodeInspector(element, metaModelId, dslType, this.editor);
    this.sourceCodeInspector.runInspection();
    this.codeOutline = new CodeOutline(element, metaModelId, dslType, this.editor);
    let area = "codeEditor-" + dslType + "-" + metaModelId;
    this.onlineSocket = new OnlineSocket(area);

    this.setAceEditorContent(content);
    this.codeOutline.createCodeOutline();
  }

  initAceEditor(element) {
    const editor = ace.edit(element);
    editor.setTheme("ace/theme/xcode");
    editor.getSession().setMode("ace/mode/scala");
    editor.$blockScrolling = Number.POSITIVE_INFINITY;
    editor.setOptions({
      "enableBasicAutocompletion": true,
      "enableLiveAutocompletion": true
    });
    return editor;
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

  destroy() {
    this.editor.destroy()
  }

  focus(){
    this.editor.focus();
  }

  reset(){
    this.editor.gotoLine(1);
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

.editor {
  flex-grow: 1;
  height: 500px; /* fallback for older browsers */
  height: calc(100vh - 175px);
}

.stepper-step-hover:hover {
  cursor: pointer;
}

.stepper-step-hover:hover .stepper-step-hover-text {
  filter: brightness(65%);
}

.pointer {
  cursor: pointer !important;
}
</style>