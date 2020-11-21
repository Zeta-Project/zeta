<template>
  <v-container v-on:dragover="dragover" v-on:drop="preventDrop">
    <v-row>
      <v-col md="4">
        <ProjectSelection
          :meta-models="metaModels"
          :gdsl-project="gdslProject"
        />
      </v-col>

      <v-col md="4">
        <EditorSelection
          v-if="gdslProject"
          :gdsl-project="gdslProject"
          :model-instances="modelInstances"
        />
      </v-col>

      <v-col md="4">
        <ModelSelection
          v-if="gdslProject"
          :gdsl-project="gdslProject"
          :model-instances="modelInstances"
        />
      </v-col>
    </v-row>

    <v-row>
      <div class="bottom-link right">
        <a href="@routes.ScalaRoutes.getWebApp()">
          <button class="btn">
            Generators App
            <span class="glyphicon glyphicon-chevron-right"></span>
          </button>
        </a>
      </div>
    </v-row>
  </v-container>
</template>

<script>
import axios from "axios";
import { EventBus } from "@/eventbus/eventbus";
import ProjectSelection from "./ProjectSelection";
import EditorSelection from "./EditorSelection";
import ModelSelection from "./ModelSelection";

export default {
  name: "DiagramsOverview",
  components: { ModelSelection, EditorSelection, ProjectSelection },
  props: {
    msg: String,
  },
  data() {
    return {
      metaModels: [
        {
          id: "",
          name: "loading...",
        },
      ],
      gdslProject: {
        id: "",
        name: "",
        concept: "",
        diagram: "",
        shape: "",
        style: "",
        validator: "",
      },
      modelInstances: [
        /*{
          id: "UUID",
          graphicalDslId: "UUID",
          name: "String"
        }*/
      ],
      inputProjectName: "",
      selectedProjectId: null,

      uploadText: "Drag and Drop .zeta file here...",
      importProjectName: "",
      file: null,
    };
  },
  methods: {
    loadProjects() {
      axios
        .get("http://localhost:9000/overview", { withCredentials: true })
        .then(
          (response) => {
            this.metaModels = response.data.metaModels;
            // this.modelInstances = response.data.modelInstances
          },
          (error) =>
            EventBus.$emit(
              "errorMessage",
              "Could not load metamodels: " + error
            )
        );
      axios
        .get("http://localhost:9000/rest/v1/models", { withCredentials: true })
        .then(
          (response) => {
            this.modelInstances = response.data;
          },
          (error) =>
            EventBus.$emit(
              "errorMessage",
              "Could not load model instances: " + error
            )
        );
    },
    routeParamChanged() {
      if (!this.$route.params.id || this.$route.params.id === "") {
        this.gdslProject = null;
        EventBus.$emit("gdslProjectUnselected");
      } else {
        axios
          .get(
            "http://localhost:9000/rest/v1/meta-models/" +
              this.$route.params.id,
            { withCredentials: true }
          )
          .then(
            (response) => {
              this.gdslProject = response.data;
              EventBus.$emit("gdslProjectSelected", response.data);
            },
            (error) =>
              EventBus.$emit(
                "errorMessage",
                "Could not load selected metamodel: " + error
              )
          );
      }
    },

    dragover(event) {
      event.preventDefault();
      event.stopPropagation();
      this.uploadText = "Drag here";
    },
    preventDrop(event) {
      event.preventDefault();
    },
  },
  created() {
    EventBus.$on("metaModelAdded", (metamodel) => {
      this.metaModels.push(metamodel);
    });
    EventBus.$on("metaModelRemoved", (metamodelID) => {
      let i = this.metaModels.map((item) => item.id).indexOf(metamodelID); // find index of your object
      this.metaModels.splice(i, 1);
      if (this.$route.params.id === metamodelID)
        this.$router.push("/zeta/overview").catch((err) => {});
    });
    EventBus.$on("reloadProjects", () => {
      this.loadProjects();
    });
    EventBus.$on("projectSelected", (metaModelId) => {
      this.selectedProjectId = metaModelId;
    });
  },
  mounted() {
    this.loadProjects();
    this.routeParamChanged();
  },
  watch: {
    $route: "routeParamChanged",
  },
};
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
