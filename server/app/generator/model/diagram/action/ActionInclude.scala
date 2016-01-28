package generator.model.diagram.action

import generator.model.diagram.Diagram

class ActionInclude(var globalActionIncludes:List[ActionGroup] = List[ActionGroup](), val openReferences:List[String] = List()){
  def solveOpenDependencies(diagram: Diagram) {globalActionIncludes = globalActionIncludes ::: openReferences.map(diagram.globalActionGroups(_))}
}

object ActionInclude{
  def apply(actionGroupReferences:List[ActionGroup]) = new ActionInclude(actionGroupReferences)
}

