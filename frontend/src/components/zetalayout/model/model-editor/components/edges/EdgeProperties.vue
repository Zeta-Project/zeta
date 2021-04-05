<template>
  <v-expansion-panels class="ma-1" multiple>

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
                  label="attribute.name"
                  v-model="attribute.value">
              </v-text-field>
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
              v-if="edge.style.model.operations"
              v-for="(operation, index) in edge.style.model.operations"
              :key="`${edge.style.model.name}-properties-operations-${index}`">
            <v-col>
              <v-text-field
                  label="Name"
                  v-model="operation.name">
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
  name: 'EdgeProperties',
  data: function () {
    return {}
  },
  props: {
    edge: {
      validator: prop => typeof prop === 'object' || prop === null,
      required: true
    }
  },
  methods: {

  }
}
</script>

<style scoped>

</style>
