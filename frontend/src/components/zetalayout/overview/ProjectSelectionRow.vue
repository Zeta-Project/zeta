<template>
  <div>
    <div v-on:click="deleteProject()" class="delete-list-item delete-project glyphicon glyphicon-trash"
         data-toggle="tooltip" title="Delete project"/>
    <div v-on:click="exportProject()" class="delete-list-item export-project glyphicon glyphicon-export"
         data-toggle="tooltip" title="Export project"/>
    <div v-on:click="selectedProjectId = id" class="delete-list-item duplicate-project glyphicon glyphicon-duplicate"
         data-toggle="modal" data-target="#duplicateModal" title="Duplicate project"/>
    <div v-on:click="selectedProjectId = id" class="delete-list-item invite-to-project glyphicon glyphicon-send"
         data-toggle="modal" data-target="#inviteModal" title="Invite other users"/>
    <router-link style="text-decoration: none; color: initial" :to="'/zeta/overview/' + id">
      <div> {{ name }}</div>
    </router-link>

    <!-- invite modal -->
    <div class="modal fade" id="inviteModal" tabindex="-1" role="dialog" aria-labelledby="inviteModalLabel"
         aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="inviteModalLabel">Invite to project</span>
            <button id="close-invite-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input v-model="inviteProjectName" type="text" class="form-control" id="inviteProjectName"
                   placeholder="E-Mail Address" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button v-on:click="invite" id="start-invite-btn" type="button" class="btn btn-info"
                    :disabled="inviteProjectName.trim().length === 0">Invite
            </button>
          </div>
        </div>
      </div>
    </div>
    <!-- end invite modal -->

    <!-- duplicate project modal -->
    <div class="modal fade" id="duplicateModal" tabindex="-1" role="dialog" aria-labelledby="duplicateModalLabel"
         aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="duplicateModalLabel">Duplicate project</span>
            <button id="close-duplicate-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input v-model="duplicateProjectName" type="text" class="form-control" id="duplicateProjectName"
                   placeholder="New Project Name" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button v-on:click="duplicate" id="start-duplicate-btn" type="button" class="btn btn-info"
                    :disabled="duplicateProjectName.trim().length === 0">Duplicate
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import axios from "axios";
import {EventBus} from "@/eventbus/eventbus"

export default {
  name: "ProjectSelectionRow",
  props: {
    id: String,
    name: String
  },
  data() {
    return {
      duplicateProjectName: "",
      inviteProjectName: "",
    }
  },
  methods: {
    deleteProject() {
      axios.delete(
          "http://localhost:9000/rest/v1/meta-models/" + this.id,
          {withCredentials: true}
      ).then(
          (response) => EventBus.$emit("metaModelRemoved", this.id),
          (error) => EventBus.$emit("errorMessage", "Could not delete meta model: " + error)
      )
    },
    exportProject() {
      if (this.id) {
        const url = 'http://localhost:9000/rest/v2/models/' + this.id + '/exportProject';
        window.open(url, '_blank');
      }
    },
    duplicate() {
      const name = this.duplicateProjectName.trim();
      axios.get(
          "http://localhost:9000/rest/v2/duplicate-project/" + this.id + "/" + name,
          {withCredentials: true}
      ).then(
          (response) => EventBus.$emit("reloadProjects", "Duplicate from ", this.id),
          (error) => EventBus.$emit("errorMessage", "Failed to duplicate the project")
      )
    },
    invite() {
      const email = this.inviteProjectName.trim();
      axios.get(
          "http://localhost:9000/rest/v2/invite-to-project/" + this.id + "/" + email,
          {withCredentials: true}
      ).then(
          (response) => location.reload(),
          (error) => EventBus.$emit("errorMessage", "Failed to invite the user to the project," +
              "probably there is no user with this email")
      )
    },
  }
}

</script>