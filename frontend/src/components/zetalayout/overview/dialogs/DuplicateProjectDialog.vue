<template>
  <v-dialog v-model="showDialog" max-width="500px">
    <v-card>

      <v-card-title>
        <span class="headline">Duplicate project</span>
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
        <v-form ref="duplicateForm">
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
        <v-btn color="primary"
               @click.stop="onDuplicate()"
               :disabled="projectName && projectName.trim().length === 0">
          Duplicate
        </v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "DuplicateProjectDialog",
  props: {
    showDialog: Boolean
  },
  data() {
    return {
      projectName: ""
    }
  },
  methods: {
    onCancel(){
      this.$emit('cancel');
      this.cleanUp();
    },
    onDuplicate(){
      this.$emit('duplicate', this.projectName);
      this.cleanUp();
    },
    cleanUp() {
      this.projectName = "";
    }
  }
}
</script>

<style scoped>

</style>