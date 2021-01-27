<template>
  <v-toolbar dense class="d-flex justify-center">
    <div class="action-toolbar">
      <v-switch
          v-model="isEditEnabled"
          :label="`Toggle Editing`"
          @change="$emit('toggle-editable', isEditEnabled)"
          dense
          class="v-custom-height"
      ></v-switch>
      <button class="icon-yIconSave" data-command="Save" title="Save"></button>
      <button class="icon-yIconZoomOriginal" data-command="ZoomOriginal" title="Zoom to original size"></button>
      <button class="icon-yIconZoomFit" data-command="FitContent" title="Fit Content"></button>
      <button class="icon-yIconCut" data-command="Cut" title="Cut" :disabled="!isEditEnabled"></button>
      <button class="icon-yIconCopy" data-command="Copy" title="Copy" :disabled="!isEditEnabled"></button>
      <button class="icon-yIconPaste" data-command="Paste" title="Paste" :disabled="!isEditEnabled"></button>
      <button class="icon-yIconUndo" data-command="Undo" title="Undo"></button>
      <button class="icon-yIconRedo" data-command="Redo" title="Redo"></button>
      <input type="checkbox" id="snapping-button" class="toggle-button" :disabled="!isEditEnabled">
      <label for="snapping-button" class="icon-yIconSnapping" title="Snapping"></label>
      <button data-command="Layout" title="Run Layout" class="icon-yIconLayout" :disabled="!isEditEnabled"></button>
      <span class="demo-separator"></span>
      <input type="checkbox"
             id="grid-button"
             class="demo-toggle-button"
             :checked="isGridEnabled"
             :disabled="!isEditEnabled"
             v-model="isGridEnabled"
             @change="$emit('toggle-grid', isGridEnabled)">
      <label for="grid-button" class="demo-icon-yIconGrid" title="Show Grid"></label>
    </div>
  </v-toolbar>
</template>

<script>
import {bindAction, bindCommand} from "../../utils/Bindings";
import {ICommand} from "yfiles";
import {executeLayout} from "../graphEditor/GraphEditorUtils";

export default {
  name: 'Toolbar',
  mounted() {
    this.registerCommands(this.graphComponent)
  },
  data: function () {
    return {
      isEditEnabled: false,
      isGridEnabled: false
    }
  },
  methods: {
    registerCommands(graphComponent) {
      bindAction("button[data-command='Save']", () => this.saveGraph());
      bindCommand("button[data-command='Cut']", ICommand.CUT, graphComponent)
      bindCommand("button[data-command='Copy']", ICommand.COPY, graphComponent)
      bindCommand("button[data-command='Paste']", ICommand.PASTE, graphComponent)
      bindCommand("button[data-command='FitContent']", ICommand.FIT_GRAPH_BOUNDS, graphComponent)
      bindCommand("button[data-command='ZoomOriginal']", ICommand.ZOOM, graphComponent, 1.0)
      bindCommand("button[data-command='Undo']", ICommand.UNDO, graphComponent)
      bindCommand("button[data-command='Redo']", ICommand.REDO, graphComponent)

      bindAction('#snapping-button', () => {
        const snappingEnabled = document.querySelector('#snapping-button').checked
        graphComponent.inputMode.snapContext.enabled = snappingEnabled
        graphComponent.inputMode.labelSnapContext.enabled = snappingEnabled
      })
      bindAction("button[data-command='Layout']", () => executeLayout(graphComponent))
    }
  },
  props: {
    graphComponent: {
      type: Object,
      required: true
    },
    saveGraph: {
      type: Function,
      required: true
    },
  }
}
</script>

<style scoped>
.action-toolbar {
  display: flex;
}

.action-toolbar > * {
  vertical-align: middle;
}

.action-toolbar button {
  line-height: normal;
  height: 24px;
}

.action-toolbar button,
.action-toolbar > label {
  display: inline-block;
  outline: none;
  border: none;
  background-repeat: no-repeat;
  background-position: 50% 50%;
  background-color: transparent;
  height: 24px;
  width: 24px;
  line-height: 24px;
  box-sizing: border-box;
  padding: 0;
  cursor: pointer;
}

.action-toolbar button:hover,
.action-toolbar > label:hover {
  background-color: #dedede;
}

.demo-toggle-button {
  display: none !important;
}

.demo-toggle-button:checked + label {
  background-color: #dedede;
}

.demo-toggle-button:checked:hover + label {
  background-color: #b2b2b2;
}

.demo-toggle-button:disabled + label {
  opacity: 0.5;
  cursor: default;
  background-color: transparent;
}

.demo-toggle-button.labeled + label {
  background-position-x: left;
  width: inherit;
  padding: 0 2px;
  line-height: 24px;
}

.action-toolbar button:active,
.action-toolbar > label:active,
.action-toolbar .demo-toggle-button:checked:active + label {
  background-color: #b2b2b2;
}

.action-toolbar button:disabled,
.action-toolbar > .demo-toggle-button:disabled + label {
  opacity: 0.5;
  cursor: default;
  background-color: transparent;
}

.demo-separator {
  height: 20px;
  width: 1px;
  background: #999;
  display: inline-block;
  vertical-align: middle;
  margin: 0 2px;
}

.demo-icon-yIconGrid {
  background-image: url('../../styles/icons/grid-16.svg');
}

.v-custom-height {
  height: 25px;
}

#snapping-button {
  margin-top: 5px;
}
</style>
