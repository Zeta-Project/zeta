package generator.generators.vr.diagram

import generator.model.diagram.edge.Edge
import generator.model.shapecontainer.connection.Connection

/**
  * Created by max on 08.12.16.
  */
object VrGeneratorConnectBehavior {
  def generate(connections: Iterable[Connection], edges: Iterable[Edge]) = {
    s"""
    <!-- Generated file -->
    <!-- <link rel="import" href="/assets/prototyp/behaviors/vr-connect.html"> -->

    <script>
      window.VrBehavior = window.VrBehavior || {};
      VrBehavior.ConnectExtended = {
        connectionMap: {
          ${connections.map(createMap(_)).mkString.dropRight(1)}
        },

        loadConnectionMap: {
          ${edges.map(createMap(_)).mkString.dropRight(1)}
        },

        getConnectionEntries: function() {
          return Object.keys(this.connectionMap);
        },

        pushNewConnection: function(type, connection, id) {
          var self = this;
          if(type in this.connectionMap) {
            addElement(this.connectionMap[type]());
          } else if(type in this.loadConnectionMap) {
            addElement(this.loadConnectionMap[type]());
          } else {
            console.error("No valid connection found! Caution generated Code.");
          }

          function addElement(element) {
            element.id = id != null ? id : guid();
            element.from = connection.from;
            element.to = connection.to;
            Polymer.dom(self.root).appendChild(element);
          }

          function guid() {
            function s4() {
              return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
            }
            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
              s4() + '-' + s4() + s4() + s4();
          }
        }
      };
    </script>
    """
  }

  def createMap(connection: Connection) = {
    s"""
    connection${connection.name.capitalize}: function(connection) {
      return new VrElement.Connection${connection.name.capitalize}();
    },"""
  }

  def createMap(edge: Edge) = {
    edge.connection.referencedConnection match {
      case Some(c) =>
        s"""
        ${edge.name}: function(connection) {
          return new VrElement.Connection${c.name.capitalize}();
        },"""
      case _ => ""
    }
  }
}
