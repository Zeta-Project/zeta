package generator

import generator.model.diagram.Diagram
import generator.model.diagram.action.Action
import generator.model.diagram.action.ActionGroup
import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.shape.Shape
import generator.model.style.Style

/**
 * Created by julian on 06.01.16.
 * attempt to make acces on several instances of the cache much easier
 * CacheEvaluation
 */
package object parser {
  @inline def IDtoStyle(id: String)(c: Cache): Option[Style] = c.styleHierarchy.get(id)

  @inline def OptionToStyle(id: Option[String])(c: Cache): Option[Style] = id.flatMap(IDtoStyle(_)(c))

  @inline def IDtoShape(id: String)(c: Cache): Option[Shape] = c.shapeHierarchy.get(id)

  @inline def IDtoShapeSketch(id: String)(c: Cache): ShapeSketch = c.shapeSketches(id)

  @inline def IDtoOptionShapeSketch(id: String)(c: Cache): Option[ShapeSketch] = c.shapeSketches.get(id)

  @inline def IDtoDiagram(id: String)(c: Cache): Diagram = c.diagrams(id)

  @inline def IDtoOptionDiagram(id: String)(c: Cache): Option[Diagram] = c.diagrams.get(id)

  @inline def IDtoConnection(id: String)(c: Cache): Connection = c.connections(id)

  @inline def IDtoOptionConnection(id: String)(c: Cache): Option[Connection] = c.connections.get(id)

  @inline def IDtoConnectionSketch(id: String)(c: Cache): ConnectionSketch = c.connectionSketches(id)

  @inline def IDtoOptionConnectionSketch(id: String)(c: Cache): Option[ConnectionSketch] = c.connectionSketches.get(id)

  @inline def IDtoActionGroup(id: String)(c: Cache): ActionGroup = c.actionGroups(id)

  @inline def IDtoOptionActionGroup(id: String)(c: Cache): Option[ActionGroup] = c.actionGroups.get(id)

  @inline def IDtoAction(id: String)(c: Cache): Action = c.actions(id)

  @inline def IDtoOptionAction(id: String)(c: Cache): Option[Action] = c.actions.get(id)
}
