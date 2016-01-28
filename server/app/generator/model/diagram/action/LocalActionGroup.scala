package generator.model.diagram.action

import generator.model.diagram.Diagram

/**
 * Created by julian on 30.11.15.
 * representations of LocalActionGroup and ActionInclude
 */
class LocalActionGroup(val actionIncludes:List[ActionInclude],
                       val actions:List[Action]){
  /**
   * since there is no Diagram while parsing this methode has to be called when parsing a Diagram has finished
   */
  def solveOpenDependencies(diagram: Diagram) =
    actionIncludes.map(_.solveOpenDependencies(diagram))
}

object LocalActionGroup{
  def apply(actionIncludes:List[ActionInclude], actions:List[Action]) = new LocalActionGroup(actionIncludes, actions)
}


/**
 * defined in LocalActionGroup.scala because it is only used there*/
class ActionInclude(var globalActionIncludes:List[ActionGroup] = List[ActionGroup](), val openReferences:List[String] = List()){
  def solveOpenDependencies(diagram: Diagram) {globalActionIncludes = globalActionIncludes ::: openReferences.map(diagram.globalActionGroups(_))}
}

object ActionInclude{
  def apply(actionGroupReferences:List[ActionGroup]) = new ActionInclude(actionGroupReferences)
}
