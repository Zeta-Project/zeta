<template>
  <v-app id="model-selection">
    <v-card>
      <v-card-title class="font-weight-bold headline">
        Model Instances
      </v-card-title>

      <v-divider class="ma-0"></v-divider>

      <v-card-text v-if="!modelInstances.length" class="body-1">
        There are no model instances.
      </v-card-text>

      <div v-else>
        <v-list-item v-for="model in modelInstances" v-bind:key="model.id">
          <v-list-item-content>
            <router-link style="text-decoration: none; color: initial" :to="'/zeta/metamodel/editor/' + model.id">
              <v-list-item-title v-text="model.name"></v-list-item-title>
            </router-link>
          </v-list-item-content>

          <v-list-item-action>
            <v-btn icon @click="deleteModelInstance(model.id)">
              <v-icon color="black">mdi-delete</v-icon>
            </v-btn>
          </v-list-item-action>

          <v-list-item-action>
            <v-btn icon @click="validateModelInstance(model.id)">
              <v-icon color="black">mdi-thumb-up</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </div>

      <v-divider class="ma-0"></v-divider>

      <div class="ma-2">
        <v-form>
          <v-text-field
              id="inputModelName"
              v-model="inputModelName"
              :append-outer-icon="inputModelName ? 'mdi-note-plus' : ''"
              outlined
              clearable
              label="New model name"
              type="text"
              @click:append-outer="createModelInstance"
              v-on:keyup.enter="createModelInstance"
          ></v-text-field>
        </v-form>
      </div>
    </v-card>
  </v-app>
</template>
<script>
import ModelInstanceUtils from "./ModelInstanceUtils";
import ValidatorUtils from "./ValidatorUtils";

export default {
  name: 'ModelSelection',
  props: {
    gdslProject: {},
    modelInstances: {},
  },
  data() {
    return {
      inputModelName: ""
    }
  },
  methods: {
    createModelInstance() {
      ModelInstanceUtils.createInstance(this.inputModelName, this.$route.params.id)
    },
    deleteModelInstance(modelId) {
      ModelInstanceUtils.deleteInstance(modelId)
    },
    validateModelInstance(modelId) {
      ValidatorUtils.validate(modelId)
    },
  }
}
</script>
<style scoped>

a {
  color: #42b983;
}
</style>
