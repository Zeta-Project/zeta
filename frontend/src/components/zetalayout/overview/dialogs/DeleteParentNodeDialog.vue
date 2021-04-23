<template>
  <v-dialog v-model="showDialog" max-width="500px">
    <v-card>

      <v-card-title>
        <span class="headline">Delete group node</span>
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
        Do you really want to delete this group node?
        If you do so, all child nodes within this group node will be deleted as well.
      </v-card-text>

      <v-card-actions>
        <v-spacer/>
        <v-btn @click.stop="onCancel">Cancel</v-btn>
        <v-btn color="primary" @click.stop="onDelete">
          Delete
        </v-btn>
      </v-card-actions>

    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "DeleteParentNodeDialog",
  props: {
    showDialog: Boolean
  },
  data() {
    return { }
  },
  methods: {
    onCancel(){
      this.$emit('cancel');
    },
    onDelete(){
      this.$parent.deleteGroupNode();
    }
  }
}
</script>

<style scoped>

</style>