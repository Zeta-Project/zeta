package generator.generators.vr.diagram

import generator.model.diagram.Diagram
import generator.model.diagram.node.Node

/**
  * Created by max on 12.11.16.
  */
object VrGeneratorNewBehavior {
  // TODO: remove dropRight(4)
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
             // TODO: Also generate this part!
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
    for(node <- nodes) {result += "\"" + node.name.dropRight(4) + "\", "}
    result.dropRight(2)
  }

  def generateCases(node:Node) = {
    s"""
      case "${node.name.dropRight(4)}":
        this.push('${node.name.dropRight(4)}Items', {id: '${node.name.dropRight(4)}-' + counter, xPos: position.x, yPos: position.y, classType: text.toLowerCase()});
        break;
    """
  }
}
