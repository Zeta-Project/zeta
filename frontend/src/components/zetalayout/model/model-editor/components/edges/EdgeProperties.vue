<template>
  <v-expansion-panels class="ma-1 full-control" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-if="edge.labels"
              v-for="(label, index) in edge.labels"
              :key="`${edge.name}-labels-${index}`">
            <v-col>
              <v-text-field label="Name" :value="label.text" @input="name => onAttributeNameChange(edge, name)"/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="edge.style.model.description"/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="edge.style.model.sourceDeletionDeletesTarget"
                          :label="'sourceDeletionDeletesTarget'"
                          @change="onSourceDeletionDeletesTargetChange(edge)"/>
              <v-checkbox v-model="edge.style.model.targetDeletionDeletesSource"
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
              v-if="edge.style.model.attributes"
              v-for="(attribute, index) in edge.style.model.attributes"
              :key="`${edge.style.model.name}-properties-attributes-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="attribute.name">
                <v-btn slot="append-outer" icon @click="onDeleteAttribute(edge.style.model, attribute.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddAttribute(edge.style.model)">Add Attribute</v-btn>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel>
      <v-expansion-panel-header>Operations</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-if="edge.style.model.operations"
              v-for="(operation, index) in edge.style.model.operations"
              :key="`${edge.style.model.name}-properties-operations-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="operation.name">
                <v-btn slot="append-outer" icon @click="onDeleteOperation(edge.style.model, operation.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddOperation(edge.style.model)">Add Methods</v-btn>
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
    onDeleteAttribute(model, name) {
      this.$emit('delete-attribute', model, name);
    },
    onAddAttribute(model) {
      this.$emit('add-attribute', model, 'default');
    },
    onDeleteOperation(model, name) {
      this.$emit('delete-operation', model, name);
    },
    onAddOperation(model) {
      this.$emit('add-operation', model, 'default');
    }
  }
}
</script>

<style scoped>

</style>
