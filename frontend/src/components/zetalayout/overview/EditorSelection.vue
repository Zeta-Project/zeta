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
          <v-stepper-step :complete="stepCounter > 4" step="4">Diagram</v-stepper-step>
        </v-stepper-header>


        <v-stepper-items>

      <!--STEP1-->
          <v-stepper-content step="1">
            <v-dialog v-model="editProjectDialog" fullscreen hide-overlay transition="dialog-bottom-transition">
              <template v-slot:activator="{ on, attrs }">
                <div id="app" data-app>
                <v-btn class="list-group-item my-auto" v-bind="attrs" v-on="on">
                  Start edit project
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
                      <v-stepper-step @click="showStepElement(stepCounter=4)" :complete="stepCounter > 4" step="4">{{step4}}</v-stepper-step>
                    </v-stepper-header>
                  </v-stepper>

                  <v-toolbar-items>
                    <v-btn small color="primary" @click="showStepElement(stepCounter)">Continue</v-btn>
                  </v-toolbar-items>
                </v-toolbar>
                <div>
                  <GraphicalEditor v-if="!step12IsHidden" ></GraphicalEditor>
                </div>

                  <div v-if="step12IsHidden" class="row">

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
                              <div class="editor2"></div>
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
                              <span disabled class="js-save btn btn-sm btn-primary" title="Save Document">
                                Save
                                <span class="glyphicon glyphicon-floppy-disk"></span>
                              </span>
                            </span>
                          </div>
                          <div class="panel-body editor-body">
                            <div class="editor2"></div>
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
                              <span disabled class="js-save btn btn-sm btn-primary" title="Save Document">
                                Save
                                <span class="glyphicon glyphicon-floppy-disk"></span>
                              </span>
                            </span>
                            </div>
                            <div class="panel-body editor-body">
                              <div class="editor2"></div>
                            </div>
                          </div>
                        </div>

                  </div>

              </v-card>
            </v-dialog>
            <!--   <br>
               <div>
                 <div class="col-md-4"></div>
                  <div class="col-md-4"></div>
                  <v-btn color="primary" class="col-md-4 list-group-item my-auto" @click="stepCounter = 2">Continue</v-btn>
             </div> -->
          </v-stepper-content>

      <!--STEP2-->
          <!--:to="'/zeta/codeEditor/editor/' + gdslProject.id + '/shape'"-->
          <v-stepper-content step="2">
          <!--  <v-btn class="list-group-item my-auto"  v-bind="attrs" v-on="on">
              Edit diagram
            </v-btn>
            <br>
            <div>
              <v-btn margin-bottom="15px" class="col-md-4 list-group-item my-auto" @click="stepCounter = 1">Back</v-btn>
              <div class="col-md-4"></div>
              <v-btn class="col-md-4 list-group-item my-auto" @click="stepCounter = 3">Continue</v-btn>
            </div>
            -->
          </v-stepper-content>

      <!--STEP3-->
          <v-stepper-content step="3">
        <!--    <v-btn class="list-group-item my-auto"  v-bind="attrs" v-on="on">
              Edit diagram
            </v-btn>
            <br>
            <div>
              <v-btn margin-bottom="15px" class="col-md-4 list-group-item my-auto" @click="stepCounter = 2">Back</v-btn>
              <div class="col-md-4"></div>
              <v-btn class="col-md-4 list-group-item my-auto" @click="stepCounter = 4">Continue</v-btn>
            </div>
            -->
          </v-stepper-content>

      <!--STEP4-->
          <v-stepper-content step="4">
         <!--   <v-btn class="list-group-item my-auto"  v-bind="attrs" v-on="on">
              Edit diagram
            </v-btn>
            <br>
            <div>
              <v-btn margin-bottom="15px" class="col-md-4 list-group-item my-auto" @click="stepCounter = 2">Back</v-btn>
              <div class="col-md-4"></div>
              <div class="col-md-4"></div>
            </div>
            -->
          </v-stepper-content>

        </v-stepper-items>

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


<!--

    <div>
      <v-dialog v-model="dialogTextEditor"  fullscreen transition="dialog-bottom-transition">
        <template v-slot:activator="{ on, attrs }">

        </template>
        <v-card>
          <br><br>
          <v-btn class="mx-2" fab color="white" @click="editProjectDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>

          <div class="row">

               <div class="side-box col-md-3">
                <div id="source-code-inspection"></div>
                <div id="online-users"></div>
                <div id="outline-nodes"></div>
              </div>


            <v-card class="col-md-3" ><h1>Shape</h1>
              <div class="container code-editor editor-box" :data-meta-model-id="gdslProject.id" :data-dsl-type="dslType">
                <div class="panel panel-default">
                  <div class="panel-heading">
                    <span class="editor-title">{{ dslType }}</span>
                    <span class="editor-button">
              <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
              <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
              <span class="js-save btn btn-sm btn-primary" title="Save Document">
                  Save <span class="glyphicon glyphicon-floppy-disk"></span>
              </span>
          </span>
                  </div>
                  <div class="panel-body editor-body">
                    <div class="editor"></div>
                  </div>
                </div>
              </div>
            </v-card>


          <v-card class="col-md-3" ><h1>Style</h1>
            <div class="container code-editor editor-box" :data-meta-model-id="gdslProject.id" :data-dsl-type="dslType">
              <div class="panel panel-default">
                <div class="panel-heading">
                  <span class="editor-title">{{ dslType }}</span>
                  <span class="editor-button">
              <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
              <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
              <span class="js-save btn btn-sm btn-primary" title="Save Document">
                  Save <span class="glyphicon glyphicon-floppy-disk"></span>
              </span>
          </span>
                </div>
                <div class="panel-body editor-body">
                  <div class="editor"></div>
                </div>
              </div>
            </div>
          </v-card>



            <v-card class="col-md-3" ><h1>Diagram</h1></v-card>
          </div>
        </v-card>
      </v-dialog>
    </div>
-->



  </v-app>
</template>
<script>
import ValidatorUtils from "./ValidatorUtils";
import CodeEditor from '../metamodel/CodeEditor'
import GraphicalEditor from '../metamodel/GraphicalEditor'

export default {
  name: 'EditorSelection',
  props: {
    gdslProject: {},
    modelInstances: {},
  },
  components: {
    CodeEditor,
    GraphicalEditor,
  },
  data(){
    return{
      step1: "Concept Editor",
      step2: "Shape",
      step3: "Style",
      step4: "Diagram",
      editProjectDialog: false,
      step12IsHidden: false,
      step234IsHidden: true,
      dialogTextEditor: true,
      stepCounter: 1,
    }
  },
  methods: {
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
    showStepElement(step){
      this.stepCounter++
      if(step === 1) { if(this.step12IsHidden) {this.step12IsHidden = false; this.step234IsHidden = true}
                            else {this.step12IsHidden = true; this.step234IsHidden = false;
                                  //new EditorSelection($('.code-editor'), $('.code-editor').data('meta-model-id'), $('.code-editor').data("shape"));
                                  this.initializeEditor()

                                  }
                        }
      if(step === 2) {this.step12IsHidden = true
        }
      if(step === 3) {

      }if(step === 4) {
        }
    },
    initializeEditor () {
      console.log("111")
      $('.code-editor').each((i, e) => new EditorSelection(e, $(e).data('meta-model-id'), $(e).data('dsl-type')));
      console.log("333")
    },
    mounted() {
      this.initializeEditor()
    },
    watch: {
      '$route': 'initializeEditor'
    }
  }
}
import $ from "jquery";
import 'brace';
import 'brace/ext/language_tools';
import 'brace/theme/xcode';
import 'brace/mode/scala';
import {styleLanguage, diagramLanguage, shapeLanguage} from '../metamodel/code-editor/ace-grammar';
import {SourceCodeInspector} from "../metamodel/code-editor/source-code-inspector";
import {CodeOutline} from "../metamodel/code-editor/code-outline";
import {OnlineSocket} from "../metamodel/code-editor/online-socket";
import axios from 'axios'


const modesForModel = {
  'diagram': diagramLanguage,
  'shape': shapeLanguage,
  'style': styleLanguage
};
class EditorSelection{
  constructor(element, metaModelId, dslType) {
    console.log("YES")
    this.$element = (element);
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