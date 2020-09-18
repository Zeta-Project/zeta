<template>
  <div v-on:dragover="dragover" v-on:drop="preventDrop">
    <div class="row">
      <div class="col-md-4">
        <div class="panel panel-default">
          <div class="panel-heading">
            <strong>Projects</strong>
          </div>

          <div class="list-group" v-if="metaModels.length">

            <div v-for="metamodel in metaModels" v-bind:key="metamodel.id"
                 class="list-group-item list-item-container"
                 v-bind:class="{active: gdslProject && metamodel.id === gdslProject.id}">

              <ProjectSelectionRow v-bind:id="metamodel.id" v-bind:name="metamodel.name"/>
            </div>

          </div>

          <div class="panel-body" v-else>
            There are no projects.
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input v-model="inputProjectName" v-on:keyup.enter="createProject" type="text" class="form-control"
                       id="inputProjectName" placeholder="New project name" autocomplete="off">
                <span class="input-group-btn">
                <button v-on:click="createProject" type=button id="btnCreateMetaModel" class="btn btn-default"
                        data-toggle="#tooltip" title="Create project">
                  <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                </button>
                  <!-- launch import modal -->
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importModal"
                        title="Import project">
                  <span class="glyphicon glyphicon-import" aria-hidden=true></span>
                </button>
              </span>
              </div>
              <div>


                <!-- import modal -->
                <div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="importModalLabel"
                     aria-hidden="true">
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      <div class="modal-header modal-header-info">
                        <span class="modal-title" id="importModalLabel">Import project</span>
                        <button id="close-import-modal" type="button" class="close" data-dismiss="modal"
                                aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </div>
                      <div class="modal-body">
                        <div class="form-group">
                          <!-- drop area -->
                          <input v-on:change="onFileChange" ref="file" type="file" accept=".zeta" name="file" id="file">
                          <!-- Drag and Drop container-->
                          <div v-on:click="openFileManager" v-on:drop="dropFile" class="upload-area" id="uploadfile">
                            <span id="uploadtext">{{ uploadText }}</span>
                          </div>
                        </div>
                        <input v-model="importProjectName" type="text" class="form-control" id="importProjectName"
                               placeholder="New Project Name" autocomplete="off">
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button v-on:click="importProject" id="start-import-btn" type="button" class="btn btn-info"
                                data-dismiss="modal" :disabled="!(isValidZetaProjectFile() && isValidProjectName())">
                          Import
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- end import modal -->

            </form>
          </div>
        </div>
      </div>


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
import { EventBus } from "@/eventbus/eventbus"
import ProjectSelectionRow from "./ProjectSelectionRow"
import ValidatorUtils from "./ValidatorUtils"
import ProjectUtils from "./ProjectUtils"
import ModelInstanceUtils from "./ModelInstanceUtils"

export default {
  name: 'DiagramsOverview',
  components: {ProjectSelectionRow},
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
