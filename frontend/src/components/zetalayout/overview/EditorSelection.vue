<template>
  <v-card>
    <v-overlay v-if="hasModelInstanceForProject(modelInstances)" :absolute="true" :opacity="0.85">
      <v-icon x-large>mdi-folder-lock</v-icon>
      <div>
        Locked because there are model instances
      </div>
    </v-overlay>

    <v-card-title>
      <span class="headline">Edit project {{ gdslProject.name }}</span>
    </v-card-title>

    <v-divider class="ma-0"></v-divider>

    <v-card-text>
      <EditorStepper :gdsl-project="gdslProject" :model-instances="modelInstances"/>
    </v-card-text>

    <v-divider class="ma-0"></v-divider>

    <v-card-actions>
      <v-menu offset-y>
        <template v-slot:activator="{ on }">
          <v-btn color="secondary" outlined v-on="on">
            Validator
            <v-icon class="ml-1">mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item v-on:click="validatorGenerate" id="validatorGenerate">
            <v-list-item-title>Generate / Update Validator</v-list-item-title>
          </v-list-item>
          <v-list-item v-on:click="validatorShow" id="validatorShow">
            <v-list-item-title>Show Validation Rules</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-card-actions>
  </v-card>
</template>
<script>

import EditorStepper from "@/components/zetalayout/metamodel/EditorStepper";
import ValidatorUtils from "@/components/zetalayout/overview/ValidatorUtils";

export default {
  name: 'EditorSelection',
  components: {EditorStepper},
  props: {
    gdslProject: {},
    modelInstances: {}
  },
  methods: {
    hasModelInstanceForProject(modelInstances) {
      const projectId = this.gdslProject.id;
      let result = false;
      modelInstances.forEach(modelInstance => {
        if (modelInstance.graphicalDslId === projectId) {
          result = true;
        }
      })
      return result;
    },
    validatorGenerate() {
      ValidatorUtils.generate(this.$route.params.id)
    },
    validatorShow() {
      ValidatorUtils.show(this.$route.params.id)
    },
  }
}

</script>

<style scoped>
ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>