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
      window.VrBehavior = window.Behavior || {};
      VrBehavior.ConnectExtended = [VrBehavior.Connect, {
        connectionMap: {
          ${connections.map(createMap(_)).mkString.dropRight(1)}
        },

        getEntries: function() {
          return Object.keys(this.connectionMap);
        },

        pushNewConnection: function(type, connection) {
          if(type in this.connectionMap) {
            (this.connectionMap[type].bind(this))(connection);
          } else {
            console.error("No valid element found! Caution generated Code.");
          }
        }
      }];
    </script>
    """
  }

  def createMap(connection: Connection) = {
    s"""
    connection${connection.name.capitalize}: function(connection) {
      this.ConnectBehavior.scene.push('connection${connection.name.capitalize}Items', connection)
    },"""
  }
}
