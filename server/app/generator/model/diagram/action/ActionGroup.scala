package generator.model.diagram.action

/**
 * Created by julian on 24.11.15.
 * representation of GlobalActionGroup
 */
class ActionGroup (val name:String,
                   val actions:Map[String, Action]){
  require(actions.nonEmpty)
}

object ActionGroup{
  def apply(name:String, actions:List[Action]) = parse(name, actions)
  def parse(name:String, actions:List[Action]) = new ActionGroup(name, actions.map(i => i.name -> i).toMap)
}
