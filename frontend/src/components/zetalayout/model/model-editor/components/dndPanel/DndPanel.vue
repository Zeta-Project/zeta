<template>
  <div v-show="isExpanded">
    <h1 class="demo-sidebar-header">{{ title }}</h1>
    <div class="demo-sidebar-content">
      <v-expansion-panels>
        <v-expansion-panel
            v-for="diagram in diagram.diagrams"
            :key="diagram.name"
        >
          <v-expansion-panel-header>
            {{ diagram.name }}
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <DndPalette v-for="palette in getPalettes(diagram)"
                        :key="palette.name"
                        :title="palette.name"
                        :nodes="palette.nodes"
                        :concept="concept"
                        :graph-component="graphComponent"
                        :passive-supported="passiveSupported"/>

          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </div>
  </div>
</template>

<script>

import DndPalette from "@/components/zetalayout/model/model-editor/components/dndPanel/DndPalette";

export default {
  name: 'DndPanel',
  components: {DndPalette},
  props: {
    graphComponent: {
      type: Object,
      required: true,
    },
    shape: {
      type: Object,
      required: true,
    },
    diagram: {
      type: Object,
      required: true,
    },
    styleModel: {
      type: Object,
      required: true,
    },
    isExpanded: {
      type: Boolean,
      required: true
    },
    passiveSupported: {
      type: Boolean,
      required: true
    },
    concept: {
      type: Object,
      required: true
    },
    title: {
      type: String,
      default: function () {
        return 'Drag and Drop Panel'
      }
    }
  },
  methods: {
    getPalettes: function (diagram) {
      return diagram.palettes.map(palette => {

        let pnodes = palette.nodes.map(pnode => this.shape.nodes.filter(x => {
          return x.name === pnode
        })[0]);

        return {name: palette.name, nodes: pnodes}
      })
    }
  }
}
</script>

<style scoped>
.demo-sidebar-header {
  color: #666666;
  font-size: 1.8em;
  height: 60px;
  line-height: 60px;
  margin: 0;
  box-sizing: border-box;
  padding-left: 10px;
}
</style>
