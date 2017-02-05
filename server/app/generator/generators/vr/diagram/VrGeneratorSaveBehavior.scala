package generator.generators.vr.diagram

/**
  * Created by max on 02.02.17.
  */
object VrGeneratorSaveBehavior {

  def generate() = {
    s"""
    <link rel="import" href="vr-three.html">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

    <script>
      window.VrBehavior = window.VrBehavior || {};
      VrBehavior.Save = [VrBehavior.ThreeJS, {

      _save: function () {
        var self = this;
        var token = null;

        // get model id from href
        var href = window.location.href;
        var modelId = href.substr(href.lastIndexOf('/') + 1, href.length);

        var tagType = {
          "VR-KLASSE": "Klasse",
          "VR-ABSTRACTKLASSE": "AbstractKlasse",
          "VR-INTERFACE": "InterfaceKlasse"
        };

        getToken();

        function getToken() {
          var accessTokenAttributes = {
            method: 'POST',
            url: 'http://localhost:9000/oauth/accessTokenLocal',
            contentType: 'application/x-www-form-urlencoded',
            body: 'client_id=modigen-browser-app1&grant_type=implicit',
            id: 'token',
            withCredentials: true,
            success: _onTokenSuccess,
            failure: _onFailure
          };

          self._createRequest(accessTokenAttributes);
        }

        function _onTokenSuccess(event) {
           token = event.detail.xhr.response.token_type + ' ' + event.detail.xhr.response.access_token;

          _getModel();
        }

        function _getModel() {
          var modelAttributes = {
            id: 'model',
            method: 'GET',
            url: 'http://localhost:9000/models/' + modelId,
            withCredentials: true,
            headers: {'authorization': token},
            success: _onModelSuccess,
            failure: _onFailure
          };

          self._createRequest(modelAttributes);
        }

        function updateElement(old, modified) {
            if (old.mClass !== undefined) {
              // update position
              old.position.x = modified.xPos;
              old.position.y = modified.yPos * -1;
              old.size.height = modified.height;
              old.size.width = modified.width;
              old['init-size'].height = modified.height;
              old['init-size'].width = modified.width;
            }
            return old;
          }

          function _addModel(model, element) {
                       var obj = {};
                       obj.type = '';
                       obj.size = {};
                       obj.source = {};
                       obj.target = {};
                       obj.attrs = {};
                       obj.embeds = '';
                       obj.z = Math.floor(Math.random() * (31));

                 if (element.tagName.includes('VR-CONNECTION')) {
                           var tagFrom = document.getElementById(element.from).tagName;
                           var tagTo = document.getElementById(element.to).tagName;
                           obj.sourceAttribute = tagType[tagFrom];
                           obj.targetAttribute = tagType[tagTo];
                           obj.id = element.id;
                           obj.source.id = element.from;
                           obj.target.id = element.to;
                           obj.placings = [];
                           obj.labels = [];
                           obj.styleSet = true;

                    switch (element.tagName) {
                               case 'VR-CONNECTION-AGGREGATION':
                                   obj.type = 'zeta.MLink';
                                   obj.subtype = 'aggregation';
                                   obj.mReference = "Aggregation";
                                   obj.attrs = getConnectionStyle(obj.subtype);
                                   break;
                               case 'VR-CONNECTION-INHERITANCE':
                                   obj.type = 'zeta.MLink';
                                   obj.subtype = 'inheritance';
                                   obj.mReference = "Inheritance";
                                  obj.attrs = getConnectionStyle(obj.subtype);
                                   break;
                               case 'VR-CONNECTION-REALIZATION':
                                   obj.type = 'zeta.MLink';
                                   if (tagFrom == "VR-KLASSE") {
                                       obj.subtype = 'realization';
                                       obj.mReference = "BaseClassRealization";

                             } else {
                                       obj.subtype = 'realization';
                                       obj.mReference = "Realization";
                                   }
                                   obj.attrs = getConnectionStyle(obj.subtype);
                                   break;
                               case 'VR-CONNECTION-COMPONENT':
                                   obj.type = 'zeta.MLink';
                                   obj.subtype = 'component';
                                   obj.mReference = "Component";
                                  obj.attrs = getConnectionStyle(obj.subtype);
                                   break;
                           }

                 } else {
                           // class
                           obj['init-size'] = {};
                           obj.resize = {};
                           obj.compartments = [];
                           obj.position = {};
                           obj.position.x = element.xPos;
                           obj.position.y = element.yPos * -1;
                           obj.angle = 0;
                           obj.nodeName = '';
                           obj.mClass = '';
                           obj.mClassAttributeInfo = [];
                           obj.id = element.id;
                           obj.resize.horizontal = true;
                           obj.resize.vertical = true;
                           obj.resize.propotional = true;

                     obj['init-size'].width = element.width;
                           obj['init-size'].height = element.height;
                           obj.size.width = element.width;
                           obj.size.height = element.height;

                    switch (element.tagName) {
                               case 'VR-PLACE':
                                   obj.type = 'zeta.place';
                                   obj.initSize.width = 60;
                                   obj['init-size'].height = 60;
                                   obj.size.width = 60;
                                   obj.size.height = 60;
                                   obj.nodeName = 'placeNode';
                                   obj.mClass = 'Place';
                                   obj.z = 1;
                                   obj.attrs = getShapeStyle("place");
                                   break;
                               case 'VR-TRANSITION':
                                   obj.type = 'zeta.transition';
                                   obj['init-size'].width = 100;
                                   obj['init-size'].height = 200;
                                   obj.size.width = 100;
                                   obj.size.height = 200;
                                   obj.nodeName = 'transitionNode';
                                   obj.mClass = 'Transition';
                                   obj.z = 3;
                                   obj.attrs = getShapeStyle("transition");
                                   break;
                               case 'VR-KLASSE':
                                   obj.type = 'zeta.klasse';
                                   obj.nodeName = 'classNode';
                                   obj.mClass = 'Klasse';
                                   obj.attrs = getShapeStyle("klasse");
                                   break;
                               case 'VR-ABSTRACTKLASSE':
                                   obj.type = 'zeta.abstractKlasse';
                                   obj.nodeName = 'abClassNode';
                                   obj.mClass = 'AbstractKlasse';
                                   obj.attrs = getShapeStyle("abstractKlasse");
                                   break;
                               case 'VR-INTERFACE':
                                   obj.type = 'zeta.interface';
                                   obj.nodeName = 'inClassNode';
                                   obj.mClass = 'InterfaceKlasse';
                                   obj.attrs = getShapeStyle("interface");
                      break;
            }
          }

          model.cells.push(obj);
            return model;
          }

          function guVid() {
            function s4() {
              return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
              s4() + '-' + s4() + s4() + s4();
          }

          function _addElemnt(allElements, element) {
                       var newElement = {};
                       newElement.id = element.id;
                       newElement.attributes = {};

            if (element.tagName.includes('VR-CONNECTION')) {
                          var tagFrom = document.getElementById(element.from).tagName;
                           var tagTo = document.getElementById(element.to).tagName;
                           newElement.source = {};
                           newElement.target = {};
                           newElement.source[tagType[tagFrom]] = [];
                           newElement.source[tagType[tagFrom]][0] = element.from;
                           newElement.target[tagType[tagTo]] = [];
                           newElement.target[tagType[tagTo]] [0] = element.to;
                       } else {
                           newElement.inputs = {};
                           newElement.outputs = {};
                       }

                 switch (element.tagName) {
                          case 'VR-PLACE':
                               newElement.mClass = 'Place';
                               break;
                           case 'VR-TRANSITION':
                               newElement.mClass = 'Transition';
                               break;
                           case 'VR-KLASSE':
                              newElement.mClass = 'Klasse';
                               break;
                           case 'VR-ABSTRACTKLASSE':
                               newElement.mClass = 'AbstractKlasse';
                               break;
                           case 'VR-INTERFACE':
                               newElement.mClass = 'InterfaceKlasse';
                               break;
                           case 'VR-CONNECTION-AGGREGATION':
                               newElement.mReference = 'Aggregation';
                               break;
                           case 'VR-CONNECTION-INHERITANCE':
                               newElement.mReference = 'Inheritance';
                               break;
                           case 'VR-CONNECTION-REALIZATION':
                               if (tagFrom == "VR-KLASSE") {
                                   newElement.mReference = 'BaseClassRealization';
                               } else {
                                   newElement.mReference = 'Realization';
                               }
                               break;
                           case 'VR-CONNECTION-COMPONENT':
                               newElement.mReference = 'Component';
                               break;
          }

          allElements.elements.push(newElement);
            return allElements;
          }

          function _onModelSuccess(event) {
            var response = event.detail.xhr.response;
            var model = JSON.parse(response.model.uiState);
            var elements = response.model;
            var children = document.querySelector('vr-scene').children;
            var currentElements = [];

            for (var i = 0; i < children.length; i++) {
              if (children[i].tagName.indexOf("VR-") !== -1) {
                var index = findIndexOfElement(model.cells, children[i].id);
                currentElements.push(children[i].id);
                if (index !== -1) {
                  model.cells[index] = updateElement(model.cells[index], children[i]);
                } else {
                  model = _addModel(model, children[i]);
                  elements = _addElemnt(elements, children[i]);
                }
              }
            }

            // check if element from database is deleted
            for (var j = 0; j < model.cells.length; j++) {
              if (currentElements.indexOf(model.cells[j].id) === -1) {
                model.cells.splice(j, 1);
              }
            }

            for (var u = 0; u < elements.elements.length; u++) {
              if (elements.elements[u].mReference !== undefined) {
                var lineId = elements.elements[u].id;
                var klassenId = elements.elements[u].source[Object.keys(elements.elements[u].source)[0]][0];
                for (var v = 0; v < elements.elements.length; v++) {
                  if (elements.elements[v].id === klassenId) {
                    elements.elements[v].outputs[elements.elements[u].mReference] = [];
                    elements.elements[v].outputs[elements.elements[u].mReference][0] = lineId;
                  }
                }
              }
            }

            for (var u = 0; u < elements.elements.length; u++) {
              if (elements.elements[u].mReference !== undefined) {
                var lineId = elements.elements[u].id;
                var klassenId = elements.elements[u].target[Object.keys(elements.elements[u].target)[0]][0];
                for (var v = 0; v < elements.elements.length; v++) {
                  if (elements.elements[v].id === klassenId) {
                    elements.elements[v].inputs[elements.elements[u].mReference] = [];
                    elements.elements[v].inputs[elements.elements[u].mReference][0] = lineId;
                  }
                }
              }
            }

            // back to string
            elements.uiState = JSON.stringify(model);
            var myResponse = JSON.stringify(elements);

            var updateModelAttributes = {
              method: 'PUT',
              url: 'http://localhost:9000/models/' + modelId + '/definition',
              contentType: 'application/json', // charset=UTF-8
              body: myResponse,
              id: 'update',
              headers: {'authorization': token},
              withCredentials: true,
              success: _onSaveSuccess,
              failure: _onFailure
            };

            self._createRequest(updateModelAttributes);

          }

          function findIndexOfElement(allElements, elementId) {
            return allElements.map(function (e) {
                return e.id;
              }).indexOf(elementId);
          }

          function _onSaveSuccess(event) {
            alert("Success, model saved!!");
          }

          function _onFailure(error) {
            console.error(error);
          }
        }
      }]
     </script>
     """
  }
}
