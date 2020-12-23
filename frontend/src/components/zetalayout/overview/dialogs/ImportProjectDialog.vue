<template>
  <v-dialog v-model="showDialog" max-width="500px">
    <v-card>
      <v-card-title>
        <span class="headline">Import Project</span>
        <v-spacer/>
        <v-btn
            icon
            @click.stop="onCancel">
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
              v-on:click="openFileManager"
              class="upload-area"
              @drop.prevent="dropFile"
              @dragover.prevent>
            <span class="headline">{{ uploadText }}</span>
          </div>
          <v-text-field
              class="mt-5"
              v-model="projectName"
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
        <v-btn @click.stop="onCancel">Cancel</v-btn>
        <v-btn color="primary" @click.stop="onImport"
               :disabled="!(isValidZetaProjectFile() && isValidProjectName())">
          Import
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "ImportProjectDialog",
  props: {
    showDialog: Boolean
  },
  data() {
    return {
      projectName: "",
      invalidImportFile: false,
      file: null
    }
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
    onCancel() {
      this.$emit('cancel');
      this.cleanUp();
    },
    onImport() {
      this.$emit('import', this.file, this.projectName)
      this.cleanUp();
    },
    openFileManager() {
      this.$refs.file.$refs.input.click();
    },
    dropFile(event) {
      this.file = event.dataTransfer.files[0];
      this.onProjectSelected();
    },
    onFileChange(file) {
      this.file = file;
      if (this.file)
        this.onProjectSelected();
    },
    onProjectSelected() {
      if (this.isValidZetaProjectFile()) {
        this.projectName = this.file.name.split(".zeta")[0].split("_")[0];
      } else {
        this.file = null;
      }
    },
    isValidZetaProjectFile() {
      return this.file && this.file.name.endsWith(".zeta");
    },
    isValidProjectName() {
      return this.projectName && this.projectName.trim() !== "";
    },
    cleanUp() {
      this.projectName = "";
      this.file = null;
    }
  }
}
</script>

<style scoped>
.upload-area {
  height: 180px;
  border: 1px dashed #ccc;
  border-radius: 3px;
  text-align: center;
  overflow: auto;
  padding: 20px 0 0 0;
  color: darkslategray;
  cursor: pointer;
}
</style>