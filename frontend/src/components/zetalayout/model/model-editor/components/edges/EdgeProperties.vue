<template>
  <v-expansion-panels class="ma-1" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row>
            <v-col>
              <p class="font-italic">
                Meta-Information can only be changed in Concept-Editor.
              </p>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Name" v-model="edge.style.model.name" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="edge.style.model.description" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="edge.style.model.sourceDeletionDeletesTarget"
                          :label="'Source deletion deletes target'" disabled/>
              <v-checkbox v-model="edge.style.model.targetDeletionDeletesSource"
                          :label="'Target deletion deletes source'" disabled/>
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
                  :label="attribute.name"
                  v-model="attribute.value">
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
  methods: {}
}
</script>

<style scoped>

</style>
