<template>
  <div id="edit-project" >
    <div class="panel panel-default overlay-container">
      <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip"
           title="Locked because there are model instances"></div>
      <div class="panel-heading">
        <strong>Edit project <em>{{ gdslProject.name }}</em></strong>
      </div>

      <v-stepper v-model="e1">
        <v-stepper-header>
          <v-stepper-step :complete="e1 > 1" step="1"> Concept Editor</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 2" step="2"> Diagram</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 3" step="3"> Shape</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step :complete="e1 > 4" step="4"> Style</v-stepper-step>
        </v-stepper-header>

        <div class="list-group">
        <v-stepper-items>
          <v-stepper-content step="1">
            <v-dialog v-model="dialog"  height="1000px" transition="dialog-bottom-transition">
              <template v-slot:activator="{ on, attrs }">
              <div id="app" data-app>
              <v-btn class="list-group-item my-auto" v-bind="attrs" v-on="on">
                Edit Concept Editor
              </v-btn>
              </div>
            </template>
             <!--HIER AUFRUF VON EDITOR-->
              <v-btn class="mx-2" fab color="indigo" @click="dialog = false">
                <v-icon>mdi-CloseCircle</v-icon>
              </v-btn>
              <CodeEditor />
            </v-dialog>
            <div class="mx-4">
              <v-btn max-width="200px" class="col-md-4 mx-auto" @click="e1 = 2">Back</v-btn>
            </div>
            <v-btn class="col-md-4" @click="e1 = 2">Continue</v-btn>
          </v-stepper-content>

          <v-stepper-content step="2">
            <v-btn color="primary" @click="e1 = 3"></v-btn>
            <router-link :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/diagram'">
              Edit diagram
            </router-link>
          </v-stepper-content>

          <v-stepper-content step="3">
            <v-btn color="primary" @click="e1 = 4"></v-btn>
            <router-link :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/shape'">
              Edit shape
            </router-link>
          </v-stepper-content>

          <v-stepper-content step="4">
            <router-link :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/style'">
              Edit style
            </router-link>
          </v-stepper-content>

        </v-stepper-items>
        </div>
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
  </div>
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
      dialog: false,
      e1: 1,
    }
  },
  methods: {
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
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