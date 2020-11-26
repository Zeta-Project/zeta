<template>
  <v-app id="edit-project" >
    <div class="panel panel-default overlay-container">
      <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip"
           title="Locked because there are model instances"></div>
      <div class="panel-heading">
        <strong>Edit project <em>{{ gdslProject.name }}</em></strong>
      </div>

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

            <v-dialog v-model="editProjectDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
              <template v-slot:activator="{ on, attrs }">
                <div id="app" data-app>
                <v-btn v-on:click="showStepElement(stepCounter)" class="list-group-item my-auto" v-bind="attrs" v-on="on">
                  Edit project
                </v-btn>
                </div>
              </template>

              <v-card>
                <br><br>
                <v-toolbar dark color="primary">
                  <v-btn icon dark @click="editProjectDialog = false">
                    <v-icon>mdi-close</v-icon>
                  </v-btn>
                  <v-toolbar-title>Edit project</v-toolbar-title>
                  <v-spacer></v-spacer>

                  <v-stepper v-model="stepCounter">
                    <v-stepper-header>
                      <v-stepper-step @click="showStepElement(stepCounter=1)" :complete="stepCounter > 1" step="1">{{step1}}</v-stepper-step>
                      <v-divider></v-divider>
                      <v-stepper-step @click="showStepElement(stepCounter=2)" :complete="stepCounter > 2" step="2">{{step2}}</v-stepper-step>
                      <v-divider></v-divider>
                      <v-stepper-step @click="showStepElement(stepCounter=3)":complete="stepCounter > 3" step="3">{{step3}}</v-stepper-step>
                      <v-divider></v-divider>
                      <v-stepper-step @click="showStepElement(stepCounter=4)" :complete="stepCounter === 4" step="4">{{step4}}</v-stepper-step>
                    </v-stepper-header>
                  </v-stepper>

                  <v-toolbar-items>
                    <v-btn :disabled="continueBtnIsHidden" color="primary" v-on:click="initializeEditor(), stepCounter++, showStepElement(stepCounter)">Continue</v-btn>
                  </v-toolbar-items>
                </v-toolbar>
                <div>
                  <GraphicalEditor v-if="!step1IsHidden"></GraphicalEditor>
                </div>

                  <div  v-show="step1IsHidden" class="row">
                    <div class="side-box col-md-3">
                      <div id="source-code-inspection"></div>
                      <div id="online-users"></div>
                      <div id="outline-nodes"></div>
                    </div>

                        <div class="container code-editor editor-box col-md-3" :data-meta-model-id="gdslProject.id" :data-dsl-type="step2">
                          <div class="panel panel-default">
                            <div class="panel-heading">
                              <span class="editor-title">{{step2}}</span>
                              <span class="editor-button">
                              <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
                              <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
                              <span class="js-save btn btn-sm btn-primary" title="Save Document">
                                Save
                                <span class="glyphicon glyphicon-floppy-disk"></span>
                              </span>
                            </span>
                            </div>
                            <div class="panel-body editor-body">
                              <div :onfocusin="true" class="editor"></div>
                            </div>
                          </div>
                        </div>

                      <div class="container code-editor editor-box col-md-3" :data-meta-model-id="gdslProject.id" :data-dsl-type="step3">
                        <div class="panel panel-default">
                          <div class="panel-heading">
                            <span class="editor-title">{{step3}}</span>
                            <span class="editor-button">
                              <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
                              <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
                              <span :disabled="step3IsHidden" class="js-save btn btn-sm btn-primary" title="Save Document">
                                Save
                                <span class="glyphicon glyphicon-floppy-disk"></span>
                              </span>
                            </span>
                          </div>
                          <div class="panel-body editor-body">
                            <div v-show="!step3IsHidden" class="editor"></div>
                          </div>
                        </div>
                      </div>

                        <div class="container code-editor editor-box col-md-3" :data-meta-model-id="gdslProject.id" :data-dsl-type="step4">
                          <div class="panel panel-default">
                            <div class="panel-heading">
                              <span class="editor-title">{{step4}}</span>
                              <span class="editor-button">
                                <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
                                <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
                                <span :disabled="step4IsHidden" class="js-save btn btn-sm btn-primary" title="Save Document">
                                  Save
                                  <span class="glyphicon glyphicon-floppy-disk"></span>
                                </span>
                              </span>
                            </div>
                            <div class="panel-body editor-body">
                              <div v-show="!step4IsHidden" class="editor"></div>
                            </div>
                          </div>
                        </div>
                  </div>
              </v-card>
            </v-dialog>
      </v-stepper>
<!--
      <div class="list-group">
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/style'">
          Style
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/shape'">
          Shape
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/diagram'">
          Diagram
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/metamodel/editor/' + gdslProject.id">
          Concept Editor
        </router-link>
      </div>
-->
      <div class="panel-footer dropdown">
        <button class="btn dropdown-toggle" type="button" id="btnValidator" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="true">
          Validator
          <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" aria-labelledby="btnValidator">
          <li><a v-on:click="validatorGenerate" id="validatorGenerate">Generate / Update Validator</a></li>
          <li><a v-on:click="validatorShow" id="validatorShow">Show Validation Rules</a></li>
        </ul>
      </div>
    </div>

  </v-app>
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
  data(){
    return{
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
    EventBus.$on("initSteps",(data) => {
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
    showStepElement(step){
      if(step === 1) {if(!this.step1IsHidden) {this.step1IsHidden = false; this.step2IsHidden = true}
                          else {this.step1IsHidden = false; this.step2IsHidden = true; this.step3IsHidden = true; this.step4IsHidden = true; this.continueBtnIsHidden=false; this.dslType = "shape"}}
      if(step === 2) {this.step1IsHidden = true, new EditorSelection(elements[0], metamodelId, $(elements[0]).data('dsl-type'))}
      if(step === 3) {this.step3IsHidden = false, new EditorSelection(elements[1], metamodelId, $(elements[1]).data('dsl-type'))}
      if(step === 4) {this.step4IsHidden = false, new EditorSelection(elements[2], metamodelId, $(elements[2]).data('dsl-type')); this.continueBtnIsHidden = true}
    },
    initializeEditor () {
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
        (error) => console.log('Error loading MetaModel ' + this.metaModelId + ': ' + error )
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