<template>
  <v-expansion-panels class="ma-1 full-control" multiple>

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
                  :label="attribute.name"
                  v-model="attribute.value">
              </v-text-field>
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
