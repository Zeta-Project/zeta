<template>
  <v-list-item :to="'/zeta/overview/' + id" @click="initStepper()">
    <v-list-item-content>
      <div class="body-1">{{ name }}</div>
    </v-list-item-content>

    <v-list-item-action>
      <v-btn icon @click="toggleInvitationDialog" title="Invite other users">
        <v-icon>mdi-account-group</v-icon>
      </v-btn>
    </v-list-item-action>

    <v-list-item-action>
      <v-btn icon @click="toggleDuplicationDialog" title="Duplicate project">
        <v-icon>mdi-content-duplicate</v-icon>
      </v-btn>
    </v-list-item-action>

    <v-list-item-action>
      <v-btn icon @click="exportProject" title="Export project">
        <v-icon>mdi-export</v-icon>
      </v-btn>
    </v-list-item-action>

    <v-list-item-action class="ml-0">
      <v-btn icon @click="deleteProject" title="Delete project">
        <v-icon>mdi-delete</v-icon>
      </v-btn>
    </v-list-item-action>

    <!-- Invitation dialog -->
    <InviteToProjectDialog
        :show-dialog="showInvitationDialog"
        @cancel="toggleInvitationDialog"
        @invite="invite"
    />

    <!-- Duplication dialog -->
    <DuplicateProjectDialog
        :show-dialog="showDuplicationDialog"
        @cancel="toggleDuplicationDialog"
        @duplicate="duplicate"
    />

  </v-list-item>
</template>
<script>
import ProjectUtils from './ProjectUtils';
import {EventBus} from '@/eventbus/eventbus'
import InviteToProjectDialog from './dialogs/InviteToProjectDialog';
import DuplicateProjectDialog from './dialogs/DuplicateProjectDialog';

export default {
  name: "ProjectSelectionRow",
  components: {InviteToProjectDialog, DuplicateProjectDialog},
  props: {
    id: String,
    name: String,
    isSelected: Boolean
  },
  data() {
    return {
      showInvitationDialog: false,
      showDuplicationDialog: false
    }
  },
  methods: {
    toggleInvitationDialog() {
      this.showInvitationDialog = !this.showInvitationDialog;
    },
    toggleDuplicationDialog(){
      this.showDuplicationDialog = !this.showDuplicationDialog;
    },
    initStepper() {
      EventBus.$emit("initSteps", 1);
    },
    deleteProject() {
      ProjectUtils.deleteProject(this.id);
    },
    exportProject() {
      ProjectUtils.exportProject(this.id);
    },
    duplicate(projectName) {
      ProjectUtils.duplicateProject(this.id, projectName);
      this.toggleDuplicationDialog();
    },
    invite(email) {
      ProjectUtils.inviteToProject(this.id, email);
      this.toggleInvitationDialog();
    }
  }
}

</script>
<style scoped>
a {
  text-decoration: none !important;
}
</style>