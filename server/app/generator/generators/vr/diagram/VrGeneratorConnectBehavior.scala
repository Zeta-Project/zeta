package generator.generators.vr.diagram

import generator.model.shapecontainer.connection.Connection

/**
  * Created by max on 08.12.16.
  */
object VrGeneratorConnectBehavior {
  def generate(connections: Iterable[Connection]) = {
    s"""
    <!-- Generated file -->
    <link rel="import" href="/assets/prototyp/behaviors/vr-connect.html">

    <script>
      window.VrBehavior = window.VrBehavior || {};
      VrBehavior.ConnectExtended = [VrBehavior.Connect, {
        connectionMap: {
          ${connections.map(createMap(_)).mkString.dropRight(1)}
        },

        getEntries: function() {
          return Object.keys(this.connectionMap);
        },

        pushNewConnection: function(type, connection, id) {
          if(type in this.connectionMap) {
            var element = this.connectionMap[type]();
            element.id = id != null ? id : guid();
            element.from = connection.from;
            element.to = connection.to;
            if(this.parentNode) Polymer.dom(this.parentNode.root).appendChild(element);
          } else {
            console.error("No valid connection found! Caution generated Code.");
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
      }];
    </script>
    """
  }

  def createMap(connection: Connection) = {
    s"""
    connection${connection.name.capitalize}: function(connection) {
      return new VrElement.Connection${connection.name.capitalize}();
    },"""
  }
}
