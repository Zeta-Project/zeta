package de.htwg.zeta.server.generator.parser

import de.htwg.zeta.server.generator.model.ClassHierarchy
import de.htwg.zeta.server.generator.model.diagram.Diagram
import de.htwg.zeta.server.generator.model.diagram.action.ActionGroup
import de.htwg.zeta.server.generator.model.diagram.action.Action
import de.htwg.zeta.server.generator.model.shapecontainer.connection.Connection
import de.htwg.zeta.server.generator.model.shapecontainer.shape.Shape
import de.htwg.zeta.server.generator.model.style.Style

/**
 * Created by julian on 24.09.15.
 * representation of a container element for shapes and styles
 */
case class Cache(
    var diagrams: Map[String, Diagram] = Map[String, Diagram](),
    styleHierarchy: ClassHierarchy[Style] = new ClassHierarchy[Style](Style("rootStyle")),
    shapeHierarchy: ClassHierarchy[Shape] = new ClassHierarchy[Shape](Shape("rootShape")),
    var shapeSketches: Map[String, ShapeSketch] = Map[String, ShapeSketch](),
    var connections: Map[String, Connection] = Map[String, Connection](),
    var connectionSketches: Map[String, ConnectionSketch] = Map[String, ConnectionSketch](),
    var actions: Map[String, Action] = Map[String, Action](),
    var actionGroups: Map[String, ActionGroup] = Map[String, ActionGroup]()
) {
  def +(diagram: Diagram) = diagrams += diagram.name -> diagram
  def +(connection: Connection) = connections += connection.name -> connection
  def +(action: Action) = actions += action.name -> action
  def +(actionGroup: ActionGroup) = actionGroups += actionGroup.name -> actionGroup
  def +(shapeSketch: ShapeSketch) = shapeSketches += shapeSketch.name -> shapeSketch
  def +(connectionSketch: ConnectionSketch) = connectionSketches += connectionSketch.name -> connectionSketch
}
