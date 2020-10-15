<template>
  <div >
    <div id="model-instance-container" class="panel panel-default">
      <div class="panel-heading">
        <strong>Model Instances</strong>
      </div>

      <div class="panel-body" v-if="!modelInstances.length">
        <span class="text-muted">There are no model instances.</span>
      </div>

      <div class="list-group" v-else>
        <a v-for="model in modelInstances" v-bind:key="model.id" href="@routes.ScalaRoutes.getModelEditor(model.id)"
           class="list-group-item list-item-container">
          {{ model.name }}
          <div v-on:click="deleteModelInstance"
               class="delete-list-item delete-model-instance glyphicon glyphicon-trash" data-toggle="tooltip"
               title="Delete model instance"></div>
          <div v-on:click="validateModelInstance"
               class="validate-list-item validate-model-instance glyphicon glyphicon-thumbs-up"
               data-toggle="tooltip" title="Validate model instance against its meta model"></div>
        </a>
      </div>

      <div class="panel-footer">
        <form>
          <div class="input-group">
            <input v-on:keyup.enter="createModelInstance" v-model="inputModelName" type="text" class="form-control"
                   id="inputModelName" placeholder="New model name" autocomplete="off">
            <span class="input-group-btn">
                  <button v-on:click="createModelInstance" type=button id="btnCreateModelInstance"
                          class="btn btn-default" data-toggle="tooltip" title="Create model instace">
                    <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                  </button>
                </span>
          </div>
        </form>
      </div>
    </div>
  </div>
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
      ModelInstanceUtils.deleteInstance(modelId, this.$route.params.id)
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