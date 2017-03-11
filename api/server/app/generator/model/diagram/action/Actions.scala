package generator.model.diagram.action

/**
 * Created by julian on 08.12.15.
 * Actions trait represents the grammars 'actionsBlock'
 */
trait Actions {
  val actionIncludes:Option[ActionInclude]
  val actions:List[Action]
}
