<template>
  <v-dialog v-model="showDialog" max-width="500px">
    <v-card>

      <v-card-title>
        <span class="headline">Invite to project</span>
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
        <v-form ref="inviteForm">
          <v-text-field
              class="mt-5"
              v-model="inviteeEmail"
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
        <v-btn @click.stop="onCancel">Cancel</v-btn>
        <v-btn color="primary" @click.stop="onInvite"
               :disabled="inviteeEmail && inviteeEmail.trim().length === 0">
          Invite
        </v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "InviteToProjectDialog",
  props: {
    showDialog: Boolean
  },
  data() {
    return {
      inviteeEmail: ""
    }
  },
  methods: {
    onCancel(){
      this.$emit('cancel');
      this.cleanUp();
    },
    onInvite(){
      this.$emit('invite', this.inviteeEmail);
      this.cleanUp();
    },
    cleanUp() {
      this.inviteeEmail = "";
    }
  }
}
</script>

<style scoped>

</style>