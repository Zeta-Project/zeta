package generator.model.diagram.edge

import generator.model.diagram.action.ActionInclude
import generator.model.diagram.action.Action
import generator.model.diagram.action.Actions
import generator.model.diagram.methodes.OnDelete
import generator.model.diagram.methodes.OnUpdate
import generator.model.diagram.methodes.OnCreate
import generator.model.diagram.methodes.Methodes
import generator.model.diagram.traits.Container
import generator.model.diagram.traits.Palette
import generator.model.style.Style
import models.modelDefinitions.metaModel.elements.MClass
import models.modelDefinitions.metaModel.elements.MReference

/**
 * Created by julian on 30.11.15.
 * representation of an Edge
 */
case class Edge(
    name: String,
    mcoreElement: MReference,
    var style: Option[Style] = None,
    /*edge-Block*/
    connection: Connection,
    from: MClass,
    to: MClass,
    override val palette: Option[String] = None,
    override val container: Option[MReference] = None,
    override val onCreate: Option[OnCreate] = None,
    override val onUpdate: Option[OnUpdate] = None,
    override val onDelete: Option[OnDelete] = None,
    override val actions: List[Action] = List(),
    override val actionIncludes: Option[ActionInclude] = None)
  extends Palette with Container with Methodes with Actions

