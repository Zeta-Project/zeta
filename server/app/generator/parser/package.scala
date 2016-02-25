import generator.model.diagram.Diagram
import generator.model.diagram.action.{Action, ActionGroup}
import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.shape.Shape
import generator.model.style.Style
import generator.parser.{ConnectionSketch, ShapeSketch, Cache}

/**
 * Created by julian on 06.01.16.
 * attempt to make acces on several instances of the cache much easier
 * CacheEvaluation
 */
package object parser {
  implicit def IDtoStyle(id:String)(implicit c:Cache): Style = c.styleHierarchy(id).data
  implicit def IDtoOptionStyle(id:String)(implicit c:Cache): Option[Style] = c.styleHierarchy.get(id)
  implicit def OptionToStyle(id:Option[String])(implicit c:Cache): Option[Style] = if(id isDefined)c.styleHierarchy.get(id.get)else None

  implicit def IDtoShape(id:String)(implicit c:Cache): Shape = c.shapeHierarchy(id).data
  implicit def IDtoOptionShape(id:String)(implicit c:Cache): Option[Shape] = c.shapeHierarchy.get(id)

  implicit def IDtoShapeSketch(id:String)(implicit c:Cache):ShapeSketch = c.shapeSketches(id)
  implicit def IDtoOptionShapeSketch(id:String)(implicit c:Cache):Option[ShapeSketch] = c.shapeSketches.get(id)

  implicit def IDtoDiagram(id:String)(implicit c:Cache): Diagram = c.diagrams(id)
  implicit def IDtoOptionDiagram(id:String)(implicit c:Cache): Option[Diagram] = c.diagrams.get(id)

  implicit def IDtoConnection(id:String)(implicit c:Cache): Connection = c.connections(id)
  implicit def IDtoOptionConnection(id:String)(implicit c:Cache): Option[Connection] = c.connections.get(id)

  implicit def IDtoConnectionSketch(id:String)(implicit c:Cache): ConnectionSketch = c.connectionSketches(id)
  implicit def IDtoOptionConnectionSketch(id:String)(implicit c:Cache): Option[ConnectionSketch] = c.connectionSketches.get(id)

  implicit def IDtoActionGroup(id:String)(implicit c:Cache): ActionGroup = c.actionGroups(id)
  implicit def IDtoOptionActionGroup(id:String)(implicit c:Cache): Option[ActionGroup] = c.actionGroups.get(id)

  implicit def IDtoAction(id:String)(implicit c:Cache):Action = c.actions(id)
  implicit def IDtoOptionAction(id:String)(implicit c:Cache):Option[Action] = c.actions.get(id)
}