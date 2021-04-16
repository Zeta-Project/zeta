<template>
  <v-expansion-panels class="ma-1" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row>
            <v-col>
              <v-text-field label="Name" v-model="node.name" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="node.description" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="node.abstract" :label="'Is Abstract?'" disabled/>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel v-if="node.attributes && node.attributes.length > 0">
      <v-expansion-panel-header>Attributes</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-for="(attribute, index) in node.attributes"
              :key="`${node.name}-properties-attributes-${index}`">
            <v-col>
              <v-text-field
                  :label="attribute.name"
                  v-model="attribute.value">
              </v-text-field>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel v-if="node.methods && node.methods.length > 0">
      <v-expansion-panel-header>Methods</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-for="(method, index) in node.methods"
              :key="`${node.name}-properties-methods-${index}`">
            <v-col>
              <v-text-field
                  :label="method.name"
                  v-model="method.value">
              </v-text-field>
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
  props: {
    node: {
      validator: prop => typeof prop === 'object' || prop === null,
      required: true
    }
  },
  methods: {}
}
</script>

<style scoped>

</style>
