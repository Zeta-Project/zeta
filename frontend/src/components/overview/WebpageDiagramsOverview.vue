<template>
  <div>
    <div class="row">
      <div class="col-md-4">
        <div class="panel panel-default">
          <div class="panel-heading">
            <strong>Projects</strong>
          </div>

          <div class="list-group" v-if="metaModels.length">

            <div v-for="metamodel in metaModels" v-bind:key="metamodel.id"
                 class="list-group-item list-item-container"
                 v-bind:class="{active: gdslProject && metamodel.id == gdslProject.id}">

              <div data-metamodel-id="@metamodel.id" class="delete-list-item delete-project glyphicon glyphicon-trash" data-toggle="tooltip" title="Delete project"></div>
              <div data-metamodel-id="@metamodel.id" class="delete-list-item export-project glyphicon glyphicon-export" data-toggle="tooltip" title="Export project"></div>
              <div data-metamodel-id="@metamodel.id" class="delete-list-item duplicate-project glyphicon glyphicon-duplicate" data-toggle="tooltip" data-target="#importModal" title="Duplicate project"></div>
              <div data-metamodel-id="@metamodel.id" class="delete-list-item invite-to-project glyphicon glyphicon-send" data-toggle="tooltip" title="Invite other users"></div>
              <a style="text-decoration: none; color: initial" :href="'/overview/' + metamodel.id"><div> {{metamodel.name}}</div></a>
            </div>

          </div>

          <div class="panel-body" v-else>
            There are no projects.
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input type="text" class="form-control" id="inputProjectName" placeholder="New project name" autocomplete="off">
                <span class="input-group-btn">
                <button type=button id="btnCreateMetaModel" class="btn btn-default" data-toggle="#tooltip" title="Create project">
                  <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                </button>
                  <!-- launch import modal -->
                <button type="button" class="btn btn-default" data-toggle="modal" data-target="#importModal" title="Import project">
                  <span class="glyphicon glyphicon-import" aria-hidden=true></span>
                </button>
              </span>
              </div>
              <div>


                <!-- import modal -->
                <div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="importModalLabel" aria-hidden="true">
                  <div class="modal-dialog" role="document">
                    <div class="modal-content">
                      <div class="modal-header modal-header-info">
                        <span class="modal-title" id="importModalLabel">Import project</span>
                        <button id="close-import-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                        </button>
                      </div>
                      <div class="modal-body">
                        <div class="form-group">
                          <!-- drop area -->
                          <input type="file" accept=".zeta" name="file" id="file">
                          <!-- Drag and Drop container-->
                          <div class="upload-area" id="uploadfile">
                            <span id="uploadtext">Drag and Drop <b>.zeta</b> file here...</span>
                          </div>
                        </div>
                        <input type="text" class="form-control" id="importProjectName" placeholder="New Project Name" autocomplete="off">
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                        <button id="start-import-btn" type="button" class="btn btn-info" data-dismiss="modal" disabled>Import</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <!-- end import modal -->

            </form>
          </div>
        </div>
      </div>


      <div id="edit-project" class="col-md-4" v-if="gdslProject">
        <div class="panel panel-default overlay-container">
          <div v-if="modelInstances.length" class="overlay" data-toggle="tooltip" title="Locked because there are model instances"></div>
          <div class="panel-heading">
            <strong>Edit project <em>{{ gdslProject.name }}</em></strong>
          </div>
          <div class="list-group">
            <a class="list-group-item" :href="'/codeEditor/editor/' + gdslProject.id + '/style'">
              Style
            </a>
            <a class="list-group-item" :href="'/codeEditor/editor/' + gdslProject.id + '/shape'">
              Shape
            </a>
            <a class="list-group-item" :href="'/codeEditor/editor/' + gdslProject.id + '/diagram'">
              Diagram
            </a>
            <a class="list-group-item" :href="'/metamodel/editor/' + gdslProject.id">
              Concept Editor
            </a>
          </div>
          <div class="panel-footer dropdown">
            <button class="btn dropdown-toggle" type="button" id="btnValidator" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
              Validator
              <span class="caret"></span>
            </button>
            <ul class="dropdown-menu" aria-labelledby="btnValidator">
              <li><a href="#" id="validatorGenerate">Generate / Update Validator</a></li>
              <li><a href="#" id="validatorShow">Show Validation Rules</a></li>
            </ul>
          </div>
        </div>

        <div id="error-panel" class="alert alert-danger alert-dismissible collapse" role="alert">
          <button type="button" class="close" data-hide="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <div></div>
        </div>
        <div id="success-panel" class="alert alert-success alert-dismissible collapse" role="alert">
          <button type="button" class="close" data-hide="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <div></div>
        </div>
      </div>



      <div class="col-md-4" v-if="gdslProject">
        <div id="model-instance-container" class="panel panel-default">
          <div class="panel-heading">
            <strong>Model Instances</strong>
          </div>

          <div class="panel-body" v-if="!modelInstances.length">
            <span class="text-muted">There are no model instances.</span>
          </div>

          <div class="list-group" v-else>
            <a v-for="model in modelInstances" v-bind:key="model.id" href="@routes.ScalaRoutes.getModelEditor(model.id)" class="list-group-item list-item-container">
              {{ model.name }}
              <div data-model-id="@model.id" class="delete-list-item delete-model-instance glyphicon glyphicon-trash" data-toggle="tooltip" title="Delete model instance"></div>
              <div data-model-id="@model.id" class="validate-list-item validate-model-instance glyphicon glyphicon-thumbs-up" data-toggle="tooltip" title="Validate model instance against its meta model"></div>
            </a>
          </div>

          <div class="panel-footer">
            <form>
              <div class="input-group">
                <input type="text" class="form-control" id="inputModelName" placeholder="New model name" autocomplete="off">
                <span class="input-group-btn">
                  <button type=button id="btnCreateModelInstance" class="btn btn-default" data-toggle="tooltip" title="Create model instace">
                    <span class="glyphicon glyphicon-plus" aria-hidden=true></span>
                  </button>
                </span>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-link right">
      <a href="@routes.ScalaRoutes.getWebApp()">
        <button class="btn">
          Generators App <span class="glyphicon glyphicon-chevron-right"></span>
        </button>
      </a>
    </div>

    <!-- invite modal -->
    <div class="modal fade" id="inviteModal" tabindex="-1" role="dialog" aria-labelledby="inviteModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="inviteModalLabel">Invite to project</span>
            <button id="close-invite-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input type="text" class="form-control" id="inviteProjectName" placeholder="E-Mail Address" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button id="start-invite-btn" type="button" class="btn btn-info" disabled>Invite</button>
          </div>
        </div>
      </div>
    </div>
    <!-- end invite modal -->

    <!-- duplicate project modal -->
    <div class="modal fade" id="duplicateModal" tabindex="-1" role="dialog" aria-labelledby="duplicateModalLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header modal-header-info">
            <span class="modal-title" id="duplicateModalLabel">Duplicate project</span>
            <button id="close-duplicate-modal" type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <input type="text" class="form-control" id="duplicateProjectName" placeholder="New Project Name" autocomplete="off">
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            <button id="start-duplicate-btn" type="button" class="btn btn-info" disabled>Duplicate</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DiagramsOverview',
  props: {
    msg: String
  },
  data() {
    return {
      metaModels: [
        {
          id: "520ec611-1dbd-4a93-bf6c-2b316cb67f0b",
          name: "testproject"
        }
      ],
      gdslProject: {
          id: "520ec611-1dbd-4a93-bf6c-2b316cb67f0b",
          name: "testproject",
          concept: "Concept",
          diagram: "diagram",
          shape: "shape",
          style: "style",
          validator: "None"
      },
      modelInstances: [
        /*{
          id: "UUID",
          graphicalDslId: "UUID",
          name: "String"
        }*/
      ]
    }
  },
  created() {
    //console.log(this.$route.params.id)
    if(this.$route.params.id == "") {
      this.gdslProject = null
    } else {
      console.log(this.$route.params.id)
    }
  }
}
</script>
<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
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
