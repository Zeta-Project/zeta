<template>
  <v-expansion-panels class="ma-1 full-control" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row>
            <v-col>
              <v-text-field label="Name" v-model="edge.name" @input="name => onAttributeNameChange(edge, name)" />
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="edge.description"/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="edge.sourceDeletionDeletesTarget"
                          :label="'sourceDeletionDeletesTarget'"
                          @change="onSourceDeletionDeletesTargetChange(edge)"/>
              <v-checkbox v-model="edge.targetDeletionDeletesSource"
                          :label="'targetDeletionDeletesSource'"
                          @change="onTargetDeletionDeletesSourceChange(edge)"/>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel>
      <v-expansion-panel-header>Attributes</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-if="edge.attributes"
              v-for="(attribute, index) in edge.attributes"
              :key="`${edge.name}-properties-attributes-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="attribute.name">
                <v-btn slot="append-outer" icon @click="onDeleteAttribute(edge, attribute.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddAttribute(edge)">Add Attribute</v-btn>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel>
      <v-expansion-panel-header>Methods</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-if="edge.methods"
              v-for="(method, index) in edge.methods"
              :key="`${edge.name}-properties-methods-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="method.name">
                <v-btn slot="append-outer" icon @click="onDeleteOperation(edge, method.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddOperation(edge)">Add Method</v-btn>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

  </v-expansion-panels>
</template>

<script>
export default {
  name: 'EdgeProperties',
  data: function () {
    return {}
  },
  watch: {
    edge: function (newVal, oldVal) { // watch it
      if (newVal.name !== oldVal.name) {
        console.log("name changed");
      }
      console.log('Prop changed: ', newVal, ' | was: ', oldVal)
    }
  },
  props: {
    edge: {
      validator: prop => typeof prop === 'object' || prop === null,
      required: true
    }
  },
  methods: {
    onAttributeNameChange(edge, name) {
      this.$emit('on-edge-name-change', edge, name)
    },
    onSourceDeletionDeletesTargetChange(edge) {
      this.$emit('on-edge-style-change', edge);
    },
    onTargetDeletionDeletesSourceChange(edge) {
      this.$emit('on-edge-style-change', edge);
    },
    onDeleteAttribute(edge, name) {
      this.$emit('delete-attribute', edge, name);
    },
    onAddAttribute(edge) {
      this.$emit('add-attribute', edge, 'default');
    },
    onDeleteOperation(edge, name) {
      this.$emit('delete-operation', edge, name);
    },
    onAddOperation(edge) {
      this.$emit('add-operation', edge, 'default');
    }
  }
}
</script>

<style scoped>

</style>
