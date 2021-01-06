<template>
  <v-expansion-panels class="ma-1 full-control" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row>
            <v-col>
              <v-text-field label="Name" v-model="node.name"/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="node.description"/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="node.abstractness" :label="'Is Abstract?'"/>
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
              v-if="node.attributes"
              v-for="(attribute, index) in node.attributes"
              :key="`${node.name}-properties-attributes-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="attribute.name">
                <v-btn slot="append-outer" icon @click="onDeleteAttribute(node, attribute.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddAttribute(node)">Add Attribute</v-btn>
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
              v-if="node.methods"
              v-for="(method, index) in node.methods"
              :key="`${node.name}-properties-operations-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="method.name">
                <v-btn slot="append-outer" icon @click="onDeleteOperation(node, method.name)">
                  <v-icon color="red">mdi-trash-can-outline</v-icon>
                </v-btn>
              </v-text-field>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-btn color="primary" @click="onAddOperation(node)">Add Operation</v-btn>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

  </v-expansion-panels>
</template>

<script>
export default {
  name: 'NodeProperties',
  data: function () {
    return {}
  },
  watch: {
    node: function (newVal, oldVal) { // watch it
      //console.log('Prop changed: ', newVal, ' | was: ', oldVal)
    }
  },
  props: {
    node: {
      validator: prop => typeof prop === 'object' || prop === null,
      required: true
    }
  },
  methods: {
    onAddAttribute(node) {
      this.$emit('add-attribute', node, 'default')
    },
    onDeleteAttribute(node, name) {
      this.$emit('delete-attribute', node, name)
    },
    onAddOperation(node) {
      this.$emit('add-operation', node, 'default')
    },
    onDeleteOperation(node, name) {
      this.$emit('delete-operation', node, name)
    }
  }
}
</script>

<style scoped>
    .full-control > .md-list {
        width: 100%;
        max-width: 100%;
        height: 400px;
        display: inline-block;
        overflow: auto;
        border: 1px solid;
        vertical-align: top;
    }
</style>
