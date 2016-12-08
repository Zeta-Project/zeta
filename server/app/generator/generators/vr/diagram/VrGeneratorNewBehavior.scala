package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.node.{DiaShape, Node}
import generator.model.shapecontainer.connection.Connection

/**
  * Created by max on 12.11.16.
  */
object VrGeneratorNewBehavior {

  def generate(nodes: Iterable[Node]) = {
    s"""
    <!-- Generated file -->
    <link rel="import" href="/assets/prototyp/behaviors/vr-new.html">

    <script>
      window.VrBehavior = window.VrBehavior || {};
      VrBehavior.NewExtended = [VrBehavior.New, {
        elementMap: {
          ${nodes.map(createMap(_)).mkString.dropRight(1)}
        },

        getEntries: function() {
          return Object.keys(this.elementMap);
        },

        pushNewElement: function(counter, position, type) {
          if(type in this.elementMap) {
            (this.elementMap[type].bind(this))(counter, position);
            return counter + 1;
          } else {
            console.error("No valid element found! Caution generated Code.");
          }
        }
      }];
    </script>
    """
  }

  def createMap(node: Node) = {
    val name = node.shape.get.getShape
    s"""
    ${name}: function(id, position) {
      this.ConnectBehavior.scene.push('${name}Items', {
        id: '${name}-'+id,
        xPos: position.x,
        yPos: position.y,
        classType: "fff"
      });
    },"""
  }
}
