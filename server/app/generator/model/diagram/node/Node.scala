package generator.model.diagram.node

import generator.model.diagram.action.{Actions, ActionInclude, Action}
import generator.model.diagram.methodes.{Methodes, OnDelete, OnUpdate, OnCreate}
import generator.model.diagram.traits.{Container, Palette}
import generator.model.style.Style

/**
 * Created by julian on 24.11.15.
 * representation of a node
 */
case class Node(name:String,
                ecoreElement:AnyRef,/*TODO this is a mock, replace with actual ecoreElement*/
                var style:Option[Style]      = None,
                                /*node-block*/
                shape:Option[Shape]                   = None,
                override val palette:Option[String]   = None,
                override val container:Option[AnyRef] = None,
                override val onCreate:Option[OnCreate]= None,
                override val onUpdate:Option[OnUpdate]= None,
                override val onDelete:Option[OnDelete]= None,
                override val actions:List[Action]     = List(),
                override val actionIncludes: Option[ActionInclude] = None
               ) extends Palette with Container with Methodes with Actions



