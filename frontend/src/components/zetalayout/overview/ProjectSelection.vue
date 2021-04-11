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
                @delete-project="onDeleteProject"
                @export-project="onExportProject"
                @duplicate-project="onDuplicateProject"
                @invite-to-project="onInviteToProject"
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
    <ImportProjectDialog
        :show-dialog="showDialog"
        @cancel="toggleDialog"
        @import="importProject" />
  </div>
</template>
<script>

import ProjectSelectionRow from "./ProjectSelectionRow";
import ImportProjectDialog from "./dialogs/ImportProjectDialog";
import ProjectUtils from "./ProjectUtils";

export default {
  name: "ProjectSelection",
  components: {ProjectSelectionRow, ImportProjectDialog},
  props: {
    metaModels: Array,
    gdslProject: Object,
  },
  data() {
    return {
      inputProjectName: "",
      showDialog: false,
    };
  },
  methods: {
    toggleDialog() {
      this.showDialog = !this.showDialog;
    },
    createProject() {
      ProjectUtils.createProject(this.inputProjectName);
      this.inputProjectName = "";
    },
    importProject(file, projectName) {
      ProjectUtils.importProject(file, projectName);
      this.toggleDialog();
    },
    onDeleteProject(id) {
      ProjectUtils.deleteProject(id);
    },
    onExportProject(id) {
      ProjectUtils.exportProject(id);
    },
    onDuplicateProject(id, projectName) {
      ProjectUtils.duplicateProject(id, projectName);
    },
    onInviteToProject(id, email) {
      ProjectUtils.inviteToProject(id, email);
    }
  }
};
</script>