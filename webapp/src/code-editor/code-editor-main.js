$(document).ready(() => {
  fetch('/rest/v1/meta-models/' + metaModelId, {
      method: 'GET',
      credentials: 'same-origin'
    })
    .then(response => response.json()) // TODO check response-code for ok
    .then(metaModel => {
      console.log(metaModel);
      if (dslType in modesForModel) {
        modesForModel[dslType](metaModel)
        initAce();
        $("#btn-save").click(() => saveCode());
        ace.edit("editor").setValue(metaModel.dsl[dslType].code);
      } else {
        $('#editor').text('No language "' + dslType + '" defined.');
      }
    })
    .catch(err => console.log("error loading MetaModel: " + err));
});


function saveCode() {
  const text = ace.edit("editor").getValue();

  fetch('/rest/v1/meta-models/' + metaModelId + '/' + dslType, {
      headers: {
        'Content-Type': 'application/json'
      },
      method: 'PUT',
      credentials: 'same-origin',
      body: JSON.stringify({
        "code": text
      })
    })
    .then(response => alert("Saving succeed")) // TODO check response-code for ok
    .catch(err => "Saving failed: " + err);
}

const modesForModel = {
  'diagram': checkDslLanguage,
  'shape': checkDslLanguage,
  'style': checkStyleLanguage
}

function checkStyleLanguage() {
  alert("TODO: checkStyleLanguage");
  return [];
}

function checkDslLanguage() {
  alert("TODO: checkDslLanguage");
  return [];
}

function initAce() {
  const editor = ace.edit("editor");
  editor.setTheme("ace/theme/xcode");
  editor.getSession().setMode("ace/mode/scala");
  editor.$blockScrolling = Number.PositiveInfinity;
  editor.setOptions({
    "enableBasicAutocompletion": true,
    "enableLiveAutocompletion": true
  });

  const container = $('<div class="container"></div>');
  $('#editor').append(container);

  const panel = $('<div class="panel panel-default"></div>');
  container.append(panel);

  const header = $('<div class="panel-heading"></div>');
  panel.append(header);

  const headerTitle = $('<h3 class="panel-title editor-title"></h3>');
  header.append(headerTitle);

  const strong = $('<strong>' + dslType + '</strong>');
  header.append(strong);

  const btnSave = $('<span id="btn-save" class="btn btn-default glyphicon glyphicon-floppy-disk typcnbtn pull-right">Save Document</span>');
  header.append(btnSave);

  const body = $('<div class="panel-body editor-body"></div>');
  panel.append(body);

  const bodyInner = $('<div style="background-color: gray"></div>');
  body.append(bodyInner);

  const divEditor = $('<div id="aceId" class="editor"></div>');
  bodyInner.append(divEditor);

}
