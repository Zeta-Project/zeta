<template>
  <GraphEditor ref="GraphComponent" v-on:on-toggle-dnd="toggleDnd"></GraphEditor>
</template>

<script>
import GraphEditor from '../metamodel/graphical-editor/components/graphEditor/GraphEditor'

export default {
  name: 'MetamodelGraphicalEditor',
  components: {
    GraphEditor,
  },
  data: function () {
    return {
      isDndExpanded: false
    }
  },
  methods: {
    toggleDnd(isDndExpanded) {
      this.isDndExpanded = isDndExpanded;
    }
  }
}
</script>

<style>
  html,
  body {
    position: fixed;
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
    background-color: white;
    touch-action: none;
    overscroll-behavior-y: contain;
    overflow: hidden;
    font-family: Tahoma, Verdana, sans-serif;
    font-size: 12px;
    color: #333333;
  }
</style>
