package generator.model.diagram.edge

import generator.model

/**
 * Created by julian on 11.12.15.
 * representation of diagramm connection
 */
case class Connection(connection:model.shapecontainer.connection.Connection,
                 vars:Map[String, AnyRef] = Map(),/*TODO String is a Mockup for EcoreAttribute*/
                 vals:Map[String, AnyRef] = Map())
