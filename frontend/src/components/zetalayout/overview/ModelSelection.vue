<template>
  <v-card>
    <v-card-title>
      Model Instances
    </v-card-title>

    <v-divider class="ma-0"></v-divider>

    <v-card-text>
      <div v-if="!modelInstances.length" class="body-1">
        There are no model instances.
      </div>

      <div v-else>
        <v-list-item v-for="model in modelInstances" v-bind:key="model.id">
          <v-list-item-content>
            <router-link style="text-decoration: none; color: initial" :to="'/zeta/metamodel/editor/' + model.id">
              <v-list-item-title v-text="model.name"></v-list-item-title>
            </router-link>
          </v-list-item-content>

          <v-list-item-action>
            <v-btn icon @click="deleteModelInstance(model.id)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-list-item-action>

          <v-list-item-action>
            <v-btn icon @click="validateModelInstance(model.id)">
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </div>
    </v-card-text>

    <v-divider class="ma-0"></v-divider>

    <v-card-actions>
        <v-text-field
            id="inputModelName"
            v-model="inputModelName"
            :append-outer-icon="'mdi-plus-box'"
            outlined
            clearable
            hide-details
            label="New model name"
            type="text"
            @click:append-outer="createModelInstance"
            v-on:keyup.enter="createModelInstance"
        ></v-text-field>
    </v-card-actions>
  </v-card>
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
