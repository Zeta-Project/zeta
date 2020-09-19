<template>
  <div v-on:dragover="dragover" v-on:drop="preventDrop">
    <div class="row">

      <ProjectSelection class="col-md-4" v-bind:meta-models="this.metaModels" v-bind:gdsl-project="this.gdslProject"/>


      <div id="edit-project" class="col-md-4" v-if="gdslProject">
        <div class="panel panel-default overlay-container">
          <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip"
               title="Locked because there are model instances"></div>
          <div class="panel-heading">
            <strong>Edit project <em>{{ gdslProject.name }}</em></strong>
          </div>
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


      <div class="col-md-4" v-if="gdslProject">
        <div id="model-instance-container" class="panel panel-default">
          <div class="panel-heading">
            <strong>Model Instances</strong>
          </div>

          <div class="panel-body" v-if="!modelInstances.length">
            <span class="text-muted">There are no model instances.</span>
          </div>

          <div class="list-group" v-else>
            <a v-for="model in modelInstances" v-bind:key="model.id" href="@routes.ScalaRoutes.getModelEditor(model.id)"
               class="list-group-item list-item-container">
              {{ model.name }}
              <div v-on:click="deleteModelInstance(model.id)"
                   class="delete-list-item delete-model-instance glyphicon glyphicon-trash" data-toggle="tooltip"
                   title="Delete model instance"></div>
              <div v-on:click="validateModelInstance(model.id)"
                   class="validate-list-item validate-model-instance glyphicon glyphicon-thumbs-up"
                   data-toggle="tooltip" title="Validate model instance against its meta model"></div>
            </a>
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input v-on:keyup.enter="createModelInstance" v-model="inputModelName" type="text" class="form-control"
                       id="inputModelName" placeholder="New model name" autocomplete="off">
                <span class="input-group-btn">
                  <button v-on:click="createModelInstance" type=button id="btnCreateModelInstance"
                          class="btn btn-default" data-toggle="tooltip" title="Create model instace">
                    <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                  </button>
                </span>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-link right">
      <a href="@routes.ScalaRoutes.getWebApp()">
        <button class="btn">
          Generators App <span class="glyphicon glyphicon-chevron-right"></span>
        </button>
      </a>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import {EventBus} from "@/eventbus/eventbus"
import ValidatorUtils from "./ValidatorUtils"
import ProjectUtils from "./ProjectUtils"
import ModelInstanceUtils from "./ModelInstanceUtils"
import ProjectSelection from "./ProjectSelection";

export default {
  name: 'DiagramsOverview',
  components: {ProjectSelection},
  props: {
    msg: String
  },
  data() {
    return {
      metaModels: [
        {
          id: "",
          name: "loading..."
        }
      ],
      gdslProject: {
        id: "",
        name: "",
        concept: "",
        diagram: "",
        shape: "",
        style: "",
        validator: ""
      },
      modelInstances: [
        /*{
          id: "UUID",
          graphicalDslId: "UUID",
          name: "String"
        }*/
      ],
      inputProjectName: "",
      selectedProjectId: null,
      inputModelName: "",
      uploadText: "Drag and Drop .zeta file here...",
      importProjectName: "",
      file: null
    }
  },
  methods: {
    loadProjects() {
      axios.get("http://localhost:9000/overview", {withCredentials: true}).then(
          (response) => {
            this.metaModels = response.data.metaModels;
            this.modelInstances = response.data.modelInstances
          },
          (error) => EventBus.$emit("errorMessage", "Could not load metamodels: " + error)
      )
    },
    routeParamChanged() {
      if (!this.$route.params.id || this.$route.params.id == "") {
        this.gdslProject = null
        EventBus.$emit("gdslProjectUnselected")
      } else {
        axios.get(
            "http://localhost:9000/rest/v1/meta-models/" + this.$route.params.id,
            {withCredentials: true}
        ).then(
            (response) => {
              this.gdslProject = response.data;
              EventBus.$emit("gdslProjectSelected", response.data)
            },
            (error) => EventBus.$emit("errorMessage", "Could not load selected metamodel: " + error)
        )
      }
    },
    createProject() {
      ProjectUtils.createProject(this.inputProjectName)
    },
    deleteProject(metaModelId) {
      ProjectUtils.deleteProject(metaModelId)
    },
    importProject() {
      ProjectUtils.importProject(this.file, this.importProjectName)
    },
    invite() {
      ProjectUtils.inviteToProject(this.selectedProjectId, this.inviteProjectName)
    },
    duplicate() {
      ProjectUtils.duplicateProject(this.selectedProjectId, this.duplicateProjectName)
    },
    exportProject(metaModelId) {
      ProjectUtils.exportProject(metaModelId)
    },
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
    validateModelInstance(modelId) {
      ValidatorUtils.validate(modelId)
    },
    createModelInstance() {
      ModelInstanceUtils.createInstance(this.inputModelName, this.$route.params.id)
    },
    deleteModelInstance(modelId) {
      ModelInstanceUtils.deleteInstance(modelId, this.$route.params.id)
    },
    dragover(event) {
      event.preventDefault();
      event.stopPropagation();
      this.uploadText = "Drag here"
    },
    preventDrop(event) {
      event.preventDefault();
    },
    openFileManager() {
      this.$refs.file.click()
    },
    dropFile(event) {
      event.preventDefault();
      this.file = event.dataTransfer.files[0];
      this.onProjectSelected();
    },
    onFileChange(event) {
      this.file = event.target.files[0];
      this.onProjectSelected();
    },
    onProjectSelected() {
      if (this.isValidZetaProjectFile()) {
        this.uploadText = this.file.name
        const projectNameFromFile = this.file.name.split(".zeta")[0].split("_")[0];
        this.importProjectName = projectNameFromFile
      } else {
        this.uploadText = "Invalid zeta project file!"
      }
    },
    isValidZetaProjectFile() {
      return this.file && this.file.name.endsWith(".zeta");
    },
    isValidProjectName() {
      return this.importProjectName.trim() !== "";
    }
  },
  created() {
    EventBus.$on('metaModelAdded', metamodel => {
      this.metaModels.push(metamodel)
    });
    EventBus.$on('metaModelRemoved', metamodelID => {
      let i = this.metaModels.map(item => item.id).indexOf(metamodelID) // find index of your object
      this.metaModels.splice(i, 1)
      if (this.$route.params.id === metamodelID) this.$router.push("/zeta/overview").catch(err => {
      })
    });
    EventBus.$on('reloadProjects', () => {
      this.loadProjects()
    });
    EventBus.$on('projectSelected', metaModelId => {
      this.selectedProjectId = metaModelId
    })

  },
  mounted() {
    this.loadProjects()
    this.routeParamChanged()
  },
  watch: {
    '$route': 'routeParamChanged'
  }
}

</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}

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
