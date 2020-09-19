<template>
  <div id="edit-project" >
    <div class="panel panel-default overlay-container">
      <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip"
           title="Locked because there are model instances"></div>
      <div class="panel-heading">
        <strong>Edit project <em>{{ gdslProject.name }}</em></strong>
      </div>
      <div class="list-group">
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/style'">
          Style
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/shape'">
          Shape
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/codeEditor/editor/' + gdslProject.id + '/diagram'">
          Diagram
        </router-link>
        <router-link class="list-group-item" :to="'/zeta/metamodel/editor/' + gdslProject.id">
          Concept Editor
        </router-link>
      </div>
      <div class="panel-footer dropdown">
        <button class="btn dropdown-toggle" type="button" id="btnValidator" data-toggle="dropdown"
                aria-haspopup="true" aria-expanded="true">
          Validator
          <span class="caret"></span>
        </button>
        <ul class="dropdown-menu" aria-labelledby="btnValidator">
          <li><a v-on:click="validatorGenerate" id="validatorGenerate">Generate / Update Validator</a></li>
          <li><a v-on:click="validatorShow" id="validatorShow">Show Validation Rules</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>
<script>
import ValidatorUtils from "./ValidatorUtils";

export default {
  name: 'EditorSelection',
  props: {
    gdslProject: {},
    modelInstances: {},
  },
  methods: {
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