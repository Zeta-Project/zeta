<template>
  <div>
    <v-card>
      <v-card-title>
        <span class="headline">Projects</span>
      </v-card-title>

      <v-divider class="ma-0"/>

      <v-card-text v-if="!metaModels || metaModels.length === 0">
        There are no projects.
      </v-card-text>

      <v-list v-else>
        <v-list-item-group>
          <v-list-item v-for="(metamodel, i) in metaModels" :key="i">
            <ProjectSelectionRow
                v-bind:id="metamodel.id"
                v-bind:name="metamodel.name"
                v-bind:is-selected="
                gdslProject && metamodel.id === gdslProject.id
              "
            />
          </v-list-item>
        </v-list-item-group>
      </v-list>

      <v-divider class="ma-0"/>

      <v-card-actions>
        <v-text-field
            id="inputProjectName"
            v-model="inputProjectName"
            :append-icon="'mdi-plus-box'"
            :append-outer-icon="'mdi-import'"
            hide-details
            outlined
            clearable
            label="New project name"
            type="text"
            @click:append="createProject"
            @click:append-outer="toggleDialog"
            v-on:keyup.enter="createProject"
        />
      </v-card-actions>
    </v-card>

    <!-- import dialog -->
    <v-dialog v-model="showDialog" max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Import Project</span>
          <v-spacer/>
          <v-btn
              icon
              @click.stop="toggleDialog">
            <v-icon>
              mdi-close
            </v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form ref="form">
            <!-- drop area -->
            <v-file-input
                id="file"
                class="d-none"
                name="file"
                v-on:change="onFileChange"
                accept=".zeta"
                ref="file"/>
            <!-- Drag and Drop container-->
            <div
                id="uploadfile"
                v-on:click="openFileManager"
                class="upload-area"
                @drop.prevent="dropFile"
                @dragover.prevent>
              <span class="headline">{{ uploadText }}</span>
            </div>
            <v-text-field
                id="importProjectName"
                class="mt-5"
                v-model="importProjectName"
                label="Project Name"
                placeholder="New Project Name"
                autocomplete="off"
                required
                outlined
                hide-details
                clearable
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer/>
          <v-btn @click.stop="toggleDialog">Cancel</v-btn>
          <v-btn color="primary" @click.stop="importProject"
                 :disabled="!(isValidZetaProjectFile() && isValidProjectName())">
            Import
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>
<script>
import ProjectSelectionRow from "./ProjectSelectionRow";
import ProjectUtils from "./ProjectUtils";

export default {
  name: "ProjectSelection",
  components: {ProjectSelectionRow},
  props: {
    metaModels: Array,
    gdslProject: Object,
  },
  data() {
    return {
      inputProjectName: "",
      importProjectName: "",
      file: null,
      invalidImportFile: false,
      showDialog: false,
    };
  },
  computed: {
    uploadText: function () {
      return this.file ?
          this.file.name :
          this.invalidImportFile ?
              "Invalid zeta project file!" :
              "Drag and Drop .zeta file here...";
    }
  },
  methods: {
    toggleDialog() {
      if (this.showDialog) {
        // Reset inputs on close
        this.$refs.form.reset();  // Does not reset file
        this.$refs.file.reset();
      }

      this.showDialog = !this.showDialog;
    },
    createProject() {
      ProjectUtils.createProject(this.inputProjectName);
      this.inputProjectName = "";
    },
    onFileChange(file) {
      this.file = file;
      if (this.file)
        this.onProjectSelected();
    },
    onProjectSelected() {
      if (this.isValidZetaProjectFile()) {
        // this.uploadText = this.file.name;
        this.importProjectName = this.file.name.split(".zeta")[0].split("_")[0];
      } else {
        this.file = null;
        // this.uploadText = "Invalid zeta project file!";
      }
    },
    dropFile(event) {
      this.file = event.dataTransfer.files[0];
      this.onProjectSelected();
    },
    openFileManager() {
      this.$refs.file.$refs.input.click();
    },
    importProject() {
      ProjectUtils.importProject(this.file, this.importProjectName);
      this.toggleDialog();
    },
    isValidZetaProjectFile() {
      return this.file && this.file.name.endsWith(".zeta");
    },
    isValidProjectName() {
      return this.importProjectName && this.importProjectName.trim() !== "";
    },
  },
};
</script>