package de.htwg.zeta.server.generator.model.diagram.node

import de.htwg.zeta.server.generator.model.diagram.action.ActionInclude
import de.htwg.zeta.server.generator.model.diagram.action.Action
import de.htwg.zeta.server.generator.model.diagram.action.Actions
import de.htwg.zeta.server.generator.model.diagram.methodes.Methodes
import de.htwg.zeta.server.generator.model.diagram.methodes.OnCreate
import de.htwg.zeta.server.generator.model.diagram.methodes.OnUpdate
import de.htwg.zeta.server.generator.model.diagram.methodes.OnDelete
import de.htwg.zeta.server.generator.model.diagram.traits.Container
import de.htwg.zeta.server.generator.model.diagram.traits.Palette
import de.htwg.zeta.server.generator.model.style.Style
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MReference
import de.htwg.zeta.common.models.modelDefinitions.concept.elements.MClass

/**
 * Created by julian on 24.11.15.
 * representation of a node
 */
case class Node(
    name: String,
    mcoreElement: MClass,
    style: Option[Style] = None,
    // node-block
    shape: Option[DiaShape] = None,
    override val palette: Option[String] = None,
    override val container: Option[MReference] = None,
    override val onCreate: Option[OnCreate] = None,
    override val onUpdate: Option[OnUpdate] = None,
    override val onDelete: Option[OnDelete] = None,
    override val actions: List[Action] = List(),
    override val actionIncludes: Option[ActionInclude] = None)
  extends Palette with Container with Methodes with Actions

