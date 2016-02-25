package generator.model.diagram.edge

import generator.model.diagram.action.{ActionInclude, Action, Actions}
import generator.model.diagram.methodes.{OnDelete, OnUpdate, OnCreate, Methodes}
import generator.model.diagram.traits.{Container, Palette}
import generator.model.style.Style
import generators.{MreferenceMock, MclassMock}
import models.metaModel.mCore.{MReference, MClass}

/**
 * Created by julian on 30.11.15.
 * representation of an Edge
 */
case class Edge(name:String,
                mcoreElement:MClass,
                var style: Option[Style] = None,
                            /*edge-Block*/
                connection:Connection,
                from:MReference,
                to:MReference,
                override val palette:Option[String] = None,
                override val container:Option[MReference] = None,
                override val onCreate:Option[OnCreate] = None,
                override val onUpdate:Option[OnUpdate] = None,
                override val onDelete:Option[OnDelete] = None,
                override val actions:List[Action]      = List(),
                override val actionIncludes:Option[ActionInclude] = None
                ) extends  Palette with Container with Methodes with Actions
