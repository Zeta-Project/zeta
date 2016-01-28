package generator.util

import generator.model.ClassHierarchy
import generator.model.diagram.Diagram
import generator.model.diagram.action.{ActionGroup, Action}
import generator.model.shapecontainer.connection.Connection
import generator.model.shapecontainer.shape.Shape
import generator.model.style.Style

/**
 * Created by julian on 24.09.15.
 * representation of a container element for shapes and styles
 */
case class Cache(var diagrams:Map[String, Diagram] = Map[String, Diagram](),
                 styleHierarchy: ClassHierarchy[Style] = new ClassHierarchy[Style](new Style(name = "rootStyle")),
                 shapeHierarchy: ClassHierarchy[Shape] = new ClassHierarchy[Shape](new Shape(name = "rootShape")),
                 var connections:Map[String, Connection] = Map[String, Connection](),
                 var actions:Map[String, Action] = Map[String, Action](),
                 var actionGroups:Map[String, ActionGroup] = Map[String, ActionGroup]()){
  def +(diagram: Diagram) = diagrams += diagram.name -> diagram
  def +(connection:Connection) = connections += connection.name -> connection
  def +(action:Action) = actions += action.name -> action
  def +(actionGroup:ActionGroup) = actionGroups += actionGroup.name -> actionGroup
}

