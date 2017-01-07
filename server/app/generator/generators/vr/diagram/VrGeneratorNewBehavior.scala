package generator.generators.vr.diagram

import generator.model.diagram.node.Node

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

        pushNewElement: function(id, type, position, size) {
          var scene = document.querySelector("vr-scene");
          if(type in this.elementMap) {
            (this.elementMap[type].bind(this))(scene, id, position, size);
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
       //TODO: get default size and not 50
    ${name}: function(scene, id, position, size) {
      scene.push('${name}Items', {
        id: '${name}-'+id,
        xPos: position.x,
        yPos: position.y,
        width: size != null ? size.width : 50,
        height: size != null ? size.height : 50
      });
    },"""
  }
}
