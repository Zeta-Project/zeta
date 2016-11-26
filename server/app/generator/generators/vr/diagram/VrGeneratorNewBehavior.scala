package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.node.{DiaShape, Node}

/**
  * Created by max on 12.11.16.
  */
object VrGeneratorNewBehavior {
  def generate(diagram: Diagram) = {
    s"""
    <!-- Generated file -->
    <link rel="import" href="/assets/prototyp/behaviors/vr-new.html">

    <script>
         window.VrBehavior = window.VrBehavior || {};
         VrBehavior.NewExtended = [VrBehavior.New, {
             getEntries: function() {
               return [${generateEntriesArray(diagram.nodes)}];
             },

         pushNewElement: function(counter, position, text) {
             counter += 1
             // classItems must be part off vr-scene.html
             switch(text) {
                ${diagram.nodes.map(generateCases(_)).mkString}
                default:
                    console.error("No valid element found! Caution generated Code.");
                    counter -= 1;
             }
               return counter;
             }
         }];
    </script>
    """
  }

  def generateEntriesArray(nodes: List[Node]) = {
    var result = ""
    for(node <- nodes) {result += "\"" + node.shape.get.getShape + "\", "}
    result.dropRight(2)
  }

  def generateCases(node:Node) = {
    val name = node.shape.get.getShape
    s"""
      case "${name}":
        this.push('${name}Items', {id: '${name}-' + counter, xPos: position.x, yPos: position.y, classType: text.toLowerCase()});
        break;
    """
  }
}
