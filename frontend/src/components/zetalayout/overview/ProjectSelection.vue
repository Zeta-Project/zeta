<template>
  <div>
    <div class="panel panel-default">
      <div class="panel-heading">
        <strong>Projects</strong>
      </div>

      <div class="list-group" v-if="metaModels.length">

        <div v-for="metamodel in metaModels" v-bind:key="metamodel.id">
<!--          <ProjectSelectionRow-->
<!--              v-bind:id="metamodel.id"-->
<!--              v-bind:name="metamodel.name"-->
<!--              v-bind:is-selected="gdslProject && metamodel.id === gdslProject.id"-->
<!--          />-->
          <ProjectSelectionRow
                        v-bind:id="metamodel.id"
                        v-bind:name="metamodel.name"
                        v-bind:is-selected="gdslProject && metamodel.id === gdslProject.id" />
<!--        </div>-->
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
</template>
<script>
import ProjectSelectionRow from "./ProjectSelectionRow"
import ProjectUtils from "./ProjectUtils";

export default {
  name: "ProjectSelection",
  components: {ProjectSelectionRow},
  props: {
    metaModels: Array,
    gdslProject: Object
  },
  data() {
    return {
      inputProjectName: "",
      uploadText: "Drag and Drop .zeta file here...",
      importProjectName: "",
      file: null
    }
  },
  methods: {
    createProject() {
      ProjectUtils.createProject(this.inputProjectName)
    },
    onFileChange(event) {
      this.file = event.target.files[0];
      this.onProjectSelected();
    },
    onProjectSelected() {
      if (this.isValidZetaProjectFile()) {
        this.uploadText = this.file.name
        this.importProjectName = this.file.name.split(".zeta")[0].split("_")[0];
      } else {
        this.uploadText = "Invalid zeta project file!"
      }
    },
    dropFile(event) {
      event.preventDefault();
      this.file = event.dataTransfer.files[0];
      this.onProjectSelected();
    },
    openFileManager() {
      this.$refs.file.click()
    },
    importProject() {
      ProjectUtils.importProject(this.file, this.importProjectName)
    },
    isValidZetaProjectFile() {
      return this.file && this.file.name.endsWith(".zeta");
    },
    isValidProjectName() {
      return this.importProjectName.trim() !== "";
    }
  }
}
</script>