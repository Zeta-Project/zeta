<template>
  <v-expansion-panels class="ma-1" multiple>

    <v-expansion-panel>
      <v-expansion-panel-header>Meta-Information</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row>
            <v-col>
              <v-text-field label="Name" v-model="edge.name" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-text-field label="Description" v-model="edge.description" disabled/>
            </v-col>
          </v-row>

          <v-row>
            <v-col>
              <v-checkbox v-model="edge.sourceDeletionDeletesTarget"
                          :label="'Source deletion deletes target'" disabled/>
              <v-checkbox v-model="edge.targetDeletionDeletesSource"
                          :label="'Target deletion deletes source'" disabled/>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel v-if="edge.attributes && edge.attributes.length > 0">
      <v-expansion-panel-header>Attributes</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-for="(attribute, index) in edge.attributes"
              :key="`${edge.name}-properties-attributes-${index}`">
            <v-col>
              <v-text-field
                  :label="attribute.name"
                  v-model="attribute.value"
                  @input="value => onAttributeValueChange(attribute.name, value)">
              </v-text-field>
            </v-col>
          </v-row>

        </v-container>
      </v-expansion-panel-content>
    </v-expansion-panel>

    <v-expansion-panel v-if="edge.methods && edge.methods.length > 0">
      <v-expansion-panel-header>Methods</v-expansion-panel-header>

      <v-expansion-panel-content>
        <v-container>

          <v-row
              v-for="(method, index) in edge.methods"
              :key="`${edge.name}-properties-methods-${index}`">
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
    onAttributeValueChange(attributeName, value) {
      // Check if attribute is bound to an edge label
      if(this.edge.labels.find(l => l.geoElement.identifier === attributeName)){
        this.$emit('on-edge-label-change', this.edge, attributeName, value);
      }
    }
  }
}
</script>

<style scoped>

</style>
