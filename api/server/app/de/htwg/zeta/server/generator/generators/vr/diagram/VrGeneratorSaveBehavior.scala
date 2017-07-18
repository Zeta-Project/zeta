package de.htwg.zeta.server.generator.generators.vr.diagram

import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.edge.Edge
import de.htwg.zeta.server.generator.model.diagram.node.Node
import de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection

/**
 * Created by max on 02.02.17.
 */
object VrGeneratorSaveBehavior {

  def generate(nodes: Iterable[Node], connections: Iterable[Connection], diagram: Diagram) = {
    val edgeMap = diagram.edges.groupBy(connectionGroup(_))
    s"""
    <link rel="import" href="/assets/prototyp/behaviors/vr-three.html">
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
            ${nodes.map(generateNodeMapping(_)).mkString.dropRight(1)}
          };

          getToken();
          ${generateGetTokenFunction()}
          ${generateAddModelFunction(nodes, edgeMap)}

          function guVid() {
            function s4() {
              return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
              s4() + '-' + s4() + s4() + s4();
          }

          ${generateAddElementFunction(nodes, edgeMap)}
          ${generateOnModelSuccessFunction()}

          function _onFailure(error) {
            console.error(error);
          }
        }
      }]
    </script>
    """
  }

  private def generateNodeMapping(node: Node) = {
    val name = node.shape match {
      case Some(shape) => shape.getNameOfShape
      case _ => ""
    }
    s"""'VR-${name.toUpperCase()}': '${node.mcoreElement.name}',"""
  }

  private def generateGetTokenFunction(): Unit = {
    """
      | function getToken() {
      |   var accessTokenAttributes = {
      |     method: 'POST',
      |     url: 'http://localhost:9000/oauth/accessTokenLocal',
      |     contentType: 'application/x-www-form-urlencoded',
      |     body: 'client_id=modigen-browser-app1&grant_type=implicit',
      |     id: 'token',
      |     withCredentials: true,
      |     success: _onTokenSuccess,
      |     failure: _onFailure
      |   };
      |
      |   self._createRequest(accessTokenAttributes);
      | }
      |
      | function _onTokenSuccess(event) {
      |   token = event.detail.xhr.response.token_type + ' ' + event.detail.xhr.response.access_token;
      |
      |   _getModel();
      | }
      |
      | function _getModel() {
      |   var modelAttributes = {
      |     id: 'model',
      |     method: 'GET',
      |     url: '/rest/v1/models/' + modelId,
      |     withCredentials: true,
      |     headers: {'authorization': token},
      |     success: _onModelSuccess,
      |     failure: _onFailure
      |   };
      |
      |   self._createRequest(modelAttributes);
      | }
    """.stripMargin
  }

  private def generateConnectionSwitch(name: String, edges: List[Edge], nodes: Iterable[Node]) = {
    s"""
    case 'VR-CONNECTION-${name.toUpperCase()}':
      obj.type = 'zeta.MLink';
      obj.subtype = '${name}';

      obj.mReference = '${edges.head.mcoreElement.name}';

      ${edges.tail.map(referenceIf(_, nodes)).mkString.drop(5)}

      obj.attrs = getConnectionStyle(obj.subtype);
      break;
    """
  }

  private def referenceIf(edge: Edge, nodes: Iterable[Node]) = {
    s"""else if(tagFrom == 'VR-${getNodeName(edge.from.name, nodes)}') { obj.mReference = '${edge.mcoreElement.name}';}"""
  }

  private def getNodeName(mclass: String, nodes: Iterable[Node]) = {
    nodes.filter(_.mcoreElement.name == mclass).head.shape match {
      case Some(shape) => shape.getNameOfShape.toUpperCase()
      case _ => ""
    }
  }

  private def generateShapeSwitch(node: Node) = {
    val name = node.shape match {
      case Some(shape) => shape.getNameOfShape
      case _ => ""
    }
    s"""
    case 'VR-${name.toUpperCase()}':
      obj.type = 'zeta.${name}';
      obj.nodeName = '${node.name}';
      obj.mClass = '${name.capitalize}';
      obj.attrs = getShapeStyle("${name}");
      break;
    """
  }

  private def generateAddModelFunction(nodes: Iterable[Node], edgeMap: Map[String, List[Edge]]) = {
    s"""
      | function _addModel(model, element) {
      |   // setup obj structure
      |   var obj = {};
      |   obj.type = '';
      |   obj.size = {};
      |   obj.source = {};
      |   obj.target = {};
      |   obj.attrs = {};
      |   obj.embeds = '';
      |   obj.z = Math.floor(Math.random() * (31));
      |
      |   ${generateModelGenerator(nodes, edgeMap)}
      |   model.cells.push(obj);
      |   return model;
      | }
    """.stripMargin
  }

  private def generateModelGenerator(nodes: Iterable[Node], edgeMap: Map[String, List[Edge]]) = {
    s"""
      | if (element.tagName.includes('VR-CONNECTION')) {
      |   var tagFrom = document.getElementById(element.from).tagName;
      |   var tagTo = document.getElementById(element.to).tagName;
      |   obj.sourceAttribute = tagType[tagFrom];
      |   obj.targetAttribute = tagType[tagTo];
      |   obj.id = element.id;
      |   obj.source.id = element.from;
      |   obj.target.id = element.to;
      |   obj.placings = [];
      |   obj.labels = [];
      |   obj.styleSet = true;
      |
      |   switch (element.tagName) {
      |     ${edgeMap.map { case (key, value) => generateConnectionSwitch(key, value, nodes) }.mkString}
      |   }
      | } else {
      |   // class
      |   obj['init-size'] = {};
      |   obj.resize = {};
      |   obj.compartments = [];
      |   obj.position = {};
      |   obj.position.x = element.xPos;
      |   obj.position.y = element.yPos * -1;
      |   obj.angle = 0;
      |   obj.nodeName = '';
      |   obj.mClass = '';
      |   obj.mClassAttributeInfo = [];
      |   obj.id = element.id;
      |   obj.resize.horizontal = true;
      |   obj.resize.vertical = true;
      |   obj.resize.propotional = true;
      |
      |   obj['init-size'].width = element.width;
      |   obj['init-size'].height = element.height;
      |   obj.size.width = element.width;
      |   obj.size.height = element.height;
      |
      |   switch (element.tagName) {
      |     ${nodes.map(generateShapeSwitch(_)).mkString}
      |   }
      | }
    """.stripMargin
  }

  private def generateAddElementFunction(nodes: Iterable[Node], edgeMap: Map[String, List[Edge]]) = {
    s"""
      | function _addElemnt(allElements, element) {
      |   var newElement = {};
      |   newElement.id = element.id;
      |   newElement.attributes = {};
      |
      |   if (element.tagName.includes('VR-CONNECTION')) {
      |     var tagFrom = document.getElementById(element.from).tagName;
      |     var tagTo = document.getElementById(element.to).tagName;
      |     newElement.source = {};
      |     newElement.target = {};
      |     newElement.source[tagType[tagFrom]] = [];
      |     newElement.source[tagType[tagFrom]][0] = element.from;
      |     newElement.target[tagType[tagTo]] = [];
      |     newElement.target[tagType[tagTo]] [0] = element.to;
      |   } else {
      |     newElement.inputs = {};
      |     newElement.outputs = {};
      |   }
      |
      |   switch (element.tagName) {
      |     ${generateMixedSwitch(nodes, edgeMap)}
      |   }
      |
      |   allElements.elements.push(newElement);
      |   return allElements;
      | }
    """.stripMargin
  }

  private def generateMixedSwitch(nodes: Iterable[Node], edges: Map[String, List[Edge]]) = {
    s"""
    ${nodes.map(generateNodeMClass(_)).mkString}
    ${edges.map { case (key, value) => generateEdgeMClass(key, value, nodes) }.mkString}
    """
  }

  private def generateNodeMClass(node: Node) = {
    val name = node.shape match {
      case Some(shape) => shape.getNameOfShape
      case _ => ""
    }
    s"""
    case 'VR-${name.toUpperCase()}':
      newElement.mClass = '${node.mcoreElement.name}';
      break;
    """
  }

  private def generateEdgeMClass(name: String, edges: List[Edge], nodes: Iterable[Node]) = {
    s"""
    case 'VR-CONNECTION-${name.toUpperCase()}':
      newElement.mReference = '${edges.head.mcoreElement.name}';

      ${edges.tail.map(mclassIf(_, nodes)).mkString.drop(5)}

      break;
    """
  }

  private def mclassIf(edge: Edge, nodes: Iterable[Node]) = {
    s"""else if(tagFrom == 'VR-${getNodeName(edge.from.name, nodes)}') { newElement.mReference = '${edge.mcoreElement.name}';}"""
  }

  private def generateOnModelSuccessFunction() = {
    s"""
      | function _onModelSuccess(event) {
      |   var response = event.detail.xhr.response;
      |   var model = JSON.parse(response.model.uiState);
      |   var elements = response.model;
      |   var children = document.querySelector('vr-scene').children;
      |   var currentElements = [];
      |   ${generateModelElementGenerator()}
      |
      |   // back to string
      |   elements.uiState = JSON.stringify(model);
      |   var myResponse = JSON.stringify(elements);
      |   var updateModelAttributes = {
      |     method: 'PUT',
      |     url: '/rest/v1/models/' + modelId + '/definition',
      |     contentType: 'application/json', // charset=UTF-8
      |     body: myResponse,
      |     id: 'update',
      |     headers: {'authorization': token},
      |     withCredentials: true,
      |     success: _onSaveSuccess,
      |     failure: _onFailure
      |   };
      |
      |   self._createRequest(updateModelAttributes);
      | }
      |
      | function findIndexOfElement(allElements, elementId) {
      |   return allElements.map(function (e) { return e.id; }).indexOf(elementId);
      | }
      |
      | function _onSaveSuccess(event) {
      |   alert("Success, model saved!!");
      | }
      |
      | function updateElement(old, modified) {
      |   if (old.mClass !== undefined) {
      |     old.position.x = modified.xPos;
      |     old.position.y = modified.yPos * -1;
      |     old.size.height = modified.height;
      |     old.size.width = modified.width;
      |     old['init-size'].height = modified.height;
      |     old['init-size'].width = modified.width;
      |   }
      |   return old;
      | }
    """.stripMargin
  }

  private def generateModelElementGenerator() = {
    """
      | for (var i = 0; i < children.length; i++) {
      |   if (children[i].tagName.indexOf("VR-") !== -1) {
      |     var index = findIndexOfElement(model.cells, children[i].id);
      |     currentElements.push(children[i].id);
      |     if (index !== -1) {
      |       model.cells[index] = updateElement(model.cells[index], children[i]);
      |     } else {
      |       model = _addModel(model, children[i]);
      |       elements = _addElemnt(elements, children[i]);
      |     }
      |   }
      | }
      |
      | // check if element from database is deleted
      | for (var j = 0; j < model.cells.length; j++) {
      |   if (currentElements.indexOf(model.cells[j].id) === -1) {
      |     model.cells.splice(j, 1);
      |   }
      | }
      |
      | for (var u = 0; u < elements.elements.length; u++) {
      |   if (elements.elements[u].mReference !== undefined) {
      |     var lineId = elements.elements[u].id;
      |     var klassenId = elements.elements[u].source[Object.keys(elements.elements[u].source)[0]][0];
      |     for (var v = 0; v < elements.elements.length; v++) {
      |       if (elements.elements[v].id === klassenId) {
      |         elements.elements[v].outputs[elements.elements[u].mReference] = [];
      |         elements.elements[v].outputs[elements.elements[u].mReference][0] = lineId;
      |       }
      |     }
      |   }
      | }
      |
      | for (var u = 0; u < elements.elements.length; u++) {
      |   if (elements.elements[u].mReference !== undefined) {
      |     var lineId = elements.elements[u].id;
      |     var klassenId = elements.elements[u].target[Object.keys(elements.elements[u].target)[0]][0];
      |     for (var v = 0; v < elements.elements.length; v++) {
      |       if (elements.elements[v].id === klassenId) {
      |         elements.elements[v].inputs[elements.elements[u].mReference] = [];
      |         elements.elements[v].inputs[elements.elements[u].mReference][0] = lineId;
      |       }
      |     }
      |   }
      | }
    """.stripMargin
  }

  private def connectionGroup(edge: Edge) = {
    edge.connection.referencedConnection match {
      case Some(conn) => conn.name
      case _ => ""
    }
  }
}
