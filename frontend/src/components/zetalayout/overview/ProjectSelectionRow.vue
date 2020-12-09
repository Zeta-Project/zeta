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
      <v-btn icon @click="toggleDuplicateDialog" title="Duplicate project">
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

    <!-- Invite dialog -->
    <v-dialog v-model="showInviteDialog" max-width="500px">
      <v-card>

        <v-card-title>
          <span class="headline">Invite to project</span>
          <v-spacer/>
          <v-btn
              icon
              @click.stop="toggleInvitationDialog">
            <v-icon>
              mdi-close
            </v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form ref="inviteForm">
            <v-text-field
                class="mt-5"
                v-model="inviteProjectName"
                label="E-Mail"
                placeholder="E-Mail Address"
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
          <v-btn @click.stop="toggleInvitationDialog">Cancel</v-btn>
          <v-btn color="primary" @click.stop="toggleInvitationDialog();invite()"
                 :disabled="inviteProjectName && inviteProjectName.trim().length === 0">
            Invite
          </v-btn>
        </v-card-actions>

      </v-card>
    </v-dialog>

    <!-- Duplicate dialog -->
    <v-dialog v-model="showDuplicateDialog" max-width="500px">
      <v-card>

        <v-card-title>
          <span class="headline">Duplicate project</span>
          <v-spacer/>
          <v-btn
              icon
              @click.stop="toggleDuplicateDialog">
            <v-icon>
              mdi-close
            </v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form ref="duplicateForm">
            <v-text-field
                class="mt-5"
                v-model="duplicateProjectName"
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
          <v-btn @click.stop="toggleDuplicateDialog">Cancel</v-btn>
          <v-btn color="primary"
                 @click.stop="duplicate();toggleDuplicateDialog()"
                 :disabled="duplicateProjectName && duplicateProjectName.trim().length === 0">
            Duplicate
          </v-btn>
        </v-card-actions>

      </v-card>
    </v-dialog>

  </v-list-item>
</template>
<script>
import ProjectUtils from "./ProjectUtils";
import {EventBus} from '../../../eventbus/eventbus'

export default {
  name: "ProjectSelectionRow",
  props: {
    id: String,
    name: String,
    isSelected: Boolean
  },
  data() {
    return {
      duplicateProjectName: "",
      inviteProjectName: "",
      showInviteDialog: false,
      showDuplicateDialog: false
    }
  },
  methods: {
    toggleInvitationDialog() {
      if(this.showInviteDialog)
        this.$refs.inviteForm.reset();  // Reset form when closing

      this.showInviteDialog = !this.showInviteDialog;
    },
    toggleDuplicateDialog(){
      if(this.showDuplicateDialog)
        this.$refs.duplicateForm.reset(); // Reset form when closing

      this.showDuplicateDialog = !this.showDuplicateDialog;
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
    duplicate() {
      ProjectUtils.duplicateProject(this.id, this.duplicateProjectName);
    },
    invite() {
      ProjectUtils.inviteToProject(this.id, this.inviteProjectName);
    },
  }
}

</script>
<style scoped>
.list-group-item.active, .list-group-item.active:hover, .list-group-item.active:focus {
  z-index: auto;
}

a {
  text-decoration: none !important;
}
</style>