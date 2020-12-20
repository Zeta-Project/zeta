<template>
  <div class="editor-container">
    <div class="side-box">
      <div id="source-code-inspection"></div>
      <div id="online-users"></div>
      <div id="outline-nodes"></div>
    </div>

    <div class="container code-editor editor-box" :data-meta-model-id="gdslProject.id" :data-dsl-type="dslType">
      <div class="panel panel-default">
        <div class="panel-heading">
          <span class="editor-title">{{ dslType }}</span>
          <span class="editor-button">
              <span class="label label-success js-save-successful" style="display: none">Saving succeed</span>
              <span class="label label-danger js-save-failed" style="display: none">Saving failed</span>
              <span class="js-save btn btn-sm btn-primary" title="Save Document">
                  Save <span class="glyphicon glyphicon-floppy-disk"></span>
              </span>
          </span>
        </div>
        <div class="panel-body editor-body">
          <div class="editor"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'MetamodelCodeEditor',
  data() {
    return {
      gdslProject: {
        id: "520ec611-1dbd-4a93-bf6c-2b316cb67f0b",
        name: "testproject",
        concept: "Concept",
        diagram: "diagram",
        shape: "shape",
        style: "style",
        validator: "None"
      },
      dslType: ''
    }
  },
  methods: {
    initializeEditor: function () {
      $('.code-editor').each((i, e) => new CodeEditor(e, $(e).data('meta-model-id'), $(e).data('dsl-type')));
    }
  },
  created() {
    this.gdslProject.id = this.$route.params.id
    this.dslType = this.$route.params.dslType
  },
  mounted() {
    this.initializeEditor()
  },
  watch: {
    '$route': 'initializeEditor'
  }
}

import $ from "jquery";
import 'brace';
import 'brace/ext/language_tools';
import 'brace/theme/xcode';
import 'brace/mode/scala';
import {styleLanguage, diagramLanguage, shapeLanguage} from './utils/ace-grammar';
import {SourceCodeInspector} from "./utils/source-code-inspector";
import {CodeOutline} from "./utils/code-outline";
import {OnlineSocket} from "./utils/online-socket";
import axios from 'axios'


const modesForModel = {
  'diagram': diagramLanguage,
  'shape': shapeLanguage,
  'style': styleLanguage
};

class CodeEditor {
  constructor(element, metaModelId, dslType) {
    this.$element = $(element);
    this.metaModelId = metaModelId;
    this.dslType = dslType;
    this.editor = this.initAceEditor(element.querySelector('.editor'));
    this.loadSourceCode();
    this.$element.on('click', '.js-save', () => this.saveSourceCode(this.editor.getValue()));
    this.sourceCodeInspector = new SourceCodeInspector(element, metaModelId, dslType, this.editor);
    this.sourceCodeInspector.runInspection();
    this.codeOutline = new CodeOutline(element, metaModelId, dslType, this.editor);
    let area = "codeEditor-" + dslType + "-" + metaModelId;
    this.onlineSocket = new OnlineSocket(area);
  }

  initAceEditor(element) {
    const editor = ace.edit(element);
    editor.setTheme("ace/theme/xcode");
    editor.getSession().setMode("ace/mode/scala");
    editor.$blockScrolling = Number.PositiveInfinity;
    editor.setOptions({
      "enableBasicAutocompletion": true,
      "enableLiveAutocompletion": true
    });
    return editor;
  }

  loadSourceCode() {
    axios.get(
        'http://localhost:9000/rest/v1/meta-models/' + this.metaModelId,
        {withCredentials: true}).then(
        (response) => {
          this.setAceEditorContent(response.data[this.dslType]);
          this.codeOutline.createCodeOutline();
        },
        (error) => console.log('Error loading MetaModel ' + this.metaModelId + ': ' + error )
    )
  }

  setAceEditorContent(content) {
    const session = ace.createEditSession(content, modesForModel[this.dslType]);
    this.editor.setSession(session);
  }

  saveSourceCode(code) {
    axios.put(
        'http://localhost:9000/rest/v1/meta-models/' + this.metaModelId + '/' + this.dslType,
        JSON.stringify(code),
        {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json'
          }
        }
    ).then(
        (response) => {
          this.toggleSaveNotifications('.js-save-successful');
          this.sourceCodeInspector.runInspection();
        },
        (error) => {
          this.toggleSaveNotifications('.js-save-failed');
          console.error(`Save failed`, error);
        }
    )
  }

  toggleSaveNotifications(element) {
    this.$element.find(element).stop(true, true).fadeIn(400).delay(3000).fadeOut(400);
  }

}

</script>

<style>
/* scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
  border-radius: 4px;
}
::-webkit-scrollbar-track {
  background: #3c3f35;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}
::-webkit-scrollbar-thumb:hover {
  background: #555;
  border-radius: 4px;
}

#container {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  background: #2f3129;
  padding: 10px;
}

#sidebar {
  flex-grow: 0;
  color: white;
  min-width: 150px;
  max-height: 100vh;
  overflow-y: auto;
}

#sidebar a {
  display: block;
  color: #f4fdd2;
  text-decoration: none;
  padding: 2px;
  margin-right: 5px;
  cursor: pointer;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
}
#sidebar a:hover {
  color: #95ad3e;
}

.editor {
  flex-grow: 1;
}

.download-btn {
  margin: 0 0 10px 0;
}

.download-btn>.glyhicon {
  margin: 0 0 0 5px;
}

.folder-element {
  color: #74d474;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
}

/* scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
  border-radius: 4px;
}
::-webkit-scrollbar-track {
  background: #dedede;
}
::-webkit-scrollbar-thumb {
  background: #888;
}
::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.editor-container {
  display: flex;
}

.editor {
  height: 500px; /* fallback for older browsers */
  height: calc(100vh - 170px);
}

.editor-body {
  padding: 0;
}

.editor-title {
  text-transform: capitalize;
  font-size: 14px;
  font-weight: bold;
}
.editor-button {
  margin: -5px -10px;
  float: right;
}
.editor-button .btn>.glyphicon {
  margin: 0 0 0 5px;
}

.ace-editor {
  height: 100%;
  width: 70%;
}

.lineErrorMarker {
  position: absolute;
  background: #f0ad4e;
  z-index: 20
}

.charErrorMarker {
  position: absolute;
  background-color: #d9534f;
  border-color: #d43f3a;;
  z-index: 21
}

.ace-container {
  border-radius: 5px;
  border: 1px solid gray;
}

#selectable .ui-selecting {
  text-shadow: 2px 2px #000000;
}

#selectable .ui-selected {
  color: #F8F8F8;
  background-color: #0c7cf4;
}

#selectable {
  list-style-type: none;
  margin: 0;
  padding: 0;
  width: 100%;
  overflow: scroll;
}

#selectable li {
  background: transparent;
  font-size: 14px;
  height: 22px;
  cursor: pointer;
  width: 100%;
  border: none;
  padding-left: 5px;
  padding-right: 5px;
  vertical-align: middle;
  color: black;
}

.sidebar-container {
  background-color: #ff0000;
  height: 100%;
}

#sidebar {
  -webkit-touch-callout: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  height: 500px;
  max-width: 200px;
  padding: 0;
  float: left;
  border-right: 1px solid lightgray;
  background-color: #F7F7F7;
}

.vcenter {
  display: inline-block;
  vertical-align: middle;
  float: none;
}

.toolbar {
  height: 49px;
  width: 100%;
  float: left;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  border-bottom: 1px solid lightgray;
  background-color: #F7F7F7;
}

.toolbarbtn {
  margin: 3px;
}

.toolbar-button:hover {
  background: linear-gradient(#FCFCFC, #DCDCDC);
}

.toolbar-button:active {
  background: linear-gradient(#1AD6FD, #1D62F0);
}

#source-code-inspection {
  height: 0;
  overflow: hidden;
}
#source-code-inspection > div {
  border-radius: 4px;
  vertical-align: middle;
  padding: 8px 15px;
  /*white-space: pre;*/
  margin-bottom: 20px;
  height: auto;
  border: 1px solid transparent;
  color: #fff;
  -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05);
}

#source-code-inspection > div.error {
  background-color: #d9534f;
  border-color: #d43f3a;
}

#source-code-inspection > div.success {
  background-color: #5cb85c;
  border-color: #4cae4c;
}

#source-code-inspection > div.warning {
  background-color: #f0ad4e;
  border-color: #eea236;
}

#source-code-inspection > div > .lineNumber {
  display: block;
  font-size: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
  float: right;
  line-height: 20px;
}

.side-box {
  min-width: 275px;
  max-width: 275px;
}

#outline-nodes .outline-heading {
  font-size: 14px;
  font-weight: bold;
}

#outline-nodes .outline-node {
  padding: 0 0 4px 20px;
  cursor: pointer;
  font-size: 12px;
  position: relative;
  color: #333;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
}

#outline-nodes .outline-node > .line {
  font-size: 12px;
  float: right;
}

#outline-nodes .outline-node:hover {
  font-weight: bold;
}

#outline-nodes .outline-node::before {
  content: '';
  background-color: #204d74;
  width: 14px;
  height: 14px;
  font-size: 11px;
  position: absolute;
  font-weight: normal;
  left: 1px;
  top: 1px;
  border-radius: 4px;
  color: white;
  text-align: center;
}

#outline-nodes .outline-node.style::before {
  content: 'S';
  background-color: #0ab27e;
}

#outline-nodes .outline-node.palette::before {
  content: 'P';
  background-color: #b217a6;
}

#outline-nodes .outline-node.node::before {
  content: 'N';
  background-color: #1C5D99;
}

#outline-nodes .outline-node.edge::before {
  content: 'E';
  background-color: #81941a;
}

#online-users {
  margin: 0 0 13px 0;
}

#online-users .online-user {
  position: relative;
  display: inline-block;
  padding: 7px 12px 7px 25px;
  margin: 0 7px 7px 0;
  color: #333333;
  font-size: 12px;
  background-color: #f5f5f5;
  -webkit-border-radius: 3px;
  border-radius: 3px;
  -webkit-box-shadow: 0 2px 3px 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.08);
  box-shadow: 0 2px 3px 0 rgba(0,0,0,0.16), 0 0 0 1px rgba(0,0,0,0.08);
}

#online-users .online-user:before {
  content: '';
  width: 8px;
  height: 8px;
  background-color: #5cb85c;
  position: absolute;
  top: 11px;
  left: 11px;
  -webkit-border-radius: 50%;
  border-radius: 50%;
}

@media (max-width: 768px) {
  #outline-nodes {
    display: none;
  }

  .editor-container {
    flex-direction: column;
  }

  .side-box {
    max-width: 100%;
  }

  .editor {
    height: calc(100vh - 170px - 45px);
  }

  .editor-box {
    width: 100%;
    padding: 0;
  }
}
</style>
