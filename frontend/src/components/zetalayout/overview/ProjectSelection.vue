<template>
  <div>
    <v-card>
      <v-card-title>
        Projects
      </v-card-title>

      <v-card-text v-if="!metaModels || metaModels.length === 0">
        There are no projects.
      </v-card-text>

      <v-list v-else>
        <v-list-item-group>
          <v-list-item
              v-for="(metamodel, i) in metaModels"
              :key="i">

            <ProjectSelectionRow
                v-bind:id="metamodel.id"
                v-bind:name="metamodel.name"
                v-bind:is-selected="gdslProject && metamodel.id === gdslProject.id"/>

          </v-list-item>
        </v-list-item-group>
      </v-list>

      <div class="ma-2">
        <v-text-field
            id="inputProjectName"
            v-model="inputProjectName"
            :append-icon="'mdi-plus-box'"
            :append-outer-icon="'mdi-import'"
            outlined
            clearable
            label="New project name"
            type="text"
            @click:append="createProject"
            @click:append-outer="toggleDialog"
            v-on:keyup.enter="createProject"/>
      </div>
    </v-card>

    <v-dialog v-model="showDialog" max-width="500px">
      <v-card>
        <v-card-actions>
          <v-btn color="primary" @click.stop="toggleDialog">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
      file: null,
      showDialog: false
    }
  },
  methods: {
    toggleDialog() {
      this.showDialog = !this.showDialog;
    },
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