package generator.model.diagram.node

import generator.model
import generator.model.shapecontainer.shape.Compartment
import generator.model.shapecontainer.shape.geometrics.Text

/**
 * Created by julian on 30.11.15.
 * diagrams shape definition
 */
class Shape(val shape: model.shapecontainer.shape.Shape,
            val vars:Map[String, Text] = Map(),/*TODO String is a Mockup for EcoreAttribute*/
            val vals:Map[String, Text] = Map(),
            val nests:Map[String, Compartment] = Map())

/*TODO String is a Mockup for EcoreAttribute*/
