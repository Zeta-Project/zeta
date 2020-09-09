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
                 v-bind:class="{active: gdslProject && metamodel.id == gdslProject.id}">

              <div v-on:click="deleteProject(metamodel.id)" class="delete-list-item delete-project glyphicon glyphicon-trash" data-toggle="tooltip" title="Delete project"></div>
              <div v-on:click="exportProject(metamodel.id)" class="delete-list-item export-project glyphicon glyphicon-export" data-toggle="tooltip" title="Export project"></div>
              <div v-on:click="selectedProjectId = metamodel.id" class="delete-list-item duplicate-project glyphicon glyphicon-duplicate" data-toggle="modal" data-target="#duplicateModal" title="Duplicate project"></div>
              <div v-on:click="selectedProjectId = metamodel.id" class="delete-list-item invite-to-project glyphicon glyphicon-send" data-toggle="modal" data-target="#inviteModal" title="Invite other users">

              </div>
              <router-link style="text-decoration: none; color: initial" :to="'/zeta/overview/' + metamodel.id">
                <div> {{metamodel.name}}</div>
              </router-link>
            </div>

          </div>

          <div class="panel-body" v-else>
            There are no projects.
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input v-model="inputProjectName" v-on:keyup.enter="createProject" type="text" class="form-control" id="inputProjectName" placeholder="New project name" autocomplete="off">
                <span class="input-group-btn">
                <button v-on:click="createProject" type=button id="btnCreateMetaModel" class="btn btn-default" data-toggle="#tooltip" title="Create project">
                  <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                </button>
                  <!-- launch import modal -->
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importModal" title="Import project">
                  <span class="glyphicon glyphicon-import" aria-hidden=true></span>
                </button>
              </span>
              </div>
              <div>


                <!-- import modal -->
                <div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="importModalLabel" aria-hidden="true">
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      <div class="modal-header modal-header-info">
                        <span class="modal-title" id="importModalLabel">Import project</span>
                        <button id="close-import-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
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
                        <input v-model="importProjectName" type="text" class="form-control" id="importProjectName" placeholder="New Project Name" autocomplete="off">
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button v-on:click="importProject" id="start-import-btn" type="button" class="btn btn-info" data-dismiss="modal" :disabled="!(isValidZetaProjectFile() && isValidProjectName())">Import</button>
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
          <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip" title="Locked because there are model instances"></div>
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
            <button class="btn dropdown-toggle" type="button" id="btnValidator" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
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
            <a v-for="model in modelInstances" v-bind:key="model.id" href="@routes.ScalaRoutes.getModelEditor(model.id)" class="list-group-item list-item-container">
              {{ model.name }}
              <div v-on:click="deleteModelInstance(model.id)" class="delete-list-item delete-model-instance glyphicon glyphicon-trash" data-toggle="tooltip" title="Delete model instance"></div>
              <div v-on:click="validateModelInstance(model.id)" class="validate-list-item validate-model-instance glyphicon glyphicon-thumbs-up" data-toggle="tooltip" title="Validate model instance against its meta model"></div>
            </a>
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input v-on:keyup.enter="createModelInstance" v-model="inputModelName" type="text" class="form-control" id="inputModelName" placeholder="New model name" autocomplete="off">
                <span class="input-group-btn">
                  <button v-on:click="createModelInstance" type=button id="btnCreateModelInstance" class="btn btn-default" data-toggle="tooltip" title="Create model instace">
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

    <!-- invite modal -->
    <div class="modal fade" id="inviteModal" tabindex="-1" role="dialog" aria-labelledby="inviteModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="inviteModalLabel">Invite to project</span>
            <button id="close-invite-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input v-model="inviteProjectName" type="text" class="form-control" id="inviteProjectName" placeholder="E-Mail Address" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button v-on:click="invite" id="start-invite-btn" type="button" class="btn btn-info" :disabled="!(inviteProjectName.trim().length !== 0)">Invite</button>
          </div>
        </div>
      </div>
    </div>
    <!-- end invite modal -->

    <!-- duplicate project modal -->
    <div class="modal fade" id="duplicateModal" tabindex="-1" role="dialog" aria-labelledby="duplicateModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="duplicateModalLabel">Duplicate project</span>
            <button id="close-duplicate-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input v-model="duplicateProjectName" type="text" class="form-control" id="duplicateProjectName" placeholder="New Project Name" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button v-on:click="duplicate" id="start-duplicate-btn" type="button" class="btn btn-info" :disabled="!(duplicateProjectName.trim().length !== 0)">Duplicate</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="errorMessage" class="col-md-6 col-md-offset-3 alert alert-danger">
      <a href="#" class="close" data-dismiss="alert" v-on:click="errorMessage = ''">&times;</a>
      <strong>Error!</strong>
      <br>
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="col-md-6 col-md-offset-3 alert alert-success">
      <a href="#" class="close" data-dismiss="alert" v-on:click="successMessage = ''">&times;</a>
      <strong>Success!</strong>
      <br>
      {{ successMessage }}
    </div>

  </div>
</template>

<script>
import axios from "axios";
import { EventBus } from "@/eventbus/eventbus"
import jQuery from "jquery"
window.jQuery = jQuery;
window.$ = jQuery;

import modelValidatorUtil from "@/components/zetalayout/overview/modelValidatorUtil";

export default {
  name: 'DiagramsOverview',
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
      inviteProjectName: "",
      duplicateProjectName: "",
      inputModelName: "",
      uploadText: "Drag and Drop .zeta file here...",
      importProjectName: "",
      file: null,
      errorMessage: "",
      successMessage: ""
    }
  },
  methods: {
    loadProjects() {
      axios.get("http://localhost:9000/overview", {withCredentials: true}).then(
          (response) => {
            this.metaModels = response.data.metaModels;
            this.modelInstances = response.data.modelInstances
            console.log(this.metaModels)
          },
          (error) => console.log(error)
      )
    },
    routeParamChanged() {
      if(!this.$route.params.id || this.$route.params.id == "") {
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
            (error) => console.log(error)
        )
      }
    },
    createProject() {
      const name = this.inputProjectName
      if (name === "") return;
      const defaultMetamodelDefinition = require('./defaultMetamodelDefinition.json')
      axios.post(
          "http://localhost:9000/rest/v1/meta-models",
          {name: name},
          {withCredentials: true}
      ).then(
          (response) => {
            EventBus.$emit('metaModelAdded', {id: response.data.id, name: response.data.name});
            axios.put(
                "http://localhost:9000/rest/v1/meta-models/" + response.data.id +"/definition",
                defaultMetamodelDefinition,
                { withCredentials: true}
            ).then(
                (response) => console.log(response),
                (error) => console.log(error)
            )
          },
          (error) => alert("Could not create meta model: " + error)
      )
    },
    deleteProject(metaModelId) {
      axios.delete(
          "http://localhost:9000/rest/v1/meta-models/" + metaModelId,
          {withCredentials: true}
      ).then(
          (response) => EventBus.$emit("metaModelRemoved", metaModelId),
          (error) => alert("Could not delete meta model: " + error)
      )
    },
    invite() {
      const metaModelId = this.selectedProjectId;
      const email = this.inviteProjectName.trim();
      axios.get(
          "http://localhost:9000/rest/v2/invite-to-project/" + metaModelId + "/" + email,
          {withCredentials: true}
      ).then(
          (response) => location.reload(),
          (error) => alert("failed to invite the user to the project, probably there is no user with this email")
      )
    },
    duplicate() {
      const metaModelId = this.selectedProjectId;
      const name = this.duplicateProjectName.trim();
      axios.get(
          "http://localhost:9000/rest/v2/duplicate-project/" + metaModelId + "/" + name,
          {withCredentials: true}
      ).then(
          (response) => this.loadProjects(),
          (error) => alert("failed to duplicate the project")
      )
    },
    exportProject(metaModelId) {
      if (metaModelId) {
        const url = 'http://localhost:9000/rest/v2/models/' + metaModelId + '/exportProject';
        window.open(url, '_blank');
      }
    },
    validatorGenerate() {
      /*modelValidatorUtil.generate(this.$route.params.id, {
        success: function(data, textStatus, jqXHR) {
          $("#success-panel").fadeOut('slow', function () {
            $("#success-panel").show();
            $("#success-panel").find("div").text("Validator successfully generated");
            $("#success-panel").fadeIn('slow');
          });
        },
        error: function (jqXHR, textStatus, errorThrown) {
          this.showError(jqXHR.responseText);
        }
      });*/

      axios.get(
          "http://localhost:9000/rest/v1/meta-models/" + this.$route.params.id + "/validator?generate=true",
          {withCredentials: true}
      ).then(
          (response) => this.successMessage = "Validator successfully generated",
          (error) => this.errorMessage = error
      )
    },
    validatorShow() {
      /*modelValidatorUtil.show(this.$route.params.id, {
        openWindow: true,
        success: function(data, textStatus, jqXHR) {
          switch (data.status) {
            case 200:
              this.showSuccess("Validator successfully generated.");
              break;
            case 201:
              this.showSuccess("Existing validator successfully loaded.");
              break;
          }
        },
        error: function(jqXHR, textStatus, errorThrown) {
          this.showError(jqXHR.responseText);
        }
      });*/

      axios.get(
          "http://localhost:9000/rest/v1/meta-models/" + this.$route.params.id + "/validator?generate=true",
          {withCredentials: true}
      ).then(
          (response) => this.openWindow().document.body.innerHTML = "<pre>" + response.data + "</pre>",
          (error) => console.log(error)
      )
    },
    validateModelInstance(modelId) {
      modelValidatorUtil.validate(modelId, {
        openWindow: true,
        error: function(jqXHR, textStatus, errorThrown) {
          this.showError(jqXHR.responseText);
        }
      });
    },
    createModelInstance() {
      const name = this.inputModelName
      console.log(name)
      if (name === "") {
        return;
      }

      const model = {
        name: name,
        graphicalDslId: this.$route.params.id
      };

      axios.post(
          "http://localhost:9000/rest/v1/models",
          JSON.stringify(model),
          {withCredentials: true}
      ).then(
          (response) => this.$router.push("/zeta/overview/" + this.$route.params.id),
          (error) => alert("failed creating model instance: " + error)
      )
    },
    deleteModelInstance(modelId) {
      axios.delete("http://localhost:9000/rest/v1/models/" + modelId, {withCredentials: true}).then(
          (response) => this.$router.push("/zeta/overview/" + this.$route.params.id),
          (error) => alert("failed deleting model instance: " + error)
      )
    },
    dragover(event) {
      event.preventDefault();
      event.stopPropagation();
      this.uploadText = "Drag here"
    },
    preventDrop(event) {
      event.preventDefault();
    },
    dropFile(event) {
      console.log(event)
      event.preventDefault();
      this.file = event.dataTransfer.files[0];
      this.onProjectSelected();
    },
    onProjectSelected() {
      if (this.isValidZetaProjectFile()) {
        const fd = new FormData();
        fd.append('file', this.file);
        this.uploadText = this.file.name
        const projectNameFromFile = this.file.name.split("_")[0];
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
    },
    openFileManager() {
      console.log("open manager")
      this.$refs.file.click()
    },
    importProject() {
      const fd = new FormData();
      fd.append('file', this.file);
      const projectName = this.importProjectName.trim();
      this.uploadProject(this.file, projectName);
    },
    uploadProject(file, projectName){
      //$("#close-import-modal").click();
      axios.post(
          'http://localhost:9000/rest/v2/projects/import?projectName=' + projectName,
          file,
          {
            withCredentials: true,
            headers: {
              'Content-Type': 'application/zip',
              'processData': false
            }}
      ).then(
          (response) => EventBus.$emit('reloadProjects'),
          (error) => this.showError('Invalid .zeta project file!')
      )
    },
    onFileChange(event) {
      console.log(event)
      this.file = event.target.files[0];
      this.onProjectSelected();
    },
    showError(text) {
      $("#error-panel").fadeOut('slow', function () {
        $("#error-panel").find("div").text(text);
        $("#error-panel").fadeIn('slow');
      });
    },
    showSuccess(text) {
      $("#success-panel").fadeOut('slow', function () {
        $("#success-panel").show();
        $("#success-panel").find("div").text(text);
        $("#success-panel").fadeIn('slow');
      });
    },
    openWindow() {
      var win = window.open('', '', 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=300');
      win.document.body.innerHTML = "waiting for data...";
      return win;
    }
  },
  created() {
    EventBus.$on('metaModelAdded', metamodel => {
      this.metaModels.push(metamodel)
    });
    EventBus.$on('metaModelRemoved', metamodelID => {
      let i = this.metaModels.map(item => item.id).indexOf(metamodelID) // find index of your object
      this.metaModels.splice(i, 1)
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


//Jquery Syntax



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
