<template>
  <div v-show="isExpanded">
    <h1 class="demo-sidebar-header">{{ title }}</h1>
    <div class="demo-sidebar-content">

      <DndPalette v-for="palette in palettes"
                  :key="palette.name"
                  :title="palette.name"
                  :nodes="palette.nodes"
                  :graph-component="graphComponent"
                  :passive-supported="passiveSupported"
      />
    </div>
  </div>
</template>

<script>

import DndPalette from "@/components/zetalayout/model/model-editor/components/dndPanel/DndPalette";

export default {
  name: 'DndPanel',
  components: {DndPalette},
  data: function () {
    return {
      panelItems: [],
      maxItemWidth: 150,
      div: ''
    }
  },
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
    title: {
      type: String,
      default: function () {
        return 'Drag and Drop Panel'
      }
    }
  },
  computed: {
    palettes: function () {
      return this.diagram.diagrams[0].palettes.map(palette => {

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

    .demo-sidebar-content {
        overflow-y: auto;
        height: calc(100% - 70px);
        padding: 0 25px;
    }

    .demo-sidebar-content h1,
    .demo-sidebar-content h2 {
        color: #666666;
    }

    .demo-sidebar-content a,
    .demo-sidebar-content a:visited {
        text-decoration: none;
        color: #1871bd;
    }

    .demo-sidebar-content a:hover {
        text-decoration: none;
        color: #18468c;
    }

    .demo-sidebar-content ul {
        padding-left: 1.3em;
    }

    .demo-sidebar-content li {
        margin: 0.5em 0;
    }
</style>
